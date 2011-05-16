package dht.tools;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Selector;

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

	public static void close(Selector selector) {
		try {
			if (selector != null)
				selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
