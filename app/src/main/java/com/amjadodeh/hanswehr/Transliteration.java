package com.amjadodeh.hanswehr;

/** Converts a "Roman Arabic" (chat alphabet) query into Arabic letters. Arabic input passes through. */
public final class Transliteration {

    private Transliteration() {
    }

    /**
     * Converts a query to Arabic. A space, hyphen, apostrophe or dot between letters forces a
     * split, so "s-h-r" is س ه ر while "shr" is ش ر.
     */
    public static String toArabic(String query) {
        StringBuilder out = new StringBuilder();
        for (String chunk : query.trim().split("[\\s\\-'.\u2019\u0640]+")) {
            if (!chunk.isEmpty()) {
                out.append(convertChunk(chunk));
            }
        }
        return out.toString();
    }

    private static String convertChunk(String q) {
        // Normalise hamza / alif variants and alif maqsura to the plain forms used by the index.
        q = q.replaceAll("[إآٱأءﺀﺁﺃﺅﺇﺉ]", "ا");
        q = q.replaceAll("[ﻯى]", "ي");
        // Two-letter combinations first.
        q = q.replace("th", "ث");
        q = q.replace("gh", "غ");
        q = q.replaceAll("[gG]", "غ");
        q = q.replace("kh", "خ");
        q = q.replace("sh", "ش");
        q = q.replace("dh", "ذ");
        // Case-sensitive single letters (emphatic consonants use capitals).
        q = q.replace("d", "د");
        q = q.replace("D", "ض");
        q = q.replace("z", "ز");
        q = q.replace("Z", "ظ");
        q = q.replace("s", "س");
        q = q.replace("S", "ص");
        q = q.replace("t", "ت");
        q = q.replace("T", "ط");
        q = q.replace("h", "ه");
        q = q.replace("H", "ح");
        q = q.replaceAll("[xX]", "خ");
        q = q.replaceAll("[vV]", "ث");
        q = q.replace("$", "ش");
        // Everything else is case-insensitive.
        q = q.replaceAll("[aA]", "ا");
        q = q.replaceAll("[bB]", "ب");
        q = q.replaceAll("[jJ]", "ج");
        q = q.replace("7", "ح");
        q = q.replaceAll("[rR]", "ر");
        q = q.replace("3", "ع");
        q = q.replaceAll("[eE]", "ع");
        q = q.replaceAll("[fF]", "ف");
        q = q.replaceAll("[qQ]", "ق");
        q = q.replaceAll("[kK]", "ك");
        q = q.replaceAll("[lL]", "ل");
        q = q.replaceAll("[mM]", "م");
        q = q.replaceAll("[nN]", "ن");
        q = q.replaceAll("[wW]", "و");
        q = q.replaceAll("[yY]", "ي");
        return q;
    }
}
