package byStats;
import java.util.ArrayList;
import java.util.List;


public class ResultsPack implements Comparable<ResultsPack>{
	List<ResultsRow> rows;
	double pft;

	public ResultsPack() {
		rows = new ArrayList<ResultsRow>();
	}

	List<ResultsRow> getRowList(){
		return rows;
	}
	/** should sort oppositely to default.  that is: decreasing */
	@Override
	public int compareTo(ResultsPack other) {

		return (pft > other.pft)?-1:(pft < other.pft)?1:0;
	}
	/** must call this before trying to sort */
	public void setMyComparator() {
		for (ResultsRow d : rows){
			if (d.ssn == 0)
				pft = d.pft;
		}
	}
	public boolean allSeasonsAreProfitable() {
		for (ResultsRow d : rows)
			if (d.pft < 0)
				return false;
		return true;
	}
	public boolean hasDataForEachSeason() {
		for (ResultsRow row : rows){
			if (row.n < 2 )
				return false;
		}
		return true;
	}

	public boolean isProfitableEachSeason() {
		for (ResultsRow row : rows){
			if (row.pft <= 0 )
				return false;
		}
		return true;
	}
	public void add(ResultsRow r) {
		rows.add(r);		
	}
	public int get_n() throws Exception {
		for (ResultsRow row : rows)
			if (row.ssn == 0)
				return row.n;
		throw new Exception("row of all seasons not found in get_n!");
	}
	public double get_pct() throws Exception {
		for (ResultsRow row : rows)
			if (row.ssn == 0) 
				return row.pct;
		throw new Exception("row of all seasons not found in get_pct!");
	}

	public boolean hasAllButOneProfitableSeason() {

		int numNegProfitSeasons = 0;

		for (ResultsRow row : rows){
			if (row.ssn == 0) 
				continue;
			if (row.pft <= 0) 
				numNegProfitSeasons++;
		}
		return (numNegProfitSeasons==1);
	}

	public boolean hasAllProfitableSeasons() {
		for (ResultsRow row : rows){
			if (row.pft <= 0) 
				return false;
		}
		return true;
	}


}
