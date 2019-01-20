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
public class LogEntryNumRequest {
    
    private String ID;
    private String Timestamp;
    private String Iface;
    private String MTU;
    private String Met;
    private String RX_OK;
    private String RX_ERR;
    private String RX_DRP;
    private String RX_OVR;
    private String TX_OK;
    private String TX_ERR;
    private String TX_DRP;
    private String TX_OVR;
    private String Flg;

    public LogEntryNumRequest(String ID, String Timestamp, String Iface, String MTU, String Met, String RX_OK, String RX_ERR, String RX_DRP, String RX_OVR, String TX_OK, String TX_ERR, String TX_DRP, String TX_OVR, String Flg) {
        this.ID = ID;
        this.Timestamp = Timestamp;
        this.Iface = Iface;
        this.MTU = MTU;
        this.Met = Met;
        this.RX_OK = RX_OK;
        this.RX_ERR = RX_ERR;
        this.RX_DRP = RX_DRP;
        this.RX_OVR = RX_OVR;
        this.TX_OK = TX_OK;
        this.TX_ERR = TX_ERR;
        this.TX_DRP = TX_DRP;
        this.TX_OVR = TX_OVR;
        this.Flg = Flg;
    }

    public LogEntryNumRequest(String Timestamp, String Iface, String MTU, String Met, String RX_OK, String RX_ERR, String RX_DRP, String RX_OVR, String TX_OK, String TX_ERR, String TX_DRP, String TX_OVR, String Flg) {
        this.Timestamp = Timestamp;
        this.Iface = Iface;
        this.MTU = MTU;
        this.Met = Met;
        this.RX_OK = RX_OK;
        this.RX_ERR = RX_ERR;
        this.RX_DRP = RX_DRP;
        this.RX_OVR = RX_OVR;
        this.TX_OK = TX_OK;
        this.TX_ERR = TX_ERR;
        this.TX_DRP = TX_DRP;
        this.TX_OVR = TX_OVR;
        this.Flg = Flg;
    }

    public String getID() {
        return ID;
    }

    


   
    
    







    public String getTimestamp() {
        return Timestamp;
    }

    public String getIface() {
        return Iface;
    }

    public String getMTU() {
        return MTU;
    }

    public String getRX_OK() {
        return RX_OK;
    }

    public String getRX_ERR() {
        return RX_ERR;
    }

    public String getRX_DRP() {
        return RX_DRP;
    }

    public String getRX_OVR() {
        return RX_OVR;
    }

    public String getTX_OK() {
        return TX_OK;
    }

    public String getTX_ERR() {
        return TX_ERR;
    }

    public String getTX_DRP() {
        return TX_DRP;
    }

    public String getTX_OVR() {
        return TX_OVR;
    }

    public String getFlg() {
        return Flg;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

   

    
    
    public void setTimestamp(String Timestamp) {
        this.Timestamp = Timestamp;
    }

    public void setIface(String Iface) {
        this.Iface = Iface;
    }

    public void setMTU(String MTU) {
        this.MTU = MTU;
    }

    public void setRX_OK(String RX_OK) {
        this.RX_OK = RX_OK;
    }

    public void setRX_ERR(String RX_ERR) {
        this.RX_ERR = RX_ERR;
    }

    public void setRX_DRP(String RX_DRP) {
        this.RX_DRP = RX_DRP;
    }

    public void setRX_OVR(String RX_OVR) {
        this.RX_OVR = RX_OVR;
    }

    public void setTX_OK(String TX_OK) {
        this.TX_OK = TX_OK;
    }

    public void setTX_ERR(String TX_ERR) {
        this.TX_ERR = TX_ERR;
    }

    public void setTX_DRP(String TX_DRP) {
        this.TX_DRP = TX_DRP;
    }

    public void setTX_OVR(String TX_OVR) {
        this.TX_OVR = TX_OVR;
    }

    public void setFlg(String Flg) {
        this.Flg = Flg;
    }

    public String getMet() {
        return Met;
    }

    public void setMet(String Met) {
        this.Met = Met;
    }



    
    
}
