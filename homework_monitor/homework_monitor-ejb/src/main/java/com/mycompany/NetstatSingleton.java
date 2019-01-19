/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
/**
 *
 * @author biagio
 */
@Singleton
@Startup
public class NetstatSingleton implements NetstatSingletonLocal {
   private List<Map<String,String>> ultimaEntry;
    private final static String QUEUE_NAME = "hello";
    private static final String EXCHANGE_NAME = "logs";
    
   @Override
   public synchronized void execute() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
       
       channel.queueDeclare(QUEUE_NAME, false, false, false, null);
   ultimaEntry= new ArrayList<Map<String,String>>();
    String path = "/home/matteo/Scrivania/homework_monitor/homework_monitor-ejb/src/main/java/com/mycompany/monitor.log";
  
      System.setProperty("user.dir", "mycompany");
    String file = "monitor.log";
    
       //System.out.println(workDir);
 //String userPath = myFile.getPath();
    StringBuilder output = new StringBuilder();
    Process p;
    long unixTime;
       
    try {
        
      // eseguiamo il comando linux
      p = Runtime.getRuntime().exec("netstat -i");
      
       
        FileWriter fw = new FileWriter(file,true); 
       
      // da byte a caratteri
      InputStreamReader in = new InputStreamReader(p.getInputStream());
      BufferedReader reader = new BufferedReader(in);
      
      //scriviamo il risultato su file
      String line = "";
      unixTime = System.currentTimeMillis() / 1000L;
      output.append(unixTime+"\n");
      fw.write(unixTime+"\r\n");
      while ((line = reader.readLine()) != null) {
       
        output.append(line + "\n");
       
      
       fw.write(line+"\r\n");
      }
      
      //si chiude il file e si ferma il comando linux
      fw.close();
      p.destroy();
      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null, output.toString().getBytes("UTF-8"));
    System.out.println(" [x] Sent '" + output.toString() + "'");
    channel.close();
    connection.close();
    
    String[] interfacce = output.toString().split("\n");
     
    //viene eseguito il parsing del testo restituito
    for (int i = 3; i < interfacce.length; i++) {
            String[] campi = interfacce[i].split("\\s+");
            
            //le informazioni vengono conservate in una mappa
            ultimaEntry.add(new HashMap<String,String>());
            int j=i-3;
            Map<String, String> m = ultimaEntry.get(j);
            
                m.put("Iface",campi[0]);
                m.put("MTU",campi[1]);
                m.put("Met",campi[2]);
                m.put("RX-OK", campi[3]);
                m.put("RX-ERR", campi[4]);
                m.put("RX-DRP",campi[5]);
                m.put("RX-OVR", campi[6]);
                m.put("TX-OK", campi[7]);
                m.put("TX-ERR",campi[8]);
                m.put("TX-DRP", campi[9]);
                m.put("TX-OVR", campi[10]);
                m.put("Flg",campi[11]);
            
        }
        
        
        //System.out.println("Thread_Netstat invocato");
         
    }
   
   
   @Override
    public String getInterfacce() {
          
         List <String> listainterf=new ArrayList<String>();  
         Iterator<Map<String, String>> iterator;
         iterator = getUltimaEntry().iterator();
         //vengono cercate le interfacce presenti all'interno della mappa 
         //e restituite in formato JSON
		while (iterator.hasNext()) {
			Map<String, String> m = iterator.next();
                        listainterf.add(m.get("Iface"));
         
				
		}
             
           
               
       try {
          String json = new ObjectMapper().writeValueAsString(listainterf);
           if (json!=null)
                    return json ;
       } catch (JsonProcessingException ex) {
           Logger.getLogger(NetstatSingleton.class.getName()).log(Level.SEVERE, null, ex);
       }

            
        return "NOT WORKING";
       
    }
     @Override  
     public String getProfilo(String profilo) {
               
        //si restituiscono in formato JSON le informazioni dell'interfaccia specificata
        //tramite una ricerca all'interno della mappa
         Iterator<Map<String, String>> iterator;
         iterator = getUltimaEntry().iterator();
		while (iterator.hasNext()) {
			Map<String, String> m = iterator.next();
                        if(m.get("Iface").equals(profilo))
                            
             
            try {
               String json = new ObjectMapper().writeValueAsString(m);
                
                if (json!=null) return json ;
            } catch (JsonProcessingException ex) {
                Logger.getLogger(NetstatSingleton.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
         
        
        return "NOT WORKING";
       
    } 
     @Override
     public String getUsoMaggiore() {
         
         //si restituisce l'interfaccia con l'uso maggiore
         int maxpacchetti=0;
         int npacchetti=0;
         String a;
         Map<String, String> mappatemp=new HashMap<>();
         Iterator<Map<String, String>> iterator;
         iterator = getUltimaEntry().iterator();
         System.out.println(getUltimaEntry().size());
		while (iterator.hasNext()) {
                    
			Map<String, String> m = iterator.next();
                        //si fa la ricerca della più utilizzata all'inerno della mappa
                       
                        npacchetti=Integer.parseInt(m.get("RX-OK"))+Integer.parseInt(m.get("TX-OK"));
                        System.out.println(maxpacchetti);
                        if (maxpacchetti<npacchetti){
                                maxpacchetti=npacchetti;
                                mappatemp.put("Iface",m.get("Iface"));
                                mappatemp.put("Pacchetti_TX+RX_OK",String.valueOf(npacchetti));
                                
                        }
                    }
             
            try {
               String json = new ObjectMapper().writeValueAsString(mappatemp);
               System.out.println(json);
              
                
                if (json!=null) return json ;
            } catch (JsonProcessingException ex) {
                Logger.getLogger(NetstatSingleton.class.getName()).log(Level.SEVERE, null, ex);
                }
            
         
        
        return "NOT WORKING";
       
    }

    public synchronized List<Map<String, String>> getUltimaEntry() {
        return ultimaEntry;
    }

    
    public List<Map<String,String>> getListaRuntime(){
    
    StringBuilder output = new StringBuilder();
       try {
           Process p = Runtime.getRuntime().exec("netstat -i");
           InputStreamReader in = new InputStreamReader(p.getInputStream());
           BufferedReader reader = new BufferedReader(in);
           String line = "";
           
           while ((line = reader.readLine()) != null) {
       
                 output.append(line + "\n");
 
             }
        p.destroy();
        reader.close();
           
           
           
       } catch (IOException ex) {
           Logger.getLogger(NetstatSingleton.class.getName()).log(Level.SEVERE, null, ex);
       }
       List<Map<String,String>> infoRuntime=new ArrayList<Map<String,String>>();
        String[] interfacce = output.toString().split("\n");
         for (int i = 2; i < interfacce.length; i++) {
            String[] campi = interfacce[i].split("\\s+");
            
            //le informazioni vengono conservate in una mappa
           
            infoRuntime.add(new HashMap<String,String>());
            int j=i-2;
            Map<String, String> m = infoRuntime.get(j);
            
                m.put("Iface",campi[0]);
                m.put("MTU",campi[1]);
                m.put("Met",campi[2]);
                m.put("RX-OK", campi[3]);
                m.put("RX-ERR", campi[4]);
                m.put("RX-DRP",campi[5]);
                m.put("RX-OVR", campi[6]);
                m.put("TX-OK", campi[7]);
                m.put("TX-ERR",campi[8]);
                m.put("TX-DRP", campi[9]);
                m.put("TX-OVR", campi[10]);
                m.put("Flg",campi[11]);
            
        }
       
    
         return infoRuntime;
    
    
    };
    
    
    
    @Override
    public String getInterfacceRuntime() {
         List <String> listainterf=new ArrayList<String>();  
         Iterator<Map<String, String>> iterator;
         iterator = getListaRuntime().iterator();
         //vengono cercate le interfacce presenti all'interno della mappa 
         //e restituite in formato JSON
		while (iterator.hasNext()) {
			Map<String, String> m = iterator.next();
                        listainterf.add(m.get("Iface"));
         
				
		}
             
           
               
       try {
          String json = new ObjectMapper().writeValueAsString(listainterf);
           if (json!=null)
                    return json ;
       } catch (JsonProcessingException ex) {
           Logger.getLogger(NetstatSingleton.class.getName()).log(Level.SEVERE, null, ex);
       }

            
        return "NOT WORKING";
       
       
       
    }

    @Override
    public String getUsoMaggioreRuntime() {
         //si restituisce l'interfaccia con l'uso maggiore
         int maxpacchetti=0;
         int npacchetti=0;
         String a;
         Map<String, String> mappatemp=new HashMap<>();
         Iterator<Map<String, String>> iterator;
         iterator = getListaRuntime().iterator();
         System.out.println(getUltimaEntry().size());
		while (iterator.hasNext()) {
                    
			Map<String, String> m = iterator.next();
                        //si fa la ricerca della più utilizzata all'inerno della mappa
                       
                        npacchetti=Integer.parseInt(m.get("RX-OK"))+Integer.parseInt(m.get("TX-OK"));
                        System.out.println(maxpacchetti);
                        if (maxpacchetti<npacchetti){
                                maxpacchetti=npacchetti;
                                mappatemp.put("Iface",m.get("Iface"));
                                mappatemp.put("Pacchetti_TX+RX_OK",String.valueOf(npacchetti));
                                
                        }
                    }
             
            try {
               String json = new ObjectMapper().writeValueAsString(mappatemp);
               System.out.println(json);
              
                
                if (json!=null) return json ;
            } catch (JsonProcessingException ex) {
                Logger.getLogger(NetstatSingleton.class.getName()).log(Level.SEVERE, null, ex);
                }
            
         
        
        return "NOT WORKING";
    }

    @Override
    public String getProfiloRuntime(String profilo) {
       //si restituiscono in formato JSON le informazioni dell'interfaccia specificata
        //tramite una ricerca all'interno della mappa
         Iterator<Map<String, String>> iterator;
         iterator = getListaRuntime().iterator();
		while (iterator.hasNext()) {
			Map<String, String> m = iterator.next();
                        if(m.get("Iface").equals(profilo))
                            
             
            try {
               String json = new ObjectMapper().writeValueAsString(m);
                
                if (json!=null) return json ;
            } catch (JsonProcessingException ex) {
                Logger.getLogger(NetstatSingleton.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
         
        
        return "NOT WORKING";
    }
  
}
