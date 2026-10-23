/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.formatter;

import java.util.Locale;
import java.util.logging.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Formatting options. This record's JSON shape is the one options contract: the
 * language server reads it from {@code initializationOptions.formatter} and from the
 * {@code drools.lsp.formatter} settings subtree; the CLI reads it from
 * {@code --config}. Defaults are the original house style, except line endings,
 * which are preserved rather than forced. Unknown keys are ignored and invalid
 * values fall back to the default, so a settings typo never disables formatting.
 */
public record FormatterOptions(int tabSize, boolean insertSpaces, int lineLength,
                               LineEndings lineEndings, boolean parenPadding,
                               boolean bindingColonSpace, boolean normalizeTerminators,
                               boolean alignDeclarations, HeaderMetadata headerMetadata) {

  private static final Logger logger = Logger.getLogger(FormatterOptions.class.getName());

  public enum LineEndings { PRESERVE, LF, CRLF }

  /** Where a rule/query/declare header's annotations and attributes go. */
  public enum HeaderMetadata { INDENTED, FLUSH, INLINE }

  public static final FormatterOptions DEFAULTS = new FormatterOptions(
      2, true, 110, LineEndings.PRESERVE, true, false, true, true, HeaderMetadata.INDENTED);

  public static FormatterOptions fromJson(JsonObject json) {
    if (json == null) {
      return DEFAULTS;
    }
    FormatterOptions d = DEFAULTS;
    return new FormatterOptions(
        intOr(json, "tabSize", d.tabSize, 1),
        boolOr(json, "insertSpaces", d.insertSpaces),
        intOr(json, "lineLength", d.lineLength, 40),
        enumOr(json, "lineEndings", LineEndings.class, d.lineEndings),
        boolOr(json, "parenPadding", d.parenPadding),
        boolOr(json, "bindingColonSpace", d.bindingColonSpace),
        boolOr(json, "normalizeTerminators", d.normalizeTerminators),
        boolOr(json, "alignDeclarations", d.alignDeclarations),
        enumOr(json, "headerMetadata", HeaderMetadata.class, d.headerMetadata));
  }

  public FormatterOptions withIndent(int tabSize, boolean insertSpaces) {
    return new FormatterOptions(tabSize < 1 ? this.tabSize : tabSize, insertSpaces, lineLength,
        lineEndings, parenPadding, bindingColonSpace, normalizeTerminators, alignDeclarations,
        headerMetadata);
  }

  /** One indentation level. */
  public String indentUnit() {
    return insertSpaces ? " ".repeat(tabSize) : "\t";
  }

  /** The line ending to write for {@code input}, honouring {@code PRESERVE}. */
  public String lineEnding(String input) {
    return switch (lineEndings) {
      case LF -> "\n";
      case CRLF -> "\r\n";
      case PRESERVE -> detectLineEnding(input);
    };
  }

  /** The input's first line break, or LF when it has none. */
  static String detectLineEnding(String text) {
    if (text == null) {
      return "\n";
    }
    int nl = text.indexOf('\n');
    return nl > 0 && text.charAt(nl - 1) == '\r' ? "\r\n" : "\n";
  }

  private static int intOr(JsonObject json, String key, int fallback, int min) {
    JsonElement v = json.get(key);
    if (v == null) {
      return fallback;
    }
    try {
      int n = v.getAsInt();
      if (n >= min) {
        return n;
      }
    } catch (RuntimeException ignored) {
      // not a number
    }
    logger.fine(() -> "Ignoring formatter option " + key + "=" + v + "; using " + fallback);
    return fallback;
  }

  private static boolean boolOr(JsonObject json, String key, boolean fallback) {
    JsonElement v = json.get(key);
    if (v != null && v.isJsonPrimitive() && v.getAsJsonPrimitive().isBoolean()) {
      return v.getAsBoolean();
    }
    if (v != null && v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
      String text = v.getAsString().trim();
      if (text.equalsIgnoreCase("true")) {
        return true;
      }
      if (text.equalsIgnoreCase("false")) {
        return false;
      }
    }
    if (v != null) {
      logger.fine(() -> "Ignoring formatter option " + key + "=" + v + "; using " + fallback);
    }
    return fallback;
  }

  private static <E extends Enum<E>> E enumOr(JsonObject json, String key, Class<E> type, E fallback) {
    JsonElement v = json.get(key);
    if (v != null && v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
      try {
        return Enum.valueOf(type, v.getAsString().trim().toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException ignored) {
        // unknown constant
      }
    }
    if (v != null) {
      logger.fine(() -> "Ignoring formatter option " + key + "=" + v + "; using " + fallback);
    }
    return fallback;
  }
}
