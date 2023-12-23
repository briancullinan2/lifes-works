/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CS565.P2PChat;

import java.io.*;

/**
 *
 * @author Brian Cullinan
 */
public class LeaveMessage extends CmdMessage implements Serializable {

    private String reconnect_ip, leave_ip;

    public LeaveMessage(String reconnect_ip, String leave_ip) {
            this.reconnect_ip = reconnect_ip;
            this.leave_ip = leave_ip;
    }

    public boolean doForward() { return false; }

    public synchronized void update(P2PCore core)
    {
        // reconnect to new ip
        try {
            if(reconnect_ip.equals(""))
            {
                System.out.println("Became parent node!");
                //Thread.sleep(500);
                core.connect("");
            }
            else
            {
System.out.println("Trying to reconnect to " + reconnect_ip);
                Thread.sleep(500);

                core.connect(reconnect_ip.split(":")[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
