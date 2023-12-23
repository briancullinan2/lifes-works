/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CS565.NTP;

import java.math.*;
import java.net.*;
/**
 *
 * @author Brian Cullinan
 */
public class NTPServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // for seperate thread
        try {
            DatagramSocket server = new DatagramSocket(12345);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket recievePacket = new DatagramPacket(buffer, buffer.length);
                server.receive(recievePacket);

                System.out.println("Connection recieved from " + recievePacket.getAddress().getHostAddress());

//int delay = (int)(Math.random()*1000);
//System.out.println("Adding delay: " + delay + " milliseconds");
//Thread.sleep(delay);

                byte[] returnBytes = Long.valueOf(System.currentTimeMillis()).toString().getBytes();
                server.send(new DatagramPacket(returnBytes, returnBytes.length, recievePacket.getAddress(), 12345));

                System.out.println("Data sent");

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    

}
