package byCrowd;

//record lines from the perspective of the HOME TEAM!!! tm2!

//untested

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

class TabulateHelper{
	public final String betsDir 			= "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\user_bets_NHL_2011";
	public final String outputFileName 		= "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\tabulated_NHL_2011_"+ Long.toString(System.currentTimeMillis()) +".csv";
	public final int p 						= 500;
	public final int[] unitsParameters 		= new int[] {4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 15, 20, 30, 35, 40, 50, 80, 130};	// 4, 5, 6, 7, 8, 9, 10, 15, 20, 30, 40, 70, 100, 120, 140};
	public final int NUM_USERS_TO_RANK 		= 50;
	public final int NUM_FILES_TO_READ 		= -1;									//-1 for all files
	public final SimpleDateFormat sdf 		= new SimpleDateFormat("MM/dd/yy");

	public TabulateHelper(){}
	public String makeGameID(String tm1, String tm2, Date date){
		String gameID = sdf.format(date) + " - " +  tm1 + " - " + tm2;
		return gameID;		
	}
	public List<GameData> gamesOnDate(Map<String, GameData> games, Date date) {
		List<GameData> gameDatas = new ArrayList<GameData>();
		for (GameData gd : games.values()){
			if (gd.date.equals(date))
				gameDatas.add(gd);
		}
		return gameDatas;
	}
	public int compareATS_reverseOrder(User user1, User user2, Date date, int unitsParam) {
		Integer user_1_units = user1.getCumUnitsATS(date, unitsParam);
		if (user_1_units == null) user_1_units = Integer.MIN_VALUE;

		Integer user_2_units = user2.getCumUnitsATS(date, unitsParam);
		if (user_2_units == null) user_2_units = Integer.MIN_VALUE;

		return user_2_units.compareTo(user_1_units);
	}
	public int compareOU_reverseOrder(User user1, User user2, Date date, int unitsParam) {

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
class User {
	String userID;
	Map<String, UserGameBet> gamesList;
	Map<Date, Map<Integer, Integer>> cumUnitsMapATS;
	Map<Date, Map<Integer, Integer>> cumUnitsMapOU;
	public User(String arg0){
		this.userID = arg0;
		this.gamesList = new TreeMap<String, UserGameBet>();
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
		for (UserGameBet ugb : this.gamesList.values())
			out = out + r + userID +s+ ugb.toString();

		//unitsMapATS
		for (Date date : cumUnitsMapATS.keySet()){
			for (Integer param : cumUnitsMapATS.get(date).keySet()){
				out = out + r + userID +s+ new TabulateHelper().formatDate(date) + s + param + s + "ATS units: "+ cumUnitsMapATS.get(date).get(param);
			}
		}
		//unitsMapOU
		for (Date date : cumUnitsMapOU.keySet()){
			for (Integer param : cumUnitsMapATS.get(date).keySet()){
				out = out + r + userID +s+ new TabulateHelper().formatDate(date) + s + param + s +  "OU units: "+ cumUnitsMapOU.get(date).get(param);
			}
		}		
		return out;
	}
}
class UserGameBet  implements Comparator<UserGameBet>, Comparable<UserGameBet>{	
	int p;
	Date date;
	String gameID;
	String betATS, betOU;
	Double lineATS, lineOU;
	Integer unitsATS, unitsOU;
	public UserGameBet(Date date, String gameID, String betATS, String betOU, Double lineATS, Double lineOU, Integer unitsATS, Integer unitsOU){
		this.p 			= new TabulateHelper().p;
		this.date 		= date;
		this.gameID 	= gameID;
		this.betATS 	= betATS;
		this.betOU 		= betOU;
		this.lineATS 	= lineATS;
		this.lineOU 	= lineOU;
		this.unitsATS 	= unitsATS;
		this.unitsOU 	= unitsOU;
		
		if (this.unitsATS == null) this.unitsATS = 0;
		if (this.unitsOU == null) this.unitsOU = 0;
	}	
	@Override
	public int compareTo(UserGameBet arg0) {
		return this.date.compareTo(arg0.date);
	}
	@Override
	public int compare(UserGameBet arg0, UserGameBet arg1) {
		return arg0.date.compareTo(arg1.date);
	}
	@Override 
	public String toString(){
		String c = ", ";
		return "(gameID, betATS, betOU, lineATS, lineOU, unitsATS, unitsOU): " + gameID +c+ betATS +c+ betOU +c+ lineATS +c+ lineOU +c+ unitsATS +c+ unitsOU;
	}
}

class GameData  implements Comparator<GameData>, Comparable<GameData>{
	Date date;
	String gameID, tm1, tm2;
	Integer tm1Pts, tm2Pts;
	Double lineATS, lineOU;
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
		else if (tm1Pts + tm2Pts > lineOU)		resultOU = 1;
		else if (tm1Pts + tm2Pts < lineOU)		resultOU = 2;
		else									resultOU = 3;
		
//		System.out.format("%d %40s  %2d, %2d, %f%n", returnValue, gameID, tm1Pts, tm2Pts, lineOU);

	}

	public GameData(){}
	public GameData (String gameID, Date date, String tm1, String tm2, int tm1Pts, int tm2Pts, Double lineATS, Double lineOU){
		this.gameID = gameID;
		this.date = date;
		this.tm1 = tm1;
		this.tm2 = tm2;
		this.tm1Pts = tm1Pts;
		this.tm2Pts = tm2Pts;
		this.lineATS = lineATS;
		this.lineOU = lineOU;
		this.betsMapATS = new TreeMap<Integer, List<String>>();
		this.betsMapOU = new TreeMap<Integer, List<String>>();
	}
	@Override
	public int compareTo(GameData arg0) {
		return this.date.compareTo(arg0.date);
	}
	@Override
	public int compare(GameData arg0, GameData arg1) {
		return arg0.date.compareTo(arg1.date);
	}

	public String toString(){
		String s = ", ";
		String out = "(gameID, tm1Pts, tm2Pts, lineATS, lineOU, resultATS, resultOU): " + gameID +s+ tm1Pts +s+ tm2Pts +s+ lineATS +s+ lineOU +s+ resultATS +s+ resultOU;
		return out;
	}

	public String toOutputString(){
		String c = ",";
		String out = gameID +c+ new TabulateHelper().sdf.format(date) +c+ tm1 +c+ tm2 +c+ tm1Pts +c+ tm2Pts +c+ lineATS +c+ lineOU +c+ resultATS +c+ resultOU +c;

		for (Integer unitsParam : new TabulateHelper().unitsParameters){
			if (betsMapATS.get(unitsParam) != null)
				out = out + betsMapATS.get(unitsParam).toString().replaceAll("[\\[\\],]", "") +c;
			if (betsMapOU.get(unitsParam) != null)
				out = out + betsMapOU.get(unitsParam).toString().replaceAll("[\\[\\],]", "") +c;
		}
		return out;
	}
	public String outputFileHeaderLineString(){

		String c = ",";
		String out = "gameID,date,tm1,tm2,tm1Pts,tm2Pts,lineATS,lineOU,resultATS,resultOU,";

		for (Integer unitsParam : new TabulateHelper().unitsParameters){
			out = out + "betsATS_" + unitsParam + c;
			out = out + "betsOU_" + unitsParam + c;
		}
		return out;
	}
}


public class Tabulate_nonML {

	static int[] unitsParameters 		= new TabulateHelper().unitsParameters; 
	static int p 						= new TabulateHelper().p;
	static SimpleDateFormat sdf 		= new TabulateHelper().sdf;
	static final long DAY_IN_MILLIS 	= (1000 * 60 * 60 * 24);
	static int NUM_USERS_TO_RANK 		= new TabulateHelper().NUM_USERS_TO_RANK;
	static int NUM_FILES_TO_READ 		= new TabulateHelper().NUM_FILES_TO_READ;
	static String betsDir 				= new TabulateHelper().betsDir;
	static String outputFileName 		= new TabulateHelper().outputFileName; 

	/**
	 * prepare data for analysis.  save in files.
	 * @throws Exception 
	 */
	public static void main(String[] args)  {
		try {
			PrintWriter log = new PrintWriter(new FileWriter("C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\testOutput.txt"));

			log.println("start");

			Set<Date> dates = new TreeSet<Date>();
			List<User> users = new ArrayList<User>();
			Map<String, GameData> games = new TreeMap<String, GameData>();

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
				User user = new User(userID);
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
					String  gameID		= new TabulateHelper().makeGameID(tm1, tm2, date);
					dates.add(date);								
					user.cumUnitsMapATS.put(date, new TreeMap<Integer, Integer>());
					user.cumUnitsMapOU.put(date, new TreeMap<Integer, Integer>());

					if ( !betATS.equals("1") && !betATS.equals("2") ) 	betATS = "3";
					if ( !betOU.equals("1")  && !betOU.equals("2") ) 	betOU = "3";

					Double lineATS = null, lineOU = null;
					Integer unitsATS = null, unitsOU = null;
					
					if (betATS.equals("1"))		lineATS 	= Double.parseDouble(lineATS_s)*-1;		//lineATS from perspective of home team, by wagerline convention
					if (betATS.equals("2"))		lineATS 	= Double.parseDouble(lineATS_s);
					if (lineOU_s.length()>0)	lineOU 		= Double.parseDouble(lineOU_s);
					if (unitsATS_s.length()>0)	unitsATS	= Integer.parseInt(unitsATS_s);
					if (unitsOU_s.length()>0)	unitsOU		= Integer.parseInt(unitsOU_s);

					user.gamesList.put(gameID, new UserGameBet(date, gameID, betATS, betOU, lineATS, lineOU, unitsATS, unitsOU))	;
					
					if (games.containsKey(gameID)){
						GameData alreadyAddedGame = games.get(gameID); 
						if (lineATS != null) {
							if (alreadyAddedGame.lineATS == null)
								alreadyAddedGame.lineATS = lineATS;
						}
						if (alreadyAddedGame.lineOU == null && lineOU != null){
							alreadyAddedGame.lineOU = lineOU;
						}
					}
					else {
						GameData gameData = new GameData(gameID, date, tm1, tm2, tm1Pts, tm2Pts, lineATS, lineOU);
						games.put(gameID, gameData);
					}
				}
				if (user.has_data_on_more_than_one_day())
					users.add(user);
				reader.close();
			}


			for (GameData game : games.values()){
				game.calculateGameResultATS();
				game.calculateGameResultOU();
			}

			//now fill the units map for each user by day
			int k = 1;
			for (User user : users){	
				System.out.println(k++ + ".  filling units map for user "+ user.userID);

				for (int unitsParameter : unitsParameters){
					for (UserGameBet game : user.gamesList.values()){			

						Date gameDate = game.date;

						int unitsATS = 0, unitsOU = 0;
						for (UserGameBet iterativeGame : user.gamesList.values()){
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

					Collections.sort(users, new Comparator<User>() {
						@Override
						public int compare(User user1, User user2) {
							return new TabulateHelper().compareATS_reverseOrder(user1, user2, date, unitsParam);
						} }); 
					for (GameData game : new TabulateHelper().gamesOnDate(games, date)){ 
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

					Collections.sort(users, new Comparator<User>() {
						@Override
						public int compare(User user1, User user2) {
							return new TabulateHelper().compareOU_reverseOrder(user1, user2, date, unitsParam);
						} }); 
					for (GameData game : new TabulateHelper().gamesOnDate(games, date)){
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
			out.println(new GameData().outputFileHeaderLineString());
			int h = 0;
			for (GameData gd : games.values()){
				out.println(gd.toOutputString());
				h++;
			}
			log.close();
			out.close();

			System.out.println("finished");	System.out.println(h +" loops of games.  games length is "+ games.size());
		}catch (Exception e) {e.printStackTrace();}
	}
}



