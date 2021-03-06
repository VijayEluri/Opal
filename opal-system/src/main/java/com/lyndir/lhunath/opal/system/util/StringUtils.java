/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.opal.system.util;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.Verify;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NonNls;


/**
 * <h2>{@link StringUtils}<br> <sub>Some utility methods for working with {@link String}s.</sub></h2>
 * <p>
 * <p> <i>Jan 12, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class StringUtils {

    private static final Pattern TLD              = Pattern.compile( "^.*?([^\\.]+\\.[^\\.]+)$" );
    private static final Pattern TRAILING_SLASHES = Pattern.compile( "/+$" );
    private static final Pattern NON_FINAL_PATH   = Pattern.compile( "^.*/" );

    /**
     * Convenience shortcut for {@link MessageFormat#format(String, Object...)}.  Great for static imports.
     */
    public static String str(final String messageFormat, final Object... args) {

        return MessageFormat.format( messageFormat, args );
    }

    /**
     * Convenience shortcut for {@link String#format(String, Object...)}.  Great for static imports.
     */
    public static String strf(@NonNls final String format, final Object... args) {

        return String.format( format, args );
    }

    /**
     * Concatenate several strings into one.
     *
     * @param delimitor The delimitor to use to separate the strings.
     * @param elements  The strings that should be concatenated, in order form left to right.
     *
     * @return A long string containing all the given strings delimited by the delimitor.
     */
    public static String concat(@NonNls final String delimitor, final String... elements) {

        StringBuilder concatenation = new StringBuilder();
        for (final String element : elements)
            concatenation.append( element ).append( delimitor );

        return concatenation.substring( 0, concatenation.length() - delimitor.length() );
    }

    public static boolean isEmpty(@Nullable final String data) {

        return data == null || data.isEmpty();
    }

    /**
     * Trim all {@code trim} strings off of the {@code source} string, operating only on the left side.
     *
     * @param source The source object that needs to be converted to a string and trimmed.
     * @param trim   The object that needs to be converted to a string and is what will be trimmed off.
     *
     * @return The result of the trimming.
     */
    @Nullable
    public static String ltrim(@Nullable final String source, @NonNls @Nullable final String trim) {

        if (source == null || trim == null)
            return source == null? null: source;

        StringBuilder trimmed = new StringBuilder( source );
        while (trimmed.indexOf( trim ) == 0)
            trimmed.delete( 0, trim.length() );

        return trimmed.toString();
    }

    /**
     * Trim all {@code trim} strings off of the {@code source} string, operating only on the left side.
     *
     * @param source The source object that needs to be converted to a string and trimmed.
     * @param trim   The object that needs to be converted to a string and is what will be trimmed off.
     *
     * @return The result of the trimming.
     */
    @Nullable
    public static String rtrim(@Nullable final String source, @NonNls @Nullable final String trim) {

        if (source == null || trim == null)
            return source == null? null: source;

        StringBuilder trimmed = new StringBuilder( source );
        while (trimmed.indexOf( trim, trimmed.length() - trim.length() ) == trimmed.length() - trim.length())
            trimmed.delete( trimmed.length() - trim.length(), trimmed.length() );

        return trimmed.toString();
    }

    /**
     * Trim all {@code trim} strings off of the {@code source} string, operating on both sides.
     *
     * @param source The source object that needs to be converted to a string and trimmed.
     * @param trim   The object that needs to be converted to a string and is what will be trimmed off.
     *
     * @return The result of the trimming.
     */
    @Nullable
    public static String trim(final String source, @NonNls final String trim) {

        return rtrim( ltrim( source, trim ), trim );
    }

    /**
     * @param home The {@link URL} that needs to be converted to a short string version.
     *
     * @return A concise representation of the URL showing only the root domain and final part of the path.
     */
    public static String shortUrl(final URL home) {

        String shortHome = TLD.matcher( home.getHost() ).replaceFirst( "$1" );
        String path = NON_FINAL_PATH.matcher( TRAILING_SLASHES.matcher( home.getPath() ).replaceFirst( "" ) ).replaceFirst( "" );

        return String.format( "%s:%s", shortHome, path );
    }

    /**
     * Perform an expansion operation on the given {@code source} string by expanding all curly-braced words with a certain
     * {@code keyPrefix} into an expansion value determined by the {@code keyToExpansion} function.
     * <p>
     * <p>If the source string is {@code I am a ${karma} sentence.}, the keyPrefix is {@code $} and the keyToExpansion function
     * returns {@code bad} when its input is the string {@code karma}, the result of this method will be: {@code I am a bad sentence.}</p>
     * <p>
     * You may append a default value after the expansion key, which will be used when the function returns {@code null} for the key.
     * If no default value is provided in the expansion and the functions returns {@code null} for the key, an {@code
     * java.lang.NullPointerException} will be thrown.  You can specify an optional expansion by providing an empty default value.
     * </p>
     * <p>
     * For instance, if the keyToExpansion function returns {@code null} for the string {@code karma} in the previous example, an exception
     * will be thrown.  If the source string is changed to {@code I am a ${karma:good} sentence.}, then this case will yield the
     * following result: {@code I am a good sentence.}
     * </p>
     *
     * @param source         The string to search for expansion words and to expand into.
     * @param keyPrefix      The string that should come just before the opening curly-brace.
     * @param keyToExpansion The function that determines the value to expand an expansion word into.  The word within the curly braces
     *                       will be given to the function.  The expansion value will be expected as return value.
     *
     * @return An expanded version of the source string.
     */
    public static String expand(final String source, @NonNls final String keyPrefix, final Function<String, String> keyToExpansion) {

        Map<Integer, Integer> indexToEnds = Maps.newTreeMap();
        Map<Integer, String> indexToExpansions = Maps.newTreeMap();

        Pattern keyPattern = Pattern.compile( String.format( "%s\\{([^\\}:]*)(?::([^\\}]*))?\\}", Pattern.quote( keyPrefix ) ) );
        Matcher matcher = keyPattern.matcher( source );
        while (matcher.find()) {
            int index = matcher.start();
            String key = matcher.group( 1 );
            String fallback = matcher.group( 2 );
            String value = keyToExpansion.apply( key );

            indexToEnds.put( index, matcher.end() );
            indexToExpansions.put( index, Verify.verifyNotNull( ifNotNullElse( value, fallback ), //
                                                                "No value for required expansion key: %s", key ) );
        }

        SortedSet<Integer> reverseIndexes = Sets.newTreeSet( Collections.reverseOrder() );
        reverseIndexes.addAll( indexToExpansions.keySet() );

        StringBuilder filtered = new StringBuilder( source );
        for (final Integer index : reverseIndexes)
            filtered.replace( index, indexToEnds.get( index ), indexToExpansions.get( index ) );

        return filtered.toString();
    }

    public static String indent(final int indents) {

        return indent( indents, 4 );
    }

    public static String indent(final int indents, final int size) {

        StringBuilder indent = new StringBuilder(indents * size);
        for (int i = 0; i < indents * size; i++)
            indent.append( ' ' );

        return indent.toString();
    }

    public static String indent(final int indents, final int size, final String message) {

        return indent( indents, size ) + message;
    }

    public static String repeat(final String s, final int repeat) {
        StringBuilder repeatedString = new StringBuilder( repeat * s.length() );
        for (int i = 0; i < repeat; ++i)
            repeatedString.append( s );

        return repeatedString.toString();
    }
}
