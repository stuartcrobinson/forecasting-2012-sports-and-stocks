package intraDayWorld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

public class TAQ_BidAskFormatter {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {

		BufferedReader br = new BufferedReader(new FileReader(new File("C:\\TAQ d2 out\\q.txt")));

		PrintStream ps = new PrintStream(new File("C:\\TAQ d2 out\\q_shrunk.txt"));

		String lineStr = br.readLine();	//skip header

		int minute = -1, prev_minute;

		int i = -1;
		while ((lineStr = br.readLine()) != null) {// && i < 10000){
			i++;

			prev_minute = minute;

			ArrayList<String> lineAL = new ArrayList<String>(
					Arrays.asList(lineStr.split("[ 	]")));
			while (lineAL.remove(""));

			//			@SYMBOL	DATE	EXCHANGE	TIME	BID	BID SIZE	OFFER	OFFER SIZE	MODE	MMID
			//			IBM	02/03/2005	P	8:00:06	        92.3500	15	         0.0000	0
			//			IBM	02/03/2005	P	8:00:13	        92.9200	10	        95.4800	10

			String symbol 	= lineAL.get(0);
			String date 	= lineAL.get(1);
//			String exchange = lineAL.get(2);
			String time		= lineAL.get(3);
			String bid		= lineAL.get(4);
//			String size		= lineAL.get(5);
			String ask		= lineAL.get(6);

			String [] timeAr = time.split(":");

			int hour = Integer.parseInt(timeAr[0]);
			minute = Integer.parseInt(timeAr[1]); 
			int sec = Integer.parseInt(timeAr[2]);

			if (minute != prev_minute)
				if (hour < 16 && hour >= 9 && minute >= 31)
					ps.format("%s,%s,%s,%s,%s%n", symbol, date, time, bid, ask);

		}

		br.close();
		ps.close();
	}
}


