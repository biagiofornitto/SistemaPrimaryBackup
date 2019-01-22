/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package replica1;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import utilities.*;
import org.json.*;
/**
 *
 * @author biagio
 */
public class Replica1Singleton implements Replica1SingletonLocal{
     public String name = "replica1";
    public static int NUMBER_OF_REPLICAS = 4;
    private final static String PORT = "3306";
    private final static String DB = "db";
    private final static String hostdb="172.21.1.3";
    private final static String PASSWORD = "root";
    public static boolean working = true; 
   
    
     private static int countAck;
   // private final String logFilePath ="C:\\Users\\biagio\\Documents\\NetBeansProjects\\ReplicheApplication\\ReplicheApplication-ejb\\src\\main\\java\\EJB\\LogPrimary";
    private Queue<LogEntryNumRequest> codaLog;
    private Map<String,Integer> mapAck;
    private Map<Integer,Integer> mapAbort;
    private static final String URL2add= "http://172.21.1.8:8080/ReplicaManager2-web/webresources/replica2/add"; 
     private static final String URL3add= "http://172.21.1.9:8080/ReplicaManager3-web/webresources/replica3/add"; 
     private static final String URL4add="http://172.21.1.10:8080/ReplicaManager4-web/webresources/replica4/add"; 
     private static final String URL2commit = "http://172.21.1.8:8080/ReplicaManager2-web/webresources/replica2/commit"; 
     private static final String URL3commit = "http://172.21.1.9:8080/ReplicaManager3-web/webresources/replica3/commit"; 
     private static final String URL4commit="http://172.21.1.10:8080/ReplicaManager4-web/webresources/replica4/commit"; 
      private static final String URL2abort = "http://172.21.1.8:8080/ReplicaManager2-web/webresources/replica2/abort"; 
     private static final String URL3abort = "http://172.21.1.9:8080/ReplicaManager3-web/webresources/replica3/abort"; 
     private static final String URL4abort="http://172.21.1.10:8080/ReplicaManager4-web/webresources/replica4/abort"; 	
    
     private static Replica1Singleton instance;
	
	
	
	/*costruttore privato*/
	private Replica1Singleton() {
            codaLog= new ArrayBlockingQueue<LogEntryNumRequest>(100); 
            mapAck=new HashMap<String,Integer>();
            mapAbort=new HashMap<Integer, Integer>();
            for (int i=2; i <=NUMBER_OF_REPLICAS ; i++){
                    
                mapAbort.put(i, 0);
        
        
        }
        }  
									
	/*metodo che mi restituisce instance*/
	public static Replica1Singleton getInstance() {
		if (instance==null){
		/*System.err.println("Istanza creata");*/
		instance = new Replica1Singleton();
		}
		return instance;
	
	}
    
 
    
    
    

    @Override
    public String getName() {
        return name;
       
    }

  
    @Override
    public synchronized void writeOnDB(LogEntryNumRequest log) {
        String myQuery = "INSERT INTO LogEntries VALUES ('" + 
                log.getID()+ "', '" + 
                log.getTimestamp()+ "', '" + 
                log.getIface() + "', '" +
                log.getMTU() + "', '" +
                log.getMet()+ "', '" +
                log.getRX_OK()+ "', '" +
                log.getRX_ERR()+"', '" +
                log.getRX_DRP()+"', '" +
                log.getRX_OVR()+"', '" +
                log.getTX_OK()+"', '" +
                log.getTX_ERR()+"', '" +
                log.getTX_DRP()+"', '" +
                log.getTX_OVR()+"', '" +
                log.getFlg()+ "');";
                   
        
        doQuery(myQuery);
}
    public void doQuery (String query) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }
        try{
            //System.out.println("Sto scrivendo sul DB");
           // Connection con = DriverManager.getConnection("jdbc:mysql://192.168.99.100:"+ PORT + "/" + DB + "?autoReconnect=true&useSSL=false","root",PASSWORD);          
            Connection con = DriverManager.getConnection("jdbc:mysql://"+hostdb+":"+ PORT + "/" + DB + "?autoReconnect=true&useSSL=false","root",PASSWORD);          
            // create the java statement
            Statement st = con.createStatement();

            // execute the query
            int rs = st.executeUpdate(query);

            st.close();
            con.close();
            //System.out.println("Scritto su DB1");
            working = true;
        }
        catch (SQLException e)
        {
            //Logger.getLogger(ReplicaManagerBean1.class.getName()).log(Level.SEVERE, null, e);
            working = false;
            
        }
    }

    @Override  
    public JSONArray readFromDB(String query) {
        System.out.println("sono nella read 1");
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return null;
        }
            
             JSONArray jsonResult = new JSONArray();
        try{

            Connection con = DriverManager.getConnection("jdbc:mysql://"+hostdb+":"+ PORT + "/" + DB + "?autoReconnect=true&useSSL=false","root",PASSWORD);
            
            // create the java statement
            Statement st = con.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);
            
            
           
            ResultSetMetaData rsmd = rs.getMetaData();
            // iterate through the java resultset
            while (rs.next())
            {
                //conversion resultset to json
                
                int numColumns = rsmd.getColumnCount();
                 JSONObject obj = new JSONObject();
                 for (int i=1; i<=numColumns; i++) {
                 String column_name = rsmd.getColumnName(i);
                 obj.put(column_name, rs.getObject(column_name));
                
                 }
                 System.out.println("Sto per aggiungere " + obj);
                 jsonResult.put(obj);
            }
            st.close();
            con.close();
        }
        catch (SQLException e)
        {
          System.err.println(e.getMessage());
        }
        return jsonResult;
    }

    @Override
    public void addLocal(String log) {
      
        
        LogEntryNumRequest le = new Gson().fromJson(log, LogEntryNumRequest.class);
        
     
            
        codaLog.add(le);
        mapAck.put(le.getID(),0);
        
        System.out.println("Size coda: "+codaLog.size());
        addLocalRepliche(log);
     
  
    }
    
     public void addLocalRepliche(String log){
    
         
                Client c = Client.create();  
                c.setReadTimeout(15000);
                WebResource webResourcePost;
                ClientResponse rispostaPost;
                
                
                
                //invio entry a replica2
                if (mapAbort.containsKey(2)){
                
                
                        webResourcePost = c.resource(URL2add);

                        //questa chiamata ritornerà quando la replica avrà fatto il commit definitivo
                       try{
                            rispostaPost = webResourcePost.post(ClientResponse.class, log);  
                            System.out.println("sendReplica2add: "+rispostaPost.toString());
                            if (rispostaPost.getStatus()>=400){

                                System.err.println("replica2 do not response,error: "+rispostaPost.getStatus());
                                countAbort(2);
                               
                                LogEntryNumRequest le = new Gson().fromJson(log, LogEntryNumRequest.class);
                                sendAbort(le.getID());
                                return;
                        
                                     
                           }
                                  
       
                        }  catch (ClientHandlerException e) {
                           e.printStackTrace();
                           System.err.println("replica2 do not response");
                           countAbort(2);
                            LogEntryNumRequest le = new Gson().fromJson(log, LogEntryNumRequest.class);              
                          
                           sendAbort(le.getID());
                           return;
                        }
                }  
                
                
                  //invio a replica3
                  if (mapAbort.containsKey(3)){
                            
                            webResourcePost = c.resource(URL3add);
                            try{
                            rispostaPost = webResourcePost.post(ClientResponse.class, log);  
                            System.out.println("sendReplica3add: "+rispostaPost.toString());
                            if (rispostaPost.getStatus()>=400){

                                System.err.println("replica3 do not response,error: "+rispostaPost.getStatus());
                                countAbort(3);
                                LogEntryNumRequest le = new Gson().fromJson(log, LogEntryNumRequest.class);
                                sendAbort(le.getID());
                                return;
                        
                                     
                           }
                            }  catch (ClientHandlerException e) {
                           // e.printStackTrace();
                            e.printStackTrace();
                           System.err.println("replica3 do not response");
                           
                           
                           countAbort(3);
                            LogEntryNumRequest le = new Gson().fromJson(log, LogEntryNumRequest.class);  
                           sendAbort(le.getID());
                           return;
                              
                            }
                  }
                     
                      //invio a replica2
                      if (mapAbort.containsKey(4)){
                            
                            webResourcePost = c.resource(URL4add);
                            try{
                            rispostaPost = webResourcePost.post(ClientResponse.class, log);  
                            System.out.println("sendReplica4add: "+rispostaPost.toString());
                            if (rispostaPost.getStatus()>=400){

                                System.err.println("replica4 do not response,error: "+rispostaPost.getStatus());
                                countAbort(4);
                             
                                LogEntryNumRequest le = new Gson().fromJson(log, LogEntryNumRequest.class);
                                sendAbort(le.getID());
                               
                        
                                     
                           }
                            
                            
                            
                            
                            }  catch (ClientHandlerException e) {
                           // e.printStackTrace();
                            e.printStackTrace();
                           System.err.println("replica4 do not response");
                           
                           
                           countAbort(4);
                            LogEntryNumRequest le = new Gson().fromJson(log, LogEntryNumRequest.class);  
                           sendAbort(le.getID());
                         
                              
                            }
                  }
                  
                  
                
            
             
    
    }
    
    



    @Override
    public void receiveAck(AckClass ack) {
       
        
        System.out.println("Sono nella receive ack");
        System.out.println("Id richiesta ack: "+ack.getIdRequest());
        if(!mapAck.containsKey(ack.getIdRequest())) {
                System.err.println("Ack non valido ricevuto da replica: "+ack.getNumReplica()); 
            }
         else {
             mapAck.put(ack.getIdRequest(), mapAck.get(ack.getIdRequest())+1);
        }
        
        if (mapAck.get(ack.getIdRequest())==NUMBER_OF_REPLICAS-1){
           
            //si fa il commit locale eliminando l'entry dalla coda
            writeOnDB(codaLog.poll());
            
            //si manda il commit alle repliche
            sendCommit(ack.getIdRequest());
            
           
           
       
           
        }
    }
    
    
     @Override
     public void sendCommit(String idRequest){
            Client c=Client.create();
            WebResource webResourcePost;
            ClientResponse rispostaPost;
            if(mapAbort.containsKey(2)){
             
                webResourcePost = c.resource(URL2commit);
                rispostaPost = webResourcePost.post(ClientResponse.class,idRequest);
                System.out.println("sendReplica2commit: "+rispostaPost.toString());
            }
            if(mapAbort.containsKey(3)){
                
                webResourcePost = c.resource(URL3commit);
                rispostaPost = webResourcePost.post(ClientResponse.class, idRequest);
                System.out.println("sendReplica3commit: "+rispostaPost.toString());
            }
            if (mapAbort.containsKey(4)){
            
                webResourcePost = c.resource(URL4commit);
                rispostaPost = webResourcePost.post(ClientResponse.class,idRequest );
                System.out.println("sendReplica4commit: "+rispostaPost.toString());
            }  
                
                   
    
    }

    public Queue<LogEntryNumRequest> getCodaLog() {
        return codaLog;
    }

    public Map<String, Integer> getMapAck() {
        return mapAck;
    }

   
    @Override
    public void sendAbort(String idRequest){
            Client c=Client.create();
            WebResource webResourcePost = c.resource(URL2abort);
            ClientResponse rispostaPost;
           try{
                rispostaPost = webResourcePost.post(ClientResponse.class, idRequest);  
                System.out.println("sendReplica2abort: "+rispostaPost.toString());
                }  
           catch (ClientHandlerException e) {
                     e.printStackTrace();
                     System.err.println("replica2 do not response abort");    
    
                }
           webResourcePost = c.resource(URL3abort);
           try{
                rispostaPost = webResourcePost.post(ClientResponse.class, idRequest);  
                System.out.println("sendReplica3abort: "+rispostaPost.toString());
                }  
           catch (ClientHandlerException e) {
                     e.printStackTrace();
                     System.err.println("replica3 do not response abort");    
    
                }
           webResourcePost = c.resource(URL4abort);
           try{
                rispostaPost = webResourcePost.post(ClientResponse.class, idRequest);  
                System.out.println("sendReplica4abort: "+rispostaPost.toString());
                }  
           catch (ClientHandlerException e) {
                     e.printStackTrace();
                     System.err.println("replica4 do not response abort");    
    
                }
           Gson gson = new Gson();
           String log= gson.toJson(codaLog.peek());
           ///se qualche replica ha già mandato l'ack si azzera il conteggio relativo a quella entry
           mapAck.put(codaLog.peek().getID(),0);
           
           //si riprova l'inoltro alle repliche
           addLocalRepliche(log);
    
    }
    
    
    public void countAbort(int numReplica){
            
        mapAbort.put(numReplica,mapAbort.get(numReplica)+1);
        if (mapAbort.get(numReplica)==3){ 
                         mapAbort.remove(numReplica);
                         NUMBER_OF_REPLICAS--;

                       }
    
    
    }
}
