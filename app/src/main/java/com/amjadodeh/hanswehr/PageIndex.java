package com.amjadodeh.hanswehr;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The dictionary is a sequence of scanned pages. {@code hw4_indexes.json} holds, for each page,
 * the root that is current at the top of that page (which may have started on the previous page).
 * Front-matter pages have an empty entry.
 */
public final class PageIndex {

    private static final String INDEX_ASSET = "hw4_indexes.json";
    private static final String PAGES_DIR = "pages";

    private final List<String> roots = new ArrayList<>();
    private final int pageCount;

    public PageIndex(Context context) {
        AssetManager assets = context.getAssets();
        try {
            String json = readAsset(assets, INDEX_ASSET);
            JSONArray array = new JSONObject(json).getJSONArray("hw4");
            for (int i = 0; i < array.length(); i++) {
                roots.add(array.getString(i).trim());
            }
        } catch (IOException | JSONException e) {
            throw new IllegalStateException("Could not read bundled page index", e);
        }
        int count;
        try {
            String[] files = assets.list(PAGES_DIR);
            count = files == null ? 0 : files.length;
        } catch (IOException e) {
            count = 0;
        }
        // Every indexed page must exist; a few trailing pages may have no index entry.
        pageCount = Math.max(count, roots.size());
    }

    public int getPageCount() {
        return pageCount;
    }

    /** Asset path of the scan for the given 0-based pager position. */
    public static String assetPathForPosition(int position) {
        return PAGES_DIR + "/" + String.format(Locale.ROOT, "hw4-%04d.png", position + 1);
    }

    /**
     * Returns the pager position that most likely contains the given Arabic root.
     * Mirrors the original app: find the first page whose top root is >= the query, then step back
     * one page because that root may have begun on the previous page.
     */
    public int positionForRoot(String arabicRoot) {
        if (arabicRoot == null || arabicRoot.isEmpty()) {
            return 0;
        }
        int lowerBound = roots.size();
        for (int i = 0; i < roots.size(); i++) {
            String root = roots.get(i);
            if (!root.isEmpty() && root.compareTo(arabicRoot) >= 0) {
                lowerBound = i;
                break;
            }
        }
        int position = lowerBound - 1;
        return Math.max(0, Math.min(position, pageCount - 1));
    }

    private static String readAsset(AssetManager assets, String name) throws IOException {
        try (InputStream in = assets.open(name); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            String text = new String(out.toByteArray(), StandardCharsets.UTF_8);
            // The asset was saved with a UTF-8 byte-order mark; drop it before parsing.
            if (!text.isEmpty() && text.charAt(0) == 0xFEFF) {
                text = text.substring(1);
            }
            return text;
        }
    }
}
