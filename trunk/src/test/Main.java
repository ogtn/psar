package test;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		Server s = new Server();
		s.start();

		Thread.sleep(500);

		Client c1 = new Client();
		c1.start();
		c1.join();
		/*
		Client c2 = new Client();
		c2.start();
		c2.join();
		*/
		s.join();
	}
}
