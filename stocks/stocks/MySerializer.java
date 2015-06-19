package stocks;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MySerializer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			String symbolsFileName = "C:\\Users\\User\\Documents\\stocks\\symbols.txt";

			ArrayList<String> symbols = new ArrayList<String>();
			ArrayList<String> symbolsRaw = new ArrayList<String>();
			ArrayList<String> priceSets = new ArrayList<String>();
			ArrayList<String> dates = new ArrayList<String>();

			
			BufferedReader reader;
			reader = new BufferedReader(new FileReader(symbolsFileName));
			String symbol1;
			while ((symbol1 = reader.readLine()) != null)   {
				symbolsRaw.add(symbol1);
				System.out.println(symbol1);
			}
			System.out.println("\n");
			System.out.println(symbolsRaw);
			reader.close();


			for (String symbol : symbolsRaw){
				String fileName = "C:\\Users\\User\\Documents\\stocks\\data\\" + symbol + ".csv";

				System.out.println(symbol);

				try {
					reader = new BufferedReader( new FileReader (fileName));
					symbols.add(symbol);
					String line = null;
					StringBuilder stringBuilder = new StringBuilder();
					String ls = System.getProperty("line.separator");

					int days = 0;
					while( ( line = reader.readLine() ) != null && days < 2555) {	//2555 days is 7 years
						List<String> lineList = Arrays.asList(line.split(",", -1));

						
						String open = 	lineList.get(1);
						String low = 	lineList.get(3);
						String close = 	lineList.get(4);
						String adjClose = lineList.get(6);

						stringBuilder.append( open +","+ low +","+ close +","+ adjClose);
						stringBuilder.append( ls );
						days++;
					}
					priceSets.add(stringBuilder.toString());
				}catch (Exception e){}
			}

			System.out.println(priceSets.get(3));
			System.out.println(priceSets.size());

			//now serialize priceSets and symbols and save to disck
			FileOutputStream file1Out = new FileOutputStream("C:\\Users\\User\\Documents\\stocks\\data\\priceSets.ser");
			ObjectOutputStream out1 = new ObjectOutputStream(file1Out);
			out1.writeObject(priceSets);
			out1.close();
			file1Out.close();

			FileOutputStream file2Out = new FileOutputStream("C:\\Users\\User\\Documents\\stocks\\data\\symbols.ser");
			ObjectOutputStream out2 = new ObjectOutputStream(file2Out);
			out2.writeObject(symbols);
			out2.close();
			file2Out.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

//
//
//
//package stocks;
//
//import java.io.BufferedReader;
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.InputStreamReader;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Scanner;
//
//public class MySerializer {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//
//		int stockDays = 365;
//
//		try {
//			String symbolsFileName = "C:\\Users\\User\\Documents\\stocks\\symbols.txt";
//
//			ArrayList<String> symbols = new ArrayList<String>();
//			ArrayList<String> symbolsRaw = new ArrayList<String>();
//
//			BufferedReader reader;
//			reader = new BufferedReader(new FileReader(symbolsFileName));
//			String symbol1;
//			while ((symbol1 = reader.readLine()) != null)   {
//				symbolsRaw.add(symbol1);
//				System.out.println(symbol1);
//			}
//			System.out.println("\n");
//			System.out.println(symbolsRaw);
//			reader.close();
//
//			ArrayList<double[][]> priceSets = new ArrayList<double[][]>();
//
//			for (String symbol : symbolsRaw){
//				String fileName = "C:\\Users\\User\\Documents\\stocks\\data\\" + symbol + ".csv";
//
//				System.out.println(symbol);
//
//				try {
//					reader = new BufferedReader( new FileReader (fileName));
//					symbols.add(symbol);
//					//					StringBuilder stringBuilder = new StringBuilder();
//					//					String ls = System.getProperty("line.separator");
//
//					double[][] priceSet = new double[stockDays][3];
//
//					int day = 0;
//					String line = reader.readLine();	//read the header
//					while( ( line = reader.readLine() ) != null && day < stockDays) {	//2555 days is 7 years
//
//						List<String> lineList = Arrays.asList(line.split(",", -1));
//
//						double open = 	Double.parseDouble(lineList.get(1));
//						double low = 	Double.parseDouble(lineList.get(3));
//						double close = 	Double.parseDouble(lineList.get(4));
//
//						priceSet[day][0] = open;
//						priceSet[day][1] = low;
//						priceSet[day][2] = close;
//
//						//						stringBuilder.append( open +","+ low +","+ close);
//						//						stringBuilder.append( ls );
//						day++;
//					}
//					//					priceSets.add(stringBuilder.toString());
//
//					priceSets.add(priceSet);
//				}catch (Exception e){System.out.println(e);}
//			}
//
//			System.out.println(symbols.size());
//			System.out.println(priceSets.size());
//			System.out.println(priceSets.get(3));
//
//			//now serialize priceSets and symbols and save to disck
//			FileOutputStream file1Out = new FileOutputStream("C:\\Users\\User\\Documents\\stocks\\data\\priceSets.ser");
//			ObjectOutputStream out1 = new ObjectOutputStream(file1Out);
//			out1.writeObject(priceSets);
//			out1.close();
//			file1Out.close();
//
//			FileOutputStream file2Out = new FileOutputStream("C:\\Users\\User\\Documents\\stocks\\data\\symbols.ser");
//			ObjectOutputStream out2 = new ObjectOutputStream(file2Out);
//			out2.writeObject(symbols);
//			out2.close();
//			file2Out.close();
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//}
