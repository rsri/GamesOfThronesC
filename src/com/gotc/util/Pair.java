package com.gotc.util;

/**
 * Created by srikaram on 08-Nov-16.
 */
public final class Pair {
    public Object writer;
    public DeclarationDictionary dictionary;

    private Pair(Object writer, DeclarationDictionary dictionary) {
        this.writer = writer;
        this.dictionary = dictionary;
    }

    public static Pair create(Object writer, DeclarationDictionary dictionary) {
        return new Pair(writer, dictionary);
    }
}
