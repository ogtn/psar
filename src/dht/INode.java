package dht;

import java.io.Serializable;

/**
 * Interface représentant un noeud de l'application chargé de stocker une partie
 * des données de la DHT.
 */
public interface INode {
	/**
	 * Retourne l'identifiant du noeud.
	 */
	ANodeId getId();

	ANodeId getNextShortcut();
	
	void ping();
	
	void put(UInt key, Serializable data);
	
	void leave();
	
	UInt getNextRange();
	
	Object get(UInt key);
	
	void errorNext();
	
	Range getRange();

	// TODO virer cette meth inutiles
	ANodeState getState();
	ANodeId getPrev();
	ANodeId getNext();
	
	void addINodeListener(INodeListener listener);
	
	void removeINodeListener(INodeListener listener);
}
