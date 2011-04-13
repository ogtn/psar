package psar;

/**
 * Interface représentant un noeud de l'application chargé de stocker une partie
 * des données de la DHT.
 */
public interface INode {
	/**
	 * Retorune l'identifiant du noeud.
	 */
	long getId();

	Range getRange();

	/*
	 * Range getRange();
	 * 
	 * INetwork getNetwork();
	 * 
	 * int getNext();
	 */
}
