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

class Main {

	static void init(List<TCPId> ids, Map<UInt, TCPId> connectedNodes,
			List<Thread> threads, List<Node> nodes) {

		for (TCPId netId : ids) {
			TCPId connectId = connectedNodes.get(netId.getNumericID());
			Node node = null;

			if (connectId != null) {
				node = new Node(new NetworkTCP(), netId, connectId);
			} else {
				node = new Node(new NetworkTCP(), netId);
			}
			nodes.add(node);
			threads.add(new Thread(node));
		}
	}

	private static void contigue(final int n) {

		List<TCPId> ids = new LinkedList<TCPId>();
		Map<UInt, TCPId> connectedNodes = new HashMap<UInt, TCPId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();

		for (int cpt = 0; cpt < n; cpt++)
			ids
					.add(new TCPId(new UInt(cpt), new InetSocketAddress(
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

		List<TCPId> ids = new LinkedList<TCPId>();
		Map<UInt, TCPId> connectedNodes = new HashMap<UInt, TCPId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();

		for (int cpt = 0; cpt < n; cpt++)
			ids.add(new TCPId(new UInt((long) (Math.random() * 1000) /*
																	 * Range.
																	 * MAX_KEY
																	 */),
					new InetSocketAddress(1515 + cpt)));

		for (int cpt = 0; cpt < n; cpt++)
			if (cpt != 0)
				connectedNodes.put(ids.get(cpt).getNumericID(), ids.get(0));
			else
				connectedNodes.put(ids.get(cpt).getNumericID(), null);

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

	private static void coranAlternatif() {
		Map<UInt, TCPId> connectedNodes = new HashMap<UInt, TCPId>();
		List<TCPId> ids = new LinkedList<TCPId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();
		ids.add(new TCPId(new UInt(0L), new InetSocketAddress(1732)));
		ids.add(new TCPId(new UInt(4000L), new InetSocketAddress(1515)));
		ids.add(new TCPId(new UInt(8000L), new InetSocketAddress(1789)));

		for (int cpt = 0; cpt < ids.size(); cpt++)
			if (cpt != 0)
				connectedNodes.put(ids.get(cpt).getNumericID(), ids.get(0));
			else
				connectedNodes.put(ids.get(cpt).getNumericID(), null);

		init(ids, connectedNodes, threads, nodes);

		for (Thread t : threads) {

			t.start();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		StringBuilder strBuild = new StringBuilder();

		for (int cpt = 0; cpt < (512 * 1024); cpt++) {
			strBuild.append("K");
		}

		String hh = strBuild.toString();

		for (int cpt = 2000; cpt < 2025; cpt++) {
			nodes.get(0).put(new UInt(cpt), hh);
		}

		nodes.get(0).ping();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		nodes.get(1).put(new UInt(4000), "A");

		Node node = new Node(new NetworkTCP(), new TCPId(new UInt(2000L),
				new InetSocketAddress(1941)), new TCPId(new UInt(0L),
				new InetSocketAddress(1732)));

		new Thread(node).start();

		/*
		 * try { Thread.sleep(1000); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 */
		System.out.println("=====");
		nodes.get(0).ping();

		int i = 30;
		while (i-- > 0) {
			// nodes.get(2).get(new UInt(4444));
		}
		/*
		 * try { threads.get(0).join(); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */

	}

	private static void cokeAndPut(final int n) {

		Map<UInt, Object> data = new HashMap<UInt, Object>();
		Random generator = new Random();
		List<TCPId> ids = new LinkedList<TCPId>();
		Map<UInt, TCPId> connectedNodes = new HashMap<UInt, TCPId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();

		generator.setSeed(42);

		data.put(new UInt(1000L), "1000");
		data.put(new UInt(2000L), "2000");
		data.put(new UInt(5000L), "5000");
		data.put(new UInt(7000L), "7000");
		data.put(new UInt(9000L), "9000");

		for (int cpt = 0; cpt < n; cpt++)
			ids.add(new TCPId(new UInt((long) (generator.nextInt(10000))),
					new InetSocketAddress(1515 + cpt)));

		for (TCPId id : ids)
			System.out.println(id.getNumericID());
		System.out.println("");

		for (int cpt = 0; cpt < n; cpt++)
			if (cpt != 0)
				connectedNodes.put(ids.get(cpt).getNumericID(), ids.get(0));
			else
				connectedNodes.put(ids.get(cpt).getNumericID(), null);

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
					nodes.get(0).put(entry.getKey(), entry.getValue());
				}
			}
			cpt++;
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("----- PING -----");
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
			nodes.get(0).put(entry.getKey(), entry.getValue());
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
		List<TCPId> ids = new LinkedList<TCPId>();
		Map<UInt, TCPId> connectedNodes = new HashMap<UInt, TCPId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();

		generator.setSeed(42);

		data.put(new UInt(1000L), "1000");
		data.put(new UInt(2000L), "2000");
		data.put(new UInt(5000L), "5000");
		data.put(new UInt(7000L), "7000");
		data.put(new UInt(9000L), "9000");

		for (int cpt = 0; cpt < n; cpt++)
			ids.add(new TCPId(new UInt(generator.nextInt(10000)),
					new InetSocketAddress(1515 + cpt)));

		for (TCPId id : ids)
			System.out.println(id.getNumericID());
		System.out.println("");

		for (int cpt = 0; cpt < n; cpt++)
			if (cpt != 0)
				connectedNodes.put(ids.get(cpt).getNumericID(), ids.get(0));
			else
				connectedNodes.put(ids.get(cpt).getNumericID(), null);

		init(ids, connectedNodes, threads, nodes);

		int cpt = 0;
		for (Thread t : threads) {
			t.start();

			if (cpt == 0) {
				for (Entry<UInt, Object> entry : data.entrySet()) {
					nodes.get(0).put(entry.getKey(), entry.getValue());
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
		List<TCPId> ids = new LinkedList<TCPId>();
		Map<UInt, TCPId> connectedNodes = new HashMap<UInt, TCPId>();
		List<Thread> threads = new LinkedList<Thread>();
		List<Node> nodes = new LinkedList<Node>();
		Map<UInt, Object> data = new HashMap<UInt, Object>();

		data.put(new UInt(1000L), "1000");
		data.put(new UInt(2763L), "2763");
		data.put(new UInt(2000L), "2000");
		data.put(new UInt(5000L), "5000");
		data.put(new UInt(7000L), "7000");
		data.put(new UInt(9000L), "9000");

		generator.setSeed(42);

		for (int cpt = 0; cpt < n; cpt++)
			ids.add(new TCPId(new UInt((long) (generator.nextInt(10000))),
					new InetSocketAddress(1515 + cpt)));

		for (TCPId id : ids)
			System.out.println(id.getNumericID());
		System.out.println("");

		for (int cpt = 0; cpt < n; cpt++)
			if (cpt != 0)
				connectedNodes.put(ids.get(cpt).getNumericID(), ids.get(0));
			else
				connectedNodes.put(ids.get(cpt).getNumericID(), null);

		init(ids, connectedNodes, threads, nodes);

		int cpt = 0;

		for (Thread t : threads) {
			t.start();

			if (cpt == 0) {
				for (Entry<UInt, Object> entry : data.entrySet()) {
					nodes.get(0).put(entry.getKey(), entry.getValue());
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
		contigue(10);
		//random(10);
		//cokeAndPut(3);
		//getMeIMFamous(10);
		//leaveMyAss(2); TODO pq ce marche?
		//leaveMyAss(5);
		//coranAlternatif();
	}
}