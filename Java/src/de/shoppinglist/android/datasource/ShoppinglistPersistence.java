package de.shoppinglist.android.datasource;

import de.shoppinglist.android.bean.BusinessBean;
import de.shoppinglist.android.bean.Shoppinglist;
import de.shoppinglist.android.constant.DBConstants;

public class ShoppinglistPersistence implements IPersistence{
	private ShoppinglistDataSourceData data = ShoppinglistDataSourceData.getInstance();
	
	public void add(BusinessBean bean){
		Shoppinglist shoppingList = (Shoppinglist) bean;
		createNewShoppinglist();
	}
	
	public void update(BusinessBean bean){
		Shoppinglist shoppingList = (Shoppinglist) bean;
		addAllToHistory();
	}

	public void createNewShoppinglist() {

		// at first set the old shoppinglist to finished (current_timestamp)
		final String sqlMarkShoppinglistFinished = "UPDATE "
				+ DBConstants.TAB_SHOPPINGLIST_NAME + " SET "
				+ DBConstants.COL_SHOPPINGLIST_FINISHED_TIME
				+ " = CURRENT_TIMESTAMP WHERE "
				+ DBConstants.COL_SHOPPINGLIST_ID + " = (SELECT MAX("
				+ DBConstants.COL_SHOPPINGLIST_ID + ") AS "
				+ DBConstants.COL_SHOPPINGLIST_ID + " FROM "
				+ DBConstants.TAB_SHOPPINGLIST_NAME + ")";

		this.data.getDatabase().execSQL(sqlMarkShoppinglistFinished);

		// then insert a new one
		final String sqlInsertNew = "INSERT INTO "
				+ DBConstants.TAB_SHOPPINGLIST_NAME + " ("
				+ ") VALUES (CURRENT_TIMESTAMP)";

		this.data.getDatabase().execSQL(sqlInsertNew);
	}
	
	
	/**
	 * adds the current shoppinglist with all its relations to the table.history
	 */
	public void addAllToHistory() {
		this.data.isDbLockedByThread();

		final String replaceUmlautsHistoryPart1 = "replace(replace(replace(replace(replace(replace(replace(";
		final String replaceUmlautsHistoryPart2 = ",'&auml;','ä'),'&Auml;','Ä'),'&ouml;','ö'),'&Ouml;','Ö'),'&uuml;','ü'),'&Uuml;','Ü'),'&szlig;','ß')";

		final String sqlInsertHistory = "INSERT INTO " + DBConstants.TAB_HISTORY_NAME + " ("
				+ DBConstants.COL_HISTORY_SHOPPINGLIST_ID + ", " + DBConstants.COL_HISTORY_STORE
				+ ", " + DBConstants.COL_HISTORY_PRODUCT + ", " + DBConstants.COL_HISTORY_UNIT
				+ ", " + DBConstants.COL_HISTORY_QUANTITY + ") SELECT "
				+ DBConstants.COL_SHOPPINGLIST_ID + ", " + replaceUmlautsHistoryPart1
				+ DBConstants.COL_STORE_NAME + replaceUmlautsHistoryPart2 + ", "
				+ replaceUmlautsHistoryPart1 + DBConstants.COL_PRODUCT_NAME
				+ replaceUmlautsHistoryPart2 + ", " + replaceUmlautsHistoryPart1
				+ DBConstants.COL_UNIT_NAME + replaceUmlautsHistoryPart2 + ", "
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_QUANTITY + " FROM "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + " INNER JOIN "
				+ DBConstants.TAB_SHOPPINGLIST_NAME + " on "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_SHOPPINGLIST_ID + " = "
				+ DBConstants.TAB_SHOPPINGLIST_NAME + "." + DBConstants.COL_SHOPPINGLIST_ID
				+ " INNER JOIN " + DBConstants.TAB_STORE_NAME + " on "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_STORE_ID + " = "
				+ DBConstants.TAB_STORE_NAME + "." + DBConstants.COL_STORE_ID + " INNER JOIN "
				+ DBConstants.TAB_PRODUCT_NAME + " on "
				+ DBConstants.TAB_SHOPPINGLIST_PRODUCT_MAPPING_NAME + "."
				+ DBConstants.COL_SHOPPINGLIST_PRODUCT_MAPPING_PRODUCT_ID + " = "
				+ DBConstants.TAB_PRODUCT_NAME + "." + DBConstants.COL_PRODUCT_ID + " INNER JOIN "
				+ DBConstants.TAB_UNIT_NAME + " on " + DBConstants.TAB_PRODUCT_NAME + "."
				+ DBConstants.COL_PRODUCT_UNIT_ID + " = " + DBConstants.TAB_UNIT_NAME + "."
				+ DBConstants.COL_UNIT_ID;

		this.data.getDatabase().execSQL(sqlInsertHistory);

	//	deleteAllShoppinglistProductMappings();
	}
	
	

}
