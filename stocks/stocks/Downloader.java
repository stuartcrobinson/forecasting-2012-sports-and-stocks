package stocks;

import java.io.BufferedReader;
import java.io.FileReader;

public class Downloader {
	public static void main(String[] args) {

		//1.  open C:\Users\User\Documents\stocks\symbols.txt
		//2.  iterate through these stock symbols.  
		//		2b.  download each yahoo historical chart, save file with that name in C:\Users\User\Documents\stocks\data

		String fileURL_body = "http://ichart.finance.yahoo.com/table.csv?s=";
		String saveDir = "C:\\Users\\User\\Documents\\stocks\\data2";
		String symbolsFileName = "C:\\Users\\User\\Documents\\stocks\\symbols.txt";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(symbolsFileName));
			String symbol;
			while ((symbol = reader.readLine()) != null)   {

				String fileURL = fileURL_body + symbol;
				String fileName = symbol + ".csv";

				HttpDownloadUtility.downloadFile(fileURL, saveDir, fileName);
			}
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
