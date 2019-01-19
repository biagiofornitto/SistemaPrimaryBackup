/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author biagio
 */
public class ApiServlet extends HttpServlet implements Runnable{
    
     //String path2 = "/home/matteo/Scrivania/homework_monitor/homework_monitor-ejb/src/main/java/com/mycompany/statistiche.log";
    /*per linux
    String path2)"statistiche.log";*/
    
     String path2="C:\\Users\\biagio\\Documents\\NetBeansProjects\\homework_monitor\\homework_monitor-ejb\\src\\main\\java\\com\\mycompany\\statistiche.log";
     
     FileWriter fw;
      private int tempo=10000;
      @EJB
      private NetstatSingletonLocal netstat;
        
     Thread writer1;

   
     
     @Override
    public void init() throws ServletException {
        
        //avvio il thread che invoca il comando netstat -i
        //(programma java che esegue ciclicamente)
    writer1 = new Thread(this);
         try {
             
             fw = new FileWriter(path2,true);
             
             fw.close();
         } catch (IOException ex) {
             Logger.getLogger(ApiServlet.class.getName()).log(Level.SEVERE, null, ex);
         }
    writer1.start();
    System.out.println("Ciao sono il thread writer1");
     
  }
    
  
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long tempo_ric=System.currentTimeMillis()/1000L;
        String nome_metodo="";
        String parametro="";
        String parametro_runtime="";
        
        
        long stopTime=0;
         fw = new FileWriter(path2,true); 
        //nella doGet si capisce quale comando è stato chiamato analizzando il 
        //parametro request
        
        
        String result="";
        
        String choice=request.getParameter("choice");
       
        parametro=request.getParameter("param1");
        parametro_runtime=request.getParameter("runtime");
        if (parametro==null)
            parametro="non_previsto";
        
        if (parametro_runtime==null)
            parametro_runtime="false";
        
        long startTime=System.nanoTime();
        switch(choice){
            case "interfacce":  
                    if (parametro_runtime.equals("true")){
                            result= netstat.getInterfacceRuntime();
                            nome_metodo="getInterfacceRuntime()";
                    }
                    else{
                        
                        result= netstat.getInterfacce();
                        nome_metodo="getInterfacce()";
                    }
                    stopTime=System.nanoTime();
                    
         
               break;
            case "maxuso":
                if (parametro_runtime.equals("true")){
                    result = netstat.getUsoMaggioreRuntime();
                    nome_metodo="getUsoMaggioreRuntime";
                }
                else{   
                
                result = netstat.getUsoMaggiore();
                nome_metodo="getUsoMaggiore()";
                }
                stopTime=System.nanoTime();
                 
                break;
            case "profilo":
                if (parametro_runtime.equals("true")){
                    result =netstat.getProfiloRuntime(parametro);
                    nome_metodo="getProfiloRuntime";
                    
                }
                else{
                     result =netstat.getProfilo(parametro);
                    nome_metodo="getProfilo()";
                }
                   stopTime=System.nanoTime();
             break;
             
        
        }
        
        //nelle operazioni seguenti si converte il testo in formato json per 
        //essere scritto su file
        
        System.out.println(String.valueOf(startTime));
         StringBuilder output = new StringBuilder();
         output.insert(0, String.valueOf(startTime));
         
        //i dati vengono inseriti all'interno di una mappa che rende piu semplice 
        //la conversione in json
        
        Map<String, String> dati =new  HashMap<String,String>();
        dati.put("tempo_ric", String.valueOf(tempo_ric));
        dati.put("tempo_exe_ns", String.valueOf(stopTime-startTime));
        dati.put("metodo",nome_metodo); 
        dati.put("tipo_metodo", request.getMethod());
        
        dati.put("parametri", "interfaccia="+parametro+"_runtime="+parametro_runtime);
        
        //la mappa viene convertita in json e scritta su file
        String json = new ObjectMapper().writeValueAsString(dati);
        fw.write(json+"\r\n");
                 
        
        //viene restituita una pagina per visualizzare la risposta
         try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ApiServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>ApiServlet</h1>");
            out.println("<h2> Risultato richiesta: " + result + "</h2>");
           
            out.println("<a  href=http://localhost:8080/homework_monitor-web/apiservlet.html>Torna indietro</a>");
            out.println("</body>");
            out.println("</html>");
        }
         fw.close();
         
    }

        
        
        
        
        
        
    

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param req
     * @param resp
     
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */ 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long tempo_ric=System.currentTimeMillis()/1000L;
        long stop_time=0;
        int temp=Integer.parseInt(req.getParameter("secondi"))*1000;
        long startTime=System.nanoTime();
        
        //viene settato il nuovo tempo di esecuzione
        setTempo(temp);
        
        long stopTime=System.nanoTime();
         fw = new FileWriter(path2,true); 
        
         
        
         //viene restituita una pagina di risposta
         
         try (PrintWriter out = resp.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ApiServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>ApiServlet</h1>");
            out.println("<h2> Risultato richiesta: Tempo settato a " +temp/1000+ " secondi</h2>");
            
            out.println("<a  href=http://localhost:8080/homework_monitor-web/apiservlet.html>Torna indietro</a>");
            out.println("</body>");
            out.println("</html>");
        }
          
         // si conservano le informazioni relative alla chiamata come nella doGet
          stopTime=System.nanoTime();
         StringBuilder output = new StringBuilder();
         output.insert(0, String.valueOf(startTime));
        Map<String, String> dati =new  HashMap<String,String>();
        dati.put("tempo_ric", String.valueOf(tempo_ric));
        dati.put("tempo_exe_ns", String.valueOf(stopTime-startTime));
        dati.put("metodo","setTempo()");
        dati.put("tipo_metodo", req.getMethod());
        dati.put("parametri", "secondi="+req.getParameter("secondi"));
                
                
        
        
        String json = new ObjectMapper().writeValueAsString(dati);
        fw.write(json+"\r\n");
        fw.close();
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    } 

    @Override
    public void run() {
       while(true) {
           try {
               //consente l'esecuzione dello script java
               netstat.execute();
           } catch (Exception ex) {
               Logger.getLogger(ApiServlet.class.getName()).log(Level.SEVERE, null, ex);
           }
            System.out.println("Sono nel run dell'apiservlet");
           try {
               Thread.sleep(getTempo());
               System.out.println(getTempo());
           } catch (InterruptedException ex) {
               Logger.getLogger(ApiServlet.class.getName()).log(Level.SEVERE, null, ex);
           }
           
          
        }
    }
    
     @Override
     public void destroy() {
          writer1.stop();
  }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

}
