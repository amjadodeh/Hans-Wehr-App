package com.amjadodeh.hanswehr;

import android.content.SearchRecentSuggestionsProvider;

/** Stores recent searches on-device for the search box's suggestion dropdown. */
public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.amjadodeh.hanswehr.SuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
