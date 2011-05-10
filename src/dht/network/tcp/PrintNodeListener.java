package dht.network.tcp;

import dht.ANodeState;
import dht.INode;
import dht.INodeListener;

/**
 * Listener d'affichage sur la sortie standard des évènements du noeud. 
 */
public class PrintNodeListener implements INodeListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changeState(INode node, ANodeState oldState, ANodeState newState) {
		System.out.println("Le noeud \"" + node.getId().getNumericID()
				+ "\" passe de l'état " + oldState.getClass().getSimpleName()
				+ "\" à \"" + newState.getClass().getSimpleName() + "\"");
	}
}