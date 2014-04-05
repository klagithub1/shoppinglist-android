package de.shoppinglist.android.datasource;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import de.shoppinglist.android.bean.History;
import de.shoppinglist.android.bean.Shoppinglist;
import de.shoppinglist.android.constant.DBConstants;

public class HistoryPersistence implements Persistence{
	private ShoppinglistDataSourceData data = ShoppinglistDataSourceData.getInstance();
	private ShoppinglistProductMappingPersistence shoppinglistProductMappingPersistence;
	public HistoryPersistence(
			ShoppinglistProductMappingPersistence shoppinglistProductMappingPersistence) {
		this.shoppinglistProductMappingPersistence = shoppinglistProductMappingPersistence;
	}

	public void addAllToHistory() {
		final String replaceUmlautsHistoryPart1 = "replace(replace(replace(replace(replace(replace(replace(";
		final String replaceUmlautsHistoryPart2 = ",'&auml;','�),'&Auml;','�),'&ouml;','�),'&Ouml;','�),'&uuml;','�),'&Uuml;','�),'&szlig;','�)";

		final String sqlInsertHistory = "INSERT INTO "
				+ DBConstants.TAB_HISTORY_NAME + " ("
				+ DBConstants.COL_HISTORY_SHOPPINGLIST_ID + ", "
				+ DBConstants.COL_HISTORY_STORE + ", "
				+ DBConstants.COL_HISTORY_PRODUCT + ", "
				+ DBConstants.COL_HISTORY_UNIT + ", "
				+ DBConstants.COL_HISTORY_QUANTITY + ") SELECT "
				+ DBConstants.COL_SHOPPINGLIST_ID + ", "
				+ replaceUmlautsHistoryPart1 + DBConstants.COL_STORE_NAME
				+ replaceUmlautsHistoryPart2 + ", "
				+ replaceUmlautsHistoryPart1 + DBConstants.COL_PRODUCT_NAME
				+ replaceUmlautsHistoryPart2 + ", "
				+ replaceUmlautsHistoryPart1 + DBConstants.COL_UNIT_NAME
				+ replaceUmlautsHistoryPart2 + ", "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_QUANTITY
				+ " FROM " + DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME
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
				+ DBConstants.TAB_UNIT_NAME + "." + DBConstants.COL_UNIT_ID;

		this.data.getDatabase().execSQL(sqlInsertHistory);

		shoppinglistProductMappingPersistence.deleteAllShoppinglistProductMappings();
	}
	
	public void delete(Object o) {

		final String sqlDeleteHistory = "DELETE FROM "
				+ DBConstants.TAB_HISTORY_NAME;

		this.data.getDatabase().execSQL(sqlDeleteHistory);
	}
	
	public List<History> getHistoryByShoppinglistId(final int shoppinglistId) {
		final String sqlQuery = "SELECT * FROM " + DBConstants.TAB_HISTORY_NAME
				+ " INNER JOIN " + DBConstants.TAB_SHOPPINGLIST_NAME + " ON "
				+ DBConstants.COL_HISTORY_SHOPPINGLIST_ID + " = "
				+ DBConstants.COL_SHOPPINGLIST_ID + " WHERE "
				+ DBConstants.COL_HISTORY_SHOPPINGLIST_ID + " = "
				+ shoppinglistId + " ORDER BY "
				+ DBConstants.COL_HISTORY_PRODUCT;

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		final List<History> historyList = new LinkedList<History>();

		while (cursor.moveToNext()) {
			final History history = new History();

			history.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_HISTORY_ID)));
			history.setProduct(cursor.getString(cursor
					.getColumnIndex(DBConstants.COL_HISTORY_PRODUCT)));
			history.setStore(cursor.getString(cursor
					.getColumnIndex(DBConstants.COL_HISTORY_STORE)));
			history.setUnit(cursor.getString(cursor
					.getColumnIndex(DBConstants.COL_HISTORY_UNIT)));
			history.setQuantity(cursor.getString(cursor
					.getColumnIndex(DBConstants.COL_HISTORY_QUANTITY)));

			final Shoppinglist shoppinglist = new Shoppinglist();
			shoppinglist.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_SHOPPINGLIST_ID)));
			shoppinglist
					.setCreatedTime(Timestamp.valueOf(cursor.getString(cursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_CREATED_TIME))));
			shoppinglist
					.setFinishedTime(Timestamp.valueOf(cursor.getString(cursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_FINISHED_TIME))));

			history.setShoppinglist(shoppinglist);

			historyList.add(history);
		}
		cursor.close();

		return historyList;
	}
	
	public List<Shoppinglist> getHistoryShoppinglists() {
		final String sqlQuery = "SELECT DISTINCT "
				+ DBConstants.COL_SHOPPINGLIST_ID + ", "
				+ DBConstants.COL_SHOPPINGLIST_FINISHED_TIME + " FROM "
				+ DBConstants.TAB_HISTORY_NAME + " INNER JOIN "
				+ DBConstants.TAB_SHOPPINGLIST_NAME + " ON "
				+ DBConstants.COL_HISTORY_SHOPPINGLIST_ID + " = "
				+ DBConstants.COL_SHOPPINGLIST_ID;

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		final List<Shoppinglist> shoppinglists = new LinkedList<Shoppinglist>();

		while (cursor.moveToNext()) {
			final Shoppinglist shoppinglist = new Shoppinglist();

			shoppinglist.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_SHOPPINGLIST_ID)));
			shoppinglist
					.setFinishedTime(Timestamp.valueOf(cursor.getString(cursor
							.getColumnIndex(DBConstants.COL_SHOPPINGLIST_FINISHED_TIME))));

			shoppinglists.add(shoppinglist);
		}
		cursor.close();

		return shoppinglists;
	}

	public void save(String name) {
		// TODO Auto-generated method stub
		
	}

	public void update(Object object) {
		// TODO Auto-generated method stub
		
	}
}
