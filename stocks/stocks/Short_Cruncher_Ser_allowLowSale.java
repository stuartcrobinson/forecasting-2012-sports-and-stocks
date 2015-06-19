package stocks;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Short_Cruncher_Ser_allowLowSale {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		//next, check this -- if open is high, how many days does it take the price to fall?  like what's the 
		//risk of shorting something but then it goes way up?

		//actually test some strategies.

		double p = 1000;
		double balance = 0;
		double minBalance = 0;
		double fee = 10;

		int dayStart = 1;
		int stockDays = 365;

		double priceMin = 0;
		double priceMax = 1000;
		double openChangeMin = 0.4;
		double openChangeMax = 0.5;

		boolean sellAtClose = true;
		double lowChangeTarget = -0.04;

		int numDaysToHold = 2;


		ArrayList<String> symbols = null;
		ArrayList<String> priceSets = null;
		ArrayList<String> dates = new ArrayList<String>();

		try {

			FileInputStream fileIn = new FileInputStream("C:\\Users\\User\\Documents\\stocks\\data\\symbols_onlyThoseUsed.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			symbols = (ArrayList<String>) in.readObject();
			in.close();
			fileIn.close();

			FileInputStream fileIn2 = new FileInputStream("C:\\Users\\User\\Documents\\stocks\\data\\priceSets_1Years_openLowCloseAdjclose.ser");
			ObjectInputStream in2 = new ObjectInputStream(fileIn2);
			priceSets = (ArrayList<String>) in2.readObject();
			in2.close();
			fileIn2.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(
					new FileInputStream("C:\\Users\\User\\Documents\\stocks\\data\\dates.txt"))));

			String lineStr; //no header
			while ((lineStr = br.readLine()) != null)
				dates.add(lineStr);
			br.close();
		}catch (Exception e1){}


		System.out.println(priceSets.size());
		System.out.println(symbols.size());


		double closeChangeSum = 0;

		double lowChangeSum = 0;

		double saleChangeSum = 0;

		double buyCount = 0;
		double lowTargetFailCount = 0;

		int i = 0;
		for (String priceSet : priceSets){
			String theSymbol = symbols.get(i++);

			double[][] data = new double[stockDays][4];

			BufferedReader br = new BufferedReader(new StringReader(priceSet));
			try {
				String lineStr = br.readLine(); //skip the header
				int r = 0;
				while ((lineStr = br.readLine()) != null && r < dayStart-1 + stockDays) { 
					List<String> line = Arrays.asList(lineStr.split(",", -1));

					if (r >= dayStart - 1) {
						data[r - (dayStart -1)][0] = Double.parseDouble(line.get(0));
						data[r - (dayStart -1)][1] = Double.parseDouble(line.get(1));
						data[r - (dayStart -1)][2] = Double.parseDouble(line.get(2));
						data[r - (dayStart -1)][3] = Double.parseDouble(line.get(3));
					}
					r++;
				}

				for (r = stockDays - 2; r > 0; r--){
					double saleChange = 0;

					double open 			= data[r][0];	
					double low 				= data[r][1];	
					double close			= data[r][2];
					double closeAdj 		= data[r][3];
					double closeAdj_yest 	= data[r+1][3];
					double closeAdj_tomo 	= data[r-1][3];	


					double open_real = open;
					double low_real = low;
					double close_real = close;


					double salePrice = 0;

					double multiplier = closeAdj/close;
					double openAdj = open * multiplier;
					double lowAdj = low * multiplier;

					open = openAdj;
					low = lowAdj;
					close = closeAdj;
					double close_yest = closeAdj_yest;

					double openChange = (open - close_yest) / close_yest;
					if (openChange > openChangeMin && openChange < openChangeMax && open > priceMin && open < priceMax){

						double lowChange = (low - open) / open;
						double closeChange =  (close - open) / open;
						lowChangeSum = lowChangeSum + lowChange;
						closeChangeSum = closeChangeSum + closeChange;

						buyCount++;


						if (lowChange > lowChangeTarget){
							salePrice = close;
							lowTargetFailCount++;
						}
						else {
							salePrice = open + open*lowChangeTarget;
						}


						if (sellAtClose)
							salePrice = close;

						saleChange = (salePrice - open) / open;

						balance = balance + Math.abs(p)*(open - salePrice)/open - fee;

						if (balance < minBalance){
							//							negBalance = true;
							minBalance = balance;
						}

						System.out.format(" %5.2f %6s %11s %7.3f %7.2f %5.2f %5.2f %5.2f\n", openChange, theSymbol, dates.get(r), saleChange, open_real, low_real, close_real, closeAdj);

						saleChangeSum = saleChangeSum + saleChange;

					}
				}
			}
			catch (IOException e){System.out.println(e);}


		}
		double lowChangeAve = lowChangeSum/buyCount;	
		double saleChangeAve = saleChangeSum/buyCount;

		double lowTargetFailRate = lowTargetFailCount/buyCount;

		System.out.println("\n");
		System.out.println("price minimum: " + priceMin );
		System.out.println("price maximum: " + priceMax );
		System.out.println("day change minimum: " + openChangeMin );
		System.out.println("day change max: " 	+ openChangeMax );
		System.out.println("buy count = " + buyCount);
		System.out.println("low change ave = " + lowChangeAve);  
		System.out.println("close change ave = " + closeChangeSum /buyCount );  
		System.out.println("low Change Target = " + lowChangeTarget); 
		System.out.println("low target fail rate = " + lowTargetFailRate);
		System.out.println("sale change ave = " + saleChangeAve);
		System.out.println("day start: " + dayStart);
		System.out.println("stock Days: " + stockDays);
		System.out.println("num days to hold: " + numDaysToHold);
		System.out.println("principle = " + p);
		System.out.println("balance = " + balance);
		if (sellAtClose)
			System.out.println("sell at close");
		if (!sellAtClose)
			System.out.println("sell at low target");

		//		if (negBalance)
		System.out.println("min balance: " + minBalance);

	}

}

/* this is the big risk -- avoid big intervals and use big openChange DON"T HOLD A SHORT OVER NIGHT
2012-10-03	10.75	12.25	8.70	8.70	98200	8.70	
2012-10-02	0.16	0.51	0.16	0.25	49100	0.25	
2012-10-01	0.15	0.19	0.15	0.18	90000	0.18	
2012-09-28	0.19	0.19	0.15	0.15	62500	0.15	
 */