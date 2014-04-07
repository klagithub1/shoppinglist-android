package de.shoppinglist.android.bean;

public class FavoriteProductMapping extends ListProductMapping 
{

	private Favorite favorite;

	public Favorite getFavorite() 
	{
		return favorite;
	}

	public void setFavorite(Favorite favorite) 
	{
		this.favorite = favorite;
	}

	@Override
	public
	BusinessBean getSpecificBean() {
		
		return getFavorite();
	}

}
