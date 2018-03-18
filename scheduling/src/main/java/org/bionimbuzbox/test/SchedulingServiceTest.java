package org.bionimbuzbox.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bionimbuzbox.helper.FileHelper;
import org.bionimbuzbox.helper.JSONHelper;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Resource;
import org.bionimbuzbox.model.Server;
import org.bionimbuzbox.model.Workflow;
import org.bionimbuzbox.scheduling.SchedulingService;

public class SchedulingServiceTest {
	private static final Logger LOGGER = Logger.getLogger( SchedulingServiceTest.class.getName() );
	
	public static void main(String[] args) throws Exception {
		SchedulingServiceTest t = new SchedulingServiceTest();
		//t.test();
		t.testJSON();
		//t.testRnatoy();
	}
	
	public Cluster buildCluster() {
    String ca = FileHelper.readFile("/Users/tiago/.docker/machine/certs/ca.pem");
    String cert = FileHelper.readFile("/Users/tiago/.docker/machine/certs/cert.pem");
    String key = FileHelper.readFile("/Users/tiago/.docker/machine/certs/key.pem");

    Cluster cluster = new Cluster();
    cluster.setId("Cluster1");
    cluster.setName("cluster-1");
    cluster.setType("swarm");
    
    Server s1 = new Server();
    s1.addMetaInfo("private-ip", "192.168.62.21");
    s1.addMetaInfo("public-ip", "192.168.62.21");
    s1.addMetaInfo("fqdn", "192.168.62.21");
    //s1.addMetaInfo("ssl-ca-cert", ca);
    //s1.addMetaInfo("ssl-client-cert", cert);
    //s1.addMetaInfo("ssl-client-key", key);
    cluster.addServer(s1);

    Server s2 = new Server();
    s2.addMetaInfo("private-ip", "192.168.62.22");
    s2.addMetaInfo("public-ip", "192.168.62.22");
    s2.addMetaInfo("fqdn", "192.168.62.22");
    //s2.addMetaInfo("ssl-ca-cert", ca);
    //s2.addMetaInfo("ssl-client-cert", cert);
    //s2.addMetaInfo("ssl-client-key", key);
    cluster.addServer(s2);

    return cluster;
	}
	
	public Cluster buildLocalCluster() {
	  String ca = FileHelper.readFile("/Users/tiago/.docker/machine/certs/ca.pem");
    String cert = FileHelper.readFile("/Users/tiago/.docker/machine/certs/cert.pem");
    String key = FileHelper.readFile("/Users/tiago/.docker/machine/certs/key.pem");

    Cluster cluster = new Cluster();
    cluster.setId("Cluster1");
    cluster.setName("cluster-1");
    cluster.setType("swarm");
    cluster.addMetaInfo("storage-url", "https://storage.box.io.etc.br");
    cluster.addMetaInfo("storage-access-key", "WD5M1689NYO7I22G404X");
    cluster.addMetaInfo("storage-secret-key", "pco+dpYvbVaDMYLrKi3YxAfx141zOR+teVWj3dvs");
    
    Server s1 = new Server();
    s1.addMetaInfo("private-ip", "192.168.99.100");
    s1.addMetaInfo("public-ip", "192.168.99.100");
    s1.addMetaInfo("fqdn", "192.168.99.100");
    s1.addMetaInfo("ssl-ca-cert", ca);
    s1.addMetaInfo("ssl-client-cert", cert);
    s1.addMetaInfo("ssl-client-key", key);
    s1.addMetaInfo("init", "true");
    cluster.addServer(s1);

    return cluster;
	}
	
	public Workflow buildTrapnellWorkflow() {
	  Workflow w = new Workflow("Workflow1");
    /*
    Resource r1 = new Resource("resource-1");
    r1.setWorkflow(w);
    w.addResource(r1);
    */
    String image = "bionimbuz/tophat";
    
    List<String> commonCommands = Arrays.asList(
        "tophat",
        "-p", "4", 
        "-G", "/data/Drosophila_melanogaster/Ensembl/BDGP5.25/Annotation/Genes/genes.gtf", 
        "-o", "/out/C1_R1_thout", 
        "/data/Drosophila_melanogaster/Ensembl/BDGP5.25/Sequence/BowtieIndex/genome");
    
    List<String> step1Commands = new ArrayList<>(commonCommands);
    step1Commands.add("/data/GSM794483_C1_R1_1.fq");
    step1Commands.add("/data/GSM794483_C1_R1_2.fq");
    
    
    List<String> step2Commands = new ArrayList<>(commonCommands);
    step2Commands.add("/data/GSM794484_C1_R2_1.fq");
    step2Commands.add("/data/GSM794484_C1_R2_2.fq");
    
    List<String> step3Commands = new ArrayList<>(commonCommands);
    step3Commands.add("/data/GSM794485_C1_R3_1.fq");
    step3Commands.add("/data/GSM794485_C1_R3_2.fq");
    
    List<String> step4Commands = new ArrayList<>(commonCommands);
    step4Commands.add("/data/GSM794486_C2_R1_1.fq");
    step4Commands.add("/data/GSM794486_C2_R1_2.fq");
    
    List<String> step5Commands = new ArrayList<>(commonCommands);
    step5Commands.add("/data/GSM794487_C2_R2_1.fq");
    step5Commands.add("/data/GSM794487_C2_R2_2.fq");
    
    List<String> step6Commands = new ArrayList<>(commonCommands);
    step6Commands.add("/data/GSM794488_C2_R3_1.fq");
    step6Commands.add("/data/GSM794488_C2_R3_2.fq");
       
    List<Map<String,String>> mounts = new ArrayList<>();
    HashMap<String,String> mount1 = new HashMap<>();
    mount1.put("source", "/data/data");
    mount1.put("target", "/data");
    mount1.put("readonly", "true");
    mount1.put("type", "bind");
    mounts.add(mount1);
    
    HashMap<String,String> mount2 = new HashMap<>();
    mount2.put("source", "/data/bind-out");
    mount2.put("target", "/out");
    mount2.put("readonly", "false");
    mount2.put("type", "bind");
    mounts.add(mount2);
    
    String jsonMounts = JSONHelper.toJSON(mounts);
    
    
    Job j1 = new Job("tophat1");
    j1.addMetaInfo("swarm.image", image);
    j1.addMetaInfo("swarm.commands", JSONHelper.toJSON(step1Commands));
    j1.addMetaInfo("swarm.mounts", jsonMounts);
    j1.setWorkflow(w);
    w.addJob(j1);
    
    Job j2 = new Job("tophat2");
    j2.addMetaInfo("swarm.image", image);
    j2.addMetaInfo("swarm.commands", JSONHelper.toJSON(step2Commands));
    j2.addMetaInfo("swarm.mounts", jsonMounts);
    j2.setWorkflow(w);
    j2.addDependency(j1);
    w.addJob(j2);
    
    Job j3 = new Job("tophat3");
    j3.addMetaInfo("swarm.image", image);
    j3.addMetaInfo("swarm.commands", JSONHelper.toJSON(step3Commands));
    j3.addMetaInfo("swarm.mounts", jsonMounts);
    j3.setWorkflow(w);
    j3.addDependency(j2);
    w.addJob(j3);
    
    Job j4 = new Job("tophat4");
    j4.addMetaInfo("swarm.image", image);
    j4.addMetaInfo("swarm.commands", JSONHelper.toJSON(step4Commands));
    j4.addMetaInfo("swarm.mounts", jsonMounts);
    j4.setWorkflow(w);
    j4.addDependency(j3);
    w.addJob(j4);
    
    Job j5 = new Job("tophat5");
    j5.addMetaInfo("swarm.image", image);
    j5.addMetaInfo("swarm.commands", JSONHelper.toJSON(step5Commands));
    j5.addMetaInfo("swarm.mounts", jsonMounts);
    j5.setWorkflow(w);
    j5.addDependency(j4);
    w.addJob(j5);
    
    Job j6 = new Job("tophat6");
    j6.addMetaInfo("swarm.image", image);
    j6.addMetaInfo("swarm.commands", JSONHelper.toJSON(step6Commands));
    j6.addMetaInfo("swarm.mounts", jsonMounts);
    j6.setWorkflow(w);
    j6.addDependency(j5);
    w.addJob(j6);
    
    return w;
	}
	
	public Workflow buildWorkflow() {
	  Workflow w = new Workflow("Workflow1");
    
    Resource r1 = new Resource("resource-1");
    r1.setWorkflow(w);
    w.addResource(r1);
    
    List<String> commands = new ArrayList<>();
    commands.add("/bin/sh");
    commands.add("-c");
    commands.add("for i in 1 2 3; do echo $i; sleep 10; done");
    
    
    List<String> step1Commands = new ArrayList<>();
    step1Commands.add("/data/GSM794483_C1_R1_1.fq");
    step1Commands.add("/data/GSM794483_C1_R1_2.fq");
    
    List<Map<String,String>> mounts = new ArrayList<>();
    HashMap<String,String> mount1 = new HashMap<>();
    mount1.put("source", "resource-1");
    mount1.put("target", "/mnt");
    mount1.put("readonly", "false");
    mount1.put("type", "volume");
    mounts.add(mount1);
    
    Map<String, String> meta = new HashMap<>();
    meta.put("swarm.image", "alpine");
    meta.put("swarm.commands", JSONHelper.toJSON(commands));
    meta.put("swarm.limit-cpu", "1");
    meta.put("swarm.limit-memory", "");
    meta.put("swarm.reserve-cpu", "0");
    meta.put("swarm.reserve-memory", "0");
    meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    
    Job j1 = new Job("Job1");
    j1.setMeta(meta);
    j1.setWorkflow(w);
    w.addJob(j1);
    
    Job j2 = new Job("Job2");
    j2.setMeta(meta);
    j2.setWorkflow(w);
    j2.addDependency(j1);
    w.addJob(j2);
    
    Job j3 = new Job("Job3");
    j3.setMeta(meta);
    j3.setWorkflow(w);
    j3.addDependency(j2);
    w.addJob(j3);
    
    Job j4 = new Job("Job4");
    j4.setMeta(meta);
    j4.setWorkflow(w);
    j4.addDependency(j2);
    w.addJob(j4);
    
    Job j5 = new Job("Job5");
    j5.setMeta(meta);
    j5.setWorkflow(w);
    j5.addDependency(j3);
    w.addJob(j5);
    
    Job j6 = new Job("Job6");
    j6.setMeta(meta);
    j6.setWorkflow(w);
    j6.addDependency(j5);
    j6.addDependency(j4);
    w.addJob(j6);
    
    return w;
	}
	
	
	public Workflow rnatoy() {
    Workflow w = new Workflow("rnatoy");
    w.setName("rnatoy");
 
////////////
    
    List<Map<String,String>> mounts = new ArrayList<>();
    HashMap<String,String> mount1 = new HashMap<>();
    mount1.put("source", "/mnt");
    mount1.put("target", "/mnt");
    mount1.put("readonly", "false");
    mount1.put("type", "bind");
    mounts.add(mount1);
    
    
    List<String> step0Commands = Arrays.asList(
        "/bin/sh",
        "-c", 
        "install -cD /dev/null /mnt/rnatoy/out/step1/_empty_ ; "
        + "install -cD /dev/null /mnt/rnatoy/out/step2/_empty_ ; "
        + "install -cD /dev/null /mnt/rnatoy/out/step3/_empty_ ; "
        + "install -cD /dev/null /mnt/rnatoy/out/step4/_empty_ ; "
        + "install -cD /dev/null /mnt/rnatoy/out/step5/_empty_ ; "
        + "install -cD /dev/null /mnt/rnatoy/data/_empty_ ; "
        + "git clone https://github.com/bionimbuzbox/pipeline.git ; "
        + "cp -a pipeline/rnatoy/* /mnt/rnatoy/data/");
    
    Map<String, String> step0Meta = new HashMap<>();
    step0Meta.put("swarm.image", "bionimbuz/git");
    step0Meta.put("swarm.commands", JSONHelper.toJSON(step0Commands));
    step0Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step0Meta.put("environment.vars", "");
    step0Meta.put("environment.work-dir", "/mnt");
    step0Meta.put("sync-output", "/mnt/rnatoy");
    
    Job j0 = new Job("rnatoy-prepare");
    j0.setMeta(step0Meta);
    j0.setWorkflow(w);
    w.addJob(j0);
    
////////////
    
    List<String> step1Commands = Arrays.asList(
        "bowtie2-build",
        "--threads", "1", 
        "/mnt/rnatoy/data/ggal_1_48850000_49020000.Ggal71.500bpflank.fa", 
        "/mnt/rnatoy/out/step1/genome.index");
    
    Map<String, String> step1Meta = new HashMap<>();
    step1Meta.put("swarm.image", "nextflow/rnatoy:1.3");
    step1Meta.put("swarm.commands", JSONHelper.toJSON(step1Commands));
    step1Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step1Meta.put("environment.vars", "");
    step1Meta.put("environment.work-dir", "/mnt/rnatoy/");
    step1Meta.put("sync-input1", "/mnt/rnatoy/data");
    step1Meta.put("sync-input2", "/mnt/rnatoy/out/step1");
    step1Meta.put("sync-output", "/mnt/rnatoy/out/step1");
    
    Job j1 = new Job("rnatoy-bowtie-build");
    j1.setMeta(step1Meta);
    j1.setWorkflow(w);
    j1.addDependency(j0);
    w.addJob(j1);
    
////////////
    
    List<String> step2Commands = Arrays.asList(
        "tophat2",
        "-p", "1",
        "-o", "/mnt/rnatoy/out/step2",
        "--GTF",
        "/mnt/rnatoy/data/ggal_1_48850000_49020000.bed.gff", 
        "/mnt/rnatoy/out/step1/genome.index",
        "/mnt/rnatoy/data/ggal_liver_1.fq", 
        "/mnt/rnatoy/data/ggal_liver_2.fq");
    
    Map<String, String> step2Meta = new HashMap<>();
    step2Meta.put("swarm.image", "nextflow/rnatoy:1.3");
    step2Meta.put("swarm.commands", JSONHelper.toJSON(step2Commands));
    step2Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step2Meta.put("environment.vars", "");
    step2Meta.put("environment.work-dir", "/mnt/rnatoy/");
    step2Meta.put("sync-input1", "/mnt/rnatoy/data");
    step2Meta.put("sync-input2", "/mnt/rnatoy/out/step1");
    step2Meta.put("sync-input3", "/mnt/rnatoy/out/step2");
    step2Meta.put("sync-output", "/mnt/rnatoy/out/step2");
    
    Job j2 = new Job("rnatoy-tophat2-a");
    j2.setMeta(step2Meta);
    j2.setWorkflow(w);
    j2.addDependency(j1);
    w.addJob(j2);
    
////////////
    
    List<String> step3Commands = Arrays.asList(
        "tophat2",
        "-p", "1",
        "-o", "/mnt/rnatoy/out/step3",
        "--GTF",
        "/mnt/rnatoy/data/ggal_1_48850000_49020000.bed.gff", 
        "/mnt/rnatoy/out/step1/genome.index",
        "/mnt/rnatoy/data/ggal_gut_1.fq", 
        "/mnt/rnatoy/data/ggal_gut_2.fq");
    
    Map<String, String> step3Meta = new HashMap<>();
    step3Meta.put("swarm.image", "nextflow/rnatoy:1.3");
    step3Meta.put("swarm.commands", JSONHelper.toJSON(step3Commands));
    step3Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step3Meta.put("environment.vars", "");
    step3Meta.put("environment.work-dir", "/mnt/rnatoy/");
    step3Meta.put("sync-input1", "/mnt/rnatoy/data");
    step3Meta.put("sync-input2", "/mnt/rnatoy/out/step1");
    step3Meta.put("sync-input3", "/mnt/rnatoy/out/step3");
    step3Meta.put("sync-output", "/mnt/rnatoy/out/step3");
    
    
    Job j3 = new Job("rnatoy-tophat2-b");
    j3.setMeta(step3Meta);
    j3.setWorkflow(w);
    j3.addDependency(j1);
    w.addJob(j3);
    
////////////
    
    List<String> step4Commands = Arrays.asList(
        "cufflinks",
        "--no-update-check", 
        "-q", 
        "-p", "1",
        "-o", "/mnt/rnatoy/out/step4",
        "-G",
        "/mnt/rnatoy/data/ggal_1_48850000_49020000.bed.gff", 
        "/mnt/rnatoy/out/step2/accepted_hits.bam");
    
    Map<String, String> step4Meta = new HashMap<>();
    step4Meta.put("swarm.image", "nextflow/rnatoy:1.3");
    step4Meta.put("swarm.commands", JSONHelper.toJSON(step4Commands));
    step4Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step4Meta.put("environment.vars", "");
    step4Meta.put("environment.work-dir", "/mnt/rnatoy/");
    step4Meta.put("sync-input1", "/mnt/rnatoy/data");
    step4Meta.put("sync-input2", "/mnt/rnatoy/out/step2");
    step4Meta.put("sync-input3", "/mnt/rnatoy/out/step4");
    step4Meta.put("sync-output", "/mnt/rnatoy/out/step4");
    
    Job j4 = new Job("rnatoy-cufflinks-a");
    j4.setMeta(step4Meta);
    j4.setWorkflow(w);
    j4.addDependency(j2);
    w.addJob(j4);
    
////////////
    
    List<String> step5Commands = Arrays.asList(
      "cufflinks",
      "--no-update-check", 
      "-q", 
      "-p", "1",
      "-o", "/mnt/rnatoy/out/step5",
      "-G",
      "/mnt/rnatoy/data/ggal_1_48850000_49020000.bed.gff", 
      "/mnt/rnatoy/out/step3/accepted_hits.bam");
    
    Map<String, String> step5Meta = new HashMap<>();
    step5Meta.put("swarm.image", "nextflow/rnatoy:1.3");
    step5Meta.put("swarm.commands", JSONHelper.toJSON(step5Commands));
    step5Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step5Meta.put("environment.vars", "");
    step5Meta.put("environment.work-dir", "/mnt/rnatoy/");
    step5Meta.put("sync-input1", "/mnt/rnatoy/data");
    step5Meta.put("sync-input2", "/mnt/rnatoy/out/step3");
    step5Meta.put("sync-input3", "/mnt/rnatoy/out/step5");
    step5Meta.put("sync-output", "/mnt/rnatoy/out/step5");
    
    Job j5 = new Job("rnatoy-cufflinks-b");
    j5.setMeta(step5Meta);
    j5.setWorkflow(w);
    j5.addDependency(j3);
    w.addJob(j5);
    
    return w;
  }
	
	public Workflow kallisto() {
    Workflow w = new Workflow("kallisto");
 
////////////
    
    List<Map<String,String>> prepareMounts = new ArrayList<>();
    HashMap<String,String> prepareMount1 = new HashMap<>();
    prepareMount1.put("source", "/mnt");
    prepareMount1.put("target", "/mnt");
    prepareMount1.put("readonly", "false");
    prepareMount1.put("type", "bind");
    prepareMounts.add(prepareMount1);
    
    
    List<String> step0Commands = Arrays.asList(
        "/bin/sh",
        "-c", 
        "mkdir -p /mnt/out/step1 /mnt/out/step2 /mnt/out/step3 /mnt/out/step4 /mnt/out/step5 /mnt/data/;"
        + "git clone https://github.com/bionimbuzbox/pipeline.git; "
        + "cp -a pipeline/rnatoy /mnt/data/");
    
    Map<String, String> step0Meta = new HashMap<>();
    step0Meta.put("swarm.image", "bionimbuz/git");
    step0Meta.put("swarm.commands", JSONHelper.toJSON(step0Commands));
    step0Meta.put("swarm.mounts", JSONHelper.toJSON(prepareMounts));
    step0Meta.put("environment.vars", "");
    step0Meta.put("environment.work-dir", "/out");
    
    Job j0 = new Job("rnatoy-prepare");
    j0.setMeta(step0Meta);
    j0.setWorkflow(w);
    w.addJob(j0);
    
////////////
    
    List<Map<String,String>> mounts = new ArrayList<>();
    HashMap<String,String> mount1 = new HashMap<>();
    mount1.put("source", "/mnt/data/rnatoy");
    mount1.put("target", "/data");
    mount1.put("readonly", "false");
    mount1.put("type", "bind");
    mounts.add(mount1);
    
    HashMap<String,String> mount2 = new HashMap<>();
    mount2.put("source", "/mnt/out/rnatoy");
    mount2.put("target", "/out");
    mount2.put("readonly", "false");
    mount2.put("type", "bind");
    mounts.add(mount2);
    
    
////////////
    
    List<String> step1Commands = Arrays.asList(
        "bowtie2-build",
        "--threads", "1", 
        "/data/ggal_1_48850000_49020000.Ggal71.500bpflank.fa", 
        "/out/step1/genome.index");
    
    Map<String, String> step1Meta = new HashMap<>();
    step1Meta.put("swarm.image", "nextflow/rnatoy:1.3");
    step1Meta.put("swarm.commands", JSONHelper.toJSON(step1Commands));
    step1Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step1Meta.put("environment.vars", "");
    step1Meta.put("environment.work-dir", "/out");
    
    Job j1 = new Job("rnatoy-bowtie-build");
    j1.setMeta(step1Meta);
    j1.setWorkflow(w);
    j1.addDependency(j0);
    w.addJob(j1);
    
////////////
    
    List<String> step2Commands = Arrays.asList(
        "tophat2",
        "-p", "1",
        "-o", "/out/step2",
        "--GTF",
        "/data/ggal_1_48850000_49020000.bed.gff", 
        "/out/step1/genome.index",
        "/data/ggal_liver_1.fq", 
        "/data/ggal_liver_2.fq");
    
    Map<String, String> step2Meta = new HashMap<>();
    step2Meta.put("swarm.image", "nextflow/rnatoy:1.3");
    step2Meta.put("swarm.commands", JSONHelper.toJSON(step2Commands));
    step2Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step2Meta.put("environment.vars", "");
    step2Meta.put("environment.work-dir", "/out");
    
    Job j2 = new Job("rnatoy-tophat2-a");
    j2.setMeta(step2Meta);
    j2.setWorkflow(w);
    j2.addDependency(j1);
    w.addJob(j2);
    
////////////
    
    List<String> step3Commands = Arrays.asList(
        "tophat2",
        "-p", "1",
        "-o", "/out/step3",
        "--GTF",
        "/data/ggal_1_48850000_49020000.bed.gff", 
        "/out/step1/genome.index",
        "/data/ggal_gut_1.fq", 
        "/data/ggal_gut_2.fq");
    
    Map<String, String> step3Meta = new HashMap<>();
    step3Meta.put("swarm.image", "nextflow/rnatoy:1.3");
    step3Meta.put("swarm.commands", JSONHelper.toJSON(step3Commands));
    step3Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step3Meta.put("environment.vars", "");
    step3Meta.put("environment.work-dir", "/out");
    
    
    Job j3 = new Job("rnatoy-tophat2-b");
    j3.setMeta(step3Meta);
    j3.setWorkflow(w);
    j3.addDependency(j1);
    w.addJob(j3);
    
////////////
    
    List<String> step4Commands = Arrays.asList(
        "cufflinks",
        "--no-update-check", 
        "-q", 
        "-p", "1",
        "-o", "/out/step4",
        "-G",
        "/data/ggal_1_48850000_49020000.bed.gff", 
        "/out/step2/accepted_hits.bam");
    
    Map<String, String> step4Meta = new HashMap<>();
    step4Meta.put("swarm.image", "nextflow/rnatoy:1.3");
    step4Meta.put("swarm.commands", JSONHelper.toJSON(step4Commands));
    step4Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step4Meta.put("environment.vars", "");
    step4Meta.put("environment.work-dir", "/out");
    
    Job j4 = new Job("rnatoy-cufflinks-a");
    j4.setMeta(step4Meta);
    j4.setWorkflow(w);
    j4.addDependency(j2);
    w.addJob(j4);
    
////////////
    
    List<String> step5Commands = Arrays.asList(
      "cufflinks",
      "--no-update-check", 
      "-q", 
      "-p", "1",
      "-o", "/out/step5",
      "-G",
      "/data/ggal_1_48850000_49020000.bed.gff", 
      "/out/step3/accepted_hits.bam");
    
    Map<String, String> step5Meta = new HashMap<>();
    step5Meta.put("swarm.image", "nextflow/rnatoy:1.3");
    step5Meta.put("swarm.commands", JSONHelper.toJSON(step5Commands));
    step5Meta.put("swarm.mounts", JSONHelper.toJSON(mounts));
    step5Meta.put("environment.vars", "");
    step5Meta.put("environment.work-dir", "/out");
    
    Job j5 = new Job("rnatoy-cufflinks-b");
    j5.setMeta(step5Meta);
    j5.setWorkflow(w);
    j5.addDependency(j3);
    w.addJob(j5);
    
    return w;
  }
	
	public void test() {
	  
	  Workflow workflow = rnatoy();
	  
	  List<Cluster> clusters = new ArrayList<>();
	  clusters.add(buildCluster());
	 
	  SchedulingService service = new SchedulingService();
	  service.execute(workflow, clusters);
	}
	
public void testRnatoy() {
    
    Workflow workflow = rnatoy();
    
    List<Cluster> clusters = new ArrayList<>();
    clusters.add(buildLocalCluster());
    
    SchedulingService service = new SchedulingService();
    service.execute(workflow, clusters);
  }
	
	public void testJSON() {
	  //Workflow w = buildTrapnellWorkflow();
	  Workflow w = rnatoy();
	  LOGGER.info(w.toJSONString());
	}

}

