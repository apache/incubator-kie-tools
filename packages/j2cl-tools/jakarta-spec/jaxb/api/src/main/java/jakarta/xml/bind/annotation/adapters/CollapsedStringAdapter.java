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
 * Built-in {@link XmlAdapter} to handle {@code xs:token} and its derived types.
 *
 * <p>This adapter removes leading and trailing whitespaces, then truncate any sequence of tab, CR,
 * LF, and SP by a single whitespace character ' '.
 *
 * @author Kohsuke Kawaguchi
 * @since 1.6, JAXB 2.0
 */
public class CollapsedStringAdapter extends XmlAdapter<String, String> {

  public CollapsedStringAdapter() {}

  /**
   * Removes leading and trailing whitespaces of the string given as the parameter, then truncate
   * any sequence of tab, CR, LF, and SP by a single whitespace character ' '.
   */
  @Override
  public String unmarshal(String text) {
    if (text == null) return null; // be defensive

    int len = text.length();

    // most of the texts are already in the collapsed form.
    // so look for the first whitespace in the hope that we will
    // never see it.
    int s = 0;
    while (s < len) {
      if (isWhiteSpace(text.charAt(s))) break;
      s++;
    }
    if (s == len)
      // the input happens to be already collapsed.
      return text;

    // we now know that the input contains spaces.
    // let's sit down and do the collapsing normally.

    StringBuilder result = new StringBuilder(len /*allocate enough size to avoid re-allocation*/);

    if (s != 0) {
      for (int i = 0; i < s; i++) result.append(text.charAt(i));
      result.append(' ');
    }

    boolean inStripMode = true;
    for (int i = s + 1; i < len; i++) {
      char ch = text.charAt(i);
      boolean b = isWhiteSpace(ch);
      if (inStripMode && b) continue; // skip this character

      inStripMode = b;
      if (inStripMode) result.append(' ');
      else result.append(ch);
    }

    // remove trailing whitespaces
    len = result.length();
    if (len > 0 && result.charAt(len - 1) == ' ') result.setLength(len - 1);
    // whitespaces are already collapsed,
    // so all we have to do is to remove the last one character
    // if it's a whitespace.

    return result.toString();
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

  /** returns true if the specified char is a white space character. */
  protected static boolean isWhiteSpace(char ch) {
    // most of the characters are non-control characters.
    // so check that first to quickly return false for most of the cases.
    if (ch > 0x20) return false;

    // other than we have to do four comparisons.
    return ch == 0x9 || ch == 0xA || ch == 0xD || ch == 0x20;
  }
}
