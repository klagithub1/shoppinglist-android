package de.shoppinglist.android.bean;

public class ShoppinglistProductMapping extends ListProductMapping
{

	private Shoppinglist shoppinglist;

	public Shoppinglist getShoppinglist() 
	{
		return shoppinglist;
	}

	public void setShoppinglist(Shoppinglist shoppinglist) 
	{
		this.shoppinglist = shoppinglist;
	}

	@Override
	public String toString() 
	{
		return getQuantity()+ " " + getProduct().getUnit().getName() + " " + getProduct().getName();
	}

	@Override
	public
	BusinessBean getSpecificBean() {
		
		return getShoppinglist();
	}
}
