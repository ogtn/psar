package dht.tools;

import java.io.Closeable;
import java.io.IOException;

/**
 * Classe utilitaire.
 */
public class Tools {

	/**
	 * Ferme une ressource Closeable sans lancer d'exceptions.
	 * 
	 * @param c
	 *            La ressource à fermer.
	 */
	public static void close(Closeable c) {

		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
