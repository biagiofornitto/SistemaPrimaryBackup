/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;



import com.google.gson.Gson;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import utilities.AckClass;
import utilities.LogEntryNumRequest;
import replica1.*;
/**
 * REST Web Service
 *
 * @author biagio
 */
@Path("replica1")
@RequestScoped
public class Replica1Resource {

    Replica1Singleton replica1;

   
    
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Replica1Resource
     */
    public Replica1Resource() {
        replica1=Replica1Singleton.getInstance();
        System.out.println("costruttore rest 1");
    }

    /**
     * Retrieves representation of an instance of com.mycompany.Replica1Resource
     * @param query
     * @return an instance of java.lang.String
     */
    @GET
    @Path("query/{qr}")
    @Produces(MediaType.TEXT_PLAIN)
    public String read(@PathParam("qr") String query) {
       JSONArray le= replica1.readFromDB(query); 
       return le.toString();
          
    }

    /**
     * PUT method for updating or creating an instance of Replica1Resource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    @POST
    @Path("add")
    @Consumes(MediaType.TEXT_PLAIN)
    public void add(String logEntryJson) {
        System.out.println("Sono nell'add del primary");
          LogEntryNumRequest le = new Gson().fromJson(logEntryJson, LogEntryNumRequest.class);
         // le = new Gson().fromJson(logEntryJson, LogEntryNumRequest.class);
         replica1.addLocal(logEntryJson);
         // LogEntryNumRequest le = new Gson().fromJson(logEntryJson, LogEntryNumRequest.class);
         // primary.writeOnDB(le);
        
        
    }
    @POST
    @Path("ack")
    @Consumes(MediaType.TEXT_PLAIN)
    public void ack(String ackJson) {
        
         System.out.println("Sono nell'ack del primary");
         AckClass a=new Gson().fromJson(ackJson,AckClass.class);
         System.out.println(a.toString());
         replica1.receiveAck(a);
         
         
          
        
        
    }
    
    


}
