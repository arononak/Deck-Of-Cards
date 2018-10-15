package com.example.deckofcard.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;

@Data
public class Draw {
    int remaining;
    @SerializedName("deck_id") String deckId;
    boolean success;
    Card[] cards;

    @Getter
    public class Card {
        String code;
        String value;
        String image;
        String suit;
        Images images;

        @Getter
        public class Images {
            String png;
            String svg;
        }
    }
}
