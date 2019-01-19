/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;


/**
 *
 * @author matteo
 */
@Singleton
@Startup
public class LoggerSingleton implements LoggerSingletonLocal {
   
    private Map<String, Integer> maxInv=new  HashMap<String,Integer>();
    private Map<String, Integer> maxInv15=new  HashMap<String,Integer>();
    private long nowTime;
    private String performante="";
    private long minTime=-1;  
   

   //per linux
   /*String path="statistiche.log";
   String path2="monitoring.log";*/
    //per windows
    String path="C:\\Users\\biagio\\Documents\\NetBeansProjects\\homework_monitor\\homework_monitor-ejb\\src\\main\\java\\com\\mycompany\\statistiche.log";
    String path2="C:\\Users\\biagio\\Documents\\NetBeansProjects\\homework_monitor\\homework_monitor-ejb\\src\\main\\java\\com\\mycompany\\monitoring.log";
   
   @Override
   public synchronized void execute(){
       

    //si esegue una lettura delle informazioni dal file monitor.log
    //e si salvano le informazioni di interesse nel file monitoring.log
        nowTime = System.currentTimeMillis()/1000L;
         String opInvocata="";
         String opInvocata15="";
    
        try {
            FileReader fr = new FileReader(path);
             BufferedReader r=new BufferedReader(fr);
     
            String line = "";
            //lettura del file
            while (line!=null){
                line=r.readLine();
               
                //viene richiamata la funzione di parsing sul testo del file
                //riga per riga
                if ((line!=null) && (!line.equals(""))) this.splitter(line);
            }
            fr.close();
            // da byte a caratteri
        } catch (IOException ex) {
            Logger.getLogger(LoggerSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       try {
           FileWriter fw=new FileWriter(path2,true);
           
           //si ricerca all'interno della mappa la chiamata più invocata
           int max=0;
           for (String chiave:maxInv.keySet()){
               if (maxInv.get(chiave)>max) {
                   
                   max=maxInv.get(chiave);
                   opInvocata=chiave;
               }
           }
           
            //si ricerca all'interno della mappa la chiamata più invocata
            //negli ultimi 15 minuti
           int max15=0;
           for (String chiave:maxInv15.keySet()){
               if (maxInv15.get(chiave)>max15) {
                    
                   max15=maxInv15.get(chiave);
                   
                   opInvocata15=chiave;
               }
           }
        if(opInvocata15.equals("")) opInvocata15="nessuna";
        if(opInvocata.equals("")) opInvocata="nessuna";
        if(performante.equals("")) performante="nessuna";
        
        //inserimento dei dati all'interno di una mappa per la conversione in JSON
        Map<String, String> dati =new  HashMap<String,String>();
        dati.put("timestamp", String.valueOf(nowTime));
        dati.put("op_più_invocata", opInvocata);
        dati.put("op_più_performante ",performante);
        dati.put("op_più_invocata_ultimi_15_min  ", opInvocata15);
       
        //scrittura su file
        String json = new ObjectMapper().writeValueAsString(dati);
        fw.write(json+"\r\n");
        fw.close();
           
       } catch (IOException ex) {
           Logger.getLogger(LoggerSingleton.class.getName()).log(Level.SEVERE, null, ex);
       }
    
    
  
        System.out.println("ThreadLogger invocato");
    
    }

    
    public void splitter(String line){
       
        //viene eseguito un parsing dei dati prelevati dal file in formato JSON
        long startTime=0;
        long performance=0;
        
        //levo le parentesi graffe
        String frase=line.substring(1,line.length()-1);
       
        
        for(String elemento:frase.split(",")){
            String[] e=elemento.split(":");
            
            //levo le virgolette di inizio e fine stringa
            e[0]=e[0].substring(1,e[0].length()-1);
            e[1]=e[1].substring(1,e[1].length()-1);
            
           
            //mi salvo il tempo di esecuzione
            if (e[0].equals("tempo_exe_ns")) performance=Long.valueOf(e[1]);
            
            //mi salvo il tempo di inizio
            if (e[0].equals("tempo_ric")) startTime=Long.valueOf(e[1]);
            
            //conto quante volte è invocato un metodo
            if (e[0].equals("metodo")){
                
                //901 sono 15 minuti in linux epoch
                
                if ((nowTime-startTime)<901) {
                        if(maxInv15.containsKey(e[1])) maxInv15.replace(e[1], maxInv.get(e[1])+1);
                        else maxInv15.put(e[1], 1);
              
                    }
                
                //qui conto le occorrenze dei metodi 
                if(maxInv.containsKey(e[1])) maxInv.replace(e[1], maxInv.get(e[1])+1);
                else maxInv.put(e[1], 1);
                
                //mi salvo il processo piu performante sia tempo che nome
                if ((performance<minTime) || (minTime==-1)){
                    minTime=performance;
                    performante=e[1];
                }
                
            }
            
        }
        
    }

    @Override
    public String getUltimaInfo() {
       String result="";
        
        //viene restituita l'ultima informazione presente nel file monitoring.log
        FileReader fr;
       try {
           fr = new FileReader(path2);
       
        BufferedReader r=new BufferedReader(fr);
    
        String line = "";
        
         while (line!=null){
                line=r.readLine();
                if (line!=null) result=line;
               
            }
       
           fr.close();
           } catch (FileNotFoundException ex) {
           Logger.getLogger(LoggerSingleton.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IOException ex) {
           Logger.getLogger(LoggerSingleton.class.getName()).log(Level.SEVERE, null, ex);
       }
        return result;
        
    }    
}