package de.shoppinglist.android.datasource;

import de.shoppinglist.android.bean.BusinessBean;

public interface IPersistence {
	void add(BusinessBean bean);
	void update(BusinessBean bean);
}
