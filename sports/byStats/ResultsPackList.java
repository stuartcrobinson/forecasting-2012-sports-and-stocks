package byStats;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
public class ResultsPackList {

	private ArrayList<ResultsPack> myPackList;

	public ResultsPackList() {
		myPackList = new ArrayList<ResultsPack>();
	}	
	/** adds the resultPack if it has no 0-n seasons. also sets the comparator, to prepare the pack to be sortable */
	public void addConditionally(ResultsPack rp) {

		if (rp.hasDataForEachSeason() && rp.isProfitableEachSeason()){
			rp.setMyComparator();
			myPackList.add(rp);
		}
	}	

	public ResultsPack remove(int i){
		return myPackList.remove(i);
	}	
	public int size(){
		return myPackList.size();
	}
	public ResultsPack get(int i){
		return myPackList.get(i);
	}
	/** remove unprofitable rows, duplicated result rows (by n & pct), and then sort */
	public void treat() {
		try {
			int n;
			double pct;

			for (int i = 0; i < myPackList.size(); i ++){												//remove other packs who's all-seasons row has the same n and pct.
				n = myPackList.get(i).get_n();
				pct = myPackList.get(i).get_pct();

				for (int j = myPackList.size() - 1; j > i ; j--){
					if (myPackList.get(j).get_n() == n && myPackList.get(j).get_pct() == pct){
						myPackList.remove(j);					
					}
				}
			}		
			Collections.sort(myPackList);
		}catch (Exception e){ System.out.println(e);}
	}

	public void printAll(PrintStream digestStream) {
		for (ResultsPack rp : myPackList){
			for (ResultsRow row : rp.getRowList())
				digestStream.println(row.toOutputString());
		}
	}

}
