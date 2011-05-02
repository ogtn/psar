package test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server extends Thread {

	public static final InetSocketAddress address;

	static {

		InetSocketAddress tmpAddr = null;

		try {
			tmpAddr = new InetSocketAddress(InetAddress.getLocalHost(), 3000);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		address = tmpAddr;
	}

	@Override
	public void run() {

		try {
			// création d'un selector
			Selector selector = Selector.open();

			ServerSocketChannel serverChannel = ServerSocketChannel.open();

			serverChannel.configureBlocking(false);

			// acquisition de l'adresse socket
			serverChannel.socket().bind(address);

			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

			System.out.println("Avant selc");

			while (selector.select() > 0) {
				
				System.out.println("apres selc");
				
				Iterator<SelectionKey> keys = selector.selectedKeys()
						.iterator();
				
				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					keys.remove();

					// l'évenement correspond à une acceptation de connexion
					// on est dans le cas du ServerChannel
					if (key.isValid() && key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key
								.channel();
						SocketChannel client = server.accept();
						client.configureBlocking(false);

						System.out.println("Connection " + selector.keys().size());

						// on ajoute le client à l'ensemble
						client.register(selector, SelectionKey.OP_READ
								| SelectionKey.OP_WRITE);
					}

					// l'évenement correspond à une lecture possible
					// on est donc dans un SocketChannel
					if (key.isValid() && key.isReadable()) {

						SocketChannel client = (SocketChannel) key.channel();
						
						try {
							ByteBuffer buffer = ByteBuffer.allocate(12);

							int bitsLus = 0;
							
							do {
								int nbRead = client.read(buffer);

								// Si la connexion a été coupée
								/*if (nbRead == -1)
									throw new EOFException(bitsLus + "");*/

								bitsLus += nbRead;

							} while (bitsLus != 12);

							buffer.flip();

							// Récupération dans un tableau de byte
							byte[] data = new byte[buffer.capacity()];
							buffer.get(data, 0, data.length);

							// Lecture de l'objet depuis le tableau de byte
							ByteArrayInputStream bis = new ByteArrayInputStream(data);
							ObjectInputStream ois = new ObjectInputStream(bis);

							System.out.println(ois.readObject());
							
							//c'est la fin de la connexion, on retire la clef
							// du selector
							client.close();
							System.out.println("Fin de connexion " + selector.keys().size());
							key.cancel();

						} catch (Exception e) {
							// une erreur s'est produit, on retire
							// du selector
							e.printStackTrace();
							key.cancel();
							client.close();
						}
					}

					if (key.isValid() && key.isWritable()) {
						// écrire éventuellement
					}
				}
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
