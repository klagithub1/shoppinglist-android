package de.shoppinglist.android.datasource;

import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import de.shoppinglist.android.bean.Favorite;
import de.shoppinglist.android.bean.FavoriteProductMapping;
import de.shoppinglist.android.bean.Product;
import de.shoppinglist.android.bean.Store;
import de.shoppinglist.android.bean.Unit;
import de.shoppinglist.android.constant.DBConstants;
import de.shoppinglist.android.helper.TranslateUmlauts;

public class FavoriteProductMappingPersistence {
	private ShoppinglistDataSourceData data = ShoppinglistDataSourceData.getInstance();
	private ProductPersistence productPersistence;
	private FavoritePersistence favoritePersistence;
	private StorePersistence storePersistence;
	
	public FavoriteProductMappingPersistence(
			ProductPersistence productPersistence,
			FavoritePersistence favoritePersistence,
			StorePersistence storePersistence) {
		super();
		this.productPersistence = productPersistence;
		this.favoritePersistence = favoritePersistence;
		this.storePersistence = storePersistence;
	}

	public FavoriteProductMapping doesFavoriteProductMappingExist(
			final int favoriteId, final int storeId, final int productId) {

		final String sqlQuery = "SELECT "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_ID + ", "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_FAVORITE_ID + ", "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_STORE_ID + ", "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID + ", "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_QUANTITY + " FROM "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + " WHERE "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_STORE_ID + " = "
				+ storeId + " AND "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID + " = "
				+ productId + " AND "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_FAVORITE_ID + " = "
				+ favoriteId;

		final Cursor cursor = data.getDatabase().rawQuery(sqlQuery, null);

		FavoriteProductMapping favoriteProductMapping = null;

		while ((cursor.getCount() != 0) && cursor.moveToNext()) {
			favoriteProductMapping = new FavoriteProductMapping();

			final Store store = new Store();
			store.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_FAVORITE_PRODUCT_MAPPING_STORE_ID)));
			favoriteProductMapping.setStore(store);

			final Product product = new Product();
			product.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID)));
			favoriteProductMapping.setProduct(product);

			final Favorite favorite = new Favorite();
			favorite.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_FAVORITE_PRODUCT_MAPPING_FAVORITE_ID)));
			favoriteProductMapping.setFavorite(favorite);

			favoriteProductMapping
					.setQuantity(cursor.getString(cursor
							.getColumnIndex(DBConstants.COL_FAVORITE_PRODUCT_MAPPING_QUANTITY)));
			favoriteProductMapping
					.setId(cursor.getInt(cursor
							.getColumnIndex(DBConstants.COL_FAVORITE_PRODUCT_MAPPING_ID)));

		}
		cursor.close();
		return favoriteProductMapping;
	}
	
	public void deleteFavoriteProductMapping(final int favoriteProductMappingId) {

		// temporary save the product id to delete, for the check, whether it
		// could be deleted in table: product
		final String sqlNoteProductsIdForFurtherCheck = "SELECT "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID
				+ " FROM " + DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME
				+ " WHERE " + DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME
				+ "." + DBConstants.COL_FAVORITE_PRODUCT_MAPPING_ID + " = "
				+ favoriteProductMappingId;

		final Cursor productIdCursor = this.data.getDatabase().rawQuery(
				sqlNoteProductsIdForFurtherCheck, null);

		final String sqlDeleteMappings = "DELETE FROM "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + " WHERE "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_ID + " = "
				+ favoriteProductMappingId;

		this.data.getDatabase().execSQL(sqlDeleteMappings);

		// delete the products which could be deleted
		while (productIdCursor.moveToNext()) {
			final int productId = productIdCursor
					.getInt(productIdCursor
							.getColumnIndex(DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID));
			if (productPersistence.isProductNotInUse(productId)) {
				productPersistence.delete(productId);
			}
		}
		productIdCursor.close();

	}
	
	public void deleteFavoriteAndItsMappings(final int favoriteId) {

		// temporary save the product id to delete, for the check, whether it
		// could be deleted in table: product
		final String sqlNoteProductsIdForFurtherCheck = "SELECT "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID
				+ " FROM " + DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME
				+ " WHERE " + DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME
				+ "." + DBConstants.COL_FAVORITE_PRODUCT_MAPPING_FAVORITE_ID
				+ " = " + favoriteId;

		final Cursor productIdCursor = this.data.getDatabase().rawQuery(
				sqlNoteProductsIdForFurtherCheck, null);

		final String sqlDeleteMappings = "DELETE FROM "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + " WHERE "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_FAVORITE_ID + " = "
				+ favoriteId;

		this.data.getDatabase().execSQL(sqlDeleteMappings);

		final String sqlDeleteFavorite = "DELETE FROM "
				+ DBConstants.TAB_FAVORITE_NAME + " WHERE "
				+ DBConstants.TAB_FAVORITE_NAME + "."
				+ DBConstants.COL_FAVORITE_ID + " = " + favoriteId;

		this.data.getDatabase().execSQL(sqlDeleteFavorite);

		// delete the products which could be deleted
		while (productIdCursor.moveToNext()) {
			final int productId = productIdCursor
					.getInt(productIdCursor
							.getColumnIndex(DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID));
			if (productPersistence.isProductNotInUse(productId)) {
				productPersistence.delete(productId);
			}
		}
		productIdCursor.close();

	}
	
	public List<FavoriteProductMapping> getFavoriteProductMappingsByFavoriteId(
			final int favoriteId) {

		String sqlQuery = "SELECT * FROM "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME
				+ " INNER JOIN " + DBConstants.TAB_FAVORITE_NAME + " on "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_FAVORITE_ID + " = "
				+ DBConstants.TAB_FAVORITE_NAME + "."
				+ DBConstants.COL_FAVORITE_ID + " INNER JOIN "
				+ DBConstants.TAB_STORE_NAME + " on "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_STORE_ID + " = "
				+ DBConstants.TAB_STORE_NAME + "." + DBConstants.COL_STORE_ID
				+ " INNER JOIN " + DBConstants.TAB_PRODUCT_NAME + " on "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID + " = "
				+ DBConstants.TAB_PRODUCT_NAME + "."
				+ DBConstants.COL_PRODUCT_ID + " INNER JOIN "
				+ DBConstants.TAB_UNIT_NAME + " on "
				+ DBConstants.TAB_PRODUCT_NAME + "."
				+ DBConstants.COL_PRODUCT_UNIT_ID + " = "
				+ DBConstants.TAB_UNIT_NAME + "." + DBConstants.COL_UNIT_ID
				+ " WHERE " + DBConstants.TAB_FAVORITE_NAME + "."
				+ DBConstants.COL_FAVORITE_ID + " = " + favoriteId;

		sqlQuery += " ORDER BY " + DBConstants.COL_PRODUCT_NAME;

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		final List<FavoriteProductMapping> favoriteProductMappings = new LinkedList<FavoriteProductMapping>();
		while (cursor.moveToNext()) {

			final FavoriteProductMapping favoriteProductMapping = new FavoriteProductMapping();

			final Favorite favorite = new Favorite();
			favorite.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_FAVORITE_ID)));
			favorite.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor
							.getColumnIndex(DBConstants.COL_FAVORITE_NAME))));

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

			favoriteProductMapping
					.setId(cursor.getInt(cursor
							.getColumnIndex(DBConstants.COL_FAVORITE_PRODUCT_MAPPING_ID)));
			favoriteProductMapping
					.setQuantity(cursor.getString(cursor
							.getColumnIndex(DBConstants.COL_FAVORITE_PRODUCT_MAPPING_QUANTITY)));
			favoriteProductMapping.setProduct(product);
			favoriteProductMapping.setFavorite(favorite);
			favoriteProductMapping.setStore(store);

			favoriteProductMappings.add(favoriteProductMapping);
		}
		cursor.close();

		return favoriteProductMappings;
	}
	
	public void saveFavoriteProductMapping(final int favoriteId,
			final int storeId, final int productId, final String quantity) {
		final String sqlQuery = "INSERT INTO "
				+ DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + " ("
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_FAVORITE_ID + ", "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_STORE_ID + ", "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID + ", "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_QUANTITY
				+ ") VALUES (" + favoriteId + ", " + storeId + ", " + productId
				+ ", " + quantity + ")";
		this.data.getDatabase().execSQL(sqlQuery);
	}
	
//	public void updateFavoriteProductMapping(FavoriteProductMapping favoriteProductMapping) {
//		updateFavoriteProductMapping(favoriteProductMapping, favoriteProductMapping.getProduct().getId());
//	}
	public void updateFavoriteProductMapping(final int favoriteProductMappingId, final int storeId,
			final int productId, final String quantity) {

		final String sqlQuery = "UPDATE " + DBConstants.TAB_FAVORITE_PRODUCT_MAPPING_NAME + " SET "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_STORE_ID + " = " + storeId + ", "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_PRODUCT_ID + " = " + productId + ", "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_QUANTITY + " = " + quantity + " WHERE "
				+ DBConstants.COL_FAVORITE_PRODUCT_MAPPING_ID + " = " + favoriteProductMappingId;

		data.getDatabase().execSQL(sqlQuery);
	}
}
