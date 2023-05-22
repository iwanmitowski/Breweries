package com.example.breweries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.breweries.Models.Brewery;
import com.example.breweries.Models.SQLiteHelper;

public class DetailsActivity extends AppCompatActivity {

    private EditText editName;
    private TextView detailsCity;
    private TextView detailsCountry;

    private Button btnUpdate;
    private Button btnShowOnMap;
    private Button btnDelete;

    private Brewery brewery;

    private SQLiteHelper sqLiteHelper;

    private boolean isFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        editName = findViewById(R.id.editName);
        detailsCity = findViewById(R.id.detailsCity);
        detailsCountry = findViewById(R.id.detailsCountry);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnShowOnMap = findViewById(R.id.btnShowOnMap);
        btnDelete = findViewById(R.id.btnDelete);

        brewery = (Brewery) getIntent().getSerializableExtra("Brewery");

        sqLiteHelper = new SQLiteHelper(getApplicationContext());
        isFavourite = sqLiteHelper.isBreweryExisting(brewery.getId());

        if (isFavourite) {
            btnUpdate.setText("Update");
        } else {
            btnDelete.setVisibility(View.GONE);
            btnUpdate.setText("Add to favourites");
        }

        editName.setText(brewery.getName());
        detailsCity.setText(brewery.getCity());
        detailsCountry.setText(brewery.getCountry());

        setOnclickListeners();
    }

    private void updateBreweryAsync() {
        sqLiteHelper.update(brewery.getId(), editName.getText().toString());

        reloadMainActivity();
    }

    private void addToFavouritesAsync() {
        sqLiteHelper.insert(brewery);
        isFavourite = true;

        reloadMainActivity();
    }

    private void reloadMainActivity() {
        Intent intent = new Intent(
            DetailsActivity.this,
            MainActivity.class
        );

        intent.putExtra("showFavourites", true);
        startActivity(intent);
    }

    private void removeFromFavouritesAsync() {
        sqLiteHelper.delete(brewery.getId());
        isFavourite = false;

        reloadMainActivity();
    }
    private void setOnclickListeners() {
        btnShowOnMap.setOnClickListener(view -> {
            Intent intent = new Intent(
                DetailsActivity.this,
                MapsActivity.class
            );

            intent.putExtra("Name", brewery.getName());
            intent.putExtra("Latitude", brewery.getLatitude());
            intent.putExtra("Longitude", brewery.getLongitude());
            startActivity(intent);
        });

        btnUpdate.setOnClickListener(view -> {
            try {
                Runnable r = () -> {
                    try {
                        if (isFavourite) {
                            updateBreweryAsync();
                        } else {
                            addToFavouritesAsync();
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

        btnDelete.setOnClickListener(view -> {
            try {
                Runnable r = () -> {
                    try {
                        removeFromFavouritesAsync();
                    } catch (final Exception e) {
                        e.printStackTrace();
                        showErrorToast(e);
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