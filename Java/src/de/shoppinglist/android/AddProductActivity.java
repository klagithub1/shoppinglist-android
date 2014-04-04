package de.shoppinglist.android;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import de.shoppinglist.android.adapter.StoreAdapter;
import de.shoppinglist.android.adapter.UnitAdapter;
import de.shoppinglist.android.bean.Product;
import de.shoppinglist.android.bean.ShoppinglistProductMapping;
import de.shoppinglist.android.bean.Store;
import de.shoppinglist.android.bean.Unit;
import de.shoppinglist.android.constant.GlobalValues;
import de.shoppinglist.android.datasource.ShoppinglistDataSource;

public class AddProductActivity extends AbstractShoppinglistActivity {

	private Button buttonConfirmAddProduct;

	private ShoppinglistDataSource datasource;

	private final List<Integer> editTextIds = new LinkedList<Integer>(Arrays.asList(
			R.id.editTextQuantityAddProduct, R.id.editTextProductNameAutocomplete));

	private AutoCompleteTextView editTextProductName;

	private EditText editTextQuantity;

	private Spinner spinnerStores;

	private Spinner spinnerUnits;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.datasource = super.getDatasource();

		this.setContentView(R.layout.add_or_edit_product);

		// set title to match the activity
		final TextView titleView = (TextView) this.findViewById(R.id.titleEditOrAddProduct);
		titleView.setText(R.string.title_add_new_product);

		final List<Unit> units = this.datasource.getAllUnits();
		final List<Store> stores = this.datasource.getAllStores();

		this.spinnerUnits = (Spinner) this.findViewById(R.id.spinnerUnitAddProduct);
		final ArrayAdapter<Unit> spinnerUnitAdapter = new UnitAdapter(this, units);
		this.spinnerUnits.setAdapter(spinnerUnitAdapter);

		this.spinnerStores = (Spinner) this.findViewById(R.id.spinnerStoreAddProduct);
		final ArrayAdapter<Store> spinnerStoreAdapter = new StoreAdapter(this, stores);
		this.spinnerStores.setAdapter(spinnerStoreAdapter);

		this.editTextProductName = (AutoCompleteTextView) this
				.findViewById(R.id.editTextProductNameAutocomplete);
		this.editTextProductName.addTextChangedListener(super
				.getTextWatcher(R.id.editTextProductNameAutocomplete));
		editTextProductName.setAdapter(new ArrayAdapter<String>(this, R.layout.autocomplete,
				this.datasource.getAllProductNames()));

		this.editTextQuantity = (EditText) this.findViewById(R.id.editTextQuantityAddProduct);
		this.editTextQuantity.addTextChangedListener(super
				.getTextWatcher(R.id.editTextQuantityAddProduct));

		this.buttonConfirmAddProduct = (Button) this.findViewById(R.id.buttonConfirmAddProduct);
		this.buttonConfirmAddProduct.setOnClickListener(new ConfirmAddProductListener());
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			Intent intent = new Intent(this, ShoppinglistActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		default:
			break;
		}
		return false;
	}
	
	class ConfirmAddProductListener implements OnClickListener {
		public void onClick(final View v) {
			if (setErrorOnEmptyEditTexts(AddProductActivity.this.editTextIds)) {
				final Store selectedStore = (Store) spinnerStores.getSelectedItem();
				final Unit selectedUnit = (Unit)spinnerUnits.getSelectedItem();
				final String productName = editTextProductName
						.getText().toString();
				final String quantity = editTextQuantity.getText()
						.toString();

				Product product = datasource.getProductByNameAndUnit(
						productName, selectedUnit.getId());
				if (product == null) {
					datasource.saveProduct(productName, selectedUnit.getId());
					product = datasource.getProductByNameAndUnit(productName, selectedUnit.getId());
				}

				final ShoppinglistProductMapping alreadyExistingMapping = 
						datasource.checkWhetherShoppinglistProductMappingExists
							(selectedStore.getId(), product.getId());
				if (alreadyExistingMapping != null) {
					// JA: update quantity
					final double quantityToUpdate = Double.valueOf(alreadyExistingMapping.getQuantity()) 
													+ Double.valueOf(quantity);
					datasource.updateShoppinglistProductMapping(
							alreadyExistingMapping.getId(), 
							alreadyExistingMapping.getStore().getId(), 
							alreadyExistingMapping.getProduct().getId(),
							String.valueOf(quantityToUpdate));
				} else {
					// NEIN: insert new / save
					datasource.saveShoppingListProductMapping(
							selectedStore.getId(), product.getId(), quantity, GlobalValues.NO);
				}

				finish();
			}
		}
	}
}
