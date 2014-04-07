package de.shoppinglist.android.model;

import java.util.ArrayList;
import java.util.List;

import de.shoppinglist.android.bean.Shoppinglist;
import de.shoppinglist.android.bean.ShoppinglistProductMapping;
import de.shoppinglist.android.constant.GlobalValues;
import de.shoppinglist.android.datasource.ShoppinglistDataSource;
import android.app.Application;
import android.content.Context;

public class ShoppingListModel{
	
	private static ShoppingListModel instance;
	private static ShoppinglistDataSource datasource;	
	
	private List<ShoppinglistProductMapping> shoppingListItems;
	private Shoppinglist currentShoppingList;
	
	private ShoppingListModel(){
		load();
	}
	
	public static ShoppingListModel getInstance(Context context){
		if (instance == null){
			instance = new ShoppingListModel();	
			datasource = new ShoppinglistDataSource(context);
			datasource.open();
		}
		return instance;
	}
	
	
	public void moveShoppintListToHistory(){
		ArrayList<ShoppinglistProductMapping> history = (ArrayList<ShoppinglistProductMapping>) shoppingListItems;
		datasource.addAllToHistory();
		restartShoppingList();
	}
	
	
	private void restartShoppingList (){
		this.currentShoppingList.finish();
		this.shoppingListItems = new ArrayList<ShoppinglistProductMapping>();
		datasource.deleteAllShoppinglistProductMappings();
		this.currentShoppingList = new Shoppinglist();
		datasource.createNewShoppinglist();
	}
	
	private void load(){
		this.shoppingListItems = new ArrayList<ShoppinglistProductMapping>();
		this.currentShoppingList = new Shoppinglist();
	}

	private void addItemToShoppingList(int storeId, int productId, String quantity){
		boolean itemExist = false;
		int itemId = 0;
		
		for (int index=0; index<shoppingListItems.size(); index++){
			if (shoppingListItems.get(index).getProduct().getId() == productId){
				itemExist = true;
				itemId = shoppingListItems.get(index).getId();
			}
		}
		if (!itemExist)
			datasource.saveShoppingListProductMapping(storeId, productId, quantity, GlobalValues.NO);
		else 
			datasource.updateShoppinglistProductMapping(itemId, storeId, productId, quantity);
	}
	
	
}
