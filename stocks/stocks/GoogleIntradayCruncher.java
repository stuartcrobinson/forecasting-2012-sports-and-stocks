package stocks;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//long
@Deprecated
public class GoogleIntradayCruncher {

	@SuppressWarnings("unchecked")
	
	public static void main(String[] args) {
		int longCount = 0, shortCount = 0;
		double shortSaleChangeSum = 0, longSaleChangeSum = 0;

		double priceMin = 0;
		double priceMax = 1000;
		double openChangeMin = -100;
		double openChangeMax = 100;

		int stockDays = 15;	

		double changeLimit = 0.5;
		System.out.println("changelimit: "+ changeLimit);

		int p = 1000;
		double shortBalance = 0;
		double longBalance = 0;
		double minBalance = 1000;
		double saleHandicap = 0.98;
		int fee = 2;

		ArrayList<String> symbols = null;
		ArrayList<String> priceSets = null;

		try {

			FileInputStream fileIn = new FileInputStream(
					"C:\\Users\\User\\Documents\\stocks\\archive\\Data Downloader\\data\\google1_symbols.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			symbols = (ArrayList<String>) in.readObject();
			in.close();
			fileIn.close();

			FileInputStream fileIn2 = new FileInputStream(
					"C:\\Users\\User\\Documents\\stocks\\archive\\Data Downloader\\data\\google1_priceSetsOpen.ser");
			ObjectInputStream in2 = new ObjectInputStream(fileIn2);
			priceSets = (ArrayList<String>) in2.readObject();
			in2.close();
			fileIn2.close();

		}catch (Exception e1){System.out.println(e1);}

		System.out.println(priceSets.size());
		System.out.println(symbols.size());


		int i = 0;
		for (String priceSet : priceSets){
			String theSymbol = symbols.get(i++);

			//if (theSymbol.equals("VISN") ||theSymbol.equals("ESMC") || theSymbol.equals("FONR")){}
			//else
			//continue;
			//			System.out.println(theSymbol);

			//			if (i > 2)
			//				break;

			int len = priceSet.split(System.getProperty("line.separator")).length;


			double[][] data = new double[len][3];

			try {
				/*			A
			1028,0930,51.79
			1028,0931,51.87
			1028,0932,51.88
			1028,0933,51.84
			1028,0934,51.62
			1028,0935,51.58
			1028,0936,51.59
			1029...*/
				int r = 0;
				BufferedReader br = new BufferedReader(new StringReader(priceSet));
				String lineStr;
				while ((lineStr = br.readLine()) != null) { 
					List<String> line = Arrays.asList(lineStr.split(",", -1));
					data[r][0] = Double.parseDouble(line.get(0));
					data[r][1] = Double.parseDouble(line.get(1));
					data[r][2] = Double.parseDouble(line.get(2));
					r++;
				}

				double high = 0;		//tracking minutely.  NOT the google-listed highs and lows
				double low = 0;
				double priceDelayed = 0;
				double open = 0;
				double open_delayed = 0;
				double close = 0;
				double close_early = 0;

				double priceChange = 0;

				double longBuy = 0, shortBuy = 0;

				boolean goneLong = false, goneShort = false;


				boolean lastRowForDay = false;
				boolean firstRowForDay = true;

				double previousClose = 0.7777777777;

				for (r = 0; r < len; r++){
					if (len == 1)
						break;
					double day	 = data[r][0];
					double time	 = data[r][1];
					double price = data[r][2];

					if (r ==0 || day != data[r-1][0])
						firstRowForDay = true;
					else
						firstRowForDay = false;

					if (r == len-1 || day != data[r+1][0])
						lastRowForDay = true;
					else
						lastRowForDay = false;						

					if (firstRowForDay && lastRowForDay){
						break;
					}


					if (firstRowForDay){
						open = price;
						open_delayed =  data[r+1][2];
						if (r!=0){
							previousClose = data[r-1][2];
							high = previousClose;
							low = previousClose;
						}
						else {
							previousClose = open;		//sketchy
							high = open;
							low = open;
						}
					}
					if (lastRowForDay){
						close = price;
						close_early =  data[r-1][2];
					}


					//TODO
//										close_early = close;

					if ((goneLong || goneShort) && !lastRowForDay){	//already picked a position AND it's not the end of the day
						continue;
					}			

					if (lastRowForDay){		//end of day

						if (goneShort){

							System.out.format(" open:%.2f, buy:%.3f, close:%.3f, change:%.3f\n",  open, shortBuy, close_early, (close_early - shortBuy)/shortBuy);
							shortSaleChangeSum = shortSaleChangeSum + (close_early - shortBuy)/shortBuy;
							shortBalance = shortBalance + p*(shortBuy - close_early)/shortBuy - fee;
							shortCount++;

						}
						if (goneLong){

							System.out.format(" open:%.2f, buy:%.3f, close:%.3f, change:%.3f\n", open, longBuy, close_early, (close_early - longBuy)/longBuy);
							longSaleChangeSum = longSaleChangeSum + (close_early - longBuy)/longBuy;
							longBalance = longBalance + p*(close_early - longBuy)/longBuy - fee;
							longCount++;

						}
						goneLong = false;
						goneShort = false;
					}

					//TODO
//										priceDelayed = price-0.00001;

					if (time < 1530 && !lastRowForDay && r !=0 ){
						double realPriceChange = 0;
						double initialPrice = 0.69696969;

						if (price > high){
							high = price;

							//TODO
							priceDelayed = data[r+1][2];

							if (firstRowForDay){
								initialPrice 	= previousClose;
							}
							else{ 
								initialPrice 	= low;	//TODO changed from open -- low
							}
							priceChange = (price - initialPrice) / initialPrice;
							realPriceChange = (priceDelayed - initialPrice) / initialPrice;

							if (price != priceDelayed && priceChange > changeLimit && realPriceChange > changeLimit){

								if (!goneShort){
									System.out.println(theSymbol);
									shortBuy = priceDelayed;	//delay purchase time by a minute
									System.out.format("day %.0f, time %.0f, short change: %.2f, ", day, time, realPriceChange);//, initialPrice, low, previousClose);
									goneShort = true;
								}
							}
						}

						if (price < low){
							low = price;

							//TODO
							priceDelayed = data[r+1][2];

							if (firstRowForDay)
								initialPrice 	= previousClose;
							else 
								initialPrice 	= high;	//TODO changed from open -- high

							priceChange = (price - initialPrice) / initialPrice;
							realPriceChange = (priceDelayed - initialPrice) / initialPrice;

							if (price != priceDelayed && -1*priceChange > changeLimit && -1*realPriceChange > changeLimit ){

								if (!goneLong){
									System.out.println(theSymbol);
									longBuy = priceDelayed;	//delay purchase time by a minute
									System.out.format("day %.0f, time %.0f,  long change: %.2f, ", day, time, realPriceChange);
									goneLong = true;
								}
							}
						}
					}
				}

			}catch (IOException e){System.out.println(e);}
		}
		System.out.format("%.3f, %d, %.3f, %.3f, %d, %.3f\n", 
				shortBalance, shortCount, (shortSaleChangeSum/shortCount),
				longBalance, longCount, (longSaleChangeSum/longCount));

		//		System.out.println(shortBalance +", "+ shortCount  +", "+ (shortSaleChangeSum/shortCount)+", "+ longBalance +", "+ longCount +", "+ (longSaleChangeSum/longCount));
	}
}