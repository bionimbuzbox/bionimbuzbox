package org.bionimbuzbox.dao.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.net.InetAddress;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Model;
import org.bionimbuzbox.model.Workflow;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class UpsertTest {

  public UpsertTest() {

  }

  public static void test1()  throws Exception {
    String json = jsonBuilder()
        .startObject()
        .field("name", "workflow")
        .startObject("jobs")
        .startObject("job-id")
        .field("name", "j1")
        .field("name", "j1")
        .endObject()
        .endObject()
        .endObject()
        .string();
    System.out.println(json);
  }

  public static void test2() {
    Workflow w = new Workflow("workflow-id");
    w.setName("workflow");

    for (int i=1; i<=3; i++) {
      Job job = new Job("j" + i);
      job.addMetaInfo("key" + i, "value" + i);
      job.addMetaInfo("key" + i, "value" + i);
      
      for (int j=i-1; j>=1; j--) {
        job.addDependency(w.getJob("j" + j ));
      }
      w.addJob(job);
    }
    String json = w.toJSONString();
    System.out.println(json);
    Workflow w2 = Model.buildFromJSONString(json, Workflow.class);
    System.out.println(w2.toJSONString());
    System.out.println(w2 );
  }
  
  public static void test3() throws Exception {
    IndexRequest indexRequest = new IndexRequest("index", "type", "1")
        .source(jsonBuilder()
            .startObject()
            .field("name", "workflow")
            .startObject("jobs")
            .startObject("job-id-1")
            .field("name", "j1")
            .endObject()
            .startObject("job-id-2")
            .field("name", "j2")
            .endObject()
            .endObject()
            .endObject());
    UpdateRequest updateRequest = new UpdateRequest("index", "type", "1")
        .doc(jsonBuilder()
            .startObject()
            .field("name", "workflow")
            .startObject("jobs")
            .startObject("job-id-1")
            .field("name", "job-id-1")
            .endObject()
            .endObject()
            .endObject())
        .upsert(indexRequest); 
    
    TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
        .addTransportAddress(
            new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
    
    client.update(updateRequest).get();
    client.close();
  }

  public static void main(String[] args) throws Exception {
    test2();
  }
}
