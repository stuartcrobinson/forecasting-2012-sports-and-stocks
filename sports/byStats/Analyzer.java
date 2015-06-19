package byStats;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;


//TODO this is all fucked up.

public class Analyzer {

	static String rInputFileName_TimeStamp = "1349930054306";	//1349930054306<-nba			//wnba: "1349838403333";
//67.7 million combos here
	
	//TODO -- I DON"T HAVE TO CHECK EVERY COMBINATION OF EVERY PARAMETER!!!!!! just check them individually, one at a time, on the ENTIRE games set!  if it will increase pct in a combo parameter, then it will increase pct alone!  no need for this crap to run all night right now!
	//TODO keep the r output that it's generating tonight
	
	final static List<Boolean> 	ignoreOtherSeasons_ 		= Arrays.asList(true);								//r restriction parameters.
	final static List<Integer> 	numTeamPriorGames_ 			= Arrays.asList(10);
	final static List<String> 	decayType_ 					= Arrays.asList("linear");										//"none", "linear", "exponential", "logistic"

	final static List<Boolean> 	useCIs_						= Arrays.asList(true, false);
	final static List<Boolean> 	useTmPs_					= Arrays.asList(true, false);					
	final static List<Boolean> 	useTmDAs_					= Arrays.asList(true, false);				

	final static List<Integer> 	numDecreasing_ 			= Arrays.asList(0, 1, 2, 3);
	final static List<Integer> 	numRestricted_ 			= Arrays.asList(0, 1, 2, 3);

	final static List<Double> 		pMin_ 	= Arrays.asList( 0.0);
	final static List<Double> 		pMax_ 	= Arrays.asList( 1.0, 0.8, 0.6, 0.2, 0.07, 0.02, 0.005);
	final static List<double[]> 	dbMin_ 	= Arrays.asList(	
			new double[] 							{0.0, 0.0},
			new double[] 							{0.5, 1.0},
			new double[] 							{1.0, 2.0},
			new double[] 							{1.5, 3.0},
			new double[] 							{2.0, 4.0});
	final static List<double[]> 	dbMax_ 	= Arrays.asList(
			new double[] 							{900.0, 900.0}, 	
			new double[] 							{50.0, 90.0},//, 	
			new double[] 							{13.0, 25.0}//, 	
//			new double[] 							{10.0, 20.0},	
//			new double[] 							{7.0, 10.0},  
//			new double[] 							{2.0, 4.0},	
//			new double[] 							{1.0, 2.0}
			);
	final static List<double[]> 	daMin_ 	= Arrays.asList(	
			new double[] 							{0.0, 0.0});		//anything besides 0 here is stupid.
	final static List<double[]> 	daMax_ 	= Arrays.asList(					//maybe this metric only makes sense when applied to individual team score predictions?  glm makes same total prediction if you predict the total vs. predict the individual scores and add them
			new double[] 							{900.0, 900.0}, 	
			new double[] 							{50.0, 80.0}, 	
			new double[] 							{10.0, 40.0}, 	
			new double[] 							{5.0, 20.0}, 
			new double[] 							{2.0, 4.0});


	static int nr;
	static int cumIts;
	static int nBetTypes = 2;
	static List<Integer> seasonsList;
	static List<Integer> monthsList;
	//declaring data matrices.  purpose of matrices is to transfer data from the row-based List<String[]> to a set of columns for more practical analysis.  also matrices for computer speed, and easy bracket indexing.
	static Integer[]		ssn;		
	static Integer[]		mnth;
	static boolean[][]		correct;
	static boolean[][]		push;

	static int numPacksCount = 0;
	static int numPacksWithOnlyOneNegProfitSeasonCount = 0;
	static int numPacksWithNoNegProfitSeasonCount =0;
	static ResultsPackList[] packList;

	static class F {		
		private static boolean isBounded(double x, double min, double max) {
			return (x >= min) && (x <= max);
		}
		public static boolean areBounded(double[] values, double min, double max, int numBounded, int numDecreasing) {	
			for (int i = 0; i < numBounded; i++)									//AreBounded ?
				if ( !F.isBounded(values[i], min, max)  )
					return false;
			for (int i = 1; i < numDecreasing; i++)									//AreDecreasing ?
				if ( !  (values[i-1] <= values[i])  )
					return false;
			return true;
		}
	}
	

	private static void calculateAndPrintResults(boolean[][] pass_filter, ResultsRow[] resultsRow, MyFiles f) {

		// beginning of block to put in external function
		ResultsPack[] pack = new ResultsPack[nBetTypes];	
		for (int betType : Arrays.asList(H.i.S, H.i.T)){
			pack[betType] = new ResultsPack();
		}
		for (int month : monthsList){	
			for (int season : seasonsList){						
				int _n, sumCorrectBetGames;
				Double _pct, _profit;
				double[] pct = new double[nBetTypes], profit = new double[nBetTypes];
				int[] n = new int[nBetTypes];
				double b = 100.0;									//bet amount (dollars)
				for (int betType : Arrays.asList(H.i.S, H.i.T)){
					_n = 0;
					sumCorrectBetGames = 0;
					_pct = Double.NaN;
					_profit = Double.NaN;
					for (int r = 0; r < nr; r++) {			//restrict by pushed games, db, da, and if there is even data there.   what if using simple glm where ATL p-values are NA? then badData[r] is true

						if (	!push[r][betType]						&&
								pass_filter[r][betType]					&&																									
								(ssn[r] == season || season == 0)								&&
								(mnth[r] == month || month == 0)) {
							_n++;
							sumCorrectBetGames = sumCorrectBetGames + (correct[r][betType]? 1 : 0);				
						}		
					}
					try {
						_pct = (double) sumCorrectBetGames/_n;
						_profit = (double) sumCorrectBetGames*0.9523*b - (_n - sumCorrectBetGames)*b;	
					} catch (Exception e){}
					n[betType] = _n;
					profit[betType] = _profit;	
					pct[betType] = _pct;								
				}


				for (int betType : Arrays.asList(H.i.S, H.i.T)){
					resultsRow[betType].ssn = season;
					resultsRow[betType].mth = month;
					resultsRow[betType].n 	= n[betType];
					resultsRow[betType].pft = profit[betType];
					resultsRow[betType].pct = pct[betType];
							
				
					f.resultsStream[betType].println(resultsRow[betType].toOutputString());

					pack[betType].add(resultsRow[betType]);
				}
				cumIts++;
			}


			for (int betType : Arrays.asList(H.i.S, H.i.T))	{		
				numPacksCount++;
				if (pack[betType].hasAllProfitableSeasons())
					numPacksWithNoNegProfitSeasonCount++;
				if (pack[betType].hasAllButOneProfitableSeason())
					numPacksWithOnlyOneNegProfitSeasonCount++;
				packList[betType].addConditionally(pack[betType]);
			}


		}
		// end of block to put in external function somehow		
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		long timeStart = System.currentTimeMillis();
		
		numPacksCount = 0;
		numPacksWithOnlyOneNegProfitSeasonCount = 0;
		numPacksWithNoNegProfitSeasonCount =0;
		
		try {
			boolean x_wins_SU, x_wins_ATS, over_wins, x_e_wins_SU, x_e_wins_ATS, over_e_wins;
			double sf_b, tf_b;

			int maxNumRestricted = Collections.max(numRestricted_);

			cumIts = 1;
			int rIts = 0;
			MyFiles f = new MyFiles(rInputFileName_TimeStamp);
			packList = new ResultsPackList[nBetTypes];

			for (int betType : Arrays.asList(H.i.S, H.i.T)) {
				packList[betType] = new ResultsPackList();
				f.resultsStream[betType].println((new ResultsRow()).headerString);
				f.digestStream[betType].println((new ResultsRow()).headerString);
			}


			for (boolean ignoreOtherSeasons : ignoreOtherSeasons_){															//iterate through r restriction parameters
				for (int numTeamPriorGames : numTeamPriorGames_){
					for (String decayType : decayType_){

						rIts++;
						String rOutputFileName	= "C:\\Users\\User\\Documents\\forecasting\\data\\results_keep\\r_data_1349930545616_1.csv"; // f.rOutputName(rIts); 	//"C:\\Users\\User\\Documents\\forecasting\\data\\all\\r_data_1349924212057_1.csv";	//r_data_1349930545616_1
//						String rExecCmdLine = f.rExecCmdLine(ignoreOtherSeasons, numTeamPriorGames, decayType, rOutputFileName, f.timeStamp);  //"Rscript C:\\Users\\User\\workspace\\test1\\regressor.r "+ ignoreOtherSeasons +" "+ numTeamPriorGames +" "+ decayType +" "+ rInputFileName +" "+ rOutputFileName;
//
//						Process process = Runtime.getRuntime().exec(rExecCmdLine);
//						BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
//						BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//						String s = null;						
//						while ((s = stdInput.readLine()) != null)																// read the output from the command 
//							System.out.println(s);
//						while ((s = stdError.readLine()) != null) 															// read any errors from the attempted command
//							System.out.println(s);

						CSVReader csvr = new CSVReader( new FileReader(rOutputFileName));								//start handling r output data
						List<String[]> data = csvr.readAll();
						csvr.close();

						List<String> headerList = Arrays.asList(data.get(0));										//build header map
						HashMap<String, Integer> header = new HashMap<String, Integer>();
						for (int i = 0; i < headerList.size(); i++)
							header.put(headerList.get(i), i);

						//for (int r = 1; r < data.size(); r++)	{}												//prepare to delete duplicate ///// actually, don't worry about this crap. just do an extra model in r.  yes it will make it take a little longer for now.				


						for (int r = 1; r < data.size(); r++)	{												//clean the data - skip the header row
							if (	data.get(r)[header.get(H.Cols.sf_e)].equals("NA") ||													//remove row with missing data -- okay to throw out whole row cuz if spread is NA, total probably will be also.
									data.get(r)[header.get(H.Cols.tf_e)].equals("NA") ||
									data.get(r)[header.get(H.Cols.p_sf_x)].equals("NA") ||
									data.get(r)[header.get(H.Cols.p_sf_y)].equals("NA") ||
									data.get(r)[header.get(H.Cols.p_tf_x)].equals("NA") ||
									data.get(r)[header.get(H.Cols.p_tf_y)].equals("NA") ||
									data.get(r)[header.get(H.Cols.p_xf)].equals("NA") ||						//this is bad.  this should never happen.  where this is NA but the other p's arent.
									data.get(r)[header.get(H.Cols.p_yf)].equals("NA") ||
									(		data.get(r)[header.get(H.Cols.x_name)].equals(data.get(r-1)[header.get(H.Cols.y_name)]) 	&& 			//remove duplicated rows
											data.get(r)[header.get(H.Cols.y_name)].equals(data.get(r-1)[header.get(H.Cols.x_name)]) 	&& 	
											data.get(r)[header.get(H.Cols.date)].equals(data.get(r-1)[header.get(H.Cols.date)])	) ) {
								data.remove(r);
								r--;																									//to prevent skipping a row when one gets deleted.
							}
						}
						data.remove(0);																				//remove the header so data starts at index 0.  for consistency w/ matrix row index.

						nr = data.size();																						//number of rows of data === number of rows to declare for upcoming data matrices
						int nTeams = 2;

						//tmP refers to the p-value for the model estimating separate scores. 
						//tmDA refers to the "difference actual" values -- differences between the actual x final score and x estimated final score.

						Date[] 			date = 		new Date[nr];								//declaring data matrices.  purpose of matrices is to transfer data from the row-based List<String[]> to a set of columns for more practical analysis.  also matrices for computer speed, and easy bracket indexing.
										ssn = 		new Integer[nr];		
										mnth =		new Integer[nr];		

						String[][] 		team = 		new String[nr][nTeams];							//2-d arrays to store both spread and totals value in the same object
						double[][]		tmP =		new double[nr][nTeams];
						double[][][]	p =			new double[nr][nBetTypes][nTeams];		
						//double[][][]	ci =		new double[nr][nTeams][2];							//lwr and upr		

						double[][]		tmDA =		new double[nr][nTeams];							
						double[][]		da =		new double[nr][nBetTypes];							// DA = Difference Actual.  the difference between my estimated value and the actual value.
						double[][]		db =		new double[nr][nBetTypes];							// DB = Difference Bookee.  the difference between my estimated value and the bookee's value.
										push =		new boolean[nr][nBetTypes];					
						boolean[][]		ciCleared = new boolean[nr][nBetTypes];
										correct =	new boolean[nr][nBetTypes];							// was my prediction correct

						double[][][] 	pastTmPs = 	new double[nr][nTeams][maxNumRestricted];			 
						double[][][][] 	pastPs = 	new double[nr][nBetTypes][nTeams][maxNumRestricted];			 
						double[][][] 	pastTmDAs = new double[nr][nTeams][maxNumRestricted];			 
						double[][][][] 	pastDAs = 	new double[nr][nBetTypes][nTeams][maxNumRestricted];			// per bet type (spread or total), this is an array of an arrays of the past DAs per the team of the given row 
						boolean[]		fullDAs = 	new boolean[nr];
						boolean[]		fullTmDAs = new boolean[nr];
						boolean[]		fullPs = new boolean[nr];
						boolean[]		fullTmPs = new boolean[nr];

						boolean[][]		pass_P =	new boolean[nr][nBetTypes];
						boolean[][]		pass_DA =	new boolean[nr][nBetTypes];
						boolean[][]		pass_DB =	new boolean[nr][nBetTypes];
						boolean[][]		pass_CI =	new boolean[nr][nBetTypes];
						boolean[][]		pass_All =	new boolean[nr][nBetTypes];

						
						for (int r=0; r < nr; r++)	{		
							List<String> rowList = Arrays.asList(data.get(r));								
							HashMap<String, String> row = new HashMap<String, String>();
							for (int c = 0; c < rowList.size(); c++)										// make the row a map with key of column name
								row.put(headerList.get(c), rowList.get(c));

							date[r] 		= (Date) H.dateformat.parse(row.get(H.Cols.date));
							ssn[r]			= Integer.valueOf(row.get(H.Cols.ssn));

							team[r][H.i.X] = row.get(H.Cols.x_name);
							team[r][H.i.Y] = row.get(H.Cols.y_name);

							p[r][H.i.S][H.i.X] =  Double.valueOf(row.get(H.Cols.p_sf_x));
							p[r][H.i.S][H.i.Y] =  Double.valueOf(row.get(H.Cols.p_sf_y)); 
							p[r][H.i.T][H.i.X] =  Double.valueOf(row.get(H.Cols.p_tf_x));
							p[r][H.i.T][H.i.Y] =  Double.valueOf(row.get(H.Cols.p_tf_y));
							
							System.out.println(date[r]);
							System.out.println(row.get(H.Cols.x_name));
							System.out.println(Double.valueOf(row.get(H.Cols.p_sf_x)));
							System.out.println(Double.valueOf(row.get(H.Cols.p_sf_y)));

							tmP[r][H.i.X] =  Double.valueOf(row.get(H.Cols.p_xf));
							tmP[r][H.i.Y] =  Double.valueOf(row.get(H.Cols.p_yf));


							double xf_a = Double.valueOf(row.get(H.Cols.xf_a));
							double yf_a = Double.valueOf(row.get(H.Cols.yf_a));
							double xf_e = Double.valueOf(row.get(H.Cols.xf_e));
							double yf_e = Double.valueOf(row.get(H.Cols.yf_e));


							double sf_a = Double.valueOf(row.get(H.Cols.xf_a)) - Double.valueOf(row.get(H.Cols.yf_a));
							double tf_a = Double.valueOf(row.get(H.Cols.xf_a)) + Double.valueOf(row.get(H.Cols.yf_a));
							double sf_e = Double.valueOf(row.get(H.Cols.sf_e));
							double tf_e = Double.valueOf(row.get(H.Cols.tf_e));

							try {					sf_b = Double.valueOf(row.get(H.Cols.sf_b)); 	}				//bookee values might have missing data
							catch(Exception e){		sf_b = Double.NaN;								}
							try {					tf_b = Double.valueOf(row.get(H.Cols.tf_b)); 	}
							catch(Exception e){		tf_b = Double.NaN;								}


							ciCleared[r][H.i.S] = !F.isBounded(sf_b, Double.valueOf(row.get(H.Cols.sf_e_lwr)), Double.valueOf(row.get(H.Cols.sf_e_upr)));
							ciCleared[r][H.i.S] = !F.isBounded(tf_b, Double.valueOf(row.get(H.Cols.tf_e_lwr)), Double.valueOf(row.get(H.Cols.tf_e_upr)));



							tmDA[r][H.i.X] =  Math.abs(xf_e - xf_a);
							tmDA[r][H.i.Y] =  Math.abs(yf_e - yf_a);

							da[r][H.i.S] =  Math.abs(sf_e - sf_a);													//if any of these string --> number conversions fail, then i want the program to fail.
							da[r][H.i.T] =  Math.abs(tf_e - tf_a);

							db[r][H.i.S] =  Math.abs(sf_e - sf_b);		
							db[r][H.i.T] =  Math.abs(tf_e - tf_b);

							push[r][H.i.S] = sf_a == sf_b 	|| 	sf_e == sf_b	||	Double.isNaN(sf_b);				//push is used to denote games that don't have bookee data, also.  (kind of sloppy)
							push[r][H.i.T] = tf_a == tf_b 	|| 	tf_e == tf_b	||	Double.isNaN(tf_b);

							x_wins_ATS =	sf_a > sf_b;		//spread = x - y === away minus home
							over_wins =		tf_a > tf_b;
							x_wins_SU = 	sf_a > 0;

							x_e_wins_ATS =	sf_e > sf_b;
							over_e_wins =	tf_e > tf_b;
							x_e_wins_SU	= 	sf_e > 0;

							correct[r][H.i.ATS] =	x_wins_ATS	== x_e_wins_ATS;
							correct[r][H.i.OU] =	over_wins 	== over_e_wins;		
//							correct[r][H.i.SU] = 	x_wins_SU 	== x_e_wins_SU;		

							fullDAs[r] = false;
						}

						for (int r=0; r < nr; r++)	{																	//fill pastDAs, pastTmDAs, pastPs, pastTmPs
							String teamX = team[r][H.i.X];
							String teamY = team[r][H.i.Y];
							int daXi = 0, daYi = 0;																	
							fullDAs[r] 		= true;
							fullTmDAs[r] 	= true;
							fullPs[r] 		= true;
							fullTmPs[r] 	= true;

							for (int betType : Arrays.asList(H.i.S, H.i.T)) {											//fill pastDAs
								daXi = 0; daYi = 0;																	

								for (int r2 = r - 1; r2 >= 0;	 r2--) {												//r2:  looping backwards from r, looking for games that the current teams played in.
									if (daXi < maxNumRestricted){															
										if (team[r2][H.i.X].equals(teamX) || team[r2][H.i.Y].equals(teamX)){
											pastDAs[r][betType][H.i.X][daXi] = da[r2][betType];						//assign the game's DA to the specific team (x or y).  x here.
											daXi++;						
										}
									}
									if (daYi < maxNumRestricted){
										if (team[r2][H.i.Y].equals(teamY) || team[r2][H.i.X].equals(teamY)){
											pastDAs[r][betType][H.i.Y][daYi] = da[r2][betType];
											daYi++;				
										}	
									}
								}
								if ( daXi != maxNumRestricted || daYi != maxNumRestricted )				
									fullDAs[r] = false;
							}

							daXi = 0; daYi = 0;		
							for (int r2 = r - 1; r2 >= 0;	 r2--) {											//fill pastTmDAs
								if (daXi < maxNumRestricted){															
									if (team[r2][H.i.X].equals(teamX)){							// || team[r2][H.i.Y].equals(teamX)
										pastTmDAs[r][H.i.X][daXi] = tmDA[r2][H.i.X];					
										daXi++;						
									}												
									if (team[r2][H.i.Y].equals(teamX)){							// || team[r2][H.i.Y].equals(teamX)
										pastTmDAs[r][H.i.X][daXi] = tmDA[r2][H.i.Y];					
										daXi++;						
									}
								}
								if (daYi < maxNumRestricted){
									if (team[r2][H.i.Y].equals(teamY)){										// || team[r2][H.i.X].equals(teamY)
										pastTmDAs[r][H.i.Y][daYi] = tmDA[r2][H.i.Y];
										daYi++;				
									}	
									if (team[r2][H.i.X].equals(teamY)){										// || team[r2][H.i.X].equals(teamY)
										pastTmDAs[r][H.i.Y][daYi] = tmDA[r2][H.i.X];
										daYi++;				
									}	
								}
							}
							if ( daXi != maxNumRestricted || daYi != maxNumRestricted )				
								fullTmDAs[r] = false;

							for (int betType : Arrays.asList(H.i.S, H.i.T)) {											//fill pastPs
								daXi = 0; daYi = 0;																	

								for (int r2 = r - 1; r2 >= 0;	 r2--) {											
									if (daXi < maxNumRestricted){															
										if (team[r2][H.i.X].equals(teamX)){												// || team[r2][H.i.Y].equals(teamX)
											pastPs[r][betType][H.i.X][daXi] = p[r2][betType][H.i.X];					
											daXi++;						
										}												
										if (team[r2][H.i.Y].equals(teamX)){												// || team[r2][H.i.Y].equals(teamX)
											pastPs[r][betType][H.i.X][daXi] = p[r2][betType][H.i.Y];					
											daXi++;						
										}
									}
									if (daYi < maxNumRestricted){
										if (team[r2][H.i.Y].equals(teamY)){												// || team[r2][H.i.X].equals(teamY)
											pastPs[r][betType][H.i.Y][daYi] = p[r2][betType][H.i.Y];
											daYi++;				
										}	
										if (team[r2][H.i.X].equals(teamY)){												// || team[r2][H.i.X].equals(teamY)
											pastPs[r][betType][H.i.Y][daYi] = p[r2][betType][H.i.X];
											daYi++;				
										}	
									}
								}
								if ( daXi != maxNumRestricted || daYi != maxNumRestricted )				
									fullPs[r] = false;
							}

							daXi = 0; daYi = 0;													
							for (int r2 = r - 1; r2 >= 0;	 r2--) {												//fill pastTmPs
								if (daXi < maxNumRestricted){															
									if (team[r2][H.i.X].equals(teamX)){
										pastTmPs[r][H.i.X][daXi] = tmP[r2][H.i.X];					
										daXi++;						
									}									
									if (team[r2][H.i.Y].equals(teamX)){
										pastTmPs[r][H.i.X][daXi] = tmP[r2][H.i.Y];					
										daXi++;						
									}
								}
								if (daYi < maxNumRestricted){
									if (team[r2][H.i.Y].equals(teamY)){				
										pastTmPs[r][H.i.Y][daYi] = tmP[r2][H.i.Y];
										daYi++;				
									}	
									if (team[r2][H.i.X].equals(teamY)){				
										pastTmPs[r][H.i.Y][daYi] = tmP[r2][H.i.X];
										daYi++;				
									}	
								}
							}
							if ( daXi != maxNumRestricted || daYi != maxNumRestricted )				
								fullTmPs[r] = false;

						}

						seasonsList = Arrays.asList(ssn);									//makes seasonsList a list of unique seasons.
						monthsList = Arrays.asList(mnth);

						Set<Integer> seasonsLinkedHashSet = new LinkedHashSet<Integer>(seasonsList);
						seasonsList = new ArrayList<Integer>();
						seasonsList.add(0);																//this will be used to indicate that all seasons should be analyzed together
						seasonsList.addAll(seasonsLinkedHashSet);	
												
						for (int i=0; i < nr; i++)
							mnth[i] = date[i].getMonth() + 1;
						
						Set<Integer> monthsLinkedHashSet = new LinkedHashSet<Integer>(monthsList);
						monthsList = new ArrayList<Integer>();
						monthsList.add(0);																//this will be used to indicate that all seasons should be analyzed together
						monthsList.addAll(monthsLinkedHashSet);		
						ResultsRow[] resultsRow;

						System.out.println("now starting analyses");
						
						//filter CI's here
						resultsRow = new ResultsRow[nBetTypes];
						resultsRow[0] = new ResultsRow(); 
						resultsRow[1] = new ResultsRow();
						for (int betType : Arrays.asList(H.i.S, H.i.T)){
							
							resultsRow[betType].ios = ignoreOtherSeasons;
							resultsRow[betType].nTPG = numTeamPriorGames;
							resultsRow[betType].decay = decayType;
							resultsRow[betType].useCI = true;

						}
						Analyzer.calculateAndPrintResults(ciCleared, resultsRow, f);

						for (boolean useTmDAs : useTmDAs_ ){
							for (int nRes : numRestricted_) {
								for (int nDec : numDecreasing_){
									for (double [] daMin : daMin_){
										for (double [] daMax : daMax_){	
											//filter da's here
											for (int betType : Arrays.asList(H.i.S, H.i.T)){
												for (int r = 0; r < nr; r++) {	
													if ((useTmDAs && fullTmDAs[r] && 	F.areBounded(pastTmDAs[r][H.i.X], daMin[betType], daMax[betType], nRes, nDec) &&
															F.areBounded(pastTmDAs[r][H.i.Y], daMin[betType], daMax[betType], nRes, nDec)) ||
															(!useTmDAs && fullDAs[r] &&		F.areBounded( pastDAs[r][betType][H.i.X], daMin[betType], daMax[betType], nRes, nDec) &&
																	F.areBounded( pastDAs[r][betType][H.i.Y], daMin[betType], daMax[betType], nRes, nDec)))
														pass_DA[r][betType] = true;
													else
														pass_DA[r][betType] = false;
												}
											}

											resultsRow = new ResultsRow[nBetTypes];
											resultsRow[0] = new ResultsRow(); 
											resultsRow[1] = new ResultsRow();
											for (int betType : Arrays.asList(H.i.S, H.i.T)){
												resultsRow[betType].ios = ignoreOtherSeasons;
												resultsRow[betType].nTPG = numTeamPriorGames;
												resultsRow[betType].decay = decayType;
												resultsRow[betType].useTmDA = useTmDAs;
												resultsRow[betType].nRes = nRes;
												resultsRow[betType].nDec = nDec;
												resultsRow[betType].daMin = daMin[betType];
												resultsRow[betType].daMax = daMax[betType];
											}
											Analyzer.calculateAndPrintResults(pass_DA, resultsRow, f);
										}
									}
								}
							}
						}
						for (boolean useTmPs : useTmPs_ ){
							for (int nRes : numRestricted_) {
								for (int nDec : numDecreasing_){
									for(double pMin : pMin_){
										for (double pMax : pMax_){		
											//filter p's here
											for (int betType : Arrays.asList(H.i.S, H.i.T)){
												for (int r = 0; r < nr; r++) {	
													if ((useTmPs && fullTmPs[r] && 	F.areBounded(pastTmPs[r][H.i.X], pMin, pMax, nRes, nDec) && 
															F.areBounded(pastTmPs[r][H.i.Y], pMin, pMax, nRes, nDec)) ||
															(!useTmPs &&  fullPs[r] &&  F.areBounded(pastPs[r][betType][H.i.X], pMin, pMax, nRes, nDec) &&
																	F.areBounded(pastPs[r][betType][H.i.Y], pMin, pMax, nRes, nDec)))
														pass_P[r][betType] = true;
													else
														pass_P[r][betType] = false;
												}
											}
											
											resultsRow = new ResultsRow[nBetTypes];
											resultsRow[0] = new ResultsRow(); 
											resultsRow[1] = new ResultsRow();
											for (int betType : Arrays.asList(H.i.S, H.i.T)){
												resultsRow[betType].ios = ignoreOtherSeasons;
												resultsRow[betType].nTPG = numTeamPriorGames;
												resultsRow[betType].decay = decayType;
												resultsRow[betType].useTmP = true;
												resultsRow[betType].nRes = nRes;
												resultsRow[betType].nDec = nDec;	//TODO change to nd days, not DA.  use for both p nad da
												resultsRow[betType].pMin = pMin;
												resultsRow[betType].pMax = pMax;
											}
											Analyzer.calculateAndPrintResults(pass_P, resultsRow, f);
										}
									}
								}
							}
						}
						for (double[] dbMin : dbMin_) {
							for (double[] dbMax : dbMax_){
								//filter db's here
								for (int betType : Arrays.asList(H.i.S, H.i.T)){
									for (int r = 0; r < nr; r++) {	
										if (F.isBounded( db[r][betType], dbMin[betType], dbMax[betType]))
											pass_DB[r][betType] = true;
										else
											pass_DB[r][betType] = false;
									}
									//calculate and output results
								}

								resultsRow = new ResultsRow[nBetTypes];
								resultsRow[0] = new ResultsRow(); 
								resultsRow[1] = new ResultsRow();
								for (int betType : Arrays.asList(H.i.S, H.i.T)){
									resultsRow[betType].ios = ignoreOtherSeasons;
									resultsRow[betType].nTPG = numTeamPriorGames;
									resultsRow[betType].decay = decayType;
									resultsRow[betType].dbMin = dbMin[betType];
									resultsRow[betType].dbMax = dbMax[betType];
								}
								Analyzer.calculateAndPrintResults(pass_DB, resultsRow, f);
							}
						}
					}
				}
				System.out.println("iteration: " + cumIts);
			}

			for (int betType : Arrays.asList(H.i.S, H.i.T)){

				packList[betType].treat();
				packList[betType].printAll(f.digestStream[betType]);

				f.resultsStream[betType].close();
				f.digestStream[betType].close();
				System.out.println(f.resultsFile[betType]);
				System.out.println(f.digestFile[betType]);
			}
		} catch (Exception e) {	e.printStackTrace();}
		System.out.println("numPacksCount: " + numPacksCount);
		System.out.println("numPacksWithOnlyOneNegProfitSeasonCount: " + numPacksWithOnlyOneNegProfitSeasonCount);
		System.out.println("numPacksWithNoNegProfitSeasonCount: " + numPacksWithNoNegProfitSeasonCount);

		long timeEnd = System.currentTimeMillis();
		long timeDiff = timeEnd - timeStart;
		System.out.println("Elapsed time: " + timeDiff);

	}

}

