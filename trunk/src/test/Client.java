package test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client extends Thread {

	@Override
	public void run() {
		try {
			SocketAddress address = Server.address;
			
			System.out.println("address " + address );
			
			SocketChannel socketChannel = SocketChannel.open(address);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject("Hello");
			oos.flush();
			
			// Conversion du flux de byte en tableau puis en buffer de byte
			ByteBuffer bufferObject = ByteBuffer.wrap(bos.toByteArray());
			
			System.out.println("======= " + bufferObject.capacity());
			
			//socketChannel.write(bufferObject);
			
			//socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
