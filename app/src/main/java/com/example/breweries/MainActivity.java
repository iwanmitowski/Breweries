package com.example.breweries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.breweries.Models.Brewery;
import com.example.breweries.Models.BreweryAdapter;
import com.example.breweries.Models.HttpRequester;
import com.example.breweries.Models.SQLiteHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private EditText searchText;
    private Button btnSearch;
    private Button btnFavourites;
    private ListView listView;

    private HttpRequester httpRequester;
    private SQLiteHelper sqLiteHelper;
    private boolean showFavourites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpRequester = new HttpRequester();
        sqLiteHelper = new SQLiteHelper(getApplicationContext());

        listView = findViewById(R.id.listView);
        searchText = findViewById(R.id.searchText);
        btnSearch = findViewById(R.id.btnSearch);
        btnFavourites = findViewById(R.id.btnFavourites);

        setOnclickListeners();
        loadBreweriesAsync();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Runnable r = () -> {
                try {
                    boolean showFavouritesAfterUpdatedBrewery = getIntent()
                        .getBooleanExtra("showFavourites", showFavourites);

                    showFavourites = showFavouritesAfterUpdatedBrewery;

                    if (showFavourites) {
                        showFavouriteBreweriesAsync();
                    } else {
                        fillBreweriesInListViewAsync();
                    }
                } catch (final Exception e) {
                    e.printStackTrace();

                    runOnUiThread(() -> showErrorToast(e));
                }
            };

            new Thread(r).start();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorToast(e);
        }
    }

    private void showFavouriteBreweriesAsync() {
        try {
            showFavourites = true;
            btnFavourites.setText("Check global brewery database");

            List<Brewery> breweries = sqLiteHelper.get(null);

            runOnUiThread(() -> {
                loadBreweriesInAdapter(breweries);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchBreweriesAsync() {
        try {
            List<Brewery> breweries = null;
            String searchTextValue = searchText
                    .getText()
                    .toString()
                    .replace(" ", "_");

            if (showFavourites) {
                breweries = sqLiteHelper.get(searchTextValue);
            } else {
                String url = "https://api.openbrewerydb.org/v1/breweries/search?query=";

                url += searchTextValue;

                String results = httpRequester.get(url);

                Brewery[] breweriesArray = new Gson().fromJson(results, Brewery[].class);
                breweries = Arrays.asList(breweriesArray)
                    .stream()
                    .filter(brewery -> brewery.getLatitude() != null && brewery.getLongitude() != null)
                    .collect(Collectors.toList());
            }

            List<Brewery> finalBreweries = breweries;
            runOnUiThread(() -> {
                loadBreweriesInAdapter(finalBreweries);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillBreweriesInListViewAsync() {
        try {
            btnFavourites.setText("Favourite breweries");
            showFavourites = false;

            String results = httpRequester.get(
                "https://api.openbrewerydb.org/v1/breweries"
            );

            Brewery[] breweriesArray = new Gson().fromJson(results, Brewery[].class);
            List<Brewery> breweries = Arrays.asList(breweriesArray)
                .stream()
                .filter(brewery -> brewery.getLatitude() != null && brewery.getLongitude() != null)
                .collect(Collectors.toList());

            runOnUiThread(() -> {
                loadBreweriesInAdapter(breweries);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBreweriesInAdapter(List<Brewery> breweries) {
        BreweryAdapter elementAdapter = new BreweryAdapter(
            getApplicationContext(),
            R.layout.activity_list_view_fragment,
            0,
            breweries);

        listView.clearChoices();
        listView.setAdapter(elementAdapter);
    }

    private void loadBreweriesAsync() {
        try {
            Runnable r = () -> {
                try {
                    boolean showFavouritesAfterUpdatedBrewery = getIntent()
                        .getBooleanExtra("showFavourites", showFavourites);

                    showFavourites = showFavouritesAfterUpdatedBrewery;

                    if (showFavourites) {
                        showFavouriteBreweriesAsync();
                    } else {
                        fillBreweriesInListViewAsync();
                    }
                } catch (final Exception e) {
                    e.printStackTrace();

                    runOnUiThread(() -> showErrorToast(e));
                }
            };

            new Thread(r).start();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorToast(e);
        }
    }

    private void setOnclickListeners() {
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            TextView textViewId = view.findViewById(R.id.textViewId);
            TextView textViewName = view.findViewById(R.id.textViewName);
            TextView textViewCity = view.findViewById(R.id.textViewCity);
            TextView textViewCountry = view.findViewById(R.id.textViewCountry);
            TextView textViewLat = view.findViewById(R.id.textViewLat);
            TextView textViewLong = view.findViewById(R.id.textViewLong);

            Brewery selectedBrewery = new Brewery(
                textViewId.getText().toString(),
                textViewName.getText().toString(),
                textViewCity.getText().toString(),
                textViewCountry.getText().toString(),
                Float.parseFloat(textViewLat.getText().toString()),
                Float.parseFloat(textViewLong.getText().toString()));

            Intent intent = new Intent(
                MainActivity.this,
                DetailsActivity.class
            );

            intent.putExtra("Brewery", selectedBrewery);
            startActivity(intent);
        });

        btnSearch.setOnClickListener(view -> {
            try {
                Runnable r = () -> {
                    try {
                        searchBreweriesAsync();
                    } catch (final Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> showErrorToast(e));
                    }
                };

                new Thread(r).start();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorToast(e);
            }
        });

        btnFavourites.setOnClickListener(view -> {
            try {
                Runnable r = () -> {
                    try {
                        searchText.setText("");

                        if (showFavourites) {
                            fillBreweriesInListViewAsync();
                        } else {
                            showFavouriteBreweriesAsync();
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> showErrorToast(e));
                    }
                };

                new Thread(r).start();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorToast(e);
            }
        });
    }

    private void showErrorToast(Exception e) {
        Toast
            .makeText(
                getApplicationContext(),
                e.getMessage(),
                Toast.LENGTH_LONG)
            .show();
    }
}