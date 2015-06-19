package byCrowd;




/*
2011 best ATS: NHL:
		boolean flexibleRanks = true;
		int consensusGroupStartIndex = 0;
		int numConsensusBettors = 10;
		int consensusPct = 30;
		int datePct_start = 0;
		int datePct_end = 100;
		int lineATSMax = 2500;
		int lineATSMin = 0;
		double lineOUMax = 50;
		double lineOUMin = 0;
w/ half the users
ATS:   4, n: 1309, 51.03, -10,275  
ATS:   6, n: 1310, 51.45, +410  
ATS:   8, n: 1310, 52.29, +26,045  
ATS:  10, n: 1310, 51.91, +12,880  
ATS:  12, n: 1310, 52.14, +20,530  
ATS:  14, n: 1310, 52.37, +21,905  
ATS:  15, n: 1310, 52.52, +30,545  
ATS:  16, n: 1310, 51.53, +12,380  
ATS:  18, n: 1310, 54.96, +72,530  
ATS:  20, n: 1310, 53.05, +41,845  
ATS:  22, n: 1310, 51.83, +26,890  
ATS:  24, n: 1310, 52.60, +41,545  
ATS:  26, n: 1310, 51.60, +26,005  
ATS:  28, n: 1310, 50.61, +15,760  
ATS:  30, n: 1310, 52.82, +34,035  
ATS:  35, n: 1310, 54.96, +72,530  
ATS:  40, n: 1310, 50.61, +15,760  
ATS:  50, n: 1310, 49.92, +5,225  
ATS:  80, n: 1310, 49.24, +1,965  
ATS: 130, n: 1310, 48.93, +1,495  

w/ all users:

ATS:   8, n: 1308, 51.68, +6,235  
ATS:  10, n: 1307, 51.26, -2,200  
ATS:  12, n: 1307, 50.04, -22,800  
ATS:  16, n: 1308, 52.06, +21,055  
ATS:  18, n: 1308, 50.69, -3,615  
ATS:  20, n: 1308, 52.91, +34,965  
ATS:  22, n: 1308, 51.45, +14,060  
ATS:  30, n: 1308, 50.00, -7,310  
ATS:  35, n: 1308, 50.08, +12,365  
ATS:  40, n: 1308, 49.24, +3,605  
ATS:  80, n: 1308, 47.63, -5,100  
ATS: 130, n: 1308, 48.47, +21,905 

2011 best OU nhl:
w/ half the users
		boolean flexibleRanks = true;
		int consensusGroupStartIndex = 0;
		int numConsensusBettors = 10;
		int consensusPct = 70;
		int datePct_start = 0;
		int datePct_end = 100;
		int lineATSMax = 2100;
		int lineATSMin = 0;
		double lineOUMax = 50;
		double lineOUMin = 5.5;
OU:    4, n:  548, 49.45, -16,850  
OU:    6, n:  546, 50.18, -12,600  
OU:    8, n:  562, 56.58, +24,800  
OU:   10, n:  530, 54.91, +14,050  
OU:   12, n:  574, 54.53, +12,950  
OU:   14, n:  544, 52.94, +3,200  
OU:   15, n:  543, 51.57, -4,650  
OU:   16, n:  546, 50.92, -8,400  
OU:   18, n:  553, 50.99, -8,050  
OU:   20, n:  534, 52.62, +1,350  
OU:   22, n:  554, 51.26, -6,500  
OU:   24, n:  555, 52.25, -750  
OU:   26, n:  536, 52.61, +1,300  
OU:   28, n:  570, 52.46, +450  
OU:   30, n:  560, 51.96, -2,450  
OU:   35, n:  553, 50.99, -8,050  
OU:   40, n:  570, 52.46, +450  
OU:   50, n:  572, 53.67, +7,750  
OU:   80, n:  578, 52.77, +2,350  
OU:  130, n:  581, 51.29, -6,650  

w/ all users
OU:    8, n:  533, 50.66, -9,650  
OU:   10, n:  510, 52.16, -1,200  
OU:   12, n:  530, 51.89, -2,750  
OU:   16, n:  495, 51.11, -6,600  
OU:   18, n:  517, 51.06, -7,150  
OU:   20, n:  512, 49.02, -18,050  
OU:   22, n:  508, 48.62, -20,050  
OU:   30, n:  538, 51.49, -5,050  
OU:   35, n:  545, 53.58, +6,850  
OU:   40, n:  552, 52.90, +3,000  
OU:   80, n:  530, 51.70, -3,800  
OU:  130, n:  551, 50.82, -9,050 


TODO

next get all data for seasons 2012 and 2013

if those all work, then check them using independent lines (from scores on wagerline)

next do mlb

next, modify this suite so it works for nba!  and test on last few nba seasons.

and then ncaab

what if it really works? 
1.  record bets every day on wagerline.com
2.  look in to cappers websites about how to be official capper to sell stuff. some recommend sites where you can post picks to log them.
3.  for each game, generate magicBets and also expected likelihood of winning.  (have like 5 or 10 different brackets, each w/ given parameters and winrates)

go to http://contests.covers.com/sportscontests/makepicks.aspx?sportid=5 to get times of each game that day
10 minutes before the game, determine the magicbet and submit 

how to input a certain time, and then wait until that time to do something????????????????
$230 for 8 gig ram http://www.bestbuy.com/site/IBM+-+Refurbished+-+ThinkCentre+Desktop+Computer+-+8+GB+Memory+-+160+GB+Hard+Drive/1305683271.p?id=mp1305683271&skuId=1305683271
*/

//REPORT:
//
// 	flexibleRanks: 	True/False
//	n: 				#
//	pct: 			#
//	datePct_start:	#
//	datePct_start:	#
//	date_start:		<date>
//	date_end:		<date>
//
//	past number of days: ATS units
//	1:	#
//	2:	#
//	3:	#
//	...
//	40:	#
//	50:	#
//	...
//
//	past number of days: OU units
//	1:	#
//	2:	#
//	3:	#
//	...
//	40:	#
//	50:	#
//	...
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.stat.Frequency;

class AnalyzeHelper_ML{

	public void printResults(TreeSet<Integer> params, List<Game2_ML> games) {
		String str_ATS = "ATS:";
		String str_OU = "OU: ";

		for (int param : params){
			int cumUnitsATS = 0;
			double numWinsATS = 0;
			int numBetsATS = 0;

			double pctWinATS;

			for (Game2_ML game : games){
				if (game.useThisGame){
					cumUnitsATS += game.allMagicUnitsATS.get(param);

					if (game.allMagicWinsATS.get(param) == "1")
						numWinsATS++;
					if (game.allMagicWinsATS.get(param) == "1" || game.allMagicWinsATS.get(param) == "0")
						numBetsATS++;
				}
			} 	
			pctWinATS =  numWinsATS*100 / numBetsATS ;
			System.out.format("%s %3d, n: %4d, %.2f, %+,d  %n", str_ATS, param, numBetsATS, pctWinATS, cumUnitsATS);
		}
		System.out.println();
		for (int param : params){
			int cumUnitsOU = 0;
			double numWinsOU = 0;
			int numBetsOU = 0;

			double pctWinOU;

			for (Game2_ML game : games){
				if (game.useThisGame){
					cumUnitsOU += game.allMagicUnitsOU.get(param);

					if (game.allMagicWinsOU.get(param) == "1")
						numWinsOU++;
					if (game.allMagicWinsOU.get(param) == "1" || game.allMagicWinsOU.get(param) == "0")
						numBetsOU++;
				}
			}
			pctWinOU = numWinsOU*100 / numBetsOU;
			System.out.format("%s %3d, n: %4d, %.2f, %+,d  %n", str_OU, param, numBetsOU, pctWinOU, cumUnitsOU);
		}
	}

}

class Game2_ML{
	int ordinal;
	String gameID;
	Date date;
	String tm1;
	String tm2;
	double tm1LineATS;
	double tm2LineATS;
	double lineOU;
	String resultATS;
	String resultOU;

	TreeSet<Integer> params;

	TreeMap<Integer, ArrayList<String>> userBetsATS;
	TreeMap<Integer, ArrayList<String>> userBetsOU;

	TreeMap<Integer, String> allMagicBetsATS;
	TreeMap<Integer, String> allMagicBetsOU;

	TreeMap<Integer, String> allMagicWinsATS;
	TreeMap<Integer, String> allMagicWinsOU;

	TreeMap<Integer, Integer> allMagicUnitsATS;
	TreeMap<Integer, Integer> allMagicUnitsOU;

	boolean useThisGame = true;

	/**
	 * 
		//using this wagerline data:
		// line: -132 -- favored
		// ?w:  +500
		// ?l:  -660

		//line:  +167 - underdog
		// ?w:  835
		// ?l:  -500
	 * @return
	 */
	private int calculateUnitsATS(String betATS, String winATS) throws Exception{

		double lineATS = 0;
		if (betATS.equals("1"))
			lineATS = tm1LineATS;
		else if (betATS.equals("2"))
			lineATS = tm2LineATS;
		else 
			return 0;


		if (lineATS < 0){
			if (winATS == "0")					//lose
				return (int) lineATS*500/100;
			if (winATS == "1")					//win
				return 500;
			if (winATS == "3")					//push
				return 0;
		}
		if (lineATS > 0){
			if (winATS == "0")					//lose
				return -500;
			if (winATS == "1")					//win
				return (int) lineATS*500/100;
			if (winATS == "3")					//push
				return 0;
		}
		return 0;
	}


	private int calculateUnitsOU(String winOU) {
		if (winOU == "1")
			return 500;
		if (winOU == "0")
			return -550;
		return 0;			//if (winOU == "3")
	}
	public void calculateWinsAndUnits() throws Exception {

		for (int param : params) {

			String magicBetATS = allMagicBetsATS.get(param);
			String magicBetOU = allMagicBetsOU.get(param);

			String magicWinATS = "0";
			String magicWinOU = "0";

			if (magicBetATS.equals(resultATS))
				magicWinATS = "1";
			if (magicBetATS.equals("3") || resultATS.equals("3"))
				magicWinATS = "3";

			if (magicBetOU.equals(resultOU))
				magicWinOU = "1";
			if (magicBetOU.equals("3") || resultOU.equals("3"))
				magicWinOU = "3";

			Integer magicUnitsATS = calculateUnitsATS(magicBetATS, magicWinATS);
			Integer magicUnitsOU = calculateUnitsOU(magicWinOU);

//			if (param == 1) 
//				System.out.format("%s\t%8.1f\t%8.1f\t%,6d%n", magicWinATS, tm1LineATS, tm2LineATS, magicUnitsATS); 
			

//			if (param == 1)
//				System.out.format("%s,     %s, %s, %s, %d%n", gameID, resultOU, magicBetOU, magicWinOU, magicUnitsOU);

			allMagicWinsATS.put(param, magicWinATS);
			allMagicWinsOU.put(param, magicWinOU);
			
			allMagicUnitsATS.put(param, magicUnitsATS);
			allMagicUnitsOU.put(param, magicUnitsOU);
		}
	}

	public void determineMagicBets(int consensusGroupStartIndex, boolean flexibleRanks, int numConsensusBettors, int consensusPctMin){

		for (int unitsParameter : params){
			List<String> betsATS = userBetsATS.get(unitsParameter);
			List<String> betsOU = userBetsOU.get(unitsParameter);
			while (betsATS.remove("")) {}
			while (betsOU.remove("")) {}
			String magicBetATS = "3";
			String magicBetOU  = "3";

			if (betsATS.size() >= numConsensusBettors+consensusGroupStartIndex){
				if (!flexibleRanks)	{										//this will change the data.  that's okay i think?
					betsATS = betsATS.subList(consensusGroupStartIndex, consensusGroupStartIndex + numConsensusBettors);
					while (betsATS.remove("3")) {}
				}
				if (flexibleRanks) {
					while (betsATS.remove("3")) {}
					try {betsATS = betsATS.subList(consensusGroupStartIndex, consensusGroupStartIndex + numConsensusBettors); }
					catch (Exception e) { betsATS = null; }
				}
				if (betsATS != null && betsATS.size() == numConsensusBettors){
					Frequency f = new Frequency();
					for (String bet : betsATS)
						f.addValue(bet);
					magicBetATS = getMode(f);
					double consPctATS = f.getPct(magicBetATS);
					if (consPctATS*100 < consensusPctMin)
						magicBetATS = "3";
				}
			}
//			magicBetATS = testToggleMagicBet(magicBetATS);
			allMagicBetsATS.put(unitsParameter, magicBetATS);

			if (betsOU.size() >= numConsensusBettors+consensusGroupStartIndex){
				if (!flexibleRanks)	{										//this will change the data.  that's okay i think?
					betsOU = betsOU.subList(consensusGroupStartIndex, consensusGroupStartIndex + numConsensusBettors);
					while (betsOU.remove("3")) {}
				}
				if (flexibleRanks) {
					while (betsOU.remove("3")) {}
					try {betsOU = betsOU.subList(consensusGroupStartIndex, consensusGroupStartIndex + numConsensusBettors); }
					catch (Exception e) { betsOU = null; }
				}
				if (betsOU != null && betsOU.size() > 0){
					Frequency f = new Frequency();
					for (String bet : betsOU)
						f.addValue(bet);
					magicBetOU = getMode(f);
					double consPctOU = f.getPct(magicBetOU);
					if (consPctOU*100 < consensusPctMin)
						magicBetOU = "3";
				}
			}
//			if (unitsParameter == 1)
//				System.out.format("%s,     %s%n", gameID, magicBetOU);
//			magicBetOU = testToggleMagicBet(magicBetOU);
			allMagicBetsOU.put(unitsParameter, magicBetOU);

		}

	}

	private String testToggleMagicBet(String magicBet) {
		if (magicBet == "1") return "2";
		if (magicBet == "2") return "1";
		return "3";
	}

	/** f values 1 and 2 only */
	private String getMode(Frequency f) {

		double one_pct = f.getPct("1");
		double two_pct = f.getPct("2");

		if (one_pct >= two_pct)
			return "1";

		return "2";
	}


	public Game2_ML(int ordinal, TreeSet<Integer> params, String gameID, Date date, String tm1, String tm2, double tm1LineATS, double tm2LineATS, double lineOU, String resultATS, String resultOU, 
			TreeMap<Integer, ArrayList<String>> allBetsATS, TreeMap<Integer, ArrayList<String>> allBetsOU){

		this.ordinal = ordinal;
		this.params = params;
		this.gameID = gameID;
		this.date = date;
		this.tm1 = tm1;
		this.tm2 = tm2;
		this.tm1LineATS = tm1LineATS;
		this.tm2LineATS = tm2LineATS;
		this.lineOU = lineOU;
		this.resultATS = resultATS;
		this.resultOU = resultOU;

		this.userBetsATS = allBetsATS;
		this.userBetsOU = allBetsOU;

		this.allMagicBetsATS = new TreeMap<Integer, String>();
		this.allMagicBetsOU = new TreeMap<Integer, String>();

		this.allMagicUnitsATS = new TreeMap<Integer, Integer>();
		this.allMagicUnitsOU = new TreeMap<Integer, Integer>();

		this.allMagicWinsATS = new TreeMap<Integer, String>();
		this.allMagicWinsOU = new TreeMap<Integer, String>();
	}

	public String toString(){
		String c = ", ";
		return gameID +c+ date +c+ tm1 +c+ tm2 +c+ tm1LineATS +c+ tm2LineATS +c+ lineOU +c+ resultATS +c+ resultOU +c+ userBetsATS.toString() +c+ userBetsOU.toString();
	}


	public void restictMagicBetsByDate(int totalNumGames, int datePct_start, int datePct_end){

		int minOrdinal = totalNumGames * datePct_start / 100;
		int maxOrdinal = totalNumGames * datePct_end / 100;
		
		if (ordinal < minOrdinal)
			useThisGame = false;
		if (ordinal > maxOrdinal)
			useThisGame = false;

	}


	public void restictMagicBetsByLineATS(int lineATSMin,	int lineATSMax) {

		if (Math.abs(tm1LineATS) < lineATSMin)
			useThisGame = false;
		if (Math.abs(tm2LineATS) < lineATSMin)
			useThisGame = false;

		if (Math.abs(tm1LineATS) > lineATSMax)
			useThisGame = false;
		if (Math.abs(tm2LineATS) > lineATSMax)
			useThisGame = false;
		
	}


	public void restictMagicBetsByLineOU(double lineOUMin, double lineOUMax) {

		if (Math.abs(lineOU) > lineOUMax)
			useThisGame = false;
		if (Math.abs(lineOU) < lineOUMin)
			useThisGame = false;
		
	}
}

public class Analyze_ML {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println();
		//notes:  1,0: win,lose. 3: push or no bet.  1,2: (tm1,tm2) and (over,under)

		boolean flexibleRanks = true;
		int consensusGroupStartIndex = 0;
		int numConsensusBettors = 30;
		int consensusPct = 70;
		int datePct_start = 0;
		int datePct_end = 100;
		int lineATSMax = 2500;
		int lineATSMin = 0;
		double lineOUMax = 50;
		double lineOUMin = 0;

		List<Game2_ML> games = new ArrayList<Game2_ML>();
		String tabulatedDataLocation = "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\tabulated_NHL_2011_fortyUsers.csv";
		BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(tabulatedDataLocation))));
		String lineStr, headerStr = br.readLine();
		List<String> line, header = Arrays.asList(headerStr.split(",", -1));
		TreeSet<Integer> unitsParameters = new TreeSet<Integer>();
		Pattern paramPattern = Pattern.compile(".*_(\\d*).*");
		Matcher paramMatcher;	
		for (String columnName : header){
			paramMatcher = paramPattern.matcher(columnName);									
			if (paramMatcher.find())							
				unitsParameters.add(  Integer.parseInt(paramMatcher.group(1))  );
		}

		//gameID,date,tm1,tm2,tm1Pts,tm2Pts,line1ATS,line2ATS,lineOU,resultATS,resultOU,betsATS_1,betsOU_1,betsATS_2,betsOU_2,betsATS_10,betsOU_10,
		//0		1		2	3	4		5		6		7			8		9	10			11
		for (int i=1;  (lineStr = br.readLine()) != null; i++) {
//			System.out.println(i);
			line = Arrays.asList(lineStr.split(",", -1));

			TreeMap<Integer, ArrayList<String>> allBetsATS = new TreeMap<Integer, ArrayList<String>>();
			TreeMap<Integer, ArrayList<String>> allBetsOU = new TreeMap<Integer, ArrayList<String>>();

			String gameID 			= line.get(0);
			Date date				= new TabulateHelper_ML().sdf.parse(line.get(1));
			String tm1				= line.get(2);
			String tm2				= line.get(3);
			String tm1LineATS_s		= line.get(6);
			String tm2LineATS_s		= line.get(7);
			String lineOU_s			= line.get(8);
			String resultATS		= line.get(9);
			String resultOU			= line.get(10);

			double tm1LineATS, tm2LineATS, lineOU;

			try { 					tm1LineATS		= Double.parseDouble(tm1LineATS_s);}
			catch (Exception e){	tm1LineATS		= 0;}
			try { 				 	tm2LineATS		= Double.parseDouble(tm2LineATS_s);}
			catch (Exception e){	tm2LineATS		= 0;}
			try { 				 	lineOU			= Double.parseDouble(lineOU_s);}
			catch (Exception e){	lineOU			= 0;}


			int j = 11;
			for (int param : unitsParameters){
				ArrayList<String> betsATS = new ArrayList<String>( Arrays.asList(line.get(j).split(" ")));
				ArrayList<String> betsOU = new ArrayList<String>(  Arrays.asList(line.get(j+1).split(" ")));
				allBetsATS.put(param, betsATS);
				allBetsOU.put(param, betsOU);
				j+=2;
			}
			games.add(new Game2_ML(i, unitsParameters, gameID, date, tm1, tm2, tm1LineATS, tm2LineATS, lineOU, resultATS, resultOU, allBetsATS, allBetsOU));
		}
		System.out.println("\n");
		for (Game2_ML game : games){
			game.determineMagicBets(consensusGroupStartIndex, flexibleRanks, numConsensusBettors, consensusPct);
			game.restictMagicBetsByDate(games.size(), datePct_start, datePct_end);
			game.restictMagicBetsByLineATS(lineATSMin, lineATSMax);
			game.restictMagicBetsByLineOU(lineOUMin, lineOUMax);


			game.calculateWinsAndUnits();
		}
		new AnalyzeHelper_ML().printResults(unitsParameters, games);
		br.close();
	}

}
