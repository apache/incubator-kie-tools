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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FormatCLITest {

  private record CliRun(int exit, String stdout, String stderr) {}

  private static int runCli(String... args) throws Exception {
    return runCliCaptured(args).exit();
  }

  private static CliRun runCliCaptured(String... args) throws Exception {
    ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
    ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(outBytes, true, StandardCharsets.UTF_8);
    PrintStream err = new PrintStream(errBytes, true, StandardCharsets.UTF_8);
    int exit = FormatCLI.run(args, out, err);
    return new CliRun(
        exit,
        outBytes.toString(StandardCharsets.UTF_8),
        errBytes.toString(StandardCharsets.UTF_8));
  }

  private static final String CANONICAL =
      DRLFormatter.format("package p;\nrule R\n  when\n    Person( age > 18 )\n  then\nend\n");

  /**
   * PRESERVE formats a CRLF file back to CRLF, so a canonical one is clean; an
   * explicitly configured ending the file does not use is a change.
   */
  @Test
  void checkFollowsTheLineEndingsPolicy(@TempDir Path dir) throws Exception {
    Path crlf = dir.resolve("crlf.drl");
    Files.writeString(crlf, CANONICAL.replace("\n", "\r\n"));
    assertThat(runCli("--check", crlf.toString())).isZero();

    Path config = dir.resolve("lf.json");
    Files.writeString(config, "{\"lineEndings\":\"lf\"}");
    assertThat(runCli("--config", config.toString(), "--check", crlf.toString())).isEqualTo(1);
  }

  @Test
  void checkAndWriteAgreeOnACanonicalFilesFinalNewline(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("canonical.drl");
    Files.writeString(f, CANONICAL);

    assertThat(CANONICAL).endsWith("\n");
    assertThat(runCli("--check", f.toString())).isZero();
    assertThat(runCli("--write", f.toString())).isZero();
    assertThat(Files.readString(f)).isEqualTo(CANONICAL);
  }

  @Test
  void checkFlagsAMissingFinalNewlineAndWriteAppendsIt(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("no_final_newline.drl");
    Files.writeString(f, CANONICAL.substring(0, CANONICAL.length() - 1));

    assertThat(runCli("--check", f.toString())).isEqualTo(1);
    assertThat(runCli("--write", f.toString())).isZero();
    assertThat(Files.readString(f)).isEqualTo(CANONICAL);
  }

  @Test
  void checkDetectsContentDifference(@TempDir Path dir) throws Exception {
    // A genuine indentation/content difference is still flagged.
    Path f = dir.resolve("messy.drl");
    Files.writeString(f, "package p;\nrule R\nwhen\n$p : Person(  age>18 )\nthen\nend\n");
    assertThat(runCli("--check", f.toString())).isEqualTo(1);
  }

  @Test
  void checkPassesWhenOnlyDifferenceIsInsideAFrozenRegion(@TempDir Path dir) throws Exception {
    // A file whose non-frozen parts are canonically formatted and whose frozen
    // region is deliberately "unformatted". Once formatted it is a fixed point,
    // so --check must not flag it — the git-hook payoff.
    String input = String.join("\n",
        "rule \"A\"",
        "when",
        "$p : Person()",
        "then",
        "end",
        "// @formatter:off",
        "rule \"B\"",
        "    when",
        "  $p:Person(  age>18 )",
        "    then",
        "end",
        "// @formatter:on");

    String canonical = DRLFormatter.format(input);
    assertThat(DRLFormatter.format(canonical)).isEqualTo(canonical);

    Path f = dir.resolve("frozen.drl");
    Files.writeString(f, canonical);
    assertThat(runCli("--check", f.toString())).isZero();
  }

  @Test
  void writeRefusesOnParseErrors(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("broken.drl");
    String original = "package p;\nrule \"A\"\n  agenda-group \"g\"\n  when\n  then\nend\n";
    Files.writeString(f, original);

    int exit = runCli("--write", f.toString());

    assertThat(exit).isEqualTo(2);
    assertThat(Files.readString(f)).isEqualTo(original); // byte-identical: nothing written
  }

  @Test
  void checkReportsParseErrorsDistinctly(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("broken.drl");
    Files.writeString(f, "package p;\nrule \"A\"\n  agenda-group \"g\"\n  when\n  then\nend\n");
    assertThat(runCli("--check", f.toString())).isEqualTo(2);
  }

  @Test
  void stdoutModeEmitsNothingForUnparseableInput(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("broken.drl");
    Files.writeString(f, "package p;\nrule \"A\"\n  agenda-group \"g\"\n  when\n  then\nend\n");
    // default mode: formatted text goes to stdout — for broken input stdout must stay empty
    CliRun run = runCliCaptured(f.toString());
    assertThat(run.exit()).isEqualTo(2);
    assertThat(run.stdout()).isEmpty();
    assertThat(run.stderr()).contains("Refusing to format");
  }

  /**
   * The pre-commit hook path. This file has NO parse errors — an annotation's
   * chunk swallowed the when-block, and that derivation is error-free — so only
   * the structural gate stands between it and a silent overwrite that would crush
   * the swallowed span onto one line. --write must leave the bytes alone.
   */
  @Test
  void writeRefusesAndLeavesTheFileAloneWhenAWhenBlockWasSwallowed(@TempDir Path dir)
      throws Exception {
    String swallowed = String.join("\n",
        "package p;",
        "",
        "rule \"R1\"",
        "  @implements( \"RM-1\" )",
        "  when",
        "    Foo( bar == 1 )",
        "  then",
        "    doThing();",
        "end",
        "",
        "function ArrayList collect( List xs, List out )",
        "{",
        "  out.add( xs.get( 0 );",
        "  return out;",
        "}",
        "",
        "rule \"R2\"",
        "  when",
        "    Baz( qux == 2 )",
        "  then",
        "    other();",
        "end",
        "");
    Path f = dir.resolve("swallowed.drl");
    Files.writeString(f, swallowed);

    assertThat(DRLFormatter.formatChecked(swallowed).syntaxErrors())
        .as("no parse errors, so only the structural gate can catch this")
        .isZero();

    CliRun run = runCliCaptured("--write", f.toString());
    assertThat(run.exit()).isEqualTo(2);
    assertThat(run.stderr()).contains("when-block the parser could not read");
    assertThat(Files.readString(f)).isEqualTo(swallowed);   // untouched
  }

  @Test
  void stdinModeRefusesOnParseErrors() throws Exception {
    String brokenDrl = "package p;\nrule \"A\"\n  agenda-group \"g\"\n  when\n  then\nend\n";
    InputStream originalIn = System.in;
    try {
      System.setIn(new ByteArrayInputStream(brokenDrl.getBytes(StandardCharsets.UTF_8)));
      CliRun run = runCliCaptured("--stdin");
      assertThat(run.exit()).isEqualTo(2);
      assertThat(run.stdout()).isEmpty();
      assertThat(run.stderr()).contains("Refusing to format");
    } finally {
      System.setIn(originalIn);
    }
  }

  @Test
  void writeFormatsAndNotSourceCleanly(@TempDir Path dir) throws Exception {
    String source =
        "package p;\nrule \"R\"\n  when\n    accumulate(\n      Foo( x == 1 ) and not Bar( y == 2 ),\n      count()\n    )\n  then\nend\n";
    Path f = dir.resolve("victim.drl");
    Files.writeString(f, source);
    CliRun run = runCliCaptured("--write", f.toString());
    assertThat(run.exit()).isEqualTo(0);
    String written = Files.readString(f);
    assertThat(written).contains("and not Bar");            // connective survives
    assertThat(run.stderr()).doesNotContain("output fails to re-parse");
  }

  @Test
  void writeAndCheckTogetherAreRejected(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("a.drl");
    Files.writeString(f, "package p;\n");
    CliRun run = runCliCaptured("--write", "--check", f.toString());
    assertThat(run.exit()).isEqualTo(1);
    assertThat(run.stderr()).contains("--write and --check cannot be combined");
  }

  @Test
  void writeDirContinuesPastFailures(@TempDir Path dir) throws Exception {
    Files.writeString(dir.resolve("a_broken.drl"),
        "package p;\nrule \"A\"\n  agenda-group \"g\"\n  when\n  then\nend\n");
    Files.writeString(dir.resolve("b_good.drl"), "package p;\nrule \"B\"\nwhen\nthen\nend\n");

    CliRun run = runCliCaptured("--write-dir", dir.toString());

    assertThat(run.exit()).isEqualTo(2); // parse failure dominates
    assertThat(run.stderr()).contains("Refusing to format"); // a_broken reported...
    assertThat(Files.readString(dir.resolve("b_good.drl")))
        .contains("rule \"B\""); // ...and b_good was still processed
  }

  @Test
  void writeDirContinuesPastEncodingFailures(@TempDir Path dir) throws Exception {
    Files.write(dir.resolve("a_latin1.drl"), new byte[] {(byte) 0xE9, ' ', 'x', '\n'}); // invalid UTF-8
    Files.writeString(dir.resolve("b_good.drl"), "package p;\nrule \"B\"\nwhen\nthen\nend\n");

    CliRun run = runCliCaptured("--write-dir", dir.toString());

    assertThat(run.exit()).isEqualTo(1); // io failure, no parse failures
    assertThat(run.stderr()).contains("Cannot read (not valid UTF-8)");
    assertThat(Files.readString(dir.resolve("b_good.drl")))
        .contains("rule \"B\""); // walk continued
  }

  @Test
  void malformedEncodingYieldsOneLineError(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("latin1.drl");
    Files.write(f, new byte[] {(byte) 0xE9, ' ', 'x', '\n'}); // 0xE9 = é in latin-1, invalid UTF-8 lead
    CliRun run = runCliCaptured("--check", f.toString());
    assertThat(run.exit()).isEqualTo(1);
    assertThat(run.stderr()).contains("Cannot read (not valid UTF-8)");
    assertThat(run.stderr()).doesNotContain("at org.");
  }

  // ── --lines ────────────────────────────────────────────────────────────────
  //
  // Rules A (lines 2-6) and C (12-16) are already canonical; rule B (7-11) is
  // not. Whole-file --check therefore fails, and --lines decides per statement.
  private static final String MIXED = String.join("\n",
      "package p;",
      "rule \"A\"",
      "  when",
      "    $p: Person( age > 18 )",
      "  then",
      "end",
      "rule \"B\"",
      "when",
      "$q : Person(  age>21 )",
      "then",
      "end",
      "rule \"C\"",
      "  when",
      "    $r: Person( age > 65 )",
      "  then",
      "end",
      "");

  private static Path mixedFile(Path dir) throws Exception {
    Path f = dir.resolve("mixed.drl");
    Files.writeString(f, MIXED);
    return f;
  }

  @Test
  void linesCheckPassesWhenOnlyUntouchedRulesAreUnformatted(@TempDir Path dir) throws Exception {
    Path f = mixedFile(dir);
    // The payoff: the file as a whole is not canonical...
    assertThat(runCli("--check", f.toString())).isEqualTo(1);
    // ...but a commit that only edited rule A is not held responsible for rule B.
    assertThat(runCli("--check", "--lines", "4:4", f.toString())).isZero();
  }

  @Test
  void linesCheckFlagsTheTouchedRuleAndReportsItsSpan(@TempDir Path dir) throws Exception {
    CliRun run = runCliCaptured("--check", "--lines", "9:9", mixedFile(dir).toString());
    assertThat(run.exit()).isEqualTo(1);
    // The "Would reformat" prefix is the hook's stderr triage key — keep it.
    assertThat(run.stderr()).startsWith("Would reformat: ");
    assertThat(run.stderr()).contains("lines 7-11");
  }

  @Test
  void linesExpandsAnyLineOfARuleToTheWholeRule(@TempDir Path dir) throws Exception {
    // Line 11 is just rule B's "end" — still enough to pull the rule into scope.
    CliRun run = runCliCaptured("--check", "--lines", "11:11", mixedFile(dir).toString());
    assertThat(run.exit()).isEqualTo(1);
    assertThat(run.stderr()).contains("lines 7-11");
  }

  @Test
  void linesDoesNotDragInUntouchedRulesBetweenRanges(@TempDir Path dir) throws Exception {
    // A and C are canonical; the messy B sits between them and must stay out of scope.
    assertThat(runCli("--check", "--lines", "4:4,14:14", mixedFile(dir).toString())).isZero();
    // A contiguous span across all three does include B.
    assertThat(runCli("--check", "--lines", "4:14", mixedFile(dir).toString())).isEqualTo(1);
  }

  @Test
  void repeatedLinesFlagsAccumulate(@TempDir Path dir) throws Exception {
    assertThat(runCli("--check", "--lines", "4:4", "--lines", "14:14", mixedFile(dir).toString()))
        .isZero();
    assertThat(runCli("--check", "--lines", "4:4", "--lines", "9:9", mixedFile(dir).toString()))
        .isEqualTo(1);
  }

  @Test
  void linesWriteFormatsOnlyTheTargetedRule(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("two_messy.drl");
    String source = String.join("\n",
        "package p;",
        "rule \"A\"",
        "when",
        "$p : Person(  age>18 )",
        "then",
        "end",
        "rule \"B\"",
        "when",
        "$q : Person(  age>21 )",
        "then",
        "end",
        "");
    Files.writeString(f, source);

    assertThat(runCli("--write", "--lines", "9:9", f.toString())).isZero();

    String written = Files.readString(f);
    assertThat(written).contains("    $q: Person( age > 21 )");   // B formatted...
    assertThat(written).contains("$p : Person(  age>18 )");       // ...A untouched
    assertThat(runCli("--check", "--lines", "4:4", f.toString())).isEqualTo(1);
  }

  /** Splicing must not rewrite an untouched line's ending — the point of --lines. */
  @Test
  void linesWritePreservesTheFilesLineEndings(@TempDir Path dir) throws Exception {
    Path f = mixedFile(dir);
    assertThat(runCli("--write", "--lines", "9:9", f.toString())).isZero();

    String written = Files.readString(f);
    assertThat(written).doesNotContain("\r\n");
    assertThat(runCli("--check", f.toString())).isZero();   // now canonical throughout
  }

  @Test
  void linesWriteKeepsCrlfFilesOnCrlf(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("crlf.drl");
    Files.writeString(f, MIXED.replace("\n", "\r\n"));

    assertThat(runCli("--write", "--lines", "9:9", f.toString())).isZero();

    String written = Files.readString(f);
    assertThat(written).contains("\r\n");
    assertThat(written.replace("\r\n", "\n")).doesNotContain("\n\n");   // no stray bare LFs
    assertThat(written.replace("\r\n", "")).doesNotContain("\n");
  }

  /** The option decides, not the file: --lines used to re-impose the file's own. */
  @Test
  void linesWriteHonoursAnExplicitLfLineEnding(@TempDir Path dir) throws Exception {
    Path config = dir.resolve("lf.json");
    Files.writeString(config, "{\"lineEndings\":\"lf\"}");
    Path f = dir.resolve("crlf.drl");
    Files.writeString(f, MIXED.replace("\n", "\r\n"));

    assertThat(runCli("--config", config.toString(), "--write", "--lines", "9:9", f.toString()))
        .isZero();

    assertThat(Files.readString(f)).doesNotContain("\r");
  }

  @Test
  void linesWriteHonoursAnExplicitCrlfLineEnding(@TempDir Path dir) throws Exception {
    Path config = dir.resolve("crlf.json");
    Files.writeString(config, "{\"lineEndings\":\"crlf\"}");
    Path f = mixedFile(dir);

    assertThat(runCli("--config", config.toString(), "--write", "--lines", "9:9", f.toString()))
        .isZero();

    String written = Files.readString(f);
    assertThat(written).contains("\r\n");
    assertThat(written.replace("\r\n", "")).doesNotContain("\n");
  }

  @Test
  void linesWriteKeepsACommentTrailingTheRulesEnd(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("tail.drl");
    Files.writeString(f, String.join("\n",
        "package p;",
        "rule \"A\"",
        "when",
        "$p : Person(  age>18 )",
        "then",
        "end // done",
        "rule \"B\"",
        "  when",
        "    $q: Person( age > 21 )",
        "  then",
        "end",
        ""));

    assertThat(runCli("--write", "--lines", "4:4", f.toString())).isZero();

    String written = Files.readString(f);
    assertThat(written.split("// done", -1).length - 1).isEqualTo(1);
    assertThat(runCli("--check", "--lines", "4:4", f.toString())).isZero();
  }

  @Test
  void linesStillRefusesUnparseableInput(@TempDir Path dir) throws Exception {
    Path f = dir.resolve("broken.drl");
    String original = "package p;\nrule \"A\"\n  agenda-group \"g\"\n  when\n  then\nend\n";
    Files.writeString(f, original);

    CliRun run = runCliCaptured("--write", "--lines", "3:3", f.toString());

    assertThat(run.exit()).isEqualTo(2);
    assertThat(run.stderr()).contains("Refusing to format");
    assertThat(Files.readString(f)).isEqualTo(original);   // byte-identical
  }

  @Test
  void linesOverASelectionCoveringNoStatementIsClean(@TempDir Path dir) throws Exception {
    assertThat(runCli("--check", "--lines", "900:920", mixedFile(dir).toString())).isZero();
  }

  @Test
  void linesCheckWorksOverStdin() throws Exception {
    InputStream originalIn = System.in;
    try {
      System.setIn(new ByteArrayInputStream(MIXED.getBytes(StandardCharsets.UTF_8)));
      assertThat(runCli("--check", "--lines", "4:4", "--stdin", "mixed.drl")).isZero();

      System.setIn(new ByteArrayInputStream(MIXED.getBytes(StandardCharsets.UTF_8)));
      CliRun run = runCliCaptured("--check", "--lines", "9:9", "--stdin", "mixed.drl");
      assertThat(run.exit()).isEqualTo(1);
      assertThat(run.stderr()).startsWith("Would reformat: mixed.drl (lines 7-11)");
    } finally {
      System.setIn(originalIn);
    }
  }

  @Test
  void linesRejectsMalformedRanges(@TempDir Path dir) throws Exception {
    Path f = mixedFile(dir);
    assertThat(runCliCaptured("--check", "--lines", "abc", f.toString()).stderr())
        .contains("Malformed --lines range");
    assertThat(runCliCaptured("--check", "--lines", "0:4", f.toString()).stderr())
        .contains("1 <= start <= end");
    assertThat(runCliCaptured("--check", "--lines", "9:4", f.toString()).stderr())
        .contains("1 <= start <= end");
    assertThat(runCli("--check", "--lines", "abc", f.toString())).isEqualTo(1);
  }

  @Test
  void linesRequiresCheckOrWrite(@TempDir Path dir) throws Exception {
    CliRun run = runCliCaptured("--lines", "4:4", mixedFile(dir).toString());
    assertThat(run.exit()).isEqualTo(1);
    assertThat(run.stderr()).contains("--lines requires --check or --write");
  }

  @Test
  void linesRejectsMultipleFilesAndWriteDir(@TempDir Path dir) throws Exception {
    Path a = mixedFile(dir);
    Path b = dir.resolve("b.drl");
    Files.writeString(b, MIXED);

    CliRun many = runCliCaptured("--check", "--lines", "4:4", a.toString(), b.toString());
    assertThat(many.exit()).isEqualTo(1);
    assertThat(many.stderr()).contains("--lines expects exactly one file");

    CliRun dirRun = runCliCaptured("--lines", "4:4", "--write-dir", dir.toString());
    assertThat(dirRun.exit()).isEqualTo(1);
    assertThat(dirRun.stderr()).contains("--lines cannot be used with --write-dir");
  }

  @Test
  void helpAdvertisesLinesSoHooksCanFeatureDetect() throws Exception {
    // CheckDRL.py greps --help for "--lines" to tell a current jar from a stale
    // toolkit one. If this string moves, the hook's detection must move with it.
    CliRun run = runCliCaptured("--help");
    assertThat(run.exit()).isZero();
    assertThat(run.stdout()).contains("--lines");
  }

  @Test
  void configFileDrivesTheOptions(@TempDir Path dir) throws Exception {
    Path config = dir.resolve("fmt.json");
    Files.writeString(config, "{\"parenPadding\": false, \"lineEndings\": \"lf\"}");
    Path drl = dir.resolve("r.drl");
    Files.writeString(drl, "package p;\r\nrule R\r\n  when\r\n    Person( age > 18 )\r\n  then\r\nend\r\n");

    assertThat(runCli("--config", config.toString(), "--write", drl.toString())).isZero();

    String written = Files.readString(drl);
    assertThat(written).contains("Person(age > 18)").doesNotContain("\r");
  }

  @Test
  void configFileIsHonouredByCheckAndStdin(@TempDir Path dir) throws Exception {
    Path config = dir.resolve("fmt.json");
    Files.writeString(config, "{\"parenPadding\": false}");
    Path tight = dir.resolve("t.drl");
    Files.writeString(tight, "package p;\nrule R\n  when\n    Person(age > 18)\n  then\nend\n");

    // canonical under the config, so --check passes; padded under defaults, so it would not
    assertThat(runCli("--config", config.toString(), "--check", tight.toString())).isZero();
    assertThat(runCli("--check", tight.toString())).isEqualTo(1);

    InputStream original = System.in;
    try {
      System.setIn(new ByteArrayInputStream(Files.readAllBytes(tight)));
      CliRun run = runCliCaptured("--config", config.toString(), "--stdin");
      assertThat(run.exit()).isZero();
      assertThat(run.stdout()).contains("Person(age > 18)");
    } finally {
      System.setIn(original);
    }
  }

  @Test
  void missingOrInvalidConfigFileIsAUsageError(@TempDir Path dir) throws Exception {
    Path drl = dir.resolve("r.drl");
    Files.writeString(drl, "package p;\n");
    CliRun missing = runCliCaptured("--config", dir.resolve("nope.json").toString(), "--check", drl.toString());
    assertThat(missing.exit()).isEqualTo(1);
    assertThat(missing.stderr()).contains("--config");

    Path notAnObject = dir.resolve("bad.json");
    Files.writeString(notAnObject, "[1, 2]");
    assertThat(runCliCaptured("--config", notAnObject.toString(), "--check", drl.toString()).exit()).isEqualTo(1);

    assertThat(runCliCaptured("--config").exit()).isEqualTo(1);
  }

  @Test
  void configMustPrecedeWriteDirAndThenApplies(@TempDir Path dir) throws Exception {
    Path config = dir.resolve("fmt.json");
    Files.writeString(config, "{\"parenPadding\": false}");
    Path rules = dir.resolve("rules");
    Files.createDirectories(rules);
    Path drl = rules.resolve("r.drl");
    Files.writeString(drl, "package p;\nrule R\n  when\n    Person( age > 18 )\n  then\nend\n");

    assertThat(runCli("--config", config.toString(), "--write-dir", rules.toString())).isZero();
    assertThat(Files.readString(drl)).contains("Person(age > 18)");

    CliRun reversed = runCliCaptured("--write-dir", rules.toString(), "--config", config.toString());
    assertThat(reversed.exit()).isEqualTo(1);
    assertThat(reversed.stderr()).contains("--write-dir");
  }
}
