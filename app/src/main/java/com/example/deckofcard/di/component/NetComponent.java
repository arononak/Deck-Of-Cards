package com.example.deckofcard.di.component;

import com.example.deckofcard.di.component.module.AppModule;
import com.example.deckofcard.di.component.module.NetModule;
import com.example.deckofcard.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(MainActivity activity);
}
