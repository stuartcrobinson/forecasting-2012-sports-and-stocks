package forex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//i tried buying based on jumps in price and then selling at set interval distance in days. doens't work.
@Deprecated
public class SerializerTester {

	final static int DAY = 0;
	final static int TIME = 1;
	final static int PRICE = 2;

	final static int NUM_COMPARATORS = 2;		//high, low, MA
	static int NUM_COMPARATOR_DAYS;	//.5, 1, 3, 6, 10, 30, 80

	final static int HIGH = 0;
	final static int LOW = 1;
	//	final static int MA = 2;

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		final long startTime = System.currentTimeMillis();

		ArrayList<String> strings = new ArrayList<String>();
		FileInputStream fileIn = new FileInputStream("C:\\Users\\User\\Documents\\stocks\\forex\\forexite\\2013toNov_string.ser");
		ObjectInputStream in = new ObjectInputStream(fileIn);
		strings = (ArrayList<String>) in.readObject();
		in.close();	fileIn.close();


		int sumRows = 0; 

		int [] startingRows = new int [18];
		String [] symbols = new String [] {
				"AUDJPY",
				"AUDUSD",
				"CHFJPY",
				"EURCAD",
				"EURCHF",
				"EURGBP",
				"EURJPY",
				"EURUSD",
				"GBPCHF",
				"GBPJPY",
				"GBPUSD",
				"NZDJPY",
				"NZDUSD",
				"USDCAD",
				"USDCHF",
				"USDJPY",
				"XAGUSD",
				"XAUUSD"
		};

		int rowNum = 0;
		for (String s : strings){
			startingRows[rowNum++] = sumRows;
			sumRows += s.split(System.getProperty("line.separator")).length;
		}

		for (int i1 : startingRows)
			System.out.println(i1);
		System.out.println(sumRows);
		System.out.println("converting to matrix...");


		ArrayList<double[][]> priceSets = new ArrayList<double[][]>();

//		int limiter = -1;
		for (String s : strings){
//			limiter++;
//			if (limiter > 2) break;

			int len = s.split(System.getProperty("line.separator")).length;

			double[][] priceSet = new double[len][3];

			BufferedReader br = new BufferedReader(new StringReader(s));	
			int r = 0;

			String lineStr;
			while ((lineStr = br.readLine()) != null) { 
				List<String> line = Arrays.asList(lineStr.split(",", -1));
				priceSet[r][0] = Double.parseDouble(line.get(0));
				priceSet[r][1] = Double.parseDouble(line.get(1));
				priceSet[r][2] = Double.parseDouble(line.get(2));
				r++;
			}		
			priceSets.add(priceSet);
		}

		System.out.println(priceSets.size());
		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

		System.out.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );

		System.out.println("bulding comparators....");

		double[] nDays = new double[] {0.5, 1, 5};
		NUM_COMPARATOR_DAYS = nDays.length;

		ArrayList<double[][][]> comparators = new ArrayList<double[][][]>();

		int symbol_i = -1;
		for (double[][] priceSet : priceSets){
			symbol_i++;
//			if (symbol_i > 2) break;

			String symbol = symbols[symbol_i];
			System.out.println(symbol);


			double[][][] comparator = new double[priceSet.length][NUM_COMPARATORS][NUM_COMPARATOR_DAYS];

			for (int di = 0; di < NUM_COMPARATOR_DAYS; di++){	//comparator days index

				int highRow = -1;
				int lowRow = -1;

				double high = -9999;				//high
				double low = 9999;				//high

				int numRowsLookback = (int) (nDays[di]*1440);
				System.out.println(di +" "+ numRowsLookback);

				for (int r = numRowsLookback; r < priceSet.length; r++){

					if (r - highRow > numRowsLookback){
						high = -1;												//reset the high!
						for (int i = r - 1; i > r - numRowsLookback; i--){
							if (priceSet[i][PRICE] > high){
								high = priceSet[i][PRICE];
								highRow = i;
							}
						}
					}
					else if (priceSet[r-1][PRICE] > high){
						high = priceSet[r-1][PRICE];
						highRow = r-1;
					}
					

					if (r - lowRow > numRowsLookback){
						low = 100000;												//reset the low!
						for (int i = r - 1; i > r - numRowsLookback; i--){
							if (priceSet[i][PRICE] < low){
								low = priceSet[i][PRICE];
								lowRow = i;
							}
						}
					}
					else if (priceSet[r-1][PRICE] < low){
						low = priceSet[r-1][PRICE];
						lowRow = r-1;
					}
					
					
					comparator[r][HIGH][di] = high;
					comparator[r][LOW][di] = low;
				}
			}
			comparators.add(comparator);

//			for (int i = 0; i < comparator.length; i++){
//				System.out.format("%.4f %.4f %.4f %.4f\n", comparator[i][HIGH][0], comparator[i][HIGH][1],  comparator[i][LOW][0], comparator[i][LOW][1]);//, comparator[i][HIGH][2]);
//				//				if (comparator[i][HIGH][0] != comparator[i][HIGH][1])
//				//					System.out.println("mismatch!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			}
		}
		System.out.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );

		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		System.out.println("ready!");


		String input = "";

		while (!input.equals("q")){ 

			System.out.println("enter: delay fwd buyChange lookahead_rows comp_i day_i");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			try {	input = br.readLine(); 		}catch (IOException ioe) {System.exit(1);}

			List<String> inputs = Arrays.asList(input.split(" ", -1));

			double buyChangeMin	= Double.parseDouble(	inputs.get(0));		//use neg numbers.  so max is actually less than min.
			double buyChangeMax	= Double.parseDouble(	inputs.get(1));		
			double sellChange 	= Double.parseDouble(	inputs.get(2));		
			int max_hold		= Integer.parseInt(		inputs.get(3));		
			int day_i 			= Integer.parseInt(		inputs.get(4));

			//0 0 0.01 1440 0 0
			double saleChange = 999999999;
			double saleChangeSum = 0;
			double holdSum = 0;
			int count = 0;
			int buyCount = 0;

			symbol_i = -1;
			for (double[][] d : priceSets){
				symbol_i++;
//				if (symbol_i > 2) break;

				String symbol = symbols[symbol_i];
				if ((symbol.equals("XAUUSD") || symbol.equals("XAGUSD")))
					continue;

				int buyRow = -999999;
				boolean boughtHighs = false;
				boolean boughtLows = false;
				boolean bought = false;
				double buyPrice = 0;

				double high = 0;
				double low = 100000;

				for (int r = (int) (nDays[day_i]*1440.0); r < d.length; r++){

					double price = d[r][PRICE];
					double day = d[r][DAY];
					double time = d[r][TIME];

					if (!bought){
						high = comparators.get(symbol_i)[r][HIGH][day_i];
						low  = comparators.get(symbol_i)[r][LOW][day_i];
																			
//						if ( (price - high)/high < buyChangeMin && (price - high)/high > buyChangeMax) {
//							boughtHighs = true;
//							bought = true;
//							buyPrice = price;
//							buyRow = r;
//							buyCount++;
//						}
						if ( (price - low)/low > buyChangeMin && (price - low)/low < buyChangeMax) {
							boughtLows = true;
							bought = true;
							buyPrice = price;
							buyRow = r;
							buyCount++;
						}
					}
					if (bought){
//						if ( boughtHighs && ((price - buyPrice)/buyPrice > sellChange || r - buyRow == max_hold)){
//
//							double salePrice = price;
//
//							saleChange = (salePrice - buyPrice) / buyPrice;
//							saleChangeSum += saleChange;
//							count++;
//							boughtHighs = false;
//							bought = false;
//
//							holdSum += (r-buyRow)/1440.0;
//							
//							System.out.format("h %s %04.0f %04.0f %6.3f %8.3f hold: %.2f %5.4f\n", 
//									symbol, day, time, buyPrice, price, (r-buyRow)/1440.0, saleChange); 
//						}
						if ( boughtLows && ((price - buyPrice)/buyPrice < sellChange || r - buyRow == max_hold)){

							double salePrice = price;

							saleChange = (salePrice - buyPrice) / buyPrice;
							saleChangeSum += saleChange;
							count++;
							boughtLows = false;
							bought = false;

							holdSum += (r-buyRow)/1440.0;
							
							System.out.format("l %s %04.0f %04.0f %6.3f %8.3f hold: %.2f %5.4f\n", 
									symbol, day, time, buyPrice, price, (r-buyRow)/1440.0, saleChange); 
						}
					}
					
				}
			}

			System.out.format("%d, sell count: %d, pct: %.4f, ave hold: %.4f \n\n\n", buyCount, count, saleChangeSum/count, holdSum/count);



		}




		System.out.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );
	}
}


//these show momentum -- buying a stock if it jumps up, and it will keep going up.  NOT metals. currencies only.
//5 0.01 0.15 100 288000
//5 0.01 0.15 180 288000
//0 0.008 0.04 720 288000	-- test this on other years
//0 0.03 0.02 2880 288000

//metals seem to work with contrarian strategy.  max return 36%/yr
//0 0.01 -0.04 7200 288000


/*
if (bought && ( ((p - buyPrice) / buyPrice) < sellChange || r - buyRow > fwd || lastRowOfData) ){

//must sell now
holdSum += r - buyRow;

bought = false;
count++;

saleChange = (p - buyPrice)/buyPrice;
saleChangeSum += saleChange;

System.out.format("%s %04.0f %04.0f %6.3f %8.3f hold: %.3f %5.4f\n", 
		symbol, day, time, buyPrice, price, (r-buyRow)/1440.0, saleChange); 
}*/