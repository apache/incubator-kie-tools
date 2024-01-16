/*
 * Copyright (c) 2004, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation.adapters;

/**
 * {@link XmlAdapter} to handle {@code xs:normalizedString}.
 *
 * <p>Replaces any tab, CR, and LF by a whitespace character ' ', as specified in <a
 * href="http://www.w3.org/TR/xmlschema-2/#rf-whiteSpace">the whitespace facet 'replace'</a>
 *
 * @author Kohsuke Kawaguchi, Martin Grebac
 * @since 1.6, JAXB 2.0
 */
public final class NormalizedStringAdapter extends XmlAdapter<String, String> {

  public NormalizedStringAdapter() {}

  /**
   * Replace any tab, CR, and LF by a whitespace character ' ', as specified in <a
   * href="http://www.w3.org/TR/xmlschema-2/#rf-whiteSpace">the whitespace facet 'replace'</a>
   */
  @Override
  public String unmarshal(String text) {
    if (text == null) return null; // be defensive

    int i = text.length() - 1;

    // look for the first whitespace char.
    while (i >= 0 && !isWhiteSpaceExceptSpace(text.charAt(i))) i--;

    if (i < 0)
      // no such whitespace. replace(text)==text.
      return text;

    // we now know that we need to modify the text.
    // allocate a char array to do it.
    char[] buf = text.toCharArray();

    buf[i--] = ' ';
    for (; i >= 0; i--) if (isWhiteSpaceExceptSpace(buf[i])) buf[i] = ' ';

    return new String(buf);
  }

  /**
   * No-op.
   *
   * <p>Just return the same string given as the parameter.
   */
  @Override
  public String marshal(String s) {
    return s;
  }

  /** Returns true if the specified char is a white space character but not 0x20. */
  protected static boolean isWhiteSpaceExceptSpace(char ch) {
    // most of the characters are non-control characters.
    // so check that first to quickly return false for most of the cases.
    if (ch >= 0x20) return false;

    // other than we have to do four comparisons.
    return ch == 0x9 || ch == 0xA || ch == 0xD;
  }
}
