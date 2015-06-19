package byStats;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
public class Data_Premodification {

	final static String rawInputFileNameEnd = "raw_basketball1349239303414";	//"raw_womens-basketball1349805606125";
	final static String rawInput = MyFiles.data_path + rawInputFileNameEnd +".csv";

	public static void main(String[] args) {
		try {
			String timeStamp = Long.toString(System.currentTimeMillis());
			String outputFileName = MyFiles.modified + timeStamp + ".csv";
			PrintStream fout = new PrintStream(new FileOutputStream (outputFileName));
			boolean a_ssn_is_contained_in_one_year, this_is_a_money_line_sport;

			if (rawInput.contains("womens-basketball") || rawInput.contains("baseball")) 
				a_ssn_is_contained_in_one_year = true;
			else
				a_ssn_is_contained_in_one_year = false;

			if (rawInput.contains("baseball") || rawInput.contains("hockey")) 
				this_is_a_money_line_sport = true;
			else
				this_is_a_money_line_sport = false;

			BufferedReader in = new BufferedReader(new FileReader(rawInput));
			String line = in.readLine();																						//first fix headers for soon-to-be-produced headers
			line = line.replace(H.Cols.af_a, H.Cols.xf_a);																		//converting "away" and "home" labels to "x" and "y" for row duplication (for maximal data usage by glm)
			line = line.replace(H.Cols.hf_a, H.Cols.yf_a);
			line = line.replace(H.Cols.a_name, H.Cols.x_name);
			line = line.replace(H.Cols.h_name, H.Cols.y_name);
			

			ArrayList<String> colTitles = new ArrayList<String>(Arrays.asList(line.split(",")));		//first fix headers for soon-to-be-produced headers
			colTitles.add(0,  "ssn");																				//insert ssn as first column.  doing this immediately

			for (String str : Arrays.asList(H.Cols.dup, H.Cols.xf_e, H.Cols.xf_e2, H.Cols.xf_e_lwr, H.Cols.xf_e_upr, 
														H.Cols.yf_e, H.Cols.yf_e2, H.Cols.yf_e_lwr, H.Cols.yf_e_upr, 
														H.Cols.sf_e, H.Cols.sf_e2, H.Cols.sf_e_lwr, H.Cols.sf_e_upr, 
														H.Cols.tf_e, H.Cols.tf_e2, H.Cols.tf_e_lwr, H.Cols.tf_e_upr,    
														H.Cols.p_xf, H.Cols.p_yf,
														H.Cols.p_sf_x, H.Cols.p_sf_y, 
														H.Cols.p_tf_x, H.Cols.p_tf_y ))				// TODO the p for x and y SHOULD be the same for both glm models, since they're using the same data.  in the future, i should just model it once, and then somehow apply the model to the other team. ... no i could do it now.  just model the x team.  fit data.  then in analyzer, flip the second x team's estimated value up to a new column on the line above it (for the two lines of the same game).  change "x_is_away" to "dup"(licate)
				colTitles.add(str);			

			int xi 			= colTitles.indexOf(H.Cols.x_name);
			int yi 			= colTitles.indexOf(H.Cols.y_name);
			int xfai 		= colTitles.indexOf(H.Cols.xf_a);
			int yfai 		= colTitles.indexOf(H.Cols.yf_a);	
//			int xfei 		= colTitles.indexOf(H.Cols.xf_e);
//			int yfei 		= colTitles.indexOf(H.Cols.yf_e);																//yf_e is not going to be calculated.  will be the xf_e of the duplicate row.
			int xMLi 		= colTitles.indexOf(H.Cols.x_ML);
			int yMLi 		= colTitles.indexOf(H.Cols.y_ML);
			int sfbi 		= colTitles.indexOf(H.Cols.sf_b);																//"tf_b" column not used. since same for orig and duplicated data.

			if (xMLi == -1) this_is_a_money_line_sport = false;
			else 			this_is_a_money_line_sport = true;

			System.out.println(Joiner.on(",").join(colTitles));
			fout.println(Joiner.on(",").join(colTitles));

			String temp, datestr;
			int ssn, monthInt, yearInt;	
			Pattern datePattern = Pattern.compile("(\\d+)\\-(\\d+)\\-(\\d+).*");
			Matcher dateMatcher;
			ArrayList<String> values;		

			while ((line = in.readLine()) != null) {																			//now handle data
				if (line.contains("error") || line.contains(" ") || line.contains(",-,") || line.contains("null"))	//TODO i just removed the check to ignore lines with missing data.  cuz i don't want to blow off the game just because a piece of odds data wasn't recorded.  but i need to make sure that the analyzer knows that some odds data might be missing now.
					continue;

				values = new ArrayList<String>(Arrays.asList(line.split(",")));									//parse csv line into an arraylist

				datestr = values.get(0);																				//distilling date to determine ssn
				dateMatcher = datePattern.matcher(datestr);									
				dateMatcher.find();																				
				yearInt = Integer.valueOf(dateMatcher.group(3));
				monthInt = Integer.valueOf(dateMatcher.group(1));
				if (a_ssn_is_contained_in_one_year) 
					ssn = yearInt;
				else 
					if (monthInt >= 8)
						ssn = yearInt + 1;
					else ssn = yearInt;																					//finished calculating ssn

				values.add(0,  String.valueOf(ssn));																				//add ssn immediately
				values.add("0");																						//"t" for true to "x_is_away" column -- so 0 is for false - not a duplicate

				System.out.println(Joiner.on(",").join(values));
				fout.println(Joiner.on(",").join(values));

				values = new ArrayList<String>(Arrays.asList(line.split(",")));										// handle data duplication-- overwrite originally inputted data
				values.add(0,  ssn + "");																				//add ssn immediately
				values.add("f");																						//"f" for false to "x_is_away" column -- 1 is for true, this is the duplicate row

				temp = values.get(xi);														//switches away and home names
				values.set(xi, values.get(yi) );
				values.set(yi, temp);

				temp = values.get(xfai);													//switches away and home scores
				values.set(xfai, values.get(yfai) );
				values.set(yfai, temp);

				if (this_is_a_money_line_sport) {																		//switch away and home moneylines
					temp = values.get(xMLi);
					values.set(xMLi, values.get(yMLi));
					values.set(yMLi, temp);
				}
				else values.set(sfbi, String.valueOf(-1 * Double.valueOf(values.get(sfbi))));		//flip sign of ATS_odds if not a ML game

				System.out.println(Joiner.on(",").join(values));
				fout.println(Joiner.on(",").join(values));

			}
			in.close();
			fout.close();
			System.out.println(outputFileName);


		} catch (Exception e3) {System.out.println(e3);  e3.printStackTrace(); }
	}
}
