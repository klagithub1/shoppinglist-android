package de.shoppinglist.android.bean;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestHistory {
	private History fixture;
	private final String PRODUCT = "Milk";
	private final String QUANTITY = "2";
	private final String UNIT = "liters";
	private final String STORE = "Walmart";
	
	@Before
	public void setUp() throws Exception {
		fixture = new History();
		fixture.setProduct(PRODUCT);
		fixture.setQuantity(QUANTITY);
		fixture.setUnit(UNIT);
		fixture.setStore(STORE);
	}

	@Test
	public void testGetProduct() {
		assertEquals(PRODUCT, fixture.getProduct());
	}
	
	@Test
	public void testGetQuantity() {
		assertEquals(QUANTITY, fixture.getQuantity());
	}
	
	@Test
	public void testGetUnit(){
		assertEquals(UNIT, fixture.getUnit());
	}
	
	@Test
	public void testGetStore(){
		assertEquals(STORE, fixture.getStore());
	}

}
