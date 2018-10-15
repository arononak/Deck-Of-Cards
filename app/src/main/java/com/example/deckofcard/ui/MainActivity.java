package com.example.deckofcard.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.deckofcard.App;
import com.example.deckofcard.R;
import com.example.deckofcard.model.Draw;
import com.example.deckofcard.model.Shuffle;
import com.example.deckofcard.retrofit.RestApi;
import com.example.deckofcard.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final int CARD_COUNT = 5;

    @Inject
    Retrofit retrofit;

    RecyclerView recyclerView;
    TextView textMessage;
    TextView textError;
    NumberPicker numberPicker;

    RestApi restApi;

    String deckId;
    int remainingCards = 0;

    View container;
    View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((App) getApplication()).getNetComponent().inject(this);

        restApi = retrofit.create(RestApi.class);

        progressBar = findViewById(R.id.progress_bar);
        container = findViewById(R.id.container);
        recyclerView = findViewById(R.id.recycler_view);
        textMessage = findViewById(R.id.text_message);
        textError = findViewById(R.id.text_error);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        numberPicker = findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5);
        numberPicker.setWrapSelectorWheel(true);

        showProgress(false);

        findViewById(R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);

                if (numberPicker.isEnabled()) {
                    numberPicker.setEnabled(false);

                }

                if (Utils.isNetworkAvailable(MainActivity.this)) {
                    loadCardsFromApiAsync(numberPicker.getValue(), CARD_COUNT);
                } else {
                    showError();
                }
            }
        });
    }

    private void showError() {
        textMessage.setVisibility(View.GONE);
        textError.setVisibility(View.VISIBLE);
        textError.setText("Nie udało się połączyć z API");
        showProgress(false);
    }

    private void showMessage(String message) {
        textError.setVisibility(View.GONE);
        textMessage.setVisibility(View.VISIBLE);
        textMessage.setText(message);
        showProgress(false);
    }

    private void showProgress(boolean isShow) {
        container.setVisibility(isShow ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void showCards(List<Draw.Card> cards) {
        showProgress(false);

        List<String> imageUrls = new ArrayList<>();
        for (Draw.Card card : cards)
            imageUrls.add(card.getImage());

        /* Dex problem with android, java8 and lombok
        List<String> imageUrls = cards.stream()
                .map(Draw.Card::getImage)
                .collect(Collectors.toList());
        */

        recyclerView.setAdapter(new CardAdapter(imageUrls));

        String cardsArrangement = determineCardsArrangement(cards);

        showMessage("Remaining: " + remainingCards + "\n" + cardsArrangement);
    }

    private String determineCardsArrangement(List<Draw.Card> cards) {
        String theSameColor = isTheSameColor(cards);
        String theSameFigure = isTheSameFigure(cards);

        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(theSameColor)) {
            builder.append("\nColor: ").append(theSameColor);
        } else if (!TextUtils.isEmpty(theSameFigure)) {
            builder.append("\nTriple repetition: ").append(theSameFigure);
        } else if (areThreeCardJackOrQueenOrKing(cards)) {
            builder.append("\nThree figures (\"JACK\", \"QUEEN\", \"KING\")");
        } else if (isGrowingOrDecreasing(cards)) {
            builder.append("\nFigures are arranged in a staircase");
        }

        return builder.toString();
    }

    private String isTheSameColor(List<Draw.Card> cards) {
        final int MINIMAL_COUNT = 3;
        final String[] colorNames = {"CLUBS", "DIAMONDS", "HEARTS", "SPADES"};
        int[] colorNameCounts = new int[colorNames.length];
        for (Draw.Card card : cards) {
            for (int i = 0; i < colorNames.length; i++) {
                if (card.getSuit().equalsIgnoreCase(colorNames[i])) {
                    colorNameCounts[i]++;
                }
            }
        }
        for (int i = 0; i < colorNames.length; i++) {
            if (colorNameCounts[i] >= MINIMAL_COUNT) {
                return colorNames[i];
            }
        }
        return "";
    }

    private String isTheSameFigure(List<Draw.Card> cards) {
        final int MINIMAL_COUNT = 3;
        final String[] figureNames = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "JACK", "QUEEN", "KING", "ACE"};
        int[] figureNameCounts = new int[figureNames.length];
        for (Draw.Card card : cards) {
            for (int i = 0; i < figureNames.length; i++) {
                if (card.getValue().equalsIgnoreCase(figureNames[i])) {
                    figureNameCounts[i]++;
                }
            }
        }
        for (int i = 0; i < figureNames.length; i++) {
            if (figureNameCounts[i] >= MINIMAL_COUNT) {
                return figureNames[i];
            }
        }
        return "";
    }

    private boolean areThreeCardJackOrQueenOrKing(List<Draw.Card> cards) {
        int counter = 0;
        final List<String> targetValues = new ArrayList<>(Arrays.asList("JACK", "QUEEN", "KING"));
        for (Draw.Card card : cards) {
            if (targetValues.contains(card.getValue())) {
                counter++;
            }
        }
        return counter >= 3;
    }

    private boolean isGrowingOrDecreasing(List<Draw.Card> cards) {
        List<Integer> values = new ArrayList<>();
        for (Draw.Card card : cards) {
            values.add(getValueByCardValue(card.getValue()));
        }

        for (int i = 0; i < values.size() - 2; i++) {
            List<Integer> threeValues = values.subList(i, i + 3);
            if (isGrowing(threeValues) || isDecreasing(threeValues)) {
                return true;
            }
        }

        return false;
    }

    private boolean isGrowing(List<Integer> numbers) {
        for (int i = 1; i < numbers.size(); i++) {
            if (numbers.get(i - 1) + 1 != numbers.get(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDecreasing(List<Integer> numbers) {
        for (int i = 1; i < numbers.size(); i++) {
            if (numbers.get(i - 1) - 1 != numbers.get(i)) {
                return false;
            }
        }
        return true;
    }

    private int getValueByCardValue(String cardValue) {
        switch (cardValue) {
            case "ACE": return 1;
            case "2": return 2;
            case "3": return 3;
            case "4": return 4;
            case "5": return 5;
            case "6": return 6;
            case "7": return 7;
            case "8": return 8;
            case "9": return 9;
            case "10": return 10;
            case "JACK": return 11;
            case "QUEEN": return 12;
            case "KING": return 13;
        }
        return 0;
    }

    private void loadCardsFromApiAsync(final int deckCount, final int cardCount) {
        if (TextUtils.isEmpty(deckId)) {
            restApi.newDeck(deckCount).enqueue(new Callback<Shuffle>() {
                @Override
                public void onResponse(Call<Shuffle> call, Response<Shuffle> response) {
                    if (response.isSuccessful()) {
                        Shuffle shuffle = response.body();
                        if (shuffle != null) {
                            if (shuffle.isSuccess()) {
                                MainActivity.this.remainingCards = shuffle.getRemaining();
                                MainActivity.this.deckId = shuffle.getDeckId();
                                loadCardsFromApiAsync(deckCount, cardCount);
                                return;
                            }
                        }
                    }
                    showError();
                }

                @Override
                public void onFailure(Call<Shuffle> call, Throwable throwable) {
                    showError();
                }
            });
        } else {
            if (remainingCards < CARD_COUNT) {
                restApi.shuffleDeck(deckId).enqueue(new Callback<Shuffle>() {
                    @Override
                    public void onResponse(Call<Shuffle> call, Response<Shuffle> response) {
                        if (response.isSuccessful()) {
                            Shuffle shuffle = response.body();
                            if (shuffle != null) {
                                if (shuffle.isSuccess()) {
                                    MainActivity.this.remainingCards = shuffle.getRemaining();
                                    MainActivity.this.deckId = shuffle.getDeckId();
                                    loadCardsFromApiAsync(deckCount, cardCount);
                                    return;
                                }
                            }
                        }
                        showError();
                    }

                    @Override
                    public void onFailure(Call<Shuffle> call, Throwable t) {
                        showError();
                    }
                });
            } else {
                MainActivity.this.remainingCards -= cardCount;
                restApi.getCards(deckId, cardCount).enqueue(new Callback<Draw>() {
                    @Override
                    public void onResponse(Call<Draw> call, Response<Draw> response) {
                        if (response.isSuccessful()) {
                            Draw draw = response.body();
                            if (draw != null) {
                                if (draw.isSuccess()) {
                                    if (!(MainActivity.this.remainingCards == draw.getRemaining()
                                            || MainActivity.this.deckId.equals(draw.getDeckId()))) {
                                        throw new AssertionError();
                                    }
                                    showCards(Arrays.asList(draw.getCards()));
                                    return;
                                }
                            }
                        }
                        showError();
                    }

                    @Override
                    public void onFailure(Call<Draw> call, Throwable t) {
                        showError();
                    }
                });
            }
        }

    }
}
