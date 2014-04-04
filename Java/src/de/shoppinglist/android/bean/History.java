package de.shoppinglist.android.bean;

public class History extends BusinessBean {

	private Shoppinglist shoppinglist;
	
	private ShoppinglistProductMapping itemHistory;
	
	public History() {
		super();
		this.itemHistory = new ShoppinglistProductMapping();
		this.itemHistory.setStore(new Store());
		this.itemHistory.setProduct(new Product());
		this.itemHistory.getProduct().setUnit(new Unit());
	}

	public Shoppinglist getShoppinglist() {
		return shoppinglist;
	}

	public void setShoppinglist(Shoppinglist shoppinglist) {
		this.shoppinglist = shoppinglist;
	}

	public String getStore() {
		return this.itemHistory.getStore().getName();
	}

	public void setStore(String store) {
		this.itemHistory.getStore().setName(store);
	}

	public String getProduct() {
		return this.itemHistory.getProduct().getName();
	}

	public void setProduct(String product) {
		this.itemHistory.getProduct().setName(product);
	}

	public String getUnit() {
		return this.itemHistory.getProduct().getUnit().getName();
	}

	public void setUnit(String unit) {
		this.itemHistory.getProduct().getUnit().setName(unit);
	}

	public String getQuantity() {
		return this.itemHistory.getQuantity();
	}

	public void setQuantity(String quantity) {
		this.itemHistory.setQuantity(quantity);
	}
}
