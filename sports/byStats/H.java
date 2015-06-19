package byStats;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/** helper class */
public class H {
	public final static DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public final static int asdf = 9;

	public static class Cols {


		static String date 		= "date";
		static String ssn 		= "ssn";
		static String a_name 	= "a_name";
		static String h_name 	= "h_name";
		static String a_ML 		= "a_ML";
		static String h_ML 		= "h_ML";		
		static String af_a 		= "af_a";
		static String hf_a 		= "hf_a";

		static String x_ML 		= "x_ML";
		static String y_ML 		= "y_ML";		
		static String x_name 	= "x_name";
		static String y_name 	= "y_name";

		static String dup		= "dup";	

		static String xf_a 		= "xf_a";
		static String yf_a 		= "yf_a";
		
		static String xf_e2		= "xf_e2";
		static String xf_e 		= "xf_e";
		static String xf_e_lwr 	= "xf_e_lwr";
		static String xf_e_upr 	= "xf_e_upr";

		static String yf_e 		= "yf_e";
		static String yf_e2 	= "yf_e2";
		static String yf_e_lwr 	= "yf_e_lwr";
		static String yf_e_upr 	= "yf_e_upr";
		
		
		static String sf_b 		= "sf_b";
		static String tf_b 		= "tf_b";	
		
		static String sf_e2		= "sf_e2";	
		static String sf_e		= "sf_e";	
		static String sf_e_lwr	= "sf_e_lwr";	
		static String sf_e_upr	= "sf_e_upr";	
		static String tf_e2		= "tf_e2";
		static String tf_e		= "tf_e";
		static String tf_e_lwr	= "tf_e_lwr";	
		static String tf_e_upr	= "tf_e_upr";	

		
		static String p_sf_x	= "p_sf_x";	
		static String p_sf_y	= "p_sf_y";
		static String p_tf_x	= "p_tf_x";
		static String p_tf_y	= "p_tf_y";	
		
		static String p_xf	= "p_xf";	
		static String p_yf	= "p_yf";	
		

	}
	/** data matrix indices */
	public static class i {	final static int X = 0, Y = 1, S = 0, T = 1, ATS = 0, OU = 1, SU = 2, LWR = 0, UPR = 1; }
	


}
