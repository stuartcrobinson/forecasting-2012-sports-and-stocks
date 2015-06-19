import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//		Pattern linePattern = Pattern.compile(".*=([-_\\w]*).*");
		//		Matcher lineMatcher;
		//
		//		
		//		String line = "asdfasdfasdf=-300";
		//		lineMatcher = linePattern.matcher(line);									
		//		lineMatcher.find();						
		//		System.out.println(lineMatcher.group(1));

//
//		System.out.println(Arrays.asList("ze_ro_one.two_three'four_five".split(".")));
//		System.out.println("ze_ro_one.two_three'four_five".split(".").length);


//        double d = 1.2;
//        DecimalFormat df = new DecimalFormat("####.####");
//        System.out.print(df.format(d));
		
		
		final SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm:ss"); // format for display
        String timeStr = "1387833440000";
        long timeLong = Long.parseLong(timeStr);
        System.out.println(timeLong);
        System.out.println(sdf.format(new Date(timeLong)));
		
	}

}
