/**
 * Copyright (c) 2004-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *
 * ---------------------------------------------------------------------
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999-2003, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.eclipse.emf.ecore.xml.type.internal;


import java.util.HashMap;
import java.util.Vector;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * NOTE: this class is for internal use only.
 */
public final class RegEx
{
  // TODO
  public static class CharacterIterator
  {
    public char setIndex(int index)
    {
      return ' ';
    }
    
    public int getBeginIndex()
    {
      return -1;
    }
    
    public int getEndIndex()
    {
      return -1;
    }
  }
  static class BMPattern
  {
    char[] pattern;

    int[] shiftTable;

    boolean ignoreCase;

    public BMPattern(String pat, boolean ignoreCase)
    {
      this(pat, 256, ignoreCase);
    }

    public BMPattern(String pat, int tableSize, boolean ignoreCase)
    {
      this.pattern = pat.toCharArray();
      this.shiftTable = new int [tableSize];
      this.ignoreCase = ignoreCase;
      int length = pattern.length;
      for (int i = 0; i < this.shiftTable.length; i++)
        this.shiftTable[i] = length;
      for (int i = 0; i < length; i++)
      {
        char ch = this.pattern[i];
        int diff = length - i - 1;
        int index = ch % this.shiftTable.length;
        if (diff < this.shiftTable[index])
          this.shiftTable[index] = diff;
        if (this.ignoreCase)
        {
          ch = Character.toUpperCase(ch);
          index = ch % this.shiftTable.length;
          if (diff < this.shiftTable[index])
            this.shiftTable[index] = diff;
          ch = Character.toLowerCase(ch);
          index = ch % this.shiftTable.length;
          if (diff < this.shiftTable[index])
            this.shiftTable[index] = diff;
        }
      }
    }

    /**
     *
     * @return -1 if <var>iterator</var> does not contain this pattern.
     */
    public int matches(CharacterIterator iterator, int start, int limit)
    {
      if (this.ignoreCase)
        return this.matchesIgnoreCase(iterator, start, limit);
      int plength = this.pattern.length;
      if (plength == 0)
        return start;
      int index = start + plength;
      while (index <= limit)
      {
        int pindex = plength;
        int nindex = index + 1;
        char ch;
        do
        {
          if ((ch = iterator.setIndex(--index)) != this.pattern[--pindex])
            break;
          if (pindex == 0)
            return index;
        }
        while (pindex > 0);
        index += this.shiftTable[ch % this.shiftTable.length] + 1;
        if (index < nindex)
          index = nindex;
      }
      return -1;
    }

    /**
     *
     * @return -1 if <var>str</var> does not contain this pattern.
     */
    public int matches(String str, int start, int limit)
    {
      if (this.ignoreCase)
        return this.matchesIgnoreCase(str, start, limit);
      int plength = this.pattern.length;
      if (plength == 0)
        return start;
      int index = start + plength;
      while (index <= limit)
      {
        //System.err.println("Starts at "+index);
        int pindex = plength;
        int nindex = index + 1;
        char ch;
        do
        {
          if ((ch = str.charAt(--index)) != this.pattern[--pindex])
            break;
          if (pindex == 0)
            return index;
        }
        while (pindex > 0);
        index += this.shiftTable[ch % this.shiftTable.length] + 1;
        if (index < nindex)
          index = nindex;
      }
      return -1;
    }

    /**
     *
     * @return -1 if <var>chars</char> does not contain this pattern.
     */
    public int matches(char[] chars, int start, int limit)
    {
      if (this.ignoreCase)
        return this.matchesIgnoreCase(chars, start, limit);
      int plength = this.pattern.length;
      if (plength == 0)
        return start;
      int index = start + plength;
      while (index <= limit)
      {
        //System.err.println("Starts at "+index);
        int pindex = plength;
        int nindex = index + 1;
        char ch;
        do
        {
          if ((ch = chars[--index]) != this.pattern[--pindex])
            break;
          if (pindex == 0)
            return index;
        }
        while (pindex > 0);
        index += this.shiftTable[ch % this.shiftTable.length] + 1;
        if (index < nindex)
          index = nindex;
      }
      return -1;
    }

    int matchesIgnoreCase(CharacterIterator iterator, int start, int limit)
    {
      int plength = this.pattern.length;
      if (plength == 0)
        return start;
      int index = start + plength;
      while (index <= limit)
      {
        int pindex = plength;
        int nindex = index + 1;
        char ch;
        do
        {
          char ch1 = ch = iterator.setIndex(--index);
          char ch2 = this.pattern[--pindex];
          if (ch1 != ch2)
          {
            ch1 = Character.toUpperCase(ch1);
            ch2 = Character.toUpperCase(ch2);
            if (ch1 != ch2 && Character.toLowerCase(ch1) != Character.toLowerCase(ch2))
              break;
          }
          if (pindex == 0)
            return index;
        }
        while (pindex > 0);
        index += this.shiftTable[ch % this.shiftTable.length] + 1;
        if (index < nindex)
          index = nindex;
      }
      return -1;
    }

    int matchesIgnoreCase(String text, int start, int limit)
    {
      int plength = this.pattern.length;
      if (plength == 0)
        return start;
      int index = start + plength;
      while (index <= limit)
      {
        int pindex = plength;
        int nindex = index + 1;
        char ch;
        do
        {
          char ch1 = ch = text.charAt(--index);
          char ch2 = this.pattern[--pindex];
          if (ch1 != ch2)
          {
            ch1 = Character.toUpperCase(ch1);
            ch2 = Character.toUpperCase(ch2);
            if (ch1 != ch2 && Character.toLowerCase(ch1) != Character.toLowerCase(ch2))
              break;
          }
          if (pindex == 0)
            return index;
        }
        while (pindex > 0);
        index += this.shiftTable[ch % this.shiftTable.length] + 1;
        if (index < nindex)
          index = nindex;
      }
      return -1;
    }

    int matchesIgnoreCase(char[] chars, int start, int limit)
    {
      int plength = this.pattern.length;
      if (plength == 0)
        return start;
      int index = start + plength;
      while (index <= limit)
      {
        int pindex = plength;
        int nindex = index + 1;
        char ch;
        do
        {
          char ch1 = ch = chars[--index];
          char ch2 = this.pattern[--pindex];
          if (ch1 != ch2)
          {
            ch1 = Character.toUpperCase(ch1);
            ch2 = Character.toUpperCase(ch2);
            if (ch1 != ch2 && Character.toLowerCase(ch1) != Character.toLowerCase(ch2))
              break;
          }
          if (pindex == 0)
            return index;
        }
        while (pindex > 0);
        index += this.shiftTable[ch % this.shiftTable.length] + 1;
        if (index < nindex)
          index = nindex;
      }
      return -1;
    }
    /*
     public static void main(String[] argv) {
     try {
     int[] shiftTable = new int[256];
     initializeBoyerMoore(argv[0], shiftTable, true);
     int o = -1;
     CharacterIterator ite = new java.text.StringCharacterIterator(argv[1]);
     long start = System.currentTimeMillis();
     //for (int i = 0;  i < 10000;  i ++)
     o = searchIgnoreCasesWithBoyerMoore(ite, 0, argv[0], shiftTable);
     start = System.currentTimeMillis()-start;
     System.out.println("Result: "+o+", Elapsed: "+start);
     } catch (Exception ex) {
     ex.printStackTrace();
     }
     }*/
  }
  
  public static class Match implements Cloneable {
    int[] beginpos = null;
    int[] endpos = null;
    int nofgroups = 0;

    CharacterIterator ciSource = null;
    String strSource = null;
    char[] charSource = null;

    /**
     * Creates an instance.
     */
    public Match() {
      super();
    }

    /**
     *
     */
    protected void setNumberOfGroups(int n) {
        int oldn = this.nofgroups;
        this.nofgroups = n;
        if (oldn <= 0
            || oldn < n || n*2 < oldn) {
            this.beginpos = new int[n];
            this.endpos = new int[n];
        }
        for (int i = 0;  i < n;  i ++) {
            this.beginpos[i] = -1;
            this.endpos[i] = -1;
        }
    }

    /**
     *
     */
    protected void setSource(CharacterIterator ci) {
        this.ciSource = ci;
        this.strSource = null;
        this.charSource = null;
    }
    /**
     *
     */
    protected void setSource(String str) {
        this.ciSource = null;
        this.strSource = str;
        this.charSource = null;
    }
    /**
     *
     */
    protected void setSource(char[] chars) {
        this.ciSource = null;
        this.strSource = null;
        this.charSource = chars;
    }

    /**
     *
     */
    protected void setBeginning(int index, int v) {
        this.beginpos[index] = v;
    }

    /**
     *
     */
    protected void setEnd(int index, int v) {
        this.endpos[index] = v;
    }

    /**
     * Return the number of regular expression groups.
     * This method returns 1 when the regular expression has no capturing-parenthesis.
     */
    public int getNumberOfGroups() {
        if (this.nofgroups <= 0)
            throw new IllegalStateException("A result is not set.");
        return this.nofgroups;
    }

    /**
     * Return a start position in the target text matched to specified regular expression group.
     *
     * @param index Less than <code>getNumberOfGroups()</code>.
     */
    public int getBeginning(int index) {
        if (this.beginpos == null)
            throw new IllegalStateException("A result is not set.");
        if (index < 0 || this.nofgroups <= index)
            throw new IllegalArgumentException("The parameter must be less than "
                                               +this.nofgroups+": "+index);
        return this.beginpos[index];
    }

    /**
     * Return an end position in the target text matched to specified regular expression group.
     *
     * @param index Less than <code>getNumberOfGroups()</code>.
     */
    public int getEnd(int index) {
        if (this.endpos == null)
            throw new IllegalStateException("A result is not set.");
        if (index < 0 || this.nofgroups <= index)
            throw new IllegalArgumentException("The parameter must be less than "
                                               +this.nofgroups+": "+index);
        return this.endpos[index];
    }

    /**
     * Return an substring of the target text matched to specified regular expression group.
     *
     * @param index Less than <code>getNumberOfGroups()</code>.
     */
    public String getCapturedText(int index) {
        if (this.beginpos == null)
            throw new IllegalStateException("match() has never been called.");
        if (index < 0 || this.nofgroups <= index)
            throw new IllegalArgumentException("The parameter must be less than "
                                               +this.nofgroups+": "+index);
        String ret;
        int begin = this.beginpos[index], end = this.endpos[index];
        if (begin < 0 || end < 0)  return null;
        if (this.ciSource != null) {
            ret = REUtil.substring(this.ciSource, begin, end);
        } else if (this.strSource != null) {
            ret = this.strSource.substring(begin, end);
        } else {
            ret = new String(this.charSource, begin, end-begin);
        }
        return ret;
    }
  }
  
  public final static class REUtil {
    private REUtil() {
      super();
    }

    static final int composeFromSurrogates(int high, int low) {
        return 0x10000 + ((high-0xd800)<<10) + low-0xdc00;
    }

    static final boolean isLowSurrogate(int ch) {
        return (ch & 0xfc00) == 0xdc00;
    }

    static final boolean isHighSurrogate(int ch) {
        return (ch & 0xfc00) == 0xd800;
    }

    static final String decomposeToSurrogates(int ch) {
        char[] chs = new char[2];
        ch -= 0x10000;
        chs[0] = (char)((ch>>10)+0xd800);
        chs[1] = (char)((ch&0x3ff)+0xdc00);
        return new String(chs);
    }

    static final String substring(CharacterIterator iterator, int begin, int end) {
        char[] src = new char[end-begin];
        for (int i = 0;  i < src.length;  i ++)
            src[i] = iterator.setIndex(i+begin);
        return new String(src);
    }

    // ================================================================

    static final int getOptionValue(int ch) {
        int ret = 0;
        switch (ch) {
          case 'i':
            ret = RegularExpression.IGNORE_CASE;
            break;
          case 'm':
            ret = RegularExpression.MULTIPLE_LINES;
            break;
          case 's':
            ret = RegularExpression.SINGLE_LINE;
            break;
          case 'x':
            ret = RegularExpression.EXTENDED_COMMENT;
            break;
          case 'u':
            ret = RegularExpression.USE_UNICODE_CATEGORY;
            break;
          case 'w':
            ret = RegularExpression.UNICODE_WORD_BOUNDARY;
            break;
          case 'F':
            ret = RegularExpression.PROHIBIT_FIXED_STRING_OPTIMIZATION;
            break;
          case 'H':
            ret = RegularExpression.PROHIBIT_HEAD_CHARACTER_OPTIMIZATION;
            break;
          case 'X':
            ret = RegularExpression.XMLSCHEMA_MODE;
            break;
          case ',':
            ret = RegularExpression.SPECIAL_COMMA;
            break;
          default:
        }
        return ret;
    }

    static final int parseOptions(String opts) throws ParseException {
        if (opts == null)  return 0;
        int options = 0;
        for (int i = 0;  i < opts.length();  i ++) {
            int v = getOptionValue(opts.charAt(i));
            if (v == 0)
                throw new ParseException("Unknown Option: "+opts.substring(i), -1);
            options |= v;
        }
        return options;
    }

    static final String createOptionString(int options) {
        StringBuffer sb = new StringBuffer(9);
        if ((options & RegularExpression.PROHIBIT_FIXED_STRING_OPTIMIZATION) != 0)
            sb.append('F');
        if ((options & RegularExpression.PROHIBIT_HEAD_CHARACTER_OPTIMIZATION) != 0)
            sb.append('H');
        if ((options & RegularExpression.XMLSCHEMA_MODE) != 0)
            sb.append('X');
        if ((options & RegularExpression.IGNORE_CASE) != 0)
            sb.append('i');
        if ((options & RegularExpression.MULTIPLE_LINES) != 0)
            sb.append('m');
        if ((options & RegularExpression.SINGLE_LINE) != 0)
            sb.append('s');
        if ((options & RegularExpression.USE_UNICODE_CATEGORY) != 0)
            sb.append('u');
        if ((options & RegularExpression.UNICODE_WORD_BOUNDARY) != 0)
            sb.append('w');
        if ((options & RegularExpression.EXTENDED_COMMENT) != 0)
            sb.append('x');
        if ((options & RegularExpression.SPECIAL_COMMA) != 0)
            sb.append(',');
        return sb.toString().intern();
    }

    // ================================================================

    static String stripExtendedComment(String regex) {
        int len = regex.length();
        StringBuffer buffer = new StringBuffer(len);
        int offset = 0;
        while (offset < len) {
            int ch = regex.charAt(offset++);
                                                // Skips a white space.
            if (ch == '\t' || ch == '\n' || ch == '\f' || ch == '\r' || ch == ' ')
                continue;

            if (ch == '#') {                    // Skips characters between '#' and a line end.
                while (offset < len) {
                    ch = regex.charAt(offset++);
                    if (ch == '\r' || ch == '\n')
                        break;
                }
                continue;
            }

            int next;                           // Strips an escaped white space.
            if (ch == '\\' && offset < len) {
                if ((next = regex.charAt(offset)) == '#'
                    || next == '\t' || next == '\n' || next == '\f'
                    || next == '\r' || next == ' ') {
                    buffer.append((char)next);
                    offset ++;
                } else {                        // Other escaped character.
                    buffer.append('\\');
                    buffer.append((char)next);
                    offset ++;
                }
            } else                              // As is.
                buffer.append((char)ch);
        }
        return buffer.toString();
    }

    // ================================================================

    static final int CACHESIZE = 20;
    static final RegularExpression[] regexCache = new RegularExpression[CACHESIZE];
    /**
     * Creates a RegularExpression instance.
     * This method caches created instances.
     *
     * @see RegularExpression#RegularExpression(String, String)
     */
    public static RegularExpression createRegex(String pattern, String options)
        throws ParseException {
        RegularExpression re = null;
        int intOptions = REUtil.parseOptions(options);
        synchronized (REUtil.regexCache) {
            int i;
            for (i = 0;  i < REUtil.CACHESIZE;  i ++) {
                RegularExpression cached = REUtil.regexCache[i];
                if (cached == null) {
                    i = -1;
                    break;
                }
                if (cached.equals(pattern, intOptions)) {
                    re = cached;
                    break;
                }
            }
            if (re != null) {
                if (i != 0) {
                    System.arraycopy(REUtil.regexCache, 0, REUtil.regexCache, 1, i);
                    REUtil.regexCache[0] = re;
                }
            } else {
                re = new RegularExpression(pattern, options);
                System.arraycopy(REUtil.regexCache, 0, REUtil.regexCache, 1, REUtil.CACHESIZE-1);
                REUtil.regexCache[0] = re;
            }
        }
        return re;
    }

    /**
     *
     * @see RegularExpression#matches(String)
     */
    public static boolean matches(String regex, String target) throws ParseException {
        return REUtil.createRegex(regex, null).matches(target);
    }

    /**
     *
     * @see RegularExpression#matches(String)
     */
    public static boolean matches(String regex, String options, String target) throws ParseException {
        return REUtil.createRegex(regex, options).matches(target);
    }

    // ================================================================

    /**
     *
     */
    public static String quoteMeta(String literal) {
        int len = literal.length();
        StringBuffer buffer = null;
        for (int i = 0;  i < len;  i ++) {
            int ch = literal.charAt(i);
            if (".*+?{[()|\\^$".indexOf(ch) >= 0) {
                if (buffer == null) {
                    buffer = new StringBuffer(i+(len-i)*2);
                    if (i > 0)  buffer.append(literal.substring(0, i));
                }
                buffer.append('\\');
                buffer.append((char)ch);
            } else if (buffer != null)
                buffer.append((char)ch);
        }
        return buffer != null ? buffer.toString() : literal;
    }

    // ================================================================

    static void dumpString(String v) {
        for (int i = 0;  i < v.length();  i ++) {
            System.out.print(Integer.toHexString(v.charAt(i)));
            System.out.print(" ");
        }
        System.out.println();
    }
}
  

  /**
   * A regular expression matching engine using Non-deterministic Finite Automaton (NFA).
   * This engine does not conform to the POSIX regular expression.
   *
   * <hr width="50%">
   * <h3>How to use</h3>
   *
   * <dl>
   *   <dt>A. Standard way
   *   <dd>
   * <pre>
   * RegularExpression re = new RegularExpression(<var>regex</var>);
   * if (re.matches(text)) { ... }
   * </pre>
   *
   *   <dt>B. Capturing groups
   *   <dd>
   * <pre>
   * RegularExpression re = new RegularExpression(<var>regex</var>);
   * Match match = new Match();
   * if (re.matches(text, match)) {
   *     ... // You can refer captured texts with methods of the <code>Match</code> class.
   * }
   * </pre>
   *
   * </dl>
   *
   * <h4>Case-insensitive matching</h4>
   * <pre>
   * RegularExpression re = new RegularExpression(<var>regex</var>, "i");
   * if (re.matches(text) >= 0) { ...}
   * </pre>
   *
   * <h4>Options</h4>
   * <p>You can specify options to <a href="#RegularExpression(java.lang.String, java.lang.String)"><code>RegularExpression(</code><var>regex</var><code>, </code><var>options</var><code>)</code></a>
   *    or <a href="#setPattern(java.lang.String, java.lang.String)"><code>setPattern(</code><var>regex</var><code>, </code><var>options</var><code>)</code></a>.
   *    This <var>options</var> parameter consists of the following characters.
   * </p>
   * <dl>
   *   <dt><a name="I_OPTION"><code>"i"</code></a>
   *   <dd>This option indicates case-insensitive matching.
   *   <dt><a name="M_OPTION"><code>"m"</code></a>
   *   <dd class="REGEX"><kbd>^</kbd> and <kbd>$</kbd> consider the EOL characters within the text.
   *   <dt><a name="S_OPTION"><code>"s"</code></a>
   *   <dd class="REGEX"><kbd>.</kbd> matches any one character.
   *   <dt><a name="U_OPTION"><code>"u"</code></a>
   *   <dd class="REGEX">Redefines <Kbd>\d \D \w \W \s \S \b \B \&lt; \></kbd> as becoming to Unicode.
   *   <dt><a name="W_OPTION"><code>"w"</code></a>
   *   <dd class="REGEX">By this option, <kbd>\b \B \&lt; \></kbd> are processed with the method of
   *      'Unicode Regular Expression Guidelines' Revision 4.
   *      When "w" and "u" are specified at the same time,
   *      <kbd>\b \B \&lt; \></kbd> are processed for the "w" option.
   *   <dt><a name="COMMA_OPTION"><code>","</code></a>
   *   <dd>The parser treats a comma in a character class as a range separator.
   *      <kbd class="REGEX">[a,b]</kbd> matches <kbd>a</kbd> or <kbd>,</kbd> or <kbd>b</kbd> without this option.
   *      <kbd class="REGEX">[a,b]</kbd> matches <kbd>a</kbd> or <kbd>b</kbd> with this option.
   *
   *   <dt><a name="X_OPTION"><code>"X"</code></a>
   *   <dd class="REGEX">
   *       By this option, the engine conforms to <a href="http://www.w3.org/TR/2000/WD-xmlschema-2-20000407/#regexs">XML Schema: Regular Expression</a>.
   *       The <code>match()</code> method does not do substring matching
   *       but entire string matching.
   *
   * </dl>
   * 
   * <hr width="50%">
   * <h3>Syntax</h3>
   * <table border="1" bgcolor="#ddeeff">
   *   <tr>
   *    <td>
   *     <h4>Differences from the Perl 5 regular expression</h4>
   *     <ul>
   *      <li>There is 6-digit hexadecimal character representation  (<kbd>\u005cv</kbd><var>HHHHHH</var>.)
   *      <li>Supports subtraction, union, and intersection operations for character classes.
   *      <li>Not supported: <kbd>\</kbd><var>ooo</var> (Octal character representations),
   *          <Kbd>\G</kbd>, <kbd>\C</kbd>, <kbd>\l</kbd><var>c</var>,
   *          <kbd>\u005c u</kbd><var>c</var>, <kbd>\L</kbd>, <kbd>\U</kbd>,
   *          <kbd>\E</kbd>, <kbd>\Q</kbd>, <kbd>\N{</kbd><var>name</var><kbd>}</kbd>,
   *          <Kbd>(?{<kbd><var>code</var><kbd>})</kbd>, <Kbd>(??{<kbd><var>code</var><kbd>})</kbd>
   *     </ul>
   *    </td>
   *   </tr>
   * </table>
   *
   * <P>Meta characters are `<KBD>. * + ? { [ ( ) | \ ^ $</KBD>'.</P>
   * <ul>
   *   <li>Character
   *     <dl>
   *       <dt class="REGEX"><kbd>.</kbd> (A period)
   *       <dd>Matches any one character except the following characters.
   *       <dd>LINE FEED (U+000A), CARRIAGE RETURN (U+000D),
   *           PARAGRAPH SEPARATOR (U+2029), LINE SEPARATOR (U+2028)
   *       <dd>This expression matches one code point in Unicode. It can match a pair of surrogates.
   *       <dd>When <a href="#S_OPTION">the "s" option</a> is specified,
   *           it matches any character including the above four characters.
   *
   *       <dt class="REGEX"><Kbd>\e \f \n \r \t</kbd>
   *       <dd>Matches ESCAPE (U+001B), FORM FEED (U+000C), LINE FEED (U+000A),
   *           CARRIAGE RETURN (U+000D), HORIZONTAL TABULATION (U+0009)
   *
   *       <dt class="REGEX"><kbd>\c</kbd><var>C</var>
   *       <dd>Matches a control character.
   *           The <var>C</var> must be one of '<kbd>@</kbd>', '<kbd>A</kbd>'-'<kbd>Z</kbd>',
   *           '<kbd>[</kbd>', '<kbd>\u005c</kbd>', '<kbd>]</kbd>', '<kbd>^</kbd>', '<kbd>_</kbd>'.
   *           It matches a control character of which the character code is less than
   *           the character code of the <var>C</var> by 0x0040.
   *       <dd class="REGEX">For example, a <kbd>\cJ</kbd> matches a LINE FEED (U+000A),
   *           and a <kbd>\c[</kbd> matches an ESCAPE (U+001B).
   *
   *       <dt class="REGEX">a non-meta character
   *       <dd>Matches the character.
   *
   *       <dt class="REGEX"><KBD>\</KBD> + a meta character
   *       <dd>Matches the meta character.
   *
   *       <dt class="REGEX"><kbd>\u005cx</kbd><var>HH</var> <kbd>\u005cx{</kbd><var>HHHH</var><kbd>}</kbd>
   *       <dd>Matches a character of which code point is <var>HH</var> (Hexadecimal) in Unicode.
   *           You can write just 2 digits for <kbd>\u005cx</kbd><var>HH</var>, and
   *           variable length digits for <kbd>\u005cx{</kbd><var>HHHH</var><kbd>}</kbd>.
   *
   *       <!--
   *       <dt class="REGEX"><kbd>\u005c u</kbd><var>HHHH</var>
   *       <dd>Matches a character of which code point is <var>HHHH</var> (Hexadecimal) in Unicode.
   *       -->
   *
   *       <dt class="REGEX"><kbd>\u005cv</kbd><var>HHHHHH</var>
   *       <dd>Matches a character of which code point is <var>HHHHHH</var> (Hexadecimal) in Unicode.
   *
   *       <dt class="REGEX"><kbd>\g</kbd>
   *       <dd>Matches a grapheme.
   *       <dd class="REGEX">It is equivalent to <kbd>(?[\p{ASSIGNED}]-[\p{M}\p{C}])?(?:\p{M}|[\x{094D}\x{09CD}\x{0A4D}\x{0ACD}\x{0B3D}\x{0BCD}\x{0C4D}\x{0CCD}\x{0D4D}\x{0E3A}\x{0F84}]\p{L}|[\x{1160}-\x{11A7}]|[\x{11A8}-\x{11FF}]|[\x{FF9E}\x{FF9F}])*</kbd>
   *
   *       <dt class="REGEX"><kbd>\X</kbd>
   *       <dd class="REGEX">Matches a combining character sequence.
   *       It is equivalent to <kbd>(?:\PM\pM*)</kbd>
   *     </dl>
   *   </li>
   *
   *   <li>Character class
   *     <dl>
  + *       <dt class="REGEX"><kbd>[</kbd><var>R<sub>1</sub></var><var>R<sub>2</sub></var><var>...</var><var>R<sub>n</sub></var><kbd>]</kbd> (without <a href="#COMMA_OPTION">"," option</a>)
  + *       <dt class="REGEX"><kbd>[</kbd><var>R<sub>1</sub></var><kbd>,</kbd><var>R<sub>2</sub></var><kbd>,</kbd><var>...</var><kbd>,</kbd><var>R<sub>n</sub></var><kbd>]</kbd> (with <a href="#COMMA_OPTION">"," option</a>)
   *       <dd>Positive character class.  It matches a character in ranges.
   *       <dd><var>R<sub>n</sub></var>:
   *       <ul>
   *         <li class="REGEX">A character (including <Kbd>\e \f \n \r \t</kbd> <kbd>\u005cx</kbd><var>HH</var> <kbd>\u005cx{</kbd><var>HHHH</var><kbd>}</kbd> <!--kbd>\u005c u</kbd><var>HHHH</var--> <kbd>\u005cv</kbd><var>HHHHHH</var>)
   *             <p>This range matches the character.
   *         <li class="REGEX"><var>C<sub>1</sub></var><kbd>-</kbd><var>C<sub>2</sub></var>
   *             <p>This range matches a character which has a code point that is >= <var>C<sub>1</sub></var>'s code point and &lt;= <var>C<sub>2</sub></var>'s code point.
  + *         <li class="REGEX">A POSIX character class: <Kbd>[:alpha:] [:alnum:] [:ascii:] [:cntrl:] [:digit:] [:graph:] [:lower:] [:print:] [:punct:] [:space:] [:upper:] [:xdigit:]</kbd>,
  + *             and negative POSIX character classes in Perl like <kbd>[:^alpha:]</kbd>
   *             <p>...
   *         <li class="REGEX"><kbd>\d \D \s \S \w \W \p{</kbd><var>name</var><kbd>} \P{</kbd><var>name</var><kbd>}</kbd>
   *             <p>These expressions specifies the same ranges as the following expressions.
   *       </ul>
   *       <p class="REGEX">Enumerated ranges are merged (union operation).
   *          <kbd>[a-ec-z]</kbd> is equivalent to <kbd>[a-z]</kbd>
   *
   *       <dt class="REGEX"><kbd>[^</kbd><var>R<sub>1</sub></var><var>R<sub>2</sub></var><var>...</var><var>R<sub>n</sub></var><kbd>]</kbd> (without a <a href="#COMMA_OPTION">"," option</a>)
   *       <dt class="REGEX"><kbd>[^</kbd><var>R<sub>1</sub></var><kbd>,</kbd><var>R<sub>2</sub></var><kbd>,</kbd><var>...</var><kbd>,</kbd><var>R<sub>n</sub></var><kbd>]</kbd> (with a <a href="#COMMA_OPTION">"," option</a>)
   *       <dd>Negative character class.  It matches a character not in ranges.
   *
   *       <dt class="REGEX"><kbd>(?[</kbd><var>ranges</var><kbd>]</kbd><var>op</var><kbd>[</kbd><var>ranges</var><kbd>]</kbd><var>op</var><kbd>[</kbd><var>ranges</var><kbd>]</kbd> ... <Kbd>)</kbd>
   *       (<var>op</var> is <kbd>-</kbd> or <kbd>+</kbd> or <kbd>&</kbd>.)
   *       <dd>Subtraction or union or intersection for character classes.
   *       <dd class="REGEX">For exmaple, <kbd>(?[A-Z]-[CF])</kbd> is equivalent to <kbd>[A-BD-EG-Z]</kbd>, and <kbd>(?[0x00-0x7f]-[K]&[\p{Lu}])</kbd> is equivalent to <kbd>[A-JL-Z]</kbd>.
   *       <dd>The result of this operations is a <u>positive character class</u>
   *           even if an expression includes any negative character classes.
   *           You have to take care on this in case-insensitive matching.
   *           For instance, <kbd>(?[^b])</kbd> is equivalent to <kbd>[\x00-ac-\x{10ffff}]</kbd>,
   *           which is equivalent to <kbd>[^b]</kbd> in case-sensitive matching.
   *           But, in case-insensitive matching, <kbd>(?[^b])</kbd> matches any character because
   *           it includes '<kbd>B</kbd>' and '<kbd>B</kbd>' matches '<kbd>b</kbd>'
   *           though <kbd>[^b]</kbd> is processed as <kbd>[^Bb]</kbd>.
   *
   *       <dt class="REGEX"><kbd>[</kbd><var>R<sub>1</sub>R<sub>2</sub>...</var><kbd>-[</kbd><var>R<sub>n</sub>R<sub>n+1</sub>...</var><kbd>]]</kbd> (with an <a href="#X_OPTION">"X" option</a>)</dt>
   *       <dd>Character class subtraction for the XML Schema.
   *           You can use this syntax when you specify an <a href="#X_OPTION">"X" option</a>.
   *           
   *       <dt class="REGEX"><kbd>\d</kbd>
   *       <dd class="REGEX">Equivalent to <kbd>[0-9]</kbd>.
   *       <dd>When <a href="#U_OPTION">a "u" option</a> is set, it is equivalent to
   *           <span class="REGEX"><kbd>\p{Nd}</kbd></span>.
   *
   *       <dt class="REGEX"><kbd>\D</kbd>
   *       <dd class="REGEX">Equivalent to <kbd>[^0-9]</kbd>
   *       <dd>When <a href="#U_OPTION">a "u" option</a> is set, it is equivalent to
   *           <span class="REGEX"><kbd>\P{Nd}</kbd></span>.
   *
   *       <dt class="REGEX"><kbd>\s</kbd>
   *       <dd class="REGEX">Equivalent to <kbd>[ \f\n\r\t]</kbd>
   *       <dd>When <a href="#U_OPTION">a "u" option</a> is set, it is equivalent to
   *           <span class="REGEX"><kbd>[ \f\n\r\t\p{Z}]</kbd></span>.
   *
   *       <dt class="REGEX"><kbd>\S</kbd>
   *       <dd class="REGEX">Equivalent to <kbd>[^ \f\n\r\t]</kbd>
   *       <dd>When <a href="#U_OPTION">a "u" option</a> is set, it is equivalent to
   *           <span class="REGEX"><kbd>[^ \f\n\r\t\p{Z}]</kbd></span>.
   *
   *       <dt class="REGEX"><kbd>\w</kbd>
   *       <dd class="REGEX">Equivalent to <kbd>[a-zA-Z0-9_]</kbd>
   *       <dd>When <a href="#U_OPTION">a "u" option</a> is set, it is equivalent to
   *           <span class="REGEX"><kbd>[\p{Lu}\p{Ll}\p{Lo}\p{Nd}_]</kbd></span>.
   *
   *       <dt class="REGEX"><kbd>\W</kbd>
   *       <dd class="REGEX">Equivalent to <kbd>[^a-zA-Z0-9_]</kbd>
   *       <dd>When <a href="#U_OPTION">a "u" option</a> is set, it is equivalent to
   *           <span class="REGEX"><kbd>[^\p{Lu}\p{Ll}\p{Lo}\p{Nd}_]</kbd></span>.
   *
   *       <dt class="REGEX"><kbd>\p{</kbd><var>name</var><kbd>}</kbd>
   *       <dd>Matches one character in the specified General Category (the second field in <a href="ftp://ftp.unicode.org/Public/UNIDATA/UnicodeData.txt"><kbd>UnicodeData.txt</kbd></a>) or the specified <a href="ftp://ftp.unicode.org/Public/UNIDATA/Blocks.txt">Block</a>.
   *       The following names are available:
   *       <dl>
   *         <dt>Unicode General Categories:
   *         <dd><kbd>
   *       L, M, N, Z, C, P, S, Lu, Ll, Lt, Lm, Lo, Mn, Me, Mc, Nd, Nl, No, Zs, Zl, Zp,
   *       Cc, Cf, Cn, Co, Cs, Pd, Ps, Pe, Pc, Po, Sm, Sc, Sk, So,
   *         </kbd>
   *         <dd>(Currently the Cn category includes U+10000-U+10FFFF characters)
   *         <dt>Unicode Blocks:
   *         <dd><kbd>
   *       Basic Latin, Latin-1 Supplement, Latin Extended-A, Latin Extended-B,
   *       IPA Extensions, Spacing Modifier Letters, Combining Diacritical Marks, Greek,
   *       Cyrillic, Armenian, Hebrew, Arabic, Devanagari, Bengali, Gurmukhi, Gujarati,
   *       Oriya, Tamil, Telugu, Kannada, Malayalam, Thai, Lao, Tibetan, Georgian,
   *       Hangul Jamo, Latin Extended Additional, Greek Extended, General Punctuation,
   *       Superscripts and Subscripts, Currency Symbols, Combining Marks for Symbols,
   *       Letterlike Symbols, Number Forms, Arrows, Mathematical Operators,
   *       Miscellaneous Technical, Control Pictures, Optical Character Recognition,
   *       Enclosed Alphanumerics, Box Drawing, Block Elements, Geometric Shapes,
   *       Miscellaneous Symbols, Dingbats, CJK Symbols and Punctuation, Hiragana,
   *       Katakana, Bopomofo, Hangul Compatibility Jamo, Kanbun,
   *       Enclosed CJK Letters and Months, CJK Compatibility, CJK Unified Ideographs,
   *       Hangul Syllables, High Surrogates, High Private Use Surrogates, Low Surrogates,
   *       Private Use, CJK Compatibility Ideographs, Alphabetic Presentation Forms,
   *       Arabic Presentation Forms-A, Combining Half Marks, CJK Compatibility Forms,
   *       Small Form Variants, Arabic Presentation Forms-B, Specials,
   *       Halfwidth and Fullwidth Forms
   *         </kbd>
   *         <dt>Others:
   *         <dd><kbd>ALL</kbd> (Equivalent to <kbd>[\u005cu0000-\u005cv10FFFF]</kbd>)
   *         <dd><kbd>ASSGINED</kbd> (<kbd>\p{ASSIGNED}</kbd> is equivalent to <kbd>\P{Cn}</kbd>)
   *         <dd><kbd>UNASSGINED</kbd>
   *             (<kbd>\p{UNASSIGNED}</kbd> is equivalent to <kbd>\p{Cn}</kbd>)
   *       </dl>
   *
   *       <dt class="REGEX"><kbd>\P{</kbd><var>name</var><kbd>}</kbd>
   *       <dd>Matches one character not in the specified General Category or the specified Block.
   *     </dl>
   *   </li>
   *
   *   <li>Selection and Quantifier
   *     <dl>
   *       <dt class="REGEX"><VAR>X</VAR><kbd>|</kbd><VAR>Y</VAR>
   *       <dd>...
   *
   *       <dt class="REGEX"><VAR>X</VAR><kbd>*</KBD>
   *       <dd>Matches 0 or more <var>X</var>.
   *
   *       <dt class="REGEX"><VAR>X</VAR><kbd>+</KBD>
   *       <dd>Matches 1 or more <var>X</var>.
   *
   *       <dt class="REGEX"><VAR>X</VAR><kbd>?</KBD>
   *       <dd>Matches 0 or 1 <var>X</var>.
   *
   *       <dt class="REGEX"><var>X</var><kbd>{</kbd><var>number</var><kbd>}</kbd>
   *       <dd>Matches <var>number</var> times.
   *
   *       <dt class="REGEX"><var>X</var><kbd>{</kbd><var>min</var><kbd>,}</kbd>
   *       <dd>...
   *
   *       <dt class="REGEX"><var>X</var><kbd>{</kbd><var>min</var><kbd>,</kbd><var>max</var><kbd>}</kbd>
   *       <dd>...
   *
   *       <dt class="REGEX"><VAR>X</VAR><kbd>*?</kbd>
   *       <dt class="REGEX"><VAR>X</VAR><kbd>+?</kbd>
   *       <dt class="REGEX"><VAR>X</VAR><kbd>??</kbd>
   *       <dt class="REGEX"><var>X</var><kbd>{</kbd><var>min</var><kbd>,}?</kbd>
   *       <dt class="REGEX"><var>X</var><kbd>{</kbd><var>min</var><kbd>,</kbd><var>max</var><kbd>}?</kbd>
   *       <dd>Non-greedy matching.
   *     </dl>
   *   </li>
   *
   *   <li>Grouping, Capturing, and Back-reference
   *     <dl>
   *       <dt class="REGEX"><KBD>(?:</kbd><VAR>X</VAR><kbd>)</KBD>
   *       <dd>Grouping. "<KBD>foo+</KBD>" matches "<KBD>foo</KBD>" or "<KBD>foooo</KBD>".
   *       If you want it matches "<KBD>foofoo</KBD>" or "<KBD>foofoofoo</KBD>",
   *       you have to write "<KBD>(?:foo)+</KBD>".
   *
   *       <dt class="REGEX"><KBD>(</kbd><VAR>X</VAR><kbd>)</KBD>
   *       <dd>Grouping with capturing.
   * It make a group and applications can know
   * where in target text a group matched with methods of a <code>Match</code> instance
   * after <code><a href="#matches(java.lang.String, org.apache.xerces.utils.regex.Match)">matches(String,Match)</a></code>.
   * The 0th group means whole of this regular expression.
   * The <VAR>N</VAR>th gorup is the inside of the <VAR>N</VAR>th left parenthesis.
   * 
   *   <p>For instance, a regular expression is
   *   "<FONT color=blue><KBD> *([^&lt;:]*) +&lt;([^&gt;]*)&gt; *</KBD></FONT>"
   *   and target text is
   *   "<FONT color=red><KBD>From: TAMURA Kent &lt;kent@trl.ibm.co.jp&gt;</KBD></FONT>":
   *   <ul>
   *     <li><code>Match.getCapturedText(0)</code>:
   *     "<FONT color=red><KBD> TAMURA Kent &lt;kent@trl.ibm.co.jp&gt;</KBD></FONT>"
   *     <li><code>Match.getCapturedText(1)</code>: "<FONT color=red><KBD>TAMURA Kent</KBD></FONT>"
   *     <li><code>Match.getCapturedText(2)</code>: "<FONT color=red><KBD>kent@trl.ibm.co.jp</KBD></FONT>"
   *   </ul>
   *
   *       <dt class="REGEX"><kbd>\1 \2 \3 \4 \5 \6 \7 \8 \9</kbd>
   *       <dd>
   *
   *       <dt class="REGEX"><kbd>(?></kbd><var>X</var><kbd>)</kbd>
   *       <dd>Independent expression group. ................
   *
   *       <dt class="REGEX"><kbd>(?</kbd><var>options</var><kbd>:</kbd><var>X</var><kbd>)</kbd>
   *       <dt class="REGEX"><kbd>(?</kbd><var>options</var><kbd>-</kbd><var>options2</var><kbd>:</kbd><var>X</var><kbd>)</kbd>
   *       <dd>............................
   *       <dd>The <var>options</var> or the <var>options2</var> consists of 'i' 'm' 's' 'w'.
   *           Note that it can not contain 'u'.
   *
   *       <dt class="REGEX"><kbd>(?</kbd><var>options</var><kbd>)</kbd>
   *       <dt class="REGEX"><kbd>(?</kbd><var>options</var><kbd>-</kbd><var>options2</var><kbd>)</kbd>
   *       <dd>......
   *       <dd>These expressions must be at the beginning of a group.
   *     </dl>
   *   </li>
   *
   *   <li>Anchor
   *     <dl>
   *       <dt class="REGEX"><kbd>\A</kbd>
   *       <dd>Matches the beginnig of the text.
   *
   *       <dt class="REGEX"><kbd>\Z</kbd>
   *       <dd>Matches the end of the text, or before an EOL character at the end of the text,
   *           or CARRIAGE RETURN + LINE FEED at the end of the text.
   *
   *       <dt class="REGEX"><kbd>\z</kbd>
   *       <dd>Matches the end of the text.
   *
   *       <dt class="REGEX"><kbd>^</kbd>
   *       <dd>Matches the beginning of the text.  It is equivalent to <span class="REGEX"><Kbd>\A</kbd></span>.
   *       <dd>When <a href="#M_OPTION">a "m" option</a> is set,
   *           it matches the beginning of the text, or after one of EOL characters (
   *           LINE FEED (U+000A), CARRIAGE RETURN (U+000D), LINE SEPARATOR (U+2028),
   *           PARAGRAPH SEPARATOR (U+2029).)
   *
   *       <dt class="REGEX"><kbd>$</kbd>
   *       <dd>Matches the end of the text, or before an EOL character at the end of the text,
   *           or CARRIAGE RETURN + LINE FEED at the end of the text.
   *       <dd>When <a href="#M_OPTION">a "m" option</a> is set,
   *           it matches the end of the text, or before an EOL character.
   *
   *       <dt class="REGEX"><kbd>\b</kbd>
   *       <dd>Matches word boundary.
   *           (See <a href="#W_OPTION">a "w" option</a>)
   *
   *       <dt class="REGEX"><kbd>\B</kbd>
   *       <dd>Matches non word boundary.
   *           (See <a href="#W_OPTION">a "w" option</a>)
   *
   *       <dt class="REGEX"><kbd>\&lt;</kbd>
   *       <dd>Matches the beginning of a word.
   *           (See <a href="#W_OPTION">a "w" option</a>)
   *
   *       <dt class="REGEX"><kbd>\&gt;</kbd>
   *       <dd>Matches the end of a word.
   *           (See <a href="#W_OPTION">a "w" option</a>)
   *     </dl>
   *   </li>
   *   <li>Lookahead and lookbehind
   *     <dl>
   *       <dt class="REGEX"><kbd>(?=</kbd><var>X</var><kbd>)</kbd>
   *       <dd>Lookahead.
   *
   *       <dt class="REGEX"><kbd>(?!</kbd><var>X</var><kbd>)</kbd>
   *       <dd>Negative lookahead.
   *
   *       <dt class="REGEX"><kbd>(?&lt;=</kbd><var>X</var><kbd>)</kbd>
   *       <dd>Lookbehind.
   *       <dd>(Note for text capturing......)
   *
   *       <dt class="REGEX"><kbd>(?&lt;!</kbd><var>X</var><kbd>)</kbd>
   *       <dd>Negative lookbehind.
   *     </dl>
   *   </li>
   *
   *   <li>Misc.
   *     <dl>
   *       <dt class="REGEX"><kbd>(?(</Kbd><var>condition</var><Kbd>)</kbd><var>yes-pattern</var><kbd>|</kbd><var>no-pattern</var><kbd>)</kbd>,
   *       <dt class="REGEX"><kbd>(?(</kbd><var>condition</var><kbd>)</kbd><var>yes-pattern</var><kbd>)</kbd>
   *       <dd>......
   *       <dt class="REGEX"><kbd>(?#</kbd><var>comment</var><kbd>)</kbd>
   *       <dd>Comment.  A comment string consists of characters except '<kbd>)</kbd>'.
   *           You can not write comments in character classes and before quantifiers.
   *     </dl>
   *   </li>
   * </ul>
   *
   *
   * <hr width="50%">
   * <h3>BNF for the regular expression</h3>
   * <pre>
   * regex ::= ('(?' options ')')? term ('|' term)*
   * term ::= factor+
   * factor ::= anchors | atom (('*' | '+' | '?' | minmax ) '?'? )?
   *            | '(?#' [^)]* ')'
   * minmax ::= '{' ([0-9]+ | [0-9]+ ',' | ',' [0-9]+ | [0-9]+ ',' [0-9]+) '}'
   * atom ::= char | '.' | char-class | '(' regex ')' | '(?:' regex ')' | '\' [0-9]
   *          | '\w' | '\W' | '\d' | '\D' | '\s' | '\S' | category-block | '\X'
   *          | '(?>' regex ')' | '(?' options ':' regex ')'
   *          | '(?' ('(' [0-9] ')' | '(' anchors ')' | looks) term ('|' term)? ')'
   * options ::= [imsw]* ('-' [imsw]+)?
   * anchors ::= '^' | '$' | '\A' | '\Z' | '\z' | '\b' | '\B' | '\&lt;' | '\>'
   * looks ::= '(?=' regex ')'  | '(?!' regex ')'
   *           | '(?&lt;=' regex ')' | '(?&lt;!' regex ')'
   * char ::= '\\' | '\' [efnrtv] | '\c' [@-_] | code-point | character-1
   * category-block ::= '\' [pP] category-symbol-1
   *                    | ('\p{' | '\P{') (category-symbol | block-name
   *                                       | other-properties) '}'
   * category-symbol-1 ::= 'L' | 'M' | 'N' | 'Z' | 'C' | 'P' | 'S'
   * category-symbol ::= category-symbol-1 | 'Lu' | 'Ll' | 'Lt' | 'Lm' | Lo'
   *                     | 'Mn' | 'Me' | 'Mc' | 'Nd' | 'Nl' | 'No'
   *                     | 'Zs' | 'Zl' | 'Zp' | 'Cc' | 'Cf' | 'Cn' | 'Co' | 'Cs'
   *                     | 'Pd' | 'Ps' | 'Pe' | 'Pc' | 'Po'
   *                     | 'Sm' | 'Sc' | 'Sk' | 'So'
   * block-name ::= (See above)
   * other-properties ::= 'ALL' | 'ASSIGNED' | 'UNASSIGNED'
   * character-1 ::= (any character except meta-characters)
   *
   * char-class ::= '[' ranges ']'
   *                | '(?[' ranges ']' ([-+&] '[' ranges ']')? ')'
   * ranges ::= '^'? (range <a href="#COMMA_OPTION">','?</a>)+
   * range ::= '\d' | '\w' | '\s' | '\D' | '\W' | '\S' | category-block
   *           | range-char | range-char '-' range-char
   * range-char ::= '\[' | '\]' | '\\' | '\' [,-efnrtv] | code-point | character-2
   * code-point ::= '\x' hex-char hex-char
   *                | '\x{' hex-char+ '}'
   * <!--               | '\u005c u' hex-char hex-char hex-char hex-char
   * -->               | '\v' hex-char hex-char hex-char hex-char hex-char hex-char
   * hex-char ::= [0-9a-fA-F]
   * character-2 ::= (any character except \[]-,)
   * </pre>
   *
   * <hr width="50%">
   * <h3>to do</h3>
   * <ul>
   *   <li><a href="http://www.unicode.org/unicode/reports/tr18/">Unicode Regular Expression Guidelines</a>
   *     <ul>
   *       <li>2.4 Canonical Equivalents
   *       <li>Level 3
   *     </ul>
   *   <li>Parsing performance
   * </ul>
   *
   * <hr width="50%">
   *
   * @author TAMURA Kent &lt;kent@trl.ibm.co.jp&gt;
   */
  public static class RegularExpression implements java.io.Serializable {
      private static final long serialVersionUID = 1L;
      static final boolean DEBUG = false;

      /**
       * Compiles a token tree into an operation flow.
       */
      private synchronized void compile(Token tok) {
          if (this.operations != null)
              return;
          this.numberOfClosures = 0;
          this.operations = this.compile(tok, null, false);
      }

      /**
       * Converts a token to an operation.
       */
      private Op compile(Token tok, Op next, boolean reverse) {
          Op ret;
          switch (tok.type) {
          case Token.DOT:
              ret = Op.createDot();
              ret.next = next;
              break;

          case Token.CHAR:
              ret = Op.createChar(tok.getChar());
              ret.next = next;
              break;

          case Token.ANCHOR:
              ret = Op.createAnchor(tok.getChar());
              ret.next = next;
              break;

          case Token.RANGE:
          case Token.NRANGE:
              ret = Op.createRange(tok);
              ret.next = next;
              break;

          case Token.CONCAT:
              ret = next;
              if (!reverse) {
                  for (int i = tok.size()-1;  i >= 0;  i --) {
                      ret = compile(tok.getChild(i), ret, false);
                  }
              } else {
                  for (int i = 0;  i < tok.size();  i ++) {
                      ret = compile(tok.getChild(i), ret, true);
                  }
              }
              break;

          case Token.UNION:
              Op.UnionOp uni = Op.createUnion(tok.size());
              for (int i = 0;  i < tok.size();  i ++) {
                  uni.addElement(compile(tok.getChild(i), next, reverse));
              }
              ret = uni;                          // ret.next is null.
              break;

          case Token.CLOSURE:
          case Token.NONGREEDYCLOSURE:
              Token child = tok.getChild(0);
              int min = tok.getMin();
              int max = tok.getMax();
              if (min >= 0 && min == max) { // {n}
                  ret = next;
                  for (int i = 0; i < min;  i ++) {
                      ret = compile(child, ret, reverse);
                  }
                  break;
              }
              if (min > 0 && max > 0)
                  max -= min;
              if (max > 0) {
                  // X{2,6} -> XX(X(X(XX?)?)?)?
                  ret = next;
                  for (int i = 0;  i < max;  i ++) {
                      Op.ChildOp q = Op.createQuestion(tok.type == Token.NONGREEDYCLOSURE);
                      q.next = next;
                      q.setChild(compile(child, ret, reverse));
                      ret = q;
                  }
              } else {
                  Op.ChildOp op;
                  if (tok.type == Token.NONGREEDYCLOSURE) {
                      op = Op.createNonGreedyClosure();
                  } else {                        // Token.CLOSURE
                      if (child.getMinLength() == 0)
                          op = Op.createClosure(this.numberOfClosures++);
                      else
                          op = Op.createClosure(-1);
                  }
                  op.next = next;
                  op.setChild(compile(child, op, reverse));
                  ret = op;
              }
              if (min > 0) {
                  for (int i = 0;  i < min;  i ++) {
                      ret = compile(child, ret, reverse);
                  }
              }
              break;

          case Token.EMPTY:
              ret = next;
              break;

          case Token.STRING:
              ret = Op.createString(tok.getString());
              ret.next = next;
              break;

          case Token.BACKREFERENCE:
              ret = Op.createBackReference(tok.getReferenceNumber());
              ret.next = next;
              break;

          case Token.PAREN:
              if (tok.getParenNumber() == 0) {
                  ret = compile(tok.getChild(0), next, reverse);
              } else if (reverse) {
                  next = Op.createCapture(tok.getParenNumber(), next);
                  next = compile(tok.getChild(0), next, reverse);
                  ret = Op.createCapture(-tok.getParenNumber(), next);
              } else {
                  next = Op.createCapture(-tok.getParenNumber(), next);
                  next = compile(tok.getChild(0), next, reverse);
                  ret = Op.createCapture(tok.getParenNumber(), next);
              }
              break;

          case Token.LOOKAHEAD:
              ret = Op.createLook(Op.LOOKAHEAD, next, compile(tok.getChild(0), null, false));
              break;
          case Token.NEGATIVELOOKAHEAD:
              ret = Op.createLook(Op.NEGATIVELOOKAHEAD, next, compile(tok.getChild(0), null, false));
              break;
          case Token.LOOKBEHIND:
              ret = Op.createLook(Op.LOOKBEHIND, next, compile(tok.getChild(0), null, true));
              break;
          case Token.NEGATIVELOOKBEHIND:
              ret = Op.createLook(Op.NEGATIVELOOKBEHIND, next, compile(tok.getChild(0), null, true));
              break;

          case Token.INDEPENDENT:
              ret = Op.createIndependent(next, compile(tok.getChild(0), null, reverse));
              break;

          case Token.MODIFIERGROUP:
              ret = Op.createModifier(next, compile(tok.getChild(0), null, reverse),
                                      ((Token.ModifierToken)tok).getOptions(),
                                      ((Token.ModifierToken)tok).getOptionsMask());
              break;

          case Token.CONDITION:
              Token.ConditionToken ctok = (Token.ConditionToken)tok;
              int ref = ctok.refNumber;
              Op condition = ctok.condition == null ? null : compile(ctok.condition, null, reverse);
              Op yes = compile(ctok.yes, next, reverse);
              Op no = ctok.no == null ? null : compile(ctok.no, next, reverse);
              ret = Op.createCondition(next, ref, condition, yes, no);
              break;

          default:
              throw new RuntimeException("Unknown token type: "+tok.type);
          } // switch (tok.type)
          return ret;
      }


//  Public

      /**
       * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
       *
       * @return true if the target is matched to this regular expression.
       */
      public boolean matches(char[]  target) {
          return this.matches(target, 0,  target .length , (Match)null);
      }

      /**
       * Checks whether the <var>target</var> text <strong>contains</strong> this pattern
       * in specified range or not.
       *
       * @param start Start offset of the range.
       * @param end  End offset +1 of the range.
       * @return true if the target is matched to this regular expression.
       */
      public boolean matches(char[]  target, int start, int end) {
          return this.matches(target, start, end, (Match)null);
      }

      /**
       * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
       *
       * @param match A Match instance for storing matching result.
       * @return Offset of the start position in <VAR>target</VAR>; or -1 if not match.
       */
      public boolean matches(char[]  target, Match match) {
          return this.matches(target, 0,  target .length , match);
      }


      /**
       * Checks whether the <var>target</var> text <strong>contains</strong> this pattern
       * in specified range or not.
       *
       * @param start Start offset of the range.
       * @param end  End offset +1 of the range.
       * @param match A Match instance for storing matching result.
       * @return Offset of the start position in <VAR>target</VAR>; or -1 if not match.
       */
      public boolean matches(char[]  target, int start, int end, Match match) {

          synchronized (this) {
              if (this.operations == null)
                  this.prepare();
              if (this.context == null)
                  this.context = new Context();
          }
          Context con = null;
          synchronized (this.context) {
              con = this.context.inuse ? new Context() : this.context;
              con.reset(target, start, end, this.numberOfClosures);
          }
          if (match != null) {
              match.setNumberOfGroups(this.nofparen);
              match.setSource(target);
          } else if (this.hasBackReferences) {
              match = new Match();
              match.setNumberOfGroups(this.nofparen);
              // Need not to call setSource() because
              // a caller can not access this match instance.
          }
          con.match = match;

          if (RegularExpression.isSet(this.options, XMLSCHEMA_MODE)) {
              int matchEnd = this. matchCharArray (con, this.operations, con.start, 1, this.options);
              //System.err.println("DEBUG: matchEnd="+matchEnd);
              if (matchEnd == con.limit) {
                  if (con.match != null) {
                      con.match.setBeginning(0, con.start);
                      con.match.setEnd(0, matchEnd);
                  }
                  con.inuse = false;
                  return true;
              }
              return false;
          }

          /*
           * The pattern has only fixed string.
           * The engine uses Boyer-Moore.
           */
          if (this.fixedStringOnly) {
              //System.err.println("DEBUG: fixed-only: "+this.fixedString);
              int o = this.fixedStringTable.matches(target, con.start, con.limit);
              if (o >= 0) {
                  if (con.match != null) {
                      con.match.setBeginning(0, o);
                      con.match.setEnd(0, o+this.fixedString.length());
                  }
                  con.inuse = false;
                  return true;
              }
              con.inuse = false;
              return false;
          }

          /*
           * The pattern contains a fixed string.
           * The engine checks with Boyer-Moore whether the text contains the fixed string or not.
           * If not, it return with false.
           */
          if (this.fixedString != null) {
              int o = this.fixedStringTable.matches(target, con.start, con.limit);
              if (o < 0) {
                  //System.err.println("Non-match in fixed-string search.");
                  con.inuse = false;
                  return false;
              }
          }

          int limit = con.limit-this.minlength;
          int matchStart;
          int matchEnd = -1;

          /*
           * Checks whether the expression starts with ".*".
           */
          if (this.operations != null
              && this.operations.type == Op.CLOSURE && this.operations.getChild().type == Op.DOT) {
              if (isSet(this.options, SINGLE_LINE)) {
                  matchStart = con.start;
                  matchEnd = this. matchCharArray (con, this.operations, con.start, 1, this.options);
              } else {
                  boolean previousIsEOL = true;
                  for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                      int ch =  target [  matchStart ] ;
                      if (isEOLChar(ch)) {
                          previousIsEOL = true;
                      } else {
                          if (previousIsEOL) {
                              if (0 <= (matchEnd = this. matchCharArray (con, this.operations,
                                                                         matchStart, 1, this.options)))
                                  break;
                          }
                          previousIsEOL = false;
                      }
                  }
              }
          }

          /*
           * Optimization against the first character.
           */
          else if (this.firstChar != null) {
              //System.err.println("DEBUG: with firstchar-matching: "+this.firstChar);
              RangeToken range = this.firstChar;
              if (RegularExpression.isSet(this.options, IGNORE_CASE)) {
                  range = this.firstChar.getCaseInsensitiveToken();
                  for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                      int ch =  target [  matchStart ] ;
                      if (REUtil.isHighSurrogate(ch) && matchStart+1 < con.limit) {
                          ch = REUtil.composeFromSurrogates(ch,  target [  matchStart+1 ] );
                          if (!range.match(ch))  continue;
                      } else {
                          if (!range.match(ch)) {
                              char ch1 = Character.toUpperCase((char)ch);
                              if (!range.match(ch1))
                                  if (!range.match(Character.toLowerCase(ch1)))
                                      continue;
                          }
                      }
                      if (0 <= (matchEnd = this. matchCharArray (con, this.operations,
                                                                 matchStart, 1, this.options)))
                          break;
                  }
              } else {
                  for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                      int ch =  target [  matchStart ] ;
                      if (REUtil.isHighSurrogate(ch) && matchStart+1 < con.limit)
                          ch = REUtil.composeFromSurrogates(ch,  target [  matchStart+1 ] );
                      if (!range.match(ch))  continue;
                      if (0 <= (matchEnd = this. matchCharArray (con, this.operations,
                                                                 matchStart, 1, this.options)))
                          break;
                  }
              }
          }

          /*
           * Straightforward matching.
           */
          else {
              for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                  if (0 <= (matchEnd = this. matchCharArray (con, this.operations, matchStart, 1, this.options)))
                      break;
              }
          }

          if (matchEnd >= 0) {
              if (con.match != null) {
                  con.match.setBeginning(0, matchStart);
                  con.match.setEnd(0, matchEnd);
              }
              con.inuse = false;
              return true;
          } else {
              con.inuse = false;
              return false;
          }
      }

  /**
   * @return -1 when not match; offset of the end of matched string when match.
   */
      private int matchCharArray (Context con, Op op, int offset, int dx, int opts) {

          char[] target = con.charTarget;


          while (true) {
              if (op == null)
                  return isSet(opts, XMLSCHEMA_MODE) && offset != con.limit ? -1 : offset;
              if (offset > con.limit || offset < con.start)
                  return -1;
              switch (op.type) {
              case Op.CHAR:
                  if (isSet(opts, IGNORE_CASE)) {
                      int ch = op.getData();
                      if (dx > 0) {
                          if (offset >= con.limit || !matchIgnoreCase(ch,  target [  offset ] ))
                              return -1;
                          offset ++;
                      } else {
                          int o1 = offset-1;
                          if (o1 >= con.limit || o1 < 0 || !matchIgnoreCase(ch,  target [  o1 ] ))
                              return -1;
                          offset = o1;
                      }
                  } else {
                      int ch = op.getData();
                      if (dx > 0) {
                          if (offset >= con.limit || ch !=  target [  offset ] )
                              return -1;
                          offset ++;
                      } else {
                          int o1 = offset-1;
                          if (o1 >= con.limit || o1 < 0 || ch !=  target [  o1 ] )
                              return -1;
                          offset = o1;
                      }
                  }
                  op = op.next;
                  break;

              case Op.DOT:
                  if (dx > 0) {
                      if (offset >= con.limit)
                          return -1;
                      int ch =  target [  offset ] ;
                      if (isSet(opts, SINGLE_LINE)) {
                          if (REUtil.isHighSurrogate(ch) && offset+1 < con.limit)
                              offset ++;
                      } else {
                          if (REUtil.isHighSurrogate(ch) && offset+1 < con.limit)
                              ch = REUtil.composeFromSurrogates(ch,  target [  ++offset ] );
                          if (isEOLChar(ch))
                              return -1;
                      }
                      offset ++;
                  } else {
                      int o1 = offset-1;
                      if (o1 >= con.limit || o1 < 0)
                          return -1;
                      int ch =  target [  o1 ] ;
                      if (isSet(opts, SINGLE_LINE)) {
                          if (REUtil.isLowSurrogate(ch) && o1-1 >= 0)
                              o1 --;
                      } else {
                          if (REUtil.isLowSurrogate(ch) && o1-1 >= 0)
                              ch = REUtil.composeFromSurrogates( target [  --o1 ] , ch);
                          if (!isEOLChar(ch))
                              return -1;
                      }
                      offset = o1;
                  }
                  op = op.next;
                  break;

              case Op.RANGE:
              case Op.NRANGE:
                  if (dx > 0) {
                      if (offset >= con.limit)
                          return -1;
                      int ch =  target [  offset ] ;
                      if (REUtil.isHighSurrogate(ch) && offset+1 < con.limit)
                          ch = REUtil.composeFromSurrogates(ch,  target [  ++offset ] );
                      RangeToken tok = op.getToken();
                      if (isSet(opts, IGNORE_CASE)) {
                          tok = tok.getCaseInsensitiveToken();
                          if (!tok.match(ch)) {
                              if (ch >= 0x10000)  return -1;
                              char uch;
                              if (!tok.match(uch = Character.toUpperCase((char)ch))
                                  && !tok.match(Character.toLowerCase(uch)))
                                  return -1;
                          }
                      } else {
                          if (!tok.match(ch))  return -1;
                      }
                      offset ++;
                  } else {
                      int o1 = offset-1;
                      if (o1 >= con.limit || o1 < 0)
                          return -1;
                      int ch =  target [  o1 ] ;
                      if (REUtil.isLowSurrogate(ch) && o1-1 >= 0)
                          ch = REUtil.composeFromSurrogates( target [  --o1 ] , ch);
                      RangeToken tok = op.getToken();
                      if (isSet(opts, IGNORE_CASE)) {
                          tok = tok.getCaseInsensitiveToken();
                          if (!tok.match(ch)) {
                              if (ch >= 0x10000)  return -1;
                              char uch;
                              if (!tok.match(uch = Character.toUpperCase((char)ch))
                                  && !tok.match(Character.toLowerCase(uch)))
                                  return -1;
                          }
                      } else {
                          if (!tok.match(ch))  return -1;
                      }
                      offset = o1;
                  }
                  op = op.next;
                  break;

              case Op.ANCHOR:
                  boolean go = false;
                  switch (op.getData()) {
                  case '^':
                      if (isSet(opts, MULTIPLE_LINES)) {
                          if (!(offset == con.start
                                || offset > con.start && isEOLChar( target [  offset-1 ] )))
                              return -1;
                      } else {
                          if (offset != con.start)
                              return -1;
                      }
                      break;

                  case '@':                         // Internal use only.
                      // The @ always matches line beginnings.
                      if (!(offset == con.start
                            || offset > con.start && isEOLChar( target [  offset-1 ] )))
                          return -1;
                      break;

                  case '$':
                      if (isSet(opts, MULTIPLE_LINES)) {
                          if (!(offset == con.limit
                                || offset < con.limit && isEOLChar( target [  offset ] )))
                              return -1;
                      } else {
                          if (!(offset == con.limit
                                || offset+1 == con.limit && isEOLChar( target [  offset ] )
                                || offset+2 == con.limit &&  target [  offset ]  == CARRIAGE_RETURN
                                &&  target [  offset+1 ]  == LINE_FEED))
                              return -1;
                      }
                      break;

                  case 'A':
                      if (offset != con.start)  return -1;
                      break;

                  case 'Z':
                      if (!(offset == con.limit
                            || offset+1 == con.limit && isEOLChar( target [  offset ] )
                            || offset+2 == con.limit &&  target [  offset ]  == CARRIAGE_RETURN
                            &&  target [  offset+1 ]  == LINE_FEED))
                          return -1;
                      break;

                  case 'z':
                      if (offset != con.limit)  return -1;
                      break;

                  case 'b':
                      if (con.length == 0)  return -1;
                      {
                          int after = getWordType(target, con.start, con.limit, offset, opts);
                          if (after == WT_IGNORE)  return -1;
                          int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                          if (after == before)  return -1;
                      }
                      break;

                  case 'B':
                      if (con.length == 0)
                          go = true;
                      else {
                          int after = getWordType(target, con.start, con.limit, offset, opts);
                          go = after == WT_IGNORE
                               || after == getPreviousWordType(target, con.start, con.limit, offset, opts);
                      }
                      if (!go)  return -1;
                      break;

                  case '<':
                      if (con.length == 0 || offset == con.limit)  return -1;
                      if (getWordType(target, con.start, con.limit, offset, opts) != WT_LETTER
                          || getPreviousWordType(target, con.start, con.limit, offset, opts) != WT_OTHER)
                          return -1;
                      break;

                  case '>':
                      if (con.length == 0 || offset == con.start)  return -1;
                      if (getWordType(target, con.start, con.limit, offset, opts) != WT_OTHER
                          || getPreviousWordType(target, con.start, con.limit, offset, opts) != WT_LETTER)
                          return -1;
                      break;
                  } // switch anchor type
                  op = op.next;
                  break;

              case Op.BACKREFERENCE:
                  {
                      int refno = op.getData();
                      if (refno <= 0 || refno >= this.nofparen)
                          throw new RuntimeException("Internal Error: Reference number must be more than zero: "+refno);
                      if (con.match.getBeginning(refno) < 0
                          || con.match.getEnd(refno) < 0)
                          return -1;                // ********
                      int o2 = con.match.getBeginning(refno);
                      int literallen = con.match.getEnd(refno)-o2;
                      if (!isSet(opts, IGNORE_CASE)) {
                          if (dx > 0) {
                              if (!regionMatches(target, offset, con.limit, o2, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatches(target, offset-literallen, con.limit, o2, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      } else {
                          if (dx > 0) {
                              if (!regionMatchesIgnoreCase(target, offset, con.limit, o2, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatchesIgnoreCase(target, offset-literallen, con.limit,
                                                           o2, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      }
                  }
                  op = op.next;
                  break;
              case Op.STRING:
                  {
                      String literal = op.getString();
                      int literallen = literal.length();
                      if (!isSet(opts, IGNORE_CASE)) {
                          if (dx > 0) {
                              if (!regionMatches(target, offset, con.limit, literal, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatches(target, offset-literallen, con.limit, literal, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      } else {
                          if (dx > 0) {
                              if (!regionMatchesIgnoreCase(target, offset, con.limit, literal, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatchesIgnoreCase(target, offset-literallen, con.limit,
                                                           literal, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      }
                  }
                  op = op.next;
                  break;

              case Op.CLOSURE:
                  {
                      /*
                       * Saves current position to avoid
                       * zero-width repeats.
                       */
                      int id = op.getData();
                      if (id >= 0) {
                          int previousOffset = con.offsets[id];
                          if (previousOffset < 0 || previousOffset != offset) {
                              con.offsets[id] = offset;
                          } else {
                              con.offsets[id] = -1;
                              op = op.next;
                              break;
                          }
                      }

                      int ret = this. matchCharArray (con, op.getChild(), offset, dx, opts);
                      if (id >= 0)  con.offsets[id] = -1;
                      if (ret >= 0)  return ret;
                      op = op.next;
                  }
                  break;

              case Op.QUESTION:
                  {
                      int ret = this. matchCharArray (con, op.getChild(), offset, dx, opts);
                      if (ret >= 0)  return ret;
                      op = op.next;
                  }
                  break;

              case Op.NONGREEDYCLOSURE:
              case Op.NONGREEDYQUESTION:
                  {
                      int ret = this. matchCharArray (con, op.next, offset, dx, opts);
                      if (ret >= 0)  return ret;
                      op = op.getChild();
                  }
                  break;

              case Op.UNION:
                  for (int i = 0;  i < op.size();  i ++) {
                      int ret = this. matchCharArray (con, op.elementAt(i), offset, dx, opts);
                      if (DEBUG) {
                          System.err.println("UNION: "+i+", ret="+ret);
                      }
                      if (ret >= 0)  return ret;
                  }
                  return -1;

              case Op.CAPTURE:
                  int refno = op.getData();
                  if (con.match != null && refno > 0) {
                      int save = con.match.getBeginning(refno);
                      con.match.setBeginning(refno, offset);
                      int ret = this. matchCharArray (con, op.next, offset, dx, opts);
                      if (ret < 0)  con.match.setBeginning(refno, save);
                      return ret;
                  } else if (con.match != null && refno < 0) {
                      int index = -refno;
                      int save = con.match.getEnd(index);
                      con.match.setEnd(index, offset);
                      int ret = this. matchCharArray (con, op.next, offset, dx, opts);
                      if (ret < 0)  con.match.setEnd(index, save);
                      return ret;
                  }
                  op = op.next;
                  break;

              case Op.LOOKAHEAD:
                  if (0 > this. matchCharArray (con, op.getChild(), offset, 1, opts))  return -1;
                  op = op.next;
                  break;
              case Op.NEGATIVELOOKAHEAD:
                  if (0 <= this. matchCharArray (con, op.getChild(), offset, 1, opts))  return -1;
                  op = op.next;
                  break;
              case Op.LOOKBEHIND:
                  if (0 > this. matchCharArray (con, op.getChild(), offset, -1, opts))  return -1;
                  op = op.next;
                  break;
              case Op.NEGATIVELOOKBEHIND:
                  if (0 <= this. matchCharArray (con, op.getChild(), offset, -1, opts))  return -1;
                  op = op.next;
                  break;

              case Op.INDEPENDENT:
                  {
                      int ret = this. matchCharArray (con, op.getChild(), offset, dx, opts);
                      if (ret < 0)  return ret;
                      offset = ret;
                      op = op.next;
                  }
                  break;

              case Op.MODIFIER:
                  {
                      int localopts = opts;
                      localopts |= op.getData();
                      localopts &= ~op.getData2();
                      //System.err.println("MODIFIER: "+Integer.toString(opts, 16)+" -> "+Integer.toString(localopts, 16));
                      int ret = this. matchCharArray (con, op.getChild(), offset, dx, localopts);
                      if (ret < 0)  return ret;
                      offset = ret;
                      op = op.next;
                  }
                  break;

              case Op.CONDITION:
                  {
                      Op.ConditionOp cop = (Op.ConditionOp)op;
                      boolean matchp = false;
                      if (cop.refNumber > 0) {
                          if (cop.refNumber >= this.nofparen)
                              throw new RuntimeException("Internal Error: Reference number must be more than zero: "+cop.refNumber);
                          matchp = con.match.getBeginning(cop.refNumber) >= 0
                                   && con.match.getEnd(cop.refNumber) >= 0;
                      } else {
                          matchp = 0 <= this. matchCharArray (con, cop.condition, offset, dx, opts);
                      }

                      if (matchp) {
                          op = cop.yes;
                      } else if (cop.no != null) {
                          op = cop.no;
                      } else {
                          op = cop.next;
                      }
                  }
                  break;

              default:
                  throw new RuntimeException("Unknown operation type: "+op.type);
              } // switch (op.type)
          } // while
      }

      private static final int getPreviousWordType(char[]  target, int begin, int end,
                                                   int offset, int opts) {
          int ret = getWordType(target, begin, end, --offset, opts);
          while (ret == WT_IGNORE)
              ret = getWordType(target, begin, end, --offset, opts);
          return ret;
      }

      private static final int getWordType(char[]  target, int begin, int end,
                                           int offset, int opts) {
          if (offset < begin || offset >= end)  return WT_OTHER;
          return getWordType0( target [  offset ] , opts);
      }



      private static final boolean regionMatches(char[]  target, int offset, int limit,
                                                 String part, int partlen) {
          if (offset < 0)  return false;
          if (limit-offset < partlen)
              return false;
          int i = 0;
          while (partlen-- > 0) {
              if ( target [  offset++ ]  != part.charAt(i++))
                  return false;
          }
          return true;
      }

      private static final boolean regionMatches(char[]  target, int offset, int limit,
                                                 int offset2, int partlen) {
          if (offset < 0)  return false;
          if (limit-offset < partlen)
              return false;
          int i = offset2;
          while (partlen-- > 0) {
              if ( target [  offset++ ]  !=  target [  i++ ] )
                  return false;
          }
          return true;
      }

  /**
   * @see java.lang.String#regionMatches(int, String, int, int)
   */
      private static final boolean regionMatchesIgnoreCase(char[]  target, int offset, int limit,
                                                           String part, int partlen) {
          if (offset < 0)  return false;
          if (limit-offset < partlen)
              return false;
          int i = 0;
          while (partlen-- > 0) {
              char ch1 =  target [  offset++ ] ;
              char ch2 = part.charAt(i++);
              if (ch1 == ch2)
                  continue;
              char uch1 = Character.toUpperCase(ch1);
              char uch2 = Character.toUpperCase(ch2);
              if (uch1 == uch2)
                  continue;
              if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2))
                  return false;
          }
          return true;
      }

      private static final boolean regionMatchesIgnoreCase(char[]  target, int offset, int limit,
                                                           int offset2, int partlen) {
          if (offset < 0)  return false;
          if (limit-offset < partlen)
              return false;
          int i = offset2;
          while (partlen-- > 0) {
              char ch1 =  target [  offset++ ] ;
              char ch2 =  target [  i++ ] ;
              if (ch1 == ch2)
                  continue;
              char uch1 = Character.toUpperCase(ch1);
              char uch2 = Character.toUpperCase(ch2);
              if (uch1 == uch2)
                  continue;
              if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2))
                  return false;
          }
          return true;
      }




      /**
       * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
       *
       * @return true if the target is matched to this regular expression.
       */
      public boolean matches(String  target) {
          return this.matches(target, 0,  target .length() , (Match)null);
      }

      /**
       * Checks whether the <var>target</var> text <strong>contains</strong> this pattern
       * in specified range or not.
       *
       * @param start Start offset of the range.
       * @param end  End offset +1 of the range.
       * @return true if the target is matched to this regular expression.
       */
      public boolean matches(String  target, int start, int end) {
          return this.matches(target, start, end, (Match)null);
      }

      /**
       * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
       *
       * @param match A Match instance for storing matching result.
       * @return Offset of the start position in <VAR>target</VAR>; or -1 if not match.
       */
      public boolean matches(String  target, Match match) {
          return this.matches(target, 0,  target .length() , match);
      }

      /**
       * Checks whether the <var>target</var> text <strong>contains</strong> this pattern
       * in specified range or not.
       *
       * @param start Start offset of the range.
       * @param end  End offset +1 of the range.
       * @param match A Match instance for storing matching result.
       * @return Offset of the start position in <VAR>target</VAR>; or -1 if not match.
       */
      public boolean matches(String  target, int start, int end, Match match) {

          synchronized (this) {
              if (this.operations == null)
                  this.prepare();
              if (this.context == null)
                  this.context = new Context();
          }
          Context con = null;
          synchronized (this.context) {
              con = this.context.inuse ? new Context() : this.context;
              con.reset(target, start, end, this.numberOfClosures);
          }
          if (match != null) {
              match.setNumberOfGroups(this.nofparen);
              match.setSource(target);
          } else if (this.hasBackReferences) {
              match = new Match();
              match.setNumberOfGroups(this.nofparen);
              // Need not to call setSource() because
              // a caller can not access this match instance.
          }
          con.match = match;

          if (RegularExpression.isSet(this.options, XMLSCHEMA_MODE)) {
              if (DEBUG) {
                  System.err.println("target string="+target);
              }
              int matchEnd = this. matchString (con, this.operations, con.start, 1, this.options);
              if (DEBUG) {
                  System.err.println("matchEnd="+matchEnd);
                  System.err.println("con.limit="+con.limit);
              }
              if (matchEnd == con.limit) {
                  if (con.match != null) {
                      con.match.setBeginning(0, con.start);
                      con.match.setEnd(0, matchEnd);
                  }
                  con.inuse = false;
                  return true;
              }
              return false;
          }

          /*
           * The pattern has only fixed string.
           * The engine uses Boyer-Moore.
           */
          if (this.fixedStringOnly) {
              //System.err.println("DEBUG: fixed-only: "+this.fixedString);
              int o = this.fixedStringTable.matches(target, con.start, con.limit);
              if (o >= 0) {
                  if (con.match != null) {
                      con.match.setBeginning(0, o);
                      con.match.setEnd(0, o+this.fixedString.length());
                  }
                  con.inuse = false;
                  return true;
              }
              con.inuse = false;
              return false;
          }

          /*
           * The pattern contains a fixed string.
           * The engine checks with Boyer-Moore whether the text contains the fixed string or not.
           * If not, it return with false.
           */
          if (this.fixedString != null) {
              int o = this.fixedStringTable.matches(target, con.start, con.limit);
              if (o < 0) {
                  //System.err.println("Non-match in fixed-string search.");
                  con.inuse = false;
                  return false;
              }
          }

          int limit = con.limit-this.minlength;
          int matchStart;
          int matchEnd = -1;

          /*
           * Checks whether the expression starts with ".*".
           */
          if (this.operations != null
              && this.operations.type == Op.CLOSURE && this.operations.getChild().type == Op.DOT) {
              if (isSet(this.options, SINGLE_LINE)) {
                  matchStart = con.start;
                  matchEnd = this. matchString (con, this.operations, con.start, 1, this.options);
              } else {
                  boolean previousIsEOL = true;
                  for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                      int ch =  target .charAt(  matchStart ) ;
                      if (isEOLChar(ch)) {
                          previousIsEOL = true;
                      } else {
                          if (previousIsEOL) {
                              if (0 <= (matchEnd = this. matchString (con, this.operations,
                                                                      matchStart, 1, this.options)))
                                  break;
                          }
                          previousIsEOL = false;
                      }
                  }
              }
          }

          /*
           * Optimization against the first character.
           */
          else if (this.firstChar != null) {
              //System.err.println("DEBUG: with firstchar-matching: "+this.firstChar);
              RangeToken range = this.firstChar;
              if (RegularExpression.isSet(this.options, IGNORE_CASE)) {
                  range = this.firstChar.getCaseInsensitiveToken();
                  for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                      int ch =  target .charAt(  matchStart ) ;
                      if (REUtil.isHighSurrogate(ch) && matchStart+1 < con.limit) {
                          ch = REUtil.composeFromSurrogates(ch,  target .charAt(  matchStart+1 ) );
                          if (!range.match(ch))  continue;
                      } else {
                          if (!range.match(ch)) {
                              char ch1 = Character.toUpperCase((char)ch);
                              if (!range.match(ch1))
                                  if (!range.match(Character.toLowerCase(ch1)))
                                      continue;
                          }
                      }
                      if (0 <= (matchEnd = this. matchString (con, this.operations,
                                                              matchStart, 1, this.options)))
                          break;
                  }
              } else {
                  for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                      int ch =  target .charAt(  matchStart ) ;
                      if (REUtil.isHighSurrogate(ch) && matchStart+1 < con.limit)
                          ch = REUtil.composeFromSurrogates(ch,  target .charAt(  matchStart+1 ) );
                      if (!range.match(ch))  continue;
                      if (0 <= (matchEnd = this. matchString (con, this.operations,
                                                              matchStart, 1, this.options)))
                          break;
                  }
              }
          }

          /*
           * Straightforward matching.
           */
          else {
              for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                  if (0 <= (matchEnd = this. matchString (con, this.operations, matchStart, 1, this.options)))
                      break;
              }
          }

          if (matchEnd >= 0) {
              if (con.match != null) {
                  con.match.setBeginning(0, matchStart);
                  con.match.setEnd(0, matchEnd);
              }
              con.inuse = false;
              return true;
          } else {
              con.inuse = false;
              return false;
          }
      }

      /**
       * @return -1 when not match; offset of the end of matched string when match.
       */
      private int matchString (Context con, Op op, int offset, int dx, int opts) {




          String target = con.strTarget;




          while (true) {
              if (op == null)
                  return isSet(opts, XMLSCHEMA_MODE) && offset != con.limit ? -1 : offset;
              if (offset > con.limit || offset < con.start)
                  return -1;
              switch (op.type) {
              case Op.CHAR:
                  if (isSet(opts, IGNORE_CASE)) {
                      int ch = op.getData();
                      if (dx > 0) {
                          if (offset >= con.limit || !matchIgnoreCase(ch,  target .charAt(  offset ) ))
                              return -1;
                          offset ++;
                      } else {
                          int o1 = offset-1;
                          if (o1 >= con.limit || o1 < 0 || !matchIgnoreCase(ch,  target .charAt(  o1 ) ))
                              return -1;
                          offset = o1;
                      }
                  } else {
                      int ch = op.getData();
                      if (dx > 0) {
                          if (offset >= con.limit || ch !=  target .charAt(  offset ) )
                              return -1;
                          offset ++;
                      } else {
                          int o1 = offset-1;
                          if (o1 >= con.limit || o1 < 0 || ch !=  target .charAt(  o1 ) )
                              return -1;
                          offset = o1;
                      }
                  }
                  op = op.next;
                  break;

              case Op.DOT:
                  if (dx > 0) {
                      if (offset >= con.limit)
                          return -1;
                      int ch =  target .charAt(  offset ) ;
                      if (isSet(opts, SINGLE_LINE)) {
                          if (REUtil.isHighSurrogate(ch) && offset+1 < con.limit)
                              offset ++;
                      } else {
                          if (REUtil.isHighSurrogate(ch) && offset+1 < con.limit)
                              ch = REUtil.composeFromSurrogates(ch,  target .charAt(  ++offset ) );
                          if (isEOLChar(ch))
                              return -1;
                      }
                      offset ++;
                  } else {
                      int o1 = offset-1;
                      if (o1 >= con.limit || o1 < 0)
                          return -1;
                      int ch =  target .charAt(  o1 ) ;
                      if (isSet(opts, SINGLE_LINE)) {
                          if (REUtil.isLowSurrogate(ch) && o1-1 >= 0)
                              o1 --;
                      } else {
                          if (REUtil.isLowSurrogate(ch) && o1-1 >= 0)
                              ch = REUtil.composeFromSurrogates( target .charAt(  --o1 ) , ch);
                          if (!isEOLChar(ch))
                              return -1;
                      }
                      offset = o1;
                  }
                  op = op.next;
                  break;

              case Op.RANGE:
              case Op.NRANGE:
                  if (dx > 0) {
                      if (offset >= con.limit)
                          return -1;
                      int ch =  target .charAt(  offset ) ;
                      if (REUtil.isHighSurrogate(ch) && offset+1 < con.limit)
                          ch = REUtil.composeFromSurrogates(ch,  target .charAt(  ++offset ) );
                      RangeToken tok = op.getToken();
                      if (isSet(opts, IGNORE_CASE)) {
                          tok = tok.getCaseInsensitiveToken();
                          if (!tok.match(ch)) {
                              if (ch >= 0x10000)  return -1;
                              char uch;
                              if (!tok.match(uch = Character.toUpperCase((char)ch))
                                  && !tok.match(Character.toLowerCase(uch)))
                                  return -1;
                          }
                      } else {
                          if (!tok.match(ch))  return -1;
                      }
                      offset ++;
                  } else {
                      int o1 = offset-1;
                      if (o1 >= con.limit || o1 < 0)
                          return -1;
                      int ch =  target .charAt(  o1 ) ;
                      if (REUtil.isLowSurrogate(ch) && o1-1 >= 0)
                          ch = REUtil.composeFromSurrogates( target .charAt(  --o1 ) , ch);
                      RangeToken tok = op.getToken();
                      if (isSet(opts, IGNORE_CASE)) {
                          tok = tok.getCaseInsensitiveToken();
                          if (!tok.match(ch)) {
                              if (ch >= 0x10000)  return -1;
                              char uch;
                              if (!tok.match(uch = Character.toUpperCase((char)ch))
                                  && !tok.match(Character.toLowerCase(uch)))
                                  return -1;
                          }
                      } else {
                          if (!tok.match(ch))  return -1;
                      }
                      offset = o1;
                  }
                  op = op.next;
                  break;

              case Op.ANCHOR:
                  boolean go = false;
                  switch (op.getData()) {
                  case '^':
                      if (isSet(opts, MULTIPLE_LINES)) {
                          if (!(offset == con.start
                                || offset > con.start && isEOLChar( target .charAt(  offset-1 ) )))
                              return -1;
                      } else {
                          if (offset != con.start)
                              return -1;
                      }
                      break;

                  case '@':                         // Internal use only.
                      // The @ always matches line beginnings.
                      if (!(offset == con.start
                            || offset > con.start && isEOLChar( target .charAt(  offset-1 ) )))
                          return -1;
                      break;

                  case '$':
                      if (isSet(opts, MULTIPLE_LINES)) {
                          if (!(offset == con.limit
                                || offset < con.limit && isEOLChar( target .charAt(  offset ) )))
                              return -1;
                      } else {
                          if (!(offset == con.limit
                                || offset+1 == con.limit && isEOLChar( target .charAt(  offset ) )
                                || offset+2 == con.limit &&  target .charAt(  offset )  == CARRIAGE_RETURN
                                &&  target .charAt(  offset+1 )  == LINE_FEED))
                              return -1;
                      }
                      break;

                  case 'A':
                      if (offset != con.start)  return -1;
                      break;

                  case 'Z':
                      if (!(offset == con.limit
                            || offset+1 == con.limit && isEOLChar( target .charAt(  offset ) )
                            || offset+2 == con.limit &&  target .charAt(  offset )  == CARRIAGE_RETURN
                            &&  target .charAt(  offset+1 )  == LINE_FEED))
                          return -1;
                      break;

                  case 'z':
                      if (offset != con.limit)  return -1;
                      break;

                  case 'b':
                      if (con.length == 0)  return -1;
                      {
                          int after = getWordType(target, con.start, con.limit, offset, opts);
                          if (after == WT_IGNORE)  return -1;
                          int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                          if (after == before)  return -1;
                      }
                      break;

                  case 'B':
                      if (con.length == 0)
                          go = true;
                      else {
                          int after = getWordType(target, con.start, con.limit, offset, opts);
                          go = after == WT_IGNORE
                               || after == getPreviousWordType(target, con.start, con.limit, offset, opts);
                      }
                      if (!go)  return -1;
                      break;

                  case '<':
                      if (con.length == 0 || offset == con.limit)  return -1;
                      if (getWordType(target, con.start, con.limit, offset, opts) != WT_LETTER
                          || getPreviousWordType(target, con.start, con.limit, offset, opts) != WT_OTHER)
                          return -1;
                      break;

                  case '>':
                      if (con.length == 0 || offset == con.start)  return -1;
                      if (getWordType(target, con.start, con.limit, offset, opts) != WT_OTHER
                          || getPreviousWordType(target, con.start, con.limit, offset, opts) != WT_LETTER)
                          return -1;
                      break;
                  } // switch anchor type
                  op = op.next;
                  break;

              case Op.BACKREFERENCE:
                  {
                      int refno = op.getData();
                      if (refno <= 0 || refno >= this.nofparen)
                          throw new RuntimeException("Internal Error: Reference number must be more than zero: "+refno);
                      if (con.match.getBeginning(refno) < 0
                          || con.match.getEnd(refno) < 0)
                          return -1;                // ********
                      int o2 = con.match.getBeginning(refno);
                      int literallen = con.match.getEnd(refno)-o2;
                      if (!isSet(opts, IGNORE_CASE)) {
                          if (dx > 0) {
                              if (!regionMatches(target, offset, con.limit, o2, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatches(target, offset-literallen, con.limit, o2, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      } else {
                          if (dx > 0) {
                              if (!regionMatchesIgnoreCase(target, offset, con.limit, o2, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatchesIgnoreCase(target, offset-literallen, con.limit,
                                                           o2, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      }
                  }
                  op = op.next;
                  break;
              case Op.STRING:
                  {
                      String literal = op.getString();
                      int literallen = literal.length();
                      if (!isSet(opts, IGNORE_CASE)) {
                          if (dx > 0) {
                              if (!regionMatches(target, offset, con.limit, literal, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatches(target, offset-literallen, con.limit, literal, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      } else {
                          if (dx > 0) {
                              if (!regionMatchesIgnoreCase(target, offset, con.limit, literal, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatchesIgnoreCase(target, offset-literallen, con.limit,
                                                           literal, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      }
                  }
                  op = op.next;
                  break;

              case Op.CLOSURE:
                  {
                      /*
                       * Saves current position to avoid
                       * zero-width repeats.
                       */
                      int id = op.getData();
                      if (id >= 0) {
                          int previousOffset = con.offsets[id];
                          if (previousOffset < 0 || previousOffset != offset) {
                              con.offsets[id] = offset;
                          } else {
                              con.offsets[id] = -1;
                              op = op.next;
                              break;
                          }
                      }
                      int ret = this. matchString (con, op.getChild(), offset, dx, opts);
                      if (id >= 0)  con.offsets[id] = -1;
                      if (ret >= 0)  return ret;
                      op = op.next;
                  }
                  break;

              case Op.QUESTION:
                  {
                      int ret = this. matchString (con, op.getChild(), offset, dx, opts);
                      if (ret >= 0)  return ret;
                      op = op.next;
                  }
                  break;

              case Op.NONGREEDYCLOSURE:
              case Op.NONGREEDYQUESTION:
                  {
                      int ret = this. matchString (con, op.next, offset, dx, opts);
                      if (ret >= 0)  return ret;
                      op = op.getChild();
                  }
                  break;

              case Op.UNION:
                  for (int i = 0;  i < op.size();  i ++) {
                      int ret = this. matchString (con, op.elementAt(i), offset, dx, opts);
                      if (DEBUG) {
                          System.err.println("UNION: "+i+", ret="+ret);
                      }
                      if (ret >= 0)  return ret;
                  }
                  return -1;

              case Op.CAPTURE:
                  int refno = op.getData();
                  if (con.match != null && refno > 0) {
                      int save = con.match.getBeginning(refno);
                      con.match.setBeginning(refno, offset);
                      int ret = this. matchString (con, op.next, offset, dx, opts);
                      if (ret < 0)  con.match.setBeginning(refno, save);
                      return ret;
                  } else if (con.match != null && refno < 0) {
                      int index = -refno;
                      int save = con.match.getEnd(index);
                      con.match.setEnd(index, offset);
                      int ret = this. matchString (con, op.next, offset, dx, opts);
                      if (ret < 0)  con.match.setEnd(index, save);
                      return ret;
                  }
                  op = op.next;
                  break;

              case Op.LOOKAHEAD:
                  if (0 > this. matchString (con, op.getChild(), offset, 1, opts))  return -1;
                  op = op.next;
                  break;
              case Op.NEGATIVELOOKAHEAD:
                  if (0 <= this. matchString (con, op.getChild(), offset, 1, opts))  return -1;
                  op = op.next;
                  break;
              case Op.LOOKBEHIND:
                  if (0 > this. matchString (con, op.getChild(), offset, -1, opts))  return -1;
                  op = op.next;
                  break;
              case Op.NEGATIVELOOKBEHIND:
                  if (0 <= this. matchString (con, op.getChild(), offset, -1, opts))  return -1;
                  op = op.next;
                  break;

              case Op.INDEPENDENT:
                  {
                      int ret = this. matchString (con, op.getChild(), offset, dx, opts);
                      if (ret < 0)  return ret;
                      offset = ret;
                      op = op.next;
                  }
                  break;

              case Op.MODIFIER:
                  {
                      int localopts = opts;
                      localopts |= op.getData();
                      localopts &= ~op.getData2();
                      //System.err.println("MODIFIER: "+Integer.toString(opts, 16)+" -> "+Integer.toString(localopts, 16));
                      int ret = this. matchString (con, op.getChild(), offset, dx, localopts);
                      if (ret < 0)  return ret;
                      offset = ret;
                      op = op.next;
                  }
                  break;

              case Op.CONDITION:
                  {
                      Op.ConditionOp cop = (Op.ConditionOp)op;
                      boolean matchp = false;
                      if (cop.refNumber > 0) {
                          if (cop.refNumber >= this.nofparen)
                              throw new RuntimeException("Internal Error: Reference number must be more than zero: "+cop.refNumber);
                          matchp = con.match.getBeginning(cop.refNumber) >= 0
                                   && con.match.getEnd(cop.refNumber) >= 0;
                      } else {
                          matchp = 0 <= this. matchString (con, cop.condition, offset, dx, opts);
                      }

                      if (matchp) {
                          op = cop.yes;
                      } else if (cop.no != null) {
                          op = cop.no;
                      } else {
                          op = cop.next;
                      }
                  }
                  break;

              default:
                  throw new RuntimeException("Unknown operation type: "+op.type);
              } // switch (op.type)
          } // while
      }

      private static final int getPreviousWordType(String  target, int begin, int end,
                                                   int offset, int opts) {
          int ret = getWordType(target, begin, end, --offset, opts);
          while (ret == WT_IGNORE)
              ret = getWordType(target, begin, end, --offset, opts);
          return ret;
      }

      private static final int getWordType(String  target, int begin, int end,
                                           int offset, int opts) {
          if (offset < begin || offset >= end)  return WT_OTHER;
          return getWordType0( target .charAt(  offset ) , opts);
      }


      private static final boolean regionMatches(String text, int offset, int limit,
                                                 String part, int partlen) {
          if (limit-offset < partlen)  return false;
          return text.regionMatches(offset, part, 0, partlen);
      }

      private static final boolean regionMatches(String text, int offset, int limit,
                                                 int offset2, int partlen) {
          if (limit-offset < partlen)  return false;
          return text.regionMatches(offset, text, offset2, partlen);
      }

      private static final boolean regionMatchesIgnoreCase(String text, int offset, int limit,
                                                           String part, int partlen) {
          return text.regionMatches(true, offset, part, 0, partlen);
      }

      private static final boolean regionMatchesIgnoreCase(String text, int offset, int limit,
                                                           int offset2, int partlen) {
          if (limit-offset < partlen)  return false;
          return text.regionMatches(true, offset, text, offset2, partlen);
      }







      /**
       * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
       *
       * @return true if the target is matched to this regular expression.
       */
      public boolean matches(CharacterIterator target) {
          return this.matches(target, (Match)null);
      }


      /**
       * Checks whether the <var>target</var> text <strong>contains</strong> this pattern or not.
       *
       * @param match A Match instance for storing matching result.
       * @return Offset of the start position in <VAR>target</VAR>; or -1 if not match.
       */
      public boolean matches(CharacterIterator  target, Match match) {
          int start = target.getBeginIndex();
          int end = target.getEndIndex();



          synchronized (this) {
              if (this.operations == null)
                  this.prepare();
              if (this.context == null)
                  this.context = new Context();
          }
          Context con = null;
          synchronized (this.context) {
              con = this.context.inuse ? new Context() : this.context;
              con.reset(target, start, end, this.numberOfClosures);
          }
          if (match != null) {
              match.setNumberOfGroups(this.nofparen);
              match.setSource(target);
          } else if (this.hasBackReferences) {
              match = new Match();
              match.setNumberOfGroups(this.nofparen);
              // Need not to call setSource() because
              // a caller can not access this match instance.
          }
          con.match = match;

          if (RegularExpression.isSet(this.options, XMLSCHEMA_MODE)) {
              int matchEnd = this. matchCharacterIterator (con, this.operations, con.start, 1, this.options);
              //System.err.println("DEBUG: matchEnd="+matchEnd);
              if (matchEnd == con.limit) {
                  if (con.match != null) {
                      con.match.setBeginning(0, con.start);
                      con.match.setEnd(0, matchEnd);
                  }
                  con.inuse = false;
                  return true;
              }
              return false;
          }

          /*
           * The pattern has only fixed string.
           * The engine uses Boyer-Moore.
           */
          if (this.fixedStringOnly) {
              //System.err.println("DEBUG: fixed-only: "+this.fixedString);
              int o = this.fixedStringTable.matches(target, con.start, con.limit);
              if (o >= 0) {
                  if (con.match != null) {
                      con.match.setBeginning(0, o);
                      con.match.setEnd(0, o+this.fixedString.length());
                  }
                  con.inuse = false;
                  return true;
              }
              con.inuse = false;
              return false;
          }

          /*
           * The pattern contains a fixed string.
           * The engine checks with Boyer-Moore whether the text contains the fixed string or not.
           * If not, it return with false.
           */
          if (this.fixedString != null) {
              int o = this.fixedStringTable.matches(target, con.start, con.limit);
              if (o < 0) {
                  //System.err.println("Non-match in fixed-string search.");
                  con.inuse = false;
                  return false;
              }
          }

          int limit = con.limit-this.minlength;
          int matchStart;
          int matchEnd = -1;

          /*
           * Checks whether the expression starts with ".*".
           */
          if (this.operations != null
              && this.operations.type == Op.CLOSURE && this.operations.getChild().type == Op.DOT) {
              if (isSet(this.options, SINGLE_LINE)) {
                  matchStart = con.start;
                  matchEnd = this. matchCharacterIterator (con, this.operations, con.start, 1, this.options);
              } else {
                  boolean previousIsEOL = true;
                  for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                      int ch =  target .setIndex(  matchStart ) ;
                      if (isEOLChar(ch)) {
                          previousIsEOL = true;
                      } else {
                          if (previousIsEOL) {
                              if (0 <= (matchEnd = this. matchCharacterIterator (con, this.operations,
                                                                                 matchStart, 1, this.options)))
                                  break;
                          }
                          previousIsEOL = false;
                      }
                  }
              }
          }

          /*
           * Optimization against the first character.
           */
          else if (this.firstChar != null) {
              //System.err.println("DEBUG: with firstchar-matching: "+this.firstChar);
              RangeToken range = this.firstChar;
              if (RegularExpression.isSet(this.options, IGNORE_CASE)) {
                  range = this.firstChar.getCaseInsensitiveToken();
                  for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                      int ch =  target .setIndex(  matchStart ) ;
                      if (REUtil.isHighSurrogate(ch) && matchStart+1 < con.limit) {
                          ch = REUtil.composeFromSurrogates(ch,  target .setIndex(  matchStart+1 ) );
                          if (!range.match(ch))  continue;
                      } else {
                          if (!range.match(ch)) {
                              char ch1 = Character.toUpperCase((char)ch);
                              if (!range.match(ch1))
                                  if (!range.match(Character.toLowerCase(ch1)))
                                      continue;
                          }
                      }
                      if (0 <= (matchEnd = this. matchCharacterIterator (con, this.operations,
                                                                         matchStart, 1, this.options)))
                          break;
                  }
              } else {
                  for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                      int ch =  target .setIndex(  matchStart ) ;
                      if (REUtil.isHighSurrogate(ch) && matchStart+1 < con.limit)
                          ch = REUtil.composeFromSurrogates(ch,  target .setIndex(  matchStart+1 ) );
                      if (!range.match(ch))  continue;
                      if (0 <= (matchEnd = this. matchCharacterIterator (con, this.operations,
                                                                         matchStart, 1, this.options)))
                          break;
                  }
              }
          }

          /*
           * Straightforward matching.
           */
          else {
              for (matchStart = con.start;  matchStart <= limit;  matchStart ++) {
                  if (0 <= (matchEnd = this. matchCharacterIterator (con, this.operations, matchStart, 1, this.options)))
                      break;
              }
          }

          if (matchEnd >= 0) {
              if (con.match != null) {
                  con.match.setBeginning(0, matchStart);
                  con.match.setEnd(0, matchEnd);
              }
              con.inuse = false;
              return true;
          } else {
              con.inuse = false;
              return false;
          }
      }

      /**
       * @return -1 when not match; offset of the end of matched string when match.
       */
      private int matchCharacterIterator (Context con, Op op, int offset, int dx, int opts) {


          CharacterIterator target = con.ciTarget;






          while (true) {
              if (op == null)
                  return isSet(opts, XMLSCHEMA_MODE) && offset != con.limit ? -1 : offset;
              if (offset > con.limit || offset < con.start)
                  return -1;
              switch (op.type) {
              case Op.CHAR:
                  if (isSet(opts, IGNORE_CASE)) {
                      int ch = op.getData();
                      if (dx > 0) {
                          if (offset >= con.limit || !matchIgnoreCase(ch,  target .setIndex(  offset ) ))
                              return -1;
                          offset ++;
                      } else {
                          int o1 = offset-1;
                          if (o1 >= con.limit || o1 < 0 || !matchIgnoreCase(ch,  target .setIndex(  o1 ) ))
                              return -1;
                          offset = o1;
                      }
                  } else {
                      int ch = op.getData();
                      if (dx > 0) {
                          if (offset >= con.limit || ch !=  target .setIndex(  offset ) )
                              return -1;
                          offset ++;
                      } else {
                          int o1 = offset-1;
                          if (o1 >= con.limit || o1 < 0 || ch !=  target .setIndex(  o1 ) )
                              return -1;
                          offset = o1;
                      }
                  }
                  op = op.next;
                  break;

              case Op.DOT:
                  if (dx > 0) {
                      if (offset >= con.limit)
                          return -1;
                      int ch =  target .setIndex(  offset ) ;
                      if (isSet(opts, SINGLE_LINE)) {
                          if (REUtil.isHighSurrogate(ch) && offset+1 < con.limit)
                              offset ++;
                      } else {
                          if (REUtil.isHighSurrogate(ch) && offset+1 < con.limit)
                              ch = REUtil.composeFromSurrogates(ch,  target .setIndex(  ++offset ) );
                          if (isEOLChar(ch))
                              return -1;
                      }
                      offset ++;
                  } else {
                      int o1 = offset-1;
                      if (o1 >= con.limit || o1 < 0)
                          return -1;
                      int ch =  target .setIndex(  o1 ) ;
                      if (isSet(opts, SINGLE_LINE)) {
                          if (REUtil.isLowSurrogate(ch) && o1-1 >= 0)
                              o1 --;
                      } else {
                          if (REUtil.isLowSurrogate(ch) && o1-1 >= 0)
                              ch = REUtil.composeFromSurrogates( target .setIndex(  --o1 ) , ch);
                          if (!isEOLChar(ch))
                              return -1;
                      }
                      offset = o1;
                  }
                  op = op.next;
                  break;

              case Op.RANGE:
              case Op.NRANGE:
                  if (dx > 0) {
                      if (offset >= con.limit)
                          return -1;
                      int ch =  target .setIndex(  offset ) ;
                      if (REUtil.isHighSurrogate(ch) && offset+1 < con.limit)
                          ch = REUtil.composeFromSurrogates(ch,  target .setIndex(  ++offset ) );
                      RangeToken tok = op.getToken();
                      if (isSet(opts, IGNORE_CASE)) {
                          tok = tok.getCaseInsensitiveToken();
                          if (!tok.match(ch)) {
                              if (ch >= 0x10000)  return -1;
                              char uch;
                              if (!tok.match(uch = Character.toUpperCase((char)ch))
                                  && !tok.match(Character.toLowerCase(uch)))
                                  return -1;
                          }
                      } else {
                          if (!tok.match(ch))  return -1;
                      }
                      offset ++;
                  } else {
                      int o1 = offset-1;
                      if (o1 >= con.limit || o1 < 0)
                          return -1;
                      int ch =  target .setIndex(  o1 ) ;
                      if (REUtil.isLowSurrogate(ch) && o1-1 >= 0)
                          ch = REUtil.composeFromSurrogates( target .setIndex(  --o1 ) , ch);
                      RangeToken tok = op.getToken();
                      if (isSet(opts, IGNORE_CASE)) {
                          tok = tok.getCaseInsensitiveToken();
                          if (!tok.match(ch)) {
                              if (ch >= 0x10000)  return -1;
                              char uch;
                              if (!tok.match(uch = Character.toUpperCase((char)ch))
                                  && !tok.match(Character.toLowerCase(uch)))
                                  return -1;
                          }
                      } else {
                          if (!tok.match(ch))  return -1;
                      }
                      offset = o1;
                  }
                  op = op.next;
                  break;

              case Op.ANCHOR:
                  boolean go = false;
                  switch (op.getData()) {
                  case '^':
                      if (isSet(opts, MULTIPLE_LINES)) {
                          if (!(offset == con.start
                                || offset > con.start && isEOLChar( target .setIndex(  offset-1 ) )))
                              return -1;
                      } else {
                          if (offset != con.start)
                              return -1;
                      }
                      break;

                  case '@':                         // Internal use only.
                      // The @ always matches line beginnings.
                      if (!(offset == con.start
                            || offset > con.start && isEOLChar( target .setIndex(  offset-1 ) )))
                          return -1;
                      break;

                  case '$':
                      if (isSet(opts, MULTIPLE_LINES)) {
                          if (!(offset == con.limit
                                || offset < con.limit && isEOLChar( target .setIndex(  offset ) )))
                              return -1;
                      } else {
                          if (!(offset == con.limit
                                || offset+1 == con.limit && isEOLChar( target .setIndex(  offset ) )
                                || offset+2 == con.limit &&  target .setIndex(  offset )  == CARRIAGE_RETURN
                                &&  target .setIndex(  offset+1 )  == LINE_FEED))
                              return -1;
                      }
                      break;

                  case 'A':
                      if (offset != con.start)  return -1;
                      break;

                  case 'Z':
                      if (!(offset == con.limit
                            || offset+1 == con.limit && isEOLChar( target .setIndex(  offset ) )
                            || offset+2 == con.limit &&  target .setIndex(  offset )  == CARRIAGE_RETURN
                            &&  target .setIndex(  offset+1 )  == LINE_FEED))
                          return -1;
                      break;

                  case 'z':
                      if (offset != con.limit)  return -1;
                      break;

                  case 'b':
                      if (con.length == 0)  return -1;
                      {
                          int after = getWordType(target, con.start, con.limit, offset, opts);
                          if (after == WT_IGNORE)  return -1;
                          int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                          if (after == before)  return -1;
                      }
                      break;

                  case 'B':
                      if (con.length == 0)
                          go = true;
                      else {
                          int after = getWordType(target, con.start, con.limit, offset, opts);
                          go = after == WT_IGNORE
                               || after == getPreviousWordType(target, con.start, con.limit, offset, opts);
                      }
                      if (!go)  return -1;
                      break;

                  case '<':
                      if (con.length == 0 || offset == con.limit)  return -1;
                      if (getWordType(target, con.start, con.limit, offset, opts) != WT_LETTER
                          || getPreviousWordType(target, con.start, con.limit, offset, opts) != WT_OTHER)
                          return -1;
                      break;

                  case '>':
                      if (con.length == 0 || offset == con.start)  return -1;
                      if (getWordType(target, con.start, con.limit, offset, opts) != WT_OTHER
                          || getPreviousWordType(target, con.start, con.limit, offset, opts) != WT_LETTER)
                          return -1;
                      break;
                  } // switch anchor type
                  op = op.next;
                  break;

              case Op.BACKREFERENCE:
                  {
                      int refno = op.getData();
                      if (refno <= 0 || refno >= this.nofparen)
                          throw new RuntimeException("Internal Error: Reference number must be more than zero: "+refno);
                      if (con.match.getBeginning(refno) < 0
                          || con.match.getEnd(refno) < 0)
                          return -1;                // ********
                      int o2 = con.match.getBeginning(refno);
                      int literallen = con.match.getEnd(refno)-o2;
                      if (!isSet(opts, IGNORE_CASE)) {
                          if (dx > 0) {
                              if (!regionMatches(target, offset, con.limit, o2, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatches(target, offset-literallen, con.limit, o2, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      } else {
                          if (dx > 0) {
                              if (!regionMatchesIgnoreCase(target, offset, con.limit, o2, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatchesIgnoreCase(target, offset-literallen, con.limit,
                                                           o2, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      }
                  }
                  op = op.next;
                  break;
              case Op.STRING:
                  {
                      String literal = op.getString();
                      int literallen = literal.length();
                      if (!isSet(opts, IGNORE_CASE)) {
                          if (dx > 0) {
                              if (!regionMatches(target, offset, con.limit, literal, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatches(target, offset-literallen, con.limit, literal, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      } else {
                          if (dx > 0) {
                              if (!regionMatchesIgnoreCase(target, offset, con.limit, literal, literallen))
                                  return -1;
                              offset += literallen;
                          } else {
                              if (!regionMatchesIgnoreCase(target, offset-literallen, con.limit,
                                                           literal, literallen))
                                  return -1;
                              offset -= literallen;
                          }
                      }
                  }
                  op = op.next;
                  break;

              case Op.CLOSURE:
                  {
                      /*
                       * Saves current position to avoid
                       * zero-width repeats.
                       */
                      int id = op.getData();
                      if (id >= 0) {
                          int previousOffset = con.offsets[id];
                          if (previousOffset < 0 || previousOffset != offset) {
                              con.offsets[id] = offset;
                          } else {
                              con.offsets[id] = -1;
                              op = op.next;
                              break;
                          }
                      }
                      
                      int ret = this. matchCharacterIterator (con, op.getChild(), offset, dx, opts);
                      if (id >= 0)  con.offsets[id] = -1;
                      if (ret >= 0)  return ret;
                      op = op.next;
                  }
                  break;

              case Op.QUESTION:
                  {
                      int ret = this. matchCharacterIterator (con, op.getChild(), offset, dx, opts);
                      if (ret >= 0)  return ret;
                      op = op.next;
                  }
                  break;

              case Op.NONGREEDYCLOSURE:
              case Op.NONGREEDYQUESTION:
                  {
                      int ret = this. matchCharacterIterator (con, op.next, offset, dx, opts);
                      if (ret >= 0)  return ret;
                      op = op.getChild();
                  }
                  break;

              case Op.UNION:
                  for (int i = 0;  i < op.size();  i ++) {
                      int ret = this. matchCharacterIterator (con, op.elementAt(i), offset, dx, opts);
                      if (DEBUG) {
                          System.err.println("UNION: "+i+", ret="+ret);
                      }
                      if (ret >= 0)  return ret;
                  }
                  return -1;

              case Op.CAPTURE:
                  int refno = op.getData();
                  if (con.match != null && refno > 0) {
                      int save = con.match.getBeginning(refno);
                      con.match.setBeginning(refno, offset);
                      int ret = this. matchCharacterIterator (con, op.next, offset, dx, opts);
                      if (ret < 0)  con.match.setBeginning(refno, save);
                      return ret;
                  } else if (con.match != null && refno < 0) {
                      int index = -refno;
                      int save = con.match.getEnd(index);
                      con.match.setEnd(index, offset);
                      int ret = this. matchCharacterIterator (con, op.next, offset, dx, opts);
                      if (ret < 0)  con.match.setEnd(index, save);
                      return ret;
                  }
                  op = op.next;
                  break;

              case Op.LOOKAHEAD:
                  if (0 > this. matchCharacterIterator (con, op.getChild(), offset, 1, opts))  return -1;
                  op = op.next;
                  break;
              case Op.NEGATIVELOOKAHEAD:
                  if (0 <= this. matchCharacterIterator (con, op.getChild(), offset, 1, opts))  return -1;
                  op = op.next;
                  break;
              case Op.LOOKBEHIND:
                  if (0 > this. matchCharacterIterator (con, op.getChild(), offset, -1, opts))  return -1;
                  op = op.next;
                  break;
              case Op.NEGATIVELOOKBEHIND:
                  if (0 <= this. matchCharacterIterator (con, op.getChild(), offset, -1, opts))  return -1;
                  op = op.next;
                  break;

              case Op.INDEPENDENT:
                  {
                      int ret = this. matchCharacterIterator (con, op.getChild(), offset, dx, opts);
                      if (ret < 0)  return ret;
                      offset = ret;
                      op = op.next;
                  }
                  break;

              case Op.MODIFIER:
                  {
                      int localopts = opts;
                      localopts |= op.getData();
                      localopts &= ~op.getData2();
                      //System.err.println("MODIFIER: "+Integer.toString(opts, 16)+" -> "+Integer.toString(localopts, 16));
                      int ret = this. matchCharacterIterator (con, op.getChild(), offset, dx, localopts);
                      if (ret < 0)  return ret;
                      offset = ret;
                      op = op.next;
                  }
                  break;

              case Op.CONDITION:
                  {
                      Op.ConditionOp cop = (Op.ConditionOp)op;
                      boolean matchp = false;
                      if (cop.refNumber > 0) {
                          if (cop.refNumber >= this.nofparen)
                              throw new RuntimeException("Internal Error: Reference number must be more than zero: "+cop.refNumber);
                          matchp = con.match.getBeginning(cop.refNumber) >= 0
                                   && con.match.getEnd(cop.refNumber) >= 0;
                      } else {
                          matchp = 0 <= this. matchCharacterIterator (con, cop.condition, offset, dx, opts);
                      }

                      if (matchp) {
                          op = cop.yes;
                      } else if (cop.no != null) {
                          op = cop.no;
                      } else {
                          op = cop.next;
                      }
                  }
                  break;

              default:
                  throw new RuntimeException("Unknown operation type: "+op.type);
              } // switch (op.type)
          } // while
      }

      private static final int getPreviousWordType(CharacterIterator  target, int begin, int end,
                                                   int offset, int opts) {
          int ret = getWordType(target, begin, end, --offset, opts);
          while (ret == WT_IGNORE)
              ret = getWordType(target, begin, end, --offset, opts);
          return ret;
      }

      private static final int getWordType(CharacterIterator  target, int begin, int end,
                                           int offset, int opts) {
          if (offset < begin || offset >= end)  return WT_OTHER;
          return getWordType0( target .setIndex(  offset ) , opts);
      }



      private static final boolean regionMatches(CharacterIterator  target, int offset, int limit,
                                                 String part, int partlen) {
          if (offset < 0)  return false;
          if (limit-offset < partlen)
              return false;
          int i = 0;
          while (partlen-- > 0) {
              if ( target .setIndex(  offset++ )  != part.charAt(i++))
                  return false;
          }
          return true;
      }

      private static final boolean regionMatches(CharacterIterator  target, int offset, int limit,
                                                 int offset2, int partlen) {
          if (offset < 0)  return false;
          if (limit-offset < partlen)
              return false;
          int i = offset2;
          while (partlen-- > 0) {
              if ( target .setIndex(  offset++ )  !=  target .setIndex(  i++ ) )
                  return false;
          }
          return true;
      }

      /**
       * @see java.lang.String#regionMatches(int, String, int, int)
       */
      private static final boolean regionMatchesIgnoreCase(CharacterIterator  target, int offset, int limit,
                                                           String part, int partlen) {
          if (offset < 0)  return false;
          if (limit-offset < partlen)
              return false;
          int i = 0;
          while (partlen-- > 0) {
              char ch1 =  target .setIndex(  offset++ ) ;
              char ch2 = part.charAt(i++);
              if (ch1 == ch2)
                  continue;
              char uch1 = Character.toUpperCase(ch1);
              char uch2 = Character.toUpperCase(ch2);
              if (uch1 == uch2)
                  continue;
              if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2))
                  return false;
          }
          return true;
      }

      private static final boolean regionMatchesIgnoreCase(CharacterIterator  target, int offset, int limit,
                                                           int offset2, int partlen) {
          if (offset < 0)  return false;
          if (limit-offset < partlen)
              return false;
          int i = offset2;
          while (partlen-- > 0) {
              char ch1 =  target .setIndex(  offset++ ) ;
              char ch2 =  target .setIndex(  i++ ) ;
              if (ch1 == ch2)
                  continue;
              char uch1 = Character.toUpperCase(ch1);
              char uch2 = Character.toUpperCase(ch2);
              if (uch1 == uch2)
                  continue;
              if (Character.toLowerCase(uch1) != Character.toLowerCase(uch2))
                  return false;
          }
          return true;
      }




      // ================================================================

      /**
       * A regular expression.
       * @serial
       */
      String regex;
      /**
       * @serial
       */
      int options;

      /**
       * The number of parenthesis in the regular expression.
       * @serial
       */
      int nofparen;
      /**
       * Internal representation of the regular expression.
       * @serial
       */
      Token tokentree;

      boolean hasBackReferences = false;

      transient int minlength;
      transient Op operations = null;
      transient int numberOfClosures;
      transient Context context = null;
      transient RangeToken firstChar = null;

      transient String fixedString = null;
      transient int fixedStringOptions;
      transient BMPattern fixedStringTable = null;
      transient boolean fixedStringOnly = false;


      static final class Context {
          CharacterIterator ciTarget;
          String strTarget;
          char[] charTarget;
          int start;
          int limit;
          int length;
          Match match;
          boolean inuse = false;
          int[] offsets;

          Context() {
            super();
          }

          private void resetCommon(int nofclosures) {
              this.length = this.limit-this.start;
              this.inuse = true;
              this.match = null;
              if (this.offsets == null || this.offsets.length != nofclosures)
                  this.offsets = new int[nofclosures];
              for (int i = 0;  i < nofclosures;  i ++)  this.offsets[i] = -1;
          }
          void reset(CharacterIterator target, int start, int limit, int nofclosures) {
              this.ciTarget = target;
              this.start = start;
              this.limit = limit;
              this.resetCommon(nofclosures);
          }
          void reset(String target, int start, int limit, int nofclosures) {
              this.strTarget = target;
              this.start = start;
              this.limit = limit;
              this.resetCommon(nofclosures);
          }
          void reset(char[] target, int start, int limit, int nofclosures) {
              this.charTarget = target;
              this.start = start;
              this.limit = limit;
              this.resetCommon(nofclosures);
          }
      }

      /**
       * Prepares for matching.  This method is called just before starting matching.
       */
      void prepare() {
          if (Op.COUNT)  Op.nofinstances = 0;
          this.compile(this.tokentree);
          /*
          if  (this.operations.type == Op.CLOSURE && this.operations.getChild().type == Op.DOT) { // .*
              Op anchor = Op.createAnchor(isSet(this.options, SINGLE_LINE) ? 'A' : '@');
              anchor.next = this.operations;
              this.operations = anchor;
          }
          */
          if (Op.COUNT)  System.err.println("DEBUG: The number of operations: "+Op.nofinstances);

          this.minlength = this.tokentree.getMinLength();

          this.firstChar = null;
          if (!isSet(this.options, PROHIBIT_HEAD_CHARACTER_OPTIMIZATION)
              && !isSet(this.options, XMLSCHEMA_MODE)) {
              RangeToken firstChar = Token.createRange();
              int fresult = this.tokentree.analyzeFirstCharacter(firstChar, this.options);
              if (fresult == Token.FC_TERMINAL) {
                  firstChar.compactRanges();
                  this.firstChar = firstChar;
                  if (DEBUG)
                      System.err.println("DEBUG: Use the first character optimization: "+firstChar);
              }
          }

          if (this.operations != null
              && (this.operations.type == Op.STRING || this.operations.type == Op.CHAR)
              && this.operations.next == null) {
              if (DEBUG)
                  System.err.print(" *** Only fixed string! *** ");
              this.fixedStringOnly = true;
              if (this.operations.type == Op.STRING)
                  this.fixedString = this.operations.getString();
              else if (this.operations.getData() >= 0x10000) { // Op.CHAR
                  this.fixedString = REUtil.decomposeToSurrogates(this.operations.getData());
              } else {
                  char[] ac = new char[1];
                  ac[0] = (char)this.operations.getData();
                  this.fixedString = new String(ac);
              }
              this.fixedStringOptions = this.options;
              this.fixedStringTable = new BMPattern(this.fixedString, 256,
                                                    isSet(this.fixedStringOptions, IGNORE_CASE));
          } else if (!isSet(this.options, PROHIBIT_FIXED_STRING_OPTIMIZATION)
                     && !isSet(this.options, XMLSCHEMA_MODE)) {
              Token.FixedStringContainer container = new Token.FixedStringContainer();
              this.tokentree.findFixedString(container, this.options);
              this.fixedString = container.token == null ? null : container.token.getString();
              this.fixedStringOptions = container.options;
              if (this.fixedString != null && this.fixedString.length() < 2)
                  this.fixedString = null;
              // This pattern has a fixed string of which length is more than one.
              if (this.fixedString != null) {
                  this.fixedStringTable = new BMPattern(this.fixedString, 256,
                                                        isSet(this.fixedStringOptions, IGNORE_CASE));
                  if (DEBUG) {
                      System.err.println("DEBUG: The longest fixed string: "+this.fixedString.length()
                                         +"/" //+this.fixedString
                                         +"/"+REUtil.createOptionString(this.fixedStringOptions));
                      System.err.print("String: ");
                      REUtil.dumpString(this.fixedString);
                  }
              }
          }
      }

      /**
       * An option.
       * If you specify this option, <span class="REGEX"><kbd>(</kbd><var>X</var><kbd>)</kbd></span>
       * captures matched text, and <span class="REGEX"><kbd>(:?</kbd><var>X</var><kbd>)</kbd></span>
       * does not capture.
       *
       * @see #RegularExpression(java.lang.String,int)
       * @see #setPattern(java.lang.String,int)
      static final int MARK_PARENS = 1<<0;
       */

      /**
       * "i"
       */
      static final int IGNORE_CASE = 1<<1;

      /**
       * "s"
       */
      static final int SINGLE_LINE = 1<<2;

      /**
       * "m"
       */
      static final int MULTIPLE_LINES = 1<<3;

      /**
       * "x"
       */
      static final int EXTENDED_COMMENT = 1<<4;

      /**
       * This option redefines <span class="REGEX"><kbd>\d \D \w \W \s \S</kbd></span>.
       *
       * @see #RegularExpression(String)
       * @see #setPattern(java.lang.String,int)
       * @see #UNICODE_WORD_BOUNDARY
       */
      static final int USE_UNICODE_CATEGORY = 1<<5; // "u"

      /**
       * An option.
       * This enables to process locale-independent word boundary for <span class="REGEX"><kbd>\b \B \&lt; \></kbd></span>.
       * <p>By default, the engine considers a position between a word character
       * (<span class="REGEX"><Kbd>\w</kbd></span>) and a non word character
       * is a word boundary.
       * <p>By this option, the engine checks word boundaries with the method of
       * 'Unicode Regular Expression Guidelines' Revision 4.
       *
       * @see RegEx#RegEx()
       * @see #setPattern(java.lang.String,int)
       */
      static final int UNICODE_WORD_BOUNDARY = 1<<6; // "w"

      /**
       * "H"
       */
      static final int PROHIBIT_HEAD_CHARACTER_OPTIMIZATION = 1<<7;
      /**
       * "F"
       */
      static final int PROHIBIT_FIXED_STRING_OPTIMIZATION = 1<<8;
      /**
       * "X". XML Schema mode.
       */
      static final int XMLSCHEMA_MODE = 1<<9;
      /**
       * ",".
       */
      static final int SPECIAL_COMMA = 1<<10;


      private static final boolean isSet(int options, int flag) {
          return (options & flag) == flag;
      }

      /**
       * Creates a new RegularExpression instance.
       *
       * @param regex A regular expression
       * @exception ParseException <VAR>regex</VAR> is not conforming to the syntax.
       */
      public RegularExpression(String regex) throws ParseException {
          this.setPattern(regex, null);
      }

      /**
       * Creates a new RegularExpression instance with options.
       *
       * @param regex A regular expression
       * @param options A String consisted of "i" "m" "s" "u" "w" "," "X"
       * @exception ParseException <VAR>regex</VAR> is not conforming to the syntax.
       */
      public RegularExpression(String regex, String options) throws ParseException {
          this.setPattern(regex, options);
      }

      RegularExpression(String regex, Token tok, int parens, boolean hasBackReferences, int options) {
          this.regex = regex;
          this.tokentree = tok;
          this.nofparen = parens;
          this.options = options;
          this.hasBackReferences = hasBackReferences;
      }

      /**
       *
       */
      public void setPattern(String newPattern) throws ParseException {
          this.setPattern(newPattern, this.options);
      }

      private void setPattern(String newPattern, int options) throws ParseException {
          this.regex = newPattern;
          this.options = options;
          RegexParser rp = RegularExpression.isSet(this.options, RegularExpression.XMLSCHEMA_MODE)
                           ? new ParserForXMLSchema() : new RegexParser();
          this.tokentree = rp.parse(this.regex, this.options);
          this.nofparen = rp.parennumber;
          this.hasBackReferences = rp.hasBackReferences;

          this.operations = null;
          this.context = null;
      }
      /**
       *
       */
      public void setPattern(String newPattern, String options) throws ParseException {
          this.setPattern(newPattern, REUtil.parseOptions(options));
      }

      /**
       *
       */
      public String getPattern() {
          return this.regex;
      }

      /**
       * Represents this instance in String.
       */
      @Override
      public String toString() {
          return this.tokentree.toString(this.options);
      }

      /**
       * Returns a option string.
       * The order of letters in it may be different from a string specified
       * in a constructor or <code>setPattern()</code>.
       *
       * @see #RegularExpression(String, String)
       * @see #setPattern(java.lang.String,java.lang.String)
       */
      public String getOptions() {
          return REUtil.createOptionString(this.options);
      }

      /**
       *  Return true if patterns are the same and the options are equivalent.
       */
      @Override
      public boolean equals(Object obj) {
          if (obj == null)  return false;
          if (!(obj instanceof RegularExpression))
              return false;
          RegularExpression r = (RegularExpression)obj;
          return this.regex.equals(r.regex) && this.options == r.options;
      }

      boolean equals(String pattern, int options) {
          return this.regex.equals(pattern) && this.options == options;
      }

      /**
       *
       */
      @Override
      public int hashCode() {
          return (this.regex+"/"+this.getOptions()).hashCode();
      }

      /**
       * Return the number of regular expression groups.
       * This method returns 1 when the regular expression has no capturing-parenthesis.
       *
       */
      public int getNumberOfGroups() {
          return this.nofparen;
      }

      // ================================================================

      private static final int WT_IGNORE = 0;
      private static final int WT_LETTER = 1;
      private static final int WT_OTHER = 2;
      private static final int getWordType0(char ch, int opts) {
          if (!isSet(opts, UNICODE_WORD_BOUNDARY)) {
              if (isSet(opts, USE_UNICODE_CATEGORY)) {
                  return (Token.getRange("IsWord", true).match(ch)) ? WT_LETTER : WT_OTHER;
              }
              return isWordChar(ch) ? WT_LETTER : WT_OTHER;
          }

          /*
          switch (Character.getType(ch)) {
          case Character.UPPERCASE_LETTER:      // L
          case Character.LOWERCASE_LETTER:      // L
          case Character.TITLECASE_LETTER:      // L
          case Character.MODIFIER_LETTER:       // L
          case Character.OTHER_LETTER:          // L
          case Character.LETTER_NUMBER:         // N
          case Character.DECIMAL_DIGIT_NUMBER:  // N
          case Character.OTHER_NUMBER:          // N
          case Character.COMBINING_SPACING_MARK: // Mc
              return WT_LETTER;

          case Character.FORMAT:                // Cf
          case Character.NON_SPACING_MARK:      // Mn
          case Character.ENCLOSING_MARK:        // Mc
              return WT_IGNORE;

          case Character.CONTROL:               // Cc
              switch (ch) {
              case '\t':
              case '\n':
              case '\u000B':
              case '\f':
              case '\r':
                  return WT_OTHER;
              default:
                  return WT_IGNORE;
              }

          default:
              return WT_OTHER;
          }
          */
          // TODO
          return WT_OTHER;
      }

      // ================================================================

      static final int LINE_FEED = 0x000A;
      static final int CARRIAGE_RETURN = 0x000D;
      static final int LINE_SEPARATOR = 0x2028;
      static final int PARAGRAPH_SEPARATOR = 0x2029;

      private static final boolean isEOLChar(int ch) {
          return ch == LINE_FEED || ch == CARRIAGE_RETURN || ch == LINE_SEPARATOR
          || ch == PARAGRAPH_SEPARATOR;
      }

      private static final boolean isWordChar(int ch) { // Legacy word characters
          if (ch == '_')  return true;
          if (ch < '0')  return false;
          if (ch > 'z')  return false;
          if (ch <= '9')  return true;
          if (ch < 'A')  return false;
          if (ch <= 'Z')  return true;
          if (ch < 'a')  return false;
          return true;
      }

      private static final boolean matchIgnoreCase(int chardata, int ch) {
          if (chardata == ch)  return true;
          if (chardata > 0xffff || ch > 0xffff)  return false;
          char uch1 = Character.toUpperCase((char)chardata);
          char uch2 = Character.toUpperCase((char)ch);
          if (uch1 == uch2)  return true;
          return Character.toLowerCase(uch1) == Character.toLowerCase(uch2);
      }
  }
  
  public static class ParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    int location;

    /*
    public ParseException(String mes) {
        this(mes, -1);
    }
    */
    /**
     *
     */
    public ParseException(String mes, int location) {
        super(mes);
        this.location = location;
    }

    /**
     *
     * @return -1 if location information is not available.
     */
    public int getLocation() {
        return this.location;
    }
}

  static class Op {
    static final int DOT = 0;
    static final int CHAR = 1;                  // Single character
    static final int RANGE = 3;                 // [a-zA-Z]
    static final int NRANGE = 4;                // [^a-zA-Z]
    static final int ANCHOR = 5;                // ^ $ ...
    static final int STRING = 6;                // literal String 
    static final int CLOSURE = 7;               // X*
    static final int NONGREEDYCLOSURE = 8;      // X*?
    static final int QUESTION = 9;              // X?
    static final int NONGREEDYQUESTION = 10;    // X??
    static final int UNION = 11;                // X|Y
    static final int CAPTURE = 15;              // ( and )
    static final int BACKREFERENCE = 16;        // \1 \2 ...
    static final int LOOKAHEAD = 20;            // (?=...)
    static final int NEGATIVELOOKAHEAD = 21;    // (?!...)
    static final int LOOKBEHIND = 22;           // (?<=...)
    static final int NEGATIVELOOKBEHIND = 23;   // (?<!...)
    static final int INDEPENDENT = 24;          // (?>...)
    static final int MODIFIER = 25;             // (?ims-ims:...)
    static final int CONDITION = 26;            // (?(..)yes|no)

    static int nofinstances = 0;
    static final boolean COUNT = false;

    static Op createDot() {
        if (Op.COUNT)  Op.nofinstances ++;
        return new Op(Op.DOT);
    }
    static CharOp createChar(int data) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new CharOp(Op.CHAR, data);
    }
    static CharOp createAnchor(int data) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new CharOp(Op.ANCHOR, data);
    }
    static CharOp createCapture(int number, Op next) {
        if (Op.COUNT)  Op.nofinstances ++;
        CharOp op = new CharOp(Op.CAPTURE, number);
        op.next = next;
        return op;
    }
    static UnionOp createUnion(int size) {
        if (Op.COUNT)  Op.nofinstances ++;
        //System.err.println("Creates UnionOp");
        return new UnionOp(Op.UNION, size);
    }
    static ChildOp createClosure(int id) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new ModifierOp(Op.CLOSURE, id, -1);
    }
    static ChildOp createNonGreedyClosure() {
        if (Op.COUNT)  Op.nofinstances ++;
        return new ChildOp(Op.NONGREEDYCLOSURE);
    }
    static ChildOp createQuestion(boolean nongreedy) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new ChildOp(nongreedy ? Op.NONGREEDYQUESTION : Op.QUESTION);
    }
    static RangeOp createRange(Token tok) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new RangeOp(Op.RANGE, tok);
    }
    static ChildOp createLook(int type, Op next, Op branch) {
        if (Op.COUNT)  Op.nofinstances ++;
        ChildOp op = new ChildOp(type);
        op.setChild(branch);
        op.next = next;
        return op;
    }
    static CharOp createBackReference(int refno) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new CharOp(Op.BACKREFERENCE, refno);
    }
    static StringOp createString(String literal) {
        if (Op.COUNT)  Op.nofinstances ++;
        return new StringOp(Op.STRING, literal);
    }
    static ChildOp createIndependent(Op next, Op branch) {
        if (Op.COUNT)  Op.nofinstances ++;
        ChildOp op = new ChildOp(Op.INDEPENDENT);
        op.setChild(branch);
        op.next = next;
        return op;
    }
    static ModifierOp createModifier(Op next, Op branch, int add, int mask) {
        if (Op.COUNT)  Op.nofinstances ++;
        ModifierOp op = new ModifierOp(Op.MODIFIER, add, mask);
        op.setChild(branch);
        op.next = next;
        return op;
    }
    static ConditionOp createCondition(Op next, int ref, Op conditionflow, Op yesflow, Op noflow) {
        if (Op.COUNT)  Op.nofinstances ++;
        ConditionOp op = new ConditionOp(Op.CONDITION, ref, conditionflow, yesflow, noflow);
        op.next = next;
        return op;
    }

    int type;
    Op next = null;

    protected Op(int type) {
        this.type = type;
    }

    int size() {                                // for UNION
        return 0;
    }
    Op elementAt(int index) {                   // for UNIoN
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    Op getChild() {                             // for CLOSURE, QUESTION
        throw new RuntimeException("Internal Error: type="+this.type);
    }
                                                // ModifierOp
    int getData() {                             // CharOp  for CHAR, BACKREFERENCE, CAPTURE, ANCHOR, 
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    int getData2() {                            // ModifierOp
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    RangeToken getToken() {                     // RANGE, NRANGE
        throw new RuntimeException("Internal Error: type="+this.type);
    }
    String getString() {                        // STRING
        throw new RuntimeException("Internal Error: type="+this.type);
    }

    // ================================================================
    static class CharOp extends Op {
        int charData;
        CharOp(int type, int data) {
            super(type);
            this.charData = data;
        }
        @Override
        int getData() {
            return this.charData;
        }
    }

    // ================================================================
    static class UnionOp extends Op {
        Vector<Op> branches;
        UnionOp(int type, int size) {
            super(type);
            this.branches = new Vector<Op>(size);
        }
        void addElement(Op op) {
            this.branches.addElement(op);
        }
        @Override
        int size() {
            return this.branches.size();
        }
        @Override
        Op elementAt(int index) {
            return this.branches.elementAt(index);
        }
    }

    // ================================================================
    static class ChildOp extends Op {
        Op child;
        ChildOp(int type) {
            super(type);
        }
        void setChild(Op child) {
            this.child = child;
        }
        @Override
        Op getChild() {
            return this.child;
        }
    }
    // ================================================================
    static class ModifierOp extends ChildOp {
        int v1;
        int v2;
        ModifierOp(int type, int v1, int v2) {
            super(type);
            this.v1 = v1;
            this.v2 = v2;
        }
        @Override
        int getData() {
            return this.v1;
        }
        @Override
        int getData2() {
            return this.v2;
        }
    }
    // ================================================================
    static class RangeOp extends Op {
        Token tok;
        RangeOp(int type, Token tok) {
            super(type);
            this.tok = tok;
        }
        @Override
        RangeToken getToken() {
            return (RangeToken)this.tok;
        }
    }
    // ================================================================
    static class StringOp extends Op {
        String string;
        StringOp(int type, String literal) {
            super(type);
            this.string = literal;
        }
        @Override
        String getString() {
            return this.string;
        }
    }
    // ================================================================
    static class ConditionOp extends Op {
        int refNumber;
        Op condition;
        Op yes;
        Op no;
        ConditionOp(int type, int refno, Op conditionflow, Op yesflow, Op noflow) {
            super(type);
            this.refNumber = refno;
            this.condition = conditionflow;
            this.yes = yesflow;
            this.no = noflow;
        }
    }
}

  final static class RangeToken extends Token implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    int[] ranges;
    boolean sorted;
    boolean compacted;
    RangeToken icaseCache = null;
    volatile int[] map = null;
    volatile int nonMapIndex;

    RangeToken(int type) {
        super(type);
        this.setSorted(false);
    }

                                                // for RANGE or NRANGE
    @Override
    protected void addRange(int start, int end) {
        this.icaseCache = null;
        //System.err.println("Token#addRange(): "+start+" "+end);
        int r1, r2;
        if (start <= end) {
            r1 = start;
            r2 = end;
        } else {
            r1 = end;
            r2 = start;
        }

        int pos = 0;
        if (this.ranges == null) {
            this.ranges = new int[2];
            this.ranges[0] = r1;
            this.ranges[1] = r2;
            this.setSorted(true);
        } else {
            pos = this.ranges.length;
            if (this.ranges[pos-1]+1 == r1) {
                this.ranges[pos-1] = r2;
                return;
            }
            int[] temp = new int[pos+2];
            System.arraycopy(this.ranges, 0, temp, 0, pos);
            this.ranges = temp;
            if (this.ranges[pos-1] >= r1)
                this.setSorted(false);
            this.ranges[pos++] = r1;
            this.ranges[pos] = r2;
            if (!this.sorted)
                this.sortRanges();
        }
    }

    private final boolean isSorted() {
        return this.sorted;
    }
    private final void setSorted(boolean sort) {
        this.sorted = sort;
        if (!sort)  this.compacted = false;
    }
    private final boolean isCompacted() {
        return this.compacted;
    }
    private final void setCompacted() {
        this.compacted = true;
    }

    @Override
    protected void sortRanges() {
        if (this.isSorted())
            return;
        if (this.ranges == null)
            return;
        //System.err.println("Do sorting: "+this.ranges.length);

                                                // Bubble sort
                                                // Why? -- In many cases,
                                                //         this.ranges has few elements.
        for (int i = this.ranges.length-4;  i >= 0;  i -= 2) {
            for (int j = 0;  j <= i;  j += 2) {
                if (this.ranges[j] > this.ranges[j+2]
                    || this.ranges[j] == this.ranges[j+2] && this.ranges[j+1] > this.ranges[j+3]) {
                    int tmp;
                    tmp = this.ranges[j+2];
                    this.ranges[j+2] = this.ranges[j];
                    this.ranges[j] = tmp;
                    tmp = this.ranges[j+3];
                    this.ranges[j+3] = this.ranges[j+1];
                    this.ranges[j+1] = tmp;
                }
            }
        }
        this.setSorted(true);
    }

    /**
     * this.ranges is sorted.
     */
    @Override
    protected void compactRanges() {
        boolean DEBUG = false;
        if (this.ranges == null || this.ranges.length <= 2)
            return;
        if (this.isCompacted())
            return;
        int base = 0;                           // Index of writing point
        int target = 0;                         // Index of processing point

        while (target < this.ranges.length) {
            if (base != target) {
                this.ranges[base] = this.ranges[target++];
                this.ranges[base+1] = this.ranges[target++];
            } else
                target += 2;
            int baseend = this.ranges[base+1];
            while (target < this.ranges.length) {
                if (baseend+1 < this.ranges[target])
                    break;
                if (baseend+1 == this.ranges[target]) {
                    if (DEBUG)
                        System.err.println("Token#compactRanges(): Compaction: ["+this.ranges[base]
                                           +", "+this.ranges[base+1]
                                           +"], ["+this.ranges[target]
                                           +", "+this.ranges[target+1]
                                           +"] -> ["+this.ranges[base]
                                           +", "+this.ranges[target+1]
                                           +"]");
                    this.ranges[base+1] = this.ranges[target+1];
                    baseend = this.ranges[base+1];
                    target += 2;
                } else if (baseend >= this.ranges[target+1]) {
                    if (DEBUG)
                        System.err.println("Token#compactRanges(): Compaction: ["+this.ranges[base]
                                           +", "+this.ranges[base+1]
                                           +"], ["+this.ranges[target]
                                           +", "+this.ranges[target+1]
                                           +"] -> ["+this.ranges[base]
                                           +", "+this.ranges[base+1]
                                           +"]");
                    target += 2;
                } else if (baseend < this.ranges[target+1]) {
                    if (DEBUG)
                        System.err.println("Token#compactRanges(): Compaction: ["+this.ranges[base]
                                           +", "+this.ranges[base+1]
                                           +"], ["+this.ranges[target]
                                           +", "+this.ranges[target+1]
                                           +"] -> ["+this.ranges[base]
                                           +", "+this.ranges[target+1]
                                           +"]");
                    this.ranges[base+1] = this.ranges[target+1];
                    baseend = this.ranges[base+1];
                    target += 2;
                } else {
                    throw new RuntimeException("Token#compactRanges(): Internel Error: ["
                                               +this.ranges[base]
                                               +","+this.ranges[base+1]
                                               +"] ["+this.ranges[target]
                                               +","+this.ranges[target+1]+"]");
                }
            } // while
            base += 2;
        }

        if (base != this.ranges.length) {
            int[] result = new int[base];
            System.arraycopy(this.ranges, 0, result, 0, base);
            this.ranges = result;
        }
        this.setCompacted();
    }

    @Override
    protected void mergeRanges(Token token) {
        RangeToken tok = (RangeToken)token;
        this.sortRanges();
        tok.sortRanges();
        if (tok.ranges == null)
            return;
        this.icaseCache = null;
        this.setSorted(true);
        if (this.ranges == null) {
            this.ranges = new int[tok.ranges.length];
            System.arraycopy(tok.ranges, 0, this.ranges, 0, tok.ranges.length);
            return;
        }
        int[] result = new int[this.ranges.length+tok.ranges.length];
        for (int i = 0, j = 0, k = 0;  i < this.ranges.length || j < tok.ranges.length;) {
            if (i >= this.ranges.length) {
                result[k++] = tok.ranges[j++];
                result[k++] = tok.ranges[j++];
            } else if (j >= tok.ranges.length) {
                result[k++] = this.ranges[i++];
                result[k++] = this.ranges[i++];
            } else if (tok.ranges[j] < this.ranges[i]
                       || tok.ranges[j] == this.ranges[i] && tok.ranges[j+1] < this.ranges[i+1]) {
                result[k++] = tok.ranges[j++];
                result[k++] = tok.ranges[j++];
            } else {
                result[k++] = this.ranges[i++];
                result[k++] = this.ranges[i++];
            }
        }
        this.ranges = result;
    }

    @Override
    protected void subtractRanges(Token token) {
        if (token.type == NRANGE) {
            this.intersectRanges(token);
            return;
        }
        RangeToken tok = (RangeToken)token;
        if (tok.ranges == null || this.ranges == null)
            return;
        this.icaseCache = null;
        this.sortRanges();
        this.compactRanges();
        tok.sortRanges();
        tok.compactRanges();

        //System.err.println("Token#substractRanges(): Entry: "+this.ranges.length+", "+tok.ranges.length);

        int[] result = new int[this.ranges.length+tok.ranges.length];
        int wp = 0, src = 0, sub = 0;
        while (src < this.ranges.length && sub < tok.ranges.length) {
            int srcbegin = this.ranges[src];
            int srcend = this.ranges[src+1];
            int subbegin = tok.ranges[sub];
            int subend = tok.ranges[sub+1];
            if (srcend < subbegin) {            // Not overlapped
                                                // src: o-----o
                                                // sub:         o-----o
                                                // res: o-----o
                                                // Reuse sub
                result[wp++] = this.ranges[src++];
                result[wp++] = this.ranges[src++];
            } else if (srcend >= subbegin
                       && srcbegin <= subend) { // Overlapped
                                                // src:    o--------o
                                                // sub:  o----o
                                                // sub:      o----o
                                                // sub:          o----o
                                                // sub:  o------------o
                if (subbegin <= srcbegin && srcend <= subend) {
                                                // src:    o--------o
                                                // sub:  o------------o
                                                // res: empty
                                                // Reuse sub
                    src += 2;
                } else if (subbegin <= srcbegin) {
                                                // src:    o--------o
                                                // sub:  o----o
                                                // res:       o-----o
                                                // Reuse src(=res)
                    this.ranges[src] = subend+1;
                    sub += 2;
                } else if (srcend <= subend) {
                                                // src:    o--------o
                                                // sub:          o----o
                                                // res:    o-----o
                                                // Reuse sub
                    result[wp++] = srcbegin;
                    result[wp++] = subbegin-1;
                    src += 2;
                } else {
                                                // src:    o--------o
                                                // sub:      o----o
                                                // res:    o-o    o-o
                                                // Reuse src(=right res)
                    result[wp++] = srcbegin;
                    result[wp++] = subbegin-1;
                    this.ranges[src] = subend+1;
                    sub += 2;
                }
            } else if (subend < srcbegin) {
                                                // Not overlapped
                                                // src:          o-----o
                                                // sub: o----o
                sub += 2;
            } else {
                throw new RuntimeException("Token#subtractRanges(): Internal Error: ["+this.ranges[src]
                                           +","+this.ranges[src+1]
                                           +"] - ["+tok.ranges[sub]
                                           +","+tok.ranges[sub+1]
                                           +"]");
            }
        }
        while (src < this.ranges.length) {
            result[wp++] = this.ranges[src++];
            result[wp++] = this.ranges[src++];
        }
        this.ranges = new int[wp];
        System.arraycopy(result, 0, this.ranges, 0, wp);
                                                // this.ranges is sorted and compacted.
    }

    /**
     * @param token Ignore whether it is NRANGE or not.
     */
    @Override
    protected void intersectRanges(Token token) {
        RangeToken tok = (RangeToken)token;
        if (tok.ranges == null || this.ranges == null)
            return;
        this.icaseCache = null;
        this.sortRanges();
        this.compactRanges();
        tok.sortRanges();
        tok.compactRanges();

        int[] result = new int[this.ranges.length+tok.ranges.length];
        int wp = 0, src1 = 0, src2 = 0;
        while (src1 < this.ranges.length && src2 < tok.ranges.length) {
            int src1begin = this.ranges[src1];
            int src1end = this.ranges[src1+1];
            int src2begin = tok.ranges[src2];
            int src2end = tok.ranges[src2+1];
            if (src1end < src2begin) {          // Not overlapped
                                                // src1: o-----o
                                                // src2:         o-----o
                                                // res:  empty
                                                // Reuse src2
                src1 += 2;
            } else if (src1end >= src2begin
                       && src1begin <= src2end) { // Overlapped
                                                // src1:    o--------o
                                                // src2:  o----o
                                                // src2:      o----o
                                                // src2:          o----o
                                                // src2:  o------------o
                if (src2begin <= src1begin && src1end <= src2end) {
                                                // src1:    o--------o
                                                // src2:  o------------o
                                                // res:     o--------o
                                                // Reuse src2
                    result[wp++] = src1begin;
                    result[wp++] = src1end;
                    src1 += 2;
                } else if (src2begin <= src1begin) {
                                                // src1:    o--------o
                                                // src2:  o----o
                                                // res:     o--o
                                                // Reuse the rest of src1
                    result[wp++] = src1begin;
                    result[wp++] = src2end;
                    this.ranges[src1] = src2end+1;
                    src2 += 2;
                } else if (src1end <= src2end) {
                                                // src1:    o--------o
                                                // src2:          o----o
                                                // res:           o--o
                                                // Reuse src2
                    result[wp++] = src2begin;
                    result[wp++] = src1end;
                    src1 += 2;
                } else {
                                                // src1:    o--------o
                                                // src2:      o----o
                                                // res:       o----o
                                                // Reuse the rest of src1
                    result[wp++] = src2begin;
                    result[wp++] = src2end;
                    this.ranges[src1] = src2end+1;
                }
            } else if (src2end < src1begin) {
                                                // Not overlapped
                                                // src1:          o-----o
                                                // src2: o----o
                src2 += 2;
            } else {
                throw new RuntimeException("Token#intersectRanges(): Internal Error: ["
                                           +this.ranges[src1]
                                           +","+this.ranges[src1+1]
                                           +"] & ["+tok.ranges[src2]
                                           +","+tok.ranges[src2+1]
                                           +"]");
            }
        }
        while (src1 < this.ranges.length) {
            result[wp++] = this.ranges[src1++];
            result[wp++] = this.ranges[src1++];
        }
        this.ranges = new int[wp];
        System.arraycopy(result, 0, this.ranges, 0, wp);
                                                // this.ranges is sorted and compacted.
    }

    /**
     * for RANGE: Creates complement.
     * for NRANGE: Creates the same meaning RANGE.
     */
    static Token complementRanges(Token token) {
        if (token.type != RANGE && token.type != NRANGE)
            throw new IllegalArgumentException("Token#complementRanges(): must be RANGE: "+token.type);
        RangeToken tok = (RangeToken)token;
        tok.sortRanges();
        tok.compactRanges();
        int len = tok.ranges.length+2;
        if (tok.ranges[0] == 0)
            len -= 2;
        int last = tok.ranges[tok.ranges.length-1];
        if (last == UTF16_MAX)
            len -= 2;
        RangeToken ret = Token.createRange();
        ret.ranges = new int[len];
        int wp = 0;
        if (tok.ranges[0] > 0) {
            ret.ranges[wp++] = 0;
            ret.ranges[wp++] = tok.ranges[0]-1;
        }
        for (int i = 1;  i < tok.ranges.length-2;  i += 2) {
            ret.ranges[wp++] = tok.ranges[i]+1;
            ret.ranges[wp++] = tok.ranges[i+1]-1;
        }
        if (last != UTF16_MAX) {
            ret.ranges[wp++] = last+1;
            ret.ranges[wp] = UTF16_MAX;
        }
        ret.setCompacted();
        return ret;
    }

    synchronized RangeToken getCaseInsensitiveToken() {
        if (this.icaseCache != null)
            return this.icaseCache;
            
        RangeToken uppers = this.type == Token.RANGE ? Token.createRange() : Token.createNRange();
        for (int i = 0;  i < this.ranges.length;  i += 2) {
            for (int ch = this.ranges[i];  ch <= this.ranges[i+1];  ch ++) {
                if (ch > 0xffff)
                    uppers.addRange(ch, ch);
                else {
                    char uch = Character.toUpperCase((char)ch);
                    uppers.addRange(uch, uch);
                }
            }
        }
        RangeToken lowers = this.type == Token.RANGE ? Token.createRange() : Token.createNRange();
        for (int i = 0;  i < uppers.ranges.length;  i += 2) {
            for (int ch = uppers.ranges[i];  ch <= uppers.ranges[i+1];  ch ++) {
                if (ch > 0xffff)
                    lowers.addRange(ch, ch);
                else {
                    char uch = Character.toUpperCase((char)ch);
                    lowers.addRange(uch, uch);
                }
            }
        }
        lowers.mergeRanges(uppers);
        lowers.mergeRanges(this);
        lowers.compactRanges();

        this.icaseCache = lowers;
        return lowers;
    }

    void dumpRanges() {
        System.err.print("RANGE: ");
        if (this.ranges == null) {
            System.err.println(" NULL");
            return;
        }
        
        for (int i = 0;  i < this.ranges.length;  i += 2) {
            System.err.print("["+this.ranges[i]+","+this.ranges[i+1]+"] ");
        }
        System.err.println("");
    }

    @Override
    boolean match(int ch) {
        if (this.map == null)  this.createMap();
        boolean ret;
        if (this.type == RANGE) {
            if (ch < MAPSIZE)
                return (this.map[ch/32] & (1<<(ch&0x1f))) != 0;
            ret = false;
            for (int i = this.nonMapIndex;  i < this.ranges.length;  i += 2) {
                if (this.ranges[i] <= ch && ch <= this.ranges[i+1])
                    return true;
            }
        } else {
            if (ch < MAPSIZE)
                return (this.map[ch/32] & (1<<(ch&0x1f))) == 0;
            ret = true;
            for (int i = this.nonMapIndex;  i < this.ranges.length;  i += 2) {
                if (this.ranges[i] <= ch && ch <= this.ranges[i+1])
                    return false;
            }
        }
        return ret;
    }

    private static final int MAPSIZE = 256;
    private void createMap() {
      int asize = MAPSIZE/32;                 // 32 is the number of bits in `int'.
      int [] map = new int[asize];
      int nonMapIndex = this.ranges.length;
      // for (int i = 0;  i < asize;  i ++)  this.map[i] = 0;
      for (int i = 0;  i < this.ranges.length;  i += 2) {
          int s = this.ranges[i];
          int e = this.ranges[i+1];
          if (s < MAPSIZE) {
              for (int j = s;  j <= e && j < MAPSIZE;  j ++)
                  map[j/32] |= 1<<(j&0x1f); // s&0x1f : 0-31
          } else {
              nonMapIndex = i;
              break;
          }
          if (e >= MAPSIZE) {
              nonMapIndex = i;
              break;
          }
      }
      this.nonMapIndex = nonMapIndex;
      this.map = map;
      //for (int i = 0;  i < asize;  i ++)  System.err.println("Map: "+Integer.toString(this.map[i], 16));
  }

    @Override
    public String toString(int options) {
        String ret;
        if (this.type == RANGE) {
            if (this == Token.token_dot)
                ret = ".";
            else if (this == Token.token_0to9)
                ret = "\\d";
            else if (this == Token.token_wordchars)
                ret = "\\w";
            else if (this == Token.token_spaces)
                ret = "\\s";
            else {
                StringBuffer sb = new StringBuffer();
                sb.append("[");
                for (int i = 0;  i < this.ranges.length;  i += 2) {
                    if ((options & RegularExpression.SPECIAL_COMMA) != 0 && i > 0)  sb.append(",");
                    if (this.ranges[i] == this.ranges[i+1]) {
                        sb.append(escapeCharInCharClass(this.ranges[i]));
                    } else {
                        sb.append(escapeCharInCharClass(this.ranges[i]));
                        sb.append('-');
                        sb.append(escapeCharInCharClass(this.ranges[i+1]));
                    }
                }
                sb.append("]");
                ret = sb.toString();
            }
        } else {
            if (this == Token.token_not_0to9)
                ret = "\\D";
            else if (this == Token.token_not_wordchars)
                ret = "\\W";
            else if (this == Token.token_not_spaces)
                ret = "\\S";
            else {
                StringBuffer sb = new StringBuffer();
                sb.append("[^");
                for (int i = 0;  i < this.ranges.length;  i += 2) {
                    if ((options & RegularExpression.SPECIAL_COMMA) != 0 && i > 0)  sb.append(",");
                    if (this.ranges[i] == this.ranges[i+1]) {
                        sb.append(escapeCharInCharClass(this.ranges[i]));
                    } else {
                        sb.append(escapeCharInCharClass(this.ranges[i]));
                        sb.append('-');
                        sb.append(escapeCharInCharClass(this.ranges[i+1]));
                    }
                }
                sb.append("]");
                ret = sb.toString();
            }
        }
        return ret;
    }

    private static String escapeCharInCharClass(int ch) {
        String ret;
        switch (ch) {
          case '[':  case ']':  case '-':  case '^':
          case ',':  case '\\':
            ret = "\\"+(char)ch;
            break;
          case '\f':  ret = "\\f";  break;
          case '\n':  ret = "\\n";  break;
          case '\r':  ret = "\\r";  break;
          case '\t':  ret = "\\t";  break;
          case 0x1b:  ret = "\\e";  break;
          //case 0x0b:  ret = "\\v";  break;
          default:
            if (ch < 0x20) {
                String pre = "0"+Integer.toHexString(ch);
                ret = "\\x"+pre.substring(pre.length()-2, pre.length());
            } else if (ch >= 0x10000) {
                String pre = "0"+Integer.toHexString(ch);
                ret = "\\v"+pre.substring(pre.length()-6, pre.length());
            } else
                ret = ""+(char)ch;
        }
        return ret;
    }

}

  static class RegexParser {
      static final int T_CHAR = 0;
      static final int T_EOF = 1;
      static final int T_OR = 2;                  // '|'
      static final int T_STAR = 3;                // '*'
      static final int T_PLUS = 4;                // '+'
      static final int T_QUESTION = 5;            // '?'
      static final int T_LPAREN = 6;              // '('
      static final int T_RPAREN = 7;              // ')'
      static final int T_DOT = 8;                 // '.'
      static final int T_LBRACKET = 9;            // '['
      static final int T_BACKSOLIDUS = 10;        // '\'
      static final int T_CARET = 11;              // '^'
      static final int T_DOLLAR = 12;             // '$'
      static final int T_LPAREN2 = 13;            // '(?:'
      static final int T_LOOKAHEAD = 14;          // '(?='
      static final int T_NEGATIVELOOKAHEAD = 15;  // '(?!'
      static final int T_LOOKBEHIND = 16;         // '(?<='
      static final int T_NEGATIVELOOKBEHIND = 17; // '(?<!'
      static final int T_INDEPENDENT = 18;        // '(?>'
      static final int T_SET_OPERATIONS = 19;     // '(?['
      static final int T_POSIX_CHARCLASS_START = 20; // '[:' in a character class
      static final int T_COMMENT = 21;            // '(?#'
      static final int T_MODIFIERS = 22;          // '(?' [\-,a-z,A-Z]
      static final int T_CONDITION = 23;          // '(?('
      static final int T_XMLSCHEMA_CC_SUBTRACTION = 24; // '-[' in a character class

      static class ReferencePosition {
          int refNumber;
          int position;
          ReferencePosition(int n, int pos) {
              this.refNumber = n;
              this.position = pos;
          }
      }

      int offset;
      String regex;
      int regexlen;
      int options;
      int chardata;
      int nexttoken;
      static protected final int S_NORMAL = 0;
      static protected final int S_INBRACKETS = 1;
      static protected final int S_INXBRACKETS = 2;
      int context = S_NORMAL;
      int parennumber = 1;
      boolean hasBackReferences;
      Vector<ReferencePosition> references = null;

      public RegexParser() {
          //this.setLocale(Locale.getDefault());
      }

      final ParseException ex(String key, int loc) {
          return new ParseException(EcorePlugin.INSTANCE.getString(key), loc);
      }

      private final boolean isSet(int flag) {
          return (this.options & flag) == flag;
      }

      synchronized Token parse(String regex, int options) throws ParseException {
          this.options = options;
          this.offset = 0;
          this.setContext(S_NORMAL);
          this.parennumber = 1;
          this.hasBackReferences = false;
          this.regex = regex;
          if (this.isSet(RegularExpression.EXTENDED_COMMENT))
              this.regex = REUtil.stripExtendedComment(this.regex);
          this.regexlen = this.regex.length();


          this.next();
          Token ret = this.parseRegex();
          if (this.offset != this.regexlen)
              throw ex("parser.parse.1", this.offset);
          if (this.references != null) {
              for (int i = 0;  i < this.references.size();  i ++) {
                  ReferencePosition position = this.references.elementAt(i);
                  if (this.parennumber <= position.refNumber)
                      throw ex("parser.parse.2", position.position);
              }
              this.references.removeAllElements();
          }
          return ret;
      }

      /*
      public RegularExpression createRegex(String regex, int options) throws ParseException {
          Token tok = this.parse(regex, options);
          return new RegularExpression(regex, tok, this.parennumber, this.hasBackReferences, options);
      }
      */

      protected final void setContext(int con) {
          this.context = con;
      }

      final int read() {
          return this.nexttoken;
      }

      final void next() {
          if (this.offset >= this.regexlen) {
              this.chardata = -1;
              this.nexttoken = T_EOF;
              return;
          }

          int ret;
          int ch = this.regex.charAt(this.offset++);
          this.chardata = ch;

          if (this.context == S_INBRACKETS) {
              // In a character class, this.chardata has one character, that is to say,
              // a pair of surrogates is composed and stored to this.chardata.
              switch (ch) {
                case '\\':
                  ret = T_BACKSOLIDUS;
                  if (this.offset >= this.regexlen)
                      throw ex("parser.next.1", this.offset-1);
                  this.chardata = this.regex.charAt(this.offset++);
                  break;

                case '-':
                  if (this.isSet(RegularExpression.XMLSCHEMA_MODE)
                      && this.offset < this.regexlen && this.regex.charAt(this.offset) == '[') {
                      this.offset++;
                      ret = T_XMLSCHEMA_CC_SUBTRACTION;
                  } else
                      ret = T_CHAR;
                  break;

                case '[':
                  if (!this.isSet(RegularExpression.XMLSCHEMA_MODE)
                      && this.offset < this.regexlen && this.regex.charAt(this.offset) == ':') {
                      this.offset++;
                      ret = T_POSIX_CHARCLASS_START;
                      break;
                  } // Through down
                default:
                  if (REUtil.isHighSurrogate(ch) && this.offset < this.regexlen) {
                      int low = this.regex.charAt(this.offset);
                      if (REUtil.isLowSurrogate(low)) {
                          this.chardata = REUtil.composeFromSurrogates(ch, low);
                          this.offset ++;
                      }
                  }
                  ret = T_CHAR;
              }
              this.nexttoken = ret;
              return;
          }

          switch (ch) {
            case '|': ret = T_OR;             break;
            case '*': ret = T_STAR;           break;
            case '+': ret = T_PLUS;           break;
            case '?': ret = T_QUESTION;       break;
            case ')': ret = T_RPAREN;         break;
            case '.': ret = T_DOT;            break;
            case '[': ret = T_LBRACKET;       break;
            case '^': ret = T_CARET;          break;
            case '$': ret = T_DOLLAR;         break;
            case '(':
              ret = T_LPAREN;
              if (this.offset >= this.regexlen)
                  break;
              if (this.regex.charAt(this.offset) != '?')
                  break;
              if (++this.offset >= this.regexlen)
                  throw ex("parser.next.2", this.offset-1);
              ch = this.regex.charAt(this.offset++);
              switch (ch) {
                case ':':  ret = T_LPAREN2;            break;
                case '=':  ret = T_LOOKAHEAD;          break;
                case '!':  ret = T_NEGATIVELOOKAHEAD;  break;
                case '[':  ret = T_SET_OPERATIONS;     break;
                case '>':  ret = T_INDEPENDENT;        break;
                case '<':
                  if (this.offset >= this.regexlen)
                      throw ex("parser.next.2", this.offset-3);
                  ch = this.regex.charAt(this.offset++);
                  if (ch == '=') {
                      ret = T_LOOKBEHIND;
                  } else if (ch == '!') {
                      ret = T_NEGATIVELOOKBEHIND;
                  } else
                      throw ex("parser.next.3", this.offset-3);
                  break;
                case '#':
                  while (this.offset < this.regexlen) {
                      ch = this.regex.charAt(this.offset++);
                      if (ch == ')')  break;
                  }
                  if (ch != ')')
                      throw ex("parser.next.4", this.offset-1);
                  ret = T_COMMENT;
                  break;
                default:
                  if (ch == '-' || 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z') {// Options
                      this.offset --;
                      ret = T_MODIFIERS;
                      break;
                  } else if (ch == '(') {         // conditional
                      ret = T_CONDITION;          // this.offsets points the next of '('.
                      break;
                  }
                  throw ex("parser.next.2", this.offset-2);
              }
              break;
              
            case '\\':
              ret = T_BACKSOLIDUS;
              if (this.offset >= this.regexlen)
                  throw ex("parser.next.1", this.offset-1);
              this.chardata = this.regex.charAt(this.offset++);
              break;

            default:
              ret = T_CHAR;
          }
          this.nexttoken = ret;
      }

      /**
       * regex ::= term (`|` term)*
       * term ::= factor+
       * factor ::= ('^' | '$' | '\A' | '\Z' | '\z' | '\b' | '\B' | '\<' | '\>'
       *            | atom (('*' | '+' | '?' | minmax ) '?'? )?)
       *            | '(?=' regex ')'  | '(?!' regex ')'  | '(?&lt;=' regex ')'  | '(?&lt;!' regex ')'
       * atom ::= char | '.' | range | '(' regex ')' | '(?:' regex ')' | '\' [0-9]
       *          | '\w' | '\W' | '\d' | '\D' | '\s' | '\S' | category-block 
       */
      Token parseRegex() throws ParseException {
          Token tok = this.parseTerm();
          Token parent = null;
          while (this.read() == T_OR) {
              this.next();                    // '|'
              if (parent == null) {
                  parent = Token.createUnion();
                  parent.addChild(tok);
                  tok = parent;
              }
              tok.addChild(this.parseTerm());
          }
          return tok;
      }

      /**
       * term ::= factor+
       */
      Token parseTerm() throws ParseException {
          int ch = this.read();
          if (ch == T_OR || ch == T_RPAREN || ch == T_EOF) {
              return Token.createEmpty();
          } else {
              Token tok = this.parseFactor();
              Token concat = null;
              while ((ch = this.read()) != T_OR && ch != T_RPAREN && ch != T_EOF) {
                  if (concat == null) {
                      concat = Token.createConcat();
                      concat.addChild(tok);
                      tok = concat;
                  }
                  concat.addChild(this.parseFactor());
                  //tok = Token.createConcat(tok, this.parseFactor());
              }
              return tok;
          }
      }

      // ----------------------------------------------------------------

      Token processCaret() throws ParseException {
          this.next();
          return Token.token_linebeginning;
      }
      Token processDollar() throws ParseException {
          this.next();
          return Token.token_lineend;
      }
      Token processLookahead() throws ParseException {
          this.next();
          Token tok = Token.createLook(Token.LOOKAHEAD, this.parseRegex());
          if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
          this.next();                            // ')'
          return tok;
      }
      Token processNegativelookahead() throws ParseException {
          this.next();
          Token tok = Token.createLook(Token.NEGATIVELOOKAHEAD, this.parseRegex());
          if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
          this.next();                            // ')'
          return tok;
      }
      Token processLookbehind() throws ParseException {
          this.next();
          Token tok = Token.createLook(Token.LOOKBEHIND, this.parseRegex());
          if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
          this.next();                            // ')'
          return tok;
      }
      Token processNegativelookbehind() throws ParseException {
          this.next();
          Token tok = Token.createLook(Token.NEGATIVELOOKBEHIND, this.parseRegex());
          if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
          this.next();                    // ')'
          return tok;
      }
      Token processBacksolidus_A() throws ParseException {
          this.next();
          return Token.token_stringbeginning;
      }
      Token processBacksolidus_Z() throws ParseException {
          this.next();
          return Token.token_stringend2;
      }
      Token processBacksolidus_z() throws ParseException {
          this.next();
          return Token.token_stringend;
      }
      Token processBacksolidus_b() throws ParseException {
          this.next();
          return Token.token_wordedge;
      }
      Token processBacksolidus_B() throws ParseException {
          this.next();
          return Token.token_not_wordedge;
      }
      Token processBacksolidus_lt() throws ParseException {
          this.next();
          return Token.token_wordbeginning;
      }
      Token processBacksolidus_gt() throws ParseException {
          this.next();
          return Token.token_wordend;
      }
      Token processStar(Token tok) throws ParseException {
          this.next();
          if (this.read() == T_QUESTION) {
              this.next();
              return Token.createNGClosure(tok);
          } else
              return Token.createClosure(tok);
      }
      Token processPlus(Token tok) throws ParseException {
          // X+ -> XX*
          this.next();
          if (this.read() == T_QUESTION) {
              this.next();
              return Token.createConcat(tok, Token.createNGClosure(tok));
          } else
              return Token.createConcat(tok, Token.createClosure(tok));
      }
      Token processQuestion(Token tok) throws ParseException {
          // X? -> X|
          this.next();
          Token par = Token.createUnion();
          if (this.read() == T_QUESTION) {
              this.next();
              par.addChild(Token.createEmpty());
              par.addChild(tok);
          } else {
              par.addChild(tok);
              par.addChild(Token.createEmpty());
          }
          return par;
      }
      boolean checkQuestion(int off) {
          return off < this.regexlen && this.regex.charAt(off) == '?';
      }
      Token processParen() throws ParseException {
          this.next();
          int p = this.parennumber++;
          Token tok = Token.createParen(this.parseRegex(), p);
          if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
          this.next();                            // Skips ')'
          return tok;
      }
      Token processParen2() throws ParseException {
          this.next();
          Token tok = Token.createParen(this.parseRegex(), 0);
          if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
          this.next();                            // Skips ')'
          return tok;
      }
      Token processCondition() throws ParseException {
                                                  // this.offset points the next of '('
          if (this.offset+1 >= this.regexlen)  throw ex("parser.factor.4", this.offset);
                                                  // Parses a condition.
          int refno = -1;
          Token condition = null;
          int ch = this.regex.charAt(this.offset);
          if ('1' <= ch && ch <= '9') {
              refno = ch-'0';
              this.hasBackReferences = true;
              if (this.references == null)  this.references = new Vector<ReferencePosition>();
              this.references.addElement(new ReferencePosition(refno, this.offset));
              this.offset ++;
              if (this.regex.charAt(this.offset) != ')')  throw ex("parser.factor.1", this.offset);
              this.offset ++;
          } else {
              if (ch == '?')  this.offset --; // Points '('.
              this.next();
              condition = this.parseFactor();
              switch (condition.type) {
                case Token.LOOKAHEAD:
                case Token.NEGATIVELOOKAHEAD:
                case Token.LOOKBEHIND:
                case Token.NEGATIVELOOKBEHIND:
                  break;
                case Token.ANCHOR:
                  if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
                  break;
                default:
                  throw ex("parser.factor.5", this.offset);
              }
          }
                                                  // Parses yes/no-patterns.
          this.next();
          Token yesPattern = this.parseRegex();
          Token noPattern = null;
          if (yesPattern.type == Token.UNION) {
              if (yesPattern.size() != 2)  throw ex("parser.factor.6", this.offset);
              noPattern = yesPattern.getChild(1);
              yesPattern = yesPattern.getChild(0);
          }
          if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
          this.next();
          return Token.createCondition(refno, condition, yesPattern, noPattern);
      }
      Token processModifiers() throws ParseException {
                                                  // this.offset points the next of '?'.
                                                  // modifiers ::= [imsw]* ('-' [imsw]*)? ':'
          int add = 0, mask = 0, ch = -1;
          while (this.offset < this.regexlen) {
              ch = this.regex.charAt(this.offset);
              int v = REUtil.getOptionValue(ch);
              if (v == 0)  break;                 // '-' or ':'?
              add |= v;
              this.offset ++;
          }
          if (this.offset >= this.regexlen)  throw ex("parser.factor.2", this.offset-1);
          if (ch == '-') {
              this.offset ++;
              while (this.offset < this.regexlen) {
                  ch = this.regex.charAt(this.offset);
                  int v = REUtil.getOptionValue(ch);
                  if (v == 0)  break;             // ':'?
                  mask |= v;
                  this.offset ++;
              }
              if (this.offset >= this.regexlen)  throw ex("parser.factor.2", this.offset-1);
          }
          Token tok;
          if (ch == ':') {
              this.offset ++;
              this.next();
              tok = Token.createModifierGroup(this.parseRegex(), add, mask);
              if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
              this.next();
          } else if (ch == ')') {                 // such as (?-i)
              this.offset ++;
              this.next();
              tok = Token.createModifierGroup(this.parseRegex(), add, mask);
          } else
              throw ex("parser.factor.3", this.offset);

          return tok;
      }
      Token processIndependent() throws ParseException {
          this.next();
          Token tok = Token.createLook(Token.INDEPENDENT, this.parseRegex());
          if (this.read() != T_RPAREN)  throw ex("parser.factor.1", this.offset-1);
          this.next();                            // Skips ')'
          return tok;
      }
      Token processBacksolidus_c() throws ParseException {
          int ch2;                                // Must be in 0x0040-0x005f
          if (this.offset >= this.regexlen
              || ((ch2 = this.regex.charAt(this.offset++)) & 0xffe0) != 0x0040)
              throw ex("parser.atom.1", this.offset-1);
          this.next();
          return Token.createChar(ch2-0x40);
      }
      Token processBacksolidus_C() throws ParseException {
          throw ex("parser.process.1", this.offset);
      }
      Token processBacksolidus_i() throws ParseException {
          Token tok = Token.createChar('i');
          this.next();
          return tok;
      }
      Token processBacksolidus_I() throws ParseException {
          throw ex("parser.process.1", this.offset);
      }
      Token processBacksolidus_g() throws ParseException {
          this.next();
          return Token.getGraphemePattern();
      }
      Token processBacksolidus_X() throws ParseException {
          this.next();
          return Token.getCombiningCharacterSequence();
      }
      Token processBackreference() throws ParseException {
          int refnum = this.chardata-'0';
          Token tok = Token.createBackReference(refnum);
          this.hasBackReferences = true;
          if (this.references == null)  this.references = new Vector<ReferencePosition>();
          this.references.addElement(new ReferencePosition(refnum, this.offset-2));
          this.next();
          return tok;
      }

      // ----------------------------------------------------------------

      /**
       * factor ::= ('^' | '$' | '\A' | '\Z' | '\z' | '\b' | '\B' | '\<' | '\>'
       *            | atom (('*' | '+' | '?' | minmax ) '?'? )?)
       *            | '(?=' regex ')'  | '(?!' regex ')'  | '(?&lt;=' regex ')'  | '(?&lt;!' regex ')'
       *            | '(?#' [^)]* ')'
       * minmax ::= '{' min (',' max?)? '}'
       * min ::= [0-9]+
       * max ::= [0-9]+
       */
      Token parseFactor() throws ParseException {        
          int ch = this.read();
          Token tok;
          switch (ch) {
            case T_CARET:         return this.processCaret();
            case T_DOLLAR:        return this.processDollar();
            case T_LOOKAHEAD:     return this.processLookahead();
            case T_NEGATIVELOOKAHEAD: return this.processNegativelookahead();
            case T_LOOKBEHIND:    return this.processLookbehind();
            case T_NEGATIVELOOKBEHIND: return this.processNegativelookbehind();

            case T_COMMENT:
              this.next();
              return Token.createEmpty();

            case T_BACKSOLIDUS:
              switch (this.chardata) {
                case 'A': return this.processBacksolidus_A();
                case 'Z': return this.processBacksolidus_Z();
                case 'z': return this.processBacksolidus_z();
                case 'b': return this.processBacksolidus_b();
                case 'B': return this.processBacksolidus_B();
                case '<': return this.processBacksolidus_lt();
                case '>': return this.processBacksolidus_gt();
              }
                                                  // through down
          }
          tok = this.parseAtom();
          ch = this.read();
          switch (ch) {
            case T_STAR:  return this.processStar(tok);
            case T_PLUS:  return this.processPlus(tok);
            case T_QUESTION: return this.processQuestion(tok);
            case T_CHAR:
              if (this.chardata == '{' && this.offset < this.regexlen) {

                  int off = this.offset;          // this.offset -> next of '{'
                  int min = 0, max = -1;

                  if ((ch = this.regex.charAt(off++)) >= '0' && ch <= '9') {

                      min = ch -'0';
                      while (off < this.regexlen
                             && (ch = this.regex.charAt(off++)) >= '0' && ch <= '9') {
                          min = min*10 +ch-'0';
                          if (min < 0)
                              throw ex("parser.quantifier.5", this.offset);
                      }
                  }
                  else {
                      throw ex("parser.quantifier.1", this.offset);
                  }

                  max = min;
                  if (ch == ',') {

                     if (off >= this.regexlen) {
                         throw ex("parser.quantifier.3", this.offset);
                     }
                     else if ((ch = this.regex.charAt(off++)) >= '0' && ch <= '9') {                       

                          max = ch -'0';       // {min,max}
                          while (off < this.regexlen
                                 && (ch = this.regex.charAt(off++)) >= '0'
                                 && ch <= '9') {
                              max = max*10 +ch-'0';
                              if (max < 0)
                                  throw ex("parser.quantifier.5", this.offset);
                          }

                          if (min > max)
                              throw ex("parser.quantifier.4", this.offset);
                     }
                     else { // assume {min,}
                          max = -1;           
                      }
                  }

                 if (ch != '}')
                     throw ex("parser.quantifier.2", this.offset);

                 if (this.checkQuestion(off)) {  // off -> next of '}'
                      tok = Token.createNGClosure(tok);
                      this.offset = off+1;
                  } else {
                      tok = Token.createClosure(tok);
                      this.offset = off;
                  }

                  tok.setMin(min);
                  tok.setMax(max);
                  //System.err.println("CLOSURE: "+min+", "+max);
                  this.next();
              }
          }
          return tok;
      }

      /**
       * atom ::= char | '.' | char-class | '(' regex ')' | '(?:' regex ')' | '\' [0-9]
       *          | '\w' | '\W' | '\d' | '\D' | '\s' | '\S' | category-block
       *          | '(?>' regex ')'
       * char ::= '\\' | '\' [efnrt] | bmp-code | character-1
       */
      Token parseAtom() throws ParseException {
          int ch = this.read();
          Token tok = null;
          switch (ch) {
            case T_LPAREN:        return this.processParen();
            case T_LPAREN2:       return this.processParen2(); // '(?:'
            case T_CONDITION:     return this.processCondition(); // '(?('
            case T_MODIFIERS:     return this.processModifiers(); // (?modifiers ... )
            case T_INDEPENDENT:   return this.processIndependent();
            case T_DOT:
              this.next();                    // Skips '.'
              tok = Token.token_dot;
              break;

              /**
               * char-class ::= '[' ( '^'? range ','?)+ ']'
               * range ::= '\d' | '\w' | '\s' | category-block | range-char
               *           | range-char '-' range-char
               * range-char ::= '\[' | '\]' | '\\' | '\' [,-efnrtv] | bmp-code | character-2
               * bmp-char ::= '\' 'u' [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F]
               */
            case T_LBRACKET:      return this.parseCharacterClass(true);
            case T_SET_OPERATIONS: return this.parseSetOperations();

            case T_BACKSOLIDUS:
              switch (this.chardata) {
                case 'd':  case 'D':
                case 'w':  case 'W':
                case 's':  case 'S':
                  tok = this.getTokenForShorthand(this.chardata);
                  this.next();
                  return tok;

                case 'e':  case 'f':  case 'n':  case 'r':
                case 't':  case 'u':  case 'v':  case 'x':
                  {
                      int ch2 = this.decodeEscaped();
                      if (ch2 < 0x10000) {
                          tok = Token.createChar(ch2);
                      } else {
                          tok = Token.createString(REUtil.decomposeToSurrogates(ch2));
                      }
                  }
                  break;

                case 'c': return this.processBacksolidus_c();
                case 'C': return this.processBacksolidus_C();
                case 'i': return this.processBacksolidus_i();
                case 'I': return this.processBacksolidus_I();
                case 'g': return this.processBacksolidus_g();
                case 'X': return this.processBacksolidus_X();
                case '1':  case '2':  case '3':  case '4':
                case '5':  case '6':  case '7':  case '8':  case '9':
                  return this.processBackreference();

                case 'P':
                case 'p':
                  int pstart = this.offset;
                  tok = processBacksolidus_pP(this.chardata);
                  if (tok == null)  throw this.ex("parser.atom.5", pstart);
                  break;

                default:
                  tok = Token.createChar(this.chardata);
              }
              this.next();
              break;

            case T_CHAR:
              if (this.chardata == ']' || this.chardata == '{' || this.chardata == '}')
                  throw this.ex("parser.atom.4", this.offset-1);
              tok = Token.createChar(this.chardata);
              int high = this.chardata;
              this.next();
              if (REUtil.isHighSurrogate(high)
                  && this.read() == T_CHAR && REUtil.isLowSurrogate(this.chardata)) {
                  char[] sur = new char[2];
                  sur[0] = (char)high;
                  sur[1] = (char)this.chardata;
                  tok = Token.createParen(Token.createString(new String(sur)), 0);
                  this.next();
              }
              break;

            default:
              throw this.ex("parser.atom.4", this.offset-1);
          }
          return tok;
      }

      protected RangeToken processBacksolidus_pP(int c) throws ParseException {

          this.next();
          if (this.read() != T_CHAR || this.chardata != '{')
              throw this.ex("parser.atom.2", this.offset-1);

          // handle category escape
          boolean positive = c == 'p';
          int namestart = this.offset;
          int nameend = this.regex.indexOf('}', namestart);

          if (nameend < 0)
              throw this.ex("parser.atom.3", this.offset);

          String pname = this.regex.substring(namestart, nameend);
          this.offset = nameend+1;

          return Token.getRange(pname, positive, this.isSet(RegularExpression.XMLSCHEMA_MODE));
      }

      int processCIinCharacterClass(RangeToken tok, int c) {
          return this.decodeEscaped();
      }

      /**
       * char-class ::= '[' ( '^'? range ','?)+ ']'
       * range ::= '\d' | '\w' | '\s' | category-block | range-char
       *           | range-char '-' range-char
       * range-char ::= '\[' | '\]' | '\\' | '\' [,-efnrtv] | bmp-code | character-2
       * bmp-code ::= '\' 'u' [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F]
       */
      protected RangeToken parseCharacterClass(boolean useNrange) throws ParseException {
          this.setContext(S_INBRACKETS);
          this.next();                            // '['
          RangeToken base = null;
          RangeToken tok;
          if (this.read() == T_CHAR && this.chardata == '^') {
              this.next();                        // '^'
              if (useNrange) {
                  tok = Token.createNRange();
              } else {
                  base = Token.createRange();
                  base.addRange(0, Token.UTF16_MAX);
                  tok = Token.createRange();
              }
          } else {
              tok = Token.createRange();
          }
          int type;
          boolean firstloop = true;
          while ((type = this.read()) != T_EOF) {
              if (type == T_CHAR && this.chardata == ']' && !firstloop)
                  break;
              firstloop = false;
              int c = this.chardata;
              boolean end = false;
              if (type == T_BACKSOLIDUS) {
                  switch (c) {
                    case 'd':  case 'D':
                    case 'w':  case 'W':
                    case 's':  case 'S':
                      tok.mergeRanges(this.getTokenForShorthand(c));
                      end = true;
                      break;

                    case 'i':  case 'I':
                    case 'c':  case 'C':
                      c = this.processCIinCharacterClass(tok, c);
                      if (c < 0)  end = true;
                      break;
                      
                    case 'p':
                    case 'P':
                      int pstart = this.offset;
                      RangeToken tok2 = this.processBacksolidus_pP(c);
                      if (tok2 == null)  throw this.ex("parser.atom.5", pstart);
                      tok.mergeRanges(tok2);
                      end = true;
                      break;

                    default:
                      c = this.decodeEscaped();
                  } // \ + c
              } // backsolidus
                                                  // POSIX Character class such as [:alnum:]
              else if (type == T_POSIX_CHARCLASS_START) {
                  int nameend = this.regex.indexOf(':', this.offset);
                  if (nameend < 0) throw this.ex("parser.cc.1", this.offset);
                  boolean positive = true;
                  if (this.regex.charAt(this.offset) == '^') {
                      this.offset ++;
                      positive = false;
                  }
                  String name = this.regex.substring(this.offset, nameend);
                  RangeToken range = Token.getRange(name, positive,
                                                    this.isSet(RegularExpression.XMLSCHEMA_MODE));
                  if (range == null)  throw this.ex("parser.cc.3", this.offset);
                  tok.mergeRanges(range);
                  end = true;
                  if (nameend+1 >= this.regexlen || this.regex.charAt(nameend+1) != ']')
                      throw this.ex("parser.cc.1", nameend);
                  this.offset = nameend+2;
              }
              this.next();
              if (!end) {                         // if not shorthands...
                  if (this.read() != T_CHAR || this.chardata != '-') { // Here is no '-'.
                      tok.addRange(c, c);
                  } else {
                      this.next(); // Skips '-'
                      if ((type = this.read()) == T_EOF)  throw this.ex("parser.cc.2", this.offset);
                      if (type == T_CHAR && this.chardata == ']') {
                          tok.addRange(c, c);
                          tok.addRange('-', '-');
                      } else {
                          int rangeend = this.chardata;
                          if (type == T_BACKSOLIDUS)
                              rangeend = this.decodeEscaped();
                          this.next();
                          tok.addRange(c, rangeend);
                      }
                  }
              }
              if (this.isSet(RegularExpression.SPECIAL_COMMA)
                  && this.read() == T_CHAR && this.chardata == ',')
                  this.next();
          }
          if (this.read() == T_EOF)
              throw this.ex("parser.cc.2", this.offset);
          if (base != null) {
              base.subtractRanges(tok);
              tok = base;
          }
          tok.sortRanges();
          tok.compactRanges();
          //tok.dumpRanges();
          /*
          if (this.isSet(RegularExpression.IGNORE_CASE))
              tok = RangeToken.createCaseInsensitiveToken(tok);
          */
          this.setContext(S_NORMAL);
          this.next();                    // Skips ']'

          return tok;
      }

      /**
       * '(?[' ... ']' (('-' | '+' | '&') '[' ... ']')? ')'
       */
      protected RangeToken parseSetOperations() throws ParseException {
          RangeToken tok = this.parseCharacterClass(false);
          int type;
          while ((type = this.read()) != T_RPAREN) {
              int ch = this.chardata;
              if (type == T_CHAR && (ch == '-' || ch == '&')
                  || type == T_PLUS) {
                  this.next();
                  if (this.read() != T_LBRACKET) throw ex("parser.ope.1", this.offset-1);
                  RangeToken t2 = this.parseCharacterClass(false);
                  if (type == T_PLUS)
                      tok.mergeRanges(t2);
                  else if (ch == '-')
                      tok.subtractRanges(t2);
                  else if (ch == '&')
                      tok.intersectRanges(t2);
                  else
                      throw new RuntimeException("ASSERT");
              } else {
                  throw ex("parser.ope.2", this.offset-1);
              }
          }
          this.next();
          return tok;
      }

      Token getTokenForShorthand(int ch) {
          Token tok;
          switch (ch) {
            case 'd':
              tok = this.isSet(RegularExpression.USE_UNICODE_CATEGORY)
                  ? Token.getRange("Nd", true) : Token.token_0to9;
              break;
            case 'D':
              tok = this.isSet(RegularExpression.USE_UNICODE_CATEGORY)
                  ? Token.getRange("Nd", false) : Token.token_not_0to9;
              break;
            case 'w':
              tok = this.isSet(RegularExpression.USE_UNICODE_CATEGORY)
                  ? Token.getRange("IsWord", true) : Token.token_wordchars;
              break;
            case 'W':
              tok = this.isSet(RegularExpression.USE_UNICODE_CATEGORY)
                  ? Token.getRange("IsWord", false) : Token.token_not_wordchars;
              break;
            case 's':
              tok = this.isSet(RegularExpression.USE_UNICODE_CATEGORY)
                  ? Token.getRange("IsSpace", true) : Token.token_spaces;
              break;
            case 'S':
              tok = this.isSet(RegularExpression.USE_UNICODE_CATEGORY)
                  ? Token.getRange("IsSpace", false) : Token.token_not_spaces;
              break;

            default:
              throw new RuntimeException("Internal Error: shorthands: \\u"+Integer.toString(ch, 16));
          }
          return tok;
      }

      /**
       */
      int decodeEscaped() throws ParseException {
          if (this.read() != T_BACKSOLIDUS)  throw ex("parser.next.1", this.offset-1);
          int c = this.chardata;
          switch (c) {
            case 'e':  c = 0x1b;  break; // ESCAPE U+001B
            case 'f':  c = '\f';  break; // FORM FEED U+000C
            case 'n':  c = '\n';  break; // LINE FEED U+000A
            case 'r':  c = '\r';  break; // CRRIAGE RETURN U+000D
            case 't':  c = '\t';  break; // HORIZONTAL TABULATION U+0009
            //case 'v':  c = 0x0b;  break; // VERTICAL TABULATION U+000B
            case 'x':
              this.next();
              if (this.read() != T_CHAR)  throw ex("parser.descape.1", this.offset-1);
              if (this.chardata == '{') {
                  int v1 = 0;
                  int uv = 0;
                  do {
                      this.next();
                      if (this.read() != T_CHAR)  throw ex("parser.descape.1", this.offset-1);
                      if ((v1 = hexChar(this.chardata)) < 0)
                          break;
                      if (uv > uv*16) throw ex("parser.descape.2", this.offset-1);
                      uv = uv*16+v1;
                  } while (true);
                  if (this.chardata != '}')  throw ex("parser.descape.3", this.offset-1);
                  if (uv > Token.UTF16_MAX)  throw ex("parser.descape.4", this.offset-1);
                  c = uv;
              } else {
                  int v1 = 0;
                  if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                      throw ex("parser.descape.1", this.offset-1);
                  int uv = v1;
                  this.next();
                  if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                      throw ex("parser.descape.1", this.offset-1);
                  uv = uv*16+v1;
                  c = uv;
              }
              break;

            case 'u':
              int v1 = 0;
              this.next();
              if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                  throw ex("parser.descape.1", this.offset-1);
              int uv = v1;
              this.next();
              if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                  throw ex("parser.descape.1", this.offset-1);
              uv = uv*16+v1;
              this.next();
              if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                  throw ex("parser.descape.1", this.offset-1);
              uv = uv*16+v1;
              this.next();
              if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                  throw ex("parser.descape.1", this.offset-1);
              uv = uv*16+v1;
              c = uv;
              break;

            case 'v':
              this.next();
              if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                  throw ex("parser.descape.1", this.offset-1);
              uv = v1;
              this.next();
              if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                  throw ex("parser.descape.1", this.offset-1);
              uv = uv*16+v1;
              this.next();
              if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                  throw ex("parser.descape.1", this.offset-1);
              uv = uv*16+v1;
              this.next();
              if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                  throw ex("parser.descape.1", this.offset-1);
              uv = uv*16+v1;
              this.next();
              if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                  throw ex("parser.descape.1", this.offset-1);
              uv = uv*16+v1;
              this.next();
              if (this.read() != T_CHAR || (v1 = hexChar(this.chardata)) < 0)
                  throw ex("parser.descape.1", this.offset-1);
              uv = uv*16+v1;
              if (uv > Token.UTF16_MAX)  throw ex("parser.descappe.4", this.offset-1);
              c = uv;
              break;
            case 'A':
            case 'Z':
            case 'z':
              throw ex("parser.descape.5", this.offset-2);
            default:
          }
          return c;
      }

      static private final int hexChar(int ch) {
          if (ch < '0')  return -1;
          if (ch > 'f')  return -1;
          if (ch <= '9')  return ch-'0';
          if (ch < 'A')  return -1;
          if (ch <= 'F')  return ch-'A'+10;
          if (ch < 'a')  return -1;
          return ch-'a'+10;
      }
  }


  static class Token implements java.io.Serializable {
      private static final long serialVersionUID = 1L;
      static final boolean COUNTTOKENS = true;
      static int tokens = 0;

      static final int CHAR = 0;                  // Literal char
      static final int DOT = 11;                  // .
      static final int CONCAT = 1;                // XY
      static final int UNION = 2;                 // X|Y|Z
      static final int CLOSURE = 3;               // X*
      static final int RANGE = 4;                 // [a-zA-Z] etc.
      static final int NRANGE = 5;                // [^a-zA-Z] etc.
      static final int PAREN = 6;                 // (X) or (?:X)
      static final int EMPTY = 7;                 //
      static final int ANCHOR = 8;                // ^ $ \b \B \< \> \A \Z \z
      static final int NONGREEDYCLOSURE = 9;      // *? +?
      static final int STRING = 10;               // strings
      static final int BACKREFERENCE = 12;        // back references
      static final int LOOKAHEAD = 20;            // (?=...)
      static final int NEGATIVELOOKAHEAD = 21;    // (?!...)
      static final int LOOKBEHIND = 22;           // (?<=...)
      static final int NEGATIVELOOKBEHIND = 23;   // (?<!...)
      static final int INDEPENDENT = 24;          // (?>...)
      static final int MODIFIERGROUP = 25;        // (?ims-ims:...)
      static final int CONDITION = 26;            // (?(...)yes|no)

      static final int UTF16_MAX = 0x10ffff;

      int type;

      static Token token_dot;
      static Token token_0to9;
      static Token token_wordchars;
      static Token token_not_0to9;
      static Token token_not_wordchars;
      static Token token_spaces;
      static Token token_not_spaces;
      static Token token_empty;
      static Token token_linebeginning;
      static Token token_linebeginning2;
      static Token token_lineend;
      static Token token_stringbeginning;
      static Token token_stringend;
      static Token token_stringend2;
      static Token token_wordedge;
      static Token token_not_wordedge;
      static Token token_wordbeginning;
      static Token token_wordend;
      static {
          Token.token_empty = new Token(Token.EMPTY);

          Token.token_linebeginning = Token.createAnchor('^');
          Token.token_linebeginning2 = Token.createAnchor('@');
          Token.token_lineend = Token.createAnchor('$');
          Token.token_stringbeginning = Token.createAnchor('A');
          Token.token_stringend = Token.createAnchor('z');
          Token.token_stringend2 = Token.createAnchor('Z');
          Token.token_wordedge = Token.createAnchor('b');
          Token.token_not_wordedge = Token.createAnchor('B');
          Token.token_wordbeginning = Token.createAnchor('<');
          Token.token_wordend = Token.createAnchor('>');

          Token.token_dot = new Token(Token.DOT);

          Token.token_0to9 = Token.createRange();
          Token.token_0to9.addRange('0', '9');
          Token.token_wordchars = Token.createRange();
          Token.token_wordchars.addRange('0', '9');
          Token.token_wordchars.addRange('A', 'Z');
          Token.token_wordchars.addRange('_', '_');
          Token.token_wordchars.addRange('a', 'z');
          Token.token_spaces = Token.createRange();
          Token.token_spaces.addRange('\t', '\t');
          Token.token_spaces.addRange('\n', '\n');
          Token.token_spaces.addRange('\f', '\f');
          Token.token_spaces.addRange('\r', '\r');
          Token.token_spaces.addRange(' ', ' ');

          Token.token_not_0to9 = Token.complementRanges(Token.token_0to9);
          Token.token_not_wordchars = Token.complementRanges(Token.token_wordchars);
          Token.token_not_spaces = Token.complementRanges(Token.token_spaces);
      }

      static Token.ParenToken createLook(int type, Token child) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.ParenToken(type, child, 0);
      }
      static Token.ParenToken createParen(Token child, int pnumber) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.ParenToken(Token.PAREN, child, pnumber);
      }
      static Token.ClosureToken createClosure(Token tok) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.ClosureToken(Token.CLOSURE, tok);
      }
      static Token.ClosureToken createNGClosure(Token tok) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.ClosureToken(Token.NONGREEDYCLOSURE, tok);
      }
      static Token.ConcatToken createConcat(Token tok1, Token tok2) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.ConcatToken(tok1, tok2);
      }
      static Token.UnionToken createConcat() {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.UnionToken(Token.CONCAT); // *** It is not a bug.
      }
      static Token.UnionToken createUnion() {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.UnionToken(Token.UNION);
      }
      static Token createEmpty() {
          return Token.token_empty;
      }
      static RangeToken createRange() {
          if (COUNTTOKENS)  Token.tokens ++;
          return new RangeToken(Token.RANGE);
      }
      static RangeToken createNRange() {
          if (COUNTTOKENS)  Token.tokens ++;
          return new RangeToken(Token.NRANGE);
      }
      static Token.CharToken createChar(int ch) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.CharToken(Token.CHAR, ch);
      }
      static private Token.CharToken createAnchor(int ch) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.CharToken(Token.ANCHOR, ch);
      }
      static Token.StringToken createBackReference(int refno) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.StringToken(Token.BACKREFERENCE, null, refno);
      }
      static Token.StringToken createString(String str) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.StringToken(Token.STRING, str, 0);
      }
      static Token.ModifierToken createModifierGroup(Token child, int add, int mask) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.ModifierToken(child, add, mask);
      }
      static Token.ConditionToken createCondition(int refno, Token condition,
                                                  Token yespat, Token nopat) {
          if (COUNTTOKENS)  Token.tokens ++;
          return new Token.ConditionToken(refno, condition, yespat, nopat);
      }

      protected Token(int type) {
          this.type = type;
      }

      /**
       * A number of children.
       */
      int size() {
          return 0;
      }
      Token getChild(int index) {
          return null;
      }
      void addChild(Token tok) {
          throw new RuntimeException("Not supported.");
      }

                                                  // for RANGE or NRANGE
      protected void addRange(int start, int end) {
          throw new RuntimeException("Not supported.");
      }
      protected void sortRanges() {
          throw new RuntimeException("Not supported.");
      }
      protected void compactRanges() {
          throw new RuntimeException("Not supported.");
      }
      protected void mergeRanges(Token tok) {
          throw new RuntimeException("Not supported.");
      }
      protected void subtractRanges(Token tok) {
          throw new RuntimeException("Not supported.");
      }
      protected void intersectRanges(Token tok) {
          throw new RuntimeException("Not supported.");
      }
      static Token complementRanges(Token tok) {
          return RangeToken.complementRanges(tok);
      }


      void setMin(int min) {                      // for CLOSURE
      }
      void setMax(int max) {                      // for CLOSURE
      }
      int getMin() {                              // for CLOSURE
          return -1;
      }
      int getMax() {                              // for CLOSURE
          return -1;
      }
      int getReferenceNumber() {                  // for STRING
          return 0;
      }
      String getString() {                        // for STRING
          return null;
      }

      int getParenNumber() {
          return 0;
      }
      int getChar() {
          return -1;
      }

      @Override
      public String toString() {
          return this.toString(0);
      }
      public String toString(int options) {
          return this.type == Token.DOT ? "." : "";
      }

      /**
       * How many characters are needed?
       */
      final int getMinLength() {
          switch (this.type) {
            case CONCAT:
              int sum = 0;
              for (int i = 0;  i < this.size();  i ++)
                  sum += this.getChild(i).getMinLength();
              return sum;

            case CONDITION:
            case UNION:
              if (this.size() == 0)
                  return 0;
              int ret = this.getChild(0).getMinLength();
              for (int i = 1;  i < this.size();  i ++) {
                  int min = this.getChild(i).getMinLength();
                  if (min < ret)  ret = min;
              }
              return ret;

            case CLOSURE:
            case NONGREEDYCLOSURE:
              if (this.getMin() >= 0)
                  return this.getMin() * this.getChild(0).getMinLength();
              return 0;

            case EMPTY:
            case ANCHOR:
              return 0;

            case DOT:
            case CHAR:
            case RANGE:
            case NRANGE:
              return 1;

            case INDEPENDENT:
            case PAREN:
            case MODIFIERGROUP:
              return this.getChild(0).getMinLength();

            case BACKREFERENCE:
              return 0;                           // *******

            case STRING:
              return this.getString().length();

            case LOOKAHEAD:
            case NEGATIVELOOKAHEAD:
            case LOOKBEHIND:
            case NEGATIVELOOKBEHIND:
              return 0;                           // ***** Really?

            default:
              throw new RuntimeException("Token#getMinLength(): Invalid Type: "+this.type);
          }
      }

      final int getMaxLength() {
          switch (this.type) {
            case CONCAT:
              int sum = 0;
              for (int i = 0;  i < this.size();  i ++) {
                  int d = this.getChild(i).getMaxLength();
                  if (d < 0)  return -1;
                  sum += d;
              }
              return sum;

            case CONDITION:
            case UNION:
              if (this.size() == 0)
                  return 0;
              int ret = this.getChild(0).getMaxLength();
              for (int i = 1;  ret >= 0 && i < this.size();  i ++) {
                  int max = this.getChild(i).getMaxLength();
                  if (max < 0) {                  // infinity
                      ret = -1;
                      break;
                  }
                  if (max > ret)  ret = max;
              }
              return ret;

            case CLOSURE:
            case NONGREEDYCLOSURE:
              if (this.getMax() >= 0)
                                                  // When this.child.getMaxLength() < 0,
                                                  // this returns minus value
                  return this.getMax() * this.getChild(0).getMaxLength();
              return -1;

            case EMPTY:
            case ANCHOR:
              return 0;

            case CHAR:
              return 1;
            case DOT:
            case RANGE:
            case NRANGE:
              return 2;

            case INDEPENDENT:
            case PAREN:
            case MODIFIERGROUP:
              return this.getChild(0).getMaxLength();

            case BACKREFERENCE:
              return -1;                          // ******

            case STRING:
              return this.getString().length();

            case LOOKAHEAD:
            case NEGATIVELOOKAHEAD:
            case LOOKBEHIND:
            case NEGATIVELOOKBEHIND:
              return 0;                           // ***** Really?

            default:
              throw new RuntimeException("Token#getMaxLength(): Invalid Type: "+this.type);
          }
      }

      static final int FC_CONTINUE = 0;
      static final int FC_TERMINAL = 1;
      static final int FC_ANY = 2;
      private static final boolean isSet(int options, int flag) {
          return (options & flag) == flag;
      }
      final int analyzeFirstCharacter(RangeToken result, int options) {
          switch (this.type) {
            case CONCAT:
              int ret = FC_CONTINUE;
              for (int i = 0;  i < this.size();  i ++)
                  if ((ret = this.getChild(i).analyzeFirstCharacter(result, options)) != FC_CONTINUE)
                      break;
              return ret;

            case UNION:
              if (this.size() == 0)
                  return FC_CONTINUE;
              /*
               *  a|b|c -> FC_TERMINAL
               *  a|.|c -> FC_ANY
               *  a|b|  -> FC_CONTINUE
               */
              int ret2 = FC_CONTINUE;
              boolean hasEmpty = false;
              for (int i = 0;  i < this.size();  i ++) {
                  ret2 = this.getChild(i).analyzeFirstCharacter(result, options);
                  if (ret2 == FC_ANY)
                      break;
                  else if (ret2 == FC_CONTINUE)
                      hasEmpty = true;
              }
              return hasEmpty ? FC_CONTINUE : ret2;

            case CONDITION:
              int ret3 = this.getChild(0).analyzeFirstCharacter(result, options);
              if (this.size() == 1)  return FC_CONTINUE;
              if (ret3 == FC_ANY)  return ret3;
              int ret4 = this.getChild(1).analyzeFirstCharacter(result, options);
              if (ret4 == FC_ANY)  return ret4;
              return ret3 == FC_CONTINUE || ret4 == FC_CONTINUE ? FC_CONTINUE : FC_TERMINAL;

            case CLOSURE:
            case NONGREEDYCLOSURE:
              this.getChild(0).analyzeFirstCharacter(result, options);
              return FC_CONTINUE;

            case EMPTY:
            case ANCHOR:
              return FC_CONTINUE;

            case CHAR:
              int ch = this.getChar();
              result.addRange(ch, ch);
              if (ch < 0x10000 && isSet(options, RegularExpression.IGNORE_CASE)) {
                  ch = Character.toUpperCase((char)ch);
                  result.addRange(ch, ch);
                  ch = Character.toLowerCase((char)ch);
                  result.addRange(ch, ch);
              }
              return FC_TERMINAL;

            case DOT:                             // ****
              if (isSet(options, RegularExpression.SINGLE_LINE)) {
                  return FC_CONTINUE;             // **** We can not optimize.
              } else {
                  return FC_CONTINUE;
                  /*
                  result.addRange(0, RegularExpression.LINE_FEED-1);
                  result.addRange(RegularExpression.LINE_FEED+1, RegularExpression.CARRIAGE_RETURN-1);
                  result.addRange(RegularExpression.CARRIAGE_RETURN+1,
                                  RegularExpression.LINE_SEPARATOR-1);
                  result.addRange(RegularExpression.PARAGRAPH_SEPARATOR+1, UTF16_MAX);
                  return 1;
                  */
              }

            case RANGE:
              if (isSet(options, RegularExpression.IGNORE_CASE)) {
                  result.mergeRanges(((RangeToken)this).getCaseInsensitiveToken());
              } else {
                  result.mergeRanges(this);
              }
              return FC_TERMINAL;

            case NRANGE:                          // ****
              if (isSet(options, RegularExpression.IGNORE_CASE)) {
                  result.mergeRanges(Token.complementRanges(((RangeToken)this).getCaseInsensitiveToken()));
              } else {
                  result.mergeRanges(Token.complementRanges(this));
              }
              return FC_TERMINAL;

            case INDEPENDENT:
            case PAREN:
              return this.getChild(0).analyzeFirstCharacter(result, options);

            case MODIFIERGROUP:
              options |= ((ModifierToken)this).getOptions();
              options &= ~((ModifierToken)this).getOptionsMask();
              return this.getChild(0).analyzeFirstCharacter(result, options);

            case BACKREFERENCE:
              result.addRange(0, UTF16_MAX);  // **** We can not optimize.
              return FC_ANY;

            case STRING:
              int cha = this.getString().charAt(0);
              int ch2;
              if (REUtil.isHighSurrogate(cha)
                  && this.getString().length() >= 2
                  && REUtil.isLowSurrogate((ch2 = this.getString().charAt(1))))
                  cha = REUtil.composeFromSurrogates(cha, ch2);
              result.addRange(cha, cha);
              if (cha < 0x10000 && isSet(options, RegularExpression.IGNORE_CASE)) {
                  cha = Character.toUpperCase((char)cha);
                  result.addRange(cha, cha);
                  cha = Character.toLowerCase((char)cha);
                  result.addRange(cha, cha);
              }
              return FC_TERMINAL;

            case LOOKAHEAD:
            case NEGATIVELOOKAHEAD:
            case LOOKBEHIND:
            case NEGATIVELOOKBEHIND:
              return FC_CONTINUE;

            default:
              throw new RuntimeException("Token#analyzeHeadCharacter(): Invalid Type: "+this.type);
          }
      }

      private final boolean isShorterThan(Token tok) {
          if (tok == null)  return false;
          /*
          int mylength;
          if (this.type == STRING)  mylength = this.getString().length();
          else if (this.type == CHAR)  mylength = this.getChar() >= 0x10000 ? 2 : 1;
          else throw new RuntimeException("Internal Error: Illegal type: "+this.type);
          int otherlength;
          if (tok.type == STRING)  otherlength = tok.getString().length();
          else if (tok.type == CHAR)  otherlength = tok.getChar() >= 0x10000 ? 2 : 1;
          else throw new RuntimeException("Internal Error: Illegal type: "+tok.type);
          */
          int mylength;
          if (this.type == STRING)  mylength = this.getString().length();
          else throw new RuntimeException("Internal Error: Illegal type: "+this.type);
          int otherlength;
          if (tok.type == STRING)  otherlength = tok.getString().length();
          else throw new RuntimeException("Internal Error: Illegal type: "+tok.type);
          return mylength < otherlength;
      }

      static class FixedStringContainer {
          Token token = null;
          int options = 0;
          FixedStringContainer() {
            super();
          }
      }

      final void findFixedString(FixedStringContainer container, int options) {
          switch (this.type) {
            case CONCAT:
              Token prevToken = null;
              int prevOptions = 0;
              for (int i = 0;  i < this.size();  i ++) {
                  this.getChild(i).findFixedString(container, options);
                  if (prevToken == null || prevToken.isShorterThan(container.token)) {
                      prevToken = container.token;
                      prevOptions = container.options;
                  }
              }
              container.token = prevToken;
              container.options = prevOptions;
              return;

            case UNION:
            case CLOSURE:
            case NONGREEDYCLOSURE:
            case EMPTY:
            case ANCHOR:
            case RANGE:
            case DOT:
            case NRANGE:
            case BACKREFERENCE:
            case LOOKAHEAD:
            case NEGATIVELOOKAHEAD:
            case LOOKBEHIND:
            case NEGATIVELOOKBEHIND:
            case CONDITION:
              container.token = null;
              return;

            case CHAR:                            // Ignore CHAR tokens.
              container.token = null;             // **
              return;                             // **

            case STRING:
              container.token = this;
              container.options = options;
              return;

            case INDEPENDENT:
            case PAREN:
              this.getChild(0).findFixedString(container, options);
              return;

            case MODIFIERGROUP:
              options |= ((ModifierToken)this).getOptions();
              options &= ~((ModifierToken)this).getOptionsMask();
              this.getChild(0).findFixedString(container, options);
              return;

            default:
              throw new RuntimeException("Token#findFixedString(): Invalid Type: "+this.type);
          }
      }

      boolean match(int ch) {
          throw new RuntimeException("NFAArrow#match(): Internal error: "+this.type);
      }

      // ------------------------------------------------------
      private final static HashMap<String, Token> categories = new HashMap<String, Token>();
      private final static HashMap<String, Token> categories2 = new HashMap<String, Token>();
      private static final String[] categoryNames = {
          "Cn", "Lu", "Ll", "Lt", "Lm", "Lo", "Mn", "Me", "Mc", "Nd",
          "Nl", "No", "Zs", "Zl", "Zp", "Cc", "Cf", null, "Co", "Cs",
          "Pd", "Ps", "Pe", "Pc", "Po", "Sm", "Sc", "Sk", "So", // 28
          "Pi", "Pf",  // 29, 30
          "L", "M", "N", "Z", "C", "P", "S",      // 31-37
      };

      // Schema Rec. {Datatypes} - Punctuation 
      static final int CHAR_INIT_QUOTE  = 29;     // Pi - initial quote
      static final int CHAR_FINAL_QUOTE = 30;     // Pf - final quote
      static final int CHAR_LETTER = 31;
      static final int CHAR_MARK = 32;
      static final int CHAR_NUMBER = 33;
      static final int CHAR_SEPARATOR = 34;
      static final int CHAR_OTHER = 35;
      static final int CHAR_PUNCTUATION = 36;
      static final int CHAR_SYMBOL = 37;
      
      //blockNames in UNICODE 3.1 that supported by XML Schema REC             
      private static final String[] blockNames = {
          /*0000..007F;*/ "Basic Latin",
          /*0080..00FF;*/ "Latin-1 Supplement",
          /*0100..017F;*/ "Latin Extended-A",
          /*0180..024F;*/ "Latin Extended-B",
          /*0250..02AF;*/ "IPA Extensions",
          /*02B0..02FF;*/ "Spacing Modifier Letters",
          /*0300..036F;*/ "Combining Diacritical Marks",
          /*0370..03FF;*/ "Greek",
          /*0400..04FF;*/ "Cyrillic",
          /*0530..058F;*/ "Armenian",
          /*0590..05FF;*/ "Hebrew",
          /*0600..06FF;*/ "Arabic",
          /*0700..074F;*/ "Syriac",  
          /*0780..07BF;*/ "Thaana",
          /*0900..097F;*/ "Devanagari",
          /*0980..09FF;*/ "Bengali",
          /*0A00..0A7F;*/ "Gurmukhi",
          /*0A80..0AFF;*/ "Gujarati",
          /*0B00..0B7F;*/ "Oriya",
          /*0B80..0BFF;*/ "Tamil",
          /*0C00..0C7F;*/ "Telugu",
          /*0C80..0CFF;*/ "Kannada",
          /*0D00..0D7F;*/ "Malayalam",
          /*0D80..0DFF;*/ "Sinhala",
          /*0E00..0E7F;*/ "Thai",
          /*0E80..0EFF;*/ "Lao",
          /*0F00..0FFF;*/ "Tibetan",
          /*1000..109F;*/ "Myanmar", 
          /*10A0..10FF;*/ "Georgian",
          /*1100..11FF;*/ "Hangul Jamo",
          /*1200..137F;*/ "Ethiopic",
          /*13A0..13FF;*/ "Cherokee",
          /*1400..167F;*/ "Unified Canadian Aboriginal Syllabics",
          /*1680..169F;*/ "Ogham",
          /*16A0..16FF;*/ "Runic",
          /*1780..17FF;*/ "Khmer",
          /*1800..18AF;*/ "Mongolian",
          /*1E00..1EFF;*/ "Latin Extended Additional",
          /*1F00..1FFF;*/ "Greek Extended",
          /*2000..206F;*/ "General Punctuation",
          /*2070..209F;*/ "Superscripts and Subscripts",
          /*20A0..20CF;*/ "Currency Symbols",
          /*20D0..20FF;*/ "Combining Marks for Symbols",
          /*2100..214F;*/ "Letterlike Symbols",
          /*2150..218F;*/ "Number Forms",
          /*2190..21FF;*/ "Arrows",
          /*2200..22FF;*/ "Mathematical Operators",
          /*2300..23FF;*/ "Miscellaneous Technical",
          /*2400..243F;*/ "Control Pictures",
          /*2440..245F;*/ "Optical Character Recognition",
          /*2460..24FF;*/ "Enclosed Alphanumerics",
          /*2500..257F;*/ "Box Drawing",
          /*2580..259F;*/ "Block Elements",
          /*25A0..25FF;*/ "Geometric Shapes",
          /*2600..26FF;*/ "Miscellaneous Symbols",
          /*2700..27BF;*/ "Dingbats",
          /*2800..28FF;*/ "Braille Patterns",
          /*2E80..2EFF;*/ "CJK Radicals Supplement",
          /*2F00..2FDF;*/ "Kangxi Radicals",
          /*2FF0..2FFF;*/ "Ideographic Description Characters",
          /*3000..303F;*/ "CJK Symbols and Punctuation",
          /*3040..309F;*/ "Hiragana",
          /*30A0..30FF;*/ "Katakana",
          /*3100..312F;*/ "Bopomofo",
          /*3130..318F;*/ "Hangul Compatibility Jamo",
          /*3190..319F;*/ "Kanbun",
          /*31A0..31BF;*/ "Bopomofo Extended",
          /*3200..32FF;*/ "Enclosed CJK Letters and Months",
          /*3300..33FF;*/ "CJK Compatibility",
          /*3400..4DB5;*/ "CJK Unified Ideographs Extension A",
          /*4E00..9FFF;*/ "CJK Unified Ideographs",
          /*A000..A48F;*/ "Yi Syllables",
          /*A490..A4CF;*/ "Yi Radicals",
          /*AC00..D7A3;*/ "Hangul Syllables",
          /*E000..F8FF;*/ "Private Use",
          /*F900..FAFF;*/ "CJK Compatibility Ideographs",
          /*FB00..FB4F;*/ "Alphabetic Presentation Forms",
          /*FB50..FDFF;*/ "Arabic Presentation Forms-A",
          /*FE20..FE2F;*/ "Combining Half Marks",
          /*FE30..FE4F;*/ "CJK Compatibility Forms",
          /*FE50..FE6F;*/ "Small Form Variants",
          /*FE70..FEFE;*/ "Arabic Presentation Forms-B",
          /*FEFF..FEFF;*/ "Specials",
          /*FF00..FFEF;*/ "Halfwidth and Fullwidth Forms",
           //missing Specials add manually
          /*10300..1032F;*/ "Old Italic",   // 84
          /*10330..1034F;*/ "Gothic",
          /*10400..1044F;*/ "Deseret",
          /*1D000..1D0FF;*/ "Byzantine Musical Symbols",
          /*1D100..1D1FF;*/ "Musical Symbols",
          /*1D400..1D7FF;*/ "Mathematical Alphanumeric Symbols",
          /*20000..2A6D6;*/ "CJK Unified Ideographs Extension B",
          /*2F800..2FA1F;*/ "CJK Compatibility Ideographs Supplement",
          /*E0000..E007F;*/ "Tags",
          //missing 2 private use add manually

      };
      //ADD THOSE MANUALLY
      //F0000..FFFFD; "Private Use",
      //100000..10FFFD; "Private Use"
      //FFF0..FFFD; "Specials", 
      static final String blockRanges = 
         "\u0000\u007F\u0080\u00FF\u0100\u017F\u0180\u024F\u0250\u02AF\u02B0\u02FF\u0300\u036F"
          +"\u0370\u03FF\u0400\u04FF\u0530\u058F\u0590\u05FF\u0600\u06FF\u0700\u074F\u0780\u07BF"
          +"\u0900\u097F\u0980\u09FF\u0A00\u0A7F\u0A80\u0AFF\u0B00\u0B7F\u0B80\u0BFF\u0C00\u0C7F\u0C80\u0CFF"
          +"\u0D00\u0D7F\u0D80\u0DFF\u0E00\u0E7F\u0E80\u0EFF\u0F00\u0FFF\u1000\u109F\u10A0\u10FF\u1100\u11FF"
          +"\u1200\u137F\u13A0\u13FF\u1400\u167F\u1680\u169F\u16A0\u16FF\u1780\u17FF\u1800\u18AF\u1E00\u1EFF"
          +"\u1F00\u1FFF\u2000\u206F\u2070\u209F\u20A0\u20CF\u20D0\u20FF\u2100\u214F\u2150\u218F\u2190\u21FF\u2200\u22FF"
          +"\u2300\u23FF\u2400\u243F\u2440\u245F\u2460\u24FF\u2500\u257F\u2580\u259F\u25A0\u25FF\u2600\u26FF\u2700\u27BF"
          +"\u2800\u28FF\u2E80\u2EFF\u2F00\u2FDF\u2FF0\u2FFF\u3000\u303F\u3040\u309F\u30A0\u30FF\u3100\u312F\u3130\u318F"
          +"\u3190\u319F\u31A0\u31BF\u3200\u32FF\u3300\u33FF\u3400\u4DB5\u4E00\u9FFF\uA000\uA48F\uA490\uA4CF"
          +"\uAC00\uD7A3\uE000\uF8FF\uF900\uFAFF\uFB00\uFB4F\uFB50\uFDFF"
          +"\uFE20\uFE2F\uFE30\uFE4F\uFE50\uFE6F\uFE70\uFEFE\uFEFF\uFEFF\uFF00\uFFEF";
      static final int[] nonBMPBlockRanges = {
          0x10300, 0x1032F,       // 84
          0x10330, 0x1034F,
          0x10400, 0x1044F,
          0x1D000, 0x1D0FF,
          0x1D100, 0x1D1FF,
          0x1D400, 0x1D7FF,
          0x20000, 0x2A6D6,
          0x2F800, 0x2FA1F,
          0xE0000, 0xE007F
      };
      private static final int NONBMP_BLOCK_START = 84;

      static protected RangeToken getRange(String name, boolean positive) {
          if (Token.categories.size() == 0) {
              synchronized (Token.categories) {
                  Token[] ranges = new Token[Token.categoryNames.length];
                  for (int i = 0;  i < ranges.length;  i ++) {
                      ranges[i] = Token.createRange();
                  }
                  /*
                  int type;
                  for (int i = 0;  i < 0x10000;  i ++) {
                      type = Character.getType((char)i);
                      if (type == Character.START_PUNCTUATION || 
                          type == Character.END_PUNCTUATION) {
                          //build table of Pi values
                          if (i == 0x00AB || i == 0x2018 || i == 0x201B || i == 0x201C ||
                              i == 0x201F || i == 0x2039) {
                              type = CHAR_INIT_QUOTE;
                          }
                          //build table of Pf values
                          if (i == 0x00BB || i == 0x2019 || i == 0x201D || i == 0x203A ) {
                              type = CHAR_FINAL_QUOTE;
                          }
                      }
                      ranges[type].addRange(i, i);
                      switch (type) {
                        case Character.UPPERCASE_LETTER:
                        case Character.LOWERCASE_LETTER:
                        case Character.TITLECASE_LETTER:
                        case Character.MODIFIER_LETTER:
                        case Character.OTHER_LETTER:
                          type = CHAR_LETTER;
                          break;
                        case Character.NON_SPACING_MARK:
                        case Character.COMBINING_SPACING_MARK:
                        case Character.ENCLOSING_MARK:
                          type = CHAR_MARK;
                          break;
                        case Character.DECIMAL_DIGIT_NUMBER:
                        case Character.LETTER_NUMBER:
                        case Character.OTHER_NUMBER:
                          type = CHAR_NUMBER;
                          break;
                        case Character.SPACE_SEPARATOR:
                        case Character.LINE_SEPARATOR:
                        case Character.PARAGRAPH_SEPARATOR:
                          type = CHAR_SEPARATOR;
                          break;
                        case Character.CONTROL:
                        case Character.FORMAT:
                        case Character.SURROGATE:
                        case Character.PRIVATE_USE:
                        case Character.UNASSIGNED:
                          type = CHAR_OTHER;
                          break;
                        case Character.CONNECTOR_PUNCTUATION:
                        case Character.DASH_PUNCTUATION:
                        case Character.START_PUNCTUATION:
                        case Character.END_PUNCTUATION:
                        case CHAR_INIT_QUOTE:
                        case CHAR_FINAL_QUOTE:
                        case Character.OTHER_PUNCTUATION:
                          type = CHAR_PUNCTUATION;
                          break;
                        case Character.MATH_SYMBOL:
                        case Character.CURRENCY_SYMBOL:
                        case Character.MODIFIER_SYMBOL:
                        case Character.OTHER_SYMBOL:
                          type = CHAR_SYMBOL;
                          break;
                        default:
                          throw new RuntimeException("org.apache.xerces.utils.regex.Token#getRange(): Unknown Unicode category: "+type);
                      }
                      ranges[type].addRange(i, i);
                  } // for all characters
                  ranges[Character.UNASSIGNED].addRange(0x10000, Token.UTF16_MAX);

                  for (int i = 0;  i < ranges.length;  i ++) {
                      if (Token.categoryNames[i] != null) {
                          if (i == Character.UNASSIGNED) { // Unassigned
                              ranges[i].addRange(0x10000, Token.UTF16_MAX);
                          }
                          Token.categories.put(Token.categoryNames[i], ranges[i]);
                          Token.categories2.put(Token.categoryNames[i],
                                                Token.complementRanges(ranges[i]));
                      }
                  }
                  */
                  //REVISIT: do we really need to support block names as in Unicode 3.1
                  //         or we can just create all the names in IsBLOCKNAME format (XML Schema REC)?
                  //
                  StringBuffer buffer = new StringBuffer(50);
                  for (int i = 0;  i < Token.blockNames.length;  i ++) {
                      Token r1 = Token.createRange();
                      int location;
                      if (i < NONBMP_BLOCK_START) {
                          location = i*2;
                          int rstart = Token.blockRanges.charAt(location);
                          int rend = Token.blockRanges.charAt(location+1);
                          //DEBUGING
                          //System.out.println(n+" " +Integer.toHexString(rstart)
                          //                     +"-"+ Integer.toHexString(rend));
                          r1.addRange(rstart, rend);
                      } else {
                          location = (i - NONBMP_BLOCK_START) * 2;
                          r1.addRange(Token.nonBMPBlockRanges[location],
                                      Token.nonBMPBlockRanges[location + 1]);
                      }
                      String n = Token.blockNames[i];
                      if (n.equals("Specials"))
                          r1.addRange(0xfff0, 0xfffd);
                      if (n.equals("Private Use")) {
                          r1.addRange(0xF0000,0xFFFFD);
                          r1.addRange(0x100000,0x10FFFD);
                      }
                      Token.categories.put(n, r1);
                      Token.categories2.put(n, Token.complementRanges(r1));
                      buffer.setLength(0);
                      buffer.append("Is");
                      if (n.indexOf(' ') >= 0) {
                          for (int ci = 0;  ci < n.length();  ci ++)
                              if (n.charAt(ci) != ' ')  buffer.append(n.charAt(ci));
                      }
                      else {
                          buffer.append(n);
                      }
                      Token.setAlias(buffer.toString(), n, true);
                  }

                  // TR#18 1.2
                  Token.setAlias("ASSIGNED", "Cn", false);
                  Token.setAlias("UNASSIGNED", "Cn", true);
                  Token all = Token.createRange();
                  all.addRange(0, Token.UTF16_MAX);
                  Token.categories.put("ALL", all);
                  Token.categories2.put("ALL", Token.complementRanges(all));
                  Token.registerNonXS("ASSIGNED");
                  Token.registerNonXS("UNASSIGNED");
                  Token.registerNonXS("ALL");

                  // TODO
                  /*
                  Token isalpha = Token.createRange();
                  isalpha.mergeRanges(ranges[Character.UPPERCASE_LETTER]); // Lu
                  isalpha.mergeRanges(ranges[Character.LOWERCASE_LETTER]); // Ll
                  isalpha.mergeRanges(ranges[Character.OTHER_LETTER]); // Lo
                  Token.categories.put("IsAlpha", isalpha);
                  Token.categories2.put("IsAlpha", Token.complementRanges(isalpha));
                  Token.registerNonXS("IsAlpha");

                  Token isalnum = Token.createRange();
                  isalnum.mergeRanges(isalpha);   // Lu Ll Lo
                  isalnum.mergeRanges(ranges[Character.DECIMAL_DIGIT_NUMBER]); // Nd
                  Token.categories.put("IsAlnum", isalnum);
                  Token.categories2.put("IsAlnum", Token.complementRanges(isalnum));
                  Token.registerNonXS("IsAlnum");

                  Token isspace = Token.createRange();
                  isspace.mergeRanges(Token.token_spaces);
                  isspace.mergeRanges(ranges[CHAR_SEPARATOR]); // Z
                  Token.categories.put("IsSpace", isspace);
                  Token.categories2.put("IsSpace", Token.complementRanges(isspace));
                  Token.registerNonXS("IsSpace");

                  Token isword = Token.createRange();
                  isword.mergeRanges(isalnum);     // Lu Ll Lo Nd
                  isword.addRange('_', '_');
                  Token.categories.put("IsWord", isword);
                  Token.categories2.put("IsWord", Token.complementRanges(isword));
                  Token.registerNonXS("IsWord");

                  Token isascii = Token.createRange();
                  isascii.addRange(0, 127);
                  Token.categories.put("IsASCII", isascii);
                  Token.categories2.put("IsASCII", Token.complementRanges(isascii));
                  Token.registerNonXS("IsASCII");

                  Token isnotgraph = Token.createRange();
                  isnotgraph.mergeRanges(ranges[CHAR_OTHER]);
                  isnotgraph.addRange(' ', ' ');
                  Token.categories.put("IsGraph", Token.complementRanges(isnotgraph));
                  Token.categories2.put("IsGraph", isnotgraph);
                  Token.registerNonXS("IsGraph");

                  Token isxdigit = Token.createRange();
                  isxdigit.addRange('0', '9');
                  isxdigit.addRange('A', 'F');
                  isxdigit.addRange('a', 'f');
                  Token.categories.put("IsXDigit", Token.complementRanges(isxdigit));
                  Token.categories2.put("IsXDigit", isxdigit);
                  Token.registerNonXS("IsXDigit");

                  Token.setAlias("IsDigit", "Nd", true);
                  Token.setAlias("IsUpper", "Lu", true);
                  Token.setAlias("IsLower", "Ll", true);
                  Token.setAlias("IsCntrl", "C", true);
                  Token.setAlias("IsPrint", "C", false);
                  Token.setAlias("IsPunct", "P", true);
                  Token.registerNonXS("IsDigit");
                  Token.registerNonXS("IsUpper");
                  Token.registerNonXS("IsLower");
                  Token.registerNonXS("IsCntrl");
                  Token.registerNonXS("IsPrint");
                  Token.registerNonXS("IsPunct");

                  Token.setAlias("alpha", "IsAlpha", true);
                  Token.setAlias("alnum", "IsAlnum", true);
                  Token.setAlias("ascii", "IsASCII", true);
                  Token.setAlias("cntrl", "IsCntrl", true);
                  Token.setAlias("digit", "IsDigit", true);
                  Token.setAlias("graph", "IsGraph", true);
                  Token.setAlias("lower", "IsLower", true);
                  Token.setAlias("print", "IsPrint", true);
                  Token.setAlias("punct", "IsPunct", true);
                  Token.setAlias("space", "IsSpace", true);
                  Token.setAlias("upper", "IsUpper", true);
                  Token.setAlias("word", "IsWord", true); // Perl extension
                  Token.setAlias("xdigit", "IsXDigit", true);
                  Token.registerNonXS("alpha");
                  Token.registerNonXS("alnum");
                  Token.registerNonXS("ascii");
                  Token.registerNonXS("cntrl");
                  Token.registerNonXS("digit");
                  Token.registerNonXS("graph");
                  Token.registerNonXS("lower");
                  Token.registerNonXS("print");
                  Token.registerNonXS("punct");
                  Token.registerNonXS("space");
                  Token.registerNonXS("upper");
                  Token.registerNonXS("word");
                  Token.registerNonXS("xdigit");
                  */
              } // synchronized
          } // if null
          RangeToken tok = positive ? (RangeToken)Token.categories.get(name)
              : (RangeToken)Token.categories2.get(name);
          //if (tok == null) System.out.println(name);
          return tok;
      }
      static protected RangeToken getRange(String name, boolean positive, boolean xs) {
          RangeToken range = Token.getRange(name, positive);
          if (xs && range != null && Token.isRegisterNonXS(name))
              range = null;
          return range;
      }

      static HashMap<String, String> nonxs = null;
      /**
       * This method is called by only getRange().
       * So this method need not MT-safe.
       */
      static protected void registerNonXS(String name) {
          if (Token.nonxs == null)
              Token.nonxs = new HashMap<String, String>();
          Token.nonxs.put(name, name);
      }
      static protected boolean isRegisterNonXS(String name) {
          if (Token.nonxs == null)
              return false;
          //DEBUG
          //System.err.println("isRegisterNonXS: "+name);
          return Token.nonxs.containsKey(name);
      }

      private static void setAlias(String newName, String name, boolean positive) {
          Token t1 = Token.categories.get(name);
          Token t2 = Token.categories2.get(name);
          if (positive) {
              Token.categories.put(newName, t1);
              Token.categories2.put(newName, t2);
          } else {
              Token.categories2.put(newName, t1);
              Token.categories.put(newName, t2);
          }
      }

      // ------------------------------------------------------

      static final String viramaString =
      "\u094D"// ;DEVANAGARI SIGN VIRAMA;Mn;9;ON;;;;;N;;;;;
      +"\u09CD"//;BENGALI SIGN VIRAMA;Mn;9;ON;;;;;N;;;;;
      +"\u0A4D"//;GURMUKHI SIGN VIRAMA;Mn;9;ON;;;;;N;;;;;
      +"\u0ACD"//;GUJARATI SIGN VIRAMA;Mn;9;ON;;;;;N;;;;;
      +"\u0B4D"//;ORIYA SIGN VIRAMA;Mn;9;ON;;;;;N;;;;;
      +"\u0BCD"//;TAMIL SIGN VIRAMA;Mn;9;ON;;;;;N;;;;;
      +"\u0C4D"//;TELUGU SIGN VIRAMA;Mn;9;ON;;;;;N;;;;;
      +"\u0CCD"//;KANNADA SIGN VIRAMA;Mn;9;ON;;;;;N;;;;;
      +"\u0D4D"//;MALAYALAM SIGN VIRAMA;Mn;9;ON;;;;;N;;;;;
      +"\u0E3A"//;THAI CHARACTER PHINTHU;Mn;9;ON;;;;;N;THAI VOWEL SIGN PHINTHU;;;;
      +"\u0F84";//;TIBETAN MARK HALANTA;Mn;9;ON;;;;;N;TIBETAN VIRAMA;;;;

      static private Token token_grapheme = null;
      static synchronized Token getGraphemePattern() {
          if (Token.token_grapheme != null)
              return Token.token_grapheme;

          Token base_char = Token.createRange();  // [{ASSIGNED}]-[{M},{C}]
          base_char.mergeRanges(Token.getRange("ASSIGNED", true));
          base_char.subtractRanges(Token.getRange("M", true));
          base_char.subtractRanges(Token.getRange("C", true));

          Token virama = Token.createRange();
          for (int i = 0;  i < Token.viramaString.length();  i ++) {
              virama.addRange(i, i);
          }

          Token combiner_wo_virama = Token.createRange();
          combiner_wo_virama.mergeRanges(Token.getRange("M", true));
          combiner_wo_virama.addRange(0x1160, 0x11ff); // hangul_medial and hangul_final
          combiner_wo_virama.addRange(0xff9e, 0xff9f); // extras

          Token left = Token.createUnion();       // base_char?
          left.addChild(base_char);
          left.addChild(Token.token_empty);

          Token foo = Token.createUnion();
          foo.addChild(Token.createConcat(virama, Token.getRange("L", true)));
          foo.addChild(combiner_wo_virama);

          foo = Token.createClosure(foo);

          foo = Token.createConcat(left, foo);

          Token.token_grapheme = foo;
          return Token.token_grapheme;
      }

      /**
       * Combing Character Sequence in Perl 5.6.
       */
      static private Token token_ccs = null;
      static synchronized Token getCombiningCharacterSequence() {
          if (Token.token_ccs != null)
              return Token.token_ccs;

          Token foo = Token.createClosure(Token.getRange("M", true)); // \pM*
          foo = Token.createConcat(Token.getRange("M", false), foo); // \PM + \pM*
          Token.token_ccs = foo;
          return Token.token_ccs;
      }

      // ------------------------------------------------------

      // ------------------------------------------------------
      /**
       * This class represents a node in parse tree.
       */
      static class StringToken extends Token implements java.io.Serializable {
          private static final long serialVersionUID = 1L;
          String string;
          int refNumber;

          StringToken(int type, String str, int n) {
              super(type);
              this.string = str;
              this.refNumber = n;
          }

          @Override
          int getReferenceNumber() {              // for STRING
              return this.refNumber;
          }
          @Override
          String getString() {                    // for STRING
              return this.string;
          }

          @Override
          public String toString(int options) {
              if (this.type == BACKREFERENCE)
                  return "\\"+this.refNumber;
              else
                  return REUtil.quoteMeta(this.string);
          }
      }

      /**
       * This class represents a node in parse tree.
       */
      static class ConcatToken extends Token implements java.io.Serializable {
          private static final long serialVersionUID = 1L;
          Token child;
          Token child2;
          
          ConcatToken(Token t1, Token t2) {
              super(Token.CONCAT);
              this.child = t1;
              this.child2 = t2;
          }

          @Override
          int size() {
              return 2;
          }
          @Override
          Token getChild(int index) {
              return index == 0 ? this.child : this.child2;
          }

          @Override
          public String toString(int options) {
              String ret;
              if (this.child2.type == CLOSURE && this.child2.getChild(0) == this.child) {
                  ret = this.child.toString(options)+"+";
              } else if (this.child2.type == NONGREEDYCLOSURE && this.child2.getChild(0) == this.child) {
                  ret = this.child.toString(options)+"+?";
              } else
                  ret = this.child.toString(options)+this.child2.toString(options);
              return ret;
          }
      }

      /**
       * This class represents a node in parse tree.
       */
      static class CharToken extends Token implements java.io.Serializable {
          private static final long serialVersionUID = 1L;

          int chardata;

          CharToken(int type, int ch) {
              super(type);
              this.chardata = ch;
          }

          @Override
          int getChar() {
              return this.chardata;
          }

          @Override
          public String toString(int options) {
              String ret;
              switch (this.type) {
                case CHAR:
                  switch (this.chardata) {
                    case '|':  case '*':  case '+':  case '?':
                    case '(':  case ')':  case '.':  case '[':
                    case '{':  case '\\':
                      ret = "\\"+(char)this.chardata;
                      break;
                    case '\f':  ret = "\\f";  break;
                    case '\n':  ret = "\\n";  break;
                    case '\r':  ret = "\\r";  break;
                    case '\t':  ret = "\\t";  break;
                    case 0x1b:  ret = "\\e";  break;
                      //case 0x0b:  ret = "\\v";  break;
                    default:
                      if (this.chardata >= 0x10000) {
                          String pre = "0"+Integer.toHexString(this.chardata);
                          ret = "\\v"+pre.substring(pre.length()-6, pre.length());
                      } else
                          ret = ""+(char)this.chardata;
                  }
                  break;

                case ANCHOR:
                  if (this == Token.token_linebeginning || this == Token.token_lineend)
                      ret = ""+(char)this.chardata;
                  else 
                      ret = "\\"+(char)this.chardata;
                  break;

                default:
                  ret = null;
              }
              return ret;
          }

          @Override
          boolean match(int ch) {
              if (this.type == CHAR) {
                  return ch == this.chardata;
              } else
                  throw new RuntimeException("NFAArrow#match(): Internal error: "+this.type);
          }
      }

      /**
       * This class represents a node in parse tree.
       */
      static class ClosureToken extends Token implements java.io.Serializable {
          private static final long serialVersionUID = 1L;
          int min;
          int max;
          Token child;

          ClosureToken(int type, Token tok) {
              super(type);
              this.child = tok;
              this.setMin(-1);
              this.setMax(-1);
          }

          @Override
          int size() {
              return 1;
          }
          @Override
          Token getChild(int index) {
              return this.child;
          }

          @Override
          final void setMin(int min) {
              this.min = min;
          }
          @Override
          final void setMax(int max) {
              this.max = max;
          }
          @Override
          final int getMin() {
              return this.min;
          }
          @Override
          final int getMax() {
              return this.max;
          }

          @Override
          public String toString(int options) {
              String ret;
              if (this.type == CLOSURE) {
                  if (this.getMin() < 0 && this.getMax() < 0) {
                      ret = this.child.toString(options)+"*";
                  } else if (this.getMin() == this.getMax()) {
                      ret = this.child.toString(options)+"{"+this.getMin()+"}";
                  } else if (this.getMin() >= 0 && this.getMax() >= 0) {
                      ret = this.child.toString(options)+"{"+this.getMin()+","+this.getMax()+"}";
                  } else if (this.getMin() >= 0 && this.getMax() < 0) {
                      ret = this.child.toString(options)+"{"+this.getMin()+",}";
                  } else
                      throw new RuntimeException("Token#toString(): CLOSURE "
                                                 +this.getMin()+", "+this.getMax());
              } else {
                  if (this.getMin() < 0 && this.getMax() < 0) {
                      ret = this.child.toString(options)+"*?";
                  } else if (this.getMin() == this.getMax()) {
                      ret = this.child.toString(options)+"{"+this.getMin()+"}?";
                  } else if (this.getMin() >= 0 && this.getMax() >= 0) {
                      ret = this.child.toString(options)+"{"+this.getMin()+","+this.getMax()+"}?";
                  } else if (this.getMin() >= 0 && this.getMax() < 0) {
                      ret = this.child.toString(options)+"{"+this.getMin()+",}?";
                  } else
                      throw new RuntimeException("Token#toString(): NONGREEDYCLOSURE "
                                                 + this.getMin() + ", " + this.getMax());
        }
        return ret;
      }
    }

    /**
     * This class represents a node in parse tree.
     */
    static class ParenToken extends Token implements java.io.Serializable
    {
      private static final long serialVersionUID = 1L;

      Token child;

      int parennumber;

      ParenToken(int type, Token tok, int paren)
      {
        super(type);
        this.child = tok;
        this.parennumber = paren;
      }

      @Override
      int size()
      {
        return 1;
      }

      @Override
      Token getChild(int index)
      {
        return this.child;
      }

      @Override
      int getParenNumber()
      {
        return this.parennumber;
      }

      @Override
      public String toString(int options)
      {
        String ret = null;
        switch (this.type)
        {
          case PAREN:
            if (this.parennumber == 0)
            {
              ret = "(?:" + this.child.toString(options) + ")";
            }
            else
            {
              ret = "(" + this.child.toString(options) + ")";
            }
            break;

          case LOOKAHEAD:
            ret = "(?=" + this.child.toString(options) + ")";
            break;
          case NEGATIVELOOKAHEAD:
            ret = "(?!" + this.child.toString(options) + ")";
            break;
          case LOOKBEHIND:
            ret = "(?<=" + this.child.toString(options) + ")";
            break;
          case NEGATIVELOOKBEHIND:
            ret = "(?<!" + this.child.toString(options) + ")";
            break;
          case INDEPENDENT:
            ret = "(?>" + this.child.toString(options) + ")";
            break;
        }
        return ret;
      }
    }

    /**
     * (?(condition)yes-pattern|no-pattern)
     */
    static class ConditionToken extends Token implements java.io.Serializable
    {
      private static final long serialVersionUID = 1L;

      int refNumber;

      Token condition;

      Token yes;

      Token no;

      ConditionToken(int refno, Token cond, Token yespat, Token nopat)
      {
        super(Token.CONDITION);
        this.refNumber = refno;
        this.condition = cond;
        this.yes = yespat;
        this.no = nopat;
      }

      @Override
      int size()
      {
        return this.no == null ? 1 : 2;
      }

      @Override
      Token getChild(int index)
      {
        if (index == 0)
          return this.yes;
        if (index == 1)
          return this.no;
        throw new RuntimeException("Internal Error: " + index);
      }

      @Override
      public String toString(int options)
      {
        String ret;
        if (refNumber > 0)
        {
          ret = "(?(" + refNumber + ")";
        }
        else if (this.condition.type == Token.ANCHOR)
        {
          ret = "(?(" + this.condition + ")";
        }
        else
        {
          ret = "(?" + this.condition;
        }

        if (this.no == null)
        {
          ret += this.yes + ")";
        }
        else
        {
          ret += this.yes + "|" + this.no + ")";
        }
        return ret;
      }
    }

    /**
     * (ims-ims: .... )
     */
    static class ModifierToken extends Token implements java.io.Serializable
    {
      private static final long serialVersionUID = 1L;

      Token child;

      int add;

      int mask;

      ModifierToken(Token tok, int add, int mask)
      {
        super(Token.MODIFIERGROUP);
        this.child = tok;
        this.add = add;
        this.mask = mask;
      }

      @Override
      int size()
      {
        return 1;
      }

      @Override
      Token getChild(int index)
      {
        return this.child;
      }

      int getOptions()
      {
        return this.add;
      }

      int getOptionsMask()
      {
        return this.mask;
      }

      @Override
      public String toString(int options)
      {
        return "(?" + (this.add == 0 ? "" : REUtil.createOptionString(this.add))
            + (this.mask == 0 ? "" : REUtil.createOptionString(this.mask)) + ":" + this.child.toString(options) + ")";
      }
    }

    /**
     * This class represents a node in parse tree.
     * for UNION or CONCAT.
     */
    static class UnionToken extends Token implements java.io.Serializable
    {
      private static final long serialVersionUID = 1L;

      Vector<Token> children;

      UnionToken(int type)
      {
        super(type);
      }

      @Override
      void addChild(Token tok)
      {
        if (tok == null)
          return;
        if (this.children == null)
          this.children = new Vector<Token>();
        if (this.type == UNION)
        {
          this.children.addElement(tok);
          return;
        }
        // This is CONCAT, and new child is CONCAT.
        if (tok.type == CONCAT)
        {
          for (int i = 0; i < tok.size(); i++)
            this.addChild(tok.getChild(i)); // Recursion
          return;
        }
        int size = this.children.size();
        if (size == 0)
        {
          this.children.addElement(tok);
          return;
        }
        Token previous = this.children.elementAt(size - 1);
        if (!((previous.type == CHAR || previous.type == STRING) && (tok.type == CHAR || tok.type == STRING)))
        {
          this.children.addElement(tok);
          return;
        }

        //System.err.println("Merge '"+previous+"' and '"+tok+"'.");

        StringBuffer buffer;
        int nextMaxLength = (tok.type == CHAR ? 2 : tok.getString().length());
        if (previous.type == CHAR)
        { // Replace previous token by STRING
          buffer = new StringBuffer(2 + nextMaxLength);
          int ch = previous.getChar();
          if (ch >= 0x10000)
            buffer.append(REUtil.decomposeToSurrogates(ch));
          else
            buffer.append((char)ch);
          previous = Token.createString(null);
          this.children.setElementAt(previous, size - 1);
        }
        else
        { // STRING
          buffer = new StringBuffer(previous.getString().length() + nextMaxLength);
          buffer.append(previous.getString());
        }

        if (tok.type == CHAR)
        {
          int ch = tok.getChar();
          if (ch >= 0x10000)
            buffer.append(REUtil.decomposeToSurrogates(ch));
          else
            buffer.append((char)ch);
        }
        else
        {
          buffer.append(tok.getString());
        }

        ((StringToken)previous).string = new String(buffer);
      }

      @Override
      int size()
      {
        return this.children == null ? 0 : this.children.size();
      }

      @Override
      Token getChild(int index)
      {
        return this.children.elementAt(index);
      }

      @Override
      public String toString(int options)
      {
        String ret;
        if (this.type == CONCAT)
        {
          if (this.children.size() == 2)
          {
            Token ch = this.getChild(0);
            Token ch2 = this.getChild(1);
            if (ch2.type == CLOSURE && ch2.getChild(0) == ch)
            {
              ret = ch.toString(options) + "+";
            }
            else if (ch2.type == NONGREEDYCLOSURE && ch2.getChild(0) == ch)
            {
              ret = ch.toString(options) + "+?";
            }
            else
              ret = ch.toString(options) + ch2.toString(options);
          }
          else
          {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < this.children.size(); i++)
            {
              sb.append(this.children.elementAt(i).toString(options));
            }
            ret = new String(sb);
          }
          return ret;
        }
        if (this.children.size() == 2 && this.getChild(1).type == EMPTY)
        {
          ret = this.getChild(0).toString(options) + "?";
        }
        else if (this.children.size() == 2 && this.getChild(0).type == EMPTY)
        {
          ret = this.getChild(1).toString(options) + "??";
        }
        else
        {
          StringBuffer sb = new StringBuffer();
          sb.append(this.children.elementAt(0).toString(options));
          for (int i = 1; i < this.children.size(); i++)
          {
            sb.append('|');
            sb.append(this.children.elementAt(i).toString(options));
          }
          ret = new String(sb);
        }
        return ret;
      }
    }
  }

  /**
   * A regular expression parser for the XML Shema.
   *
   * @author TAMURA Kent &lt;kent@trl.ibm.co.jp&gt;
   */
  static class ParserForXMLSchema extends RegexParser
  {

    public ParserForXMLSchema()
    {
      //this.setLocale(Locale.getDefault());
    }

    @Override
    Token processCaret() throws ParseException
    {
      this.next();
      return Token.createChar('^');
    }

    @Override
    Token processDollar() throws ParseException
    {
      this.next();
      return Token.createChar('$');
    }

    @Override
    Token processLookahead() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processNegativelookahead() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processLookbehind() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processNegativelookbehind() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processBacksolidus_A() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processBacksolidus_Z() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processBacksolidus_z() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processBacksolidus_b() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processBacksolidus_B() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processBacksolidus_lt() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processBacksolidus_gt() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processStar(Token tok) throws ParseException
    {
      this.next();
      return Token.createClosure(tok);
    }

    @Override
    Token processPlus(Token tok) throws ParseException
    {
      // X+ -> XX*
      this.next();
      return Token.createConcat(tok, Token.createClosure(tok));
    }

    @Override
    Token processQuestion(Token tok) throws ParseException
    {
      // X? -> X|
      this.next();
      Token par = Token.createUnion();
      par.addChild(tok);
      par.addChild(Token.createEmpty());
      return par;
    }

    @Override
    boolean checkQuestion(int off)
    {
      return false;
    }

    @Override
    Token processParen() throws ParseException
    {
      this.next();
      Token tok = Token.createParen(this.parseRegex(), 0);
      if (this.read() != T_RPAREN)
        throw ex("parser.factor.1", this.offset - 1);
      this.next(); // Skips ')'
      return tok;
    }

    @Override
    Token processParen2() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processCondition() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processModifiers() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processIndependent() throws ParseException
    {
      throw ex("parser.process.1", this.offset);
    }

    @Override
    Token processBacksolidus_c() throws ParseException
    {
      this.next();
      return this.getTokenForShorthand('c');
    }

    @Override
    Token processBacksolidus_C() throws ParseException
    {
      this.next();
      return this.getTokenForShorthand('C');
    }

    @Override
    Token processBacksolidus_i() throws ParseException
    {
      this.next();
      return this.getTokenForShorthand('i');
    }

    @Override
    Token processBacksolidus_I() throws ParseException
    {
      this.next();
      return this.getTokenForShorthand('I');
    }

    @Override
    Token processBacksolidus_g() throws ParseException
    {
      throw this.ex("parser.process.1", this.offset - 2);
    }

    @Override
    Token processBacksolidus_X() throws ParseException
    {
      throw ex("parser.process.1", this.offset - 2);
    }

    @Override
    Token processBackreference() throws ParseException
    {
      throw ex("parser.process.1", this.offset - 4);
    }

    @Override
    int processCIinCharacterClass(RangeToken tok, int c)
    {
      tok.mergeRanges(this.getTokenForShorthand(c));
      return -1;
    }

    /**
     * Parses a character-class-expression, not a character-class-escape.
     *
     * c-c-expression   ::= '[' c-group ']'
     * c-group          ::= positive-c-group | negative-c-group | c-c-subtraction
     * positive-c-group ::= (c-range | c-c-escape)+
     * negative-c-group ::= '^' positive-c-group
     * c-c-subtraction  ::= (positive-c-group | negative-c-group) subtraction
     * subtraction      ::= '-' c-c-expression
     * c-range          ::= single-range | from-to-range
     * single-range     ::= multi-c-escape | category-c-escape | block-c-escape | <any XML char>
     * cc-normal-c      ::= <any character except [, ], \>
     * from-to-range    ::= cc-normal-c '-' cc-normal-c
     *
     * @param useNrange Ignored.
     * @return This returns no NrageToken.
     */
    @Override
    protected RangeToken parseCharacterClass(boolean useNrange) throws ParseException
    {
      this.setContext(S_INBRACKETS);
      this.next(); // '['
      RangeToken base = null;
      RangeToken tok;
      if (this.read() == T_CHAR && this.chardata == '^')
      {
        this.next(); // '^'
        base = Token.createRange();
        base.addRange(0, Token.UTF16_MAX);
        tok = Token.createRange();
      }
      else
      {
        tok = Token.createRange();
      }
      int type;
      boolean firstloop = true;
      while ((type = this.read()) != T_EOF)
      { // Don't use 'cotinue' for this loop.
        // single-range | from-to-range | subtraction
        if (type == T_CHAR && this.chardata == ']' && !firstloop)
        {
          if (base != null)
          {
            base.subtractRanges(tok);
            tok = base;
          }
          break;
        }
        int c = this.chardata;
        boolean end = false;
        if (type == T_BACKSOLIDUS)
        {
          switch (c)
          {
            case 'd':
            case 'D':
            case 'w':
            case 'W':
            case 's':
            case 'S':
              tok.mergeRanges(this.getTokenForShorthand(c));
              end = true;
              break;

            case 'i':
            case 'I':
            case 'c':
            case 'C':
              c = this.processCIinCharacterClass(tok, c);
              if (c < 0)
                end = true;
              break;

            case 'p':
            case 'P':
              int pstart = this.offset;
              RangeToken tok2 = this.processBacksolidus_pP(c);
              if (tok2 == null)
                throw this.ex("parser.atom.5", pstart);
              tok.mergeRanges(tok2);
              end = true;
              break;

            default:
              c = this.decodeEscaped();
          } // \ + c
        } // backsolidus
        else if (type == T_XMLSCHEMA_CC_SUBTRACTION && !firstloop)
        {
          // Subraction
          if (base != null)
          {
            base.subtractRanges(tok);
            tok = base;
          }
          RangeToken range2 = this.parseCharacterClass(false);
          tok.subtractRanges(range2);
          if (this.read() != T_CHAR || this.chardata != ']')
            throw this.ex("parser.cc.5", this.offset);
          break; // Exit this loop
        }
        this.next();
        if (!end)
        { // if not shorthands...
          if (type == T_CHAR)
          {
            if (c == '[')
              throw this.ex("parser.cc.6", this.offset - 2);
            if (c == ']')
              throw this.ex("parser.cc.7", this.offset - 2);
            if (c == '-' && !firstloop && chardata != ']')
              throw this.ex("parser.cc.8", this.offset - 2);
          }
          if (this.read() != T_CHAR || this.chardata != '-' || c == '-' && firstloop)
          { // Here is no '-'.
            tok.addRange(c, c);
          }
          else
          { // Found '-'
            // Is this '-' is a from-to token??
            this.next(); // Skips '-'
            if ((type = this.read()) == T_EOF)
              throw this.ex("parser.cc.2", this.offset);
            // c '-' ']' -> '-' is a single-range.
            if (type == T_CHAR && this.chardata == ']') 
            { // if - is at the last position of the group
              tok.addRange(c, c);
              tok.addRange('-', '-');
            }
            else if ((type == T_CHAR && this.chardata == ']') || type == T_XMLSCHEMA_CC_SUBTRACTION)
            {
              throw this.ex("parser.cc.8", this.offset - 1);
            }
            else
            {
              int rangeend = this.chardata;
              if (type == T_CHAR)
              {
                if (rangeend == '[')
                  throw this.ex("parser.cc.6", this.offset - 1);
                if (rangeend == ']')
                  throw this.ex("parser.cc.7", this.offset - 1);
                if (rangeend == '-')
                  throw this.ex("parser.cc.8", this.offset - 1);
              }
              else if (type == T_BACKSOLIDUS)
                rangeend = this.decodeEscaped();
              this.next();

              if (c > rangeend)
                throw this.ex("parser.ope.3", this.offset - 1);
              tok.addRange(c, rangeend);
            }
          }
        }
        firstloop = false;
      }
      if (this.read() == T_EOF)
        throw this.ex("parser.cc.2", this.offset);
      tok.sortRanges();
      tok.compactRanges();
      //tok.dumpRanges();
      this.setContext(S_NORMAL);
      this.next(); // Skips ']'

      return tok;
    }

    @Override
    protected RangeToken parseSetOperations() throws ParseException
    {
      throw this.ex("parser.process.1", this.offset);
    }

    @Override
    Token getTokenForShorthand(int ch)
    {
      switch (ch)
      {
        case 'd':
          return ParserForXMLSchema.getRange("xml:isDigit", true);
        case 'D':
          return ParserForXMLSchema.getRange("xml:isDigit", false);
        case 'w':
          return ParserForXMLSchema.getRange("xml:isWord", true);
        case 'W':
          return ParserForXMLSchema.getRange("xml:isWord", false);
        case 's':
          return ParserForXMLSchema.getRange("xml:isSpace", true);
        case 'S':
          return ParserForXMLSchema.getRange("xml:isSpace", false);
        case 'c':
          return ParserForXMLSchema.getRange("xml:isNameChar", true);
        case 'C':
          return ParserForXMLSchema.getRange("xml:isNameChar", false);
        case 'i':
          return ParserForXMLSchema.getRange("xml:isInitialNameChar", true);
        case 'I':
          return ParserForXMLSchema.getRange("xml:isInitialNameChar", false);
        default:
          throw new RuntimeException("Internal Error: shorthands: \\u" + Integer.toString(ch, 16));
      }
    }

    @Override
    int decodeEscaped() throws ParseException
    {
      if (this.read() != T_BACKSOLIDUS)
        throw ex("parser.next.1", this.offset - 1);
      int c = this.chardata;
      switch (c)
      {
        case 'n':
          c = '\n';
          break; // LINE FEED U+000A
        case 'r':
          c = '\r';
          break; // CRRIAGE RETURN U+000D
        case 't':
          c = '\t';
          break; // HORIZONTAL TABULATION U+0009
        case '\\':
        case '|':
        case '.':
        case '^':
        case '-':
        case '?':
        case '*':
        case '+':
        case '{':
        case '}':
        case '(':
        case ')':
        case '[':
        case ']':
          break; // return actucal char
        default:
          throw ex("parser.process.1", this.offset - 2);
      }
      return c;
    }

    static private HashMap<String, Token> ranges = null;

    static private HashMap<String, Token> ranges2 = null;

    static synchronized protected RangeToken getRange(String name, boolean positive)
    {
      if (ranges == null)
      {
        ranges = new HashMap<String, Token>();
        ranges2 = new HashMap<String, Token>();

        Token tok = Token.createRange();
        setupRange(tok, SPACES);
        ranges.put("xml:isSpace", tok);
        ranges2.put("xml:isSpace", Token.complementRanges(tok));

        tok = Token.createRange();
        setupRange(tok, DIGITS);
        ranges.put("xml:isDigit", tok);
        ranges2.put("xml:isDigit", Token.complementRanges(tok));

        tok = Token.createRange();
        setupRange(tok, DIGITS);
        ranges.put("xml:isDigit", tok);
        ranges2.put("xml:isDigit", Token.complementRanges(tok));

        tok = Token.createRange();
        setupRange(tok, LETTERS);
        tok.mergeRanges(ranges.get("xml:isDigit"));
        ranges.put("xml:isWord", tok);
        ranges2.put("xml:isWord", Token.complementRanges(tok));

        tok = Token.createRange();
        setupRange(tok, NAMECHARS);
        ranges.put("xml:isNameChar", tok);
        ranges2.put("xml:isNameChar", Token.complementRanges(tok));

        tok = Token.createRange();
        setupRange(tok, LETTERS);
        tok.addRange('_', '_');
        tok.addRange(':', ':');
        ranges.put("xml:isInitialNameChar", tok);
        ranges2.put("xml:isInitialNameChar", Token.complementRanges(tok));
      }
      RangeToken tok = positive ? (RangeToken)ranges.get(name) : (RangeToken)ranges2.get(name);
      return tok;
    }

    static void setupRange(Token range, String src)
    {
      int len = src.length();
      for (int i = 0; i < len; i += 2)
        range.addRange(src.charAt(i), src.charAt(i + 1));
    }

    private static final String SPACES = "\t\n\r\r  ";

    private static final String NAMECHARS = "\u002d\u002e\u0030\u003a\u0041\u005a\u005f\u005f\u0061\u007a\u00b7\u00b7\u00c0\u00d6"
        + "\u00d8\u00f6\u00f8\u0131\u0134\u013e\u0141\u0148\u014a\u017e\u0180\u01c3\u01cd\u01f0"
        + "\u01f4\u01f5\u01fa\u0217\u0250\u02a8\u02bb\u02c1\u02d0\u02d1\u0300\u0345\u0360\u0361"
        + "\u0386\u038a\u038c\u038c\u038e\u03a1\u03a3\u03ce\u03d0\u03d6\u03da\u03da\u03dc\u03dc"
        + "\u03de\u03de\u03e0\u03e0\u03e2\u03f3\u0401\u040c\u040e\u044f\u0451\u045c\u045e\u0481"
        + "\u0483\u0486\u0490\u04c4\u04c7\u04c8\u04cb\u04cc\u04d0\u04eb\u04ee\u04f5\u04f8\u04f9"
        + "\u0531\u0556\u0559\u0559\u0561\u0586\u0591\u05a1\u05a3\u05b9\u05bb\u05bd\u05bf\u05bf"
        + "\u05c1\u05c2\u05c4\u05c4\u05d0\u05ea\u05f0\u05f2\u0621\u063a\u0640\u0652\u0660\u0669"
        + "\u0670\u06b7\u06ba\u06be\u06c0\u06ce\u06d0\u06d3\u06d5\u06e8\u06ea\u06ed\u06f0\u06f9"
        + "\u0901\u0903\u0905\u0939\u093c\u094d\u0951\u0954\u0958\u0963\u0966\u096f\u0981\u0983"
        + "\u0985\u098c\u098f\u0990\u0993\u09a8\u09aa\u09b0\u09b2\u09b2\u09b6\u09b9\u09bc\u09bc"
        + "\u09be\u09c4\u09c7\u09c8\u09cb\u09cd\u09d7\u09d7\u09dc\u09dd\u09df\u09e3\u09e6\u09f1"
        + "\u0a02\u0a02\u0a05\u0a0a\u0a0f\u0a10\u0a13\u0a28\u0a2a\u0a30\u0a32\u0a33\u0a35\u0a36"
        + "\u0a38\u0a39\u0a3c\u0a3c\u0a3e\u0a42\u0a47\u0a48\u0a4b\u0a4d\u0a59\u0a5c\u0a5e\u0a5e"
        + "\u0a66\u0a74\u0a81\u0a83\u0a85\u0a8b\u0a8d\u0a8d\u0a8f\u0a91\u0a93\u0aa8\u0aaa\u0ab0"
        + "\u0ab2\u0ab3\u0ab5\u0ab9\u0abc\u0ac5\u0ac7\u0ac9\u0acb\u0acd\u0ae0\u0ae0\u0ae6\u0aef"
        + "\u0b01\u0b03\u0b05\u0b0c\u0b0f\u0b10\u0b13\u0b28\u0b2a\u0b30\u0b32\u0b33\u0b36\u0b39"
        + "\u0b3c\u0b43\u0b47\u0b48\u0b4b\u0b4d\u0b56\u0b57\u0b5c\u0b5d\u0b5f\u0b61\u0b66\u0b6f"
        + "\u0b82\u0b83\u0b85\u0b8a\u0b8e\u0b90\u0b92\u0b95\u0b99\u0b9a\u0b9c\u0b9c\u0b9e\u0b9f"
        + "\u0ba3\u0ba4\u0ba8\u0baa\u0bae\u0bb5\u0bb7\u0bb9\u0bbe\u0bc2\u0bc6\u0bc8\u0bca\u0bcd"
        + "\u0bd7\u0bd7\u0be7\u0bef\u0c01\u0c03\u0c05\u0c0c\u0c0e\u0c10\u0c12\u0c28\u0c2a\u0c33"
        + "\u0c35\u0c39\u0c3e\u0c44\u0c46\u0c48\u0c4a\u0c4d\u0c55\u0c56\u0c60\u0c61\u0c66\u0c6f"
        + "\u0c82\u0c83\u0c85\u0c8c\u0c8e\u0c90\u0c92\u0ca8\u0caa\u0cb3\u0cb5\u0cb9\u0cbe\u0cc4"
        + "\u0cc6\u0cc8\u0cca\u0ccd\u0cd5\u0cd6\u0cde\u0cde\u0ce0\u0ce1\u0ce6\u0cef\u0d02\u0d03"
        + "\u0d05\u0d0c\u0d0e\u0d10\u0d12\u0d28\u0d2a\u0d39\u0d3e\u0d43\u0d46\u0d48\u0d4a\u0d4d"
        + "\u0d57\u0d57\u0d60\u0d61\u0d66\u0d6f\u0e01\u0e2e\u0e30\u0e3a\u0e40\u0e4e\u0e50\u0e59"
        + "\u0e81\u0e82\u0e84\u0e84\u0e87\u0e88\u0e8a\u0e8a\u0e8d\u0e8d\u0e94\u0e97\u0e99\u0e9f"
        + "\u0ea1\u0ea3\u0ea5\u0ea5\u0ea7\u0ea7\u0eaa\u0eab\u0ead\u0eae\u0eb0\u0eb9\u0ebb\u0ebd"
        + "\u0ec0\u0ec4\u0ec6\u0ec6\u0ec8\u0ecd\u0ed0\u0ed9\u0f18\u0f19\u0f20\u0f29\u0f35\u0f35"
        + "\u0f37\u0f37\u0f39\u0f39\u0f3e\u0f47\u0f49\u0f69\u0f71\u0f84\u0f86\u0f8b\u0f90\u0f95"
        + "\u0f97\u0f97\u0f99\u0fad\u0fb1\u0fb7\u0fb9\u0fb9\u10a0\u10c5\u10d0\u10f6\u1100\u1100"
        + "\u1102\u1103\u1105\u1107\u1109\u1109\u110b\u110c\u110e\u1112\u113c\u113c\u113e\u113e"
        + "\u1140\u1140\u114c\u114c\u114e\u114e\u1150\u1150\u1154\u1155\u1159\u1159\u115f\u1161"
        + "\u1163\u1163\u1165\u1165\u1167\u1167\u1169\u1169\u116d\u116e\u1172\u1173\u1175\u1175"
        + "\u119e\u119e\u11a8\u11a8\u11ab\u11ab\u11ae\u11af\u11b7\u11b8\u11ba\u11ba\u11bc\u11c2"
        + "\u11eb\u11eb\u11f0\u11f0\u11f9\u11f9\u1e00\u1e9b\u1ea0\u1ef9\u1f00\u1f15\u1f18\u1f1d"
        + "\u1f20\u1f45\u1f48\u1f4d\u1f50\u1f57\u1f59\u1f59\u1f5b\u1f5b\u1f5d\u1f5d\u1f5f\u1f7d"
        + "\u1f80\u1fb4\u1fb6\u1fbc\u1fbe\u1fbe\u1fc2\u1fc4\u1fc6\u1fcc\u1fd0\u1fd3\u1fd6\u1fdb"
        + "\u1fe0\u1fec\u1ff2\u1ff4\u1ff6\u1ffc\u20d0\u20dc\u20e1\u20e1\u2126\u2126\u212a\u212b"
        + "\u212e\u212e\u2180\u2182\u3005\u3005\u3007\u3007\u3021\u302f\u3031\u3035\u3041\u3094"
        + "\u3099\u309a\u309d\u309e\u30a1\u30fa\u30fc\u30fe\u3105\u312c\u4e00\u9fa5\uac00\ud7a3" + "";

    private static final String LETTERS = "\u0041\u005a\u0061\u007a\u00c0\u00d6\u00d8\u00f6\u00f8\u0131\u0134\u013e\u0141\u0148"
        + "\u014a\u017e\u0180\u01c3\u01cd\u01f0\u01f4\u01f5\u01fa\u0217\u0250\u02a8\u02bb\u02c1"
        + "\u0386\u0386\u0388\u038a\u038c\u038c\u038e\u03a1\u03a3\u03ce\u03d0\u03d6\u03da\u03da"
        + "\u03dc\u03dc\u03de\u03de\u03e0\u03e0\u03e2\u03f3\u0401\u040c\u040e\u044f\u0451\u045c"
        + "\u045e\u0481\u0490\u04c4\u04c7\u04c8\u04cb\u04cc\u04d0\u04eb\u04ee\u04f5\u04f8\u04f9"
        + "\u0531\u0556\u0559\u0559\u0561\u0586\u05d0\u05ea\u05f0\u05f2\u0621\u063a\u0641\u064a"
        + "\u0671\u06b7\u06ba\u06be\u06c0\u06ce\u06d0\u06d3\u06d5\u06d5\u06e5\u06e6\u0905\u0939"
        + "\u093d\u093d\u0958\u0961\u0985\u098c\u098f\u0990\u0993\u09a8\u09aa\u09b0\u09b2\u09b2"
        + "\u09b6\u09b9\u09dc\u09dd\u09df\u09e1\u09f0\u09f1\u0a05\u0a0a\u0a0f\u0a10\u0a13\u0a28"
        + "\u0a2a\u0a30\u0a32\u0a33\u0a35\u0a36\u0a38\u0a39\u0a59\u0a5c\u0a5e\u0a5e\u0a72\u0a74"
        + "\u0a85\u0a8b\u0a8d\u0a8d\u0a8f\u0a91\u0a93\u0aa8\u0aaa\u0ab0\u0ab2\u0ab3\u0ab5\u0ab9"
        + "\u0abd\u0abd\u0ae0\u0ae0\u0b05\u0b0c\u0b0f\u0b10\u0b13\u0b28\u0b2a\u0b30\u0b32\u0b33"
        + "\u0b36\u0b39\u0b3d\u0b3d\u0b5c\u0b5d\u0b5f\u0b61\u0b85\u0b8a\u0b8e\u0b90\u0b92\u0b95"
        + "\u0b99\u0b9a\u0b9c\u0b9c\u0b9e\u0b9f\u0ba3\u0ba4\u0ba8\u0baa\u0bae\u0bb5\u0bb7\u0bb9"
        + "\u0c05\u0c0c\u0c0e\u0c10\u0c12\u0c28\u0c2a\u0c33\u0c35\u0c39\u0c60\u0c61\u0c85\u0c8c"
        + "\u0c8e\u0c90\u0c92\u0ca8\u0caa\u0cb3\u0cb5\u0cb9\u0cde\u0cde\u0ce0\u0ce1\u0d05\u0d0c"
        + "\u0d0e\u0d10\u0d12\u0d28\u0d2a\u0d39\u0d60\u0d61\u0e01\u0e2e\u0e30\u0e30\u0e32\u0e33"
        + "\u0e40\u0e45\u0e81\u0e82\u0e84\u0e84\u0e87\u0e88\u0e8a\u0e8a\u0e8d\u0e8d\u0e94\u0e97"
        + "\u0e99\u0e9f\u0ea1\u0ea3\u0ea5\u0ea5\u0ea7\u0ea7\u0eaa\u0eab\u0ead\u0eae\u0eb0\u0eb0"
        + "\u0eb2\u0eb3\u0ebd\u0ebd\u0ec0\u0ec4\u0f40\u0f47\u0f49\u0f69\u10a0\u10c5\u10d0\u10f6"
        + "\u1100\u1100\u1102\u1103\u1105\u1107\u1109\u1109\u110b\u110c\u110e\u1112\u113c\u113c"
        + "\u113e\u113e\u1140\u1140\u114c\u114c\u114e\u114e\u1150\u1150\u1154\u1155\u1159\u1159"
        + "\u115f\u1161\u1163\u1163\u1165\u1165\u1167\u1167\u1169\u1169\u116d\u116e\u1172\u1173"
        + "\u1175\u1175\u119e\u119e\u11a8\u11a8\u11ab\u11ab\u11ae\u11af\u11b7\u11b8\u11ba\u11ba"
        + "\u11bc\u11c2\u11eb\u11eb\u11f0\u11f0\u11f9\u11f9\u1e00\u1e9b\u1ea0\u1ef9\u1f00\u1f15"
        + "\u1f18\u1f1d\u1f20\u1f45\u1f48\u1f4d\u1f50\u1f57\u1f59\u1f59\u1f5b\u1f5b\u1f5d\u1f5d"
        + "\u1f5f\u1f7d\u1f80\u1fb4\u1fb6\u1fbc\u1fbe\u1fbe\u1fc2\u1fc4\u1fc6\u1fcc\u1fd0\u1fd3"
        + "\u1fd6\u1fdb\u1fe0\u1fec\u1ff2\u1ff4\u1ff6\u1ffc\u2126\u2126\u212a\u212b\u212e\u212e"
        + "\u2180\u2182\u3007\u3007\u3021\u3029\u3041\u3094\u30a1\u30fa\u3105\u312c\u4e00\u9fa5" + "\uac00\ud7a3";

    private static final String DIGITS = "\u0030\u0039\u0660\u0669\u06F0\u06F9\u0966\u096F\u09E6\u09EF\u0A66\u0A6F\u0AE6\u0AEF"
        + "\u0B66\u0B6F\u0BE7\u0BEF\u0C66\u0C6F\u0CE6\u0CEF\u0D66\u0D6F\u0E50\u0E59\u0ED0\u0ED9" + "\u0F20\u0F29";
  }

}
