package intraDayWorld;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import org.apache.commons.io.IOUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;


public class GetChartsLiveSymbols {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws XPatherException 
	 */
	public static void main(String[] args) throws IOException, XPatherException {

		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);
		//
		//
		//		WebDriver driver = new FirefoxDriver();
		//
		//		driver.get("http://www.chartslive.com/symbollist-tyo/");
		//		Select select = new Select(driver.findElement(By.xpath("//*/label/select")));
		//		select.selectByValue("100");
		//
		//
		//		String source = driver.getPageSource();
		//		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(source));
		//		TagNode node = cleaner.clean(isr);
		//		Object[] table_array = node.evaluateXPath("//*/tbody/tr/td[3]");
		//
		//		for (int i = 0; i < table_array.length; i++){
		//			System.out.println(    ((TagNode) table_array[i]).getText().toString().trim() );
		//		}

		//		"//*/tbody/tr/td[3]"


		/////////////////////////////////////////

		String[][] urls = new String[3][2];

		urls[0][0] = "http://www.chartslive.com/symbollist-tyo/";
		urls[0][1] = "TYO";
		urls[1][0] = "http://www.chartslive.com/symbollist-nse/";
		urls[1][1] = "NSE";
		urls[2][0] = "http://www.chartslive.com/symbollist-fraxetra/";
		urls[2][1] = "FRA";

		WebDriver driver = new FirefoxDriver();



		boolean endOfTickers;

		PrintStream ps = new PrintStream(new FileOutputStream(new File(
				"C:\\Users\\User\\Documents\\stocks\\world_intraday_project\\" +
				"exchanges and symbols nov 27 2013\\chartsLiveData_TYO_NSE_FRA.txt")));
		
		for (int i = 0; i < 3; i++) {

			driver.get(urls[i][0]);
			Select select = new Select(driver.findElement(By.xpath("//*/label/select")));
			select.selectByValue("100");

			do {
				endOfTickers = false;


				String source = driver.getPageSource();
				InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(source));
				TagNode node = cleaner.clean(isr);
				Object[] table_array = node.evaluateXPath("//*/tbody/tr/td[3]");

				for (int j = 0; j < table_array.length; j++){
					String outString = ((TagNode) table_array[j]).getText().toString().trim()  +"," + urls[i][1];
					System.out.println(  outString  );
					ps.println(outString);
				}

				try {
					driver.findElement(By.className("paginate_enabled_next")).click();
				}catch (Exception e){
					endOfTickers =true;
				}

			} while ( !endOfTickers);

		}
		ps.close();
//		driver.close();

	}

}
