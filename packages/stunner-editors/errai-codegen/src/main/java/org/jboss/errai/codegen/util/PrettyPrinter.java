/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.codegen.util;

import org.mvel2.util.ParseTools;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class PrettyPrinter {
  public static String prettyPrintJava(final String input) {
    final StringBuilder out = new StringBuilder(2048);
    StringBuilder lineBuffer = new StringBuilder(120);

    final char[] expr = input.toCharArray();

    int indentLevel = 0;
    final int statementIndent = 0;

    for (int i = 0; i < expr.length; i++) {
      switch (expr[i]) {
        case '{':
          writeToBuffer(out, lineBuffer.append("{ "), indentLevel++, statementIndent);
          lineBuffer = new StringBuilder(120);
          break;

        case '}':
          writeToBuffer(out, lineBuffer, --indentLevel, statementIndent);
          lineBuffer = new StringBuilder(120).append(" }");
          break;

        case '"':
        case '\'':
          final int start = i;
          i = ParseTools.balancedCapture(expr, i, expr[i]);
          lineBuffer.append(expr, start, i - start + 1);
          break;

        case '\n':
          writeToBuffer(out, lineBuffer, indentLevel, statementIndent);
          out.append('\n');
          lineBuffer = new StringBuilder(120);
          break;

        case ',':
          lineBuffer.append(", ");
          break;


        default:
          if (Character.isWhitespace(expr[i])) {
            lineBuffer.append(" ");
            i = skipWhitespace(expr, i) - 1;
          }
          else {
            lineBuffer.append(expr[i]);
          }
      }
    }

    if (lineBuffer.length() != 0) {
      writeToBuffer(out, lineBuffer, indentLevel, statementIndent);
    }

    return compactinate(out.toString());
  }

  private static void writeToBuffer(final StringBuilder out, final StringBuilder lineBuffer,
                                    final int indentLevel, final int statementIndent) {
    final String trimmedLineBuffer = lineBuffer.toString().trim();

    if (trimmedLineBuffer.isEmpty()) {
      return;
    }

    out.append(pad((indentLevel + statementIndent) * 2)).append(trimmedLineBuffer);
  }

  private static String compactinate(final String str) {
    final char[] expr = str.toCharArray();
    final StringBuilder buf = new StringBuilder(expr.length);
    boolean newLine = false;
    for (int i = 0; i < expr.length; i++) {
      switch (expr[i]) {
        case '"':
        case '\'':
          final int start = i;
          buf.append(expr, start, (i = ParseTools.balancedCapture(expr, i, expr[i])) - start + 1);
          break;
        case '\n':
          newLine = true;
          buf.append('\n');
          break;

        default:
          if (Character.isWhitespace(expr[i])) {
            buf.append(" ");
            if (!newLine) {
              i = skipWhitespace(expr, i) - 1;
            }
          }
          else {
            newLine = false;
            buf.append(expr[i]);
          }
      }
    }
    return buf.toString().trim();
  }

  private static String pad(final int amount) {
    if (amount <= 0) return "";

    final char[] pad = new char[amount];
    for (int i = 0; i < amount; i++) {
      pad[i] = ' ';
    }
    return new String(pad);
  }

  public static int skipWhitespace(final char[] expr, int cursor) {
    Skip:
    while (cursor != expr.length) {
      switch (expr[cursor]) {
        case '\r':
          cursor++;
          continue;

        default:
          if (!Character.isWhitespace(expr[cursor])) break Skip;

      }
      cursor++;
    }
    return cursor;

  }
}
