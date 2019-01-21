/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package replica4;


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
public class Replica4Singleton implements Replica4SingletonLocal{
     public String name = "replica4";
    public final static int NUMBER_OF_REPLICAS = 4;
    private final static String PORT = "3306";
    private final static String DB = "db";
    private final static String hostdb="172.21.1.6";
    private final static String PASSWORD = "root";
    public static boolean working = true; 
   
   private Queue<LogEntryNumRequest> codaLog;
   // private final String logFilePath ="C:\\Users\\biagio\\Documents\\NetBeansProjects\\ReplicheApplication\\ReplicheApplication-ejb\\src\\main\\java\\EJB\\LogPrimary";
   // private ArrayList<LogEntryNumRequest> arraylog;
    private static final String URL1ack= "http://172.21.1.7:8080/ReplicaManager1-web/webresources/replica1/ack"; 
    
    
    
    
    
    
     private static Replica4Singleton instance;
	
	
	
	/*costruttore privato*/
    private Replica4Singleton() {
            codaLog= new ArrayBlockingQueue<LogEntryNumRequest>(100); 
    }  
									
	/*metodo che mi restituisce instance*/
    public static Replica4Singleton getInstance() {
		if (instance==null){
		/*System.err.println("Istanza creata");*/
		instance = new Replica4Singleton();
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
        System.out.println("sono nella read 4");
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
        //Gson gson = new Gson();
        //log= gson.toJson(le);
        
        //if (codaLog==null)
         //   codaLog=new ArrayBlockingQueue<LogEntryNumRequest>(100);
        
        codaLog.add(le);
        sendAck(le.getID(),2);
   
    }

    @Override
    public void receiveCommit(String idRequest) {
            System.out.println("Sono nella commit della replica 4");
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
                System.err.println("Commit della richiesta: "+idRequest+"non valido per la replica 4");
        
    }
    
     @Override
     public void sendAck(String idRequest,int numReplica){
        
        AckClass ack=new AckClass(idRequest,numReplica);
        Gson gson = new Gson();
        String input = gson.toJson(ack);
       Client c = Client.create();
       WebResource webResourcePost = c.resource(URL1ack);
       ClientResponse rispostaPost = webResourcePost.post(ClientResponse.class, input);
       System.out.println("sendReplica4ack: "+rispostaPost.toString());
            


    }

    public Queue<LogEntryNumRequest> getCodaLog() {
        return codaLog;
    }
    
     
     @Override
    public void receiveAbort(String idRequest) {
           Iterator <LogEntryNumRequest> iterator;
            iterator = getCodaLog().iterator();
            System.out.println("Dimensione coda4: "+ getCodaLog().size());
            while (iterator.hasNext()) {
                LogEntryNumRequest le=iterator.next();
                if (le.getID().equals(idRequest)){
                                                        
                         codaLog.remove(le);

                }
            }
    } 
     
    
    
    
    
}
