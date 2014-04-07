package de.shoppinglist.android.datasource;

public class PersistenceFactory {
	
	private static PersistenceFactory instance;
	
	private PersistenceFactory(){		
	}
	
	public static PersistenceFactory getInstance(){
		if (instance == null){
			instance = new PersistenceFactory();
		}
		return instance;
	}
	
	public IPersistence getShoppingListPersistance(){
		IPersistence persistence = new ShoppinglistPersistence();
		return persistence;
	}

}
