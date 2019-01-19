/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package replica1;

import java.util.ArrayList;
import org.json.JSONArray;
import utilities.AckClass;
import utilities.LogEntryNumRequest;

/**
 *
 * @author biagio
 */
public interface Replica1SingletonLocal {
    String getName();
    void writeOnDB(LogEntryNumRequest log);
    void addLocal(String log);
    void receiveAck(AckClass ack);
    JSONArray readFromDB(String query);
    void sendCommit(String idRequest);
    void sendAbort(String idRequest);
}
