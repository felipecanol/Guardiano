/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package connection;
import java.util.ArrayList;

/**
 *
 * @author felipe
 */
public class TableBean {

    private String schema;
    private String table;
    private ArrayList<String> triggers = new ArrayList<>();
    
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
    
    public ArrayList<String> getTriggers() {
        return triggers;
    }

    public void setTriggers(ArrayList<String> triggers) {
        this.triggers = triggers;
    }

    public void addTrigger(String trigger) {
        this.triggers.add(trigger);
    }
}
