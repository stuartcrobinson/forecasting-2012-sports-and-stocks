package theading;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class PortThread extends Thread {

	int i;
	public PortThread(int i) {
		this.i = i;
	}

	public void run() {	

		try {
			URL my_url = new URL("https://www.google.com/");
			BufferedReader br = new BufferedReader(new InputStreamReader(my_url.openStream()));
			String strTemp = "";
			int l = 0;
			while(null != (strTemp = br.readLine())){
				//				System.out.println(strTemp);
				l++;
			}
			System.out.println(i +". "+ l + " lines");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

public class TheMain {
	public static void main(String[] args) {

		ExecutorService pool = Executors.newFixedThreadPool(10);

		for (int i = 0; i > -1; i++){
			pool.execute(new PortThread(i));
		}		//
		//		pool.shutdown();
	}
}
