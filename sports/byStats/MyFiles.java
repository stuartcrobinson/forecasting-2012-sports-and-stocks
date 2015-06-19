package byStats;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;


public class MyFiles {

	public static String data_path 			= "C:\\Users\\User\\Documents\\forecasting\\data\\all\\";
	public static String scraper_log 			= data_path + "scraper_log_";
	public static String scraper_data 			= data_path + "raw_";
	public static String premod_log 			= data_path + "modified_log_";
	public static String modified 				= data_path + "modified_";
	public static String r_log 				= data_path + "r_log_";
	public static String r_data 				= data_path + "r_data_";

	public static String results_log	 		= data_path + "results_log_";
	
	public String[] results_full		= new String[3];
	public String[] results_culled		= new String[3];

	String[] 		digestFile = new String[3];
	String[] 		resultsFile = new String[3];
	PrintStream[] 	digestStream = new PrintStream[3];
	PrintStream[] 	resultsStream = new PrintStream[3];			

	String timeStamp;


	public String rInputFileName;

	
	public MyFiles(String rInputFileName_TimeStamp) {
		rInputFileName =  modified + rInputFileName_TimeStamp + ".csv";
		timeStamp = Long.toString(System.currentTimeMillis());


		try {
			results_full[H.i.SU] 		= data_path + "SU_results_full_";
			results_full[H.i.S] 		= data_path + "ATS_results_full_";
			results_full[H.i.T] 		= data_path + "OU_results_full_";
			results_culled[H.i.SU] 		= data_path + "SU_results_culled_";
			results_culled[H.i.S] 		= data_path + "ATS_results_culled_";
			results_culled[H.i.T]		= data_path + "OU_results_culled_";


			for (int betType : Arrays.asList(H.i.S, H.i.T, H.i.SU))
				digestFile[betType] = results_culled[betType] + timeStamp + ".csv";
			for (int betType : Arrays.asList(H.i.S, H.i.T, H.i.SU))
				resultsFile[betType] = results_full[betType] + timeStamp + ".csv";

			for (int betType : Arrays.asList(H.i.S, H.i.T, H.i.SU))
				digestStream[betType] = new PrintStream(new FileOutputStream (digestFile[betType]));
			for (int betType : Arrays.asList(H.i.S, H.i.T, H.i.SU))
				resultsStream[betType] = new PrintStream(new FileOutputStream (resultsFile[betType]));

		}catch (FileNotFoundException fnfe) {fnfe.printStackTrace();}
	}

	public String rOutputName(int rIts) {
		return r_data + timeStamp +"_"+ rIts +".csv";
	}

	public String rExecCmdLine(boolean ignoreOtherSeasons, int numTeamPriorGames, String decayType, String rOutputFileName, String timeStamp2) {
		return "Rscript C:\\Users\\User\\workspace\\test1\\regressor.r "+ ignoreOtherSeasons +" "+ numTeamPriorGames +" "+ decayType +" "+ rInputFileName +" "+ rOutputFileName +" "+ timeStamp2;
	}


}

