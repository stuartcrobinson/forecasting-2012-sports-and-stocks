package intraDayWorld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EodDataAndChartsLiveCompiler {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {

		Pattern linePattern = Pattern.compile("([-\\w]*).*");
		Matcher lineMatcher;


		//1.  get and compile EOD data files.
		//keep only text per line up to a period OR a whitespace (tab, actually)


		File folder = new File(
				"C:\\Users\\User\\Documents\\stocks\\world_intraday_project" +
				"\\exchanges and symbols nov 27 2013\\eoddata_ticker list");

		File[] files = folder.listFiles();

		PrintStream ps = new PrintStream(new FileOutputStream(new File(
				"C:\\Users\\User\\Documents\\stocks\\world_intraday_project" +
				"\\exchanges and symbols nov 27 2013\\masterSymbolExchangeList.txt")));

		for (File file : files){
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line = br.readLine();	//skip header
			while ((line = br.readLine()) != null){

				lineMatcher = linePattern.matcher(line);									
				lineMatcher.find();							
				String symbol = lineMatcher.group(1);

				String result = symbol +","+ file.getName().replace(".txt", "");
				System.out.println(result);
				ps.println(result);

			}

		}

		BufferedReader br = new BufferedReader(new FileReader(new File(
				"C:\\Users\\User\\Documents\\stocks\\world_intraday_project\\" +
				"exchanges and symbols nov 27 2013\\chartsLiveData_TYO_NSE_FRA.txt")));

		String line;
		while ((line = br.readLine()) != null){	
			System.out.println(line);
			ps.println(line);
		}

	}

}
