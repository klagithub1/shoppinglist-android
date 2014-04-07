package de.shoppinglist.android.bean;

public class FavoriteProductMapping extends BusinessBean {

	private Favorite favorite;

	private Store store;

	private Product product;

	private String quantity;
	
	public FavoriteProductMapping(){super();}
	public FavoriteProductMapping(Favorite favorite, Store store,
			Product product, String quantity) {
		super();
		this.favorite = favorite;
		this.store = store;
		this.product = product;
		this.quantity = quantity;
	}

	public Favorite getFavorite() {
		return favorite;
	}

	public void setFavorite(Favorite favorite) {
		this.favorite = favorite;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
}
