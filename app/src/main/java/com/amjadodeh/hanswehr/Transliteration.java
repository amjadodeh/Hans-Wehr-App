package com.amjadodeh.hanswehr;

/** Converts a "Roman Arabic" (chat alphabet) query into Arabic letters. Arabic input passes through. */
public final class Transliteration {

    private Transliteration() {
    }

    public static String toArabic(String query) {
        // Roots are written without spaces; tolerate "k t b" style input.
        String q = query.replaceAll("\\s+", "");
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
