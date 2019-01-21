/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subscriber;

import utilities.*;

import com.rabbitmq.client.*;
import com.google.gson.Gson;
import java.io.IOException;

import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 *
 * @author biagio
 */
public class SubscriberWriter {
   //private static int number;
   private final static String QUEUE_NAME = "hello";
   private static final String EXCHANGE_NAME = "logs";
   private static final String URL = "http://172.21.1.7:8080/ReplicaManager1-web/webresources/replica1/add"; 
   public static void main(String[] argv){
         
         try {
            
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("172.21.1.2");
            factory.setUsername("guest");
            factory.setPassword("guest");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");  
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
             Client c = Client.create(); 
             //c.setConnectTimeout(3000);
             //c.setReadTimeout(30000);
             WebResource webResourcePost = c.resource(URL);

           
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            
                
            String message = new String(delivery.getBody(), "UTF-8");
          
            System.out.println(" [x] Received '" + message + "'");
            String[] interfacce = message.split("\n");
           
            for (int i = 3; i < interfacce.length; i++) {
               
                 
                 String[] campi = interfacce[i].split("\\s+");
                 
                //genero l'id univoco per l'entry
                String ID=Long.toString(System.nanoTime());
                 
                 //compongo una entry per il database
                //l'oggetto LogEntryNumRequest si trova nel package utilities
                System.out.println(ID);
                LogEntryNumRequest le=new LogEntryNumRequest(ID,interfacce[0], campi[0], campi[1],campi[2] , campi[3], campi[4], campi[5], campi[6],campi[7], campi[8],campi[9], campi[10],campi[11]);
                Gson gson = new Gson();
                String input = gson.toJson(le);
                
               
                
                
              
               try{
                ClientResponse rispostaPost = webResourcePost.post(ClientResponse.class, input);    
                //System.out.println(rispostaPost.getStatus());
                System.out.println(rispostaPost.toString());
                } catch (ClientHandlerException e) {
                    //e.printStackTrace();
                   System.err.println("primary do not response");
                }
                
                  
            
            }    
            
            
            };
            
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
           
            
         } catch (IOException ex) {
               Logger.getLogger(SubscriberWriter.class.getName()).log(Level.SEVERE, null, ex);  
        }
          catch (TimeoutException ex) {
            Logger.getLogger(SubscriberWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    
    
    }

}

