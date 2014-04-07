package de.shoppinglist.android.controller;

import java.util.ArrayList;
import java.util.List;

import de.shoppinglist.android.bean.Shoppinglist;
import de.shoppinglist.android.bean.ShoppinglistProductMapping;
import de.shoppinglist.android.datasource.ShoppinglistDataSource;
import android.app.Application;

public class ShoppingListController extends Application{
	private ShoppinglistDataSource datasource;	
	
	public void onCreate(){
		super.onCreate();
		this.datasource = new ShoppinglistDataSource(this);
		this.datasource.open();
		
	}
	
	public void moveShoppintListToHistory(){
		datasource.addAllToHistory();
		datasource.deleteAllShoppinglistProductMappings();
		datasource.createNewShoppinglist();
	}
	
}
