package forex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ForexiteSerializer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args)   {
		final long startTime = System.currentTimeMillis();

		boolean needToRename = false;
		if (needToRename){
			//read all files in folder and see if read in order
			//C:\Users\User\Documents\stocks\forex\forexite\2013_to_nov
			File folder = new File("C:\\Users\\User\\Documents\\stocks\\forex\\forexite\\2013_to_nov");
			File[] listOfFiles = folder.listFiles();

			for (File f : listOfFiles){
				System.out.println(f.getName());

				String day = f.getName().substring(0,2);
				String mon = f.getName().substring(2,4);
				String yr  = f.getName().substring(4,6);

				File newName = new File("C:\\Users\\User\\Documents\\stocks\\forex\\forexite\\2013_to_nov\\" + yr + mon + day + ".txt");

				f.renameTo(newName);
				System.out.println(f.getName());
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////

		File folder = new File("C:\\Users\\User\\Documents\\stocks\\forex\\forexite\\2013_to_nov");
		File[] listOfFiles = folder.listFiles();

		StringBuilder bAUDJPY = new StringBuilder();
		StringBuilder bAUDUSD = new StringBuilder();
		StringBuilder bCHFJPY = new StringBuilder();
		StringBuilder bEURCAD = new StringBuilder();
		StringBuilder bEURCHF = new StringBuilder();
		StringBuilder bEURGBP = new StringBuilder();
		StringBuilder bEURJPY = new StringBuilder();
		StringBuilder bEURUSD = new StringBuilder();
		StringBuilder bGBPCHF = new StringBuilder();
		StringBuilder bGBPJPY = new StringBuilder();
		StringBuilder bGBPUSD = new StringBuilder();
		StringBuilder bNZDJPY = new StringBuilder();
		StringBuilder bNZDUSD = new StringBuilder();
		StringBuilder bUSDCAD = new StringBuilder();
		StringBuilder bUSDCHF = new StringBuilder();
		StringBuilder bUSDJPY = new StringBuilder();
		StringBuilder bXAGUSD = new StringBuilder();
		StringBuilder bXAUUSD = new StringBuilder();

		String ls = System.getProperty("line.separator");

		int lines = 0;
		String prevOpen = "cat";
		for (File f : listOfFiles){
			System.out.println(f.getName());

			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(f));
				String lineStr;
				lineStr = br.readLine();	//skip header

				while ((lineStr = br.readLine()) != null) {
					List<String> line = Arrays.asList(lineStr.split(",", -1));

					String ticker 	= line.get(0);
					String day	 	= line.get(1);
					String time		= line.get(2);
					String open		= line.get(3);
					
					if (time.equals("000000"))
						time = "240000";

//					if (!open.equals(prevOpen)){	//skip if price doesn't change, to make smaller data size
//						lines++;

						if (ticker.equals("AUDJPY")){	bAUDJPY.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bAUDJPY.append(ls);	}
						if (ticker.equals("AUDUSD")){	bAUDUSD.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bAUDUSD.append(ls);	}
						if (ticker.equals("CHFJPY")){	bCHFJPY.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bCHFJPY.append(ls);	}
						if (ticker.equals("EURCAD")){	bEURCAD.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bEURCAD.append(ls);	}
						if (ticker.equals("EURCHF")){	bEURCHF.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bEURCHF.append(ls);	}
						if (ticker.equals("EURGBP")){	bEURGBP.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bEURGBP.append(ls);	}
						if (ticker.equals("EURJPY")){	bEURJPY.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bEURJPY.append(ls);	}
						if (ticker.equals("EURUSD")){	bEURUSD.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bEURUSD.append(ls);	}
						if (ticker.equals("GBPCHF")){	bGBPCHF.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bGBPCHF.append(ls);	}
						if (ticker.equals("GBPJPY")){	bGBPJPY.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bGBPJPY.append(ls);	}
						if (ticker.equals("GBPUSD")){	bGBPUSD.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bGBPUSD.append(ls);	}
						if (ticker.equals("NZDJPY")){	bNZDJPY.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bNZDJPY.append(ls);	}
						if (ticker.equals("NZDUSD")){	bNZDUSD.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bNZDUSD.append(ls);	}
						if (ticker.equals("USDCAD")){	bUSDCAD.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bUSDCAD.append(ls);	}
						if (ticker.equals("USDCHF")){	bUSDCHF.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bUSDCHF.append(ls);	}
						if (ticker.equals("USDJPY")){	bUSDJPY.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bUSDJPY.append(ls);	}
						if (ticker.equals("XAGUSD")){	bXAGUSD.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bXAGUSD.append(ls);	}
						if (ticker.equals("XAUUSD")){	bXAUUSD.append(day.substring(4) +","+ time.substring(0,4) +","+ open); bXAUUSD.append(ls);	}
//					}
//					prevOpen = open;
				}
				br.close();
			} catch (IOException e) {	e.printStackTrace();}
		}
		System.out.println(lines);

		ArrayList<String> strings = new ArrayList<String>();

		strings.add(bAUDJPY.toString());
		strings.add(bAUDUSD.toString());
		strings.add(bCHFJPY.toString());
		strings.add(bEURCAD.toString());
		strings.add(bEURCHF.toString());
		strings.add(bEURGBP.toString());
		strings.add(bEURJPY.toString());
		strings.add(bEURUSD.toString());
		strings.add(bGBPCHF.toString());
		strings.add(bGBPJPY.toString());
		strings.add(bGBPUSD.toString());
		strings.add(bNZDJPY.toString());
		strings.add(bNZDUSD.toString());
		strings.add(bUSDCAD.toString());
		strings.add(bUSDCHF.toString());
		strings.add(bUSDJPY.toString());
		strings.add(bXAGUSD.toString());
		strings.add(bXAUUSD.toString());

		FileOutputStream file1Out;
		try {
			file1Out = new FileOutputStream(
					"C:\\Users\\User\\Documents\\stocks\\forex\\forexite\\2013toNov_string.ser");
			ObjectOutputStream out1 = new ObjectOutputStream(file1Out);
			out1.writeObject(strings);
			out1.close();
			file1Out.close();
		} catch (IOException e) {e.printStackTrace();}


		final long endTime = System.currentTimeMillis();
		System.out.println((endTime - startTime)/1000.0 +" seconds" );
	}

}



/*


	//really slow
		for (File f : listOfFiles){
			System.out.println(f.getName());

			BufferedReader br = new BufferedReader(new FileReader(f));
			String lineStr = br.readLine();	//skip header

			while ((lineStr = br.readLine()) != null) {
				List<String> line = Arrays.asList(lineStr.split(",", -1));

				String ticker 	= line.get(0);
				String day	 	= line.get(1);
				String time		= line.get(2);
				String open		= line.get(3);

				data.put(ticker, data.get(ticker) + "\n"+ day +","+ time +","+ open);

			}
			br.close();

		}

AUDJPY
AUDUSD
CHFJPY
EURCAD
EURCHF
EURGBP
EURJPY
EURUSD
GBPCHF
GBPJPY
GBPUSD
NZDJPY
NZDUSD
USDCAD
USDCHF
USDJPY
XAGUSD
XAUUSD*/

//8:07
//Map<String, String> treeMap = new TreeMap<String, String>(map);