package dht.network.tcp;

import java.net.InetSocketAddress;

import dht.Node;
import dht.UInt;

public class MainJar {

	public static void main(String[] args) {

		System.out.println("args.length  " + args.length);
		
		/*args = new String[6];
		
		args[0] = "50";
		args[1] = "1515";
		args[2] = "132.227.114.37";
		args[3] = "888";
		args[4] = "1789";
		args[5] = "132.227.114.43";*/
		
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

				InetSocketAddress firstNodeAddress = new InetSocketAddress(
						args[5], firstNodePort);

				TCPId firstNode = new TCPId(new UInt(firstNodeUid),
						firstNodeAddress);

				Node node = new Node(netTcp, netId, firstNode);
				node.addINodeListener(new PrintNodeListener());
				node.run();
			} else {
				Node node = new Node(netTcp, netId);
				node.addINodeListener(new PrintNodeListener());
				new Thread(node).start();
				
				Thread.sleep(1000);
				System.out.println("====PING!!!!====");
				
				while (true) {
					//System.out.println("====Avant sleep====");
					Thread.sleep(5000);
					//System.out.println("====Apres sleep====");
					node.ping();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
