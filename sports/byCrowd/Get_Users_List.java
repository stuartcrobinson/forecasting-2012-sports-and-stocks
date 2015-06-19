package byCrowd;
//get the list of all wagerline users and their main stats listed on the leaderboard page


import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Get_Users_List {


	static class MyConsensusWebFuncs {

		private static String getSource(WebDriver driver) {

			long end = System.currentTimeMillis() + 10000;
			while (System.currentTimeMillis() < end) {
				try {
					return driver.getPageSource();  
				} catch (Exception e){ }
			}
			return null;
		}

		private static String getXpathText(WebDriver driver, String xpathExpression) {

			long end = System.currentTimeMillis() + 10000;
			while (System.currentTimeMillis() < end) {
				try {
					return driver.findElement(By.xpath(xpathExpression)).getText();  
				} catch (Exception e){ }
			}
			return null;
		}

		public static String getPageTopUser(WebDriver driver) {
			return MyConsensusWebFuncs.getXpathText(driver, "//*[@id='ctrl_leaderboard_dgContestLeaders']/tbody/tr[2]/td[2]");
		}

		static String waitForNewPageUser (String user, WebDriver driver, PrintStream logfile) {

			System.out.println("in waitForNewPageUser");
			String newUser = "pageload_error";
			boolean success = false;
			
			long end = System.currentTimeMillis() + 10000;
			while (System.currentTimeMillis() < end && success == false) {
				try {
					newUser = MyConsensusWebFuncs.getPageTopUser(driver);
					System.out.println("new user: " + newUser);
					System.out.println("old user: " + user);
					if (!user.equals(newUser)) 
						success = true;
				}catch (Exception e){ logfile.println("failed in waitForNewPageDate ************************************************************************8****" + e); }
			}
			
			if (!success){
				System.out.println("going to try reloading page now");
		        ((JavascriptExecutor)driver).executeScript("document.location.reload()");

				
				end = System.currentTimeMillis() + 10000;
				while (System.currentTimeMillis() < end && success == false) {
					try {
						newUser = MyConsensusWebFuncs.getPageTopUser(driver);
						System.out.println("new user: " + newUser);
						System.out.println("old user: " + user);
						if (!user.equals(newUser)) 
							success = true;
					}catch (Exception e){ logfile.println("failed in waitForNewPageDate ************************************************************************8****" + e); }
				}
				
			}
			
			if (!success) System.out.println("sorry, better luck next time");

			
			System.out.println("old user " + user + ".  new user: " + newUser);
			logfile.println("old user " + user + ".  new user: " + newUser);
			return newUser;
		}
	}

	public static void main(String[] args) throws Exception {
		
		String timeStamp = Long.toString(System.currentTimeMillis());
	    WebDriver driver = new FirefoxDriver();
		driver.get("http://contests.covers.com/sportscontests/leaders.aspx?sportID=6&ID=21033&sRec=1&eRec=50&hiddenSportID=0");
		String outputFileName = "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\all_NHL_2013users_" + timeStamp + ".csv";
		String logFileName = "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\all_NHL_2013users_" + timeStamp + ".log";

		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);

		FileOutputStream fOutStream, logFileOutputStream;		
		fOutStream = new FileOutputStream(outputFileName);
		PrintStream fout = new PrintStream(fOutStream);
		logFileOutputStream = new FileOutputStream (logFileName);
		PrintStream logfile = new PrintStream(logFileOutputStream);
		Pattern userIdPattern = Pattern.compile("ur=(.*)&.*");
		Pattern numGamesPattern = Pattern.compile("(\\d*)\\D*(\\d*)\\D*(\\d*).*");
		Matcher userIdMatcher;	
		Matcher numGamesMatcher;

		fout.println("Rank,User,UserID,WLT,n,Pct,Units,");
		
		do {
			String source = MyConsensusWebFuncs.getSource(driver);
			InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(source));
			TagNode node = cleaner.clean(isr);
			Object[] table_array = node.evaluateXPath("//*[@id='ctrl_leaderboard_dgContestLeaders']/tbody");
			if (table_array.length > 0) {
				TagNode table = (TagNode) table_array[0];
				Object[] userRanks = table.evaluateXPath("//tr/td[1]");
				Object[] userNames = table.evaluateXPath("//tr/td[2]");
				Object[] userWLTs = table.evaluateXPath("//tr/td[3]");
				Object[] userPCTs = table.evaluateXPath("//tr/td[4]");
				Object[] userUnits = table.evaluateXPath("//tr/td[5]");
				Object[] userRecordURLs = table.evaluateXPath("//tr/td[6]/a");
				List<Object> urls = new ArrayList<Object>(Arrays.asList(userRecordURLs));
				urls.add(0, null);
				userRecordURLs = urls.toArray();

				for (int i = 1; i < userNames.length; i++){
					String userRank =  ((TagNode) userRanks[i]).getText().toString().trim() ;
					String userName =  ((TagNode) userNames[i]).getText().toString().trim() ;				
					String userWLT =  ((TagNode) userWLTs[i]).getText().toString().trim() ;
					String userPCT =  ((TagNode) userPCTs[i]).getText().toString().trim().replaceAll("%", "") ;
					String userUnit =  ((TagNode) userUnits[i]).getText().toString().trim() ;
					String userRecordURL =  ((TagNode) userRecordURLs[i]).getAttributeByName("href");

					userIdMatcher = userIdPattern.matcher(userRecordURL);									
					userIdMatcher.find();							
					String userID = userIdMatcher.group(1);
					numGamesMatcher = numGamesPattern.matcher(userWLT);
					numGamesMatcher.find();
					int sum = Integer.valueOf(numGamesMatcher.group(1)) + Integer.valueOf(numGamesMatcher.group(2)) + Integer.valueOf(numGamesMatcher.group(3));

					String firstUnitsChar = userUnit.substring(0, 1);
					userUnit = userUnit.replaceAll("\\D", "");
					if (firstUnitsChar.equals("-"))
						userUnit = firstUnitsChar + userUnit;
					fout.println(userRank + "," + userName + "," + userID + "," + userWLT + "," + sum + "," + userPCT + "," + userUnit + ",");
				}
				if (userRanks.length != 51){
					System.out.println("i think it's finished");
					break;		
				}
			}
			String oldUser = MyConsensusWebFuncs.getPageTopUser(driver);
	        ((JavascriptExecutor)driver).executeScript("javascript:__doPostBack('ctrl_leaderboard$next_button','')");
			System.out.println("clicked");
			MyConsensusWebFuncs.waitForNewPageUser(oldUser, driver, logfile);
		} while (true);

		fout.close();
		logfile.close();
	}
}
