package org.bionimbuzbox.scheduling.runner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bionimbuzbox.helper.JSONHelper;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.orchestration.driver.OrchestrationDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobRunner implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger( JobRunner.class );

  private Job job;
  private OrchestrationDriver driver;
  private enum Sync {
    UPLOAD,
    DOWNLOAD
  }

  public JobRunner(Job job, OrchestrationDriver driver) {
    LOGGER.info("JobRunner started for: " + job.getId());
    this.job = job;
    this.driver = driver;
  }

  @Override
  public void run() {
    LocalDateTime initialDateTime = LocalDateTime.now(), stepDateTime, previousDateTime;
    Duration jobDuration, downloadDuration, uploadDuration;
    Job.Status jobStatus = Job.Status.RUNNING;

    job.setStatus(jobStatus);

    // DOWNLOAD (block)
    sync(Sync.DOWNLOAD);
    stepDateTime = LocalDateTime.now();
    downloadDuration = Duration.between(initialDateTime, stepDateTime);

    LOGGER.info("Duration for SYNC.DOWNLOAD Job {}: {} seconds", 
        job.getId(), 
        downloadDuration.getSeconds());

    // START JOB
    driver.start(job);

    while(jobStatus != Job.Status.FINISHED) {
      jobStatus = driver.status(job);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    previousDateTime = stepDateTime;
    stepDateTime = LocalDateTime.now();
    jobDuration = Duration.between(previousDateTime, stepDateTime);

    LOGGER.info("Duration of Job {}: {} seconds", 
        job.getId(), 
        jobDuration.getSeconds());

    // UPLOAD
    sync(Sync.UPLOAD);

    previousDateTime = stepDateTime;
    stepDateTime = LocalDateTime.now();
    uploadDuration = Duration.between(previousDateTime, stepDateTime);

    LOGGER.info("Duration for SYNC.UPLOAD Job {}: {} seconds", 
        job.getId(), 
        uploadDuration.getSeconds());

    job.setStatus(jobStatus);

    LOGGER.info("JobRunner finished. Duration in seconds (job_name,cluster_name,download,execution,upload,total):\nCSV:{},{},{},{},{},{}", 
        job.getId(),
        driver.cluster().getName(),
        downloadDuration.getSeconds(),
        jobDuration.getSeconds(),
        uploadDuration.getSeconds(),
        Duration.between(initialDateTime, stepDateTime).getSeconds());
  }

  private void sync(Sync action) {

    Collection<String> syncPaths;

    if (Sync.DOWNLOAD.equals(action)) { 
      syncPaths = this.job.filterMetaInfoByKeyPrefix("sync-input");
    } else {
      syncPaths = this.job.filterMetaInfoByKeyPrefix("sync-output");
    }

    if (syncPaths.size() == 0) {
      LOGGER.info("Job {} do not need syncing for {}", job.getId(), action);
      return;
    }

    StringBuffer buffer = new StringBuffer();

    // 0. Configure Storage Access
    buffer.append(
        String.format("mc -q config host add storage %s %s %s ; ",
            driver.cluster().getMetaInfo("storage-url", "https://storage.box.io.etc.br"),
            driver.cluster().getMetaInfo("storage-access-key", ""),
            driver.cluster().getMetaInfo("storage-secret-key", "")
            ));

    // 1. Create Bucket;
    buffer.append("mc -q mb storage/mnt ; ");

    for (String syncPath : syncPaths) {
      String from, to; from = to = syncPath;

      if (Sync.DOWNLOAD.equals(action)) {
        from = "storage" + syncPath;
      } else {
        to = "storage" + syncPath;
      }

      buffer.append(
          String.format("mc -q mirror %s %s ; ", from, to));
    }

    List<String> commands = Arrays.asList(
        "/bin/sh", "-c", buffer.toString());

    Map<String, String> meta = new HashMap<>();
    meta.put("swarm.image", "minio/mc");
    meta.put("swarm.commands", JSONHelper.toJSON(commands));
    meta.put("swarm.mounts", job.getMetaInfo("swarm.mounts"));
    meta.put("environment.vars", "");
    meta.put("environment.work-dir", "/mnt");

    Job syncJob = new Job(String.format("%s-sync-job-%s", job.getId(), action.toString().toLowerCase()));
    syncJob.setMeta(meta);
    syncJob.setWorkflow(job.getWorkflow());

    driver.start(syncJob);

    while(syncJob.getStatus() != Job.Status.FINISHED) {
      syncJob.setStatus(driver.status(syncJob));
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
  }
}
