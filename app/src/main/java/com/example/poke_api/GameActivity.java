package com.example.poke_api;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    Button startButton;
    ImageButton playButton;
    PokemonCard card;
    List<CardsList> cards;
    List<Player> players;
    int deckSize = 10;
    boolean gameStarted = false;
    int turn = 0;
    public static RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initGui();


        requestQueue = Volley.newRequestQueue(this);
        getAllCards(); // skal være efter requestQueue
    }

    private void initGui() {
        startButton = findViewById(R.id.btn_start);
        startButton.setOnClickListener(this);
        playButton = findViewById(R.id.btn_next);
        playButton.setOnClickListener(this);
    }

    private void initPlayer() {
        Player p1 = new Player();
        Player p2 = new Player();

        players = new ArrayList<Player>() {{
            add(p1);
            add(p2);
        }};

        for (Player player : players) {
            player.cards = new ArrayList<>();
            addCardsToPlayer(player);
        }
    }

    private void getAllCards() {
        String url = "https://api.tcgdex.net/v2/en/cards";

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            cards = new Gson().fromJson(response, new TypeToken<List<CardsList>>() {}.getType());
        }, error -> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        );
        requestQueue.add(request);
    }

    private void addCardsToPlayer(Player player) {
        Random rnd = new Random();
        for (int i = 0; i < deckSize; i++) {
            player.cards.add(cards.get(rnd.nextInt(cards.size())));
        }
    }



    @Override
    public void onClick(View view) {
        if (!gameStarted) {
            initPlayer(); // skal kaldes async for ikke at crashe appen
            startButton.setVisibility(View.GONE);
            playButton.setVisibility(View.VISIBLE);
            gameStarted = true;
        }

        if (turn != players.get(0).cards.size()) {
            // p1 card
            getCard(players.get(0).cards.get(turn).id, players.get(0));

            // p2 card
            getCard(players.get(1).cards.get(turn).id, players.get(1));


//            if (players.get(0).card.hp > players.get(1).card.hp){
//                Toast.makeText(this, "Round: " + (turn + 1) + "\nYOU WIN", Toast.LENGTH_LONG).show();
//            }
//            if (p1Card.hp < p2Card.hp){
//                Toast.makeText(this, "Round: " + (turn + 1) + "\nYOU LOSE", Toast.LENGTH_LONG).show();
//            }
//            else{
//                Toast.makeText(this, "Round: " + (turn + 1) + "\nDRAW", Toast.LENGTH_LONG).show();
//            }


            Toast.makeText(this, "Round: " + (turn + 1), Toast.LENGTH_LONG).show();
            turn++;
        } else {
            Toast.makeText(this, "GAME OVER", Toast.LENGTH_LONG).show();
            gameStarted = false;
        }
    }




    private void getCard(String id, Player player) {
        String url = "https://api.tcgdex.net/v2/en/cards/" + id;

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            PokemonCard card = new Gson().fromJson(response, PokemonCard.class);
//            player.cards.add(card);
            player.card = card;
            getImage(player);
        }, error -> Log.d("Volley", error.toString()));
        requestQueue.add(request);
    }

    private void getImage(Player player) {
        if (players.get(0) == player) {
            ImageView cardImage = findViewById(R.id.img_playerCard);
            Picasso.get().load(card.image + "/high.jpg").into(cardImage);
        }
        if (players.get(1) == player) {
            ImageView cardImage = findViewById(R.id.img_opponentCard);
            Picasso.get().load(card.image + "/high.jpg").into(cardImage);
        }
    }
}