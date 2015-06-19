package byStats;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.collect.Lists;

/* how can i make this better?  readability?  more documentation? 
 * 
 * removed all non-critical funcionality.  removed all scores for which i don't have lines data (periods, innings, quarters, halves)
 * 9.23.2012 */
public class WagerlineScraper {

	final static List<String> urls = Arrays.asList(
	//		"http://scores.covers.com/basketball-scores-matchups.aspx",
	//	"http://scores.covers.com/hockey-scores-matchups.aspx",		
	//				"http://scores.covers.com/canadian-football-scores-matchups.aspx",
	//"http://scores.covers.com/AFL-football-scores-matchups.aspx",
	//				"http://scores.covers.com/college-basketball-scores-matchups.aspx",
	//				"http://scores.covers.com/college-football-scores-matchups.aspx",
	"http://scores.covers.com/womens-basketball-scores-matchups.aspx"); //,
	//"http://scores.covers.com/football-scores-matchups.aspx",
	//"http://scores.covers.com/baseball-scores-matchups.aspx");					//hockey and baseball are ML sports

	static class MyWebFuncs {
		private static List<String> getSeasons(WebDriver driver) {			
			List<String> seasons = new ArrayList<String>();
			for (WebElement e : MyWebFuncs.getXpathWebElements(driver,"//*[@id='DropList']/option"))
				seasons.add(e.getText());
			seasons = Lists.reverse(seasons);
			seasons.remove(seasons.size()-1);

			//			seasons.add("2009");

			return seasons;
		}

		private static List<WebElement> getXpathWebElements(WebDriver driver, String xpathExpression) {

			long end = System.currentTimeMillis() + 1000;
			while (System.currentTimeMillis() < end) {
				try {
					return driver.findElements(By.xpath(xpathExpression)); 
				} catch (Exception e){ }
			}
			return null;
		}

		private static String getXpathText(WebDriver driver, String xpathExpression) {

			long end = System.currentTimeMillis() + 1000;
			while (System.currentTimeMillis() < end) {
				try {
					return driver.findElement(By.xpath(xpathExpression)).getText();  
				} catch (Exception e){ }
			}
			return null;
		}

		private static String getXpathAttribute(WebDriver driver, String xpathExpression,String attribute) {

			long end = System.currentTimeMillis() + 1000;
			while (System.currentTimeMillis() < end) {
				try {
					return driver.findElement(By.xpath(xpathExpression)).getAttribute(attribute);  
				} catch (Exception e){ }
			}
			return null;
		}

		private static String getBoxScoreMLoddsOU(WebDriver driver, String id, int oddsColAdjust) {
			return  MyWebFuncs.getXpathText(driver, "//*[@id='HomeScore_"+ id +"']/following-sibling::td["+ (2+oddsColAdjust) +"]");
		}

		private static String getBoxScoreML(WebDriver driver, String id, int oddsColAdjust, String Visit_or_Home) {
			return MyWebFuncs.getXpathText(driver, "//*[@id='"+ Visit_or_Home +"Score_"+ id +"']/following-sibling::td["+ (1+oddsColAdjust) +"]");			
		}

		/*  spread = away minus home */
		private static String getBoxScoreNonMLsportOdds(WebDriver driver, String id, String total_or_spread) {

			try {
				String myXpath = "//td[@class='datac' and @id[contains(., '"+ total_or_spread +"')] and @id[contains(., '"+ id +"')]]";
				String oddsPrintedValue = MyWebFuncs.getXpathText(driver, myXpath);
				if (total_or_spread.contains("otal"))
					return oddsPrintedValue;
				else {
					String spreadID = MyWebFuncs.getXpathAttribute(driver, myXpath, "id");
					if (spreadID.contains("bottom"))
						return oddsPrintedValue;
					else if (spreadID.contains("top"))
						return String.valueOf(-1 * Double.valueOf(oddsPrintedValue));
				}
				return "error";
			}catch (Exception e) {
				return "error";
			}

		}

		private static String getBoxScoreTeamScore(WebDriver driver, String id, String Visit_or_Home) {
			return MyWebFuncs.getXpathText(driver, "//*[@id='"+ Visit_or_Home +"Score_"+ id +"']");	
		}

		private static String getBoxScoreTableCellName(WebDriver driver, String id,	int tr, int td) {
			return MyWebFuncs.getXpathText(driver, "//div[@id='Game_"+ id +"']//tr["+ tr +"]/td["+ td +"][@class='datab']");			
		}

		public static String getGameDateTime(WebDriver driver) {
			return MyWebFuncs.getXpathAttribute(driver, "//*[@id='gamedatetime']", "Value");
		}

		static String waitForNewPageDate (String date, WebDriver driver, PrintStream logfile) {

			String newDate = "date_error";
			long end = System.currentTimeMillis() + 20000;
			while (System.currentTimeMillis() < end) {
				try {
					newDate = MyWebFuncs.getGameDateTime(driver);
					if (!date.equals(newDate)) 
						break;
				}catch (Exception e){ logfile.println("failed in waitForNewPageDate ************************************************************************8****" + e); }
			}
			System.out.println("old date " + date + ".  new date: " + newDate);
			logfile.println("old date " + date + ".  new date: " + newDate);
			return newDate;
		}


		public static boolean try_to_go_to_the_next_month(String date, WebDriver driver, PrintStream logfile) {
			try {
				driver.findElement(By.xpath("//*[@id='cal']/table/tbody/tr[1]/td/a/b[text()='>>']")).click();
				date = MyWebFuncs.waitForNewPageDate(date, driver, logfile);			
				return true;
			}
			catch (Exception e){ 
				return false; 
			}
		}


		public static void scrapeCurrentDay(String date, WebDriver driver, ArrayList<String> boxIDs, PrintStream fout, PrintStream logfile, String url) throws InterruptedException {

			int oddsColAdjust = 0;

			if (url.contains("hockey")) 	oddsColAdjust = 0;
			if (url.contains("baseball"))	oddsColAdjust = 2;

			String printString = "printstring_error";
			String awayName, homeName, awayFinal, homeFinal, awayML = null, homeML = null, tfb = null, sfb;

			List<String> oldBoxIDs = new ArrayList<String>(boxIDs);
			boxIDs.clear();

			for (WebElement boxscore : MyWebFuncs.getXpathWebElements(driver, "//div[@class='game-box']")) 				//get boxscore IDs on a page
				boxIDs.add(boxscore.getAttribute("id").replace("Game_", ""));		

			logfile.println(boxIDs);

			Collections.sort(boxIDs);

			if (boxIDs.equals(oldBoxIDs)) return;																//cuz nfl puts the same data on every day for a week sometime

			for (String id : boxIDs){		
				logfile.println("box id: " + id);

				awayName = MyWebFuncs.getBoxScoreTableCellName(driver, id, 2, 1);			
				homeName = MyWebFuncs.getBoxScoreTableCellName(driver, id, 3, 1);				
				awayFinal = MyWebFuncs.getBoxScoreTeamScore(driver,id,"Visit");  
				homeFinal = MyWebFuncs.getBoxScoreTeamScore(driver,id,"Home");

				if (url.contains("basketball") || url.contains("football")){

					tfb = MyWebFuncs.getBoxScoreNonMLsportOdds(driver,id,"total");
					sfb = MyWebFuncs.getBoxScoreNonMLsportOdds(driver,id,"spread");

					printString = date +","+ awayName +","+ homeName +","+ sfb +","+ tfb +","+ awayFinal +","+ homeFinal; 
				}
				if (url.contains("hockey") || url.contains("baseball")){
					awayML = MyWebFuncs.getBoxScoreML(driver,id,oddsColAdjust,"Visit"); 
					homeML = MyWebFuncs.getBoxScoreML(driver,id,oddsColAdjust,"Home");
					tfb = MyWebFuncs.getBoxScoreMLoddsOU(driver,id,oddsColAdjust);

					printString =  date +","+ awayName +","+ homeName +","+ awayML +","+ homeML +","+ tfb +","+ awayFinal +","+ homeFinal ; 

				}						
				printString = printString.replace(" ", "");

				System.out.println(printString);	
				fout.println(printString);	
				logfile.println(printString);	
			}
		}


	}


	public static void main(String[] args) throws Exception {

		FileOutputStream fOutStream, logFileOutputStream;		
		String date;
		ArrayList<String> boxIDs = new ArrayList<String>();
		List<String> gameDatesThisMonth = new ArrayList<String>();
		List<String> seasons = new ArrayList<String>();
		WebDriver driver = new FirefoxDriver();
		WebDriverWait wait = new WebDriverWait(driver, 30);
		String timeStamp = Long.toString(System.currentTimeMillis());
		logFileOutputStream = new FileOutputStream (MyFiles.scraper_log	 +timeStamp +".log");
		PrintStream logfile = new PrintStream(logFileOutputStream);
		for (String url : urls){
			logfile.println(url);
			boxIDs.clear();
			seasons.clear();

			try	{
				String outputFileName = MyFiles.scraper_data +url.replace("http://scores.covers.com/", "").replace("-scores-matchups.aspx", "")+timeStamp+".csv";
				fOutStream = new FileOutputStream(outputFileName);
				PrintStream fout = new PrintStream(fOutStream);
				if (url.contains("basketball") 	|| url.contains("football"))	fout.println ("date,a_name,h_name,sf_b,tf_b,af_a,hf_a,");			//spread final book, total final book, away final actual, home final actual
				if (url.contains("baseball")	|| url.contains("hockey"))		fout.println ("date,a_name,h_name,a_ML,h_ML,tf_b,af_a,hf_a,");	
				driver.get(url);
				wait.until(ExpectedConditions.elementToBeClickable(By.id("DropList")));

				seasons = MyWebFuncs.getSeasons(driver);

				logfile.println(seasons.toString());

				for (String season : seasons){
					logfile.println("season "+ season);				

					date = MyWebFuncs.getGameDateTime(driver);																	//go to start of season
					driver.findElement(By.xpath("//*[@id='DropList']/option[text()='"+ season +"']")).click();						
					date = MyWebFuncs.waitForNewPageDate(date, driver, logfile);
					driver.findElement(By.xpath("//*[@id='CalendarList']/option[1]")).click();	
					date = MyWebFuncs.waitForNewPageDate(date, driver, logfile);

					do {																										//per month
						date = MyWebFuncs.getGameDateTime(driver);
						gameDatesThisMonth.clear();
						for (WebElement e : MyWebFuncs.getXpathWebElements(driver, "//*[@id='cal']/table/tbody/tr[position()>1]/td/a"))
							gameDatesThisMonth.add(e.getText());
						logfile.println(gameDatesThisMonth);

						logfile.println(date + " boxIDs before calling scraper: " + boxIDs.toString());

						MyWebFuncs.scrapeCurrentDay(date, driver, boxIDs, fout, logfile, url);

						for (String gameDateThisMonth : gameDatesThisMonth){ 												//per day
							driver.findElement(By.xpath("//*[@id='cal']//b[text()='"+ gameDateThisMonth +"']/..")).click();							
							date = MyWebFuncs.waitForNewPageDate(date, driver, logfile);
							MyWebFuncs.scrapeCurrentDay(date, driver, boxIDs, fout, logfile, url);
						}
					} while (MyWebFuncs.try_to_go_to_the_next_month(date, driver, logfile));
				}
				fout.close();
				System.out.println(outputFileName);
			}
			catch (IOException e) {System.err.println ("Unable to write to file");System.exit(-1);}
		}
		logfile.close();
		//driver.close();
	}
}