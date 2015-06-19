package stocks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleIntradayDataSerializer {

	public static void main(String[] args) {

		//open output file C:\Users\User\Documents\stocks\archive\Data Downloader\data\google1_compiled

		ArrayList<String> symbols = new ArrayList<String>();
		ArrayList<String> priceSets = new ArrayList<String>();

		try{

			File folder = new File("C:\\Users\\User\\Documents\\stocks\\archive\\Data Downloader\\data\\google1_nov17");
			File[] listOfFiles = folder.listFiles();

			for (File f : listOfFiles){

				String symbol = f.getName().replace(".txt", "");
				System.out.println(symbol);

				symbols.add(symbol);

				StringBuilder stringBuilder = new StringBuilder();
				String ls = System.getProperty("line.separator");

				BufferedReader br = new BufferedReader(new FileReader(f));
				String lineStr = null;

				while ((lineStr = br.readLine()) != null) {
					List<String> line = Arrays.asList(lineStr.split(",", -1));
					//String fileSymbol = line.get(0);
					String date = line.get(1);
					String time = line.get(2);
										String open = line.get(3);
					//					String high = line.get(4);
					//					String low = line.get(5);
//					String close = line.get(6);
					//					String vol = line.get(7);

					//if (!symbol.equals(fileSymbol))
					//System.out.println("there is a problem these should match: " + symbol +" "+ fileSymbol);

					stringBuilder.append( date.substring(4, 8) +","+ time.substring(0, 5).replace(":", "") +","+ open);
					stringBuilder.append( ls );

				}
				br.close();
				//System.out.println(stringBuilder.toString());

				priceSets.add(stringBuilder.toString());
//				System.out.println(stringBuilder.toString().length());
//				System.out.println(priceSets.size());
			}



			//now serialize priceSets and symbols and save to disck
			FileOutputStream file1Out = new FileOutputStream(
					"C:\\Users\\User\\Documents\\stocks\\archive\\Data Downloader\\data\\google1_priceSetsOpen.ser");
			ObjectOutputStream out1 = new ObjectOutputStream(file1Out);
			out1.writeObject(priceSets);
			out1.close();
			file1Out.close();

			FileOutputStream file2Out = new FileOutputStream(
					"C:\\Users\\User\\Documents\\stocks\\archive\\Data Downloader\\data\\google1_symbols.ser");
			ObjectOutputStream out2 = new ObjectOutputStream(file2Out);
			out2.writeObject(symbols);
			out2.close();
			file2Out.close();

			

			System.out.println(priceSets.size());
			System.out.println(symbols.size());

			
		}catch (IOException e){
			System.err.println("Error: " + e.getMessage());
		}

	}

}

//GOOG,20131119,09:30:00,1031.72,1031.72,1031.72,1031.72,15826
//GOOG,20131119,09:31:00,1031.72,1033.83,1031.59,1031.76,5225

/*  data downloader flips the close and open prices.  wtf why, stupid.  
EXCHANGE%3DNSE
MARKET_OPEN_MINUTE=555
MARKET_CLOSE_MINUTE=930
INTERVAL=60
COLUMNS=DATE,CLOSE,HIGH,LOW,OPEN,VOLUME
DATA=
TIMEZONE_OFFSET=330
a1383109620,6240.5,6240.9,6239.65,6239.85,0
1,6237.95,6240.3,6237.7,6239.95,0*/
