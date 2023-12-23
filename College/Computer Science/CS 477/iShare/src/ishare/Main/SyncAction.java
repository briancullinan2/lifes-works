/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Main;

/**
 *
 * @author Brian Cullinan
 */

public class SyncAction
{
    
    public enum Type {ADD, EDIT, REMOVE, IGNORE}
    
    Type type;
    Object obj;
    long timestamp;

    public SyncAction(Type type, Object obj, long timestamp)
    {

    }
    
    public long getTime()
    {
        return timestamp;
    }
}
