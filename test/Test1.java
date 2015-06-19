import java.text.SimpleDateFormat;
import java.util.Date;
public class Test1 {
	public static void main(String[] args) {

		final SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm:ss"); // format for display
        long time = 1387833440000L;
        System.out.println("2. "+ sdf.format(new Date(time)));
		
	}

}
