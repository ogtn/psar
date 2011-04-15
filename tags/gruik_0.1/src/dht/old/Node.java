package dht.old;


public class Node extends Thread {
	/*
	enum Status {
		NONE, CONNECTING, DISCONNECTING, RUN
	};

	private Selector selector;
	private InetSocketAddress me;
	private int id;
	private Socket next;
	private InetSocketAddress nodeAddr;
	private Status status;

	public Node(InetSocketAddress me, int id) {
		this.me = me;
		this.id = id;
		this.nodeAddr = me;
	}

	public Node(InetSocketAddress me, int id, InetSocketAddress nodeAddr) {
		
		Tools.checkNull(nodeAddr);
		
		this.me = me;
		this.id = id;
		this.nodeAddr = nodeAddr;
		status = Status.NONE;
	}

	@Override
	public void run() {

		try {
			// Connection au noeud de l'anneau
			init();
			connect(nodeAddr);

			while (true) {

				SocketChannel sc = select();
				ObjectInputStream ois = null;
				AMessage msg = null;

				ois = new ObjectInputStream(Channels.newInputStream(sc));

				// Message nous disant à qui nous connecter
				try {
					msg = (AMessage) ois.readObject();

					System.out.println("\nJe " + id + " viens de recevoir "
							+ msg);

					if (msg instanceof MessageAskConnection && status == Status.RUN) {
							route((MessageAskConnection) msg);
					} else if (msg instanceof MessageConnect && status == Status.RUN) {
						MessageAskConnection msgConn = (MessageAskConnection) msg;
						sc.register(selector, SelectionKey.OP_READ);
					} else if (msg instanceof MessageConnectTo && status == Status.CONNECTING) {
						// Message nous disant à qui nous connecter
						MessageConnectTo msgCT = (MessageConnectTo) ois
								.readObject();
						// Enregistre mon précédent
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);
						// Je me connecte a mon suivant
						next = new Socket(msgCT.getAddr().getAddress(), msgCT
								.getAddr().getPort());

						ObjectOutputStream oos = new ObjectOutputStream(
								next.getOutputStream());

						oos.writeObject(new MessageConnect(me));
						System.out.println(id + "!!!!!!!!!!!\n" + msg);
						status = Status.RUN;
					}
					else
						System.err.println("Kernel panic! " + status + msg);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					// TODO Envoyer un message d'erreur au connard qui m'a ft
					// chier
				}
			}
		} catch (IOException e) {
			status = Status.DISCONNECTING;
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 *//*
	public SocketChannel select() throws IOException {

		SocketChannel sc = null;

		try {
			selector.select();

			Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

			while (keys.hasNext()) {

				SelectionKey key = keys.next();
				keys.remove();

				// l'évenement correspond à une acceptation de connexion à
				// notre
				// serveur on est dans le cas du ServerChannel
				if (key.isValid() && key.isAcceptable()) {
					ServerSocketChannel server = (ServerSocketChannel) key
							.channel();
					System.out.println("1");
					sc = server.accept();
				}

				// l'évenement correspond à une lecture possible
				// on est donc dans un SocketChannel
				else if (key.isValid() && key.isReadable()) {
					System.out.println("2");
					sc = (SocketChannel) key.channel();
				}
			}

		} catch (IOException e) {
			Tools.close(sc);
		}

		return sc;
	}

	public void connect(InetSocketAddress inetAddr) throws IOException {

		status = Status.CONNECTING;
		
		/*
		 * Connexion au noeud qui routera notre demande
		 *//*
		Socket s = null;
		ObjectOutputStream oos = null;

		try {
			System.out.println("Je " + id
					+ " envoie un message de demande de connection");
			s = new Socket(inetAddr.getAddress(), inetAddr.getPort());
			oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(new MessageAskConnection(me, id));
		} finally {
			Tools.close(s);
			Tools.close(oos);
		}
	}

	private void init() throws IOException {
		/*
		 * Création de notre socket serveur
		 *//*
				 * ServerSocketChannel ssChan = null; selector =
				 * Selector.open(); try { // Socket du serveur ssChan =
				 * ServerSocketChannel.open(); // Non bloquante
				 * ssChan.configureBlocking(false); // Acquisition de l'adresse
				 * socket ssChan.socket().bind(me); // Enregistre notre serveur
				 * ssChan.register(selector, SelectionKey.OP_ACCEPT); } catch
				 * (IOException e) { Tools.close(ssChan); throw e; } }
				 * 
				 * private void route(MessageAskConnection msg) throws
				 * IOException {
				 * 
				 * InetSocketAddress oldNext = new InetSocketAddress(
				 * next.getInetAddress(), next.getPort()); ObjectOutputStream
				 * oos = null;
				 * 
				 * Tools.close(next);
				 * 
				 * try { next = new Socket(msg.getSource().getAddress(),
				 * msg.getSource() .getPort()); oos = new
				 * ObjectOutputStream(next.getOutputStream());
				 * oos.writeObject(new MessageConnectTo(me, oldNext, null));
				 * 
				 * } catch (IOException e) { Tools.close(next); throw e; }
				 * finally { Tools.close(oos); } }
				 * 
				 * @Override public String toString() {
				 * 
				 * StringBuilder strBuild = new StringBuilder();
				 * 
				 * strBuild.append("========" + id + "========\n");
				 * strBuild.append("Status : " + status.toString() + " \n"); if
				 * (next != null) { strBuild.append("next : mon port " +
				 * next.getLocalPort()); strBuild.append(" port next : " +
				 * next.getPort() + "\n"); } else strBuild.append("next : " +
				 * null + "\n");
				 * 
				 * if (selector != null) { Iterator<SelectionKey> keys =
				 * selector.keys().iterator();
				 * 
				 * while (keys.hasNext()) {
				 * 
				 * SelectionKey key = keys.next();
				 * 
				 * if (key.isAcceptable()) { ServerSocketChannel server =
				 * (ServerSocketChannel) key .channel();
				 * 
				 * strBuild.append("Mon serveur : " +
				 * server.socket().getLocalPort() + "\n"); }
				 * 
				 * // l'évenement correspond à une lecture possible // on est
				 * donc dans un SocketChannel else if (key.isValid() &&
				 * key.isReadable()) { SocketChannel sc = (SocketChannel)
				 * key.channel();
				 * 
				 * strBuild.append("previous : mon port" +
				 * sc.socket().getLocalPort() + "\n");
				 * strBuild.append("port previous : " + sc.socket().getPort() +
				 * "\n"); } } } else strBuild.append("selector : null\n");
				 * 
				 * strBuild.append("--------" + id + "--------\n");
				 * 
				 * return strBuild.toString(); }
				 */
}