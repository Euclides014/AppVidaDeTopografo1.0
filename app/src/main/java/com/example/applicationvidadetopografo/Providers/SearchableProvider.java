package com.example.applicationvidadetopografo.Providers;

import android.content.SearchRecentSuggestionsProvider;

public class SearchableProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "com.example.applicationvidadetopografo.Providers.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public  SearchableProvider(){
        setupSuggestions(AUTHORITY, MODE);
    }

}
