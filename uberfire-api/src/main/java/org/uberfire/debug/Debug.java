package org.uberfire.debug;

/**
 * Utility methods that help with debugging.
 */
public class Debug {

    /** The code requires this list to have 16 entries */
    private static final String[] CONSONANTS = { "b", "d", "f", "h",
                                                 "j", "k", "l", "m",
                                                 "n", "p", "r", "s",
                                                 "t", "v", "w", "z" };

    /** The code requires this list to have 8 entries */
    private static final String[] VOWELS = { "a", "e", "ee", "i",
                                             "o", "oo", "u", "y" };

    /**
     * Makes a reasonably easy-to-pronounce gibberish word from the given number. On average, the word will have about
     * as many characters as the number would have when printed as a decimal, but (especially for large numbers) it will
     * be easier to remember as a single word.
     *
     * @param num
     *            The number to convert.
     * @return The memorable string, unique to the given number. Never null.
     */
    public static String toMemorableString( long num ) {
        StringBuilder memorable = new StringBuilder();
        for ( ;; ) {
            int chunk = (int) (num & 0xf);
            memorable.append( CONSONANTS[chunk] );
            num >>= 4;
            if ( num == 0 )
                break;

            chunk = (int) (num & 7);
            memorable.append( VOWELS[chunk] );
            num >>= 3;
            if ( num == 0 )
                break;
        }
        return memorable.toString();
    }

    /**
     * Composes a compact, memorable unique string for the given object instance. The name starts with the abbreviated
     * fully-qualified class name (see {@link #abbreviatedName(Class)}), an '@' character, then a gibberish word
     * representing the object's identity hash code (see {@link #toMemorableString(long)}).
     *
     * @param o
     * @return
     */
    public static String objectId( Object o ) {
        if ( o == null ) {
            return null;
        }
        return abbreviatedName( o.getClass() ) + "@" + toMemorableString( System.identityHashCode( o ) );
    }

    /**
     * Returns a compact representation of the fully-qualified name of the given class. The string is built with the following components:
     * <ol>
     *  <li>the first letter of each component of the package name
     *  <li>a dot
     *  <li>the class name. If the class is an inner class, the name is of the form Outer$Inner
     * <ol>
     * <p>
     * For classes in the default package, items 1 and 2 are omitted.
     *
     * @param c the class whose name to abbreviate. Can be null, but will result in a null return value.
     * @return the abbreviated FQCN as described, or null if the input is null.
     */
    public static String abbreviatedName( Class<?> c ) {
        if ( c == null ) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String fqcn = c.getName();
        int lastDot = -1;
        int thisDot = -1;
        while ( (thisDot = fqcn.indexOf( '.', thisDot + 1 ) ) != -1 ) {
            result.append( fqcn.charAt( lastDot + 1 ) );
            lastDot = thisDot;
        }
        if ( lastDot != -1 ) {
            result.append( '.' );
        }
        result.append( fqcn.substring( lastDot + 1 ) );
        return result.toString();
    }

    /**
     * Returns the short name of the given class (no package name). This is the same as java.lang.Class.shortName(),
     * which is not implemented in the GWT version of java.lang.Class.
     *
     * @param c
     *            the class whose name to abbreviate. Can be null, but will result in a null return value.
     * @return the abbreviated FQCN as described, or null if the input is null.
     */
    public static String shortName( Class<?> c ) {
        if ( c == null ) {
            return null;
        }
        return c.getName().substring( c.getName().lastIndexOf( '.' ) + 1 );
    }

}
