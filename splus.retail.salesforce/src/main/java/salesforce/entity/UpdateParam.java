package salesforce.entity;

import java.util.List;

public class UpdateParam {
	
	String name;
	List<ColumnValuePair> columnValuePair;
	String selectId;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ColumnValuePair> getColumnValuePair() {
		return columnValuePair;
	}
	public void setColumnValuePair(List<ColumnValuePair> columnValuePair) {
		this.columnValuePair = columnValuePair;
	}
	
	
	
	public String getSelectId() {
		return selectId;
	}
	public void setSelectId(String selectId) {
		this.selectId = selectId;
	}
	
	@Override
	public String toString() {
		return "UpdateParam [name=" + name + ", columnValuePair=" + columnValuePair + ", selectId=" + selectId + "]";
	}
	
	
	

}
