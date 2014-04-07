package de.shoppinglist.android.datasource;

import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;
import de.shoppinglist.android.bean.Unit;
import de.shoppinglist.android.constant.DBConstants;
import de.shoppinglist.android.helper.TranslateUmlauts;

public class UnitPersistence{
	private ShoppinglistDataSourceData data = ShoppinglistDataSourceData.getInstance();

	public boolean isUnitNotInUse(final int unitId) {

		boolean isNotInProductInUse = true;
		final String sqlQuery = "SELECT " + DBConstants.COL_PRODUCT_UNIT_ID
				+ " FROM " + DBConstants.TAB_PRODUCT_NAME + " WHERE "
				+ DBConstants.TAB_PRODUCT_NAME + "."
				+ DBConstants.COL_PRODUCT_UNIT_ID + " = " + unitId;

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);
		if (cursor.getCount() != 0) {
			isNotInProductInUse = false;
		}
		cursor.close();

		return isNotInProductInUse;
	}
	
	public void delete(final int unitId) {
		final String sqlQuery = "DELETE FROM " + DBConstants.TAB_UNIT_NAME
				+ " WHERE " + DBConstants.COL_UNIT_ID + " = " + unitId;

		this.data.getDatabase().execSQL(sqlQuery);
	}
	
	public List<Unit> getAll() {
		final String sqlQuery = "SELECT " + DBConstants.COL_UNIT_ID + ", "
				+ DBConstants.COL_UNIT_NAME + " FROM "
				+ DBConstants.TAB_UNIT_NAME + " ORDER BY "
				+ DBConstants.COL_UNIT_NAME;

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		final List<Unit> units = new LinkedList<Unit>();

		while (cursor.moveToNext()) {
			final Unit unit = new Unit();
			unit.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_UNIT_ID)));
			unit.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor.getColumnIndex(DBConstants.COL_UNIT_NAME))));
			units.add(unit);
		}
		cursor.close();

		return units;
	}
	
	public Unit getUnitByName(String unitName) {
		unitName = TranslateUmlauts.translateFromGermanUmlauts(unitName);

		final String sqlQuery = "SELECT " + DBConstants.COL_UNIT_ID + ", "
				+ DBConstants.COL_UNIT_NAME + " FROM "
				+ DBConstants.TAB_UNIT_NAME + " WHERE UPPER("
				+ DBConstants.TAB_UNIT_NAME + "." + DBConstants.COL_UNIT_NAME
				+ ") = '" + unitName.toUpperCase().trim() + "'";

		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		Unit unit = null;

		if (cursor.getCount() == 1) {
			cursor.moveToNext();
			unit = new Unit();
			unit.setId(cursor.getInt(cursor
					.getColumnIndex(DBConstants.COL_UNIT_ID)));
			unit.setName(TranslateUmlauts.translateIntoGermanUmlauts(cursor
					.getString(cursor.getColumnIndex(DBConstants.COL_UNIT_NAME))));
		}
		cursor.close();

		return unit;
	}
	
	public void save(String name) {
		name = TranslateUmlauts.translateFromGermanUmlauts(name);

		final String sqlQuery = "INSERT INTO " + DBConstants.TAB_UNIT_NAME
				+ " (" + DBConstants.COL_UNIT_NAME + ") VALUES ('"
				+ name.trim() + "')";

		this.data.getDatabase().execSQL(sqlQuery);
	}
	
	public void update(Unit unit) {
		final String sqlQuery = "UPDATE " + DBConstants.TAB_UNIT_NAME + " SET "
				+ DBConstants.COL_UNIT_NAME + " = '" + unit.getName().trim()
				+ "' WHERE " + DBConstants.TAB_UNIT_NAME + "."
				+ DBConstants.COL_UNIT_ID + " = " + unit.getId();

		this.data.getDatabase().execSQL(sqlQuery);
	}

	public void delete(java.lang.Object object) {
		// TODO Auto-generated method stub
		
	}

	public void update(java.lang.Object object) {
		// TODO Auto-generated method stub
		
	}
}
