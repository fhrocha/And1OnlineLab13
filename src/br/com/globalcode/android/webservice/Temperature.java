package br.com.globalcode.android.webservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Temperature {
	
	private static final String WS_HOST = "webservices.daehosting.com";
	private static final int WS_PORT = 80;
	private static final String WS_PATH = "/services/TemperatureConversions.wso";

	private static final String MESSAGE_NOT_CONVERTED_SAME_UNIT_IS_NOT_ALLOWED = "Not converted, same unit is not allowed.";
	private static final String FAHRENHEIT_TEMPERATURE = "Fahrenheit";
	private static final String CELCIUS_TEMPERATURE = "Celcius";
	
	private Temperature() { 

	}
	
	public static String convertTemperature(String valueFrom, String unitFrom, String unitTo) {
		
		List<NameValuePair> params = null;
		String resultJSON = null;
		String temperature = null;
		
		try {
			
			if( isSameOperation(unitFrom, unitTo) ) {
				return MESSAGE_NOT_CONVERTED_SAME_UNIT_IS_NOT_ALLOWED;
			}
			
			String WSOperation = defineOperationBy(unitFrom, unitTo);

			params = defineParams(unitFrom, valueFrom);
			
			URI uri = URIUtils.createURI("http", WS_HOST, WS_PORT, WS_PATH + WSOperation +"/JSON", null, null);
			HttpPost post = new HttpPost(uri);
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			
			resultJSON = buildJSONFormat(result);
			
			temperature = getTemperatureFrom(resultJSON);
			
		} catch (URISyntaxException e) {
			Log.e("And1OnlineLab13", e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Log.e("And1OnlineLab13", e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e("And1OnlineLab13", e.getMessage());
		} catch (IOException e) {
			Log.e("And1OnlineLab13", e.getMessage());
		}
		
		return temperature;
	}
	
	private static String getTemperatureFrom(String JSONStr) {
		
		String result = null;
		
		try {
			
			JSONObject jsonObj = new JSONObject(JSONStr);
			JSONObject jsonTemperature = jsonObj.getJSONObject("temperature");
			result = jsonTemperature.getString("value");
		
		} catch (JSONException e) {
			Log.e("And1OnlineLab13", e.getMessage());
		}
		
		return result;
	}
	
	private static String buildJSONFormat(String result) {
		
		return "{\"temperature\": { \"value\": "+ result +" }}";
	}
	
	private static boolean isSameOperation(String unitFrom, String unitTo) {
		
		return unitFrom.equalsIgnoreCase(unitTo);
	}

	private static List<NameValuePair> defineParams(String unitFrom, String valueFrom) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		if(unitFrom.equalsIgnoreCase(CELCIUS_TEMPERATURE)) {
			params.add(new BasicNameValuePair("nCelcius", valueFrom));
		} else if (unitFrom.equalsIgnoreCase(FAHRENHEIT_TEMPERATURE)) {
			params.add(new BasicNameValuePair("nFahrenheit", valueFrom));
		}
		
		return params;
	}

	private static String defineOperationBy(String unitFrom, String unitTo) {
		
		String result = "";
		
		if (unitFrom.equalsIgnoreCase(CELCIUS_TEMPERATURE)) {
			result = "/CelciusToFahrenheit";
		} else if(unitFrom.equalsIgnoreCase(FAHRENHEIT_TEMPERATURE)) {
			result = "/FahrenheitToCelcius";
		}
		
		return result;
	}

}
