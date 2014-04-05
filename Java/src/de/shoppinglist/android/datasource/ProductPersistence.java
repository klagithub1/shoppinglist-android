package de.shoppinglist.android.datasource;

import android.database.Cursor;
import de.shoppinglist.android.bean.Product;
import de.shoppinglist.android.constant.DBConstants;
import de.shoppinglist.android.helper.TranslateUmlauts;

public class ProductPersistence implements Persistence{
	private ShoppinglistDataSourceData data = ShoppinglistDataSourceData.getInstance();
	
	public boolean isProductNotInUse(final int productId) {
		boolean isNotInShoppingListProductMappingInUse = true;
		final String sqlShoppingListProductMapping = "SELECT "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ " FROM " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ " WHERE " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ "." + DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ " = " + productId;

		final Cursor shoppingListProductMappingCursor = this.data.getDatabase().rawQuery(
				sqlShoppingListProductMapping, null);
		if (shoppingListProductMappingCursor.getCount() != 0) {
			isNotInShoppingListProductMappingInUse = false;
		}
		shoppingListProductMappingCursor.close();

		boolean isNotInFavoriteProductMappingInUse = true;
		final String sqlFavoriteProductMapping = "SELECT "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID
				+ " FROM " + DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME
				+ " WHERE " + DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME
				+ "." + DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID
				+ " = " + productId;

		final Cursor favoriteProductMappingCursor = this.data.getDatabase().rawQuery(
				sqlFavoriteProductMapping, null);
		if (favoriteProductMappingCursor.getCount() != 0) {
			isNotInFavoriteProductMappingInUse = false;
		}
		favoriteProductMappingCursor.close();

		return (isNotInShoppingListProductMappingInUse && isNotInFavoriteProductMappingInUse);
	}
	
	public void delete(final Object productId) {
		final String sqlDeleteProduct = "DELETE FROM "
				+ DBConstants.TAB_PRODUCT_NAME + " WHERE "
				+ DBConstants.TAB_PRODUCT_NAME + "."
				+ DBConstants.COL_PRODUCT_ID + " = " + (Integer)productId;

		this.data.getDatabase().execSQL(sqlDeleteProduct);
	}
	
	public Product getProductByNameAndUnit(String productName, final int unitId) {
		productName = TranslateUmlauts.translateFromGermanUmlauts(productName);

		final String sqlQuery = "SELECT " + DBConstants.COL_PRODUCT_ID + ", "
				+ DBConstants.COL_PRODUCT_NAME + ", "
				+ DBConstants.COL_PRODUCT_UNIT_ID + " FROM "
				+ DBConstants.TAB_PRODUCT_NAME + " WHERE UPPER("
				+ DBConstants.TAB_PRODUCT_NAME + "."
				+ DBConstants.COL_PRODUCT_NAME + ") = '"
				+ productName.toUpperCase().trim() + "' AND "
				+ DBConstants.COL_PRODUCT_UNIT_ID + " = " + unitId;

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);
		Product product = null;

		if (cursor.getCount() == 1) {
			cursor.moveToNext();
			product = new Product();
			product.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_PRODUCT_ID)));
			product.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor
							.getColumnIndex(DBConstants.COL_PRODUCT_NAME))));
		}
		return product;
	}
	
	public void save(String name, final int unitId) {
		name = TranslateUmlauts.translateFromGermanUmlauts(name);

		final String sqlQuery = "INSERT INTO " + DBConstants.TAB_PRODUCT_NAME
				+ " (" + DBConstants.COL_PRODUCT_NAME + ", "
				+ DBConstants.COL_PRODUCT_UNIT_ID + ") VALUES ('" + name.trim()
				+ "', " + unitId + ")";
		this.data.getDatabase().execSQL(sqlQuery);
	}
	
	public void update(Object o) {
		final Product product = (Product) o;
		final String sqlQuery = "UPDATE " + DBConstants.TAB_PRODUCT_NAME
				+ " SET " + DBConstants.COL_PRODUCT_NAME + " = '"
				+ product.getName().trim() + "' , "
				+ DBConstants.COL_PRODUCT_UNIT_ID + " = "
				+ product.getUnit().getId() + " WHERE "
				+ DBConstants.TAB_PRODUCT_NAME + "."
				+ DBConstants.COL_PRODUCT_ID + " = " + product.getId();

		this.data.getDatabase().execSQL(sqlQuery);
	}

	public void save(String name) {
		// TODO Auto-generated method stub
		
	}
}
