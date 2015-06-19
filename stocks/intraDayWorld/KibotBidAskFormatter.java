package intraDayWorld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;

public class KibotBidAskFormatter {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {

		BufferedReader br = new BufferedReader(new FileReader(new File("C:\\Users\\User\\Documents\\stocks\\WDC_tickbidask.txt")));

		PrintStream ps = new PrintStream(new File("C:\\Users\\User\\Documents\\stocks\\WDC_tickbidask_shrunk.txt"));

		String line;

		String minute = "NO", prev_minute;
		int i = 0;
		while ((line = br.readLine()) != null && i < 1000){

			//			System.out.println(line);
			//			i++;
			prev_minute = minute;
			minute = line.substring(14,16);



			if (!minute.equals(prev_minute)){
				//new minute, so save this time.  

				String [] lineAr = line.split(",");

				String date = lineAr[0];
				String time = lineAr[1];
				String bid = lineAr[3];
				String ask = lineAr[4];

				//				System.out.format("%s,%s,%s,%s%n", date, time, bid, ask);

				int hour = Integer.parseInt(time.substring(0,2));
				int minuteInt = Integer.parseInt(minute); 

				if (hour < 16 && (hour >= 9 && minuteInt >= 30 ))
					ps.format("%s,%s,%s,%s%n", date, time, bid, ask);

			}
		}


		System.out.println(i);
		br.close();
		ps.close();
	}

}
