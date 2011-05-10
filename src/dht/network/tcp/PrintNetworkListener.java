package dht.network.tcp;

import dht.INetworkListener;
import dht.INode;
import dht.message.AMessage;

/**
 * Listener d'affichage sur la sortie standard des évènements réseaux.
 */
public class PrintNetworkListener implements INetworkListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendMessage(AMessage message, INode node) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recvMessage(AMessage message, INode node) {
		System.out.println("Le noeud : \"" + node.getId().getNumericID()
				+ "\" reçoit un message \""
				+ message.getClass().getSimpleName() + "\" de \""
				+ message.getSource().getNumericID() + "\" envoyé par \""
				+ message.getOriginalSource().getNumericID() + "\"");
	}
}
