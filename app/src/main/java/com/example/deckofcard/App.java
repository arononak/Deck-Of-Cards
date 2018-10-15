package com.example.deckofcard;

import android.app.Application;

import com.example.deckofcard.di.component.DaggerNetComponent;
import com.example.deckofcard.di.component.NetComponent;
import com.example.deckofcard.di.component.module.AppModule;
import com.example.deckofcard.di.component.module.NetModule;

public class App extends Application {

    private NetComponent mNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule("https://deckofcardsapi.com/"))
                .build();
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }
}
