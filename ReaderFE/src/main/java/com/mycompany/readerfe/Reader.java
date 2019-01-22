/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.readerfe;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author biagio
 */
public class Reader extends HttpServlet {
    
    //api read delle repliche
    private static final String URL1read= "http://172.21.1.7:8080/ReplicaManager1-web/webresources/replica1/query/"; 
    private static final String URL2read= "http://172.21.1.8:8080/ReplicaManager2-web/webresources/replica2/query/"; 
    private static final String URL3read= "http://172.21.1.9:8080/ReplicaManager3-web/webresources/replica3/query/"; 
    private static final String URL4read= "http://172.21.1.10:8080/ReplicaManager4-web/webresources/replica4/query/"; 
     /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //creo un nuovo client usando jersey
        Client c = Client.create();
        WebResource webResourceGet;            
        
        //nella doGet si capisce quale comando è stato chiamato analizzando il 
        //parametro request
        
       int nReplica = Integer.parseInt(request.getParameter("replica"));
        String parametro="";
        String query="";
        String choice=request.getParameter("choice");
       
        parametro=request.getParameter("param1");
       
        if (parametro==null)
            parametro="non_previsto";
       
        
        switch(choice){
            //lista interfacce
            case "interfacce":  
                   
                        query= "select%20iface%20from%20LogEntries%20group%20by%20iface";        
         
               break;
             //interfaccia più utilizzata
            case "maxuso":
              
                        query="select%20iface,%20TX_OK+RX_OK%20from%20LogEntries%20as%20log%20order%20BY%20(log.TX_OK+log.RX_OK)%20DESC%20LIMIT%201;";
                          
                break;
             //profilo utilizzo interfaccia
            case "profilo":
                     query="select%20*%20from%20LogEntries%20where%20Iface='"+parametro+"'%20ORDER%20BY%20LogEntries.ID%20DESC%20LIMIT%201;";
                  
                
             break;
             
        
        }
         switch(nReplica){
          
             //replica2
            case 2:
              
                     webResourceGet = c.resource(URL2read+query);
                          
                break;
             //replica3
            case 3:
                     webResourceGet = c.resource(URL3read+query);
                  
                
                break;
              //replica4
            case 4:
                      webResourceGet = c.resource(URL4read+query);
                  
                
             break;
             default:
                      webResourceGet= c.resource(URL1read+query);    
              break;
               
        }
        
        
       
        ClientResponse rispostaGet = webResourceGet.get(ClientResponse.class);

        System.out.println("query effettuata: "+rispostaGet.toString());
        String output="";
        if (rispostaGet.getStatus()>=400)
               output="Replica non disponibile prova con un'altra";
        else       
               output = rispostaGet.getEntity(String.class);
        response.setContentType("text/html;charset=UTF-8");
       
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Reader</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Api Reader FE</h1>");
            out.println("<h2> Risultato richiesta: " + output + "</h2>");
           
            out.println("<a  href=http://localhost:8086/ReaderFE/index.html>Torna indietro</a>");
            
            
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
