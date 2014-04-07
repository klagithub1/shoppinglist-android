package de.shoppinglist.android.bean;

public class Product extends BusinessBean {

	private String name;

	private Object unit;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getUnit() {
		return unit;
	}

	public void setUnit(Object unit) {
		this.unit = unit;
	}
}
