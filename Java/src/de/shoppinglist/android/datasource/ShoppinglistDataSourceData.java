package de.shoppinglist.android.datasource;

import android.database.sqlite.SQLiteDatabase;
import de.shoppinglist.android.helper.SQLiteHelper;

public class ShoppinglistDataSourceData {
	private static ShoppinglistDataSourceData instance = null;
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;

	private ShoppinglistDataSourceData() {
	}
	
	public static ShoppinglistDataSourceData getInstance(){
		if(instance == null) instance = new ShoppinglistDataSourceData();
		return instance;
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}

	public SQLiteHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(SQLiteHelper dbHelper) {
		this.dbHelper = dbHelper;
	}
	
	public void isDbLockedByThread() {
		int counter = 0;
		while (((this.database != null)
				&& (this.database.isDbLockedByCurrentThread() || this.database
						.isDbLockedByOtherThreads()) && (counter < 1000))) {
			counter++;
		}
	}
}