package dht.network.tcp;

import java.net.InetSocketAddress;

import dht.Node;
import dht.UInt;

public class MainMonitJar {

	public static void main(String[] args) {

		try {

			InetSocketAddress address = new InetSocketAddress("132.227.114.37", 1515);

			NetworkTCP netTcp = new NetworkTCP();
			TCPId netId = new TCPId(new UInt(50), address);

			netTcp.addNetworkListener(new PrintNetworkListener());

			Node node = new Node(netTcp, netId);
			node.addINodeListener(new PrintNodeListener());

			Thread t = new Thread(node);

			t.start();

			Thread.sleep(10000);

			while (true) {
				
				Thread.sleep(5000);
				
				node.ping();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
