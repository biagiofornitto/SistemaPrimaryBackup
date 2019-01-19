/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author matteo
 */
public class ApiLogger extends HttpServlet implements Runnable {
   
   @EJB
   private LoggerSingletonLocal logger;
   
    int tempo=10000;

    
    Thread writer2;

    
     
   @Override
    public void init() throws ServletException {
    writer2 = new Thread(this);
    //avvio il thread che legge dal file delle statistiche
    //(programma java che esegue ciclicamente)
    writer2.start();
    System.out.println("Ciao sono il thread writer2");
     
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
        
        //preleviamo le informazioni dal file invocando il metodo dell' EJB
        String result=logger.getUltimaInfo();
        
        if (result==null)
            result="File ancora vuoto, invoca almeno una API";
        
        //restituiamo una pagina con le informazioni
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ApiLogger</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>ApiLogger</h1>");
            out.println("<h2> Risultato richiesta: " + result + "</h2>");
           
            out.println("<a  href=http://localhost:8080/homework_monitor-web/apilogger.html>Torna indietro</a>");
            out.println("</body>");
            out.println("</html>");
        }
        
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
        int temp=Integer.parseInt(request.getParameter("secondi"))*1000;
        
        //impostiamo il nuovo tempo di esecuzione del thread lettore
        this.setTempo(temp);
        
        //restituiamo una pagina con le informazioni
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Logger</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>ApiLogger</h1>");
            out.println("<h2> Risultato richiesta: Tempo settato a " +temp/1000+ " secondi</h2>");
            
            out.println("<a  href=http://localhost:8080/homework_monitor-web/apilogger.html>Torna indietro</a>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

    @Override
    public void run() {
       while(true) {
           System.out.println("Sono nel run dell'APIlogger");
            //consente l'esecuzione dello script java
            logger.execute();
            
           try {
               Thread.sleep(getTempo());
               System.out.println(getTempo());
           } catch (InterruptedException ex) {
               Logger.getLogger(ApiLogger.class.getName()).log(Level.SEVERE, null, ex);
           }
           
          
        }
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public int getTempo() {
        return tempo;
    }
   @Override
     public void destroy() {
          writer2.stop();
  }

}
