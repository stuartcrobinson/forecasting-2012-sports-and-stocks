package byCrowd;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TreeMap;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;



@SuppressWarnings("unused")
public class FindMismatchDatesUsers{

	public static void main(String args[]) throws ParseException, IOException, InterruptedException{


		//for each user, check if the number of bet dates in bet_dates is the same as the number of bet dates in user_bets

		//open betdates, make a treemap of all users with <key:userID, value:numBetDates>

		// go through all the files in the betsDir, make treemap2

		///compare

		Map<String, Integer> betDates_numBets = new HashMap<String, Integer>();
		Map<String, Integer> userBets_numBets = new HashMap<String, Integer>();




		List<String> lines = FileUtils.readLines(new File(
				"C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\all_NHL_2012users_bet_dates_1373434625759.csv"));
		int i = lines.size();
		for (String line : lines){
			List<String> userDatesFileRow = Arrays.asList(line.split("\\s*,\\s*"));
			int numBetDates = userDatesFileRow.size() - 2;
			betDates_numBets.put(userDatesFileRow.get(1), numBetDates);
			System.out.println("1. "+ i-- +" lines left");
		}



		String betsDir = "C:\\Users\\User\\Documents\\forecasting\\crowdsourced\\temp_data\\user_bets_NHL_2012_wrngLineSignForBetTm2";
		File[] files = new File(betsDir).listFiles();
		i = files.length;
		for (File file : files) {

			lines = FileUtils.readLines(file);

			Set<String> dates = new HashSet<String>();
			for (String line : lines) {
				List<String> lineList = Arrays.asList(line.split("\\s*,\\s*"));

				if (lineList.get(0).length() > 4)
					dates.add(lineList.get(0));
			}
			int numBets = dates.size();
			userBets_numBets.put(file.getName().substring(0, file.getName().indexOf("_")) , numBets);
			System.out.println("2. "+ i-- +" files left");

		}


		System.out.println(userBets_numBets.size());
		System.out.println(betDates_numBets.size());

		int mismatch = 0;
		for (String id : betDates_numBets.keySet()){
			if (betDates_numBets.containsKey(id) && userBets_numBets.containsKey(id)){

				if (  !betDates_numBets.get(id).equals( userBets_numBets.get(id)) ){
					mismatch++;
					System.out.format("%10s, %5d, %5d %n", id, betDates_numBets.get(id), userBets_numBets.get(id) );
				}


			}
		}
		System.out.println("total mismatch number: "+ mismatch);

		















	}
}