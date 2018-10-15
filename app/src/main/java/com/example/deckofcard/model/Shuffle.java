package com.example.deckofcard.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Shuffle {
    int remaining;
    @SerializedName("deck_id") String deckId;
    boolean success;
    boolean shuffled;
}
