package br.com.globalcode.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import br.com.globalcode.android.webservice.Temperature;

public class MainActivity extends Activity {
	
	private EditText editTextTemperature;
	private Spinner spinnerUnitFrom;
	private Spinner spinnerUnitTo;
	private TextView textViewResult;
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		editTextTemperature = (EditText) findViewById(R.id.editTextTemperature);
		spinnerUnitFrom = (Spinner) findViewById(R.id.spinnerUnitFrom);
		spinnerUnitTo = (Spinner) findViewById(R.id.spinnerUnitTo);
		textViewResult = (TextView) findViewById(R.id.textViewResult);
		
		((Button)findViewById(R.id.buttonConvert)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				convertTemperatureAction();
			}
		});
		
	}

	protected void convertTemperatureAction() {
		
		String unitFrom = spinnerUnitFrom.getSelectedItem().toString();
		String unitTo = spinnerUnitTo.getSelectedItem().toString();
		String valueToConvert = editTextTemperature.getText().toString();
		
		String[] params = {unitTo, unitFrom, valueToConvert};
		
		new AsyncTask<String, Void, String>() {

			@Override
			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(MainActivity.this, "Wait!", "Effectuating conversion...");
			}
			
			@Override
			protected String doInBackground(String... params) {
				
				String convertedTemperature = Temperature.convertTemperature(params[2], params[1], params[0]);
				return convertedTemperature;
			}
			
			protected void onPostExecute(String result) {

				progressDialog.dismiss();
				
				if(result != null) {
					textViewResult.setText(result);
				} else {
					showAlertDialog();
				}
			}

			private void showAlertDialog() {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Sorry, Error on service...");
				builder.setMessage("Please, try later.");
				builder.setPositiveButton("Ok", null);
				builder.show();
			};
			
		}.execute(params);
	}
}
