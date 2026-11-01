package com.amjadodeh.hanswehr;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_LAST_PAGE = "last_page";
    private static final String STATE_PAGE = "page";

    private PageIndex pageIndex;
    private ViewPagerFixed viewPager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The toolbar is red in both themes, so the status bar always wants light icons.
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        pageIndex = new PageIndex(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.viewPagerMain);
        ListView drawerList = findViewById(R.id.navList);

        applyWindowInsets(drawerList);
        setupPager(savedInstanceState);
        setupDrawer(drawerList);

        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    /** Edge-to-edge: paint the toolbar under the status bar and keep content clear of system bars. */
    private void applyWindowInsets(final ListView drawerList) {
        final View content = findViewById(R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            toolbar.setPadding(bars.left, bars.top, bars.right, 0);
            content.setPadding(0, 0, 0, bars.bottom);
            drawerList.setPadding(bars.left, bars.top, 0, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupPager(Bundle savedInstanceState) {
        viewPager.setAdapter(new PageAdapter(pageIndex));
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateSubtitle(position);
            }
        });
        int startPage;
        if (savedInstanceState != null) {
            startPage = savedInstanceState.getInt(STATE_PAGE, 0);
        } else {
            startPage = prefs().getInt(PREF_LAST_PAGE, 0);
        }
        showPage(startPage);
    }

    private void setupDrawer(ListView drawerList) {
        String[] titles = getResources().getStringArray(R.array.drawer_titles);
        int[] icons = {
                R.drawable.ic_verb_form_icon,
                R.drawable.ic_roman_letters_icon,
                R.drawable.ic_help_white_24dp,
                R.drawable.ic_info_white_24dp,
                R.drawable.ic_settings_applications_white_24dp,
                R.drawable.ic_announcement_white_24dp,
        };
        drawerList.setAdapter(new DrawerRowAdapter(this, titles, icons));
        drawerList.setOnItemClickListener((parent, view, position, id) -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            onDrawerItemSelected(position);
        });

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (searchView != null) {
                    searchView.clearFocus();
                }
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (searchView != null && !searchView.isIconified()) {
                    searchView.onActionViewCollapsed();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void onDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                showHtmlDialog(R.string.title_verb_forms, R.string.verb_forms);
                break;
            case 1:
                showHtmlDialog(R.string.title_roman_letters, R.string.roman_letters);
                break;
            case 2:
                showHtmlDialog(R.string.title_tutorial, R.string.tutorial);
                break;
            case 3:
                showHtmlDialog(R.string.title_about, R.string.about);
                break;
            case 4:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case 5:
                openUrl(getString(R.string.feedback_url));
                break;
            default:
                break;
        }
    }

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_browser, Toast.LENGTH_SHORT).show();
        }
    }

    private void showHtmlDialog(int titleRes, int bodyRes) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(titleRes)
                .setMessage(HtmlCompat.fromHtml(getString(bodyRes), HtmlCompat.FROM_HTML_MODE_LEGACY))
                .setPositiveButton(R.string.dismiss, null)
                .show();
        TextView message = dialog.findViewById(android.R.id.message);
        if (message != null) {
            message.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null || !Intent.ACTION_SEARCH.equals(intent.getAction())) {
            return;
        }
        String query = intent.getStringExtra(SearchManager.QUERY);
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        String root = Transliteration.toArabic(query);
        showPage(pageIndex.positionForRoot(root));
        new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE)
                .saveRecentQuery(query.trim(), null);
        if (searchView != null) {
            searchView.onActionViewCollapsed();
        }
        viewPager.requestFocus();
    }

    private void showPage(int position) {
        int clamped = Math.max(0, Math.min(position, pageIndex.getPageCount() - 1));
        viewPager.setCurrentItem(clamped, false);
        updateSubtitle(clamped);
    }

    private void updateSubtitle(int position) {
        toolbar.setSubtitle(getString(R.string.page_of, position + 1, pageIndex.getPageCount()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView != null && searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getString(R.string.search_hint));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_PAGE, viewPager.getCurrentItem());
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs().edit().putInt(PREF_LAST_PAGE, viewPager.getCurrentItem()).apply();
    }

    private SharedPreferences prefs() {
        return getSharedPreferences("hanswehr", MODE_PRIVATE);
    }
}
