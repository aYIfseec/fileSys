package entity;

public class DownloadRetVo implements Comparable<Object>{
	String areaRealName;
	
	String count;
	
	Integer sortId;

	public Integer getSortId() {
		return sortId;
	}

	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}

	public String getAreaRealName() {
		return areaRealName;
	}

	public void setAreaRealName(String areaRealName) {
		this.areaRealName = areaRealName;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	@Override
	public int compareTo(Object o) {
		DownloadRetVo other = (DownloadRetVo)o;
		Integer thisCount = Integer.parseInt(this.getCount() );
		Integer otherCount = Integer.parseInt(other.getCount() );
		return (otherCount.compareTo(thisCount) );
	}
}
