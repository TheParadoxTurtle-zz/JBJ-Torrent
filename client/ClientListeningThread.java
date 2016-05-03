package client;

import java.io.*;
import java.net.*;
import java.util.*;

import debug.Debug;

//thread to handle connections to client 
public class ClientListeningThread implements Runnable {

	private static String enc = "US-ASCII";

	private ServerSocket welcomeSocket = null;

	public ClientListeningThread(int port) {
		try {
			welcomeSocket = new ServerSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//simple sequential server behavior
	public void run() {
		while(true) {
			try {
				Socket connSocket = welcomeSocket.accept();
				//handles peer to peer functions
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream(), enc));
	            String line = inFromClient.readLine();
	            Debug.print(line);
		        while (!(line.equals(""))) {
	                StringTokenizer st = new StringTokenizer(line);
	                String command = st.nextToken();
	                String arg = st.nextToken();
	                int port = Integer.parseInt(st.nextToken());
		        	
		        	//actually handle commands
		        	if(command.equals("")) {

		        	}

			        line = inFromClient.readLine();
		        }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}