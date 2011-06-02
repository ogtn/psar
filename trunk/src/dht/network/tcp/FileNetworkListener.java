package dht.network.tcp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import dht.ANodeId;
import dht.INetworkListener;
import dht.INode;
import dht.message.AMessage;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessageReturnGet;

/**
 * Listener d'affichage dans un fichier des évènements réseaux.
 */
public class FileNetworkListener implements INetworkListener {

	private final String path;
	private PrintWriter pw = null;

	/**
	 * Crée et initialise un écouteur enregistrant dans un fichier placé dans le
	 * répertoire /tmp/.
	 */
	public FileNetworkListener() {
		path = "/tmp//";
	}

	/**
	 * Crée et initialise un écouteur enregistrant dans un fichier.
	 * 
	 * @param path
	 */
	public FileNetworkListener(String path) {
		this.path = path + "/";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void eventRecvMessage(AMessage message, INode node) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void eventSendMessage(AMessage message, INode node, ANodeId id,
			boolean isInChannel) {

		try {
			String msgStr = message.getClass().getSimpleName();

			if (pw == null) {
				File file = new File(path + node.getId().getNumericID()
						+ ".txt");
				if (file.exists())
					file.delete();
				file.createNewFile();
				pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			}

			pw.write(msgStr.substring("Message".length(), msgStr.length()));

			pw.write(" de " + message.getSource().getNumericID() + " par "
					+ message.getOriginalSource().getNumericID());

			if (message instanceof MessagePing) {
				pw.write("\n" + node + "\n");
			} else if (message instanceof MessageGet) {
				MessageGet msg = (MessageGet) message;
				pw.write(message.getOriginalSource().getNumericID()
						+ " demande la donnée de clé " + msg.getKey());
			} else if (message instanceof MessageReturnGet) {
				MessageReturnGet msg = (MessageReturnGet) message;
				pw.write("Reception de la donnée " + msg.getData());
			} else
				pw.write(msgStr);

			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
