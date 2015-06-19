package trash;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//24 hour test is running now ... profit is way too high.  something is wrong :(

@Deprecated
public class GoogleWorldMinuteCruncher {

	static double []					priceMin_	= new double[]	{ 0, 0.01, 0.1, 1, 5, 10, 50	};	
	static double []					priceMax_	= new double[]	{ 999999, 10, 1 	};
	static double []					changeMin_ 	= new double[]	{ -100, -1, -0.8, -0.6, -0.3, -0.1, -0.05	};
	static double []					changeMax_ 	= new double[]	{ 1000, 0, -0.05, -0.1, -0.2, -0.3, -0.4, -0.5 		};
	static int 	[]							delay_ 	= new int [] 	{ 0, 1, 2 		};			//0 or 1 or 2
	static int  [] 	minutes_before_close_to_buy_ 	= new int []	{ 30, 120 		};

	static int COUNT_INDEX 		= 0;
	static int OPEN_COUNT_INDEX	= 1;
	static int DAY_COUNT_INDEX 	= 2;
	static int SCSum_INDEX 		= 3;
	static int OPEN_SCSum_INDEX	= 4;
	static int DAY_SCSum_INDEX	= 5;

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
		String c = ",";
		tableFileOut.println("pMin,pMax,chMin,chMax,fee,del,spd,minPreEndBuy,ct,avPctCh,oCt,oAvPctCh,dCt,dAvPctCh,bal,minBal," +
				"AMS_tCt,AMS_tCh,AMS_oCt,AMS_oCh,AMS_dCt,AMS_dCh," +
				"ASX_tCt,ASX_tCh,ASX_oCt,ASX_oCh,ASX_dCt,ASX_dCh," +
				"BIT_tCt,BIT_tCh,BIT_oCt,BIT_oCh,BIT_dCt,BIT_dCh," +
				"EBR_tCt,EBR_tCh,EBR_oCt,EBR_oCh,EBR_dCt,EBR_dCh," +
				"EPA_tCt,EPA_tCh,EPA_oCt,EPA_oCh,EPA_dCt,EPA_dCh," +
				"FRA_tCt,FRA_tCh,FRA_oCt,FRA_oCh,FRA_dCt,FRA_dCh," +
				"HKG_tCt,HKG_tCh,HKG_oCt,HKG_oCh,HKG_dCt,HKG_dCh," +
				"LON_tCt,LON_tCh,LON_oCt,LON_oCh,LON_dCt,LON_dCh," +
				"NASDAQ_tCt,NASDAQ_tCh,NASDAQ_oCt,NASDAQ_oCh,NASDAQ_dCt,NASDAQ_dCh," +
				"NSE_tCt,NSE_tCh,NSE_oCt,NSE_oCh,NSE_dCt,NSE_dCh," +
				"NYSE_tCt,NYSE_tCh,NYSE_oCt,NYSE_oCh,NYSE_dCt,NYSE_dCh," +
				"OTCMKTS_tCt,OTCMKTS_tCh,OTCMKTS_oCt,OTCMKTS_oCh,OTCMKTS_dCt,OTCMKTS_dCh," +
				"SGX_tCt,SGX_tCh,SGX_oCt,SGX_oCh,SGX_dCt,SGX_dCh," +
				"TSE_tCt,TSE_tCh,TSE_oCt,TSE_oCh,TSE_dCt,TSE_dCh," +
				"TYO_tCt,TYO_tCh,TYO_oCt,TYO_oCh,TYO_dCt,TYO_dCh,");



		File preparedDataFolder 	= new File(dataFolderStr + preparedDataFolderStr);
		File [] preparedDataFiles 	= preparedDataFolder.listFiles();

		Map<String, double []> exchangeResults_blank = new TreeMap<String, double[]>();
		for (File file : preparedDataFiles){
			String str 			= new String(file.getName().replace(".sr", ""));
			String [] filename 	= str.split("_");
			String exchange 	= new String(filename[1]);

			exchangeResults_blank.put(exchange, new double[]{0, 0, 0, 0, 0, 0});
		}

		int totalNumScenarios = priceMin_.length * priceMax_.length * changeMin_.length * 
				changeMax_.length * delay_.length * minutes_before_close_to_buy_.length;
		System.out.println("started. num scenarios: " + totalNumScenarios + ls);

		for (double priceMin : priceMin_){
			for (double priceMax : priceMax_){
				for (double changeMin : changeMin_){
					for (double changeMax : changeMax_){
						for (int delay : delay_){
							for (int minutes_before_close_to_buy : minutes_before_close_to_buy_){

								Map<String, double []> exchangeResults = 
										new TreeMap<String, double[]>(exchangeResults_blank);

										double balance = 0, minBalance = 0, saleChangeSum = 0, openSCS = 0, daySCS = 0;
										int count = 0;
										int open_buy_count = 0, day_buy_count = 0;

										//																	int fileCount = preparedDataFiles.length + 1; 													//System.out.println(fileCount);
										for (File file : preparedDataFiles){
											//																	fileCount--;	if (fileCount < 23_000) break;	
											List<String> priceList = Files.readAllLines(
													file.toPath(), StandardCharsets.UTF_8);							//if (fileCount % 1000 == 0) System.out.println(fileCount);
											String [] filename = file.getName().split("[_\\.]");					//2HR_FRA_540_1200_60_60.sr		\\ // is not necessary?  seems like it should be
											@SuppressWarnings("unused")											String symbol 			= new String(filename[0]);
											String exchange 		= new String(filename[1]);
											String MARKET_OPEN 		= new String(filename[2]);						
											String MARKET_CLOSE 	= new String(filename[3]);	
											@SuppressWarnings("unused")											String INTERVAL 		= new String(filename[4]);
											@SuppressWarnings("unused")											String TIMEZONE_OFFSET 	= new String(filename[5]);

											
											
											
											boolean bought = false, openBought = false;

											int market_duration_minutes = 
													Integer.parseInt(MARKET_CLOSE) - Integer.parseInt(MARKET_OPEN);
											int time, buyRow; 

											double price = 0, prev_price, open = 0, buyPrice = Double.MAX_VALUE;			//so it doens't think open went down from anything

											String day, buyDay;

											int priceListLen = priceList.size();												//ORDER IS IMPORTANT!!! ! ! !
											for (int r = 0; r < priceListLen - delay - 1; r++){ 

												prev_price = price;

												String [] line 	= priceList.get(r).split(",");
												day 			= line[DAY]; 		
												time 			= Integer.parseInt(line[TIME]); 
												price 			= Double.parseDouble(line[PRICE]);

												if (bought && time == 0){										//hold through end of day (triggered by next day).
													double sellPrice 	= prev_price*(1.0 - spreadPenalty);					//prev_price is last price of day
													double saleChange 	= (sellPrice - buyPrice)/buyPrice;
													saleChangeSum 		+= saleChange;
													balance 			+= p*saleChange - 2*fee;
													if (balance < minBalance)
														minBalance = balance;
													count++;

													double xCOUNT 		= exchangeResults.get(exchange)[COUNT_INDEX];
													double xOPEN_COUNT 	= exchangeResults.get(exchange)[OPEN_COUNT_INDEX];
													double xDAY_COUNT 	= exchangeResults.get(exchange)[DAY_COUNT_INDEX];
													double xSCSum 		= exchangeResults.get(exchange)[SCSum_INDEX];
													double xOPEN_SCSum 	= exchangeResults.get(exchange)[OPEN_SCSum_INDEX];
													double xDAY_SCSum 	= exchangeResults.get(exchange)[DAY_SCSum_INDEX];

													xCOUNT++;
													xSCSum += saleChange;

													if (openBought){
														open_buy_count++;
														openSCS += saleChange;
														xOPEN_COUNT++;
														xOPEN_SCSum += saleChange;
													}
													else{
														day_buy_count++;
														daySCS += saleChange;
														xDAY_COUNT++;
														xDAY_SCSum += saleChange;
													}

													double [] exchangeResultsValue = new double[exchangeResults.size()];
													exchangeResultsValue[COUNT_INDEX] 		= xCOUNT;
													exchangeResultsValue[OPEN_COUNT_INDEX] 	= xOPEN_COUNT;
													exchangeResultsValue[DAY_COUNT_INDEX] 	= xDAY_COUNT;
													exchangeResultsValue[SCSum_INDEX] 		= xSCSum;
													exchangeResultsValue[OPEN_SCSum_INDEX] 	= xOPEN_SCSum;
													exchangeResultsValue[DAY_SCSum_INDEX] 	= xDAY_SCSum;

													exchangeResults.put(exchange, exchangeResultsValue);

													bought = false; 													//System.out.format("%5d %12s %4d %10.5f %10.5f %8.3f\n", fileCount, symbol, time, buyPrice, sellPrice, saleChange);
												}

												double priceChange;			

												if (time == 0)															//bought always false when time==0, but maybe will change later
													open = price;

												if (	price > priceMin 	&& 
														price < priceMax 	&& 
														!bought 			&& 
														time < market_duration_minutes - minutes_before_close_to_buy
														){
													if (time == 0)
														priceChange = (open - prev_price)/prev_price;						//prev price is close yesterday when time==0
													else
														priceChange = (price - open)/open;

													if (priceChange > changeMin && priceChange < changeMax){
														bought = true;
														openBought = time==0 ? true : false;

														for (buyRow = r+1; buyRow <= r+delay; buyRow++){		//look for last row where time  <= rowTime + delay AND day = today 
															buyDay = priceList.get(buyRow).split(",")[DAY];
															if (!buyDay.equals(day)){							//ensure not next day
																buyRow--;										//back to r, definitely today.
																break;
															}
															int buyTime = Integer.parseInt(
																	priceList.get(buyRow).split(",")[TIME]);	
															if (buyTime > time + delay){						//if past target buy time, buy at previous row price. we are starting at the row after the change trigger row so there's no risk of going backwards in time.
																buyRow--;									//go back to last row before it was too late to buy
																buyDay = priceList.get(
																		buyRow).split(",")[DAY];
																break;
															}
														}
														buyPrice = Double.parseDouble(priceList.get(buyRow).split(",")[PRICE]);	
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
												fee +c+ delay +c+ spreadPenalty +c+ minutes_before_close_to_buy					+c+ 
												count 																			+c+ 
												(count == 0 ? "-" : df.format(saleChangeSum/count)) 							+c+ 
												open_buy_count 																	+c+
												(open_buy_count == 0 ? "-" : df.format(openSCS/open_buy_count)) 				+c+
												day_buy_count 																	+c+
												(day_buy_count == 0 ? "-" : df.format(daySCS/day_buy_count)) 					+c+
												balance_df.format(balance) +c+ balance_df.format(minBalance)  															+c);

										for (String key : exchangeResults.keySet()){
											double [] xValues = exchangeResults.get(key);
											double xCOUNT = xValues[COUNT_INDEX];
											double xOPEN_COUNT = xValues[OPEN_COUNT_INDEX];
											double xDAY_COUNT = xValues[DAY_COUNT_INDEX];
											double xSCSum = xValues[SCSum_INDEX];
											double xOPEN_SCSum = xValues[OPEN_SCSum_INDEX];
											double xDAY_SCSum = xValues[DAY_SCSum_INDEX];																

											tableFileOut.print(
													(int) Math.round(xCOUNT)										+c+
													(xCOUNT == 0 ? "-" : df.format(xSCSum/xCOUNT))					+c+
													(int) Math.round(xOPEN_COUNT)									+c+
													(xOPEN_COUNT == 0 ? "-" : df.format(xOPEN_SCSum/xOPEN_COUNT))	+c+
													(int) Math.round(xDAY_COUNT)									+c+
													(xDAY_COUNT == 0 ? "-" : df.format(xDAY_SCSum/xDAY_COUNT))		+c); 
										}
										tableFileOut.print(ls);

										sb.append(String.format("%24s  %s %s %n%24s %s %s %n%24s  %d %d %.2f%n",
												"Price min, max:",
												(priceMin == (int) priceMin ? String.valueOf((int) priceMin) : priceMin),
												(priceMax == (int) priceMax ? String.valueOf((int) priceMax) : priceMax),
												"Change min, max:",
												(changeMin == (int) changeMin ? String.valueOf((int) changeMin) : changeMin),	
												(changeMax == (int) changeMax ? String.valueOf((int) changeMax) : changeMax),
												"fee, delay, spread:", 		fee, delay, spreadPenalty));
										sb.append(String.format("%24s  %d%n",
												"min pre-close to buy:", minutes_before_close_to_buy));
										sb.append(String.format("%24s  %d, %s %n",
												"count, ave prc chg:", 
												count, 
												count == 0 ? "-" : df.format(saleChangeSum/count)));
										sb.append(String.format("%24s  %d, %s %n",
												"open count, ave prc chg:", 
												open_buy_count, 
												open_buy_count == 0 ? "-" : df.format(openSCS/open_buy_count)));
										sb.append(String.format("%24s  %d, %s %n",
												"day count, ave prc chg:", 
												day_buy_count, 
												day_buy_count == 0 ? "-" : df.format(daySCS/day_buy_count)));
										sb.append(String.format("%24s  %.2f %.2f%n",
												"balance, min balance:", balance, minBalance));

										sb.append("exchange ----------------------------------------"); sb.append(ls);
										for (String key : exchangeResults.keySet()){
											double [] xValues = exchangeResults.get(key);
											double xCOUNT = xValues[COUNT_INDEX];
											double xOPEN_COUNT = xValues[OPEN_COUNT_INDEX];
											double xDAY_COUNT = xValues[DAY_COUNT_INDEX];
											double xSCSum = xValues[SCSum_INDEX];
											double xOPEN_SCSum = xValues[OPEN_SCSum_INDEX];
											double xDAY_SCSum = xValues[DAY_SCSum_INDEX];																

											sb.append(String.format("%8s:  %6d %7s %6d %7s %6d %7s%n",
													key, 
													(int) Math.round(xCOUNT),
													(xCOUNT == 0 ? "-" : df.format(xSCSum/xCOUNT)),
													(int) Math.round(xOPEN_COUNT),
													(xOPEN_COUNT == 0 ? "-" : df.format(xOPEN_SCSum/xOPEN_COUNT)),
													(int) Math.round(xDAY_COUNT),
													(xDAY_COUNT == 0 ? "-" : df.format(xDAY_SCSum/xDAY_COUNT)))); 
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
		System.out.println(ls+"Exchange data in the form <count pct> in three columns: cumulative, open, day"+ls);
		System.out.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );
		blocksFileOut.println(ls+"Exchange data in the form <count pct> in three columns: cumulative, open, day"+ls);
		blocksFileOut.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );

		blocksFileOut.close();
		tableFileOut.close();
	}
}

