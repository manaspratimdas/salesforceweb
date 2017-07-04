package salesforce.entity;

public class ColumnValuePair {

	
	private String columName;
	private String columnValue;
	
	public String getColumName() {
		return columName;
	}
	public void setColumName(String columName) {
		this.columName = columName;
	}
	public String getColumnValue() {
		return columnValue;
	}
	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}
	
	@Override
	public String toString() {
		return "ColumnValuePair [columName=" + columName + ", columnValue=" + columnValue + "]";
	}
	
	
	
}
