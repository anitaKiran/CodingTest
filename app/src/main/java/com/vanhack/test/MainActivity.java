package com.vanhack.test;

import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;

import com.vanhack.test.Adapter.CountriesDataAdapter;
import com.vanhack.test.webservice.Countries;
import com.vanhack.test.webservice.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private CountriesDataAdapter mAdapter;
    //wafer webservice link
    private static String url = "https://restcountries.eu/rest/v2/all";
    ArrayList<Countries> countryList;
    SwipeController swipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryList = new ArrayList();

        setupRecyclerView(); // set recycler view
        new GetCountries().execute(); // get data from webservice and show in list

    }

    /**
     * Async task class to get json data
     */
    private class GetCountries extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler httpHandler = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = httpHandler.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    for(int i = 0; i < jsonArray.length(); i++) {

                        Countries countries = new Countries();
                        JSONObject object = jsonArray.getJSONObject(i);

                        // getting currency from json array
                        JSONArray currencyArray = object.getJSONArray("currencies");
                        JSONObject currencyObj = currencyArray.getJSONObject(0); // only pick the currency at 0 index

                        // get language from json array
                        JSONArray langArray = object.getJSONArray("languages");
                        JSONObject langObj = langArray.getJSONObject(0); // only pick the lang at 0 index

                        // adding data in arraylist
                        countries.setCountryName(object.getString("name")); //country name
                        countries.setCountryCurrency(currencyObj.getString("name")); // set currency
                        countries.setCountryLang(langObj.getString("name")); // set country lang
                        countryList.add(countries);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            mAdapter.notifyDataSetChanged();


        }

    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new CountriesDataAdapter(MainActivity.this, countryList);
        recyclerView.setAdapter(mAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onDeleteClicked(int position) {
                mAdapter.countries.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }
        });


        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c,MainActivity.this);
            }
        });
    }

}
