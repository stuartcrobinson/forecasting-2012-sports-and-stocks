package trash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class GoogleWorldMinuteDataSplitter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		final long startTime = System.currentTimeMillis();

		BufferedReader br = new BufferedReader(new FileReader(new File(
				"C:\\Users\\User\\Documents\\stocks\\prepared data\\" +
				"nov 27 master sucks no exchange so lost symbols.csv")));

		File dir = new File(
				"C:\\Users\\User\\Documents\\stocks\\prepared data\\" +
				"nov 27 master sucks no exchange so lost symbols");
		dir.mkdir();

		String lineStr;
		String symbol = null;
		List<String> outLines = new ArrayList<String>();

		int i = 25968;
		symbol = br.readLine();
		while ((lineStr = br.readLine()) != null){
			if (lineStr.length() < 2){	//header on next row
				System.out.println(i--);

				//print stringbuilder so far to file titled w/ symbol name.
				Files.write(Paths.get(dir +"\\"+ symbol + ".txt"), outLines, StandardCharsets.UTF_8);
				symbol = br.readLine();
				outLines = new ArrayList<String>();
			}
			else{
				outLines.add(lineStr);
			}	
		}
		br.close();

		System.out.println((System.currentTimeMillis() - startTime)/1000.0 +" seconds" );
	}

}
