package com.example.deckofcard.retrofit;

import com.example.deckofcard.model.Draw;
import com.example.deckofcard.model.Shuffle;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {
    @GET("api/deck/new/shuffle")
    Call<Shuffle> newDeck(@Query("deck_count") int deckCount);

    @GET("api/deck/{deck_id}/draw")
    Call<Draw> getCards(@Path("deck_id") String deckId, @Query("count") int count);

    @GET("api/deck/{deck_id}/shuffle")
    Call<Shuffle> shuffleDeck(@Path("deck_id") String deckId);
}
