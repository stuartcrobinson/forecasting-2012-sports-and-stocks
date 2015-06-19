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

public class Long_Cruncher_Ser {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		double priceMin = 5;
		double priceMax = 10000;
		double openChangeMin = -100;
		double openChangeMax = 100;

		int dayStart = 1;
		int stockDays = 252;	//252, ...756, 1018 1270 1522 1774 2026 2278 2530 STOP 2783

		double lowChangeMin = -999;//-0.7;
		double lowChangeMax = 999;//-0.2;
		
		int daysToHold = 60;		//starts at 0
		
		boolean buyAtOpen = true;
		boolean buyDuringDay = false;

		int p = 1000;
		double balance = 0;
		double minBalance = 1000;
		double saleHandicap = 0.03;
		int fee = 10;

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
		System.out.println(dates.size());

		double saleChangeSum = 0;
		int buyCount = 0;
		int i = 0;
		for (String priceSet : priceSets){
			String theSymbol = symbols.get(i++);
			
			if (theSymbol.equals("DSKX") || theSymbol.equals("PED") || theSymbol.equals("SPCB")|| theSymbol.equals("ICLD"))
				continue;

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

				for (r = stockDays - 2; r >= daysToHold; r--){
					double saleChange = 0;

					double open_raw 		= data[r][0];	
					double low_raw 			= data[r][1];	
					double close_raw		= data[r][2];
					double closeAdj 		= data[r][3];

					double closeAdj_yest 	= data[r+1][3];

					double multiplier = closeAdj/close_raw;
					double openAdj = open_raw * multiplier;
					double lowAdj = low_raw * multiplier;

					double open = openAdj;
					double low = lowAdj;

					double buyPrice = 0;

					double close_yest = closeAdj_yest;
					
					double sellDayClose = data[r - daysToHold][3];
					
					double salePrice = sellDayClose - sellDayClose*saleHandicap;

					double openChange = (open - close_yest) / close_yest;
					if (openChange > openChangeMin && openChange < openChangeMax && open > priceMin && open < priceMax){

						double lowChange = (low - open) / open;

						boolean lowPass = lowChange  < lowChangeMax && lowChange  > lowChangeMin;
						boolean openPass = openChange < lowChangeMax && openChange > lowChangeMin;
						
						if ( (lowPass && buyDuringDay) || (openPass && buyAtOpen)){
							if ( openPass )
								buyPrice = open;
							else // lowPass
								buyPrice = (lowChangeMax*open + open);


							saleChange = (salePrice - buyPrice) / buyPrice;
							saleChangeSum = saleChangeSum + saleChange;

							balance = balance + p*(salePrice - buyPrice)/buyPrice - fee;
							if (balance < minBalance)
								minBalance = balance;

							System.out.format(" %5.2f %6s %11s %7.3f %7.2f %5.2f %5.2f\n", 
									lowChange, theSymbol, dates.get(r), saleChange, open, low, closeAdj);
							buyCount++;
						}
					}
				}
			}
			catch (IOException e){System.out.println(e);}
		}
		double saleChangeAve = saleChangeSum/buyCount;

		System.out.println("\n");
		System.out.format("%18s %s %3s %n", 	"price range:",
				(priceMin == (int) priceMin ? String.valueOf((int) priceMin) : priceMin), 
				(priceMax == (int) priceMax ? String.valueOf((int) priceMax) : priceMax) );
		System.out.format("%18s %s %3s%n", 	"open change range:", 
				(openChangeMin == (int) openChangeMin ? String.valueOf((int) openChangeMin) : openChangeMin),
				(openChangeMax == (int) openChangeMax ? String.valueOf((int) openChangeMax) : openChangeMax) );
		System.out.format("%18s %s %3s%n", 	"low change range:", 
				(lowChangeMin == (int) lowChangeMin ? String.valueOf((int) lowChangeMin) : lowChangeMin),
				(lowChangeMax == (int) lowChangeMax ? String.valueOf((int) lowChangeMax) : lowChangeMax) );
		System.out.format("%18s %d %n", 		"days to hold:", 		daysToHold);	
		System.out.format("%18s %s %n", 		"buy at open?:", 		String.valueOf(buyAtOpen));	
		System.out.format("%18s %s %n", 		"buy during day?:", 	String.valueOf(buyDuringDay));	

		System.out.format("%18s %d %n", 		"num days:", 		stockDays);	
		System.out.format("%18s %s-%s %n", 		"day range:",		dayStart, dayStart + stockDays -1);
		System.out.format("%18s %s to %s %n", 	"date range:",	dates.get(dayStart -1 + stockDays), dates.get(dayStart - 1));
		System.out.format("%18s %d, %d, %.2f%n", 	"$p, fee, spread:", 	p, fee, saleHandicap);
		System.out.format("%18s %d %n", 		"buy count:", 		buyCount);
		System.out.format("%18s %.3f  *****%n", "sale change ave:", saleChangeAve);
		System.out.format("%18s %.2f  %n", 		"balance:", 	balance);
		System.out.format("%18s %.2f %n", 		"min balance:", minBalance);
	}
}
