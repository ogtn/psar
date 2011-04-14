package dht.network.tcp;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import dht.UInt;
import dht.Node;

public class Main {

	private static class NetworkId {
		private UInt id;
		private InetSocketAddress addr;

		private NetworkId(UInt id, InetSocketAddress addr) {
			this.id = id;
			this.addr = addr;
		}
	}

	private static final int NB_NODES = 90;

	private static Node createNode(InetSocketAddress nodeAddr, UInt id) {
		NetworkTCP net = new NetworkTCP(new Couple(id, nodeAddr));
		return new Node(net, id);
	}

	private static Node createNode(InetSocketAddress nodeAddr, UInt id,
			UInt connectId, InetSocketAddress connectAddr) {

		NetworkTCP net = new NetworkTCP(new Couple(id, nodeAddr), new Couple(
				connectId, connectAddr));
		return new Node(net, id, connectId);
	}

	static void init(List<NetworkId> ids, Map<UInt, NetworkId> connectedNodes,
			List<Thread> threads, List<Node> nodes) {

		for (NetworkId netId : ids) {
			NetworkId connectId = connectedNodes.get(netId.id);
			Node node = null;

			if (connectId != null) {
				node = createNode(netId.addr, netId.id, connectId.id,
						connectId.addr);
			} else {
				node = createNode(netId.addr, netId.id);
			}
			nodes.add(node);
			threads.add(new Thread(node));
		}
	}

	private static void contigue(final int n) {

		List<NetworkId> ids = new LinkedList<NetworkId>();
		Map<UInt, NetworkId> connectedNodes = new HashMap<UInt, NetworkId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();

		for (int cpt = 0; cpt < n; cpt++)
			ids.add(new NetworkId(new UInt(cpt), new InetSocketAddress(
					1515 + cpt)));

		for (int cpt = 0; cpt < n; cpt++)
			if (cpt != 0)
				connectedNodes.put(new UInt((long) cpt), ids.get(0));
			else
				connectedNodes.put(new UInt((long) cpt), null);

		init(ids, connectedNodes, threads, nodes);

		for (Thread t : threads) {
			t.start();

			try {
				t.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// TODO syncronizerdd
		nodes.get(0).ping();

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void random(final int n) {

		List<NetworkId> ids = new LinkedList<NetworkId>();
		Map<UInt, NetworkId> connectedNodes = new HashMap<UInt, NetworkId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();

		for (int cpt = 0; cpt < n; cpt++)
			ids.add(new NetworkId(new UInt((long) (Math.random() * 1000) /*
																		 * Range.
																		 * MAX_KEY
																		 */),
					new InetSocketAddress(1515 + cpt)));

		for (int cpt = 0; cpt < n; cpt++)
			if (cpt != 0)
				connectedNodes.put(ids.get(cpt).id, ids.get(0));
			else
				connectedNodes.put(ids.get(cpt).id, null);

		init(ids, connectedNodes, threads, nodes);

		for (Thread t : threads) {
			t.start();

			try {
				t.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// TODO syncronizerdd
		nodes.get(0).ping();

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void cokeAndPut(final int n) {

		Map<UInt, Object> data = new HashMap<UInt, Object>();
		Random generator = new Random();
		List<NetworkId> ids = new LinkedList<NetworkId>();
		Map<UInt, NetworkId> connectedNodes = new HashMap<UInt, NetworkId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();

		generator.setSeed(42);

		data.put(new UInt(1000L), "1000");
		data.put(new UInt(2000L), "2000");
		data.put(new UInt(5000L), "5000");
		data.put(new UInt(7000L), "7000");
		data.put(new UInt(9000L), "9000");

		for (int cpt = 0; cpt < n; cpt++)
			ids.add(new NetworkId(new UInt((long) (generator.nextInt(10000))),
					new InetSocketAddress(1515 + cpt)));

		for (NetworkId id : ids)
			System.out.println(id.id);
		System.out.println("");

		for (int cpt = 0; cpt < n; cpt++)
			if (cpt != 0)
				connectedNodes.put(ids.get(cpt).id, ids.get(0));
			else
				connectedNodes.put(ids.get(cpt).id, null);

		init(ids, connectedNodes, threads, nodes);

		int cpt = 0;
		for (Thread t : threads) {
			t.start();

			if (cpt == 0) {
				for (Entry<UInt, Object> entry : data.entrySet()) {
					nodes.get(0).put(entry.getValue(), entry.getKey());
				}
			}
			cpt++;

			try {
				t.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// TODO syncronizerdd
		nodes.get(0).ping();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		data.clear();

		data.put(new UInt(408000L), "408000");
		data.put(new UInt(20000L), "20000");
		data.put(new UInt(10000L), "10000");
		data.put(new UInt(8000L), "8000");
		data.put(new UInt(4000L), "4000");

		for (Entry<UInt, Object> entry : data.entrySet()) {
			nodes.get(0).put(entry.getValue(), entry.getKey());
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// TODO syncronizerdd
		System.out.println("=========================");
		nodes.get(0).ping();

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void getMeIMFamous(final int n) {

		Map<UInt, Object> data = new HashMap<UInt, Object>();
		Random generator = new Random();
		List<NetworkId> ids = new LinkedList<NetworkId>();
		Map<UInt, NetworkId> connectedNodes = new HashMap<UInt, NetworkId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();

		generator.setSeed(42);

		data.put(new UInt(1000L), "1000");
		data.put(new UInt(2000L), "2000");
		data.put(new UInt(5000L), "5000");
		data.put(new UInt(7000L), "7000");
		data.put(new UInt(9000L), "9000");

		for (int cpt = 0; cpt < n; cpt++)
			ids.add(new NetworkId(new UInt((long) (generator.nextInt(10000))),
					new InetSocketAddress(1515 + cpt)));

		for (NetworkId id : ids)
			System.out.println(id.id);
		System.out.println("");

		for (int cpt = 0; cpt < n; cpt++)
			if (cpt != 0)
				connectedNodes.put(ids.get(cpt).id, ids.get(0));
			else
				connectedNodes.put(ids.get(cpt).id, null);

		init(ids, connectedNodes, threads, nodes);

		int cpt = 0;
		for (Thread t : threads) {
			t.start();

			if (cpt == 0) {
				for (Entry<UInt, Object> entry : data.entrySet()) {
					nodes.get(0).put(entry.getValue(), entry.getKey());
				}
			}
			cpt++;

			try {
				t.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// TODO syncronizerdd
		nodes.get(0).ping();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// /////////////////////////////////////////////////////////////////

		nodes.get(1).get(new UInt(2000));
		nodes.get(0).get(new UInt(5000));
		nodes.get(0).get(new UInt(7000));
		nodes.get(0).get(new UInt(9000));
		nodes.get(0).get(new UInt(1000));

		nodes.get(0).get(new UInt(42));

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void leaveMyAss(int n) {

		Random generator = new Random();
		List<NetworkId> ids = new LinkedList<NetworkId>();
		Map<UInt, NetworkId> connectedNodes = new HashMap<UInt, NetworkId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();

		generator.setSeed(42);

		for (int cpt = 0; cpt < n; cpt++)
			ids.add(new NetworkId(new UInt((long) (generator.nextInt(10000))),
					new InetSocketAddress(1515 + cpt)));

		for (NetworkId id : ids)
			System.out.println(id.id);
		System.out.println("");

		for (int cpt = 0; cpt < n; cpt++)
			if (cpt != 0)
				connectedNodes.put(ids.get(cpt).id, ids.get(0));
			else
				connectedNodes.put(ids.get(cpt).id, null);

		init(ids, connectedNodes, threads, nodes);

		for (Thread t : threads) {
			t.start();

			try {
				t.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// TODO syncronizerdd
		nodes.get(0).ping();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// /////////////////////////////////////////////////////////////////

		nodes.get(0).leave();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// TODO syncronizerdd
		nodes.get(1).ping();

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {

		// contigue(10);
		// random(10);
		//cokeAndPut(10);
		//getMeIMFamous(10);
		leaveMyAss(5);

		/*
		 * InetSocketAddress nodeAddr[] = new InetSocketAddress[NB_NODES + 1];
		 * INetwork network[] = new NetworkTCP[NB_NODES + 1]; Node nodeTab[] =
		 * new Node[NB_NODES + 1]; Thread threadTab[] = new Thread[NB_NODES +
		 * 1];
		 * 
		 * nodeAddr[0] = new InetSocketAddress(1515); network[0] = new
		 * NetworkTCP(new Couple(0, nodeAddr[0])); nodeTab[0] = new
		 * Node(network[0], 0);
		 * 
		 * for (int i = 1; i < NB_NODES + 1; i++) { nodeAddr[i] = new
		 * InetSocketAddress(1789 + i); network[i] = new NetworkTCP(new
		 * Couple(i, nodeAddr[i]), new Couple( i - 1, nodeAddr[i - 1]));
		 * nodeTab[i] = new Node(network[i], i, i - 1); }
		 * 
		 * for (int i = 0; i < NB_NODES + 1; i++) { threadTab[i] = new
		 * Thread(nodeTab[i]); threadTab[i].start(); Thread.sleep(1000); }
		 * 
		 * Thread.sleep(1000);
		 * 
		 * for (int i = 0; i < NB_NODES; i++) {
		 * 
		 * }
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * System.out.println("PINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPING")
		 * ;
		 * System.out.println("PINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPING"
		 * );
		 * System.out.println("PINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPING"
		 * );
		 * System.out.println("PINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPING"
		 * );
		 * System.out.println("PINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPING"
		 * );
		 * System.out.println("PINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPINGPING"
		 * );
		 * 
		 * // TODO syncronizerdd nodeTab[0].ping();
		 * 
		 * for (int i = 0; i < NB_NODES; i++) { threadTab[i].join(); }
		 */
	}
}