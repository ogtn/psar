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
	public void sendMessage(AMessage message, INode node, ANodeId id,
			boolean isInChannel) {

			System.out.println(isInChannel + " Le noeud : \""
					+ node.getId().getNumericID() + "\" envoie un message \""
					+ message.getClass().getSimpleName() + "\" a \"" + id.getNumericID()
					+ "\"");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recvMessage(AMessage message, INode node) {
		
		//if (message instanceof MessagePing) {
			System.out.println("noeud : \"" + node.getId().getNumericID()
					+ "\" msg: \""
					+ message.getClass().getSimpleName() + "\" de \""
					+ message.getSource().getNumericID() + "\" envoyé par \""
					+ message.getOriginalSource().getNumericID() + "\""
					+ " shortcut: " + (node.getNextShortcut() != null ? node.getNextShortcut().getNumericID(): null)
					+ " prev : " + (node.getPrev() != null ? node.getPrev() : null)
					+ " next : " + node.getNext().getNumericID()
			) ;

		//}
		/*else if (message instanceof MessageEventDisconnect || message instanceof MessageDisconnect) {
			System.out.println("Le noeud : \"" + node.getId().getNumericID()
					+ "\" reçoit un message \""
					+ message.getClass().getSimpleName() + "\" de \""
					+ message.getSource().getNumericID() + "\" envoyé par \""
					+ message.getOriginalSource().getNumericID() + "\" ETATE \"" + node.getState() + "\"");

		}*/
	}
}
