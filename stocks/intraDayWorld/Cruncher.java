package intraDayWorld;

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

//untested at save.  going to rewrite with totally different logic.  more realistic next time.


//get bid ask from yahoo web csv thing.  modify this so that the sell price is shrunk by the real bid ask spread (whatever's most recent).
// this will be very telling.  probably all profit will disappear.

//then write web scraper to get bid ask from http://www.otcmarkets.com/stock/zonmy/quote.  this will take a really long time.  but easy to code
//since symbol is in URL and the bid-ask price is always this xpath:    //*[@id='priceChgBBO']/div/div[2]/span[1]  text is like this: 21.20 / 21.43 



public class Cruncher {

	static double []	priceMin_			= new double[]	{ 10 }; //0.01, 0.1, 1, 5, 10 };	
	static double []	priceMax_			= new double[]	{ 999999 };
	static double []	changeMin_ 			= new double[]	{ -1 };		//make sure -1 and -100.  stock cant fall more than 100%.  should keep this at -0.5 since everything lower than that is probably data flaw
	static double []	changeMax_ 			= new double[]	{ -0.05, -0.1, -0.15, -0.2, -0.25, -0.3, -0.35, -0.4, -0.5, -0.6, -0.7, -0.8, -0.9, -1	};
	//pct up from signal
	static double []	buyLimitMax_		= new double[]	{ 0.005, 2 };
	//	//pct up from buy	
	//	static double []	sellLimitMin_		= new double[]	{ -1, 0.01, 0.03, 0.05, 0.07, 0.09, 0.11, 0.15, 0.20, 0.30, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1, 1.2, 1.4, 1.6, 1.8, 2, 2.5, 3 };	//-1 is to sell at any price. 
	//from end 
	static double []	sellStartPreClose_	= new double[]	{ 120 }; //-0.05, -0.1, -0.15, -0.2, -0.25, -0.3, -0.35, -0.4, -0.5, -0.6, -0.7, -0.8, -0.9, -1	};
	//from end
	static double []	sellEndPreClose_	= new double[]	{ 0 };	//0 means sell at close regardless of price (if there are no available prices in this time period, hold to the next day, but flag this as a time-sale failure)

	static int 		buyOrderExpireMinute 	= 120;
	static boolean 	sell_off_early			= false;
	static boolean 	displayDetails 			= false;

	/** indices for exchangeResults **/
	static int COUNT 			= 0;
	static int SCS 				= 1;																													//sale change sum = scs
	/** indieces for priceSet **/
	static int DAY 				= 0;
	static int TIME 			= 1;
	static int PRICE 			= 2;
	/** indices for prepared data filename (when filename is split into an array) **/
	static int SYMBOL			= 0;
	static int EXCHANGE			= 1;
	static int MARKET_OPEN		= 2;
	static int MARKET_CLOSE		= 3;
	static int INTERVAL			= 4;
	static int TIMEZONE_OFFSET	= 5;

	static double 	p 				= 1000;
	static int 		fee 			= 1;
	static double 	spreadHandicap 	= 0.02;

	public static void main(String[] args) throws Exception {
		final long startTime = System.currentTimeMillis();
		String ls = System.getProperty("line.separator"), c = ",";
		String dateStr = (new SimpleDateFormat ("yyyy.MM.dd hh.mm.ss a")).format(new Date());

		String dataFolderStr = "C:\\Users\\User\\Documents\\" +
				"stocks\\data\\minutely\\2013.11.29 05.26 PM complete\\";

		String 	preparedDataFolderStr 	= "preparedData 2013.11.30 03.55 PM";
		String 	javaResultsFolderStr	= dataFolderStr + preparedDataFolderStr + " crunched\\";
		File 	javaResultsFolder 		= new File(javaResultsFolderStr);
		javaResultsFolder.mkdir();
		PrintStream blocksFileOut 		= new PrintStream(new File(javaResultsFolderStr + dateStr +".sr"));
		PrintStream tableFileOut 		= new PrintStream(new File(javaResultsFolderStr + dateStr +".csv"));

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
					"OTCMKTS"
					,
					"SGX",
					"TSE",
					"TYO"
					));

			tableFileOut.print("pMin,pMax,chMin,chMax,bLim,sStart,sEnd,err,n,pct,bal,minBal,");							//err is overnightHoldCount
			for (String x : exchangesToAnalyze)
				tableFileOut.print(x +"_n," + x + "_pct,");
			tableFileOut.print(ls);

			File preparedDataFolder 	= new File(dataFolderStr + preparedDataFolderStr);
			File [] preparedDataFiles 	= preparedDataFolder.listFiles();

			/** key is exchange symbol **/
			Map<String, double []> exchangeResults_blank 	= new TreeMap<String, double[]>();
			/** key is filename **/
			Map<String, double[][]> dataMap 				= new TreeMap<String, double[][]>();

			System.out.println("processing "+ preparedDataFiles.length +" prepared data files...");
			for (File file : preparedDataFiles){

				String str 			= new String(file.getName().replace(".sr", ""));
				String [] filename 	= str.split("_");
				String exchange 	= new String(filename[1]);

				if (!exchangesToAnalyze.contains(exchange)) continue;

				exchangeResults_blank.put(exchange, new double[]{0, 0});

				List<String> priceList 	= Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);	
				int priceListLen 		= priceList.size();
				double[][] priceSet 	= new double[priceListLen][3];

				for (int i = 0; i < priceListLen; i++){
					String [] line 		= priceList.get(i).split(",");
					priceSet[i][DAY] 	= Double.parseDouble(line[DAY]);
					priceSet[i][TIME] 	= Double.parseDouble(line[TIME]); 
					priceSet[i][PRICE] 	= Double.parseDouble(line[PRICE]);
				}
				dataMap.put(file.getName(), priceSet);
			}
			int totalNumScenarios = 
					priceMin_.length * priceMax_.length * 
					changeMin_.length * changeMax_.length * 
					buyLimitMax_.length * //sellLimitMin_.length * 
					sellStartPreClose_.length * sellEndPreClose_.length;	
			int counter = 0; 
			for (double priceMin : priceMin_){
				for (double priceMax : priceMax_){
					for (double changeMin : changeMin_){
						for (double changeMax : changeMax_){
							for (double buyLimitMax : buyLimitMax_){
								//								for (double sellLimitMin : sellLimitMin_){
								for (double sellStartPreClose : sellStartPreClose_){
									for (double sellEndPreClose : sellEndPreClose_){
										counter++; System.out.println(counter);															//if (counter < 881) continue;

										if (priceMin > priceMax) 					continue;
										if (changeMin > changeMax)	 				continue;
										if (sellStartPreClose < sellEndPreClose) 	continue;															//these count back from the end

										Map<String, double []> exchangeResults = new TreeMap<String, double[]>(exchangeResults_blank);

										double 	balance 	= 0, minBalance = 0, saleChangeSum 		= 0;
										int 	fileCount 	= 0, buyCount 	= 0, overnightHoldCount = 0;

										for (String key : dataMap.keySet()){
											fileCount++;
											double [][] priceSet = dataMap.get(key);

											String [] filename 	= key.split("[_\\.]");																//2HR_FRA_540_1200_60_60.sr		\\ // is not necessary?  seems like it should be
											String symbol 		= new String(filename[SYMBOL]);
											String exchange 	= new String(filename[EXCHANGE]);
											String market_open 	= new String(filename[MARKET_OPEN]);
											String market_close = new String(filename[MARKET_CLOSE]);	

											int market_duration_minutes = Integer.parseInt(market_close) - Integer.parseInt(market_open);

											if (sell_off_early){
												if (
														exchange.equals("AMS") ||
														exchange.equals("BIT") ||
														exchange.equals("EBR") ||
														exchange.equals("EPA") ||
														exchange.equals("LON"))		sellEndPreClose = 120;													//liquify before america opens
												if (exchange.equals("FRA"))			sellEndPreClose = 360;	
												if (exchange.equals("HKG"))			sellEndPreClose = 10;												//hkg closes and lon opens at 8 UTC
											}
											if ( sellEndPreClose >= market_duration_minutes ) throw new Exception("selling before mkt opens wtf");									//these values are countdowns from close.
											double 	sellStartTime 	= market_duration_minutes - sellStartPreClose;
											double 	sellEndTime		= market_duration_minutes - sellEndPreClose;
											int 	priceSetLen		= priceSet.length;	
											double 	lastDay			= priceSet[priceSetLen-1][DAY];

											for (int r = 0; r < priceSetLen; r++){ 

												double time 	= priceSet[r][TIME]; 
												double day 		= priceSet[r][DAY];		
												double price 	= priceSet[r][PRICE];

												if (time == 0 && day > 0 && day < lastDay){
													int 	buyRow 	= -1;	
													int		sellRow	= -1;
													double 	open 	= price;

													/**  purhcase  **/
													if ( price > priceMin && price < priceMax && r > sellRow){			

														double close_yest 	= priceSet[r-1][PRICE];	
														double priceChange	= (open - close_yest)/close_yest;

														if (priceChange > changeMin && priceChange < changeMax){

															/** buy **/
															for (int i = r + 1; i < priceSetLen; i++){													//i is potential buy row//issue buy limit order for when price is at most buyLimitMax percent above open.  should expire within two hour? test this				
																if ((priceSet[i][PRICE] - price)/price < buyLimitMax	&&	 
																		priceSet[i][DAY] == day							&&
																		priceSet[i][TIME] < buyOrderExpireMinute){
																	buyRow = i;
																	break;
																}
															}if (buyRow == -1) continue;																//limit order expired.  try again tomorrow.

															double buyPrice = priceSet[buyRow][PRICE];	

															/** sell **/
															for (int i = buyRow+1; i < priceSetLen; i++){													//i is potential sell row
																if (priceSet[i][TIME] > sellEndTime		|| 		priceSet[i][DAY] != day){								//flag this error.  sell immediately
																	sellRow = i;
																	overnightHoldCount++;
																	break;
																}
																if (priceSet[i][TIME] > sellStartTime){												//&&		priceSet[i][PRICE] > buyPrice*(1 + sellLimitMin)){
																	sellRow = i;
																	break;
																}
															}if (sellRow == -1) throw new Exception("sellRow is wrong");

															double sellPrice 	= priceSet[sellRow][PRICE]*(1.0 - spreadHandicap);

															/** calculate performace **/
															double saleChange 	= (sellPrice - buyPrice)/buyPrice;
															saleChangeSum 		+= saleChange;
															balance 			+= p*saleChange - 2*fee;
															buyCount++;
															if (balance < minBalance)	minBalance = balance;

															double xCOUNT 	= exchangeResults.get(exchange)[COUNT];
															double xSCSum 	= exchangeResults.get(exchange)[SCS];

															xCOUNT++;
															xSCSum += saleChange;

															double [] exchangeResultsValue 		= new double[2];
															exchangeResultsValue[COUNT]	= xCOUNT;
															exchangeResultsValue[SCS] 	= xSCSum;

															exchangeResults.put(exchange, exchangeResultsValue);

															if (displayDetails) System.out.format("%5d %12s %7s %3.0f %2.0f %11.6f %11.6f %6.3f\n", 
																	fileCount, symbol, exchange, day-1, time, buyPrice, sellPrice, saleChange);
														}
													}
												}
											}
										}
										DecimalFormat balance_df = new DecimalFormat("0.00");
										DecimalFormat df = new DecimalFormat("0.0000");
										StringBuilder sb = new StringBuilder();

										tableFileOut.print(
												(priceMin 			== (int) priceMin ? String.valueOf((int) priceMin) : priceMin) 								+c+
												(priceMax 			== (int) priceMax ? String.valueOf((int) priceMax) : priceMax) 								+c+
												(changeMin 			== (int) changeMin ? String.valueOf((int) changeMin) : changeMin) 							+c+ 
												(changeMax 			== (int) changeMax ? String.valueOf((int) changeMax) : changeMax) 							+c+ 
												(buyLimitMax 		== (int) buyLimitMax ? String.valueOf((int) buyLimitMax) : buyLimitMax) 					+c+ 
												//													(sellLimitMin 		== (int) sellLimitMin ? String.valueOf((int) sellLimitMin) : sellLimitMin) 					+c+ 
												(sellStartPreClose 	== (int) sellStartPreClose ? String.valueOf((int) sellStartPreClose) : sellStartPreClose) 	+c+ 
												(sellEndPreClose 	== (int) sellEndPreClose ? String.valueOf((int) sellEndPreClose) : sellEndPreClose) 		+c+ 
												overnightHoldCount +c+ buyCount 																				+c+ 
												(buyCount == 0 ? "" : df.format(saleChangeSum/buyCount)) 														+c+ 
												balance_df.format(balance) +c+ balance_df.format(minBalance)  													+c);

										for (String key : exchangeResults.keySet()){
											double [] xValues = exchangeResults.get(key);
											double xCOUNT = xValues[COUNT];
											double xSCSum = xValues[SCS];

											tableFileOut.print(
													(int) Math.round(xCOUNT)										+c+
													(xCOUNT == 0 ? "" : df.format(xSCSum/xCOUNT))					+c); 
										}
										tableFileOut.print(ls);

										sb.append(String.format("%27s:  %s %s %n%27s: %s %s %n",
												"Price min, max",
												(priceMin == (int) priceMin ? String.valueOf((int) priceMin) : priceMin),
												(priceMax == (int) priceMax ? String.valueOf((int) priceMax) : priceMax),
												"Change min, max:",
												(changeMin == (int) changeMin ? String.valueOf((int) changeMin) : changeMin),	
												(changeMax == (int) changeMax ? String.valueOf((int) changeMax) : changeMax)));

										sb.append(String.format("%27s:  %s %n%27s: %s %s %n",
												"buy lim max:",
												(buyLimitMax == (int) buyLimitMax ? String.valueOf((int) buyLimitMax) : buyLimitMax),
												//													(sellLimitMin == (int) sellLimitMin ? String.valueOf((int) sellLimitMin) : sellLimitMin),
												"sell preClose: start, end:",
												(sellStartPreClose == (int) sellStartPreClose ? String.valueOf((int) sellStartPreClose) : sellStartPreClose),	
												(sellEndPreClose == (int) sellEndPreClose ? String.valueOf((int) sellEndPreClose) : sellEndPreClose)));


										sb.append(String.format("%27s:  %d %.2f%n", 
												"fee, spread", 		fee, spreadHandicap));

										sb.append(String.format("%27s:  %s%n", 
												"Sell off early?", String.valueOf(sell_off_early)));
										sb.append(String.format("%27s:  %d%n", 
												"overnightHoldCount", overnightHoldCount));
										sb.append(String.format("%27s:  %.2f %.2f%n",
												"balance, min balance", balance, minBalance));
										sb.append("-------------------------------------------"); sb.append(ls);

										sb.append(String.format("%27s:  %6d %7s%n",
												"tot count, ave prc chg", 
												buyCount, 
												buyCount == 0 ? "" : df.format(saleChangeSum/buyCount)));

										for (String key : exchangeResults.keySet()){
											double [] xValues = exchangeResults.get(key);
											double xCOUNT = xValues[COUNT];
											double xSCSum = xValues[SCS];

											sb.append(String.format("%27s:  %6d %7s%n",
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

