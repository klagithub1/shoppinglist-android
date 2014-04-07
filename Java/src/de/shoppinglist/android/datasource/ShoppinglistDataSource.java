package de.shoppinglist.android.datasource;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import de.shoppinglist.android.bean.Favorite;
import de.shoppinglist.android.bean.FavoriteProductMapping;
import de.shoppinglist.android.bean.History;
import de.shoppinglist.android.bean.Product;
import de.shoppinglist.android.bean.Shoppinglist;
import de.shoppinglist.android.bean.ShoppinglistProductMapping;
import de.shoppinglist.android.bean.Store;
import de.shoppinglist.android.bean.Object;
import de.shoppinglist.android.constant.ConfigurationConstants;
import de.shoppinglist.android.constant.DBConstants;
import de.shoppinglist.android.constant.GlobalValues;
import de.shoppinglist.android.helper.SQLiteHelper;
import de.shoppinglist.android.helper.TranslateUmlauts;

public class ShoppinglistDataSource {

	private ShoppinglistDataSourceData data = ShoppinglistDataSourceData.getInstance();
	private ProductPersistence productPersistence = new ProductPersistence();
	private StorePersistence storePersistence = new StorePersistence(productPersistence);
	private UnitPersistence unitPersistence = new UnitPersistence();
	private ShoppinglistPersistence shoppinglistPersistence = new ShoppinglistPersistence();
	private FavoritePersistence favoritePersistence = new FavoritePersistence();
	private FavoriteProductMappingPersistence favoriteProductMappingPersistence = 
			new FavoriteProductMappingPersistence(productPersistence, favoritePersistence, storePersistence);
	private ShoppinglistProductMappingPersistence shoppinglistProductMappingPersistence = 
			new ShoppinglistProductMappingPersistence(shoppinglistPersistence, productPersistence, storePersistence);
	private HistoryPersistence historyPersistence = new HistoryPersistence(shoppinglistProductMappingPersistence);
	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public ShoppinglistDataSource(final Context context) {
		this.data.setDbHelper(new SQLiteHelper(context));
	}

	/**
	 * adds the current shoppinglist with all its relations to the table.history
	 */
	public void addAllToHistory() {
		this.isDbLockedByThread();

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

		this.deleteAllShoppinglistProductMappings();
	}

	/**
	 * checks whether there is a favoriteProductMapping (with combination of
	 * given storeId and productId) in this favorite (given favoriteId)
	 * 
	 * @param favoriteId
	 * @param storeId
	 * @param productId
	 * @return favoriteProductMapping returns null when there exists no mapping
	 */
	public FavoriteProductMapping doesFavoriteProductMappingExist(
			final int favoriteId, final int storeId, final int productId) {
		this.isDbLockedByThread();
		return favoriteProductMappingPersistence.doesFavoriteProductMappingExist(favoriteId, storeId, productId);
	}

	/**
	 * checks whether the product is in use in the shoppinglist_product_mapping
	 * or favorite_product_mapping
	 * 
	 * @param productId
	 * @return true when the product is NOT in use and could be deleted, false
	 *         when it is in use
	 */
	public boolean isProductNotInUse(final int productId) {
		this.isDbLockedByThread();

		return productPersistence.isProductNotInUse(productId);
	}

	/**
	 * checks whether there is a shoppinglistProductMapping (with combination of
	 * given storeId and productId)
	 * 
	 * @param storeId
	 * @param productId
	 * @return ShoppinglistProductMapping shoppinglistProductMapping returns
	 *         null when there exists no mapping
	 */
	public ShoppinglistProductMapping doesShoppinglistProductMappingExist(
			final int storeId, final int productId) {
		this.isDbLockedByThread();

		return shoppinglistProductMappingPersistence.doesShoppinglistProductMappingExist(
				storeId, productId);
	}

	/**
	 * checks whether the store is in use in the shoppinglist_product_mapping or
	 * favorite_product_mapping
	 * 
	 * @param storeId
	 * @return true when the store is NOT in use and could be deleted, false
	 *         when it is in use
	 */
	public boolean isStoreNotInUse(final int storeId) {
		this.isDbLockedByThread();

		return storePersistence.isStoreNotInUse(storeId);
	}

	/**
	 * 
	 * checks whether the unit is in use in the table product
	 * 
	 * @param unitId
	 * @return true when the unit is NOT in use and could be deleted, false when
	 *         it is in use
	 */
	public boolean isUnitNotInUse(final int unitId) {
		this.isDbLockedByThread();

		return unitPersistence.isUnitNotInUse(unitId);
	}

	/**
	 * closes the db-connection
	 */
	public void close() {
		// this.isDbLockedByThread();

		if ((this.data.getDatabase() != null)
				&& !this.data.getDatabase().isDbLockedByOtherThreads()
				&& this.data.getDatabase().isOpen()) {
			if (this.data.getDbHelper() != null) {
				this.data.getDbHelper().close();
			}

		}
	}

	/**
	 * creates a new shoppinglist (Table: shoppinglist)
	 */
	public void createNewShoppinglist() {
		this.isDbLockedByThread();

		shoppinglistPersistence.createNewShoppinglist();
	}

	/**
	 * deletes all mappings from shoppinglistProductMapping
	 * 
	 */
	public void deleteAllShoppinglistProductMappings() {
		this.isDbLockedByThread();

		// temporary save the product id to delete, for the check, whether it
		// could be deleted in table: product
		shoppinglistProductMappingPersistence.deleteAllShoppinglistProductMappings();

	}

	/**
	 * deletes a favorite and all the mapping belong to this favorite with given
	 * favoriteId
	 * 
	 * @param favoriteId
	 */
	public void deleteFavoriteAndItsMappings(final int favoriteId) {
		this.isDbLockedByThread();

		favoriteProductMappingPersistence.deleteFavoriteAndItsMappings(favoriteId);

	}

	/**
	 * deletes a favoriteProductMapping with given favoriteProductMappingId
	 * 
	 * @param favoriteProductMappingId
	 */
	public void deleteFavoriteProductMapping(final int favoriteProductMappingId) {
		this.isDbLockedByThread();

		favoriteProductMappingPersistence.deleteFavoriteProductMapping(favoriteProductMappingId);

	}

	/**
	 * deletes the whole history
	 * 
	 */
	public void deleteHistory() {
		this.isDbLockedByThread();

		historyPersistence.delete(null);
	}

	/**
	 * deletes a product with given productId
	 * 
	 * @param productId
	 */
	public void deleteProduct(final int productId) {
		this.isDbLockedByThread();

		productPersistence.delete(productId);
	}

	/**
	 * deletes the products from the given store
	 * 
	 * @param storeId
	 */
	public void deleteProductsFromStoreList(final int storeId) {
		this.isDbLockedByThread();

		storePersistence.deleteProductsFromStoreList(storeId);

	}

	/**
	 * deletes the shoppinglistProductMapping with given id
	 * 
	 * @param shoppinglistProductMappingId
	 */
	public void deleteShoppinglistProductMapping(
			final int shoppinglistProductMappingId) {
		this.isDbLockedByThread();

		shoppinglistProductMappingPersistence.deleteShoppinglistProductMapping(
				shoppinglistProductMappingId);
	}

	/**
	 * deletes a store with given storeId
	 * 
	 * @param storeId
	 */
	public void deleteStore(final int storeId) {
		this.isDbLockedByThread();

		storePersistence.delete(storeId);
	}

	/**
	 * deletes an unit with given unitId
	 * 
	 * @param unitId
	 */
	public void deleteUnit(final int unitId) {
		this.isDbLockedByThread();

		unitPersistence.delete(unitId);
	}

	/**
	 * gets all the favorites (table: favorite)
	 * 
	 * @return List<Favorite> favorites
	 */
	public List<Favorite> getAllFavorites() {
		this.isDbLockedByThread();

		return favoritePersistence.getAllFavorites();
	}

	/**
	 * gets all stores from the DB (Table: Store)
	 * 
	 * @return List<Store> stores
	 */
	public List<Store> getAllStores() {
		this.isDbLockedByThread();

		return storePersistence.getAllStores();
	}

	/**
	 * gets all units from DB (Table: Unit)
	 * 
	 * @return List<Unit> units
	 */
	public List<Object> getAllUnits() {
		this.isDbLockedByThread();

		return unitPersistence.getAll();
	}

	/**
	 * 
	 * gets the product count (checked) for this storeId
	 * 
	 * @param storeId
	 * @return int productCount
	 */
	public int getCheckedProductCountForStore(final int storeId) {
		this.isDbLockedByThread();

		return storePersistence.getCheckedProductCountForStore(storeId);
	}

	/**
	 * gets a favorite with given favoriteName
	 * 
	 * @param favoriteName
	 * @return Favorite favorite
	 */
	public Favorite getFavoriteByName(String favoriteName) {
		this.isDbLockedByThread();
		return favoritePersistence.getFavoriteByName(favoriteName);
	}

	/**
	 * gets the mappings for given favoriteId
	 * 
	 * @param favoriteId
	 * @return List<FavoriteProductMapping> favoriteProductMappings (should be
	 *         only one entry)
	 */
	public List<FavoriteProductMapping> getFavoriteProductMappingsByFavoriteId(
			final int favoriteId) {
		this.isDbLockedByThread();

		return favoriteProductMappingPersistence.getFavoriteProductMappingsByFavoriteId(favoriteId);
	}

	/**
	 * gets the history for a given shoppinglistId(Table.History)
	 * 
	 * @param shoppinglistId
	 * 
	 * @return List<History> historyList
	 */
	public List<History> getHistoryByShoppinglistId(final int shoppinglistId) {
		this.isDbLockedByThread();

		return historyPersistence.getHistoryByShoppinglistId(shoppinglistId);
	}

	/**
	 * gets the history-shoppinglists [distincted] (Table.History)
	 * 
	 * @return List<Shoppinglist> shoppinglists
	 */
	public List<Shoppinglist> getHistoryShoppinglists() {
		this.isDbLockedByThread();

		return historyPersistence.getHistoryShoppinglists();
	}

	/**
	 * gets a product with given productName and unitId
	 * 
	 * @param productName
	 * @param unitId
	 * @return Product product
	 */
	public Product getProductByNameAndUnit(String productName, final int unitId) {
		this.isDbLockedByThread();
		return productPersistence.getProductByNameAndUnit(productName, unitId);
	}

	/**
	 * 
	 * gets the product count (all) for this storeId
	 * 
	 * @param storeId
	 * @return int productCount
	 */
	public int getProductCountForStore(final int storeId) {
		this.isDbLockedByThread();

		return storePersistence.getProductCountForStore(storeId);
	}

	/**
	 * gets the shoppinglistProductMapping (for the given storeId, if the store
	 * is not specified -1 should be given)
	 * 
	 * @param storeId
	 * @return List<ShoppinglistProductMapping>
	 */
	public List<ShoppinglistProductMapping> getProductsOnShoppingList(
			final int storeId) {
		this.isDbLockedByThread();

		return shoppinglistProductMappingPersistence.getProductsOnShoppingList(storeId);
	}

	/**
	 * gets a store with given storeId
	 * 
	 * @param storeId
	 * @return Store store
	 */
	public Store getStoreById(final int storeId) {
		this.isDbLockedByThread();

		return storePersistence.getStoreById(storeId);
	}

	/**
	 * gets a store with given storeName
	 * 
	 * @param storeName
	 * @return Store store
	 */
	public Store getStoreByName(String storeName) {
		this.isDbLockedByThread();
		return storePersistence.getStoreByName(storeName);
	}

	/**
	 * Gets all stores from the DB (Table: Store) for the overview
	 * "for the overview" means that this are only the stores which are in use
	 * 
	 * @return List<Store> stores
	 */
	public List<Store> getStoresForOverview() {
		this.isDbLockedByThread();

		return storePersistence.getStoresForOverview();
	}

	/**
	 * gets an unit with given unitName
	 * 
	 * @param unitName
	 * @return Unit unit
	 */
	public Object getUnitByName(String unitName) {
		this.isDbLockedByThread();
		return unitPersistence.getUnitByName(unitName);
	}

	/**
	 * gets the viewType the user has set up
	 * 
	 * @return short viewType
	 */
	public short getUserConfigurationViewType() {
		this.isDbLockedByThread();

		final String sqlQuery = "SELECT "
				+ DBConstants.COL_USER_CONFIGURATION_VIEW_TYPE + " FROM "
				+ DBConstants.TAB_USER_CONFIGURATION;
		final Cursor cursor = this.data.getDatabase().rawQuery(sqlQuery, null);

		short viewType = ConfigurationConstants.STORE_VIEW;

		while (cursor.moveToNext()) {
			viewType = cursor
					.getShort(cursor
							.getColumnIndex(DBConstants.COL_USER_CONFIGURATION_VIEW_TYPE));
		}
		cursor.close();

		return viewType;
	}

	/**
	 * marks the mapping as checked
	 * 
	 * @param shoppinglistProductMappingId
	 */
	public void checkShoppinglistProductMapping(
			final int shoppinglistProductMappingId) {
		this.isDbLockedByThread();

		shoppinglistProductMappingPersistence.checkShoppinglistProductMapping(shoppinglistProductMappingId);
	}

	/**
	 * marks the mapping as unchecked
	 * 
	 * @param shoppinglistProductMappingId
	 */
	public void uncheckShoppinglistProductMapping(
			final int shoppinglistProductMappingId) {
		this.isDbLockedByThread();
		shoppinglistProductMappingPersistence.uncheckShoppinglistProductMapping(shoppinglistProductMappingId);
	}

	/**
	 * opens the database
	 */
	public void open() {
		try {
			this.data.setDatabase(this.data.getDbHelper().getWritableDatabase());
		} catch (final SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * Saves a favorite with the given name
	 * 
	 * @param name
	 */
	public void saveFavorite(String name) {
		this.isDbLockedByThread();
		favoritePersistence.save(name);
	}

	/**
	 * Saves the mapping for the favorite (all information)
	 * 
	 * @param favoriteId
	 * @param storeId
	 * @param productId
	 * @param quantity
	 */
	public void saveFavoriteProductMapping(final int favoriteId,
			final int storeId, final int productId, final String quantity) {
		this.isDbLockedByThread();
		favoriteProductMappingPersistence.saveFavoriteProductMapping(favoriteId, storeId, productId, quantity);
	}

	/**
	 * Saves a product with the given name and unitId
	 * 
	 * @param name
	 * @param unitId
	 */
	public void saveProduct(String name, final int unitId) {
		this.isDbLockedByThread();
		productPersistence.save(name, unitId);
	}

	/**
	 * Saves the mapping for the overview (all information)
	 * 
	 * @param storeId
	 * @param productId
	 * @param quantity
	 * @param checked
	 */
	public void saveShoppingListProductMapping(final int storeId,
			final int productId, final String quantity, final short checked) {
		this.isDbLockedByThread();
		shoppinglistProductMappingPersistence.saveShoppingListProductMapping(storeId, productId, quantity, checked);
	}

	/**
	 * Saves a store with the given name
	 * 
	 * @param name
	 */
	public void saveStore(String name) {
		this.isDbLockedByThread();
		storePersistence.save(name);
	}

	/**
	 * Saves a unit with the given name
	 * 
	 * @param name
	 */
	public void saveUnit(String name) {
		this.isDbLockedByThread();
		unitPersistence.save(name);
	}

	/**
	 * gets the viewType the user has set up
	 * 
	 * @param viewType
	 * 
	 */
	public void setUserConfiguration(final short viewType) {
		this.isDbLockedByThread();

		final String sqlQuery = "UPDATE " + DBConstants.TAB_USER_CONFIGURATION
				+ " SET " + DBConstants.COL_USER_CONFIGURATION_VIEW_TYPE
				+ " = " + viewType;

		this.data.getDatabase().execSQL(sqlQuery);
	}

	/**
	 * updates a favorite with given favorite
	 * 
	 * @param favorite
	 * 
	 */
	public void updateFavorite(final Favorite favorite) {
		this.isDbLockedByThread();
		favoritePersistence.update(favorite);
	}

	/**
	 * updates a favoriteProductMapping with given id by given values
	 * 
	 * @param favoriteProductMappingId
	 * @param storeId
	 * @param productId
	 * @param quantity
	 */
	public void updateFavoriteProductMapping(FavoriteProductMapping favoriteProductMapping) {
		favoriteProductMappingPersistence.updateFavoriteProductMapping(favoriteProductMapping);
	}
	public void updateFavoriteProductMapping(FavoriteProductMapping favoriteProductMapping, int ProductId){
		this.isDbLockedByThread();
		favoriteProductMappingPersistence.updateFavoriteProductMapping(favoriteProductMapping, ProductId);
	}
	/**
	 * updates a product with given product
	 * 
	 * @param product
	 * 
	 */
	public void updateProduct(final Product product) {
		this.isDbLockedByThread();
		productPersistence.update(product);
	}

	/**
	 * updates a shoppinglistProductMapping with given id by given values
	 * 
	 * @param shoppinglistProductMappingId
	 * @param storeId
	 * @param productId
	 * @param quantity
	 */
	public void updateShoppinglistProductMapping(
			final int shoppinglistProductMappingId, final int storeId,
			final int productId, final String quantity) {
		this.isDbLockedByThread();
		shoppinglistProductMappingPersistence.updateShoppinglistProductMapping(shoppinglistProductMappingId, storeId, productId, quantity);
	}

	/**
	 * updates a store with given store
	 * 
	 * @param store
	 * 
	 * @param Store
	 *            store
	 */
	public void updateStore(final Store store) {
		this.isDbLockedByThread();

		storePersistence.update(store);
	}

	/**
	 * updates an unit with given unit
	 * 
	 * @param unit
	 * 
	 */
	public void updateUnit(final Object unit) {
		this.isDbLockedByThread();
		unitPersistence.update(unit);
	}

	/**
	 * 
	 * <p>
	 * Checks whether the DB is locked by a thread (current or other). To avoid
	 * deadlock-infinite-loop there's a counter.
	 * </p>
	 * 
	 */
	private void isDbLockedByThread() {
		int counter = 0;
		while (((this.data.getDatabase() != null)
				&& (this.data.getDatabase().isDbLockedByCurrentThread() || this.data.getDatabase()
						.isDbLockedByOtherThreads()) && (counter < 1000))) {
			counter++;
		}
	}
}
