package de.shoppinglist.android.datasource;

import java.util.ArrayList;

public interface Persistence {
	public void save(String name);
	public void delete(Object object);
	public void update(Object object);
}
