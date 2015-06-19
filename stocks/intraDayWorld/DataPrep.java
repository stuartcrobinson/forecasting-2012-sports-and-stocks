package intraDayWorld;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//THIS HAS A PROBLEM.  not all the file names got the timezone offset.
//probably screwed up on negative numbers, i didn't have a negative sign in the regex thing/
//i put one in but haven't tested it
//TODO

//add dates!  (fake day count)

public class DataPrep {

	public static void main(String[] args) throws IOException {

		//C:\Users\User\Documents\stocks\data\minutely\2013.11.29 05.26 PM complete\rawSourceData

		final long startTime = System.currentTimeMillis();

		Date dNow = new Date( );
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd hh.mm a");
		String dateStr = ft.format(dNow);



		Pattern linePattern = Pattern.compile(".*=([-,\\w]*).*");
		Matcher lineMatcher;

		File folder = new File("C:\\Users\\User\\Documents\\stocks\\data\\" +
				"minutely\\2013.11.29 05.26 PM complete\\rawSourceData");

		String outDirStr = "C:\\Users\\User\\Documents\\stocks\\data\\" +
				"minutely\\2013.11.29 05.26 PM complete\\preparedData " + dateStr +	"\\";
		File outDir = new File(outDirStr);

		outDir.mkdir();

		File [] files = folder.listFiles();

		int i = files.length + 1;
		for (File file : files){
			i--;
			System.out.println(i);

			if (file.length() > 195){	//has more than 4 lines, roughly

				String s = new String(file.getName().replace(".ggl",  ""));
				String [] symbolAndExchange = s.split("_");
				String symbol = symbolAndExchange[0];
				String exchange = symbolAndExchange[1];

				List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
				List<String> outLines = new ArrayList<String>();

				String MARKET_OPEN = null;						
				String MARKET_CLOSE = null;						
				String INTERVAL = null;						
				String TIMEZONE_OFFSET = null;

				int startingRow = 0;
				for (int j = 0; j < 10; j++){
					String line = lines.get(j);
					lineMatcher = linePattern.matcher(line);									
					lineMatcher.find();						
					if (line.contains("MARKET_OPEN"))
						MARKET_OPEN = lineMatcher.group(1);
					if (line.contains("MARKET_CLOSE"))
						MARKET_CLOSE = lineMatcher.group(1);
					if (line.contains("INTERVAL"))
						INTERVAL = lineMatcher.group(1);
					if (line.contains("TIMEZONE_OFFSET"))
						TIMEZONE_OFFSET = lineMatcher.group(1);
					if (line.startsWith("a")){
						startingRow = j;
						break;
					}
				}

				String priceSetOutputFileName 
				= 		symbol +"_"+ 
						exchange +"_"+ 
						MARKET_OPEN +"_"+ 
						MARKET_CLOSE +"_"+ 
						INTERVAL +"_"+ 
						TIMEZONE_OFFSET +".sr";

				int len = lines.size();

				//lines has 
				int day = -1;
				for (int j = startingRow; j < len; j++){
					String originalLine = lines.get(j);
					if (originalLine.startsWith("a")){
						String [] lineArr = originalLine.split(",");
						String price = lineArr[1];
						originalLine = "0," + price;
						//lines.set(j,  "0," + price);
						day++;
					}
					String outLine = new String(day +","+ originalLine);
					outLines.add(outLine);
				}

				Files.write(Paths.get(outDirStr + priceSetOutputFileName), outLines, StandardCharsets.UTF_8);

			}
		}

		System.out.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );


	}

}
