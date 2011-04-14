package dht;

/**
 * Interface représentant un noeud de l'application chargé de stocker une partie
 * des données de la DHT.
 */
public interface INode {
	/**
	 * Retorune l'identifiant du noeud.
	 */
	UInt getId();

	void ping();
	
	void put(Object data, UInt key);
	
	void leave();
	
	public void get(UInt key);
}
