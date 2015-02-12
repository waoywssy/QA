/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author Administrator
 */
public class ScheduledScript {
    public int id;
    public int botId;
    public int queryId;
    public String name;
    public String joinColumn;
    public String joinRunsColumn;
    public String script;
    public int convertType;
    public String database;
    public String server;
    
    public ScheduledScript(){
        
    }
    
 public ScheduledScript(int id,int botId,int queryId,String name,String joinColumn,String joinRunsColumn,
          String script,int convertType, String database,String server){
        this.id =id;
        this.botId= botId;
        this.queryId = queryId;
        this.name =name;
        this.joinColumn = joinColumn;
        this.joinRunsColumn = joinRunsColumn;
        this.script = script;
        this.convertType = convertType;
        this.server = server;
        this.database = database;
    }
}
