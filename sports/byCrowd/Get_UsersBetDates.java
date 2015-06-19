package byCrowd;
/*//the goal of this commented code is to avoid having to check every single month.  to get the list of bet months from the dropdown.  but that thing sucks to use because it's loaded by javascript so not all the motnhs show up int he html.  something that might work is going to the first month (through oct, nov and dec) - put all the found months in a list.  and then go to january -- and put all those months in the list (set would be better).  maybe if you go to jan, then you see all the active bet months of the second year of the season?  even if no bets in jan? NOPE. this doesn't work.  if there's no bets in jan, then the html for jan will show no months.  so treat the two years as separate things.  first go through the first year until you hit an active month and record all the displayed months.  then go tot eh second year of the season and go through the calendar months until you get one that is activey bet on and reocrd all the listed months.  that should work.  later TODO
 * 
 * 
Element monthBox;
Elements months;
 * 
 * 		for (String date : urlAttemptDates){

					url = "http://contests.covers.com/sportscontests/recordsByDate.aspx?interval=overall&sportID=6&date="+ date +"&ur=" + userID ;
					System.out.println(url);
					conn = conn.url(url);

					end = System.currentTimeMillis() + millisToWait;
					tryAgain = false;
					connectionDied = false;
					do {
						try {
							tryAgain = false;
							doc = conn.get();							
							System.out.println("connecting 1");

							monthBox = doc.getElementById("objRecordHeader_ddlMonth");
							months = monthBox.getElementsByTag("option");								//dies here
							for (Element month : months){														//for nhl 
								if (Integer.parseInt(month.attr("value")) > 8 )			
									monthDateList.add(month.attr("value") + "/01/2010");	
								else
									monthDateList.add(month.attr("value") + "/01/2011");	
							}

						} catch (Exception e){ 
							connectionDied = true;
							tryAgain = true;
							System.out.println("fail 1 " + e);
							try {Thread.sleep(1000);} 
							catch (InterruptedException e1) {e1.printStackTrace();							}
						}
					} while (tryAgain && System.currentTimeMillis() < end);
					if (connectionDied) System.out.println("connection died in loop 1");
					connectionDied = false;
					if (monthDateList.size() > 0) break;
				}
 */		//		monthDateList = urlAttemptDates;							//this replaces commented code above
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import com.google.common.collect.Lists;

//MUST EDIT oldOutputFileName, CONTEST ID, SPORT ID, AND YEAR!!!!!!!!!!!!!!!!!!!!!!!!!! when changing league or ssn

//makes another file that is a list of all users in a given season along with all of the dates on which they placed a bet

//now: ssn 2011, nhl

class ResultsPrinter {

	int threadNumber;
	List<String> results;
	String fileName;

	public ResultsPrinter(int threadNumber, List<String> results, String fileName){
		this.threadNumber = threadNumber;
		this.results = results;
		this.fileName = fileName;
	}


	public void run() {

		File outputfile = new File(fileName);
		FileWriter fw;
		try {
			fw = new FileWriter(outputfile);
			PrintWriter out = new PrintWriter(fw);
			out.println("UserName,UserID,Dates");	

			System.out.println("thread " + threadNumber + " printing");
			
			for (String line : results)
				out.println(line);

			out.close();
		} catch (Exception e) { System.out.println("couldn't print this to file -- this doesn't matter" + e);}	
	}


}

class MyThread2 implements Runnable {

	String outputFileName;
	int threadNumber;
	//	PrintWriter out;

	List<String> outputLinesList;

	List<String> userIDs;
	List<String> userNames;

	public MyThread2(int threadNumber, List<String> userIDs, List<String> userNames, List<String> outputLinesList, String outputFileName){
		this.outputFileName = outputFileName;
		this.threadNumber = threadNumber;
		this.userIDs = userIDs;
		this.userNames = userNames;
		//		this.out = out;
		this.outputLinesList = outputLinesList;
	}

	@Override
	public synchronized void run() {

		long millisToWait = 600*1000;

		Connection conn =  Jsoup.connect("https://accounts2.covers.com/").timeout(60*1000);
		try {
			Document doc = conn.data("Username", "topconsensus").data("Password", "rockshocker").userAgent("Mozilla").post();	//"Username" is both the field id and name
			System.out.println("\npage title: "+ doc.title());	//took 4 seconds

			List<String> urlAttemptDates = new ArrayList<String>();
			urlAttemptDates.add("10/01/2012");
			urlAttemptDates.add("11/01/2012");
			urlAttemptDates.add("12/01/2012");
			urlAttemptDates.add("01/01/2013");
			urlAttemptDates.add("02/01/2013");
			urlAttemptDates.add("03/01/2013");
			urlAttemptDates.add("04/01/2013");
			urlAttemptDates.add("05/01/2013");
			urlAttemptDates.add("06/01/2013");

			for (int i = 0; i < userIDs.size(); i++){
				String userID = userIDs.get(i);
				String userName = userNames.get(i);

				List<String> dateList = new ArrayList<String>();
				List<String> monthDateList = new ArrayList<String>();
				String url;

				long end;
				boolean tryAgain;
				boolean connectionDied;

				monthDateList = urlAttemptDates;							//this replaces commented code above

				//now go to "records by date" page per month and scrape the days on which bets were made
				for (String monthDate : monthDateList){
					//load page, scrape dates in to dateList
					url = "http://contests.covers.com/sportscontests/recordsByDate.aspx?interval=overall&sportID=6&date=" + monthDate + "&ur=" + userID;
					conn = conn.url(url);
					connectionDied = false;

					end = System.currentTimeMillis() + millisToWait;
					tryAgain = false;
					do {
						try {
							tryAgain = false;
							doc = conn.get();
							System.out.print(".");
						} catch (Exception e){ 
							connectionDied = true;
							tryAgain = true;
						}
					} while (tryAgain && System.currentTimeMillis() < end);

					if (connectionDied) System.out.println("connection died in loop 2");
					connectionDied = false;

					Elements dates = doc.select(".left:not(.highlight .left):not(.datahead .left)");
					if (dates.size() > 0){
						
						//if the month in the dropbox that is "selected ="selected"" is the same as the month of dates that are actually listed, then it is real data.
						
						for (Element date : dates){
							dateList.add(date.text());
						}
					}
				}

				String datesSpaceSeparated = dateList.toString().replaceAll(",|]|\\[", "").replaceAll(" ", ","); 

				String result = userName +","+ userID +","+ datesSpaceSeparated;

				outputLinesList.add(result);

				if (i % 10 == 3)
					new ResultsPrinter(threadNumber, outputLinesList, outputFileName).run();

				System.out.format("%nthread %d, user %d. %s %s %s%n%n", threadNumber, i, userName, userID, datesSpaceSeparated);
			}		

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}


public class Get_UsersBetDates {

	public static void main(String[] args) throws IOException, InterruptedException{

		List<String> outputLines = new ArrayList<String>();
		List<String> previouslyRecordedUserIDs = new ArrayList<String>();

		String line, oldOutputFileName = "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\all_NHL_2013users_bet_dates_1373603653974.csv";

		if (oldOutputFileName != null){
			BufferedReader reader2 = new BufferedReader(new FileReader(new File(oldOutputFileName)));
			line = reader2.readLine();
			while ( (line = reader2.readLine()) != null) {
				outputLines.add(line);
				String[] tokens = line.split(",");
				previouslyRecordedUserIDs.add(tokens[1]);
			}
			reader2.close();
		}

		boolean threadMe = true;
		String timeStamp = Long.toString(System.currentTimeMillis());
		String outputFileName = "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\all_NHL_2013users_bet_dates_" + timeStamp + ".csv";

		String usersListFileName = "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\all_NHL_2013users_1373602398919.csv";
		File file =  new File(usersListFileName);
		FileReader fileReader = new FileReader(file);
		BufferedReader reader = new BufferedReader(fileReader);

		List<String> userIDs = new ArrayList<String>();
		List<String> userNames = new ArrayList<String>();

		line = reader.readLine();
		while ( (line = reader.readLine()) != null) {
			String[] tokens = line.split(",");
			if (!previouslyRecordedUserIDs.contains(tokens[2])){
				userIDs.add(tokens[2]);
				userNames.add(tokens[1]);
			}
		}
		fileReader.close();
		reader.close();

		if (threadMe){
			int numThreads = 10;
			List<List<String>>  userIDsz = Lists.partition(userIDs, userIDs.size()/numThreads);	
			List<List<String>>  userNamesz = Lists.partition(userNames,  userIDs.size()/numThreads);		
			ExecutorService pool = Executors.newFixedThreadPool(numThreads);

			for (int i = 0; i < userIDsz.size(); i++) 
				pool.execute(new MyThread2(i, userIDsz.get(i), userNamesz.get(i), outputLines, outputFileName));
			pool.shutdown();
			pool.awaitTermination(8, TimeUnit.HOURS);
		}
		else
			new MyThread2(1, userIDs, userNames, outputLines, outputFileName).run();

		new ResultsPrinter(-1, outputLines, outputFileName).run();
		
		System.out.println("THIS IS THE SIZE &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + outputLines.size() +"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
	}
}
