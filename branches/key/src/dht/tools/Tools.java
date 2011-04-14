package dht.tools;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Tools {

	public static void close(Closeable c) {
		
		if(c != null)
		{
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void close(Socket s) {
		if(s != null)
		{
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(ServerSocket s) {
		if(s != null)
		{
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
