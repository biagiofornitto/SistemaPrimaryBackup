/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import javax.ejb.Local;

/**
 *
 * @author biagio
 */
@Local
public interface NetstatSingletonLocal {
    public String getInterfacce();
    public void execute() throws Exception;
    public String getUsoMaggiore();
    public String getProfilo(String nome);
    public String getInterfacceRuntime();
    public String getUsoMaggioreRuntime();
    public String getProfiloRuntime(String profilo);
    
}
