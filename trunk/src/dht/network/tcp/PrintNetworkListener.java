package dht.network.tcp;

import com.sun.xml.internal.ws.api.message.Message;

import dht.ANodeId;
import dht.INetworkListener;
import dht.INode;
import dht.message.AMessage;
import dht.message.MessageGet;
import dht.message.MessagePing;
import dht.message.MessageReturnGet;

/**
 * Listener d'affichage sur la sortie standard des évènements réseaux.
 */
public class PrintNetworkListener implements INetworkListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendMessage(AMessage message, INode node, ANodeId id, boolean isInChannel) {

		/*System.out.println(isInChannel + " Le noeud : \"" + node.getId().getNumericID() + "\" envoie un message \""
				+ message.getClass().getSimpleName() + "\" a \"" + id.getNumericID() + "\"");*/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recvMessage(AMessage message, INode node) {

		String msgStr = message.getClass().getSimpleName();
		msgStr = msgStr.substring("Message".length(), msgStr.length());
		msgStr += " de " + message.getSource().getNumericID() + " par " + message.getOriginalSource().getNumericID(); 
		
		if (message instanceof MessagePing) {
			System.out.println("\n" + node + "\n");
		}
		else if(message instanceof MessageGet)
		{
			MessageGet msg = (MessageGet) message;
			System.out.println(message.getOriginalSource().getNumericID() + " demande la donnée de clé " + msg.getKey());
		}
		else if(message instanceof MessageReturnGet){
			MessageReturnGet msg = (MessageReturnGet) message;
			System.out.println("Reception de la donnée " + msg.getData());
		}
		else
			System.out.println(msgStr);
		
		// }
		/*
		 * else if (message instanceof MessageEventDisconnect || message
		 * instanceof MessageDisconnect) { System.out.println("Le noeud : \"" +
		 * node.getId().getNumericID() + "\" reçoit un message \"" +
		 * message.getClass().getSimpleName() + "\" de \"" +
		 * message.getSource().getNumericID() + "\" envoyé par \"" +
		 * message.getOriginalSource().getNumericID() + "\" ETATE \"" +
		 * node.getState() + "\"");
		 * 
		 * }
		 */
	}
}
