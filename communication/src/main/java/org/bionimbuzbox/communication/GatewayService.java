package org.bionimbuzbox.communication;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

public class GatewayService {

  private BrokerService broker;
  
  public GatewayService() {
    // TODO Auto-generated constructor stub
  }
  
  public void configureBroker() {    
    try {
      broker = new BrokerService();
      broker.addConnector("tcp://0.0.0.0:6000");
      broker.setPersistent(false);
      broker.start();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void send(String topicName, String msg) {
    try {
      Connection connection = ActiveMQConnection.makeConnection("tcp://127.0.0.1:6000");
      connection.start();
      
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      
      // Create the destination (Topic or Queue)
      Destination destination = session.createTopic(topicName);

      // Create a MessageProducer from the Session to the Topic or Queue
      MessageProducer producer = session.createProducer(destination);
      producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

      // Create a messages
      TextMessage message = session.createTextMessage(msg);
      
      //session.createObjectMessage()

      // Tell the producer to send the message
      System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
      producer.send(message);

      // Clean up
      session.close();
      connection.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
