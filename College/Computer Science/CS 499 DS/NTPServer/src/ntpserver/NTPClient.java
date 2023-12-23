package CS565.NTP;

import java.io.*;
import java.net.*;
import java.util.*;

public class NTPClient {
	public static void main(String[] args) throws Exception{

		if(args.length < 2) {
			System.out.println("Proper usage: NTPClient IP port");
			System.exit(0);
		}
		int port = Integer.parseInt(args[1]);

		byte[] message = " ".getBytes();
		byte[] buffer = new byte[1024];
		InetAddress address = InetAddress.getByName(args[0]);
		System.out.println(address);

		DatagramSocket socket = new DatagramSocket(port);
		DatagramPacket requestPacket = new DatagramPacket(message, message.length, address, port);
		DatagramPacket returnPacket = new DatagramPacket(buffer, buffer.length);
		long times[] = new long[4];


		socket.send(requestPacket);
		System.out.println("Packet Sent");

		socket.receive(returnPacket);
		System.out.println("Packet Received");
		times[2] = System.currentTimeMillis();  //T_i-2

		String serverResponse = new String(returnPacket.getData(), 0, returnPacket.getLength());
		times[3] = Long.parseLong(serverResponse);//T_i-3
		

		times[1] = System.currentTimeMillis();  //T_i-1

		socket.send(requestPacket);                    
		socket.receive(returnPacket);

	  serverResponse = new String(returnPacket.getData(), 0, returnPacket.getLength());
		times[0] = Long.parseLong(serverResponse);    //T_i
		long d = (times[2] - times[3])+(times[0] - times[1]);
		long oi = d/2;
		long o = oi + ((times[0] - times[1]) + (times[2] - times[3])/2);
		System.out.println("T_i-3 = "+times[3]);
		System.out.println("T_i-2 = "+times[2]);
		System.out.println("T_i-1 = "+times[2]);
		System.out.println("T_i   = "+times[0]);
		
		System.out.println("d_i =   "+d);
		System.out.println("o_i =   "+oi);
		System.out.println("o   =   "+o+" +- "+d);

		socket.close();
	}
}
