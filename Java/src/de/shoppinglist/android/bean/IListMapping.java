package de.shoppinglist.android.bean;

public interface IListMapping 
{
	public Store getStore();

	public void setStore(Store store);

	public Product getProduct();
	
	public void setProduct(Product product);

	public String getQuantity();
	
	public void setQuantity(String quantity);

	public short isChecked();

	public void setChecked(short checked);
}
