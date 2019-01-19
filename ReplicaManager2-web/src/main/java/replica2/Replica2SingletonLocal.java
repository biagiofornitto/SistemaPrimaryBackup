/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package replica2;

import org.json.JSONArray;
import utilities.LogEntryNumRequest;

/**
 *
 * @author biagio
 */
public interface Replica2SingletonLocal {
    String getName();
    void writeOnDB(LogEntryNumRequest log);
    void addLocal(String log);
    void receiveCommit(String idRequest);
    JSONArray readFromDB(String query);
    void sendAck(String idRequest,int numReplica);
    void receiveAbort(String idRequest);
}
