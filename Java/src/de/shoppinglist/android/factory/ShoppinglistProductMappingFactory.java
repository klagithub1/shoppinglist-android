package de.shoppinglist.android.factory;

import de.shoppinglist.android.bean.FavoriteProductMapping;
import de.shoppinglist.android.bean.ShoppinglistProductMapping;

public class ShoppinglistProductMappingFactory extends AbstractListFactory{

	public ShoppinglistProductMappingFactory()
	{
		listProdMapping = new ShoppinglistProductMapping();
	}
	
	@Override
	public FavoriteProductMapping createFavoriteProductMapping() {
		return null;
	}

	@Override
	public ShoppinglistProductMapping createShoppinglistProductMapping() {
		
		return new ShoppinglistProductMapping();
	}

}
