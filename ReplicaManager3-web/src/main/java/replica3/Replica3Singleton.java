/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package replica3;


import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.json.JSONArray;
import org.json.JSONObject;
import utilities.AckClass;
import utilities.LogEntryNumRequest;

/**
 *
 * @author biagio
 */
public class Replica3Singleton implements Replica3SingletonLocal{
     public String name = "replica3";
    public final static int NUMBER_OF_REPLICAS = 4;
    private final static String PORT = "3309";
    private final static String DB = "db";
    private final static String hostdb="localhost";
    private final static String PASSWORD = "root";
    public static boolean working = true; 
   
   private Queue<LogEntryNumRequest> codaLog;
   // private final String logFilePath ="C:\\Users\\biagio\\Documents\\NetBeansProjects\\ReplicheApplication\\ReplicheApplication-ejb\\src\\main\\java\\EJB\\LogPrimary";
   // private ArrayList<LogEntryNumRequest> arraylog;
    private static final String URL1ack= "http://localhost:8081/ReplicaManager1-web/webresources/replica1/ack"; 
    
    
    
    
    
    
     private static Replica3Singleton instance;
	
	
	
	/*costruttore privato*/
    private Replica3Singleton() {
            codaLog= new ArrayBlockingQueue<LogEntryNumRequest>(100); 
    }  
									
	/*metodo che mi restituisce instance*/
    public static Replica3Singleton getInstance() {
		if (instance==null){
		/*System.err.println("Istanza creata");*/
		instance = new Replica3Singleton();
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
    public synchronized JSONArray readFromDB(String query) {
        System.out.println("sono nella read 3");
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
       
        //System.out.println("Sono nell'addLocal della replica3");
        LogEntryNumRequest le = new Gson().fromJson(log, LogEntryNumRequest.class);
        //Gson gson = new Gson();
        //log= gson.toJson(le);
        
        //if (codaLog==null)
         //   codaLog=new ArrayBlockingQueue<LogEntryNumRequest>(100);
        
        codaLog.add(le);
        sendAck(le.getID(),3);
         //System.out.println("Ho mandato l'ack dalla replica 3");
   
    }

    @Override
    public void receiveCommit(String idRequest) {
            System.out.println("Sono nella commit della replica 3");
            System.out.println("Committo la richiesta: "+idRequest);
            /*Iterator <LogEntryNumRequest> iterator;
            iterator = getArraylog().iterator();
            System.out.println("Dimensione array2: "+ getArraylog().size());
            while (iterator.hasNext()) {
                LogEntryNumRequest le=iterator.next();
                if (le.getID().equals(idRequest)){
                                                        
                         writeOnDB(le);

                }
            }*/
            if (codaLog.peek().getID().equals(idRequest))
                        writeOnDB(codaLog.poll());
            else
                System.err.println("Commit della richiesta: "+idRequest+"non valido per la replica 3");
        
    }
    
     @Override
     public void sendAck(String idRequest,int numReplica){
         System.out.println( "Sono nella sendack della replica 3");
         System.out.println("Voglio l'ack della richiesta"+idRequest);
        AckClass ack=new AckClass(idRequest,numReplica);
        Gson gson = new Gson();
        String input = gson.toJson(ack);
       Client c = Client.create();
       WebResource webResourcePost = c.resource(URL1ack);
       ClientResponse rispostaPost = webResourcePost.post(ClientResponse.class, input);
       System.out.println("sendReplica3ack: "+rispostaPost.toString());
            


    }

    public Queue<LogEntryNumRequest> getCodaLog() {
        return codaLog;
    }
    
      @Override
    public void receiveAbort(String idRequest) {
           Iterator <LogEntryNumRequest> iterator;
            iterator = getCodaLog().iterator();
            System.out.println("Dimensione coda3: "+ getCodaLog().size());
            while (iterator.hasNext()) {
                LogEntryNumRequest le=iterator.next();
                if (le.getID().equals(idRequest)){
                                                        
                         codaLog.remove(le);

                }
            }
    }
     
     
    
    
    
    
}
