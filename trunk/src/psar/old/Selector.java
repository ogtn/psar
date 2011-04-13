package psar.old;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// afr comment
/**
 * Classe permettant d'attendre un changements d'état sur un descripteur de
 * SocketServer ou sur un {@link InputStream}.
 */
public class Selector {

    /**
     * Exception lancée pour relayer une erreur issue de la couche réseau.
     */
    public static class SelectorException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Crée et initialise une <code>SelectorException</code>.
	 * 
	 * @param cause
	 *            La cause de l'exception.
	 */
	public SelectorException(Throwable cause) {
	    super(cause);
	}
    }

    private static class Content {
	private Socket socket;
	private Object object;

	public Content(Socket socket) {
	    this.socket = socket;
	    this.object = null;
	}

	public Content(Object object) {
	    this.socket = null;
	    this.object = object;
	}
    }

    // afr type de collection
    private BlockingQueue<Content> queue;
    private Content content;

    /**
     * Crée et initialise un sélecteur permettant d'attendre l'arrivée
     * d'évènements sur un ensemble d'{@link ObjectInputStream} ou de
     * {@link ServerSelector}.
     */
    public Selector() {
	queue = new LinkedBlockingQueue<Content>();
	content = null;
    }

    /**
     * Crée et initialise un sélecteur permettant d'attendre l'arrivée
     * d'évènements sur un ensemble d'{@link ObjectInputStream} ou de
     * {@link ServerSelector}.
     * 
     * @param capacity
     * 
     */
    // afr capcity
    public Selector(int capacity) {
	queue = new ArrayBlockingQueue<Content>(capacity, true);
	content = null;
    }

    // afr id
    public void add(final ObjectInputStream ois, final int id) {

	new Thread() {
	    @Override
	    public void run() {
		while (true) {
		    try {
			/*
			 * while(ois.available() == 0) {
			 * 
			 * }
			 */

			System.out.println("Le noeud [id: " + id
				+ "] ajoute un stream.");
			Object object = ois.readObject();
			System.out.println("Le stream du noeud [id: " + id
				+ "] vient de lire un objet.");
			queue.add(new Content(object));
		    } catch (IOException e) {
			System.out.println("IOException en attente ID[" + id
				+ "]ID");

			// afr throws
			e.printStackTrace();

			throw new RuntimeException(e);

		    } catch (ClassNotFoundException e) {
			// afr throws
			e.printStackTrace();
		    }
		}
	    }
	}.start();
    }

    public void add(final ServerSocket serverSocket) {
	new Thread() {
	    @Override
	    public void run() {
		while (true) {
		    try {
			Socket s = serverSocket.accept();
			queue.add(new Content(s));
		    } catch (IOException e) {
			// afr throws
			e.printStackTrace();
		    }
		}
	    }
	}.start();
    }

    public void select() {
	try {
	    if (content == null)
		content = queue.take();
	} catch (InterruptedException e) {
	    // afr throws
	    throw new IllegalStateException(e);
	}
    }

    public boolean isReadObject() {
	if (content == null)
	    // afr
	    throw new IllegalStateException("");

	return content.object != null;
    }

    public <T> T getObject() {

	if (content == null || content.object == null)
	    // afr
	    throw new IllegalStateException("");

	// afr cast
	return (T) content.object;
    }

    public Socket getSocket() {

	if (content == null || content.socket == null)
	    // afr
	    throw new IllegalStateException("");

	// afr cast
	return content.socket;
    }
}