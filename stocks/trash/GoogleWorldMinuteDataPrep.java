package trash;

//this is awesome.  takes 45 seconds to output formatted data for 26,000 symbols over 15 days. 
//(still need to replace Data Downloader so the exchange name is included in file name.  figure out date business.
//this is so beautiful. 

//lessons:
//1.  minimize file interactions  (use new java File methods for reading a whole file and writing a whole file at once.
//2.  use string builder as much as possible
//3.  use "new String(...)"  when modifying strings with substring, replace, etc.  to prevent unused string info from being lugged around. 


//////nowww........   how to crunch the output? just big string.  fuck that serialize bullshit.




//old fashioned minutely way.  check to see if open is x% lower than close.  or if it ever gets down to x% below open. 
//	then buy and sell by EOD.

//welll...... first have to read data.  big string.  
//actually................................   this data maker should make a separate file per symbol.  can't read all the symbols at once.  and why would i.
//think about scalability.   ack.  i tried that and it was super slow.  keep data in one huge file.  
//lets see how long it would take to open the file back up and split it in to component symbol files ....  attached below.  not very fast. skip it.  i think it would take like 250 seconds.
//idk maybe it would still save time later which is the biggest goal.  fuck it, yes.  i should split up the files now.  
//otherwise there will be a lot of extra checks later to split them up.

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class GoogleWorldMinuteDataPrep {

	public static void main(String[] args) throws IOException {
		final long startTime = System.currentTimeMillis();

		File folder = new File(
				"C:\\Users\\User\\Documents\\stocks\\archive\\Data Downloader\\" +
				"output\\google_DataDownloader_1minIntraday_output 20131127");

		File [] files = folder.listFiles();
		int numSymbols = files.length;
		String c = ",";
		String ls = System.getProperty("line.separator");

		PrintStream ps = new PrintStream(new FileOutputStream(new File(
				"C:\\Users\\User\\Documents\\stocks\\prepared data\\" +
				"nov 27 master sucks no exchange so lost symbols.csv")));

		int symbol_ID = -1;
		String symbol;
		for (File file : files){
			symbol_ID++;
			System.out.println(numSymbols - symbol_ID);
			symbol = new String(file.getName().replace(".txt",  ""));

			StringBuilder builder = new StringBuilder();
			builder.append(symbol);
			builder.append(ls);

			List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);						//this is awesome.

			for (String lineStr: lines){
				String [] line = lineStr.split(",");			//w/out -1 at the end it drops empty cells
				String date = new String(line[1].substring(2));						;
				String time = new String(line[2].replace(":", "").substring(0,4))  	;		//here is where runs out of memory???? see bottom
				String open = new String(line[3]);

				builder.append(date);
				builder.append(time);
				builder.append(c);
				builder.append(open);
				builder.append(ls);
			}
			
			ps.println(builder.toString());
		}
		ps.close();
		System.out.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );
	}
}


//package stockExchangeData;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Test {
//
//	/**
//	 * @param args
//	 * @throws IOException 
//	 */
//	public static void main(String[] args) throws IOException {
//		final long startTime = System.currentTimeMillis();
//
//		BufferedReader br = new BufferedReader(new FileReader(new File(
//				"C:\\Users\\User\\Documents\\stocks\\prepared data\\" +
//				"nov 27 master sucks no exchange so lost symbols.csv")));
//
//		File dir = new File(
//				"C:\\Users\\User\\Documents\\stocks\\prepared data\\" +
//				"nov 27 master sucks no exchange so lost symbols");
//		dir.mkdir();
//
//		String lineStr;
//		String symbol = null;
//		List<String> outLines = new ArrayList<String>();
//
//		int i = 25968;
//		symbol = br.readLine();
//		while ((lineStr = br.readLine()) != null){
//			if (lineStr.length() < 2){	//header on next row
//				System.out.println(i--);
//
//				//print stringbuilder so far to file titled w/ symbol name.
//				Files.write(Paths.get(dir +"\\"+ symbol + ".txt"), outLines, StandardCharsets.UTF_8);
//				symbol = br.readLine();
//				outLines = new ArrayList<String>();
//			}
//			else{
//				outLines.add(lineStr);
//			}	
//		}
//		br.close();
//
//		System.out.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );
//	}
//
//}

