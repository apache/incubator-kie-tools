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

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class FormatCLI {
  public static void main(String[] args) throws Exception {
    System.exit(run(args, System.out, System.err));
  }

  static int run(String[] args, PrintStream out, PrintStream err) throws Exception {
    for (String arg : args) {
      if ("--help".equals(arg) || "-h".equals(arg)) {
        printUsage(out);
        return 0;
      }
    }

    if (args.length < 1) {
      printUsage(err);
      return 1;
    }

    boolean write = false;
    boolean check = false;
    boolean stdin = false;
    boolean writeDir = false;
    int firstFile = 0;
    List<String> lineSpecs = new ArrayList<>();
    FormatterOptions options = FormatterOptions.DEFAULTS;

    for (int i = 0; i < args.length; i++) {
      if ("--write".equals(args[i])) {
        write = true;
        firstFile = i + 1;
      } else if ("--write-dir".equals(args[i])) {
        writeDir = true;
        write = true;
        firstFile = i + 1;
        if (i + 2 < args.length) {
          err.println("--write-dir takes only the directory and must be the last argument");
          printUsage(err);
          return 1;
        }
        break;
      } else if ("--check".equals(args[i])) {
        check = true;
        firstFile = i + 1;
      } else if ("--lines".equals(args[i])) {
        if (i + 1 >= args.length) {
          err.println("--lines requires a value, e.g. --lines 12:20");
          printUsage(err);
          return 1;
        }
        lineSpecs.add(args[i + 1]);
        i++;
        firstFile = i + 1;
      } else if ("--stdin".equals(args[i])) {
        stdin = true;
        firstFile = i + 1;
        break; // anything after --stdin is the optional display label
      } else if ("--config".equals(args[i])) {
        if (i + 1 >= args.length) {
          err.println("--config requires a path to a JSON options file");
          printUsage(err);
          return 1;
        }
        Path configPath = Path.of(args[i + 1]);
        try {
          JsonElement parsed = JsonParser.parseString(Files.readString(configPath));
          if (!parsed.isJsonObject()) {
            err.println("--config " + configPath + " must contain a JSON object");
            return 1;
          }
          options = FormatterOptions.fromJson(parsed.getAsJsonObject());
        } catch (IOException | JsonParseException ex) {
          err.println("--config " + configPath + ": " + ex.getMessage());
          return 1;
        }
        i++;
        firstFile = i + 1;
      } else {
        break;
      }
    }

    if (write && check) {
      err.println("--write and --check cannot be combined");
      printUsage(err);
      return 1;
    }

    List<int[]> ranges = null;
    if (!lineSpecs.isEmpty()) {
      if (writeDir) {
        err.println("--lines cannot be used with --write-dir");
        return 1;
      }
      if (!check && !write) {
        err.println("--lines requires --check or --write");
        printUsage(err);
        return 1;
      }
      try {
        ranges = parseLineRanges(lineSpecs);
      } catch (IllegalArgumentException e) {
        err.println(e.getMessage());
        return 1;
      }
    }

    if (stdin) {
      if (write) {
        err.println("--write cannot be used with --stdin");
        return 1;
      }
      String label = (firstFile < args.length) ? args[firstFile] : "<stdin>";
      String text = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
      DRLFormatter.FormatResult result = DRLFormatter.formatChecked(text, options);
      if (result.refused()) {
        err.println("Refusing to format (" + result.refusalReason() + "): " + label);
        return 2;
      }
      if (ranges != null) {
        // --write is rejected above, so a range run over stdin is always --check.
        List<DRLFormatter.RangeResult> unformatted =
            unformattedRanges(text, DRLFormatter.formatRanges(text, ranges, options));
        if (!unformatted.isEmpty()) {
          err.println("Would reformat: " + label + " (" + describeRanges(unformatted) + ")");
          return 1;
        }
        return 0;
      }
      String formatted = result.formatted();
      if (check) {
        if (!formatted.equals(text)) {
          err.println("Would reformat: " + label);
          return 1;
        }
      } else {
        out.print(formatted);
      }
      return 0;
    }

    if (writeDir) {
      // --check with --write-dir is intercepted by the write&&check guard above
      if (firstFile >= args.length) {
        err.println("Missing directory path for --write-dir");
        printUsage(err);
        return 1;
      }

      Path root = Path.of(args[firstFile]);
      if (!Files.isDirectory(root)) {
        err.println("Not a directory: " + root);
        return 1;
      }

      FormatterOptions dirOptions = options;
      AtomicBoolean parseFailures = new AtomicBoolean(false);
      AtomicBoolean ioFailures = new AtomicBoolean(false);
      try (Stream<Path> files = Files.walk(root)) {
        files
            .filter(Files::isRegularFile)
            .filter(FormatCLI::isDrlFile)
            .sorted()
            .forEach(
                path -> {
                  try {
                    String text = Files.readString(path);
                    DRLFormatter.FormatResult result = DRLFormatter.formatChecked(text, dirOptions);
                    if (result.refused()) {
                      err.println(
                          "Refusing to format (" + result.refusalReason() + "): " + path);
                      parseFailures.set(true);
                      return;
                    }
                    String formatted = result.formatted();
                    if (!formatted.equals(text)) {
                      Files.writeString(path, formatted);
                      err.println("Formatted: " + path);
                    }
                  } catch (MalformedInputException e) {
                    err.println("Cannot read (not valid UTF-8): " + path);
                    ioFailures.set(true);
                  } catch (Exception e) {
                    err.println("Failed formatting " + path + ": " + e.getMessage());
                    ioFailures.set(true);
                  }
                });
      }
      if (parseFailures.get()) {
        return 2;
      }
      return ioFailures.get() ? 1 : 0;
    }

    if (firstFile >= args.length) {
      printUsage(err);
      return 1;
    }

    if (ranges != null) {
      if (args.length - firstFile != 1) {
        err.println("--lines expects exactly one file (ranges are per-document)");
        return 1;
      }
      return runRange(Path.of(args[firstFile]), ranges, check, err, options);
    }

    boolean anyChanged = false;
    boolean parseFailures = false;
    boolean ioFailures = false;
    for (int i = firstFile; i < args.length; i++) {
      Path path = Path.of(args[i]);
      try {
        String text = Files.readString(path);
        DRLFormatter.FormatResult result = DRLFormatter.formatChecked(text, options);
        if (result.refused()) {
          err.println("Refusing to format (" + result.refusalReason() + "): " + path);
          parseFailures = true;
          continue;
        }
        String formatted = result.formatted();

        if (check) {
          // The same comparison --write makes, so the two can never disagree.
          if (!formatted.equals(text)) {
            err.println("Would reformat: " + path);
            anyChanged = true;
          }
        } else if (write) {
          if (!formatted.equals(text)) {
            Files.writeString(path, formatted);
            err.println("Formatted: " + path);
          }
        } else {
          out.print(formatted);
        }
      } catch (MalformedInputException e) {
        err.println("Cannot read (not valid UTF-8): " + path);
        ioFailures = true;
      } catch (Exception e) {
        err.println("Failed formatting " + path + ": " + e.getMessage());
        ioFailures = true;
      }
    }

    if (parseFailures) {
      return 2;
    }
    if ((check && anyChanged) || ioFailures) {
      return 1;
    }
    return 0;
  }

  /**
   * {@code --lines} over a single file: {@code --check} reports the touched
   * statements that are not canonically formatted, {@code --write} splices the
   * formatted text of exactly those statements back in. Both are gated by
   * {@code formatChecked} first, so a file that does not parse — or that the
   * formatter would corrupt — is refused with exit 2 just like whole-file mode.
   */
  private static int runRange(
      Path path, List<int[]> ranges, boolean check, PrintStream err, FormatterOptions options) {
    String text;
    try {
      text = Files.readString(path);
    } catch (MalformedInputException e) {
      err.println("Cannot read (not valid UTF-8): " + path);
      return 1;
    } catch (Exception e) {
      err.println("Failed formatting " + path + ": " + e.getMessage());
      return 1;
    }

    DRLFormatter.FormatResult gate = DRLFormatter.formatChecked(text, options);
    if (gate.refused()) {
      err.println("Refusing to format (" + gate.refusalReason() + "): " + path);
      return 2;
    }

    List<DRLFormatter.RangeResult> unformatted =
        unformattedRanges(text, DRLFormatter.formatRanges(text, ranges, options));
    if (unformatted.isEmpty()) {
      return 0;
    }

    if (check) {
      err.println("Would reformat: " + path + " (" + describeRanges(unformatted) + ")");
      return 1;
    }

    try {
      Files.writeString(path, applyRangeEdits(text, unformatted, options));
    } catch (Exception e) {
      err.println("Failed formatting " + path + ": " + e.getMessage());
      return 1;
    }
    err.println("Formatted: " + path + " (" + describeRanges(unformatted) + ")");
    return 0;
  }

  /**
   * Parses {@code --lines} values into 0-based inclusive ranges. Each value is a
   * comma-separated list of 1-based inclusive {@code start:end} spans (a bare
   * {@code n} means {@code n:n}), matching clang-format's {@code -lines} and
   * git's own line numbering.
   *
   * @throws IllegalArgumentException on a malformed or empty spec
   */
  static List<int[]> parseLineRanges(List<String> specs) {
    List<int[]> ranges = new ArrayList<>();
    for (String spec : specs) {
      for (String part : spec.split(",")) {
        String token = part.trim();
        if (token.isEmpty()) {
          continue;
        }
        int colon = token.indexOf(':');
        String startText = colon < 0 ? token : token.substring(0, colon);
        String endText = colon < 0 ? token : token.substring(colon + 1);
        int start;
        int end;
        try {
          start = Integer.parseInt(startText.trim());
          end = Integer.parseInt(endText.trim());
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(
              "Malformed --lines range '" + token + "' (expected start:end)");
        }
        if (start < 1 || end < start) {
          throw new IllegalArgumentException(
              "Malformed --lines range '" + token + "' (expected 1 <= start <= end)");
        }
        ranges.add(new int[]{start - 1, end - 1});
      }
    }
    if (ranges.isEmpty()) {
      throw new IllegalArgumentException("--lines was given no usable range");
    }
    return ranges;
  }

  /**
   * The subset of {@code edits} whose formatted text differs from the original
   * bytes of the lines it would replace, compared exactly: the engine emits
   * {@code options.lineEnding(text)}, so under PRESERVE a canonical range is
   * byte-identical, while under an explicit {@code lf}/{@code crlf} an ending
   * the file does not use is a change the user asked for.
   */
  static List<DRLFormatter.RangeResult> unformattedRanges(
      String text, List<DRLFormatter.RangeResult> edits) {
    String[] lines = rawLines(text);
    List<DRLFormatter.RangeResult> differing = new ArrayList<>();
    for (DRLFormatter.RangeResult edit : edits) {
      if (edit.text().isEmpty()) {
        continue; // formatRanges' "nothing to format here" signal
      }
      int end = Math.min(edit.endLine(), lines.length - 1);
      if (edit.startLine() > end) {
        continue;
      }
      String original = String.join("", Arrays.copyOfRange(lines, edit.startLine(), end + 1));
      if (!edit.text().equals(original)) {
        differing.add(edit);
      }
    }
    return differing;
  }

  /**
   * Splices each edit's formatted text over the lines it covers, bottom-up so
   * the earlier edits' line numbers stay valid. Slices go in as the engine
   * returned them (already carrying {@code options.lineEnding(text)}) and the
   * untouched lines are re-joined with that same ending: under PRESERVE that is
   * the file's own, so a two-rule fix stays a two-rule diff — what
   * {@code --lines} exists for — and under an explicit {@code lf}/{@code crlf}
   * the whole file gets the ending the user asked for.
   */
  static String applyRangeEdits(
      String text, List<DRLFormatter.RangeResult> edits, FormatterOptions options) {
    String eol = options.lineEnding(text);
    List<String> lines = new ArrayList<>(Arrays.asList(splitLines(text)));
    List<DRLFormatter.RangeResult> ordered = new ArrayList<>(edits);
    ordered.sort((a, b) -> Integer.compare(b.startLine(), a.startLine()));

    for (DRLFormatter.RangeResult edit : ordered) {
      if (edit.text().isEmpty()) {
        continue;
      }
      int end = Math.min(edit.endLine(), lines.size() - 1);
      if (edit.startLine() > end) {
        continue;
      }
      // edit.text() ends with one line ending by contract
      String[] pieces = edit.text().replace("\r\n", "\n").replace("\r", "\n").split("\n", -1);
      List<String> replacement = Arrays.asList(pieces).subList(0, pieces.length - 1);
      lines.subList(edit.startLine(), end + 1).clear();
      lines.addAll(edit.startLine(), replacement);
    }

    return String.join(eol, lines);
  }

  /** Renders the affected spans as 1-based inclusive {@code "lines 12-40, 88-96"}. */
  static String describeRanges(List<DRLFormatter.RangeResult> edits) {
    StringBuilder rendered = new StringBuilder("lines ");
    for (int i = 0; i < edits.size(); i++) {
      if (i > 0) {
        rendered.append(", ");
      }
      rendered.append(edits.get(i).startLine() + 1).append('-').append(edits.get(i).endLine() + 1);
    }
    return rendered.toString();
  }

  /**
   * Splits on LF after EOL normalization, with a trailing newline guaranteed, so
   * indices line up with the 0-based statement lines {@code formatRanges} reports.
   */
  private static String[] splitLines(String text) {
    String normalized = text.replace("\r\n", "\n").replace("\r", "\n");
    if (!normalized.endsWith("\n")) {
      normalized = normalized + "\n";
    }
    return normalized.split("\n", -1);
  }

  /**
   * The original lines with their own terminators, indexed like
   * {@link #splitLines} so a statement's reported line numbers address the same
   * lines. The terminators are what let a range be compared to the engine's
   * output exactly.
   */
  private static String[] rawLines(String text) {
    List<String> lines = new ArrayList<>();
    int start = 0;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      boolean endsLine = c == '\n'
          || (c == '\r' && (i + 1 >= text.length() || text.charAt(i + 1) != '\n'));
      if (endsLine) {
        lines.add(text.substring(start, i + 1));
        start = i + 1;
      }
    }
    lines.add(text.substring(start));
    return lines.toArray(new String[0]);
  }

  private static void printUsage(PrintStream out) {
    out.println("Usage: FormatCLI [--config F] [--check | --write] <file.drl> ...");
    out.println("       FormatCLI [--config F] --write-dir <directory>");
    out.println("       FormatCLI [--check] --stdin [<label>]");
    out.println("       FormatCLI (--check | --write) --lines <ranges> <file.drl>");
    out.println("       FormatCLI --check --lines <ranges> --stdin [<label>]");
    out.println("       FormatCLI --help");
    out.println("  --config F read formatting options from JSON file F (same keys as the drools.lsp.formatter settings)");
    out.println("  (default)  print formatted output to stdout");
    out.println("  --write    format files in-place");
    out.println("  --write-dir  recursively format all .drl files under <directory>");
    out.println("  --check    exit 1 if any file would change (for CI/hooks)");
    out.println("  --stdin    read DRL from stdin instead of files; optional <label> for messages");
    out.println("  --lines    limit --check/--write to the top-level statements overlapping");
    out.println("             <ranges>: comma-separated 1-based inclusive start:end spans");
    out.println("             (repeatable). Scope is widened to whole rules/declares, never");
    out.println("             narrowed to part of one. Requires --check or --write.");
    out.println("  --help, -h show this help and exit");
  }

  private static boolean isDrlFile(Path path) {
    String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
    return fileName.endsWith(".drl");
  }
}
