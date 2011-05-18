package dht.network.tcp;

import java.net.InetSocketAddress;

import dht.INode;
import dht.Node;
import dht.UInt;

public class MainJar {
	private static final Long data[][] = { { 10L, 100L }, { 42L, 420L }, { 666L, 6660L }, { 85300L, 853000L }, { 1040L, 10400L },
			{ 2000L, 20000L }, { 5001L, 50010L }, { 3000L, 30000L }, { 3001L, 30010L }, { 3999L, 39990L } };
	
	
	private static void testPut(INode node) {
		for (int cpt = 0; cpt < data.length; cpt++) {
			node.put(new UInt(data[cpt][0]), data[cpt][1]);
		}
	}
	
	
	private static void testGet(INode node) {
		for (int cpt = 0; cpt < data.length; cpt++) {
			node.get(new UInt(data[cpt][0]));
		}
	}

	public static void main(String[] args) {

		System.out.println("args.length  " + args.length);

		/*
		 * args = new String[6];
		 * 
		 * args[0] = "50"; args[1] = "1515"; args[2] = "132.227.114.37"; args[3]
		 * = "888"; args[4] = "1789"; args[5] = "132.227.114.43";
		 */

		try {
			int uid = Integer.valueOf(args[0]);
			int port = Integer.valueOf(args[1]);
			String addr = args[2];
			InetSocketAddress address = new InetSocketAddress(addr, port);

			NetworkTCP netTcp = new NetworkTCP();
			TCPId netId = new TCPId(new UInt(uid), address);

			netTcp.addNetworkListener(new PrintNetworkListener());

			if (args.length == 6) {

				int firstNodeUid = Integer.valueOf(args[3]);
				int firstNodePort = Integer.valueOf(args[4]);

				InetSocketAddress firstNodeAddress = new InetSocketAddress(args[5], firstNodePort);

				TCPId firstNode = new TCPId(new UInt(firstNodeUid), firstNodeAddress);

				Node node = new Node(netTcp, netId, firstNode);
				node.addINodeListener(new PrintNodeListener());
				node.run();
			} else {
				Node node = new Node(netTcp, netId);
				node.addINodeListener(new PrintNodeListener());
				new Thread(node).start();

				Thread.sleep(8000);
				System.out.println("====PING!!!!====");

				testPut(node);

				node.ping();	
				
				testGet(node);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
