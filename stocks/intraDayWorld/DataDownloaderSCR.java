package intraDayWorld;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

//proxy option
//anonymouse.  estimated total time:  5.5 to 8 hours
//

//RUN THIS AGAIN AND GET ALL DATA!  o,l,h,c,v

//9,7,8,16

//should take about 2 hours
public class DataDownloaderSCR {
	public static void main(String[] args) throws IOException {
		//http://www.marketcalls.in/database/google-realtime-intraday-backfill-data.html
		//1.  open C:\Users\User\Documents\stocks\symbols.txt
		//2.  iterate through these stock symbols.  
		//		2b.  download each yahoo historical chart, save file with that name in C:\Users\User\Documents\stocks\data

		final long startTime = System.currentTimeMillis();

		Date dNow = new Date( );
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd hh.mm a");
		String dateStr = ft.format(dNow);


		String dirStr = "C:\\Users\\User\\Documents\\stocks\\data\\minutely\\"+ dateStr;
		String dataDirStr = dirStr +"\\rawSourceData";
		String tickersListOutStr = dirStr +"\\symbols.npp";

		File dir = new File(dirStr);
		File dataDir = new File(dataDirStr);

		dir.mkdir();
		dataDir.mkdir();

		List<String> tickersList = Files.readAllLines(
				Paths.get("C:\\Users\\User\\Documents\\stocks\\archive\\Data Downloader\\Google Intraday.txt"),
				StandardCharsets.UTF_8);
		Files.write(Paths.get(tickersListOutStr), tickersList, StandardCharsets.UTF_8);

		int j = 0;
		int i = tickersList.size()+1;
		for (String tickerInfoStr : tickersList){
			i--;
			j++;
			if (j % 100 == 0) 
				System.out.println("loop "+ j + ".  "
						+ (System.currentTimeMillis() - startTime)/1000.0 
						+ " seconds" );
			String [] tickerInfo = tickerInfoStr.split(",");
			String symbol = tickerInfo[0];
			String exchange = tickerInfo[1];

			String googleFinance_URL = 	"http://www.google.com/finance/getprices?" +
					"q=" + symbol + "&x=" + exchange + "&i=60&p=15d&f=d,o,l,h,c,v";


			//TODO what happens when it hits a read time out?  does it try it again?  or just lose the data....

			boolean failed = false;

			do {
				try {
					URL url = new URL(googleFinance_URL);			//"http://anonymouse.org/cgi-bin/anon-www.cgi/" +
					URLConnection con = url.openConnection();
					//			con.setConnectTimeout(20_000);
					//			con.setReadTimeout(20_000);

					InputStream is = con.getInputStream();
					List<String> source = IOUtils.readLines(is);
					IOUtils.closeQuietly(is);

					System.out.format("%6d %12s %5s%n", i, symbol, exchange);//(symbol +" "+ exchange + " "+ source.size());

					String outputFileNameStr = dataDir +"\\"+ symbol +"_"+ exchange +".ggl";

					Files.write(Paths.get(outputFileNameStr), source,  StandardCharsets.UTF_8);
					failed = false;
				}catch(Exception e){
					System.err.println(e);
					failed = true;
				}
			}while (failed);
		}
		System.out.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );

	}
}

//go ahead and format the data now. //NO
//NO
//NO -- format data later.  just save raw google files.  they are so efficient.
//
//
//Pattern linePattern = Pattern.compile(".*=([,\\w]*).*");
//Matcher lineMatcher;
//
//
//			int len = source.size();
//			String row0 = source.get(0);
//			String row1 = source.get(1);
//			String row2 = source.get(2);
//			String row3 = source.get(3);
//			String row4 = source.get(4);
//			String row5 = source.get(5);
//			String row6 = source.get(6);
//
//
//			lineMatcher = linePattern.matcher(row1);									
//			lineMatcher.find();							
//			String MARKET_OPEN = lineMatcher.group(1);
//
//			lineMatcher = linePattern.matcher(row2);									
//			lineMatcher.find();							
//			String MARKET_CLOSE = lineMatcher.group(1);
//
//			lineMatcher = linePattern.matcher(row3);									
//			lineMatcher.find();							
//			String INTERVAL = lineMatcher.group(1);
//
//			lineMatcher = linePattern.matcher(row6);									
//			lineMatcher.find();							
//			String TIMEZONE_OFFSET = lineMatcher.group(1);
//
//			String priceSetOutputFileName 
//			= 		symbol +"_"+ 
//					exchange +"_"+ 
//					MARKET_OPEN +"_"+ 
//					MARKET_CLOSE +"_"+ 
//					INTERVAL +"_"+ 
//					TIMEZONE_OFFSET +".csv";
//
//			for (int i = 7; i < len; i++){
//				if (source.get(i).startsWith("a")){
//					//do date stuff
//				}
//
//			}






//		
//		String fileURL_body = "http://ichart.finance.yahoo.com/table.csv?s=";
//		String saveDir = "C:\\Users\\User\\Documents\\stocks\\data2";
//		String symbolsFileName = "C:\\Users\\User\\Documents\\stocks\\symbols.txt";
//
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(symbolsFileName));
//			String symbol;
//			while ((symbol = reader.readLine()) != null)   {
//
//				String fileURL = fileURL_body + symbol;
//				String fileName = symbol + ".csv";
//
//				HttpDownloadUtility.downloadFile(fileURL, saveDir, fileName);
//			}
//			reader.close();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
