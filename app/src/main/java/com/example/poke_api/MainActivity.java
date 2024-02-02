package com.example.poke_api;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText searchField;
    LinearLayout searchBar;
    ImageButton searchButton;
    Button startButton;
    Spinner searchResultSpinner;
    public static RequestQueue requestQueue;
    List<CardsList> cards;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGui();

        requestQueue = Volley.newRequestQueue(this);
        searchButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
        getAllCards();
    }


    private void initGui() {
        searchField = findViewById(R.id.et_search);
        searchButton = findViewById(R.id.btn_search);
        searchResultSpinner = findViewById(R.id.sp_searchresult);
        searchBar = findViewById(R.id.ll_searchbar);
        startButton = findViewById(R.id.btn_start);
    }

    @Override
    public void onClick(View view) {
        if (view == startButton){
            Intent intent = new Intent(view.getContext(), GameActivity.class); // creates GameActivity.java
            startActivity(intent); // starts GameActivity.java
        }
        else {
            if (cards == null) return;

            List<CardsList> searchList = new ArrayList<>();
            for (CardsList pc : cards) {
                if (pc.name != null && pc.name.toLowerCase().contains(searchField.getText().toString().toLowerCase())) {
                    searchList.add(pc);
                }
            }
            createSpinner(searchList);
        }
    }

    private void createSpinner(List<CardsList> pcList) {
        List<String> pcNames = pcList.stream().map(PokemonList -> PokemonList.name + ":" + PokemonList.id)
                .collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, pcNames);
        searchResultSpinner.setAdapter(adapter);
        searchResultSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean firstTime = true;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                if (firstTime) {
                    firstTime = false;
                    return;
                }

                String id = pcNames.get(index).split(":")[1]; // splits the string where ":" is, and selects the second part (the id)
                Intent intent = new Intent(view.getContext(), CardActivity.class); // creates CardActivity.java
                intent.putExtra("id", id); // adds on "id" to the intent
                startActivity(intent); // starts CardActivity.java and sends "id" with it
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getAllCards() {
        String url = "https://api.tcgdex.net/v2/en/cards";

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            cards = new Gson().fromJson(response, new TypeToken<List<CardsList>>() {}.getType());
            Toast.makeText(this, "Cards: " + cards.size(), Toast.LENGTH_LONG).show();
            searchBar.setVisibility(View.VISIBLE);
            Log.d("Cards", String.valueOf(cards.size()));
        }, error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        );
        requestQueue.add(request);
    }
}