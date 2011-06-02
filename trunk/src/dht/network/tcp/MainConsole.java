package dht.network.tcp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import dht.Node;
import dht.UInt;

public class MainConsole {

	public static void main(String[] args) {

//		try {
//			System.out.println(java.net.InetAddress.getLocalHost().getHostAddress() + "\n");
//
//			Enumeration<NetworkInterface> enums = java.net.NetworkInterface.getNetworkInterfaces();
//
//			while (enums.hasMoreElements()) {
//				Enumeration<InetAddress> inetEnum = enums.nextElement().getInetAddresses();
//
//				List<InterfaceAddress> list = enums.nextElement().getInterfaceAddresses();
//				
//				for(InterfaceAddress ia : list)
//					System.out.println("#" + ia);
//
//				while (inetEnum.hasMoreElements()) {
//					System.out.println("|" + inetEnum.nextElement());
//				}
//
//				// System.out.println("[ " +
//				// enums.nextElement().getInetAddresses() + "]");
//			}
//
//			System.out.println(java.net.InetAddress.getAllByName(null)[0]);
//			System.out.println(java.net.InetAddress.getAllByName(null)[1]);
//
//		} catch (UnknownHostException e1) {
//			e1.printStackTrace();
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		try {
			int uid = Integer.valueOf(args[0]);
			int port = Integer.valueOf(args[1]);
			String addr = args[2];
			InetSocketAddress address = new InetSocketAddress(addr, port);

			NetworkTCP netTcp = new NetworkTCP();
			TCPId netId = new TCPId(new UInt(uid), address);

			netTcp.addNetworkListener(new PrintNetworkListener());

			Node node;

			if (args.length == 6) {

				int firstNodeUid = Integer.valueOf(args[3]);
				int firstNodePort = Integer.valueOf(args[4]);

				InetSocketAddress firstNodeAddress = new InetSocketAddress(args[5], firstNodePort);

				TCPId firstNode = new TCPId(new UInt(firstNodeUid), firstNodeAddress);

				node = new Node(netTcp, netId, firstNode);
				node.addINodeListener(new PrintNodeListener());
				new Thread(node).start();
			} else {
				node = new Node(netTcp, netId);
				node.addINodeListener(new PrintNodeListener());
				new Thread(node).start();
			}

			Thread.sleep(10000);

			Scanner sc = new Scanner(System.in);

			Pattern putPattern = Pattern.compile("^put ([0-9]+) (.+)");
			Pattern getPattern = Pattern.compile("^get ([0-9]+)");
			Pattern leavePattern = Pattern.compile("^leave$");
			Pattern pingPattern = Pattern.compile("^ping$");

			while (sc.hasNext()) {
				String line = sc.nextLine();

				Matcher putMatcher = putPattern.matcher(line);
				if (putMatcher.matches()) {
					node.put(new UInt(Long.valueOf(putMatcher.group(1))), putMatcher.group(2));
					continue;
				}

				Matcher getMatcher = getPattern.matcher(line);
				if (getMatcher.matches()) {
					System.out.println(" La donnée est : " + node.get(new UInt(Long.valueOf(getMatcher.group(1)))));
					continue;
				}

				Matcher leaveMatcher = leavePattern.matcher(line);
				if (leaveMatcher.matches()) {
					System.out.println("Déconnexion du noeud");
					node.leave();
				}

				Matcher pingMatcher = pingPattern.matcher(line);
				if (pingMatcher.matches()) {
					System.out.println("ping");
					node.ping();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
