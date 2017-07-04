package salesforce.entity;

import java.util.List;

public class UpdateParam {

	private String name;
	List<ColumnValuePair> listColumnValuePair;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ColumnValuePair> getListColumnValuePair() {
		return listColumnValuePair;
	}

	public void setListColumnValuePair(List<ColumnValuePair> listColumnValuePair) {
		this.listColumnValuePair = listColumnValuePair;
	}

	@Override
	public String toString() {
		return "UpdateParam [name=" + name + ", listColumnValuePair=" + listColumnValuePair + "]";
	}
	
	
	

}
