package byStats;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

public class ResultsRow {

	NumberFormat format = new DecimalFormat("###.##");


	//	final static List<Boolean> 	useCIs_						= Arrays.asList(true, false);
	//	final static List<Boolean> 	useTmPs_					= Arrays.asList(true, false);					
	//	final static List<Boolean> 	useTmDAs_					= Arrays.asList(true, false);

	String headerString = 
			(		"pft," 			+
					"pct,"	 		+
					"n," 			+
					"IOS," 			+
					"nTPG," 		+
					"decay,"		+
					"ssn,"			+
					"mth,"			+
					"nRes,"		 	+
					"nDec," 		+
					"useCI,"		+
					"useTmDA,"		+
					"daMin," 		+
					"daMax," 		+
					"useTmP,"		+
					"pMin," 		+
					"pMax," 		+
					"dbMin," 		+
					"dbMax," 		);

	Double		pft;
	Double 		pct;
	Integer		n;
	Boolean 	ios;
	Integer		nTPG;
	String		decay;
	Integer		ssn;
	Integer		mth;
	Integer		nRes;
	Integer		nDec;
	Boolean 	useCI;
	Boolean 	useTmDA;
	Double		daMin;
	Double		daMax;
	Boolean 	useTmP;
	Double		pMin;
	Double		pMax;
	Double 		dbMin;
	Double		dbMax;

	public ResultsRow(){
		pft		= null;
		pct		= null;
		n		= null;
		ios		= null;
		nTPG	= null;
		decay	= null;
		ssn		= null;
		mth		= null;
		nRes	= null;
		nDec	= null;
		useCI	= null;
		useTmDA	= null;
		daMin	= null;
		daMax	= null;
		useTmP	= null;
		pMin	= null;
		pMax	= null;
		dbMin	= null;
		dbMax	= null;
	}

	public void reset(){
		pft		= null;
		pct		= null;
		n		= null;
		ios		= null;
		nTPG	= null;
		decay	= null;
		ssn		= null;
		mth		= null;
		nRes	= null;
		nDec	= null;
		useCI	= null;
		useTmDA	= null;
		daMin	= null;
		daMax	= null;
		useTmP	= null;
		pMin	= null;
		pMax	= null;
		dbMin	= null;
		dbMax	= null;
	}

	public String toOutputString() {
		return 	
				format.format(pft)      +","+  
				format.format(pct) 	    +","+
				n    	              	+","+ 
				ios   	 				+","+ 
				nTPG  	                +","+ 
				decay.substring(0, 3) 	+","+  
				ssn						+","+ 
				mth						+","+
				nRes  	                +","+ 
				nDec  	                +","+ 
				useCI					+","+
				useTmDA					+","+
				daMin	                +","+ 
				daMax					+","+
				useTmP					+","+
				pMin 	                +","+ 
				pMax 	                +","+
				dbMin	                +","+ 
				dbMax;	 
	}
}

