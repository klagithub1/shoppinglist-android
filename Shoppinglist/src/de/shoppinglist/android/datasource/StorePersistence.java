package de.shoppinglist.android.datasource;

import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import de.shoppinglist.android.bean.Store;
import de.shoppinglist.android.constant.DBConstants;
import de.shoppinglist.android.constant.GlobalValues;
import de.shoppinglist.android.helper.TranslateUmlauts;

public class StorePersistence implements Persistence{
	private ShoppinglistDataSourceData data = ShoppinglistDataSourceData.getInstance();
	private ProductPersistence productPersistence;

	public StorePersistence(ProductPersistence productPersistence) {
		super();
		this.productPersistence = productPersistence;
	}

	public boolean isStoreNotInUse(final int storeId) {

		boolean isNotInShoppingListProductMappingInUse = true;
		final String sqlShoppingListProductMapping = "SELECT "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID
				+ " FROM " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ " WHERE " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ "." + DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID
				+ " = " + storeId;

		final Cursor shoppingListProductMappingCursor = this.data.getDatabase().rawQuery(
				sqlShoppingListProductMapping, null);
		if (shoppingListProductMappingCursor.getCount() != 0) {
			isNotInShoppingListProductMappingInUse = false;
		}
		shoppingListProductMappingCursor.close();

		boolean isNotInFavoriteProductMappingInUse = true;
		final String sqlFavoriteProductMapping = "SELECT "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_STORE_ID + " FROM "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + " WHERE "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_STORE_ID + " = "
				+ storeId;

		final Cursor favoriteProductMappingCursor = this.data.getDatabase().rawQuery(
				sqlFavoriteProductMapping, null);
		if (favoriteProductMappingCursor.getCount() != 0) {
			isNotInFavoriteProductMappingInUse = false;
		}
		favoriteProductMappingCursor.close();

		return (isNotInShoppingListProductMappingInUse && isNotInFavoriteProductMappingInUse);
	}
	
	public void deleteProductsFromStoreList(final int storeId) {

		// temporary save the product ids to delete, for the check, whether they
		// could be deleted in table: product
		final String sqlNoteProductsIdForFurtherCheck = "SELECT "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ " FROM " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ " WHERE " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ "." + DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID
				+ " = " + storeId;

		final Cursor productIdsCursor = this.data.getDatabase().rawQuery(
				sqlNoteProductsIdForFurtherCheck, null);

		// delete the mapping entries for this store
		final String sqlDeleteFromShoppinglistProductMapping = "DELETE FROM "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + " WHERE "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID + " = "
				+ storeId;

		this.data.getDatabase().execSQL(sqlDeleteFromShoppinglistProductMapping);

		// delete the products which could be deleted
		while (productIdsCursor.moveToNext()) {
			final int productId = productIdsCursor
					.getInt(productIdsCursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID));
			if (productPersistence.isProductNotInUse(productId)) {
				productPersistence.delete(productId);
			}
		}
		productIdsCursor.close();

	}
	
	public void delete(final Object storeId) {
		final String sqlQuery = "DELETE FROM " + DBConstants.TAB_STORE_NAME
				+ " WHERE " + DBConstants.COL_STORE_ID + " = " + (Integer) storeId;

		this.data.getDatabase().execSQL(sqlQuery);
	}
	
	public List<Store> getAllStores() {
		final String sqlQuery = "SELECT " + DBConstants.COL_STORE_ID + ", "
				+ DBConstants.COL_STORE_NAME + " FROM "
				+ DBConstants.TAB_STORE_NAME + " ORDER BY "
				+ DBConstants.COL_STORE_NAME;

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		final List<Store> stores = new LinkedList<Store>();

		while (cursor.moveToNext()) {
			final Store store = new Store();
			store.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_STORE_ID)));
			store.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor
							.getColumnIndex(DBConstants.COL_STORE_NAME))));
			store.setCountProducts(this.getProductCountForStore(store.getId()));
			store.setAlreadyCheckedProducts(this
					.getCheckedProductCountForStore(store.getId()));
			stores.add(store);
		}
		cursor.close();

		return stores;
	}
	
	public int getCheckedProductCountForStore(final int storeId) {
		final String sqlQuery = "SELECT COUNT("
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ ") AS "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ " FROM " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ " WHERE " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ "." + DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID
				+ " = " + storeId + " AND "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_CHECKED + " = "
				+ GlobalValues.YES;

		final Cursor checkedProductCountCursor = this.data.getDatabase().rawQuery(
				sqlQuery, null);

		int checkedProductCount = 0;
		while (checkedProductCountCursor.moveToNext()) {
			checkedProductCount = checkedProductCount
					+ checkedProductCountCursor
							.getInt(checkedProductCountCursor
									.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID));
		}
		checkedProductCountCursor.close();

		return checkedProductCount;
	}
	
	public int getProductCountForStore(final int storeId) {
		final String sqlQuery = "SELECT COUNT("
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ ") AS "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ " FROM " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ " WHERE " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ "." + DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID
				+ " = " + storeId;

		final Cursor productCountCursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		int productCount = 0;
		while (productCountCursor.moveToNext()) {
			productCount = productCount
					+ productCountCursor
							.getInt(productCountCursor
									.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID));
		}
		productCountCursor.close();

		return productCount;
	}
	
	public Store getStoreById(final int storeId) {
		final String sqlQuery = "SELECT " + DBConstants.COL_STORE_ID + ", "
				+ DBConstants.COL_STORE_NAME + " FROM "
				+ DBConstants.TAB_STORE_NAME + " WHERE "
				+ DBConstants.TAB_STORE_NAME + "." + DBConstants.COL_STORE_ID
				+ " = " + storeId + ";";

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);
		final Store store = new Store();

		cursor.moveToNext();
		store.setId(storeId);
		store.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
				.getString(cursor.getColumnIndex(DBConstants.COL_STORE_NAME))));
		store.setCountProducts(this.getProductCountForStore(store.getId()));
		store.setAlreadyCheckedProducts(this
				.getCheckedProductCountForStore(store.getId()));

		cursor.close();

		return store;
	}
	
	public Store getStoreByName(String storeName) {
		storeName = TranslateUmlauts.translateFromGermanUmlauts(storeName);

		final String sqlQuery = "SELECT " + DBConstants.COL_STORE_ID + ", "
				+ DBConstants.COL_STORE_NAME + " FROM "
				+ DBConstants.TAB_STORE_NAME + " WHERE UPPER("
				+ DBConstants.TAB_STORE_NAME + "." + DBConstants.COL_STORE_NAME
				+ ") = '" + storeName.toUpperCase().trim() + "'";

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		Store store = null;

		if (cursor.getCount() == 1) {
			cursor.moveToNext();
			store = new Store();
			store.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_STORE_ID)));
			store.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor
							.getColumnIndex(DBConstants.COL_STORE_NAME))));
		}
		cursor.close();

		return store;
	}
	
	public List<Store> getStoresForOverview() {
		final String sqlQuery = "SELECT DISTINCT " + DBConstants.COL_STORE_ID
				+ ", " + DBConstants.COL_STORE_NAME + " FROM "
				+ DBConstants.TAB_STORE_NAME + " INNER JOIN "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + " ON "
				+ DBConstants.TAB_STORE_NAME + "." + DBConstants.COL_STORE_ID
				+ " = " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ "." + DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID
				+ " ORDER BY " + DBConstants.COL_STORE_NAME;

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		final List<Store> stores = new LinkedList<Store>();
		while (cursor.moveToNext()) {

			final Store store = new Store();
			store.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_STORE_ID)));
			store.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor
							.getColumnIndex(DBConstants.COL_STORE_NAME))));
			store.setCountProducts(this.getProductCountForStore(store.getId()));
			store.setAlreadyCheckedProducts(this
					.getCheckedProductCountForStore(store.getId()));
			stores.add(store);
		}
		cursor.close();

		return stores;
	}
	
	public void save(String name) {
		name = TranslateUmlauts.translateFromGermanUmlauts(name);

		final String sqlQuery = "INSERT INTO " + DBConstants.TAB_STORE_NAME
				+ " (" + DBConstants.COL_STORE_NAME + ") VALUES ('"
				+ name.trim() + "')";

		this.data.getDatabase().execSQL(sqlQuery);
	}
	
	public void update(Object o) {
		final Store store = (Store) o;
		final String sqlQuery = "UPDATE " + DBConstants.TAB_STORE_NAME
				+ " SET " + DBConstants.COL_STORE_NAME + " = '"
				+ store.getName().trim() + "' WHERE "
				+ DBConstants.TAB_STORE_NAME + "." + DBConstants.COL_STORE_ID
				+ " = " + store.getId();

		this.data.getDatabase().execSQL(sqlQuery);
	}
}
