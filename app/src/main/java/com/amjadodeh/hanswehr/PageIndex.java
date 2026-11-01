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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The dictionary is a sequence of scanned pages. {@code hw4_indexes.json} holds one root per page,
 * roughly the root in progress at the page boundary; front-matter pages have an empty entry. The
 * index is hand-made and not perfectly consistent, so lookups deliberately reproduce the original
 * app's behaviour rather than trying to be cleverer than it.
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
                // Stored verbatim (one entry has a leading space) so that lookups behave exactly
                // like the original app, which searched the raw list.
                roots.add(array.getString(i));
            }
        } catch (IOException | JSONException e) {
            throw new IllegalStateException("Could not read bundled page index", e);
        }
        // The original app showed exactly one page per index entry (1315). The scan set has two
        // further images (a publisher's catalogue and the back cover) that it never displayed;
        // keep the same page count so paging behaves identically.
        pageCount = roots.size();
    }

    public int getPageCount() {
        return pageCount;
    }

    /** Asset path of the scan for the given 0-based pager position. */
    public static String assetPathForPosition(int position) {
        return PAGES_DIR + "/" + String.format(Locale.ROOT, "hw4-%04d.png", position + 1);
    }

    /**
     * Returns the pager position for an Arabic root.
     * <p>
     * This is the original app's algorithm, kept byte-for-byte so that every query opens the same
     * page it always did: a binary search over the index (exact match returns that entry, otherwise
     * the insertion point), then one page back. The result is clamped to the page range, which the
     * old ViewPager did implicitly.
     */
    public int positionForRoot(String arabicRoot) {
        int position = lookup(roots.toArray(new String[0]), arabicRoot) - 1;
        return Math.max(0, Math.min(position, pageCount - 1));
    }

    /** The original lookup: {@code Arrays.binarySearch}, with a negative result mapped to the insertion point. */
    static int lookup(String[] roots, String arabicRoot) {
        int index = Arrays.binarySearch(roots, arabicRoot);
        if (index < 0) {
            index = (index + 1) * (-1);
        }
        return index;
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
