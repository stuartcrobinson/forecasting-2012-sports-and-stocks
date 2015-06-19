package drone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

//http://www.quackit.com/html/codes/html_radio_button.cfm

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	      String userName = null;

	      //  read the username from the command-line; need to use try/catch with the
	      //  readLine() method
	      try {
	         userName = br.readLine();
	      } catch (IOException ioe) {
	         System.out.println("IO error trying to read your name!");
	         System.exit(1);
	      }

	      System.out.println("Thanks for the name, " + userName);
		
///*
//		Connection conn =  Jsoup.connect("http://www.quackit.com/html/codes/html_radio_button.cfm").timeout(60*1000);
////		conn.request().
//		Document doc = conn.data("value", "Blue").data("value", "House").data("value", "Submit").userAgent("Mozilla").post();	//"Username" is both the field id and name
//		System.out.println("\npage title: "+ doc.title() +" "+ doc.absUrl("href"));	//took 4 seconds
//
//		System.out.println(doc.html());
//
//		*/
//		
//
//		double doubleTen = 10.0;
//		double doubleTenPointFive = 10.5;
//
//		System.out.println(  doubleTen == (int) doubleTen ? (int) doubleTen : doubleTen );
//		
//		System.out.println(doubleTen == (int) doubleTen);
//		System.out.println(doubleTen);
//		System.out.println((int) doubleTen);
//		
//		String s = String.valueOf((int) doubleTen);
//		System.out.println(s);
//		
//		System.out.format("%s%n", ( doubleTen == (int) doubleTen ? (int) doubleTen : doubleTen)  );
//		System.out.format("%s%n", ( doubleTenPointFive == (int) doubleTenPointFive ? (int) doubleTenPointFive : doubleTenPointFive)  );
//		
//		//    :-(
	}

}
