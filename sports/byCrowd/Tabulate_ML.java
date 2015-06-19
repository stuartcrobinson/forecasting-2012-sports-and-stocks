package byCrowd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

class TabulateHelper_ML{
	public final String betsDir 			= "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\user_bets_NHL_2012";
	public final String outputFileName 		= "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\tabulated_NHL_2012_"+ Long.toString(System.currentTimeMillis()) +".csv";
	public final int p 						= 500;
	public final int[] unitsParameters 		= new int[] {8, 10, 12, 16, 18, 20, 22, 30, 35, 40,  80, 130};	// 4, 5, 6, 7, 8, 9, 10, 15, 20, 30, 40, 70, 100, 120, 140};
	public final int NUM_USERS_TO_RANK 		= 40;
	public final int NUM_FILES_TO_READ 		= -1;									//-1 for all files
	public final SimpleDateFormat sdf 		= new SimpleDateFormat("MM/dd/yy");

	public TabulateHelper_ML(){}
	public String makeGameID(String tm1, String tm2, Date date){
		String gameID = sdf.format(date) + " - " +  tm1 + " - " + tm2;
		return gameID;		
	}
	public List<GameData_ML> gamesOnDate(Map<String, GameData_ML> games, Date date) {
		List<GameData_ML> gameDatas = new ArrayList<GameData_ML>();
		for (GameData_ML gd : games.values()){
			if (gd.date.equals(date))
				gameDatas.add(gd);
		}
		return gameDatas;
	}
	public int compareATS_reverseOrder(User_ML user1, User_ML user2, Date date, int unitsParam) {
		Integer user_1_units = user1.getCumUnitsATS(date, unitsParam);
		if (user_1_units == null) user_1_units = Integer.MIN_VALUE;

		Integer user_2_units = user2.getCumUnitsATS(date, unitsParam);
		if (user_2_units == null) user_2_units = Integer.MIN_VALUE;

		return user_2_units.compareTo(user_1_units);
	}
	public int compareOU_reverseOrder(User_ML user1, User_ML user2, Date date, int unitsParam) {

		Integer user_1_units = user1.getCumUnitsOU(date, unitsParam);
		if (user_1_units == null) user_1_units = Integer.MIN_VALUE;

		Integer user_2_units = user2.getCumUnitsOU(date, unitsParam);
		if (user_2_units == null) user_2_units = Integer.MIN_VALUE;

		return user_2_units.compareTo(user_1_units);
	}
	public String formatDate(Date date) {
		return sdf.format(date);
	}
}
class User_ML {
	String userID;
	Map<String, UserGameBet_ML> gamesList;
	Map<Date, Map<Integer, Integer>> cumUnitsMapATS;
	Map<Date, Map<Integer, Integer>> cumUnitsMapOU;
	public User_ML(String arg0){
		this.userID = arg0;
		this.gamesList = new TreeMap<String, UserGameBet_ML>();
		this.cumUnitsMapATS = new TreeMap<Date, Map<Integer, Integer>>();
		this.cumUnitsMapOU = new TreeMap<Date, Map<Integer, Integer>>();
	}
	public Integer getCumUnitsATS(Date date, int unitsParameter){
		if (this.cumUnitsMapATS.containsKey(date))
			return this.cumUnitsMapATS.get(date).get(unitsParameter);
		else 
			return null;
	}
	public Integer getCumUnitsOU(Date date, int unitsParameter){
		if (this.cumUnitsMapOU.containsKey(date))
			return this.cumUnitsMapOU.get(date).get(unitsParameter);
		else return null;
	}
	public boolean has_data_on_more_than_one_day() {
		if (this.gamesList.isEmpty())
			return false;
		return !Collections.max(gamesList.values()).date.equals(   Collections.min(gamesList.values()).date   );
	}

	@Override
	public String toString(){
		String r = "\n";
		String s = " ";

		//userID
		String out = userID;

		//games list
		out = out + r + "GAMES LIST";
		for (UserGameBet_ML ugb : this.gamesList.values())
			out = out + r + userID +s+ ugb.toString();

		//unitsMapATS
		for (Date date : cumUnitsMapATS.keySet()){
			for (Integer param : cumUnitsMapATS.get(date).keySet()){
				out = out + r + userID +s+ new TabulateHelper_ML().formatDate(date) + s + param + s + "ATS units: "+ cumUnitsMapATS.get(date).get(param);
			}
		}
		//unitsMapOU
		for (Date date : cumUnitsMapOU.keySet()){
			for (Integer param : cumUnitsMapATS.get(date).keySet()){
				out = out + r + userID +s+ new TabulateHelper_ML().formatDate(date) + s + param + s +  "OU units: "+ cumUnitsMapOU.get(date).get(param);
			}
		}		
		return out;
	}
}
class UserGameBet_ML  implements Comparator<UserGameBet_ML>, Comparable<UserGameBet_ML>{	
	int p;
	Date date;
	String gameID;
	String betATS, betOU;
	Double tm1LineATS, tm2LineATS, lineOU;
	Integer unitsATS, unitsOU;
	public UserGameBet_ML(Date date, String gameID, String betATS, String betOU, Double tm1LineATS, Double tm2LineATS, Double lineOU, Integer unitsATS, Integer unitsOU){
		this.p 			= new TabulateHelper_ML().p;
		this.date 		= date;
		this.gameID 	= gameID;
		this.betATS 	= betATS;
		this.betOU 		= betOU;
		this.tm1LineATS = tm1LineATS;
		this.tm2LineATS = tm2LineATS;
		this.lineOU 	= lineOU;
		this.unitsATS 	= unitsATS;
		this.unitsOU 	= unitsOU;
		
		if (this.unitsATS == null) this.unitsATS = 0;
		if (this.unitsOU == null) this.unitsOU = 0;
	}	
	@Override
	public int compareTo(UserGameBet_ML arg0) {
		return this.date.compareTo(arg0.date);
	}
	@Override
	public int compare(UserGameBet_ML arg0, UserGameBet_ML arg1) {
		return arg0.date.compareTo(arg1.date);
	}
	@Override 
	public String toString(){
		String c = ", ";
		return "(gameID, betATS, betOU, tm1LineATS, tm2LineATS, lineOU, unitsATS, unitsOU): " + gameID +c+ betATS +c+ betOU +c+ tm1LineATS +c+ tm2LineATS +c+ lineOU +c+ unitsATS +c+ unitsOU;
	}
}

class GameData_ML  implements Comparator<GameData_ML>, Comparable<GameData_ML>{
	Date date;
	String gameID, tm1, tm2;
	Integer tm1Pts, tm2Pts;
	Double tm1LineATS, tm2LineATS, lineOU;
	/** (1: tm1/Over.  2:  tm2/Under.  3: push/no bet) */
	int resultATS, resultOU;
	/** key is units param */
	Map<Integer, List<String>> betsMapATS, betsMapOU;

	/** CURRENTLY FOR NHL ONLY -- need to re-harvest user bets tonight to fix line negation error */
	public void calculateGameResultATS(){
		if (tm1Pts > tm2Pts)
			resultATS = 1;
		else if (tm1Pts < tm2Pts)
			resultATS = 2;
		else
			resultATS = 3;		//will never happen for moneyline sports.  would mean a tie happened.
	}
	public void calculateGameResultOU(){
		
		if (lineOU == null) 					resultOU = 3;
		else if ( tm1Pts + tm2Pts > lineOU)		resultOU = 1;
		else if (tm1Pts + tm2Pts < lineOU)		resultOU = 2;
		else									resultOU = 3;
		
//		System.out.format("%d %40s  %2d, %2d, %f%n", returnValue, gameID, tm1Pts, tm2Pts, lineOU);

	}

	public GameData_ML(){}
	public GameData_ML (String gameID, Date date, String tm1, String tm2, int tm1Pts, int tm2Pts, Double tm1LineATS, Double tm2LineATS, Double lineOU){
		this.gameID = gameID;
		this.date = date;
		this.tm1 = tm1;
		this.tm2 = tm2;
		this.tm1Pts = tm1Pts;
		this.tm2Pts = tm2Pts;
		this.tm1LineATS = tm1LineATS;
		this.tm2LineATS = tm2LineATS;
		this.lineOU = lineOU;
		this.betsMapATS = new TreeMap<Integer, List<String>>();
		this.betsMapOU = new TreeMap<Integer, List<String>>();
	}
	@Override
	public int compareTo(GameData_ML arg0) {
		return this.date.compareTo(arg0.date);
	}
	@Override
	public int compare(GameData_ML arg0, GameData_ML arg1) {
		return arg0.date.compareTo(arg1.date);
	}

	public String toString(){
		String s = ", ";
		String out = "(gameID, tm1Pts, tm2Pts, tm1LineATS, tm2LineATS, lineOU, resultATS, resultOU): " + gameID +s+ tm1Pts +s+ tm2Pts +s+ tm1LineATS +s+ tm2LineATS +s+ lineOU +s+ resultATS +s+ resultOU;
		return out;
	}

	public String toOutputString(){
		String c = ",";
		String out = gameID +c+ new TabulateHelper_ML().sdf.format(date) +c+ tm1 +c+ tm2 +c+ tm1Pts +c+ tm2Pts +c+ tm1LineATS +c+ tm2LineATS +c+ lineOU +c+ resultATS +c+ resultOU +c;

		for (Integer unitsParam : new TabulateHelper_ML().unitsParameters){
			if (betsMapATS.get(unitsParam) != null)
				out = out + betsMapATS.get(unitsParam).toString().replaceAll("[\\[\\],]", "") +c;
			if (betsMapOU.get(unitsParam) != null)
				out = out + betsMapOU.get(unitsParam).toString().replaceAll("[\\[\\],]", "") +c;
		}
		return out;
	}
	public String outputFileHeaderLineString(){

		String c = ",";
		String out = "gameID,date,tm1,tm2,tm1Pts,tm2Pts,tm1LineATS,tm2LineATS,lineOU,resultATS,resultOU,";

		for (Integer unitsParam : new TabulateHelper_ML().unitsParameters){
			out = out + "betsATS_" + unitsParam + c;
			out = out + "betsOU_" + unitsParam + c;
		}
		return out;
	}
}


public class Tabulate_ML {

	static int[] unitsParameters 		= new TabulateHelper_ML().unitsParameters; 
	static int p 						= new TabulateHelper_ML().p;
	static SimpleDateFormat sdf 		= new TabulateHelper_ML().sdf;
	static final long DAY_IN_MILLIS 	= (1000 * 60 * 60 * 24);
	static int NUM_USERS_TO_RANK 		= new TabulateHelper_ML().NUM_USERS_TO_RANK;
	static int NUM_FILES_TO_READ 		= new TabulateHelper_ML().NUM_FILES_TO_READ;
	static String betsDir 				= new TabulateHelper_ML().betsDir;
	static String outputFileName 		= new TabulateHelper_ML().outputFileName; 

	/**
	 * prepare data for analysis.  save in files.
	 * @throws Exception 
	 */
	public static void main(String[] args)  {
		try {
			PrintWriter log = new PrintWriter(new FileWriter("C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\testOutput.txt"));

			log.println("start");

			Set<Date> dates = new TreeSet<Date>();
			List<User_ML> users = new ArrayList<User_ML>();
			Map<String, GameData_ML> games = new TreeMap<String, GameData_ML>();

			File[] filesArray = new File(betsDir).listFiles();
			if (NUM_FILES_TO_READ < 0) 
				NUM_FILES_TO_READ = filesArray.length;
			List<File> usersBetsFiles = Arrays.asList(filesArray)
					.subList(0, NUM_FILES_TO_READ)
					;		
			log.println(filesArray.length +" files");log.println(usersBetsFiles.size() +" files to in subset");

			int j = 1;
			for (File usersBetsFile : usersBetsFiles){
				String userID = usersBetsFile.getName().substring(0, usersBetsFile.getName().indexOf("_"));
				System.out.println(j++ + ". opening file for user "+ userID);
				User_ML user = new User_ML(userID);
				BufferedReader reader = new BufferedReader(new FileReader(usersBetsFile));
				String lineStr = reader.readLine();
				while ((lineStr = reader.readLine()) != null)   {
					if (lineStr.length() < 5) continue;
					
					List<String> line = Arrays.asList(lineStr.split(",", -1));
					//date,tm1,tm2,tm1Pts,tm2Pts,lineATS,betATS,didWinATS,unitsATS,lineOU,betOU,didWinOU,unitsOU
					//				out.println(date +","+ tm1 +","+ tm2 +","+ tm1Pts +","+ tm2Pts +","+ lineATS +","+ betATS +","+ didWinATS +","+ unitsATS +","+ lineOU +","+betOU +","+ didWinOU +","+ unitsOU);
					//							0			1		2			3			4				5			6				7				8			9			10			11				12
					Date 	date 		= sdf.parse(line.get(0));
					String 	tm1 		= line.get(1);
					String 	tm2 		= line.get(2);
					int 	tm1Pts 		= Integer.parseInt(line.get(3));
					int 	tm2Pts 		= Integer.parseInt(line.get(4));
					String 	lineATS_s	= line.get(5);
					String 	betATS	 	= line.get(6);
					String 	unitsATS_s	= line.get(8);
					String 	lineOU_s	= line.get(9);
					String 	betOU		= line.get(10);
					String 	unitsOU_s	= line.get(12);
					String  gameID		= new TabulateHelper_ML().makeGameID(tm1, tm2, date);
					dates.add(date);								
					user.cumUnitsMapATS.put(date, new TreeMap<Integer, Integer>());
					user.cumUnitsMapOU.put(date, new TreeMap<Integer, Integer>());

					if ( !betATS.equals("1") && !betATS.equals("2") ) 	betATS = "3";
					if ( !betOU.equals("1")  && !betOU.equals("2") ) 	betOU = "3";

					Double tm1LineATS = null, tm2LineATS = null, lineOU = null;
					Integer unitsATS = null, unitsOU = null;

					if (betATS.equals("1"))		tm1LineATS 	= Double.parseDouble(lineATS_s);
					if (betATS.equals("2"))		tm2LineATS 	= Double.parseDouble(lineATS_s);
					if (lineOU_s.length()>0)	lineOU 		= Double.parseDouble(lineOU_s);
					if (unitsATS_s.length()>0)	unitsATS	= Integer.parseInt(unitsATS_s);
					if (unitsOU_s.length()>0)	unitsOU		= Integer.parseInt(unitsOU_s);

					user.gamesList.put(gameID, new UserGameBet_ML(date, gameID, betATS, betOU, tm1LineATS, tm2LineATS, lineOU, unitsATS, unitsOU))	;

					
					if (games.containsKey(gameID)){
						GameData_ML alreadyAddedGame = games.get(gameID); 
						if (tm1LineATS != null) {
							if (alreadyAddedGame.tm1LineATS == null)
								alreadyAddedGame.tm1LineATS = tm1LineATS;
						}
						if (tm2LineATS != null) {
							if (alreadyAddedGame.tm2LineATS == null)
								alreadyAddedGame.tm2LineATS = tm2LineATS;
						}
						if (alreadyAddedGame.lineOU == null && lineOU != null){
							alreadyAddedGame.lineOU = lineOU;
						}
					}
					else {
						GameData_ML gameData = new GameData_ML(gameID, date, tm1, tm2, tm1Pts, tm2Pts, tm1LineATS, tm2LineATS, lineOU);
						games.put(gameID, gameData);
					}
				}
				if (user.has_data_on_more_than_one_day())
					users.add(user);
				reader.close();
			}


			for (GameData_ML game : games.values()){
				game.calculateGameResultATS();
				game.calculateGameResultOU();
			}

			//now fill the units map for each user by day
			int k = 1;
			for (User_ML user : users){	
				System.out.println(k++ + ".  filling units map for user "+ user.userID);

				for (int unitsParameter : unitsParameters){
					for (UserGameBet_ML game : user.gamesList.values()){			

						Date gameDate = game.date;

						int unitsATS = 0, unitsOU = 0;
						for (UserGameBet_ML iterativeGame : user.gamesList.values()){
							int dayDifference = (int) ((gameDate.getTime() - iterativeGame.date.getTime()) / DAY_IN_MILLIS);
							if (dayDifference > 0 && dayDifference <= unitsParameter){
								unitsATS += iterativeGame.unitsATS;
								unitsOU += iterativeGame.unitsOU;
							}
						}
						user.cumUnitsMapATS.get(gameDate).put(unitsParameter, unitsATS);
						user.cumUnitsMapOU.get(gameDate).put(unitsParameter, unitsOU);
					}
				}
			}

			/** sort users by units per parameter and build ranked betlists per game */
			String bet;
			for (final Date date : dates){
				System.out.println(sdf.format(date));
				for (final int unitsParam : unitsParameters){

					Collections.sort(users, new Comparator<User_ML>() {
						@Override
						public int compare(User_ML user1, User_ML user2) {
							return new TabulateHelper_ML().compareATS_reverseOrder(user1, user2, date, unitsParam);
						} }); 
					for (GameData_ML game : new TabulateHelper_ML().gamesOnDate(games, date)){ 
						List<String> betsList = new ArrayList<String>();
						for (int i = 0; i < NUM_USERS_TO_RANK; i++){
							Integer units = users.get(i).getCumUnitsATS(date, unitsParam);
							if (units != null && units > 0){
								try { bet = users.get(i).gamesList.get(game.gameID).betATS; }
								catch (Exception e) { bet = "3"; }
								betsList.add(bet);
							}
							log.println(users.get(i).userID +", ATS units"+unitsParam+": "+ units +"    "+ game.gameID);
						}
						game.betsMapATS.put(unitsParam, betsList);
					}

					Collections.sort(users, new Comparator<User_ML>() {
						@Override
						public int compare(User_ML user1, User_ML user2) {
							return new TabulateHelper_ML().compareOU_reverseOrder(user1, user2, date, unitsParam);
						} }); 
					for (GameData_ML game : new TabulateHelper_ML().gamesOnDate(games, date)){
						List<String> betsList = new ArrayList<String>();
						for (int i = 0; i < NUM_USERS_TO_RANK; i++){			
							Integer units = users.get(i).getCumUnitsOU(date, unitsParam);
							if (units != null && units > 0){
								try { bet = users.get(i).gamesList.get(game.gameID).betOU; }
								catch (Exception e) { bet = "3"; }
								betsList.add(bet);
							}
							log.println(users.get(i).userID +", OU units"+unitsParam+": "+ units +"    "+ game.gameID);
						}
						game.betsMapOU.put(unitsParam, betsList);
					}
				}
			} 

			PrintWriter out = new PrintWriter(new FileWriter(outputFileName));

			System.out.println("printing to "+ outputFileName);
			out.println(new GameData_ML().outputFileHeaderLineString());
			int h = 0;
			for (GameData_ML gd : games.values()){
				out.println(gd.toOutputString());
				h++;
			}
			log.close();
			out.close();

			System.out.println("finished");	System.out.println(h +" loops of games.  games length is "+ games.size());
		}catch (Exception e) {e.printStackTrace();}
	}
}



