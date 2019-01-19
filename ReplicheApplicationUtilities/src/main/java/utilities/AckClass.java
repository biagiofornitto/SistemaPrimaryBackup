/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

/**
 *
 * @author biagio
 */
public class AckClass {
    private String IdRequest;
    private int numReplica;

    public AckClass(String IdRequest, int numReplica) {
        this.IdRequest = IdRequest;
        this.numReplica = numReplica;
    }

    public String getIdRequest() {
        return IdRequest;
    }

    public int getNumReplica() {
        return numReplica;
    }

    public void setIdRequest(String IdRequest) {
        this.IdRequest = IdRequest;
    }

    public void setNumReplica(int numReplica) {
        this.numReplica = numReplica;
    }

    
    
}