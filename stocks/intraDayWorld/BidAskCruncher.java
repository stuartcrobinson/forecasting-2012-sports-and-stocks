package intraDayWorld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BidAskCruncher {

	final static int DAY 	= 0;
	final static int HOUR 	= 1;
	final static int MINUTE = 2;
	final static int BID 	= 3;
	final static int ASK 	= 4;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		//at new day, get opening ASK and notice the previous ASK = yesterday's close.
		//if price change meets the cutoff, then buy at next line IF it's within X minutes
		//sell --- when?  ----------  sell at the first opportunity that comes 30 minutes before close -- sell at BID price

		//09/28/2009,09:30:06,35.34,35.64
		//09/28/2009,09:31:16,35.53,35.56
		//09/28/2009,09:32:00,35.58,35.6

		double changeMin = -0.03;
		double changeMax = 0;
		
		List<String> lines = Files.readAllLines(Paths.get(
				"C:\\Users\\User\\Documents\\stocks\\WDC_tickbidask_shrunk.txt"), StandardCharsets.UTF_8);

		System.out.println(lines.size());

		int len = lines.size();

		double[][] data = new double[len][5];

		for (int r = 0; r < len; r ++){
			String [] line = lines.get(r).split(",");
			double day = Double.parseDouble(line[0].substring(3, 5));
			double hour = Double.parseDouble(line[1].substring(0, 2));
			double minute = Double.parseDouble(line[1].substring(3, 5));
			double bid = Double.parseDouble(line[2]);
			double ask = Double.parseDouble(line[3]);

			data[r][DAY] = day;
			data[r][HOUR] = hour;
			data[r][MINUTE] = minute;
			data[r][BID] = bid;
			data[r][ASK] = ask;
		}
		
		boolean bought = false;
		double buyPrice = 0, saleChangeSum = 0;
		int count = 0;
		
		for (int r = 1; r < len; r++){
			
			if (data[r][DAY] != data[r-1][DAY]){
				
				if (bought){
					double sellPrice = data[r-1][BID];
					saleChangeSum += (sellPrice - buyPrice)/buyPrice;
					count++;
					bought = false;					
				}
				
				//at first of day
				double open = data[r][ASK];
				double prev_close = data[r-1][ASK];
				
				double openChange = (open - prev_close)/prev_close;
				
				if (openChange > changeMin && openChange < changeMax){
					bought = true;
					buyPrice = open;
				}
			}
		}
		System.out.format("%.4f, %d%n", saleChangeSum/count, count);
		

		//		BufferedReader br = new BufferedReader(new FileReader(new File(
		//				"C:\\Users\\User\\Documents\\stocks\\WDC_tickbidask_shrunk.txt")));
		//
		//		String line;
		//		String prev_day, day = "cat";
		//
		//		int lines = -1;
		//		while ((line = br.readLine()) != null){
		//			lines++;
		//			prev_day = day;
		//			day = line.substring(3, 5);
		//			
		//			if (!day.equals(prev_day)){
		//				
		//				
		//				
		//			}
		//
		//
		//
		//		}

	}

}