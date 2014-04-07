package de.shoppinglist.android.datasource;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import de.shoppinglist.android.bean.Product;
import de.shoppinglist.android.bean.Shoppinglist;
import de.shoppinglist.android.bean.ShoppinglistProductMapping;
import de.shoppinglist.android.bean.Store;
import de.shoppinglist.android.bean.Unit;
import de.shoppinglist.android.constant.DBConstants;
import de.shoppinglist.android.constant.GlobalValues;
import de.shoppinglist.android.helper.TranslateUmlauts;

public class ShoppinglistProductMappingPersistence {
	private ShoppinglistDataSourceData data = ShoppinglistDataSourceData.getInstance();
	private ShoppinglistPersistence shoppinglistPersistence;
	private ProductPersistence productPersistence;
	private StorePersistence storePersistence;

	public ShoppinglistProductMappingPersistence(
			ShoppinglistPersistence shoppinglistPersistance,
			ProductPersistence productPersistance,
			StorePersistence storePersistence) {
		this.shoppinglistPersistence = shoppinglistPersistance;
		this.productPersistence = productPersistance;
		this.storePersistence = storePersistence;
	}

	public ShoppinglistProductMapping doesShoppinglistProductMappingExist(
			final int storeId, final int productId) {

		final String sqlQuery = "SELECT * FROM "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + " WHERE "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID + " = "
				+ storeId + " AND "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ " = " + productId;

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		ShoppinglistProductMapping shoppinglistProductMapping = null;

		while ((cursor.getCount() != 0) && cursor.moveToNext()) {
			shoppinglistProductMapping = new ShoppinglistProductMapping();

			final Store store = new Store();
			store.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID)));
			shoppinglistProductMapping.setStore(store);

			final Product product = new Product();
			product.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID)));
			shoppinglistProductMapping.setProduct(product);

			final Shoppinglist shoppinglist = new Shoppinglist();
			shoppinglist
					.setId(cursor.getInt(cursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_SHOPPINGLIST_ID)));
			shoppinglistProductMapping.setShoppinglist(shoppinglist);

			shoppinglistProductMapping
					.setQuantity(cursor.getString(cursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_QUANTITY)));
			shoppinglistProductMapping
					.setId(cursor.getInt(cursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_ID)));

		}
		cursor.close();

		return shoppinglistProductMapping;
	}
	
	public void deleteAllShoppinglistProductMappings() {

		// temporary save the product id to delete, for the check, whether it
		// could be deleted in table: product
		final String sqlNoteProductsIdForFurtherCheck = "SELECT DISTINCT "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ " FROM " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME;

		final Cursor productIdCursor = this.data.getDatabase().rawQuery(
				sqlNoteProductsIdForFurtherCheck, null);

		// delete the mapping entries for this id
		final String sqlDeleteFromShoppinglistProductMapping = "DELETE FROM "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME;

		this.data.getDatabase().execSQL(sqlDeleteFromShoppinglistProductMapping);

		// delete the products which could be deleted
		while (productIdCursor.moveToNext()) {
			final int productId = productIdCursor
					.getInt(productIdCursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID));
			if (productPersistence.isProductNotInUse(productId)) {
				productPersistence.delete(productId);
			}
		}
		productIdCursor.close();

	}
	
	public void deleteShoppinglistProductMapping(
			final int shoppinglistProductMappingId) {

		// temporary save the product id to delete, for the check, whether it
		// could be deleted in table: product
		final String sqlNoteProductsIdForFurtherCheck = "SELECT "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ " FROM " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ " WHERE " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ "." + DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_ID + " = "
				+ shoppinglistProductMappingId;

		final Cursor productIdCursor = this.data.getDatabase().rawQuery(
				sqlNoteProductsIdForFurtherCheck, null);

		// delete the mapping entries for this id
		final String sqlDeleteFromShoppinglistProductMapping = "DELETE FROM "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + " WHERE "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_ID + " = "
				+ shoppinglistProductMappingId;

		this.data.getDatabase().execSQL(sqlDeleteFromShoppinglistProductMapping);

		// delete the products which could be deleted
		while (productIdCursor.moveToNext()) {
			final int productId = productIdCursor
					.getInt(productIdCursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID));
			if (productPersistence.isProductNotInUse(productId)) {
				productPersistence.delete(productId);
			}
		}
		productIdCursor.close();

	}
	
	public List<ShoppinglistProductMapping> getProductsOnShoppingList(
			final int storeId) {

		String sqlQuery = "SELECT * FROM "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
				+ " INNER JOIN " + DBConstants.TAB_SHOPPINGLIST_NAME + " on "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_SHOPPINGLIST_ID
				+ " = " + DBConstants.TAB_SHOPPINGLIST_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_ID + " INNER JOIN "
				+ DBConstants.TAB_STORE_NAME + " on "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID + " = "
				+ DBConstants.TAB_STORE_NAME + "." + DBConstants.COL_STORE_ID
				+ " INNER JOIN " + DBConstants.TAB_PRODUCT_NAME + " on "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ " = " + DBConstants.TAB_PRODUCT_NAME + "."
				+ DBConstants.COL_PRODUCT_ID + " INNER JOIN "
				+ DBConstants.TAB_UNIT_NAME + " on "
				+ DBConstants.TAB_PRODUCT_NAME + "."
				+ DBConstants.COL_PRODUCT_UNIT_ID + " = "
				+ DBConstants.TAB_UNIT_NAME + "." + DBConstants.COL_UNIT_ID
				+ " WHERE " + DBConstants.TAB_SHOPPINGLIST_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_FINISHED_TIME + " is null";
		if (storeId != -1) {
			sqlQuery += " AND " + DBConstants.TAB_STORE_NAME + "."
					+ DBConstants.COL_STORE_ID + " = " + storeId;
		}
		sqlQuery += " ORDER BY LOWER(" + DBConstants.COL_PRODUCT_NAME + ")";

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		final List<ShoppinglistProductMapping> shoppinglistProductMappings = new LinkedList<ShoppinglistProductMapping>();
		while (cursor.moveToNext()) {

			final ShoppinglistProductMapping shoppinglistProductMapping = new ShoppinglistProductMapping();

			final Shoppinglist shoppinglist = new Shoppinglist();
			shoppinglist.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_SHOPPINGLIST_ID)));
			if (cursor
					.getString((cursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_FINISHED_TIME))) != null) {
				shoppinglist
						.setFinishedTime(Timestamp.valueOf(cursor.getString((cursor
								.getColumnIndex(DBConstants.COL_SHOPPINGLIST_FINISHED_TIME)))));
			}

			final Unit unit = new Unit();
			unit.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_UNIT_ID)));
			unit.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor.getColumnIndex(DBConstants.COL_UNIT_NAME))));

			final Product product = new Product();
			product.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_PRODUCT_ID)));
			product.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor
							.getColumnIndex(DBConstants.COL_PRODUCT_NAME))));
			product.setUnit(unit);

			final Store store = new Store();
			store.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_STORE_ID)));
			store.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor
							.getColumnIndex(DBConstants.COL_STORE_NAME))));
			store.setCountProducts(storePersistence.getProductCountForStore(store.getId()));
			store.setAlreadyCheckedProducts(storePersistence
					.getCheckedProductCountForStore(store.getId()));

			shoppinglistProductMapping
					.setId(cursor.getInt(cursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_ID)));
			shoppinglistProductMapping
					.setQuantity(cursor.getString(cursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_QUANTITY)));
			shoppinglistProductMapping
					.setChecked(cursor.getShort(cursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_CHECKED)));
			shoppinglistProductMapping.setProduct(product);
			shoppinglistProductMapping.setShoppinglist(shoppinglist);
			shoppinglistProductMapping.setStore(store);

			shoppinglistProductMappings.add(shoppinglistProductMapping);
		}
		cursor.close();

		return shoppinglistProductMappings;
	}
	
	public void checkShoppinglistProductMapping(
			final int shoppinglistProductMappingId) {
		final String sqlQuery = "UPDATE "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + " SET "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_CHECKED + " = "
				+ GlobalValues.YES + " WHERE "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_ID + " = "
				+ shoppinglistProductMappingId;

		this.data.getDatabase().execSQL(sqlQuery);
	}
	
	public void uncheckShoppinglistProductMapping(
			final int shoppinglistProductMappingId) {
		final String sqlQuery = "UPDATE "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + " SET "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_CHECKED + " = "
				+ GlobalValues.NO + " WHERE "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_ID + " = "
				+ shoppinglistProductMappingId;

		this.data.getDatabase().execSQL(sqlQuery);
	}
	
	public void saveShoppingListProductMapping(final int storeId,
			final int productId, final String quantity, final short checked) {
		final String queryToGetShoppinglistId = "SELECT MAX("
				+ DBConstants.COL_SHOPPINGLIST_ID + ") as "
				+ DBConstants.COL_SHOPPINGLIST_ID + " FROM "
				+ DBConstants.TAB_SHOPPINGLIST_NAME + " WHERE "
				+ DBConstants.COL_SHOPPINGLIST_FINISHED_TIME + " is null";

		final Cursor shoppinglistIdCursor = this.data.getDatabase().rawQuery(
				queryToGetShoppinglistId, null);
		int shoppinglistId = -1;

		if (shoppinglistIdCursor.getCount() == 1) {
			shoppinglistIdCursor.moveToNext();
			shoppinglistId = shoppinglistIdCursor.getInt(shoppinglistIdCursor
					.getColumnIndex(DBConstants.COL_SHOPPINGLIST_ID));
		}
		shoppinglistIdCursor.close();

		final String sqlQuery = "INSERT INTO "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + " ("
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_SHOPPINGLIST_ID
				+ ", " + DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID
				+ ", "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ ", " + DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_QUANTITY
				+ ", " + DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_CHECKED
				+ ") VALUES (" + shoppinglistId + ", " + storeId + ", "
				+ productId + ", " + quantity + ", " + checked + ")";
		this.data.getDatabase().execSQL(sqlQuery);
	}
	
	public void updateShoppinglistProductMapping(
			final int shoppinglistProductMappingId, final int storeId,
			final int productId, final String quantity) {
		final String sqlQuery = "UPDATE "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + " SET "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID + " = "
				+ storeId + ", "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID
				+ " = " + productId + ", "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_QUANTITY + " = "
				+ quantity + " WHERE "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_ID + " = "
				+ shoppinglistProductMappingId;

		this.data.getDatabase().execSQL(sqlQuery);
	}
}
