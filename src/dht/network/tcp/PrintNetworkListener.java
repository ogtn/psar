package dht.network.tcp;

import dht.ANodeId;
import dht.INetworkListener;
import dht.INode;
import dht.message.AMessage;
import dht.message.MessagePing;

/**
 * Listener d'affichage sur la sortie standard des évènements réseaux.
 */
public class PrintNetworkListener implements INetworkListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendMessage(AMessage message, INode node, ANodeId id) {
		/*System.out.println("Le noeud : \"" + node.getId().getNumericID()
				+ "\" envoie un message \""
				+ message.getClass().getSimpleName() + "\" a \"" + id + "\"");*/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recvMessage(AMessage message, INode node) {

		if (message instanceof MessagePing) {
			System.out.println("Le noeud : \"" + node.getId()
					+ "\" reçoit un message \""
					+ message.getClass().getSimpleName() + "\" de \""
					+ message.getSource().getNumericID() + "\" envoyé par \""
					+ message.getOriginalSource().getNumericID() + "\""
					+ " shortcut: " + node.getNextShortcut());

		} else {
			/*System.out.println("Le noeud : \"" + node.getId().getNumericID()
					+ "\" reçoit un message \""
					+ message.getClass().getSimpleName() + "\" de \""
					+ message.getSource().getNumericID() + "\" envoyé par \""
					+ message.getOriginalSource().getNumericID() + "\"");*/
		}
	}
}
