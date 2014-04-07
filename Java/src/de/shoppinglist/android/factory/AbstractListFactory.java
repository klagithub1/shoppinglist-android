package de.shoppinglist.android.factory;

import de.shoppinglist.android.bean.BusinessBean;
import de.shoppinglist.android.bean.FavoriteProductMapping;
import de.shoppinglist.android.bean.ShoppinglistProductMapping;

public abstract class AbstractListFactory 
{
	de.shoppinglist.android.bean.ListProductMapping listProdMapping = null;
	
	public BusinessBean returnProductMapping()
	{
		return listProdMapping.getSpecificBean();
	}
	public abstract FavoriteProductMapping createFavoriteProductMapping();
	public abstract ShoppinglistProductMapping createShoppinglistProductMapping();
	
}
