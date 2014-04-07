package de.shoppinglist.android.datasource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import de.shoppinglist.android.bean.Favorite;
import de.shoppinglist.android.bean.Product;
import de.shoppinglist.android.constant.DBConstants;
import de.shoppinglist.android.helper.TranslateUmlauts;

public class FavoritePersistence{
	private ShoppinglistDataSourceData data = ShoppinglistDataSourceData.getInstance();

	public List<Favorite> getAllFavorites() {

		final String sqlQuery = "SELECT " + DBConstants.COL_FAVORITE_ID + ", "
				+ DBConstants.COL_FAVORITE_NAME + " FROM "
				+ DBConstants.TAB_FAVORITE_NAME + " ORDER BY "
				+ DBConstants.COL_FAVORITE_NAME;

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		final List<Favorite> favorites = new LinkedList<Favorite>();
		while (cursor.moveToNext()) {
			final Favorite favorite = new Favorite();

			favorite.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_FAVORITE_ID)));
			favorite.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor
							.getColumnIndex(DBConstants.COL_FAVORITE_NAME))));

			favorites.add(favorite);
		}
		cursor.close();

		return favorites;
	}
	
	public Favorite getFavoriteByName(String favoriteName) {
		favoriteName = TranslateUmlauts
				.translateFromGermanUmlauts(favoriteName);

		final String sqlQuery = "SELECT " + DBConstants.COL_FAVORITE_ID + ", "
				+ DBConstants.COL_FAVORITE_NAME + " FROM "
				+ DBConstants.TAB_FAVORITE_NAME + " WHERE UPPER("
				+ DBConstants.TAB_FAVORITE_NAME + "."
				+ DBConstants.COL_FAVORITE_NAME + ") = '"
				+ favoriteName.toUpperCase().trim() + "'";

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		Favorite favorite = null;

		if (cursor.getCount() == 1) {
			cursor.moveToNext();
			favorite = new Favorite();
			favorite.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_FAVORITE_ID)));
			favorite.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor
							.getColumnIndex(DBConstants.COL_FAVORITE_NAME))));
		}
		cursor.close();

		return favorite;
	}
	
	public void save(String name) {
		name = TranslateUmlauts.translateFromGermanUmlauts(name);

		final String sqlQuery = "INSERT INTO " + DBConstants.TAB_FAVORITE_NAME
				+ " (" + DBConstants.COL_FAVORITE_NAME + ") VALUES ('"
				+ name.trim() + "')";

		this.data.getDatabase().execSQL(sqlQuery);
	}
	
	public void update(Favorite favorite) {
		final String sqlQuery = "UPDATE " + DBConstants.TAB_FAVORITE_NAME
				+ " SET " + DBConstants.COL_FAVORITE_NAME + " = '"
				+ favorite.getName().trim() + "' WHERE "
				+ DBConstants.TAB_FAVORITE_NAME + "."
				+ DBConstants.COL_FAVORITE_ID + " = " + favorite.getId();

		this.data.getDatabase().execSQL(sqlQuery);
	}

}
