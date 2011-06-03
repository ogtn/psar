package dht.network.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map.Entry;

import dht.ANodeId;
import dht.INetworkListener;
import dht.INode;
import dht.UInt;
import dht.message.AMessage;
import dht.message.MessagePing;
import dht.tools.Tools;

/**
 * Listener d'affichage dans un fichier des évènements réseaux.
 */
public class GraphvizNetworkListener implements INetworkListener {

	private final File fLabel, fArrow, fImage;

	/**
	 * Crée et initialise un écouteur enregistrant dans un fichier placé dans le
	 * répertoire /tmp/.
	 */
	public GraphvizNetworkListener() {
		fLabel = new File("/tmp/label");
		fArrow = new File("/tmp/arrow");
		fImage = new File("/tmp/ring.png");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void eventRecvMessage(AMessage message, INode node) {
		if (message instanceof MessagePing) {

			System.out.println("node " + node + "\nmsg OriginalSource "
					+ message.getOriginalSource() + "\nmsg Source "
					+ message.getSource());

			try {
				MessagePing mp = (MessagePing) message;

				if (mp.getOriginalSource().equals(node.getId())
						&& mp.getSource().equals(node.getId())) {
					System.out.println("fLabel.delete(); " + fLabel.delete());
					System.out.println("fArrow.delete(): " + fArrow.delete());
					System.out.println("fImage.delete(): " + fImage.delete());
					System.out.println("Je : " + node.getId().getNumericID()
							+ " ecrase les fichiers ");

					PrintWriter pwArrow = null;

					try {
						pwArrow = new PrintWriter(new BufferedWriter(
								new FileWriter(fArrow, true)));
						pwArrow
								.write("digraph\n{\ngraph [size = \"10.67!\", ratio = 1];\n");
						pwArrow
								.write("node [shape = record, style = filled];\n");
						pwArrow
								.write("edge [shape = record, style = bold, arrowsize = 2, penwidth = 2];\n");
						pwArrow.write(node.getId().getNumericID() + " -> ");
					} finally {
						Tools.close(pwArrow);
					}

				} else if (mp.getOriginalSource().equals(node.getId())) {
					BufferedReader brL = null;
					PrintWriter pwArrow = null;

					try {
						brL = new BufferedReader(new FileReader(fLabel));
						pwArrow = new PrintWriter(new BufferedWriter(
								new FileWriter(fArrow, true)));

						pwArrow.write(node.getId().getNumericID() + ";\n");
						pwArrow.write(node.getId().getNumericID()
								+ " [label = \"{" + node.getRange().getBegin()
								+ "-" + node.getRange().getEnd() + "}\"];\n");
						String str = null;

						while ((str = brL.readLine()) != null)
							pwArrow.write(str + "\n");

						pwArrow.write("}\n");

					} finally {
						Tools.close(brL);
						Tools.close(pwArrow);
					}

					Runtime run = Runtime.getRuntime();
					Process proc = run.exec("circo " + fArrow + " -Tpng -o "
							+ fImage);
				} else {
					PrintWriter pwLabel = null;
					PrintWriter pwArrow = null;

					try {
						pwLabel = new PrintWriter(new BufferedWriter(
								new FileWriter(fLabel, true)));
						pwArrow = new PrintWriter(new BufferedWriter(
								new FileWriter(fArrow, true)));

						pwArrow.write(String.valueOf(node.getId()
								.getNumericID()));
						pwArrow.write(" -> ");

						pwLabel.write(node.getId().getNumericID()
								+ " [label = \"{" + node.getRange().getBegin()
								+ "-" + node.getRange().getEnd() + " | {");

						boolean firstLoop = true;

						for (Entry<UInt, Serializable> entry : node.getRange()
								.getData().entrySet()) {
							if (firstLoop)
								firstLoop = false;
							else
								pwLabel.write("|");
							
							pwLabel.write(entry.getValue().toString());
						}

						pwLabel.write("}}\"];\n");

					} finally {
						Tools.close(pwLabel);
						Tools.close(pwArrow);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void eventSendMessage(AMessage message, INode node, ANodeId id,
			boolean isInChannel) {
	}
}
