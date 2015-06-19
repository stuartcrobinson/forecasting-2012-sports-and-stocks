package trash;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GoogleWorldMinuteCruncher_day_re_close {

	static int 	[]							delay_ 	= new int [] 	{  1 };			//0 or 1 or 2
	static double []					priceMin_	= new double[]	{ 0.01, 0.1, 1, 5, 10 };	
	static double []					priceMax_	= new double[]	{ 999999 };
	static double []					changeMin_ 	= new double[]	{ -1 };		//make sure -1 and -100.  stock cant fall more than 100%.  should keep this at -0.5 since everything lower than that is probably data flaw
	static double []					changeMax_ 	= new double[]	{ -0.05, -0.1, -0.15, -0.2, -0.25, -0.3, -0.35, -0.4, -0.5, -0.6, -0.7, -0.8, -0.9, -1	};

	static int COUNT_INDEX 		= 0;
	static int SCSum_INDEX 		= 1;

	static int DAY 	= 0;
	static int TIME = 1;
	static int PRICE = 2;

	static double p = 1000;
	static int fee = 1;
	static double spreadPenalty = 0.02;

	public static void main(String[] args) throws Exception {
		final long startTime = System.currentTimeMillis();
		String ls = System.getProperty("line.separator");
		Date dNow = new Date( );
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd hh.mm.ss a");
		String dateStr = ft.format(dNow);

		String dataFolderStr = "C:\\Users\\User\\Documents\\" +
				"stocks\\data\\minutely\\2013.11.29 05.26 PM complete\\";

		String preparedDataFolderStr = "preparedData 2013.11.30 03.55 PM";

		String javaResultsFolderStr	= dataFolderStr + preparedDataFolderStr + " crunched\\";
		File javaResultsFolder 		= new File(javaResultsFolderStr);
		javaResultsFolder.mkdir();
		PrintStream blocksFileOut 	= new PrintStream(new File(javaResultsFolderStr + dateStr +".sr"));
		PrintStream tableFileOut 	= new PrintStream(new File(javaResultsFolderStr + dateStr +".csv"));

		try {

			ArrayList<String> exchangesToAnalyze = new ArrayList<String>(Arrays.asList(
					"AMS",
					"ASX",
					"BIT",
					"EBR",
					"EPA",
					"FRA",
					"HKG",
					"LON",
					"NASDAQ",
					"NSE",
					"NYSE",
					"OTCMKTS",
					"SGX",
					"TSE",
					"TYO"
					));

			String c = ",";
			tableFileOut.print("pMin,pMax,chMin,chMax,del,ct,pct,bal,minBal,");
			for (String x : exchangesToAnalyze)
				tableFileOut.print(x +"_n," + x + "_pct,");
			tableFileOut.print(ls);


			File preparedDataFolder 	= new File(dataFolderStr + preparedDataFolderStr);
			File [] preparedDataFiles 	= preparedDataFolder.listFiles();

			Map<String, double []> exchangeResults_blank 	= new TreeMap<String, double[]>();
			Map<String, double[][]> dataMap 				= new TreeMap<String, double[][]>();

			System.out.print("processing "+ preparedDataFiles.length +" prepared data files...");
			for (File file : preparedDataFiles){

				String str 			= new String(file.getName().replace(".sr", ""));
				String [] filename 	= str.split("_");
				String exchange 	= new String(filename[1]);

				if (!exchangesToAnalyze.contains(exchange)) continue;

				exchangeResults_blank.put(exchange, new double[]{0, 0, 0, 0, 0, 0});

				List<String> priceList = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);	
				int priceListLen = priceList.size();

				double[][] priceSet = new double[priceListLen][3];

				for (int i = 0; i < priceListLen; i++){
					String row = priceList.get(i);

					String [] line 	= row.split(",");

					double day 			= Double.parseDouble(line[DAY]); 		
					double time 		= Double.parseDouble(line[TIME]); 
					double price 		= Double.parseDouble(line[PRICE]);

					priceSet[i][DAY] = day;
					priceSet[i][TIME] = time;
					priceSet[i][PRICE] = price;
				}
				dataMap.put(file.getName(), priceSet);
			}
			System.out.print("done. read " + dataMap.size() + " out of " + preparedDataFiles.length + ls);

			int totalNumScenarios = priceMin_.length * priceMax_.length * changeMin_.length * 
					changeMax_.length * delay_.length;
			System.out.println("started. num scenarios: " + totalNumScenarios + ls);

			int counter = 0; 

			for (int delay : delay_){
				for (double priceMin : priceMin_){
					for (double priceMax : priceMax_){
						for (double changeMin : changeMin_){
							for (double changeMax : changeMax_){
								counter++; System.out.println(counter);															//if (counter < 881) continue;

								if (priceMin > priceMax) continue;
								if (changeMin > changeMax) continue;

								Map<String, double []> exchangeResults = new TreeMap<String, double[]>(exchangeResults_blank);

								double balance = 0, minBalance = 0, saleChangeSum = 0;
								int count = 0;

								for (String key : dataMap.keySet()){
									double [][] priceSet = dataMap.get(key);

									String [] filename = key.split("[_\\.]");												//2HR_FRA_540_1200_60_60.sr		\\ // is not necessary?  seems like it should be
									//																									String symbol 			= new String(filename[0]);
									String exchange 		= new String(filename[1]);
									String MARKET_OPEN 		= new String(filename[2]);						
									String MARKET_CLOSE 	= new String(filename[3]);	
									//																									String INTERVAL 		= new String(filename[4]);
									//																									String TIMEZONE_OFFSET 	= new String(filename[5]);
									int market_duration_minutes = Integer.parseInt(MARKET_CLOSE) - Integer.parseInt(MARKET_OPEN);

									int lastSellTime = 0;														//minutes before close that you can still sell
									//									if (
									//											exchange.equals("AMS") ||
									//											exchange.equals("BIT") ||
									//											exchange.equals("EBR") ||
									//											exchange.equals("EPA") ||
									//											exchange.equals("LON"))		lastSellTime = 120;						//liquify before america opens
									//									if (exchange.equals("FRA"))			lastSellTime = 360;
									//									if (exchange.equals("HKG"))			lastSellTime = 10;						//hkg closes and lon opens at 8 UTC


									if ( lastSellTime >= market_duration_minutes ) throw new Exception("selling before mkt opens wtf");									//these values are countdowns from close.

									boolean bought 	 = false;
									int 	buyRow 	 = -1;
									double 	buyPrice = -1;	
									double	high	 = -1;
									int priceListLen = priceSet.length;	
									for (int r = 0; r < priceListLen - delay - 1; r++){ 

										double time 	= priceSet[r][TIME]; 
										double day 		= priceSet[r][DAY];		
										double price 	= priceSet[r][PRICE];


										if (time == 0)
											high = price; 
										
										if (price > high)
											high = price;

										
										if ( r > 0){						//just ignore the first day of the set.  14 instead of 15, whatever



											/**  Sell  **/
											if (bought && time == 0){	
												double sellPrice = -1, prevDay = priceSet[r-1][DAY];

												if (buyRow == -1) 						throw new Exception ("buyRow is wrong");					//sellPrice = buyPrice *(1.0 - spreadPenalty);
												if (buyRow == r) 						throw new Exception ("r == buyRow");
												if (buyRow > r) 						throw new Exception ("buyRow > r");
												if (priceSet[buyRow][DAY] != prevDay) 	throw new Exception ("buyRow not in prev day WTF");

												for (int i = r - 1; i >= buyRow; i--){																//	look backwards for time it would have been sold.  scroll back through data to get latest time in day that is earlier than minutes_before_close_to_sell minutes before close (so we can get money back to invest in US)
													if ( 	priceSet[i][DAY] == prevDay &&
															priceSet[i][TIME] <= market_duration_minutes - lastSellTime ){
														sellPrice = priceSet[i][PRICE]*(1.0 - spreadPenalty);
														break;
													}
													if ( 	priceSet[i][TIME] > market_duration_minutes - lastSellTime ){
														sellPrice = priceSet[buyRow][PRICE]*(1.0 - spreadPenalty);
														break;
													}
												}
												if (sellPrice == -1) 
													throw new Exception ("sellPrice is wrong");					//sellPrice = buyPrice *(1.0 - spreadPenalty);
												if (buyPrice == -1) throw new Exception ("buyPrice is wrong");					//sellPrice = buyPrice *(1.0 - spreadPenalty);

												double saleChange 	= (sellPrice - buyPrice)/buyPrice;
												saleChangeSum 		+= saleChange;
												balance 			+= p*saleChange - 2*fee;
												if (balance < minBalance)
													minBalance = balance;
												count++;

												double xCOUNT 	= exchangeResults.get(exchange)[COUNT_INDEX];
												double xSCSum 	= exchangeResults.get(exchange)[SCSum_INDEX];

												xCOUNT++;
												xSCSum += saleChange;

												double [] exchangeResultsValue = new double[2];
												exchangeResultsValue[COUNT_INDEX] 		= xCOUNT;
												exchangeResultsValue[SCSum_INDEX] 		= xSCSum;

												exchangeResults.put(exchange, exchangeResultsValue);

												bought = false; 																					//keep this:		//System.out.format("%5d %12s %4d %10.5f %10.5f %8.3f\n", fileCount, symbol, time, buyPrice, sellPrice, saleChange);
											}



											/**  Buy  **/
											if ( !bought && price > priceMin && price < priceMax ){			

												double close_yest 	= priceSet[r-1][PRICE];	
												double priceChange	= (price - close_yest)/close_yest;

												if (priceChange > changeMin && priceChange < changeMax){

													int potentialBuyRow = r;
													while (	potentialBuyRow < priceListLen	){
														if ( priceSet[potentialBuyRow][DAY] != day ){
															potentialBuyRow--;
															break;
														}
														if ( priceSet[potentialBuyRow][TIME] > time + delay ){
															potentialBuyRow--;
															break;
														}
														potentialBuyRow++;
													}

													bought = true;
													buyRow = potentialBuyRow;
													buyPrice = priceSet[buyRow][PRICE];	
												}
											}
										}
									}
								}
								DecimalFormat balance_df = new DecimalFormat("0.00");
								DecimalFormat df = new DecimalFormat("0.0000");
								StringBuilder sb = new StringBuilder();

								tableFileOut.print(
										(priceMin == (int) priceMin ? String.valueOf((int) priceMin) : priceMin) 		+c+
										(priceMax == (int) priceMax ? String.valueOf((int) priceMax) : priceMax) 		+c+
										(changeMin == (int) changeMin ? String.valueOf((int) changeMin) : changeMin) 	+c+ 
										(changeMax == (int) changeMax ? String.valueOf((int) changeMax) : changeMax) 	+c+ 
										delay																			+c+
										count 																			+c+ 
										(count == 0 ? "" : df.format(saleChangeSum/count)) 								+c+ 

										balance_df.format(balance) +c+ balance_df.format(minBalance)  					+c);

								for (String key : exchangeResults.keySet()){
									double [] xValues = exchangeResults.get(key);
									double xCOUNT = xValues[COUNT_INDEX];
									double xSCSum = xValues[SCSum_INDEX];

									tableFileOut.print(
											(int) Math.round(xCOUNT)										+c+
											(xCOUNT == 0 ? "" : df.format(xSCSum/xCOUNT))					+c); 
								}
								tableFileOut.print(ls);

								sb.append(String.format("%24s:  %s %s %n%24s: %s %s %n%24s:  %d %d %.2f%n",
										"Price min, max",
										(priceMin == (int) priceMin ? String.valueOf((int) priceMin) : priceMin),
										(priceMax == (int) priceMax ? String.valueOf((int) priceMax) : priceMax),
										"Change min, max:",
										(changeMin == (int) changeMin ? String.valueOf((int) changeMin) : changeMin),	
										(changeMax == (int) changeMax ? String.valueOf((int) changeMax) : changeMax),
										"fee, delay, spread", 		fee, delay, spreadPenalty));
								sb.append(String.format("%24s:  %.2f %.2f%n",
										"balance, min balance", balance, minBalance));
								sb.append("-------------------------------------------"); sb.append(ls);

								sb.append(String.format("%24s:  %6d %7s%n",
										"tot count, ave prc chg", 
										count, 
										count == 0 ? "" : df.format(saleChangeSum/count)));

								for (String key : exchangeResults.keySet()){
									double [] xValues = exchangeResults.get(key);
									double xCOUNT = xValues[COUNT_INDEX];
									double xSCSum = xValues[SCSum_INDEX];

									sb.append(String.format("%24s:  %6d %7s%n",
											key, 
											(int) Math.round(xCOUNT),
											(xCOUNT == 0 ? "" : df.format(xSCSum/xCOUNT)))); 
								}
								sb.append(ls + totalNumScenarios-- +". "+ 
										(System.currentTimeMillis() - startTime)/1000.0 +" seconds" +ls);
								sb.append("_____________________________________________________" + ls);
								System.out.println(sb.toString());
								blocksFileOut.println(sb.toString());
							}
						}
					}
				}
			}
			System.out.println(ls+"Exchange data in the form <count pct> in three columns: cumulative, open, day"+ls);
			System.out.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );
			blocksFileOut.println(ls+"Exchange data in the form <count pct> in three columns: cumulative, open, day"+ls);
			blocksFileOut.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );
		}
		finally{
			blocksFileOut.close();
			tableFileOut.close();
		}

	}
}

