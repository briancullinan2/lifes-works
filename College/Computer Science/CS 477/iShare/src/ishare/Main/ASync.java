/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Main;

import java.util.*;
/**
 *
 * @author Brian Cullinan
 */
public class ASync {
    
    public SyncActionCollection log = new SyncActionCollection();

    
    public ASync()
    {
        // exchange a list of current timestamps
        //  filter out actions that already exist
        //  send and recieve actions that aren't synced yet
        
        // create a listener thread to recieve changes async
        
        // create a updater thread to send changes to all peers
    }
    
    // resolve function for resolving conflicts like overwriting little details
    //  Add resolves conflicts where something is removed add added later
    //   and then syncing
    public boolean Resolve(SyncAction action)
    {
        if(action.obj instanceof Person)
        {
            // things that matter to a person are Icon, and Name

            // loop through current log and resolve any conflicts
            for( SyncAction logAction : log.findByType(action) )
            {
                Person person = (Person)logAction.obj;
                
                // check if it matches any previous actions remove the previous actions
                if(person.getName().equals(action))
                {
                    
                }
                // if it conficts with a later action return false

                // if there is a little conflict like Icon used, used the newest icon and return true
            }

        }
        
        
        // if it isn't conflicting add it to our data structure
        return false;
    }
    
    // Sync function creates a thread and socket to async with
    
    //
    
    public void addSyncAction(SyncAction.Type type, Object obj)
    {
        Date timestamp = new Date();
        log.addAction(new SyncAction(type, obj, timestamp.getTime()));
    }
    
}
