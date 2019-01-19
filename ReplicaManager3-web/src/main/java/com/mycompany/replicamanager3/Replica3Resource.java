/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.replicamanager3;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import replica3.*;
/**
 * REST Web Service
 *
 * @author biagio
 */
@Path("replica3")
public class Replica3Resource {

    @Context
    private UriInfo context;

    Replica3Singleton replica3;
    /**
     * Creates a new instance of Replica3Resource
     */
    public Replica3Resource() {
        replica3=Replica3Singleton.getInstance();
        System.out.println("costruttore rest 3");
    }

    /**
     * Retrieves representation of an instance of com.mycompany.replicamanager3.Replica3Resource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("query/{qr}")
    @Produces(MediaType.TEXT_PLAIN)
    public String read(@PathParam("qr") String query) {
       JSONArray le= replica3.readFromDB(query); 
       return le.toString();
          
    }

    /**
     * PUT method for updating or creating an instance of Replica3Resource
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
          System.out.println("Sono nell'add della replica 3");
          System.out.println(logEntryJson);
         replica3.addLocal(logEntryJson);
        // LogEntryNumRequest le = new Gson().fromJson(logEntryJson, LogEntryNumRequest.class);
        // replica2.writeOnDB(le);
       
        
        
        
        
    }
    @POST
    @Path("commit")
    @Consumes(MediaType.TEXT_PLAIN)
    public void commit(String idRequest) {
       // System.out.println("Sono nella commit della replica 3");
         replica3.receiveCommit(idRequest);
                
    }
    @POST
    @Path("abort")
    @Consumes(MediaType.TEXT_PLAIN)
    public void abort(String idRequest) {
         System.out.println("Sono nell'abort della replica 3");
         replica3.receiveAbort(idRequest);
                    
    }
    
}
