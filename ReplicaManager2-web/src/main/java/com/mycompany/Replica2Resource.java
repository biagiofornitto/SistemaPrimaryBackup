/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;


import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;

import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import replica2.*;
/**
 * REST Web Service
 *
 * @author biagio
 */
@Path("replica2")
@RequestScoped
public class Replica2Resource {

    Replica2Singleton replica2;

    

    
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Replica2Resource
     */
    public Replica2Resource() {
        replica2=Replica2Singleton.getInstance();
        System.out.println("costruttore rest 2");
    }

    /**
     * Retrieves representation of an instance of com.mycompany.Replica2Resource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("query/{qr}")
    @Produces(MediaType.TEXT_PLAIN)
    public String read(@PathParam("qr") String query) {
       JSONArray le= replica2.readFromDB(query); 
       return le.toString();
          
    }

    /**
     * PUT method for updating or creating an instance of Replica2Resource
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
          System.out.println("Sono nell'add della replica 2");
          System.out.println(logEntryJson);
         replica2.addLocal(logEntryJson);
        // LogEntryNumRequest le = new Gson().fromJson(logEntryJson, LogEntryNumRequest.class);
        // replica2.writeOnDB(le);
       
        
        
        
        
    }
    @POST
    @Path("commit")
    @Consumes(MediaType.TEXT_PLAIN)
    public void commit(String idRequest) {
       // System.out.println("Sono nella commit della replica 2");
         replica2.receiveCommit(idRequest);
         
             
        
    }
    @POST
    @Path("abort")
    @Consumes(MediaType.TEXT_PLAIN)
    public void abort(String idRequest) {
         System.out.println("Sono nell'abort della replica 2");
         replica2.receiveAbort(idRequest);
                    
    }
    
    
    
  

 
}
