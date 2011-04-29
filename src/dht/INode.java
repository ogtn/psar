package dht;

/**
 * Interface représentant un noeud de l'application chargé de stocker une partie
 * des données de la DHT.
 */
public interface INode {
	/**
	 * Retorune l'identifiant du noeud.
	 */
	ANodeId getId();

	void ping();
	
	void put(UInt key, Object data);
	
	void leave();
	
	void get(UInt key);

	String getState();
}
