package de.shoppinglist.android.datasource;

import de.shoppinglist.android.constant.DBConstants;

public class ShoppinglistPersistence {
	private ShoppinglistDataSourceData data = ShoppinglistDataSourceData.getInstance();

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
	
}
