package de.shoppinglist.android.bean;



public abstract class ListProductMapping extends BusinessBean implements IListMapping
{
	private Store store;

	private Product product;

	private String quantity;

	private short checked;
	
	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public short isChecked() {
		return checked;
	}

	public void setChecked(short checked) {
		this.checked = checked;
	}

	public abstract BusinessBean getSpecificBean();
}
