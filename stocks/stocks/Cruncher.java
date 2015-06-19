package stocks;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Cruncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//next, check this -- if open is high, how many days does it take the price to fall?  like what's the 
		//risk of shorting something but then it goes way up?
		
		//actually test some strategies.
		
		double dayChangeMin = -0.1;
		double dayChangeMax = 0.1;
		double priceMin = 5;
		double priceMax = 10;
		
		Double open = 		null;
		Double high = 		null;
		Double low = 		null;
		Double close = 		null;
		Double open_yest = 	null;
		Double high_yest = 	null;
		Double low_yest = 	null;
		Double close_yest =	null;
		Double open_tomo = 	null;
		Double high_tomo = 	null;
		Double low_tomo = 	null;
		Double close_tomo =	null;

		double lowChangeSum = 0;
		int lowChangeCounter = 0;

		String symbolsFileName = "C:\\Users\\User\\Documents\\stocks\\symbols.txt";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(symbolsFileName));
			String symbol;
			while ((symbol = reader.readLine()) != null)   {
				System.out.println(symbol);

				String fileName = "C:\\Users\\User\\Documents\\stocks\\data\\" + symbol + ".csv";

				try { 
					BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fileName))));


					String lineStr = br.readLine(); //skip the header
					while ((lineStr = br.readLine()) != null) { 
						List<String> line = Arrays.asList(lineStr.split(",", -1));

						open = open_yest;
						high = high_yest;
						low = low_yest;
						close = close_yest;

						open_yest = 	Double.parseDouble(line.get(1));
						high_yest =		Double.parseDouble(line.get(2));
						low_yest = 		Double.parseDouble(line.get(3));
						close_yest = 	Double.parseDouble(line.get(4));

						if (open != null){

							double openChange = (open - close_yest) / close_yest;

							if (openChange > dayChangeMin && openChange < dayChangeMax && open > priceMin && open < priceMax){

								double lowChange = (open - low) / open;
								lowChangeSum = lowChangeSum + lowChange;
								lowChangeCounter ++;
							}
						}
					}
					br.close();
				}
				catch (Exception ee) {
					System.out.println("MISSING FILE:  " + symbol);
					continue;
				}
			}
			reader.close();
			double lowChangeAve = lowChangeSum/lowChangeCounter;

			System.out.println("\n");
			System.out.println("price minimum: " + priceMin );
			System.out.println("price maximum: " + priceMax );
			System.out.println("day change minimum: " + dayChangeMin );
			System.out.println("day change max: " 	+ dayChangeMax );
			System.out.println("low change count = " + lowChangeCounter);
			System.out.println("low change ave = " + lowChangeAve);
		} catch (Exception ex) {
			ex.printStackTrace();
		}


	}

}
