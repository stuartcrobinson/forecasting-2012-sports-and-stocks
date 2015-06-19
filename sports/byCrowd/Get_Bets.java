package byCrowd;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

//TODO PROBLEM - this is skipping some days!  maybe when the connection fails, it skips the day instead of redoing it?

//	MUST CHANGE LEAGUE!!!  since some dates listed as NHL are actually nfl- bad data.  fts

//TODO must check league of EVERY GAME BET ON!!!!!!!!!!

//make another file that is a list of all users in a given season along with all of the dates on which they placed a bet


class MyThread implements Runnable {
	private int threadNumber;
	private String strLine;
	private String betsDir;
	private List<String> userIDs;

	Pattern linePattern = Pattern.compile(".* \\+*([-\\d\\.]*).*");
	Matcher lineMatcher;	

	public MyThread(int threadNumber, String strLine, String betsDir, List<String> userIDs) {	
		this.threadNumber = threadNumber;
		this.strLine = strLine;		
		this.betsDir = betsDir;
		this.userIDs = userIDs;
	}

	public synchronized void run() {	

		String league = "NBA";	//sportID=6												//TODO change this for diff leagues		

		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);

		List<String> userDatesFileRow = Arrays.asList(strLine.split("\\s*,\\s*"));

		String userName = userDatesFileRow.get(0).replaceAll("[\\W_]", "");
		String userID = userDatesFileRow.get(1);

		if (userIDs.contains(userID)) {
			System.out.format("%nskipping user %s", userID);
			return;
		}

		String outputFileString = "";

		for (int i = 2; i < userDatesFileRow.size(); i++){
			String dateLeague = "";
			String dateStr = userDatesFileRow.get(i);
			String bets_url = "http://contests.covers.com/sportscontests/picksByDate.aspx?date="+ dateStr +"&ur="+ userID +"&sportID=9";	//TODO change sport ID		  //"http://contests.covers.com/sportscontests/picksByDate.aspx?date=12/26/2012&ur=353861&sportID=9";

			long end = System.currentTimeMillis() + 60_000;
			boolean tryAgain = false;

			do {
				try {
					tryAgain = false;
					URL url = new URL(bets_url);
					URLConnection con = url.openConnection();
					con.setConnectTimeout(20_000);
					con.setReadTimeout(20_000);

					//TODO what happens when it hits a read time out?  does it try it again?  or just lose the data....
					InputStream in1 = con.getInputStream();

					String source = IOUtils.toString( in1 );
					IOUtils.closeQuietly(in1);
					InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(source));
					TagNode node = cleaner.clean(isr);

					Object[] table_array = node.evaluateXPath("//*[@id='innercontent']/table/tbody");

					if (table_array.length > 0) {
						TagNode table = (TagNode) table_array[0];

						Object[] leagueNameCells = table.evaluateXPath("//tr[position() > 1]/td[1]");
						Object[] teamNameCells = table.evaluateXPath("//tr[position() > 1]/td[2]");
						Object[] teamScoreCells = table.evaluateXPath("tr[position() > 1]/td[3]");
						Object[] betPickCells = table.evaluateXPath("tr[position() > 1]/td[4]");
						Object[] betResultCells = table.evaluateXPath("tr[position() > 1]/td[6]");
						Object[] unitsCells = table.evaluateXPath("tr[position() > 1]/td[7]");

						if (leagueNameCells.length == 0) {
							System.out.format("%nempty table %s", bets_url);
							throw new Exception("Empty table");
						}

						for (int j = 0; j < teamNameCells.length; j+=2){

							String tm1 = "", tm2 = "", tm1Pts = "", tm2Pts = "", betCell1 = "", betCell2 = "", resultCell1 = "", resultCell2 = "", unitsCell1 = "", unitsCell2 = "";
							String lineATS = "", lineOU = "", betATS = "", betOU = "", didWinATS = "", didWinOU = "", unitsATS = "", unitsOU = "";

							dateLeague = ((TagNode) leagueNameCells[j]).getText().toString().trim();


							tm1 = ((TagNode) teamNameCells[j]).getText().toString().trim();
							tm2 =  ((TagNode) teamNameCells[j+1]).getText().toString().trim();

							if (!dateLeague.equals(league)){
								System.out.format("%nbad league %s %14s %14s     %s", dateLeague, tm1, tm2, bets_url);
								continue;
							}

							tm1Pts =  ((TagNode) teamScoreCells[j]).getText().toString().trim();
							tm2Pts =  ((TagNode) teamScoreCells[j+1]).getText().toString().trim();

							betCell1 =  ((TagNode) betPickCells[j]).getText().toString().trim();
							betCell2 =  ((TagNode) betPickCells[j+1]).getText().toString().trim();

							resultCell1 =  ((TagNode) betResultCells[j]).getText().toString().trim();
							resultCell2 =  ((TagNode) betResultCells[j+1]).getText().toString().trim();

							unitsCell1 =  ((TagNode) unitsCells[j]).getText().toString().trim();
							unitsCell2 =  ((TagNode) unitsCells[j+1]).getText().toString().trim();


							if (betCell1.contains(tm1) | betCell1.contains(tm2)){				//if top cell is ATS
								if (betCell1.contains(tm1))
									betATS = "1";
								if (betCell1.contains(tm2))
									betATS = "2";
								if (resultCell1.contains("LOSS"))
									didWinATS = "0";
								if (resultCell1.contains("WON"))
									didWinATS = "1";
								if (resultCell1.contains("PUSH"))
									didWinATS = "-1";
								unitsATS = unitsCell1;

								lineMatcher = linePattern.matcher(betCell1);									
								lineMatcher.find();							
								lineATS = lineMatcher.group(1);
							}
							if (betCell2.contains(tm1)| betCell2.contains(tm2)){					//if bottom cell is ATS
								if (betCell2.contains(tm1))
									betATS = "1";
								if (betCell2.contains(tm2))
									betATS = "2";
								if (resultCell2.contains("LOSS"))
									didWinATS = "0";
								if (resultCell2.contains("WON"))
									didWinATS = "1";
								if (resultCell2.contains("PUSH"))
									didWinATS = "-1";
								unitsATS = unitsCell2;

								lineMatcher = linePattern.matcher(betCell2);									
								lineMatcher.find();							
								lineATS = lineMatcher.group(1);
							}
							if (betCell1.contains("Over") | betCell1.contains("Under") ){					//if top cell is OU
								if (betCell1.contains("Over"))
									betOU = "1";
								if (betCell1.contains("Under"))
									betOU = "2";

								if (resultCell1.contains("LOSS"))
									didWinOU = "0";
								if (resultCell1.contains("WON"))
									didWinOU = "1";
								if (resultCell1.contains("PUSH"))
									didWinOU = "-1";
								unitsOU = unitsCell1;

								lineMatcher = linePattern.matcher(betCell1);									
								lineMatcher.find();							
								lineOU = lineMatcher.group(1);
							}
							if (betCell2.contains("Over") | betCell2.contains("Under") ){			//if bottom cell is OU
								if (betCell2.contains("Over"))
									betOU = "1";
								if (betCell2.contains("Under"))
									betOU = "2";

								if (resultCell2.contains("LOSS"))
									didWinOU = "0";
								if (resultCell2.contains("WON"))
									didWinOU = "1";
								if (resultCell2.contains("PUSH"))
									didWinOU = "-1";
								unitsOU = unitsCell2;

								lineMatcher = linePattern.matcher(betCell2);									
								lineMatcher.find();							
								lineOU = lineMatcher.group(1);
							}
							//							System.out.println(dateStr +","+ tm1 +","+ tm2 +","+ tm1Pts +","+ tm2Pts +","+ lineATS +","+ betATS +","+ didWinATS +","+ unitsATS +","+ lineOU +","+betOU +","+ didWinOU +","+ unitsOU + "     ");

							//							System.out.format("t%d %7s %s %12s %12s %3s %3s %5s %2s %2s %5s %4s %s %s %4s%n", threadNumber, userID, dateStr, tm1, tm2, tm1Pts, tm2Pts, lineATS, betATS, didWinATS, unitsATS, lineOU, betOU, didWinOU, unitsOU);
							System.out.print(".");

							String outputLine = dateStr +","+ tm1 +","+ tm2 +","+ tm1Pts +","+ tm2Pts +","+ lineATS +","+ betATS +","+ didWinATS +","+ unitsATS +","+ lineOU +","+betOU +","+ didWinOU +","+ unitsOU;
							outputFileString = outputFileString + outputLine + "\n";
						}
					}
				} catch (Exception e){ 
					tryAgain = true;
					System.out.println("fail 1 " + userID + "_"+ userName +" "+ e +" "+ bets_url);
					e.printStackTrace();
				}
			} while (tryAgain && System.currentTimeMillis() < end);
		}
		try {
			FileWriter fstream = new FileWriter(betsDir + "\\" + userID + "_"+ userName +".csv");
			PrintWriter out = new PrintWriter(fstream);
			out.println("date,tm1,tm2,tm1Pts,tm2Pts,lineATS,betATS,didWinATS,unitsATS,lineOU,betOU,didWinOU,unitsOU");
			out.println(outputFileString);
			System.out.println("\nthread "+ threadNumber + " printed "+ userID);
			out.close();
		}catch (Exception e) {e.printStackTrace();}
	}
}

public class Get_Bets {

	public static void main(String[] args) throws IOException, XPatherException, InterruptedException{
		System.out.println("start");
		String betsDir = "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\NBA_2013\\user_bets_NBA_2013";//_" + timeStamp;

		File[] files = new File(betsDir).listFiles();
		List<String> userIDs = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) 
			userIDs.add( files[i].getName().substring(0, files[i].getName().indexOf("_")) );

		List<String> lines = FileUtils.readLines(new File("C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\NBA_2013\\all_users_bet_dates_NBA_2013.csv"));
		Collections.sort(lines);

		ExecutorService pool = Executors.newFixedThreadPool(5);				//30: 2 minutes: 28 to 60. 32 // 10: 2 minutes: 60 to 61. //50: 61 to 86// 25:86 to 89//30: 89 to 92 // 10: 92 to 
		//362 at 4:32
		
		int i = 0;
		for (String line : lines)
			pool.execute(new MyThread(i++, line, betsDir, userIDs));

		pool.shutdown();
		pool.awaitTermination(100, TimeUnit.HOURS);
		System.out.println("finished");
	}
}

//robert x cringly - revenge of the nerds
//web 2.0
//the soul of a new machine - tracey kidder
//pattern on the stone
// same guy did the thinking machine - phd at mit
//
//