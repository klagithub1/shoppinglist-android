package de.shoppinglist.android.factory;

import de.shoppinglist.android.bean.FavoriteProductMapping;
import de.shoppinglist.android.bean.ShoppinglistProductMapping;

public class FavoriteProductMappingsFactory  extends AbstractListFactory
{
	public FavoriteProductMappingsFactory()
	{
		listProdMapping = new FavoriteProductMapping();
	}

	@Override
	public FavoriteProductMapping createFavoriteProductMapping() 
	{
		
		return new FavoriteProductMapping();
	}

	@Override
	public ShoppinglistProductMapping createShoppinglistProductMapping() 
	{
		return null;
	}

}
