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
				connectedNodes.put(new UInt(cpt), ids.get(0));
			else
				connectedNodes.put(new UInt(cpt), null);

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
			
			try {
				t.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (cpt == 0) {
				for (Entry<UInt, Object> entry : data.entrySet()) {
					nodes.get(0).put(entry.getValue(), entry.getKey());
				}
			}
			cpt++;
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
			ids.add(new NetworkId(new UInt(generator.nextInt(10000)),
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
		Map<UInt, Object> data = new HashMap<UInt, Object>();

		data.put(new UInt(1000L), "1000");
		data.put(new UInt(2000L), "2000");
		data.put(new UInt(5000L), "5000");
		data.put(new UInt(7000L), "7000");
		data.put(new UInt(9000L), "9000");
		
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

		System.out.println("===== " + nodes.get(1).getId() + " se déco : ");
		
		nodes.get(1).leave();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
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

	public static void main(String[] args) throws InterruptedException {
		contigue(2);
		//random(10);
		//cokeAndPut(10);
		//getMeIMFamous(10);
		//leaveMyAss(5);
	}
}
