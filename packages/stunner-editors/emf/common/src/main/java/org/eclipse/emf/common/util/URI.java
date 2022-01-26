/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A representation of a Uniform Resource Identifier (URI), as specified by
 * <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>, with certain
 * enhancements.  A <code>URI</code> instance can be created by specifying
 * values for its components, or by providing a single URI string, which is
 * parsed into its components.  Static factory methods whose names begin
 * with "create" are used for both forms of object creation.  No public or
 * protected constructors are provided; this class can not be subclassed.
 *
 * <p>Like <code>String</code>, <code>URI</code> is an immutable class;
 * a <code>URI</code> instance offers several by-value methods that return a
 * new <code>URI</code> object based on its current state.  Most useful,
 * a relative <code>URI</code> can be {@link #resolve(URI) resolve}d against
 * a base absolute <code>URI</code> -- the latter typically identifies the
 * document in which the former appears.  The inverse to this is {@link
 * #deresolve(URI) deresolve}, which answers the question, "what relative
 * URI will resolve, against the given base, to this absolute URI?"
 *
 * <p>In the <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC</a>, much
 * attention is focused on a hierarchical naming system used widely to
 * locate resources via common protocols such as HTTP, FTP, and Gopher, and
 * to identify files on a local file system.  Accordingly, most of this
 * class's functionality is for handling such URIs, which can be identified
 * via {@link #isHierarchical isHierarchical}.
 *
 * <p><a name="device_explanation">
 * The primary enhancement beyond the RFC description is an optional
 * device component.  Instead of treating the device as just another segment
 * in the path, it can be stored as a separate component (almost a
 * sub-authority), with the root below it.  For example, resolving
 * <code>/bar</code> against <code>file:///c:/foo</code> would result in
 * <code>file:///c:/bar</code> being returned.  Also, you cannot take
 * the parent of a device, so resolving <code>..</code> against
 * <code>file:///c:/</code> would not yield <code>file:///</code>, as you
 * might expect.  This feature is useful when working with file-scheme
 * URIs, as devices do not typically occur in protocol-based ones.  A
 * device-enabled <code>URI</code> is created by parsing a string with
 * {@link #createURI(String) createURI}; if the first segment of the path
 * ends with the <code>:</code> character, it is stored (including the colon)
 * as the device, instead.  Alternately, either the {@link
 * #createHierarchicalURI(String, String, String, String, String) no-path}
 * or the {@link #createHierarchicalURI(String, String, String, String[],
 * String, String) absolute-path} form of <code>createHierarchicalURI()</code>
 * can be used, in which a non-null <code>device</code> parameter can be
 * specified.
 *
 * <p><a name="archive_explanation"> 
 * The other enhancement provides support for the almost-hierarchical
 * form used for files within archives, such as the JAR scheme, defined
 * for the Java Platform in the documentation for {@link
 * java.net.JarURLConnection}. By default, this support is enabled for
 * absolute URIs with scheme equal to "jar", "zip", or "archive" (ignoring case), and
 * is implemented by a hierarchical URI, whose authority includes the
 * entire URI of the archive, up to and including the <code>!</code>
 * character.  The URI of the archive must have no fragment.  The whole
 * archive URI must have no device and an absolute path.  Special handling
 * is supported for {@link #createURI(String) creating}, {@link
 * #validArchiveAuthority validating}, {@link #devicePath getting the path}
 * from, and {@link #toString() displaying} archive URIs. In all other
 * operations, including {@link #resolve(URI) resolving} and {@link
 * #deresolve(URI) deresolving}, they are handled like any ordinary URI.
 * The schemes that identify archive URIs can be changed from their default
 * by setting the <code>org.eclipse.emf.common.util.URI.archiveSchemes</code>
 * system property. Multiple schemes should be space separated, and the test
 * of whether a URI's scheme matches is always case-insensitive.
 *
 * <p>This implementation does not impose all of the restrictions on
 * character validity that are specified in the RFC.  Static methods whose
 * names begin with "valid" are used to test whether a given string is valid
 * value for the various URI components.  Presently, these tests place no
 * restrictions beyond what would have been required in order for {@link
 * #createURI(String) createURI} to have parsed them correctly from a single
 * URI string.  If necessary in the future, these tests may be made more
 * strict, to better conform to the RFC.
 * 
 * <p>Another group of static methods, whose names begin with "encode", use
 * percent escaping to encode any characters that are not permitted in the
 * various URI components. Another static method is provided to {@link
 * #decode decode} encoded strings.  An escaped character is represented as
 * a percent symbol (<code>%</code>), followed by two hex digits that specify
 * the character code.  These encoding methods are more strict than the
 * validation methods described above.  They ensure validity according to the
 * RFC, with one exception: non-ASCII characters.
 *
 * <p>The RFC allows only characters that can be mapped to 7-bit US-ASCII
 * representations.  Non-ASCII, single-byte characters can be used only via
 * percent escaping, as described above.  This implementation uses Java's
 * Unicode <code>char</code> and <code>String</code> representations, and
 * makes no attempt to encode characters 0xA0 and above.  Characters in the
 * range 0x80-0x9F are still escaped.  In this respect, EMF's notion of a URI
 * is actually more like an IRI (Internationalized Resource Identifier), for
 * which an RFC is now in <href="http://www.w3.org/International/iri-edit/draft-duerst-iri-09.txt">draft
 * form</a>.
 *
 * <p>Finally, note the difference between a <code>null</code> parameter to
 * the static factory methods and an empty string.  The former signifies the
 * absence of a given URI component, while the latter simply makes the
 * component blank.  This can have a significant effect when resolving.  For
 * example, consider the following two URIs: <code>/bar</code> (with no
 * authority) and <code>///bar</code> (with a blank authority).  Imagine
 * resolving them against a base with an authority, such as
 * <code>http://www.eclipse.org/</code>.  The former case will yield
 * <code>http://www.eclipse.org/bar</code>, as the base authority will be
 * preserved.  In the latter case, the empty authority will override the
 * base authority, resulting in <code>http:///bar</code>!
 */
public final class URI
{
  // Common to all URI types.
  private final int hashCode;
  private static final int HIERARICHICAL_FLAG = 0x0100;
  private final String scheme;  // null -> relative URI reference
  private final String authority;
  private final String fragment;
  private URI cachedTrimFragment;
  private String cachedToString;
  //private final boolean iri;
  //private URI cachedASCIIURI;

  // Applicable only to a hierarchical URI.
  private final String device;
  private static final int ABSOLUTE_PATH_FLAG = 0x0010;
  private final String[] segments; // empty last segment -> trailing separator
  private final String query;

  // A cache of URIs, keyed by the strings from which they were created.
  // The fragment of any URI is removed before caching it here, to minimize
  // the size of the cache in the usual case where most URIs only differ by
  // the fragment.
  private static final URICache uriCache = new URICache();
    
  private static class URICache extends HashMap<String,URI>
  {
    private static final long serialVersionUID = 1L;

    public synchronized URI get(String key)
    {
      return super.get(key);
    }
         
    @Override
    public synchronized URI put(String key, URI value)
    {
      return super.put(key, value);
    }
  }

  // The lower-cased schemes that will be used to identify archive URIs.
  private static final Set<String> archiveSchemes;

  // Identifies a file-type absolute URI.
  private static final String SCHEME_FILE = "file";
  private static final String SCHEME_JAR = "jar";
  private static final String SCHEME_ZIP = "zip";
  private static final String SCHEME_ARCHIVE = "archive";
  private static final String SCHEME_PLATFORM = "platform";

  // Special segment values interpreted at resolve and resolve time.
  private static final String SEGMENT_EMPTY = "";
  private static final String SEGMENT_SELF = ".";
  private static final String SEGMENT_PARENT = "..";
  private static final String[] NO_SEGMENTS = new String[0];

  // Separators for parsing a URI string.
  private static final char SCHEME_SEPARATOR = ':';
  private static final String AUTHORITY_SEPARATOR = "//";
  private static final char DEVICE_IDENTIFIER = ':';
  private static final char SEGMENT_SEPARATOR = '/';
  private static final char QUERY_SEPARATOR = '?';
  private static final char FRAGMENT_SEPARATOR = '#';
  private static final char USER_INFO_SEPARATOR = '@';
  private static final char PORT_SEPARATOR = ':';
  private static final char FILE_EXTENSION_SEPARATOR = '.';
  private static final char ARCHIVE_IDENTIFIER = '!';
  private static final String ARCHIVE_SEPARATOR = "!/";

  // Characters to use in escaping.
  private static final char ESCAPE = '%';
  private static final char[] HEX_DIGITS = {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  // Some character classes, as defined in RFC 2396's BNF for URI.
  // These are 128-bit bitmasks, stored as two longs, where the Nth bit is set
  // iff the ASCII character with value N is included in the set.  These are
  // created with the highBitmask() and lowBitmask() methods defined below,
  // and a character is tested against them using matches().
  //
  private static final long ALPHA_HI = highBitmask('a', 'z') | highBitmask('A', 'Z');
  private static final long ALPHA_LO = lowBitmask('a', 'z')  | lowBitmask('A', 'Z');
  private static final long DIGIT_HI = highBitmask('0', '9');
  private static final long DIGIT_LO = lowBitmask('0', '9');
  private static final long ALPHANUM_HI = ALPHA_HI | DIGIT_HI;
  private static final long ALPHANUM_LO = ALPHA_LO | DIGIT_LO;
  private static final long HEX_HI = DIGIT_HI | highBitmask('A', 'F') | highBitmask('a', 'f');
  private static final long HEX_LO = DIGIT_LO | lowBitmask('A', 'F')  | lowBitmask('a', 'f');
  private static final long UNRESERVED_HI = ALPHANUM_HI | highBitmask("-_.!~*'()"); 
  private static final long UNRESERVED_LO = ALPHANUM_LO | lowBitmask("-_.!~*'()");
  private static final long RESERVED_HI = highBitmask(";/?:@&=+$,");
  private static final long RESERVED_LO = lowBitmask(";/?:@&=+$,");
  private static final long URIC_HI = RESERVED_HI | UNRESERVED_HI;  // | ucschar | escaped
  private static final long URIC_LO = RESERVED_LO | UNRESERVED_LO;

  // Additional useful character classes, including characters valid in certain
  // URI components and separators used in parsing them out of a string. 
  //
  private static final long SEGMENT_CHAR_HI = UNRESERVED_HI | highBitmask(";:@&=+$,");  // | ucschar | escaped
  private static final long SEGMENT_CHAR_LO = UNRESERVED_LO | lowBitmask(";:@&=+$,");
  private static final long PATH_CHAR_HI = SEGMENT_CHAR_HI | highBitmask('/');  // | ucschar | escaped
  private static final long PATH_CHAR_LO = SEGMENT_CHAR_LO | lowBitmask('/');
//  private static final long SCHEME_CHAR_HI = ALPHANUM_HI | highBitmask("+-.");
//  private static final long SCHEME_CHAR_LO = ALPHANUM_LO | lowBitmask("+-.");
  private static final long MAJOR_SEPARATOR_HI = highBitmask(":/?#");
  private static final long MAJOR_SEPARATOR_LO = lowBitmask(":/?#");
  private static final long SEGMENT_END_HI = highBitmask("/?#");
  private static final long SEGMENT_END_LO = lowBitmask("/?#");

  // The intent of this was to switch over to encoding platform resource URIs
  // by default, but allow people to use a system property to avoid this.
  // However, that caused problems for people and we had to go back to not
  // encoding and introduce yet another factory method that explicitly enables
  // encoding.
  //
  private static final boolean ENCODE_PLATFORM_RESOURCE_URIS = true;

  // Static initializer for archiveSchemes.
  static
  {
    Set<String> set = new HashSet<String>();
    set.add(SCHEME_JAR);
    set.add(SCHEME_ZIP);
    set.add(SCHEME_ARCHIVE);
    archiveSchemes = Collections.unmodifiableSet(set);
  }

  // Returns the lower half bitmask for the given ASCII character.
  private static long lowBitmask(char c)
  {
    return c < 64 ? 1L << c : 0L;
  }

  // Returns the upper half bitmask for the given ACSII character.
  private static long highBitmask(char c)
  {
    return c >= 64 && c < 128 ? 1L << (c - 64) : 0L;
  }

  // Returns the lower half bitmask for all ASCII characters between the two
  // given characters, inclusive.
  private static long lowBitmask(char from, char to)
  {
    long result = 0L;
    if (from < 64 && from <= to)
    {
      to = to < 64 ? to : 63;
      for (char c = from; c <= to; c++)
      {
        result |= (1L << c);
      }
    }
    return result;
  }

  // Returns the upper half bitmask for all AsCII characters between the two
  // given characters, inclusive.
  private static long highBitmask(char from, char to)
  {
    return to < 64 ? 0 : lowBitmask((char)(from < 64 ? 0 : from - 64), (char)(to - 64));
  }

  // Returns the lower half bitmask for all the ASCII characters in the given
  // string.
  private static long lowBitmask(String chars)
  {
    long result = 0L;
    for (int i = 0, len = chars.length(); i < len; i++)
    {
      char c = chars.charAt(i);
      if (c < 64) result |= (1L << c);
    }
    return result;
  }

  // Returns the upper half bitmask for all the ASCII characters in the given
  // string.
  private static long highBitmask(String chars)
  {
    long result = 0L;
    for (int i = 0, len = chars.length(); i < len; i++)
    {
      char c = chars.charAt(i);
      if (c >= 64 && c < 128) result |= (1L << (c - 64));
    }
    return result;
  }

  // Returns whether the given character is in the set specified by the given
  // bitmask.
  private static boolean matches(char c, long highBitmask, long lowBitmask)
  {
    if (c >= 128) return false;
    return c < 64 ?
      ((1L << c) & lowBitmask) != 0 :
      ((1L << (c - 64)) & highBitmask) != 0;
  }

  // Debugging method: converts the given long to a string of binary digits.
/*
  private static String toBits(long l)
  {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < 64; i++)
    {
      boolean b = (l & 1L) != 0;
      result.insert(0, b ? '1' : '0');
      l >>= 1;
    }
    return result.toString();
  }
*/

  /**
   * Static factory method for a generic, non-hierarchical URI.  There is no
   * concept of a relative non-hierarchical URI; such an object cannot be
   * created.
   *
   * @exception java.lang.IllegalArgumentException if <code>scheme</code> is
   * null, if <code>scheme</code> is an <a href="#archive_explanation">archive
   * URI</a> scheme, or if <code>scheme</code>, <code>opaquePart</code>, or
   * <code>fragment</code> is not valid according to {@link #validScheme
   * validScheme}, {@link #validOpaquePart validOpaquePart}, or {@link
   * #validFragment validFragment}, respectively.
   */
  public static URI createGenericURI(String scheme, String opaquePart,
                                     String fragment)
  {
    if (scheme == null)
    {
      throw new IllegalArgumentException("relative non-hierarchical URI");
    }

    if (isArchiveScheme(scheme))
    {
      throw new IllegalArgumentException("non-hierarchical archive URI");
    }

    validateURI(false, scheme, opaquePart, null, false, NO_SEGMENTS, null, fragment);
    return new URI(false, scheme, opaquePart, null, false, NO_SEGMENTS, null, fragment);
  }

  /**
   * Static factory method for a hierarchical URI with no path.  The
   * URI will be relative if <code>scheme</code> is non-null, and absolute
   * otherwise.  An absolute URI with no path requires a non-null
   * <code>authority</code> and/or <code>device</code>.
   *
   * @exception java.lang.IllegalArgumentException if <code>scheme</code> is
   * non-null while <code>authority</code> and <code>device</code> are null,
   * if <code>scheme</code> is an <a href="#archive_explanation">archive
   * URI</a> scheme, or if <code>scheme</code>, <code>authority</code>,
   * <code>device</code>, <code>query</code>, or <code>fragment</code> is not
   * valid according to {@link #validScheme validSheme}, {@link
   * #validAuthority validAuthority}, {@link #validDevice validDevice},
   * {@link #validQuery validQuery}, or {@link #validFragment validFragment},
   * respectively.
   */
  public static URI createHierarchicalURI(String scheme, String authority,
                                          String device, String query,
                                          String fragment)
  {
    if (scheme != null && authority == null && device == null)
    {
      throw new IllegalArgumentException(
        "absolute hierarchical URI without authority, device, path");
    }

    if (isArchiveScheme(scheme))
    {
      throw new IllegalArgumentException("archive URI with no path");
    }

    validateURI(true, scheme, authority, device, false, NO_SEGMENTS, query, fragment);
    return new URI(true, scheme, authority, device, false, NO_SEGMENTS, query, fragment);
  }

  /**
   * Static factory method for a hierarchical URI with absolute path.
   * The URI will be relative if <code>scheme</code> is non-null, and
   * absolute otherwise. 
   *
   * @param segments an array of non-null strings, each representing one
   * segment of the path.  As an absolute path, it is automatically
   * preceded by a <code>/</code> separator.  If desired, a trailing
   * separator should be represented by an empty-string segment as the last
   * element of the array. 
   *
   * @exception java.lang.IllegalArgumentException if <code>scheme</code> is
   * an <a href="#archive_explanation">archive URI</a> scheme and 
   * <code>device</code> is non-null, or if <code>scheme</code>,
   * <code>authority</code>, <code>device</code>, <code>segments</code>,
   * <code>query</code>, or <code>fragment</code> is not valid according to
   * {@link #validScheme validScheme}, {@link #validAuthority validAuthority}
   * or {@link #validArchiveAuthority validArchiveAuthority}, {@link
   * #validDevice validDevice}, {@link #validSegments validSegments}, {@link
   * #validQuery validQuery}, or {@link #validFragment validFragment}, as
   * appropriate.
   */
  public static URI createHierarchicalURI(String scheme, String authority,
                                          String device, String[] segments,
                                          String query, String fragment)
  {
    if (isArchiveScheme(scheme) && device != null)
    {
      throw new IllegalArgumentException("archive URI with device");
    }

    segments = fix(segments);
    validateURI(true, scheme, authority, device, true, segments, query, fragment);
    return new URI(true, scheme, authority, device, true, segments, query, fragment);
  }

  /**
   * Static factory method for a relative hierarchical URI with relative
   * path.
   *
   * @param segments an array of non-null strings, each representing one
   * segment of the path.  A trailing separator is represented by an
   * empty-string segment at the end of the array.
   *
   * @exception java.lang.IllegalArgumentException if <code>segments</code>,
   * <code>query</code>, or <code>fragment</code> is not valid according to 
   * {@link #validSegments validSegments}, {@link #validQuery validQuery}, or
   * {@link #validFragment validFragment}, respectively.
   */
  public static URI createHierarchicalURI(String[] segments, String query,
                                          String fragment)
  {
    segments = fix(segments);
    validateURI(true, null, null, null, false, segments, query, fragment);
    return new URI(true, null, null, null, false, segments, query, fragment);
  }

  // Converts null to length-zero array, and clones array to ensure
  // immutability.
  private static String[] fix(String[] segments)
  {
    if (segments == null)
    {
      return NO_SEGMENTS;
    }
    else
    {
      String[] result = new String[segments.length];
      System.arraycopy(segments, 0, result, 0, segments.length);
      return result;
    }
  }
  
  /**
   * Static factory method based on parsing a URI string, with 
   * <a href="#device_explanation">explicit device support</a> and handling
   * for <a href="#archive_explanation">archive URIs</a> enabled. The
   * specified string is parsed as described in <a
   * href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>, and an
   * appropriate <code>URI</code> is created and returned.  Note that
   * validity testing is not as strict as in the RFC; essentially, only
   * separator characters are considered.  This method also does not perform
   * encoding of invalid characters, so it should only be used when the URI
   * string is known to have already been encoded, so as to avoid double 
   * encoding.
   *
   * @exception java.lang.IllegalArgumentException if any component parsed
   * from <code>uri</code> is not valid according to {@link #validScheme
   * validScheme}, {@link #validOpaquePart validOpaquePart}, {@link
   * #validAuthority validAuthority}, {@link #validArchiveAuthority
   * validArchiveAuthority}, {@link #validDevice validDevice}, {@link
   * #validSegments validSegments}, {@link #validQuery validQuery}, or {@link
   * #validFragment validFragment}, as appropriate.
   */
  public static URI createURI(String uri)
  {
    return createURIWithCache(uri); 
  }

  /**
   * Static factory method that encodes and parses the given URI string.
   * Appropriate encoding is performed for each component of the URI.
   * If more than one <code>#</code> is in the string, the last one is
   * assumed to be the fragment's separator, and any others are encoded.
   * This method is the simplest way to safely parse an arbitrary URI string.
   *  
   * @param ignoreEscaped <code>true</code> to leave <code>%</code> characters
   * unescaped if they already begin a valid three-character escape sequence;
   * <code>false</code> to encode all <code>%</code> characters.  This
   * capability is provided to allow partially encoded URIs to be "fixed",
   * while avoiding adding double encoding; however, it is usual just to
   * specify <code>false</code> to perform ordinary encoding.
   *
   * @exception java.lang.IllegalArgumentException if any component parsed
   * from <code>uri</code> is not valid according to {@link #validScheme
   * validScheme}, {@link #validOpaquePart validOpaquePart}, {@link
   * #validAuthority validAuthority}, {@link #validArchiveAuthority
   * validArchiveAuthority}, {@link #validDevice validDevice}, {@link
   * #validSegments validSegments}, {@link #validQuery validQuery}, or {@link
   * #validFragment validFragment}, as appropriate.
   */
  public static URI createURI(String uri, boolean ignoreEscaped)
  {
    return createURIWithCache(encodeURI(uri, ignoreEscaped, FRAGMENT_LAST_SEPARATOR));
  }

  /**
   * When specified as the last argument to {@link #createURI(String, boolean, int)
   * createURI}, indicates that there is no fragment, so any <code>#</code> characters
   * should be encoded.
   * @see #createURI(String, boolean, int)
   */
  public static final int FRAGMENT_NONE = 0;

  /**
   * When specified as the last argument to {@link #createURI(String, boolean, int)
   * createURI}, indicates that the first <code>#</code> character should be taken as
   * the fragment separator, and any others should be encoded.
   * @see #createURI(String, boolean, int)
   */
  public static final int FRAGMENT_FIRST_SEPARATOR = 1;

  /**
   * When specified as the last argument to {@link #createURI(String, boolean, int)
   * createURI}, indicates that the last <code>#</code> character should be taken as
   * the fragment separator, and any others should be encoded.
   * @see #createURI(String, boolean, int)
   */
  public static final int FRAGMENT_LAST_SEPARATOR = 2; 

  /**
   * Static factory method that encodes and parses the given URI string.
   * Appropriate encoding is performed for each component of the URI.
   * Control is provided over which, if any, <code>#</code> should be 
   * taken as the fragment separator and which should be encoded.
   * This method is the preferred way to safely parse an arbitrary URI string
   * that is known to contain <code>#</code> characters in the fragment or to
   * have no fragment at all.
   * 
   * @param ignoreEscaped <code>true</code> to leave <code>%</code> characters
   * unescaped if they already begin a valid three-character escape sequence;
   * <code>false</code> to encode all <code>%</code> characters.  This
   * capability is provided to allow partially encoded URIs to be "fixed",
   * while avoiding adding double encoding; however, it is usual just to
   * specify <code>false</code> to perform ordinary encoding.
   * 
   * @param fragmentLocationStyle one of {@link #FRAGMENT_NONE},
   * {@link #FRAGMENT_FIRST_SEPARATOR}, or {@link #FRAGMENT_LAST_SEPARATOR},
   * indicating which, if any, of the <code>#</code> characters should be
   * considered the fragment separator. Any others will be encoded.
   *
   * @exception java.lang.IllegalArgumentException if any component parsed
   * from <code>uri</code> is not valid according to {@link #validScheme
   * validScheme}, {@link #validOpaquePart validOpaquePart}, {@link
   * #validAuthority validAuthority}, {@link #validArchiveAuthority
   * validArchiveAuthority}, {@link #validDevice validDevice}, {@link
   * #validSegments validSegments}, {@link #validQuery validQuery}, or {@link
   * #validFragment validFragment}, as appropriate.
   */
  public static URI createURI(String uri, boolean ignoreEscaped, int fragmentLocationStyle)
  {
    return createURIWithCache(encodeURI(uri, ignoreEscaped, fragmentLocationStyle));
  }

  /**
   * Static factory method based on parsing a URI string, with 
   * <a href="#device_explanation">explicit device support</a> enabled.  
   * Note that validity testing is not a strict as in the RFC; essentially,
   * only separator characters are considered.  So, for example, non-Latin
   * alphabet characters appearing in the scheme would not be considered an
   * error.
   *
   * @exception java.lang.IllegalArgumentException if any component parsed
   * from <code>uri</code> is not valid according to {@link #validScheme
   * validScheme}, {@link #validOpaquePart validOpaquePart}, {@link
   * #validAuthority validAuthority}, {@link #validArchiveAuthority
   * validArchiveAuthority}, {@link #validDevice validDevice}, {@link
   * #validSegments validSegments}, {@link #validQuery validQuery}, or {@link
   * #validFragment validFragment}, as appropriate.
   *
   * @deprecated Use {@link #createURI(String) createURI}, which now has explicit
   * device support enabled. The two methods now operate identically.
   */
  @Deprecated
  public static URI createDeviceURI(String uri)
  {
    return createURIWithCache(uri);
  }

  // Uses a cache to speed up creation of a URI from a string.  The cache
  // is consulted to see if the URI, less any fragment, has already been
  // created.  If needed, the fragment is re-appended to the cached URI,
  // which is considerably more efficient than creating the whole URI from
  // scratch.  If the URI wasn't found in the cache, it is created using
  // parseIntoURI() and then cached.  This method should always be used
  // by string-parsing factory methods, instead of parseIntoURI() directly.
  /**
   * This method was included in the public API by mistake.
   * 
   * @deprecated Please use {@link #createURI(String) createURI} instead.
   */
  @Deprecated
  public static URI createURIWithCache(String uri)
  {
    int i = uri.indexOf(FRAGMENT_SEPARATOR);
    String base = i == -1 ? uri : uri.substring(0, i);
    String fragment = i == -1 ? null : uri.substring(i + 1);

    URI result = uriCache.get(base);

    if (result == null)
    {
      result = parseIntoURI(base);
      uriCache.put(base, result);
      if (fragment != null)
      {
        result = result.appendFragment(fragment);
      }
    }
    else if (fragment != null)
    {
      result = result.appendFragment(new String(fragment));
    }
    return result;
  }

  // String-parsing implementation.
  private static URI parseIntoURI(String uri)
  {
    boolean hierarchical = true;
    String scheme = null;
    String authority = null;
    String device = null;
    boolean absolutePath = false;
    String[] segments = NO_SEGMENTS;
    String query = null;
    String fragment = null;

    int i = 0;
    int j = find(uri, i, MAJOR_SEPARATOR_HI, MAJOR_SEPARATOR_LO);

    if (j < uri.length() && uri.charAt(j) == SCHEME_SEPARATOR)
    {
      scheme = uri.substring(i, j);
      i = j + 1;
    }

    boolean archiveScheme = isArchiveScheme(scheme);
    if (archiveScheme)
    {
      j = uri.lastIndexOf(ARCHIVE_SEPARATOR);
      if (j == -1)
      {
        throw new IllegalArgumentException("no archive separator");
      }
      hierarchical = true;
      authority = uri.substring(i, ++j);
      i = j;
    }
    else if (uri.startsWith(AUTHORITY_SEPARATOR, i))
    {
      i += AUTHORITY_SEPARATOR.length();
      j = find(uri, i, SEGMENT_END_HI, SEGMENT_END_LO);
      authority = uri.substring(i, j);
      i = j;
    }
    else if (scheme != null &&
             (i == uri.length() || uri.charAt(i) != SEGMENT_SEPARATOR))
    {
      hierarchical = false;
      j = uri.indexOf(FRAGMENT_SEPARATOR, i);
      if (j == -1) j = uri.length();
      authority = uri.substring(i, j);
      i = j;
    }

    if (!archiveScheme && i < uri.length() && uri.charAt(i) == SEGMENT_SEPARATOR)
    {
      j = find(uri, i + 1, SEGMENT_END_HI, SEGMENT_END_LO);
      String s = uri.substring(i + 1, j);
      
      if (s.length() > 0 && s.charAt(s.length() - 1) == DEVICE_IDENTIFIER)
      {
        device = s;
        i = j;
      }
    }

    if (i < uri.length() && uri.charAt(i) == SEGMENT_SEPARATOR)
    {
      i++;
      absolutePath = true;
    }

    if (segmentsRemain(uri, i))
    {
      List<String> segmentList = new ArrayList<String>();

      while (segmentsRemain(uri, i))
      {
        j = find(uri, i, SEGMENT_END_HI, SEGMENT_END_LO);
        segmentList.add(uri.substring(i, j));
        i = j;

        if (i < uri.length() && uri.charAt(i) == SEGMENT_SEPARATOR)
        {
          if (!segmentsRemain(uri, ++i)) segmentList.add(SEGMENT_EMPTY);
        }
      }
      segments = new String[segmentList.size()];
      segmentList.toArray(segments);
    }

    if (i < uri.length() && uri.charAt(i) == QUERY_SEPARATOR)
    {
      j = uri.indexOf(FRAGMENT_SEPARATOR, ++i);
      if (j == -1) j = uri.length();
      query = uri.substring(i, j);
      i = j;
    }

    if (i < uri.length()) // && uri.charAt(i) == FRAGMENT_SEPARATOR (implied)
    {
      fragment = uri.substring(++i);
    }

    validateURI(hierarchical, scheme, authority, device, absolutePath, segments, query, fragment);
    return new URI(hierarchical, scheme, authority, device, absolutePath, segments, query, fragment);
  }

  // Checks whether the string contains any more segments after the one that
  // starts at position i.
  private static boolean segmentsRemain(String uri, int i)
  {
    return i < uri.length() && uri.charAt(i) != QUERY_SEPARATOR &&
      uri.charAt(i) != FRAGMENT_SEPARATOR;
  }

  // Finds the next occurrence of one of the characters in the set represented
  // by the given bitmask in the given string, beginning at index i. The index
  // of the first found character, or s.length() if there is none, is
  // returned.  Before searching, i is limited to the range [0, s.length()].
  //
  private static int find(String s, int i, long highBitmask, long lowBitmask)
  {
    int len = s.length();
    if (i >= len) return len;

    for (i = i > 0 ? i : 0; i < len; i++)
    {
      if (matches(s.charAt(i), highBitmask, lowBitmask)) break;
    }
    return i;
  }

  /**
   * Static factory method based on parsing a {@link java.io.File} path
   * string.  The <code>pathName</code> is converted into an appropriate
   * form, as follows: platform specific path separators are converted to
   * <code>/<code>; the path is encoded; and a "file" scheme and, if missing,
   * a leading <code>/</code>, are added to an absolute path.  The result
   * is then parsed using {@link #createURI(String) createURI}.
   *
   * <p>The encoding step escapes all spaces, <code>#</code> characters, and
   * other characters disallowed in URIs, as well as <code>?</code>, which
   * would delimit a path from a query.  Decoding is automatically performed
   * by {@link #toFileString toFileString}, and can be applied to the values
   * returned by other accessors by via the static {@link #decode(String)
   * decode} method.
   *
   * <p>A relative path with a specified device (something like
   * <code>C:myfile.txt</code>) cannot be expressed as a valid URI.
   * 
   * @exception java.lang.IllegalArgumentException if <code>pathName</code>
   * specifies a device and a relative path, or if any component of the path
   * is not valid according to {@link #validAuthority validAuthority}, {@link
   * #validDevice validDevice}, or {@link #validSegments validSegments},
   * {@link #validQuery validQuery}, or {@link #validFragment validFragment}.
   */
  public static URI createFileURI(String pathName)
  {
    String uri = pathName.replace('\\', SEGMENT_SEPARATOR);
    uri = encode(uri, PATH_CHAR_HI, PATH_CHAR_LO, false);
    URI result = createURI((uri.charAt(0) == SEGMENT_SEPARATOR ? "file:" : "file:/") + uri);
    return result;
  }

  /**
   * Static factory method based on parsing a workspace-relative path string.
   *
   * <p>The <code>pathName</code> must be of the form:
   * <pre>
   *   /project-name/path</pre>
   *
   * <p>Platform-specific path separators will be converted to slashes.
   * If not included, the leading path separator will be added.  The
   * result will be of this form, which is parsed using {@link #createURI(String)
   * createURI}:
   * <pre>
   *   platform:/resource/project-name/path</pre>
   *
   * <p>This scheme supports relocatable projects in Eclipse and in
   * stand-alone EMF.
   *
   * <p>Path encoding is performed only if the
   * <code>org.eclipse.emf.common.util.URI.encodePlatformResourceURIs</code> 
   * system property is set to "true". Decoding can be performed with the
   * static {@link #decode(String) decode} method.
   * 
   * @exception java.lang.IllegalArgumentException if any component parsed
   * from the path is not valid according to {@link #validDevice validDevice},
   * {@link #validSegments validSegments}, {@link #validQuery validQuery}, or
   * {@link #validFragment validFragment}.
   *
   * @see org.eclipse.core.runtime.Platform#resolve
   * @see #createPlatformResourceURI(String, boolean)
   * @deprecated Use {@link #createPlatformResourceURI(String, boolean)} instead.
   */
  @Deprecated
  public static URI createPlatformResourceURI(String pathName)
  {
    return createPlatformResourceURI(pathName, ENCODE_PLATFORM_RESOURCE_URIS);
  }

  /**
   * Static factory method based on parsing a workspace-relative path string,
   * with an option to encode the created URI.
   *
   * <p>The <code>pathName</code> must be of the form:
   * <pre>
   *   /project-name/path</pre>
   *
   * <p>Platform-specific path separators will be converted to slashes.
   * If not included, the leading path separator will be added.  The
   * result will be of this form, which is parsed using {@link #createURI(String)
   * createURI}:
   * <pre>
   *   platform:/resource/project-name/path</pre>
   *
   * <p>This scheme supports relocatable projects in Eclipse and in
   * stand-alone EMF.
   *
   * <p>Depending on the <code>encode</code> argument, the path may be
   * automatically encoded to escape all spaces, <code>#</code> characters,
   * and other characters disallowed in URIs, as well as <code>?</code>,
   * which would delimit a path from a query.  Decoding can be performed with
   * the static {@link #decode(String) decode} method. It is strongly
   * recommended to specify <code>true</code> to enable encoding, unless the
   * path string has already been encoded.
   * 
   * @exception java.lang.IllegalArgumentException if any component parsed
   * from the path is not valid according to {@link #validDevice validDevice},
   * {@link #validSegments validSegments}, {@link #validQuery validQuery}, or
   * {@link #validFragment validFragment}.
   *
   * @see org.eclipse.core.runtime.Platform#resolve
   */
  public static URI createPlatformResourceURI(String pathName, boolean encode)
  {
    return createPlatformURI("platform:/resource", "platform:/resource/", pathName, encode);
  }

  /**
   * Static factory method based on parsing a plug-in-based path string,
   * with an option to encode the created URI.
   *
   * <p>The <code>pathName</code> must be of the form:
   * <pre>
   *   /plugin-id/path</pre>
   *
   * <p>Platform-specific path separators will be converted to slashes.
   * If not included, the leading path separator will be added.  The
   * result will be of this form, which is parsed using {@link #createURI(String)
   * createURI}:
   * <pre>
   *   platform:/plugin/plugin-id/path</pre>
   *
   * <p>This scheme supports relocatable plug-in content in Eclipse.
   *
   * <p>Depending on the <code>encode</code> argument, the path may be
   * automatically encoded to escape all spaces, <code>#</code> characters,
   * and other characters disallowed in URIs, as well as <code>?</code>,
   * which would delimit a path from a query.  Decoding can be performed with
   * the static {@link #decode(String) decode} method. It is strongly
   * recommended to specify <code>true</code> to enable encoding, unless the
   * path string has already been encoded.
   * 
   * @exception java.lang.IllegalArgumentException if any component parsed
   * from the path is not valid according to {@link #validDevice validDevice},
   * {@link #validSegments validSegments}, {@link #validQuery validQuery}, or
   * {@link #validFragment validFragment}.
   *
   * @see org.eclipse.core.runtime.Platform#resolve
   * @since org.eclipse.emf.common 2.3
   */
  public static URI createPlatformPluginURI(String pathName, boolean encode)
  {
    return createPlatformURI("platform:/plugin", "platform:/plugin/", pathName, encode);
  }

  // Private constructor for use of platform factory methods.
  private static URI createPlatformURI(String unrootedBase, String rootedBase, String pathName, boolean encode)
  {
    pathName = pathName.replace('\\', SEGMENT_SEPARATOR);

    if (encode)
    {
      pathName = encode(pathName, PATH_CHAR_HI, PATH_CHAR_LO, false);
    }
    URI result = createURI((pathName.charAt(0) == SEGMENT_SEPARATOR ? unrootedBase : rootedBase) + pathName);
    return result;
  }
  
  // Private constructor for use of static factory methods.
  private URI(boolean hierarchical, String scheme, String authority,
              String device, boolean absolutePath, String[] segments,
              String query, String fragment)
  {
    int hashCode = 0;
    //boolean iri = false;

    if (scheme != null)
    {
      hashCode ^= scheme.toLowerCase().hashCode();
    }
    if (authority != null)
    {
      hashCode ^= authority.hashCode();
      //iri = iri || containsNonASCII(authority);
    }
    if (device != null)
    {
      hashCode ^= device.hashCode();
      //iri = iri || containsNonASCII(device);
    }
    if (query != null)
    {
      hashCode ^= query.hashCode();
      //iri = iri || containsNonASCII(query);
    }
    if (fragment != null)
    {
      hashCode ^= fragment.hashCode();
      //iri = iri || containsNonASCII(fragment);
    }

    for (int i = 0, len = segments.length; i < len; i++)
    {
      hashCode ^= segments[i].hashCode();
      //iri = iri || containsNonASCII(segments[i]);
    }

    if (hierarchical)
    {
      hashCode |= HIERARICHICAL_FLAG;
    }
    else
    {
      hashCode &= ~HIERARICHICAL_FLAG;
    }
    if (absolutePath)
    {
      hashCode |= ABSOLUTE_PATH_FLAG;
    }
    else
    {
      hashCode &= ~ABSOLUTE_PATH_FLAG;
    }
    this.hashCode = hashCode;
    //this.iri = iri;
    this.scheme = scheme == null ? null : scheme.intern();
    this.authority = authority;
    this.device = device;
    this.segments = segments;
    this.query = query;
    this.fragment = fragment;
  }
  
  // Validates all of the URI components.  Factory methods should call this
  // before using the constructor, though they must ensure that the
  // inter-component requirements described in their own Javadocs are all
  // satisfied, themselves.  If a new URI is being constructed out of
  // an existing URI, this need not be called.  Instead, just the new
  // components may be validated individually.
  private static void validateURI(boolean hierarchical, String scheme,
                                    String authority, String device,
                                    boolean absolutePath, String[] segments,
                                    String query, String fragment)
  {
    if (!validScheme(scheme))
    {
      throw new IllegalArgumentException("invalid scheme: " + scheme);
    }
    if (!hierarchical && !validOpaquePart(authority))
    {
      throw new IllegalArgumentException("invalid opaquePart: " + authority);
    }
    if (hierarchical && !isArchiveScheme(scheme) && !validAuthority(authority))
    {
      throw new IllegalArgumentException("invalid authority: " + authority);
    }
    if (hierarchical && isArchiveScheme(scheme) && !validArchiveAuthority(authority))
    {
      throw new IllegalArgumentException("invalid authority: " + authority);
    }
    if (!validDevice(device))
    {
      throw new IllegalArgumentException("invalid device: " + device);
    }
    if (!validSegments(segments))
    {
      String s = segments == null ? "invalid segments: null" :
        "invalid segment: " + firstInvalidSegment(segments);
      throw new IllegalArgumentException(s);
    }
    if (!validQuery(query))
    {
      throw new IllegalArgumentException("invalid query: " + query);
    }
    if (!validFragment(fragment))
    {
      throw new IllegalArgumentException("invalid fragment: " + fragment);
    }
  }

  // Alternate, stricter implementations of the following validation methods
  // are provided, commented out, for possible future use...

  /**
   * Returns <code>true</code> if the specified <code>value</code> would be
   * valid as the scheme component of a URI; <code>false</code> otherwise.
   *
   * <p>A valid scheme may be null or contain any characters except for the
   * following: <code>: / ? #</code>
   */
  public static boolean validScheme(String value)
  {
    return value == null || !contains(value, MAJOR_SEPARATOR_HI, MAJOR_SEPARATOR_LO);  

  // <p>A valid scheme may be null, or consist of a single letter followed
  // by any number of letters, numbers, and the following characters:
  // <code>+ - .</code>

    //if (value == null) return true;
    //return value.length() != 0 &&
    //  matches(value.charAt(0), ALPHA_HI, ALPHA_LO) &&
    //  validate(value, SCHEME_CHAR_HI, SCHEME_CHAR_LO, false, false);
  }

  /**
   * Returns <code>true</code> if the specified <code>value</code> would be
   * valid as the opaque part component of a URI; <code>false</code>
   * otherwise.
   *
   * <p>A valid opaque part must be non-null, non-empty, and not contain the
   * <code>#</code> character.  In addition, its first character must not be
   * <code>/</code>
   */
  public static boolean validOpaquePart(String value)
  {
    return value != null && value.indexOf(FRAGMENT_SEPARATOR) == -1 &&
    value.length() > 0 && value.charAt(0) != SEGMENT_SEPARATOR;

  // <p>A valid opaque part must be non-null and non-empty. It may contain
  // any allowed URI characters, but its first character may not be
  // <code>/</code> 

    //return value != null && value.length() != 0 &&
    //  value.charAt(0) != SEGMENT_SEPARATOR &&
    //  validate(value, URIC_HI, URIC_LO, true, true);
  }

  /**
   * Returns <code>true</code> if the specified <code>value</code> would be
   * valid as the authority component of a URI; <code>false</code> otherwise.
   *
   * <p>A valid authority may be null or contain any characters except for
   * the following: <code>/ ? #</code>
   */
  public static boolean validAuthority(String value)
  {
    return value == null || !contains(value, SEGMENT_END_HI, SEGMENT_END_LO);

  // A valid authority may be null or contain any allowed URI characters except
  // for the following: <code>/ ?</code>

    //return value == null || validate(value, SEGMENT_CHAR_HI, SEGMENT_CHAR_LO, true, true);
  }

  /**
   * Returns <code>true</code> if the specified <code>value</code> would be
   * valid as the authority component of an <a
   * href="#archive_explanation">archive URI</a>; <code>false</code>
   * otherwise.
   *
   * <p>To be valid, the authority, itself, must be a URI with no fragment,
   * followed by the character <code>!</code>.
   */
  public static boolean validArchiveAuthority(String value)
  {
    if (value != null && value.length() > 0 &&
        value.charAt(value.length() - 1) == ARCHIVE_IDENTIFIER)
    {
      try
      {
        URI archiveURI = createURI(value.substring(0, value.length() - 1));
        return !archiveURI.hasFragment();
      }
      catch (IllegalArgumentException e)
      {
        // Ignore the exception and return false.
      }
    }
    return false;
  }

  /**
   * Tests whether the specified <code>value</code> would be valid as the
   * authority component of an <a href="#archive_explanation">archive
   * URI</a>. This method has been replaced by {@link #validArchiveAuthority
   * validArchiveAuthority} since the same form of URI is now supported
   * for schemes other than "jar". This now simply calls that method.
   * 
   * @deprecated As of EMF 2.0, replaced by {@link #validArchiveAuthority
   * validArchiveAuthority}.
   */
  @Deprecated
  public static boolean validJarAuthority(String value)
  {
    return validArchiveAuthority(value);
  }

  /**
   * Returns <code>true</code> if the specified <code>value</code> would be
   * valid as the device component of a URI; <code>false</code> otherwise.
   *
   * <p>A valid device may be null or non-empty, containing any characters
   * except for the following: <code>/ ? #</code>  In addition, its last
   * character must be <code>:</code>
   */
  public static boolean validDevice(String value)
  {    
    if (value == null) return true;
    int len = value.length();
    return len > 0 && value.charAt(len - 1) == DEVICE_IDENTIFIER &&
      !contains(value, SEGMENT_END_HI, SEGMENT_END_LO);
  }

  /**
   * Returns <code>true</code> if the specified <code>value</code> would be
   * a valid path segment of a URI; <code>false</code> otherwise.
   *
   * <p>A valid path segment must be non-null and not contain any of the
   * following characters: <code>/ ? #</code>
   */
  public static boolean validSegment(String value)
  {
    return value != null && !contains(value, SEGMENT_END_HI, SEGMENT_END_LO);

  // <p>A valid path segment must be non-null and may contain any allowed URI
  // characters except for the following: <code>/ ?</code> 

    //return value != null && validate(value, SEGMENT_CHAR_HI, SEGMENT_CHAR_LO, true, true);
  }

  /**
   * Returns <code>true</code> if the specified <code>value</code> would be
   * a valid path segment array of a URI; <code>false</code> otherwise.
   *
   * <p>A valid path segment array must be non-null and contain only path
   * segments that are valid according to {@link #validSegment validSegment}.
   */
  public static boolean validSegments(String[] value)
  {
    if (value == null) return false;
    for (int i = 0, len = value.length; i < len; i++)
    {
      if (!validSegment(value[i])) return false;
    }
    return true;
  }

  // Returns null if the specified value is null or would be a valid path
  // segment array of a URI; otherwise, the value of the first invalid
  // segment. 
  private static String firstInvalidSegment(String[] value)
  {
    if (value == null) return null;
    for (int i = 0, len = value.length; i < len; i++)
    {
      if (!validSegment(value[i])) return value[i];
    }
    return null;
  }

  /**
   * Returns <code>true</code> if the specified <code>value</code> would be
   * valid as the query component of a URI; <code>false</code> otherwise.
   *
   * <p>A valid query may be null or contain any characters except for
   * <code>#</code>
   */
  public static boolean validQuery(String value)
  {
    return value == null || value.indexOf(FRAGMENT_SEPARATOR) == -1;

  // <p>A valid query may be null or contain any allowed URI characters.

    //return value == null || validate(value, URIC_HI, URIC_LO, true, true);
}

  /**
   * Returns <code>true</code> if the specified <code>value</code> would be
   * valid as the fragment component of a URI; <code>false</code> otherwise.
   *
   * <p>A fragment is taken to be unconditionally valid.
   */
  public static boolean validFragment(String value)
  {
    return true;

  // <p>A valid fragment may be null or contain any allowed URI characters.

    //return value == null || validate(value, URIC_HI, URIC_LO, true, true);
  }

  // Searches the specified string for any characters in the set represented
  // by the 128-bit bitmask.  Returns true if any occur, or false otherwise.
  private static boolean contains(String s, long highBitmask, long lowBitmask)
  {
    for (int i = 0, len = s.length(); i < len; i++)
    {
      if (matches(s.charAt(i), highBitmask, lowBitmask)) return true;
    }
    return false;
  }

  // Tests the non-null string value to see if it contains only ASCII
  // characters in the set represented by the specified 128-bit bitmask,
  // as well as, optionally, non-ASCII characters 0xA0 and above, and,
  // also optionally, escape sequences of % followed by two hex digits.
  // This method is used for the new, strict URI validation that is not
  // not currently in place.
/*
  private static boolean validate(String value, long highBitmask, long lowBitmask,
                                     boolean allowNonASCII, boolean allowEscaped)
  {
    for (int i = 0, length = value.length(); i < length; i++)
    { 
      char c = value.charAt(i);

      if (matches(c, highBitmask, lowBitmask)) continue;
      if (allowNonASCII && c >= 160) continue;
      if (allowEscaped && isEscaped(value, i))
      {
        i += 2;
        continue;
      }
      return false;
    }
    return true;
  }
*/

  /**
   * Returns <code>true</code> if this is a relative URI, or
   * <code>false</code> if it is an absolute URI.
   */
  public boolean isRelative()
  {
    return scheme == null;
  }

  /**
   * Returns <code>true</code> if this a a hierarchical URI, or
   * <code>false</code> if it is of the generic form.
   */
  public boolean isHierarchical()
  {
    return (hashCode & HIERARICHICAL_FLAG) != 0;
  }

  /**
   * Returns <code>true</code> if this is a hierarchical URI with an authority
   * component; <code>false</code> otherwise. 
   */
  public boolean hasAuthority()
  {
    return isHierarchical() && authority != null;
  }

  /**
   * Returns <code>true</code> if this is a non-hierarchical URI with an
   * opaque part component; <code>false</code> otherwise.
   */
  public boolean hasOpaquePart()
  {
    // note: hierarchical -> authority != null
    return !isHierarchical();
  }

  /**
   * Returns <code>true</code> if this is a hierarchical URI with a device
   * component; <code>false</code> otherwise.
   */
  public boolean hasDevice()
  {
    // note: device != null -> hierarchical
    return device != null;
  }

  /**
   * Returns <code>true</code> if this is a hierarchical URI with an
   * absolute or relative path; <code>false</code> otherwise.
   */
  public boolean hasPath()
  {
    // note: (absolutePath || authority == null) -> hierarchical
    // (authority == null && device == null && !absolutePath) -> scheme == null
    return hasAbsolutePath() || (authority == null && device == null);
  }

  /**
   * Returns <code>true</code> if this is a hierarchical URI with an
   * absolute path, or <code>false</code> if it is non-hierarchical, has no
   * path, or has a relative path.
   */
  public boolean hasAbsolutePath()
  {
    // note: absolutePath -> hierarchical
    return (hashCode & ABSOLUTE_PATH_FLAG) != 0;
  }

  /**
   * Returns <code>true</code> if this is a hierarchical URI with a relative
   * path, or <code>false</code> if it is non-hierarchical, has no path, or
   * has an absolute path.
   */
  public boolean hasRelativePath()
  {
    // note: authority == null -> hierarchical
    // (authority == null && device == null && !absolutePath) -> scheme == null
    return authority == null && device == null && !hasAbsolutePath();
  }

  /**
   * Returns <code>true</code> if this is a hierarchical URI with an empty
   * relative path; <code>false</code> otherwise.  
   *
   * <p>Note that <code>!hasEmpty()</code> does <em>not</em> imply that this
   * URI has any path segments; however, <code>hasRelativePath &&
   * !hasEmptyPath()</code> does.
   */
  public boolean hasEmptyPath()
  {
    // note: authority == null -> hierarchical
    // (authority == null && device == null && !absolutePath) -> scheme == null
    return authority == null && device == null && !hasAbsolutePath() &&
      segments.length == 0;
  }

  /**
   * Returns <code>true</code> if this is a hierarchical URI with a query
   * component; <code>false</code> otherwise.
   */
  public boolean hasQuery()
  {
    // note: query != null -> hierarchical
    return query != null;
  }

  /**
   * Returns <code>true</code> if this URI has a fragment component;
   * <code>false</code> otherwise.
   */
  public boolean hasFragment()
  {
    return fragment != null;
  }

  /**
   * Returns <code>true</code> if this is a current document reference; that
   * is, if it is a relative hierarchical URI with no authority, device or
   * query components, and no path segments; <code>false</code> is returned
   * otherwise.
   */
  public boolean isCurrentDocumentReference()
  {
    // note: authority == null -> hierarchical
    // (authority == null && device == null && !absolutePath) -> scheme == null
    return authority == null && device == null && !hasAbsolutePath() &&
      segments.length == 0 && query == null;
  }

  /**
   * Returns <code>true</code> if this is a {@link
   * #isCurrentDocumentReference() current document reference} with no
   * fragment component; <code>false</code> otherwise.
   *
   * @see #isCurrentDocumentReference()
   */
  public boolean isEmpty()
  {
    // note: authority == null -> hierarchical
    // (authority == null && device == null && !absolutePath) -> scheme == null
    return authority == null && device == null && !hasAbsolutePath() &&
      segments.length == 0 && query == null && fragment == null;
  }

  /**
   * Returns <code>true</code> if this is a hierarchical URI that may refer
   * directly to a locally accessible file.  This is considered to be the
   * case for a file-scheme absolute URI, or for a relative URI with no query;
   * <code>false</code> is returned otherwise.
   */
  public boolean isFile()
  {
    return isHierarchical() &&
      ((isRelative() && !hasQuery()) || SCHEME_FILE.equalsIgnoreCase(scheme));
  }

  /**
   * Returns <code>true</code> if this is a platform URI, that is, an absolute,
   * hierarchical URI, with "platform" scheme, no authority, and at least two
   * segments; <code>false</code> is returned otherwise.
   * @since org.eclipse.emf.common 2.3
   */
  public boolean isPlatform()
  {
    return isHierarchical() && !hasAuthority() && segmentCount() >= 2 && 
      SCHEME_PLATFORM.equalsIgnoreCase(scheme);
  }

  /**
   * Returns <code>true</code> if this is a platform resource URI, that is,
   * a {@link #isPlatform platform URI} whose first segment is "resource";
   * <code>false</code> is returned otherwise.
   * @see #isPlatform
   * @since org.eclipse.emf.common 2.3
   */
  public boolean isPlatformResource()
  {
    return isPlatform() && "resource".equals(segments[0]);
  }

  /**
   * Returns <code>true</code> if this is a platform plug-in URI, that is,
   * a {@link #isPlatform platform URI} whose first segment is "plugin";
   * <code>false</code> is returned otherwise.
   * @see #isPlatform
   * @since org.eclipse.emf.common 2.3
   */
  public boolean isPlatformPlugin()
  {
    return isPlatform() && "plugin".equals(segments[0]);
  }

  /**
   * Returns <code>true</code> if this is an archive URI.  If so, it is also
   * hierarchical, with an authority (consisting of an absolute URI followed
   * by "!"), no device, and an absolute path.
   */
  public boolean isArchive()
  {
    return isArchiveScheme(scheme);
  }

  /**
   * Returns <code>true</code> if the specified <code>value</code> would be
   * valid as the scheme of an <a
   * href="#archive_explanation">archive URI</a>; <code>false</code>
   * otherwise.
   */
  public static boolean isArchiveScheme(String value)
  {
    // Returns true if the given value is an archive scheme, as defined by
    // the org.eclipse.emf.common.util.URI.archiveSchemes system property.
    // By default, "jar", "zip", and "archive" are considered archives.
    return value != null && archiveSchemes.contains(value.toLowerCase());
  }
  
  /**
   * Returns the hash code.
   */
  @Override
  public int hashCode()
  {
    return hashCode;
  }

  /**
   * Returns <code>true</code> if <code>object</code> is an instance of
   * <code>URI</code> equal to this one; <code>false</code> otherwise.
   *
   * <p>Equality is determined strictly by comparing components, not by
   * attempting to interpret what resource is being identified.  The
   * comparison of schemes is case-insensitive.
   */
  @Override
  public boolean equals(Object object)
  {
    if (this == object) return true;
    if (!(object instanceof URI)) return false;
    URI uri = (URI) object;

    return hashCode == uri.hashCode() &&
      equals(scheme, uri.scheme(), true) &&
      equals(authority, isHierarchical() ? uri.authority() : uri.opaquePart()) &&
      equals(device, uri.device()) &&
      equals(query, uri.query()) && 
      equals(fragment, uri.fragment()) &&
      segmentsEqual(uri);
  }

  // Tests whether this URI's path segment array is equal to that of the
  // given uri.
  private boolean segmentsEqual(URI uri)
  {
    if (segments.length != uri.segmentCount()) return false;
    for (int i = 0, len = segments.length; i < len; i++)
    {
      if (!segments[i].equals(uri.segment(i))) return false;
    }
    return true;
  }

  // Tests two objects for equality, tolerating nulls; null is considered
  // to be a valid value that is only equal to itself.
  private static boolean equals(Object o1, Object o2)
  {
    return o1 == null ? o2 == null : o1.equals(o2);
  }

  // Tests two strings for equality, tolerating nulls and optionally
  // ignoring case.
  private static boolean equals(String s1, String s2, boolean ignoreCase)
  {
    return s1 == null ? s2 == null :
      ignoreCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2);
  }

  /**
   * If this is an absolute URI, returns the scheme component;
   * <code>null</code> otherwise.
   */
  public String scheme()
  {
    return scheme;
  }

  /**
   * If this is a non-hierarchical URI, returns the opaque part component;
   * <code>null</code> otherwise.
   */
  public String opaquePart()
  {
    return isHierarchical() ? null : authority;
  }

  /**
   * If this is a hierarchical URI with an authority component, returns it;
   * <code>null</code> otherwise.
   */
  public String authority()
  {
    return isHierarchical() ? authority : null;
  }

  /**
   * If this is a hierarchical URI with an authority component that has a
   * user info portion, returns it; <code>null</code> otherwise.
   */
  public String userInfo()
  { 
    if (!hasAuthority()) return null;
   
    int i = authority.indexOf(USER_INFO_SEPARATOR);
    return i < 0 ? null : authority.substring(0, i);
  }

  /**
   * If this is a hierarchical URI with an authority component that has a
   * host portion, returns it; <code>null</code> otherwise.
   */
  public String host()
  {
    if (!hasAuthority()) return null;
    
    int i = authority.indexOf(USER_INFO_SEPARATOR);
    int j = authority.indexOf(PORT_SEPARATOR);
    return j < 0 ? authority.substring(i + 1) : authority.substring(i + 1, j);
  }

  /**
   * If this is a hierarchical URI with an authority component that has a
   * port portion, returns it; <code>null</code> otherwise.
   */
  public String port()
  {
    if (!hasAuthority()) return null;

    int i = authority.indexOf(PORT_SEPARATOR);
    return i < 0 ? null : authority.substring(i + 1);
  }

  /**
   * If this is a hierarchical URI with a device component, returns it;
   * <code>null</code> otherwise.
   */
  public String device()
  {
    return device;
  }

  /**
   * If this is a hierarchical URI with a path, returns an array containing
   * the segments of the path; an empty array otherwise.  The leading
   * separator in an absolute path is not represented in this array, but a
   * trailing separator is represented by an empty-string segment as the
   * final element.
   */
  public String[] segments()
  {
    String[] result = new String[segments.length];
    System.arraycopy(segments, 0, result, 0, segments.length);
    return result;
  }

  /**
   * Returns an unmodifiable list containing the same segments as the array
   * returned by {@link #segments segments}.
   */
  public List<String> segmentsList()
  {
    return Collections.unmodifiableList(Arrays.asList(segments));
  }

  /**
   * Returns the number of elements in the segment array that would be
   * returned by {@link #segments segments}.
   */
  public int segmentCount()
  {
    return segments.length;
  }

  /**
   * Provides fast, indexed access to individual segments in the path
   * segment array.
   *
   * @exception java.lang.IndexOutOfBoundsException if <code>i < 0</code> or
   * <code>i >= segmentCount()</code>.
   */
  public String segment(int i)
  {
    return segments[i];
  }

  /**
   * Returns the last segment in the segment array, or <code>null</code>.
   */
  public String lastSegment()
  {
    int len = segments.length;
    if (len == 0) return null;
    return segments[len - 1];
  }

  /**
   * If this is a hierarchical URI with a path, returns a string
   * representation of the path; <code>null</code> otherwise.  The path
   * consists of a leading segment separator character (a slash), if the
   * path is absolute, followed by the slash-separated path segments.  If
   * this URI has a separate <a href="#device_explanation">device
   * component</a>, it is <em>not</em> included in the path.
   */
  public String path()
  {
    if (!hasPath()) return null;

    StringBuffer result = new StringBuffer();
    if (hasAbsolutePath()) result.append(SEGMENT_SEPARATOR);

    for (int i = 0, len = segments.length; i < len; i++)
    {
      if (i != 0) result.append(SEGMENT_SEPARATOR);
      result.append(segments[i]);
    }
    return result.toString();
  }

  /**
   * If this is a hierarchical URI with a path, returns a string
   * representation of the path, including the authority and the 
   * <a href="#device_explanation">device component</a>; 
   * <code>null</code> otherwise.  
   *
   * <p>If there is no authority, the format of this string is:
   * <pre>
   *   device/pathSegment1/pathSegment2...</pre>
   *
   * <p>If there is an authority, it is:
   * <pre>
   *   //authority/device/pathSegment1/pathSegment2...</pre>
   *
   * <p>For an <a href="#archive_explanation">archive URI</a>, it's just:
   * <pre>
   *   authority/pathSegment1/pathSegment2...</pre>
   */
  public String devicePath()
  {
    if (!hasPath()) return null;

    StringBuffer result = new StringBuffer();

    if (hasAuthority())
    {
      if (!isArchive()) result.append(AUTHORITY_SEPARATOR);
      result.append(authority);

      if (hasDevice()) result.append(SEGMENT_SEPARATOR);
    }

    if (hasDevice()) result.append(device);
    if (hasAbsolutePath()) result.append(SEGMENT_SEPARATOR);

    for (int i = 0, len = segments.length; i < len; i++)
    {
      if (i != 0) result.append(SEGMENT_SEPARATOR);
      result.append(segments[i]);
    }
    return result.toString();
  }

  /**
   * If this is a hierarchical URI with a query component, returns it;
   * <code>null</code> otherwise.
   */
  public String query()
  {
    return query;
  }


  /**
   * Returns the URI formed from this URI and the given query.
   *
   * @exception java.lang.IllegalArgumentException if
   * <code>query</code> is not a valid query (portion) according
   * to {@link #validQuery validQuery}.
   */
  public URI appendQuery(String query)
  {
    if (!validQuery(query))
    {
      throw new IllegalArgumentException(
        "invalid query portion: " + query);
    }
    return new URI(isHierarchical(), scheme, authority, device, hasAbsolutePath(), segments, query, fragment); 
  }

  /**
   * If this URI has a non-null {@link #query query}, returns the URI
   * formed by removing it; this URI unchanged, otherwise.
   */
  public URI trimQuery()
  {
    if (query == null)
    {
      return this;
    }
    else
    {
      return new URI(isHierarchical(), scheme, authority, device, hasAbsolutePath(), segments, null, fragment); 
    }
  }

  /**
   * If this URI has a fragment component, returns it; <code>null</code>
   * otherwise.
   */
  public String fragment()
  {
    return fragment;
  }

  /**
   * Returns the URI formed from this URI and the given fragment.
   *
   * @exception java.lang.IllegalArgumentException if
   * <code>fragment</code> is not a valid fragment (portion) according
   * to {@link #validFragment validFragment}.
   */
  public URI appendFragment(String fragment)
  {
    if (!validFragment(fragment))
    {
      throw new IllegalArgumentException(
        "invalid fragment portion: " + fragment);
    }
    URI result = new URI(isHierarchical(), scheme, authority, device, hasAbsolutePath(), segments, query, fragment); 

    if (!hasFragment())
    {
      result.cachedTrimFragment = this;
    }
    return result;
  }

  /**
   * If this URI has a non-null {@link #fragment fragment}, returns the URI
   * formed by removing it; this URI unchanged, otherwise.
   */
  public URI trimFragment()
  {
    if (fragment == null)
    {
      return this;
    }
    else if (cachedTrimFragment == null)
    {
      cachedTrimFragment = new URI(isHierarchical(), scheme, authority, device, hasAbsolutePath(), segments, query, null); 
    }

    return cachedTrimFragment;
  }

  /**
   * Resolves this URI reference against a <code>base</code> absolute
   * hierarchical URI, returning the resulting absolute URI.  If already
   * absolute, the URI itself is returned.  URI resolution is described in
   * detail in section 5.2 of <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC
   * 2396</a>, "Resolving Relative References to Absolute Form."
   *
   * <p>During resolution, empty segments, self references ("."), and parent
   * references ("..") are interpreted, so that they can be removed from the
   * path.  Step 6(g) gives a choice of how to handle the case where parent
   * references point to a path above the root: the offending segments can
   * be preserved or discarded.  This method preserves them.  To have them
   * discarded, please use the two-parameter form of {@link
   * #resolve(URI, boolean) resolve}.
   *
   * @exception java.lang.IllegalArgumentException if <code>base</code> is
   * non-hierarchical or is relative.
   */
  public URI resolve(URI base)
  {
    return resolve(base, true);
  }

  /**
   * Resolves this URI reference against a <code>base</code> absolute
   * hierarchical URI, returning the resulting absolute URI.  If already
   * absolute, the URI itself is returned.  URI resolution is described in
   * detail in section 5.2 of <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC
   * 2396</a>, "Resolving Relative References to Absolute Form."
   *
   * <p>During resolution, empty segments, self references ("."), and parent
   * references ("..") are interpreted, so that they can be removed from the
   * path.  Step 6(g) gives a choice of how to handle the case where parent
   * references point to a path above the root: the offending segments can
   * be preserved or discarded.  This method can do either.
   *
   * @param preserveRootParents <code>true</code> if segments referring to the
   * parent of the root path are to be preserved; <code>false</code> if they
   * are to be discarded.
   *
   * @exception java.lang.IllegalArgumentException if <code>base</code> is
   * non-hierarchical or is relative.
   */
  public URI resolve(URI base, boolean preserveRootParents)
  {
    if (!base.isHierarchical() || base.isRelative())
    {
      throw new IllegalArgumentException(
        "resolve against non-hierarchical or relative base");
    }

    // an absolute URI needs no resolving
    if (!isRelative()) return this;

    // note: isRelative() -> hierarchical

    String newAuthority = authority;
    String newDevice = device;
    boolean newAbsolutePath = hasAbsolutePath();
    String[] newSegments = segments;
    String newQuery = query;
    // note: it's okay for two URIs to share a segments array, since
    // neither will ever modify it
    
    if (authority == null)
    {
      // no authority: use base's
      newAuthority = base.authority();

      if (device == null)
      {
        // no device: use base's
        newDevice = base.device();

        if (hasEmptyPath() && query == null)
        {
          // current document reference: use base path and query
          newAbsolutePath = base.hasAbsolutePath();
          newSegments = base.segments();
          newQuery = base.query();
        }
        else if (hasRelativePath())
        {
          // relative path: merge with base and keep query (note: if the
          // base has no path and this a non-empty relative path, there is
          // an implied root in the resulting path) 
          newAbsolutePath = base.hasAbsolutePath() || !hasEmptyPath();
          newSegments = newAbsolutePath ? mergePath(base, preserveRootParents)
            : NO_SEGMENTS;
        }
        // else absolute path: keep it and query
      }
      // else keep device, path, and query
    }
    // else keep authority, device, path, and query
    
    // always keep fragment, even if null, and use scheme from base;
    // no validation needed since all components are from existing URIs
    return new URI(true, base.scheme(), newAuthority, newDevice,
                   newAbsolutePath, newSegments, newQuery, fragment);
  }

  // Merges this URI's relative path with the base non-relative path.  If
  // base has no path, treat it as the root absolute path, unless this has
  // no path either.
  private String[] mergePath(URI base, boolean preserveRootParents)
  {
    if (base.hasRelativePath())
    {
      throw new IllegalArgumentException("merge against relative path");
    }
    if (!hasRelativePath())
    {
      throw new IllegalStateException("merge non-relative path");
    }

    int baseSegmentCount = base.segmentCount();
    int segmentCount = segments.length;
    String[] stack = new String[baseSegmentCount + segmentCount];
    int sp = 0;

    // use a stack to accumulate segments of base, except for the last
    // (i.e. skip trailing separator and anything following it), and of
    // relative path
    for (int i = 0; i < baseSegmentCount - 1; i++)
    {
      sp = accumulate(stack, sp, base.segment(i), preserveRootParents);
    }

    for (int i = 0; i < segmentCount; i++)
    {
      sp = accumulate(stack, sp, segments[i], preserveRootParents);
    }

    // if the relative path is empty or ends in an empty segment, a parent 
    // reference, or a self reference, add a trailing separator to a
    // non-empty path
    if (sp > 0 &&  (segmentCount == 0 ||
                    SEGMENT_EMPTY.equals(segments[segmentCount - 1]) ||
                    SEGMENT_PARENT.equals(segments[segmentCount - 1]) ||
                    SEGMENT_SELF.equals(segments[segmentCount - 1])))
    {
      stack[sp++] = SEGMENT_EMPTY;
    }

    // return a correctly sized result
    String[] result = new String[sp];
    System.arraycopy(stack, 0, result, 0, sp);
    return result;
  }

  // Adds a segment to a stack, skipping empty segments and self references,
  // and interpreting parent references.
  private static int accumulate(String[] stack, int sp, String segment,
                                boolean preserveRootParents)
  {
    if (SEGMENT_PARENT.equals(segment))
    {
      if (sp == 0)
      {
        // special care must be taken for a root's parent reference: it is
        // either ignored or the symbolic reference itself is pushed
        if (preserveRootParents) stack[sp++] = segment;
      }
      else
      {
        // unless we're already accumulating root parent references,
        // parent references simply pop the last segment descended
        if (SEGMENT_PARENT.equals(stack[sp - 1])) stack[sp++] = segment;
        else sp--;
      }
    }
    else if (!SEGMENT_EMPTY.equals(segment) && !SEGMENT_SELF.equals(segment))
    {
      // skip empty segments and self references; push everything else
      stack[sp++] = segment;
    }
    return sp;
  }

  /**
   * Finds the shortest relative or, if necessary, the absolute URI that,
   * when resolved against the given <code>base</code> absolute hierarchical
   * URI using {@link #resolve(URI) resolve}, will yield this absolute URI.  
   * If <code>base</code> is non-hierarchical or is relative,
   * or <code>this</code> is non-hierarchical or is relative,
   * <code>this</code> will be returned.
   */
  public URI deresolve(URI base)
  {
    return deresolve(base, true, false, true);
  }

  /**
   * Finds an absolute URI that, when resolved against the given
   * <code>base</code> absolute hierarchical URI using {@link
   * #resolve(URI, boolean) resolve}, will yield this absolute URI.
   * If <code>base</code> is non-hierarchical or is relative,
   * or <code>this</code> is non-hierarchical or is relative,
   * <code>this</code> will be returned.
   *
   * @param preserveRootParents the boolean argument to <code>resolve(URI,
   * boolean)</code> for which the returned URI should resolve to this URI.
   * @param anyRelPath if <code>true</code>, the returned URI's path (if
   * any) will be relative, if possible.  If <code>false</code>, the form of
   * the result's path will depend upon the next parameter.
   * @param shorterRelPath if <code>anyRelPath</code> is <code>false</code>
   * and this parameter is <code>true</code>, the returned URI's path (if
   * any) will be relative, if one can be found that is no longer (by number
   * of segments) than the absolute path.  If both <code>anyRelPath</code>
   * and this parameter are <code>false</code>, it will be absolute.
   */
  public URI deresolve(URI base, boolean preserveRootParents,
                       boolean anyRelPath, boolean shorterRelPath)
  {
    if (!base.isHierarchical() || base.isRelative()) return this;
    
    if (isRelative()) return this;

    // note: these assertions imply that neither this nor the base URI has a
    // relative path; thus, both have either an absolute path or no path
    
    // different scheme: need complete, absolute URI
    if (!scheme.equalsIgnoreCase(base.scheme())) return this;

    // since base must be hierarchical, and since a non-hierarchical URI
    // must have both scheme and opaque part, the complete absolute URI is
    // needed to resolve to a non-hierarchical URI
    if (!isHierarchical()) return this;

    String newAuthority = authority;
    String newDevice = device;
    boolean newAbsolutePath = hasAbsolutePath();
    String[] newSegments = segments;
    String newQuery = query;

    if (equals(authority, base.authority()) &&
        (hasDevice() || hasPath() || (!base.hasDevice() && !base.hasPath())))
    {
      // matching authorities and no device or path removal
      newAuthority = null;

      if (equals(device, base.device()) && (hasPath() || !base.hasPath()))
      {
        // matching devices and no path removal
        newDevice = null;

        // exception if (!hasPath() && base.hasPath())

        if (!anyRelPath && !shorterRelPath)
        {
          // user rejects a relative path: keep absolute or no path
        }
        else if (hasPath() == base.hasPath() && segmentsEqual(base) &&
                 equals(query, base.query()))
        {
          // current document reference: keep no path or query
          newAbsolutePath = false;
          newSegments = NO_SEGMENTS;
          newQuery = null;
        }
        else if (!hasPath() && !base.hasPath())
        {
          // no paths: keep query only
          newAbsolutePath = false;
          newSegments = NO_SEGMENTS;
        }
        // exception if (!hasAbsolutePath())
        else if (hasCollapsableSegments(preserveRootParents))
        {
          // path form demands an absolute path: keep it and query
        }
        else
        {
          // keep query and select relative or absolute path based on length
          String[] rel = findRelativePath(base, preserveRootParents);
          if (anyRelPath || segments.length > rel.length)
          {
            // user demands a relative path or the absolute path is longer
            newAbsolutePath = false;
            newSegments = rel;
          }
          // else keep shorter absolute path
        }
      }
      // else keep device, path, and query
    }
    // else keep authority, device, path, and query

    // always include fragment, even if null;
    // no validation needed since all components are from existing URIs
    return new URI(true, null, newAuthority, newDevice, newAbsolutePath,
                   newSegments, newQuery, fragment);
  }

  // Returns true if the non-relative path includes segments that would be
  // collapsed when resolving; false otherwise.  If preserveRootParents is
  // true, collapsible segments include any empty segments, except for the
  // last segment, as well as and parent and self references.  If
  // preserveRootsParents is false, parent references are not collapsible if
  // they are the first segment or preceded only by other parent
  // references.
  private boolean hasCollapsableSegments(boolean preserveRootParents)
  {
    if (hasRelativePath())
    {
      throw new IllegalStateException("test collapsability of relative path");
    }

    for (int i = 0, len = segments.length; i < len; i++)
    {
      String segment = segments[i];
      if ((i < len - 1 && SEGMENT_EMPTY.equals(segment)) ||
          SEGMENT_SELF.equals(segment) ||
          SEGMENT_PARENT.equals(segment) && (
            !preserveRootParents || (
              i != 0 && !SEGMENT_PARENT.equals(segments[i - 1]))))
      {
        return true;
      }
    }
    return false;
  }

  // Returns the shortest relative path between the the non-relative path of
  // the given base and this absolute path.  If the base has no path, it is
  // treated as the root absolute path.
  private String[] findRelativePath(URI base, boolean preserveRootParents)
  {
    if (base.hasRelativePath())
    {
      throw new IllegalArgumentException(
        "find relative path against base with relative path");
    }
    if (!hasAbsolutePath())
    {
      throw new IllegalArgumentException(
        "find relative path of non-absolute path");
    }

    // treat an empty base path as the root absolute path
    String[] startPath = base.collapseSegments(preserveRootParents);
    String[] endPath = segments;

    // drop last segment from base, as in resolving
    int startCount = startPath.length > 0 ? startPath.length - 1 : 0;
    int endCount = endPath.length;

    // index of first segment that is different between endPath and startPath
    int diff = 0;

    // if endPath is shorter than startPath, the last segment of endPath may
    // not be compared: because startPath has been collapsed and had its
    // last segment removed, all preceding segments can be considered non-
    // empty and followed by a separator, while the last segment of endPath
    // will either be non-empty and not followed by a separator, or just empty
    for (int count = startCount < endCount ? startCount : endCount - 1;
         diff < count && startPath[diff].equals(endPath[diff]); diff++)
    {
      // Empty statement.
    }

    int upCount = startCount - diff;
    int downCount = endCount - diff;

    // a single separator, possibly preceded by some parent reference
    // segments, is redundant
    if (downCount == 1 && SEGMENT_EMPTY.equals(endPath[endCount - 1]))
    {
      downCount = 0;
    }

    // an empty path needs to be replaced by a single "." if there is no
    // query, to distinguish it from a current document reference
    if (upCount + downCount == 0)
    {
      if (query == null) return new String[] { SEGMENT_SELF };
      return NO_SEGMENTS;
    }

    // return a correctly sized result
    String[] result = new String[upCount + downCount];
    Arrays.fill(result, 0, upCount, SEGMENT_PARENT);
    System.arraycopy(endPath, diff, result, upCount, downCount);
    return result;
  }

  // Collapses non-ending empty segments, parent references, and self
  // references in a non-relative path, returning the same path that would
  // be produced from the base hierarchical URI as part of a resolve.
  String[] collapseSegments(boolean preserveRootParents)
  {
    if (hasRelativePath())
    {
      throw new IllegalStateException("collapse relative path");
    }

    if (!hasCollapsableSegments(preserveRootParents)) return segments();

    // use a stack to accumulate segments
    int segmentCount = segments.length;
    String[] stack = new String[segmentCount];
    int sp = 0;

    for (int i = 0; i < segmentCount; i++)
    {
      sp = accumulate(stack, sp, segments[i], preserveRootParents);
    }

    // if the path is non-empty and originally ended in an empty segment, a
    // parent reference, or a self reference, add a trailing separator
    if (sp > 0 && (SEGMENT_EMPTY.equals(segments[segmentCount - 1]) ||
                   SEGMENT_PARENT.equals(segments[segmentCount - 1]) ||
                   SEGMENT_SELF.equals(segments[segmentCount - 1])))
    {                   
      stack[sp++] = SEGMENT_EMPTY;
    }

    // return a correctly sized result
    String[] result = new String[sp];
    System.arraycopy(stack, 0, result, 0, sp);
    return result;
  }

  /**
   * Returns the string representation of this URI.  For a generic,
   * non-hierarchical URI, this looks like:
   * <pre>
   *   scheme:opaquePart#fragment</pre>
   * 
   * <p>For a hierarchical URI, it looks like:
   * <pre>
   *   scheme://authority/device/pathSegment1/pathSegment2...?query#fragment</pre>
   *
   * <p>For an <a href="#archive_explanation">archive URI</a>, it's just:
   * <pre>
   *   scheme:authority/pathSegment1/pathSegment2...?query#fragment</pre>
   * <p>Of course, absent components and their separators will be omitted.
   */
  @Override
  public String toString()
  {
    if (cachedToString == null)
    {
      StringBuffer result = new StringBuffer();
      if (!isRelative())
      {
        result.append(scheme);
        result.append(SCHEME_SEPARATOR);
      }

      if (isHierarchical())
      {
        if (hasAuthority())
        {
          if (!isArchive()) result.append(AUTHORITY_SEPARATOR);
          result.append(authority);
        }

        if (hasDevice())
        {
          result.append(SEGMENT_SEPARATOR);
          result.append(device);
        }

        if (hasAbsolutePath()) result.append(SEGMENT_SEPARATOR);

        for (int i = 0, len = segments.length; i < len; i++)
        {
          if (i != 0) result.append(SEGMENT_SEPARATOR);
          result.append(segments[i]);
        }

        if (hasQuery())
        {
          result.append(QUERY_SEPARATOR);
          result.append(query);
        }
      }
      else
      {
        result.append(authority);
      }

      if (hasFragment())
      {
        result.append(FRAGMENT_SEPARATOR);
        result.append(fragment);
      }
      cachedToString = result.toString();
    }
    return cachedToString;
  }

  // Returns a string representation of this URI for debugging, explicitly
  // showing each of the components.
  String toString(boolean includeSimpleForm)
  {
    StringBuffer result = new StringBuffer();
    if (includeSimpleForm) result.append(toString());
    result.append("\n hierarchical: ");
    result.append(isHierarchical());
    result.append("\n       scheme: ");
    result.append(scheme);
    result.append("\n    authority: ");
    result.append(authority);
    result.append("\n       device: ");
    result.append(device);
    result.append("\n absolutePath: ");
    result.append(hasAbsolutePath());
    result.append("\n     segments: ");
    if (segments.length == 0) result.append("<empty>");
    for (int i = 0, len = segments.length; i < len; i++)
    {
      if (i > 0) result.append("\n               ");
      result.append(segments[i]);
    }
    result.append("\n        query: ");
    result.append(query);
    result.append("\n     fragment: ");
    result.append(fragment);
    return result.toString();
  }

  /**
   * If this URI may refer directly to a locally accessible file, as
   * determined by {@link #isFile isFile}, {@link #decode decodes} and formats  
   * the URI as a pathname to that file; returns null otherwise.
   *
   * <p>If there is no authority, the format of this string is:
   * <pre>
   *   device/pathSegment1/pathSegment2...</pre>
   *
   * <p>If there is an authority, it is:
   * <pre>
   *   //authority/device/pathSegment1/pathSegment2...</pre>
   * 
   * <p>However, the character used as a separator is system-dependent and
   * obtained from {@link java.io.File#separatorChar}.
   */
  public String toFileString()
  {
    if (!isFile()) return null;

    StringBuffer result = new StringBuffer();
    char separator = '/';

    if (hasAuthority())
    {
      result.append(separator);
      result.append(separator);
      result.append(authority);

      if (hasDevice()) result.append(separator);
    }

    if (hasDevice()) result.append(device);
    if (hasAbsolutePath()) result.append(separator);

    for (int i = 0, len = segments.length; i < len; i++)
    {
      if (i != 0) result.append(separator);
      result.append(segments[i]);
    }

    return decode(result.toString());
  }
  
  /**
   * If this is a platform URI, as determined by {@link #isPlatform}, returns
   * the workspace-relative or plug-in-based path to the resource, optionally
   * {@link #decode decoding} the segments in the process.
   * @see #createPlatformResourceURI(String, boolean)
   * @see #createPlatformPluginURI
   * @since org.eclipse.emf.common 2.3
   */
  public String toPlatformString(boolean decode)
  {
    if (isPlatform())
    {
      StringBuffer result = new StringBuffer();
      for (int i = 1, len = segments.length; i < len; i++)
      {
        result.append('/').append(decode ? URI.decode(segments[i]) : segments[i]);
      }
      return result.toString();
    }
    return null;
  }

  /**
   * Returns the URI formed by appending the specified segment on to the end
   * of the path of this URI, if hierarchical; this URI unchanged,
   * otherwise.  If this URI has an authority and/or device, but no path,
   * the segment becomes the first under the root in an absolute path.
   *
   * @exception java.lang.IllegalArgumentException if <code>segment</code>
   * is not a valid segment according to {@link #validSegment}.
   */
  public URI appendSegment(String segment)
  {
    if (!validSegment(segment))
    {
      throw new IllegalArgumentException("invalid segment: " + segment);
    }

    if (!isHierarchical()) return this;

    // absolute path or no path -> absolute path
    boolean newAbsolutePath = !hasRelativePath();

    int len = segments.length;
    String[] newSegments = new String[len + 1];
    System.arraycopy(segments, 0, newSegments, 0, len);
    newSegments[len] = segment;

    return new URI(true, scheme, authority, device, newAbsolutePath,
                   newSegments, query, fragment);
  }

  /**
   * Returns the URI formed by appending the specified segments on to the
   * end of the path of this URI, if hierarchical; this URI unchanged,
   * otherwise.  If this URI has an authority and/or device, but no path,
   * the segments are made to form an absolute path.
   *
   * @param segments an array of non-null strings, each representing one
   * segment of the path.  If desired, a trailing separator should be
   * represented by an empty-string segment as the last element of the
   * array.
   *
   * @exception java.lang.IllegalArgumentException if <code>segments</code>
   * is not a valid segment array according to {@link #validSegments}.
   */
  public URI appendSegments(String[] segments)
  {
    if (!validSegments(segments))
    {
      String s = segments == null ? "invalid segments: null" :
        "invalid segment: " + firstInvalidSegment(segments);
      throw new IllegalArgumentException(s);
    }

    if (!isHierarchical()) return this;

    // absolute path or no path -> absolute path
    boolean newAbsolutePath = !hasRelativePath(); 

    int len = this.segments.length;
    int segmentsCount = segments.length;
    String[] newSegments = new String[len + segmentsCount];
    System.arraycopy(this.segments, 0, newSegments, 0, len);
    System.arraycopy(segments, 0, newSegments, len, segmentsCount);
    
    return new URI(true, scheme, authority, device, newAbsolutePath,
                   newSegments, query, fragment);
  }

  /**
   * Returns the URI formed by trimming the specified number of segments
   * (including empty segments, such as one representing a trailing
   * separator) from the end of the path of this URI, if hierarchical;
   * otherwise, this URI is returned unchanged.
   *
   * <p>Note that if all segments are trimmed from an absolute path, the
   * root absolute path remains.
   * 
   * @param i the number of segments to be trimmed in the returned URI.  If
   * less than 1, this URI is returned unchanged; if equal to or greater
   * than the number of segments in this URI's path, all segments are
   * trimmed.  
   */
  public URI trimSegments(int i)
  {
    if (!isHierarchical() || i < 1) return this;

    String[] newSegments = NO_SEGMENTS;
    int len = segments.length - i;
    if (len > 0)
    {
      newSegments = new String[len];
      System.arraycopy(segments, 0, newSegments, 0, len);
    }
    return new URI(true, scheme, authority, device, hasAbsolutePath(),
                   newSegments, query, fragment);
  }

  /**
   * Returns <code>true</code> if this is a hierarchical URI that has a path
   * that ends with a trailing separator; <code>false</code> otherwise.
   *
   * <p>A trailing separator is represented as an empty segment as the
   * last segment in the path; note that this definition does <em>not</em>
   * include the lone separator in the root absolute path.
   */
  public boolean hasTrailingPathSeparator()
  {
    return segments.length > 0 && 
      SEGMENT_EMPTY.equals(segments[segments.length - 1]);
  }

  /**
   * If this is a hierarchical URI whose path includes a file extension,
   * that file extension is returned; null otherwise.  We define a file
   * extension as any string following the last period (".") in the final
   * path segment.  If there is no path, the path ends in a trailing
   * separator, or the final segment contains no period, then we consider
   * there to be no file extension.  If the final segment ends in a period,
   * then the file extension is an empty string.
   */
  public String fileExtension()
  {
    int len = segments.length;
    if (len == 0) return null;

    String lastSegment = segments[len - 1];
    int i = lastSegment.lastIndexOf(FILE_EXTENSION_SEPARATOR);
    return i < 0 ? null : lastSegment.substring(i + 1);
  }

  /**
   * Returns the URI formed by appending a period (".") followed by the
   * specified file extension to the last path segment of this URI, if it is
   * hierarchical with a non-empty path ending in a non-empty segment;
   * otherwise, this URI is returned unchanged.

   * <p>The extension is appended regardless of whether the segment already
   * contains an extension.
   *
   * @exception java.lang.IllegalArgumentException if
   * <code>fileExtension</code> is not a valid segment (portion) according
   * to {@link #validSegment}.
   */
  public URI appendFileExtension(String fileExtension)
  {
    if (!validSegment(fileExtension))
    {
      throw new IllegalArgumentException(
        "invalid segment portion: " + fileExtension);
    }

    int len = segments.length;
    if (len == 0) return this;

    String lastSegment = segments[len - 1];
    if (SEGMENT_EMPTY.equals(lastSegment)) return this;
    StringBuffer newLastSegment = new StringBuffer(lastSegment);
    newLastSegment.append(FILE_EXTENSION_SEPARATOR);
    newLastSegment.append(fileExtension);

    String[] newSegments = new String[len];
    System.arraycopy(segments, 0, newSegments, 0, len - 1);
    newSegments[len - 1] = newLastSegment.toString();
    
    // note: segments.length > 0 -> hierarchical
    return new URI(true, scheme, authority, device, hasAbsolutePath(),
                   newSegments, query, fragment); 
  }

  /**
   * If this URI has a non-null {@link #fileExtension fileExtension},
   * returns the URI formed by removing it; this URI unchanged, otherwise.
   */
  public URI trimFileExtension()
  {
    int len = segments.length;
    if (len == 0) return this;

    String lastSegment = segments[len - 1];
    int i = lastSegment.lastIndexOf(FILE_EXTENSION_SEPARATOR);
    if (i < 0) return this;

    String newLastSegment = lastSegment.substring(0, i);
    String[] newSegments = new String[len];
    System.arraycopy(segments, 0, newSegments, 0, len - 1);
    newSegments[len - 1] = newLastSegment;

    // note: segments.length > 0 -> hierarchical
    return new URI(true, scheme, authority, device, hasAbsolutePath(),
                   newSegments, query, fragment); 
  }

  /**
   * Returns <code>true</code> if this is a hierarchical URI that ends in a
   * slash; that is, it has a trailing path separator or is the root
   * absolute path, and has no query and no fragment; <code>false</code>
   * is returned otherwise.
   */
  public boolean isPrefix()
  {
    return isHierarchical() && query == null && fragment == null &&
      (hasTrailingPathSeparator() || (hasAbsolutePath() && segments.length == 0));
  }

  /**
   * If this is a hierarchical URI reference and <code>oldPrefix</code> is a
   * prefix of it, this returns the URI formed by replacing it by
   * <code>newPrefix</code>; <code>null</code> otherwise.
   *
   * <p>In order to be a prefix, the <code>oldPrefix</code>'s
   * {@link #isPrefix isPrefix} must return <code>true</code>, and it must
   * match this URI's scheme, authority, and device.  Also, the paths must
   * match, up to prefix's end.
   *
   * @exception java.lang.IllegalArgumentException if either
   * <code>oldPrefix</code> or <code>newPrefix</code> is not a prefix URI
   * according to {@link #isPrefix}.
   */
  public URI replacePrefix(URI oldPrefix, URI newPrefix)
  {
    if (!oldPrefix.isPrefix() || !newPrefix.isPrefix())
    {
      String which = oldPrefix.isPrefix() ? "new" : "old";
      throw new IllegalArgumentException("non-prefix " + which + " value");
    }

    // Get what's left of the segments after trimming the prefix.
    String[] tailSegments = getTailSegments(oldPrefix);
    if (tailSegments == null) return null;

    // If the new prefix has segments, it is not the root absolute path,
    // and we need to drop the trailing empty segment and append the tail
    // segments.
    String[] mergedSegments = tailSegments;
    if (newPrefix.segmentCount() != 0)
    {
      int segmentsToKeep = newPrefix.segmentCount() - 1;
      mergedSegments = new String[segmentsToKeep + tailSegments.length];
      System.arraycopy(newPrefix.segments(), 0, mergedSegments, 0,
                       segmentsToKeep);

      if (tailSegments.length != 0)
      {
        System.arraycopy(tailSegments, 0, mergedSegments, segmentsToKeep,
                         tailSegments.length);
      }
    }

    // no validation needed since all components are from existing URIs
    return new URI(true, newPrefix.scheme(), newPrefix.authority(),
                   newPrefix.device(), newPrefix.hasAbsolutePath(),
                   mergedSegments, query, fragment);
  }

  // If this is a hierarchical URI reference and prefix is a prefix of it,
  // returns the portion of the path remaining after that prefix has been
  // trimmed; null otherwise.
  private String[] getTailSegments(URI prefix)
  {
    if (!prefix.isPrefix())
    {
      throw new IllegalArgumentException("non-prefix trim");
    }

    // Don't even consider it unless this is hierarchical and has scheme,
    // authority, device and path absoluteness equal to those of the prefix.
    if (!isHierarchical() ||
        !equals(scheme, prefix.scheme(), true) ||
        !equals(authority, prefix.authority()) ||
        !equals(device, prefix.device()) ||
        hasAbsolutePath() != prefix.hasAbsolutePath())
    {
      return null;
    }

    // If the prefix has no segments, then it is the root absolute path, and
    // we know this is an absolute path, too.
    if (prefix.segmentCount() == 0) return segments;

    // This must have no fewer segments than the prefix.  Since the prefix
    // is not the root absolute path, its last segment is empty; all others
    // must match.
    int i = 0;
    int segmentsToCompare = prefix.segmentCount() - 1;
    if (segments.length <= segmentsToCompare) return null;

    for (; i < segmentsToCompare; i++)
    {
      if (!segments[i].equals(prefix.segment(i))) return null;
    }

    // The prefix really is a prefix of this.  If this has just one more,
    // empty segment, the paths are the same.
    if (i == segments.length - 1 && SEGMENT_EMPTY.equals(segments[i]))
    {
      return NO_SEGMENTS;
    }
    
    // Otherwise, the path needs only the remaining segments.
    String[] newSegments = new String[segments.length - i];
    System.arraycopy(segments, i, newSegments, 0, newSegments.length);
    return newSegments;
  }

  /**
   * Encodes a string so as to produce a valid opaque part value, as defined
   * by the RFC.  All excluded characters, such as space and <code>#</code>,
   * are escaped, as is <code>/</code> if it is the first character.
   * 
   * @param ignoreEscaped <code>true</code> to leave <code>%</code> characters
   * unescaped if they already begin a valid three-character escape sequence;
   * <code>false</code> to encode all <code>%</code> characters.  Note that
   * if a <code>%</code> is not followed by 2 hex digits, it will always be
   * escaped. 
   */
  public static String encodeOpaquePart(String value, boolean ignoreEscaped)
  {
    String result = encode(value, URIC_HI, URIC_LO, ignoreEscaped);
    return result != null && result.length() > 0 && result.charAt(0) == SEGMENT_SEPARATOR ?
      "%2F" + result.substring(1) :
      result;
  }

  /**
   * Encodes a string so as to produce a valid authority, as defined by the
   * RFC.  All excluded characters, such as space and <code>#</code>,
   * are escaped, as are <code>/</code> and <code>?</code>
   * 
   * @param ignoreEscaped <code>true</code> to leave <code>%</code> characters
   * unescaped if they already begin a valid three-character escape sequence;
   * <code>false</code> to encode all <code>%</code> characters.  Note that
   * if a <code>%</code> is not followed by 2 hex digits, it will always be
   * escaped. 
   */
  public static String encodeAuthority(String value, boolean ignoreEscaped)
  {
    return encode(value, SEGMENT_CHAR_HI, SEGMENT_CHAR_LO, ignoreEscaped);
  }

  /**
   * Encodes a string so as to produce a valid segment, as defined by the
   * RFC.  All excluded characters, such as space and <code>#</code>,
   * are escaped, as are <code>/</code> and <code>?</code>
   * 
   * @param ignoreEscaped <code>true</code> to leave <code>%</code> characters
   * unescaped if they already begin a valid three-character escape sequence;
   * <code>false</code> to encode all <code>%</code> characters.  Note that
   * if a <code>%</code> is not followed by 2 hex digits, it will always be
   * escaped. 
   */
  public static String encodeSegment(String value, boolean ignoreEscaped)
  {
    return encode(value, SEGMENT_CHAR_HI, SEGMENT_CHAR_LO, ignoreEscaped);
  }

  /**
   * Encodes a string so as to produce a valid query, as defined by the RFC.
   * Only excluded characters, such as space and <code>#</code>, are escaped.
   * 
   * @param ignoreEscaped <code>true</code> to leave <code>%</code> characters
   * unescaped if they already begin a valid three-character escape sequence;
   * <code>false</code> to encode all <code>%</code> characters.  Note that
   * if a <code>%</code> is not followed by 2 hex digits, it will always be
   * escaped. 
   */
  public static String encodeQuery(String value, boolean ignoreEscaped)
  {
    return encode(value, URIC_HI, URIC_LO, ignoreEscaped);
  }

  /**
   * Encodes a string so as to produce a valid fragment, as defined by the
   * RFC.  Only excluded characters, such as space and <code>#</code>, are
   * escaped.
   * 
   * @param ignoreEscaped <code>true</code> to leave <code>%</code> characters
   * unescaped if they already begin a valid three-character escape sequence;
   * <code>false</code> to encode all <code>%</code> characters.  Note that
   * if a <code>%</code> is not followed by 2 hex digits, it will always be
   * escaped. 
   */
  public static String encodeFragment(String value, boolean ignoreEscaped)
  {
    return encode(value, URIC_HI, URIC_LO, ignoreEscaped);
  }

  // Encodes a complete URI, optionally leaving % characters unescaped when
  // beginning a valid three-character escape sequence.  We can either treat
  // the first or # as a fragment separator, or encode them all.
  private static String encodeURI(String uri, boolean ignoreEscaped, int fragmentLocationStyle)
  {
    if (uri == null) return null;

    StringBuffer result = new StringBuffer();

    int i = uri.indexOf(SCHEME_SEPARATOR);
    if (i != -1)
    {
      String scheme = uri.substring(0, i);
      result.append(scheme);
      result.append(SCHEME_SEPARATOR);
    }
    
    int j =
      fragmentLocationStyle == FRAGMENT_FIRST_SEPARATOR ? uri.indexOf(FRAGMENT_SEPARATOR) :
        fragmentLocationStyle == FRAGMENT_LAST_SEPARATOR ? uri.lastIndexOf(FRAGMENT_SEPARATOR) : -1;

    if (j != -1)
    {
      String sspart = uri.substring(++i, j);
      result.append(encode(sspart, URIC_HI, URIC_LO, ignoreEscaped));
      result.append(FRAGMENT_SEPARATOR);

      String fragment = uri.substring(++j);
      result.append(encode(fragment, URIC_HI, URIC_LO, ignoreEscaped));
    }
    else
    {
      String sspart = uri.substring(++i);
      result.append(encode(sspart, URIC_HI, URIC_LO, ignoreEscaped));
    }
    
    return result.toString();
  }

  // Encodes the given string, replacing each ASCII character that is not in
  // the set specified by the 128-bit bitmask and each non-ASCII character
  // below 0xA0 by an escape sequence of % followed by two hex digits.  If
  // % is not in the set but ignoreEscaped is true, then % will not be encoded
  // iff it already begins a valid escape sequence.
  private static String encode(String value, long highBitmask, long lowBitmask, boolean ignoreEscaped)
  {
    if (value == null) return null;

    StringBuffer result = null;

    for (int i = 0, len = value.length(); i < len; i++)
    {
      char c = value.charAt(i);

      if (!matches(c, highBitmask, lowBitmask) && c < 160 &&
          (!ignoreEscaped || !isEscaped(value, i)))
      {
        if (result == null)
        {
          result = new StringBuffer(value.substring(0, i));
        }
        appendEscaped(result, (byte)c);
      }
      else if (result != null)
      {
        result.append(c);
      }
    }
    return result == null ? value : result.toString();
  }

  // Tests whether an escape occurs in the given string, starting at index i.
  // An escape sequence is a % followed by two hex digits.
  private static boolean isEscaped(String s, int i)
  {
    return s.charAt(i) == ESCAPE && s.length() > i + 2 &&
      matches(s.charAt(i + 1), HEX_HI, HEX_LO) &&
      matches(s.charAt(i + 2), HEX_HI, HEX_LO);
  }

  // Computes a three-character escape sequence for the byte, appending
  // it to the StringBuffer.  Only characters up to 0xFF should be escaped;
  // all but the least significant byte will be ignored.
  private static void appendEscaped(StringBuffer result, byte b)
  {
    result.append(ESCAPE);

    // The byte is automatically widened into an int, with sign extension,
    // for shifting.  This can introduce 1's to the left of the byte, which
    // must be cleared by masking before looking up the hex digit.
    //
    result.append(HEX_DIGITS[(b >> 4) & 0x0F]);
    result.append(HEX_DIGITS[b & 0x0F]);
  }

  /**
   * Decodes the given string by interpreting three-digit escape sequences as the bytes of a UTF-8 encoded character
   * and replacing them with the characters they represent.
   * Incomplete escape sequences are ignored and invalid UTF-8 encoded bytes are treated as extended ASCII characters.
   */
  public static String decode(String value)
  {
    if (value == null) return null;

    int i = value.indexOf('%');
    if (i < 0)
    {
      return value;
    }
    else
    {
      StringBuilder result = new StringBuilder(value.substring(0, i));
      byte [] bytes = new byte[4];
      int receivedBytes = 0;
      int expectedBytes = 0;
      for (int len = value.length(); i < len; i++)
      {
        if (isEscaped(value, i)) 
        {
          char character = unescape(value.charAt(i + 1), value.charAt(i + 2));
          i += 2;
          
          if (expectedBytes > 0)
          {
            if ((character & 0xC0) == 0x80)
            {
              bytes[receivedBytes++] = (byte)character;
            }
            else
            {
              expectedBytes = 0;
            }
          }
          else if (character >= 0x80)
          {
            if ((character & 0xE0) == 0xC0)
            {
              bytes[receivedBytes++] = (byte)character;
              expectedBytes = 2;
            }
            else if ((character & 0xF0) == 0xE0)
            {
              bytes[receivedBytes++] = (byte)character;
              expectedBytes = 3;
            }
            else if ((character & 0xF8) == 0xF0)
            {
              bytes[receivedBytes++] = (byte)character;
              expectedBytes = 4;
            }
          }

          if (expectedBytes > 0)
          {
            if (receivedBytes == expectedBytes)
            {
              switch (receivedBytes)
              {
                case 2:
                {
                  result.append((char)((bytes[0] & 0x1F) << 6 | bytes[1] & 0x3F));
                  break;
                }
                case 3:
                {
                  result.append((char)((bytes[0] & 0xF) << 12 | (bytes[1] & 0X3F) << 6 | bytes[2] & 0x3F));
                  break;
                }
                case 4:
                {
                  // TODO
                  // result.appendCodePoint(((bytes[0] & 0x7) << 18 | (bytes[1] & 0X3F) << 12 | (bytes[2] & 0X3F) << 6 | bytes[3] & 0x3F));
                  break;
                }
              }
              receivedBytes = 0;
              expectedBytes = 0;
            }
          }
          else
          {
            for (int j = 0; j < receivedBytes; ++j)
            {
              result.append((char)bytes[j]);
            }
            receivedBytes = 0;
            result.append(character);
          }
        }
        else
        {
          for (int j = 0; j < receivedBytes; ++j)
          {
            result.append((char)bytes[j]);
          }
          receivedBytes = 0;
          result.append(value.charAt(i));
        }
      }
      return result.toString();
    }
  }

  // Returns the character encoded by % followed by the two given hex digits,
  // which is always 0xFF or less, so can safely be casted to a byte.  If
  // either character is not a hex digit, a bogus result will be returned.
  private static char unescape(char highHexDigit, char lowHexDigit)
  {
    return (char)((valueOf(highHexDigit) << 4) | valueOf(lowHexDigit));
  }

  // Returns the int value of the given hex digit.
  private static int valueOf(char hexDigit)
  {
    if (hexDigit >= 'A' && hexDigit <= 'F')
    {
      return hexDigit - 'A' + 10;
    }
    if (hexDigit >= 'a' && hexDigit <= 'f')
    {
      return hexDigit - 'a' + 10;
    }
    if (hexDigit >= '0' && hexDigit <= '9')
    {
      return hexDigit - '0';
    }
    return 0;
  }

  /*
   * Returns <code>true</code> if this URI contains non-ASCII characters;
   * <code>false</code> otherwise.
   *
   * This unused code is included for possible future use... 
   */
/*
  public boolean isIRI()
  {
    return iri; 
  }

  // Returns true if the given string contains any non-ASCII characters;
  // false otherwise.
  private static boolean containsNonASCII(String value)
  {
    for (int i = 0, length = value.length(); i < length; i++)
    {
      if (value.charAt(i) > 127) return true;
    }
    return false;
  }
*/

  /*
   * If this is an {@link #isIRI IRI}, converts it to a strict ASCII URI,
   * using the procedure described in Section 3.1 of the
   * <a href="http://www.w3.org/International/iri-edit/draft-duerst-iri-09.txt">IRI
   * Draft RFC</a>.  Otherwise, this URI, itself, is returned.
   *
   * This unused code is included for possible future use...
   */
/*
  public URI toASCIIURI()
  {
    if (!iri) return this;

    if (cachedASCIIURI == null)
    {
      String eAuthority = encodeAsASCII(authority);
      String eDevice = encodeAsASCII(device);
      String eQuery = encodeAsASCII(query);
      String eFragment = encodeAsASCII(fragment);
      String[] eSegments = new String[segments.length];
      for (int i = 0; i < segments.length; i++)
      {
        eSegments[i] = encodeAsASCII(segments[i]);
      }
      cachedASCIIURI = new URI(hierarchical, scheme, eAuthority, eDevice, absolutePath, eSegments, eQuery, eFragment); 

    }
    return cachedASCIIURI;
  }

  // Returns a strict ASCII encoding of the given value.  Each non-ASCII
  // character is converted to bytes using UTF-8 encoding, which are then
  // represented using % escaping.
  private String encodeAsASCII(String value)
  {
    if (value == null) return null;

    StringBuffer result = null;

    for (int i = 0, length = value.length(); i < length; i++)
    {
      char c = value.charAt(i);

      if (c >= 128)
      {
        if (result == null)
        {
          result = new StringBuffer(value.substring(0, i));
        }

        try
        {
          byte[] encoded = (new String(new char[] { c })).getBytes("UTF-8");
          for (int j = 0, encLen = encoded.length; j < encLen; j++)
          {
            appendEscaped(result, encoded[j]);
          }
        }
        catch (UnsupportedEncodingException e)
        {
          throw new WrappedException(e);
        }
      }
      else if (result != null)
      {
        result.append(c);
      }

    }
    return result == null ? value : result.toString();
  }

  // Returns the number of valid, consecutive, three-character escape
  // sequences in the given string, starting at index i.
  private static int countEscaped(String s, int i)
  {
    int result = 0;

    for (int length = s.length(); i < length; i += 3)
    {
      if (isEscaped(s, i)) result++;
    }
    return result;
  }
*/
}
