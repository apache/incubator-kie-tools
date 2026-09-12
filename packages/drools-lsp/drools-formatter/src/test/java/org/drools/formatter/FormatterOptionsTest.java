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

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormatterOptionsTest {

    @Test
    void defaultsAreTheHouseStyleExceptLineEndings() {
        FormatterOptions d = FormatterOptions.DEFAULTS;
        assertThat(d.tabSize()).isEqualTo(2);
        assertThat(d.insertSpaces()).isTrue();
        assertThat(d.lineLength()).isEqualTo(110);
        assertThat(d.lineEndings()).isEqualTo(FormatterOptions.LineEndings.PRESERVE);
        assertThat(d.parenPadding()).isTrue();
        assertThat(d.bindingColonSpace()).isFalse();
        assertThat(d.normalizeTerminators()).isTrue();
        assertThat(d.alignDeclarations()).isTrue();
        assertThat(d.headerMetadata()).isEqualTo(FormatterOptions.HeaderMetadata.INDENTED);
    }

    @Test
    void fromJsonReadsEveryField() {
        FormatterOptions o = FormatterOptions.fromJson(JsonParser.parseString("""
            {"tabSize":4,"insertSpaces":false,"lineLength":80,"lineEndings":"lf",
             "parenPadding":false,"bindingColonSpace":true,"normalizeTerminators":false,
             "alignDeclarations":false,"headerMetadata":"inline"}""").getAsJsonObject());
        assertThat(o).isEqualTo(new FormatterOptions(4, false, 80, FormatterOptions.LineEndings.LF,
                false, true, false, false, FormatterOptions.HeaderMetadata.INLINE));
    }

    /** A settings typo must never disable formatting: bad values fall back per field. */
    @Test
    void fromJsonIgnoresUnknownKeysAndFallsBackOnBadValues() {
        FormatterOptions o = FormatterOptions.fromJson(JsonParser.parseString("""
            {"tabSize":0,"lineLength":"wide","lineEndings":"mac","headerMetadata":7,"colour":"red"}""")
                .getAsJsonObject());
        assertThat(o).isEqualTo(FormatterOptions.DEFAULTS);
        assertThat(FormatterOptions.fromJson(null)).isEqualTo(FormatterOptions.DEFAULTS);
    }

    @Test
    void enumValuesAreCaseInsensitive() {
        FormatterOptions o = FormatterOptions.fromJson(JsonParser.parseString(
                "{\"lineEndings\":\"CRLF\",\"headerMetadata\":\"Flush\"}").getAsJsonObject());
        assertThat(o.lineEndings()).isEqualTo(FormatterOptions.LineEndings.CRLF);
        assertThat(o.headerMetadata()).isEqualTo(FormatterOptions.HeaderMetadata.FLUSH);
    }

    @Test
    void withIndentReplacesOnlyTheIndentPair() {
        FormatterOptions o = FormatterOptions.DEFAULTS.withIndent(8, false);
        assertThat(o.tabSize()).isEqualTo(8);
        assertThat(o.insertSpaces()).isFalse();
        assertThat(o.indentUnit()).isEqualTo("\t");
        assertThat(o.lineLength()).isEqualTo(110);
        assertThat(FormatterOptions.DEFAULTS.indentUnit()).isEqualTo("  ");
    }

    /** An editor that sends no tabSize (0) must not zero the configured indent. */
    @Test
    void withIndentClampsANonPositiveTabSizeToTheConfiguredOne() {
        FormatterOptions configured = FormatterOptions.DEFAULTS.withIndent(4, true);
        assertThat(configured.withIndent(0, false).tabSize()).isEqualTo(4);
        assertThat(configured.withIndent(0, false).insertSpaces()).isFalse();
        assertThat(configured.withIndent(-1, true).tabSize()).isEqualTo(4);
    }

    /** package.json declares {@code "minimum": 40}; the engine holds the same line. */
    @Test
    void lineLengthBelowTheSettingsMinimumFallsBackToTheDefault() {
        assertThat(FormatterOptions.fromJson(JsonParser.parseString("{\"lineLength\":39}")
                .getAsJsonObject()).lineLength()).isEqualTo(110);
        assertThat(FormatterOptions.fromJson(JsonParser.parseString("{\"lineLength\":40}")
                .getAsJsonObject()).lineLength()).isEqualTo(40);
    }

    /** intOr already accepts a numeric string; booleans must not be the odd one out. */
    @Test
    void booleanOptionsAlsoAcceptQuotedStrings() {
        FormatterOptions o = FormatterOptions.fromJson(JsonParser.parseString(
                "{\"parenPadding\":\"false\",\"bindingColonSpace\":\" TRUE \",\"tabSize\":\"4\"}")
                .getAsJsonObject());
        assertThat(o.parenPadding()).isFalse();
        assertThat(o.bindingColonSpace()).isTrue();
        assertThat(o.tabSize()).isEqualTo(4);
        assertThat(FormatterOptions.fromJson(JsonParser.parseString("{\"parenPadding\":\"yes\"}")
                .getAsJsonObject()).parenPadding()).isTrue();
    }

    @Test
    void preserveTakesTheInputsFirstLineBreakAndDefaultsToLf() {
        assertThat(FormatterOptions.detectLineEnding("a\r\nb\nc")).isEqualTo("\r\n");
        assertThat(FormatterOptions.detectLineEnding("a\nb\r\n")).isEqualTo("\n");
        assertThat(FormatterOptions.detectLineEnding("no break")).isEqualTo("\n");
        assertThat(FormatterOptions.detectLineEnding(null)).isEqualTo("\n");
        assertThat(FormatterOptions.DEFAULTS.lineEnding("x\r\ny")).isEqualTo("\r\n");
        assertThat(new FormatterOptions(2, true, 110, FormatterOptions.LineEndings.LF, true, false, true, true,
                FormatterOptions.HeaderMetadata.INDENTED).lineEnding("x\r\ny")).isEqualTo("\n");
    }

    private static final String RULE = "package p;\nrule R\n  when\n    $p:Person(age>18)\n  then\nend\n";

    private static String with(FormatterOptions o, String drl) {
        return DRLFormatter.format(drl, o);
    }

    @Test
    void parenPaddingOffKeepsParensTightEverywhere() {
        FormatterOptions o = FormatterOptions.fromJson(JsonParser.parseString("{\"parenPadding\":false}").getAsJsonObject());
        String out = with(o, "package p;\nfunction int f(int a) { return a; }\n" + RULE
                + "rule S\n  when\n    eval( 1 == 1 )\n  then\n    doIt( 1, 2 );\nend\n");
        assertThat(out).contains("Person(age > 18)").contains("f(int a)").contains("eval(1 == 1)").contains("doIt(1, 2);");
        assertThat(out).doesNotContain("( ").doesNotContain(" )");
    }

    @Test
    void bindingColonSpacePutsASpaceBeforeTheColon() {
        FormatterOptions o = FormatterOptions.fromJson(JsonParser.parseString("{\"bindingColonSpace\":true}").getAsJsonObject());
        assertThat(with(o, RULE)).contains("$p : Person( age > 18 )");
        assertThat(with(FormatterOptions.DEFAULTS, RULE)).contains("$p: Person( age > 18 )");
    }

    @Test
    void bindingColonSpaceAlsoGovernsDeclareFieldColons() {
        String drl = "package p;\ndeclare F\n  a : int\n  longerName : String\nend\n";
        FormatterOptions spaced = FormatterOptions.fromJson(
                JsonParser.parseString("{\"bindingColonSpace\":true}").getAsJsonObject());
        assertThat(with(spaced, drl)).contains("  a :          int\n  longerName : String");
        FormatterOptions spacedUnaligned = FormatterOptions.fromJson(JsonParser.parseString(
                "{\"bindingColonSpace\":true,\"alignDeclarations\":false}").getAsJsonObject());
        assertThat(with(spacedUnaligned, drl)).contains("  a : int\n  longerName : String");
        assertThat(with(FormatterOptions.DEFAULTS, drl)).contains("  a:          int\n  longerName: String");
    }

    @Test
    void indentComesFromTabSizeAndInsertSpaces() {
        assertThat(with(FormatterOptions.DEFAULTS.withIndent(4, true), RULE)).contains("\n    when\n        $p:");
        assertThat(with(FormatterOptions.DEFAULTS.withIndent(4, false), RULE)).contains("\n\twhen\n\t\t$p:");
    }

    @Test
    void lineLengthIsTheWrapTrigger() {
        String longRule = "package p;\nrule R\n  when\n    Person( name == \"aaaaaaaaaaaaaaaaaaaa\", age > 18 )\n  then\nend\n";
        FormatterOptions narrow = FormatterOptions.fromJson(JsonParser.parseString("{\"lineLength\":40}").getAsJsonObject());
        assertThat(with(narrow, longRule)).contains("Person(\n").contains("\n    )");
        assertThat(with(FormatterOptions.DEFAULTS, longRule)).contains("Person( name == \"aaaaaaaaaaaaaaaaaaaa\", age > 18 )");
    }

    @Test
    void lineEndingsFollowTheOption() {
        String crlfInput = RULE.replace("\n", "\r\n");
        assertThat(with(FormatterOptions.DEFAULTS, crlfInput)).contains("\r\n").doesNotContain("\n\n");
        assertThat(with(FormatterOptions.DEFAULTS, RULE)).doesNotContain("\r");
        FormatterOptions lf = FormatterOptions.fromJson(JsonParser.parseString("{\"lineEndings\":\"lf\"}").getAsJsonObject());
        assertThat(with(lf, crlfInput)).doesNotContain("\r");
        FormatterOptions crlf = FormatterOptions.fromJson(JsonParser.parseString("{\"lineEndings\":\"crlf\"}").getAsJsonObject());
        assertThat(with(crlf, RULE)).contains("\r\n");
        // range formatting returns the same ending as whole-document formatting
        assertThat(DRLFormatter.formatRange(crlfInput, 1, 1, FormatterOptions.DEFAULTS).text()).contains("\r\n");
    }

    @Test
    void normalizeTerminatorsOffKeepsTheSourcesChoices() {
        FormatterOptions o = FormatterOptions.fromJson(JsonParser.parseString("{\"normalizeTerminators\":false}").getAsJsonObject());
        String drl = "package p;\nimport a.B\nimport a.C;\nglobal java.util.List g;\nglobal java.util.List h\n"
                + "rule R\n  when\n    accumulate( X( $v : v ), $s : sum( $v ) )\n  then\nend\n";
        String out = with(o, drl);
        assertThat(out).contains("import a.B\n").contains("import a.C;\n")
                .contains("global java.util.List g;\n").contains("global java.util.List h\n")
                .contains("X( $v: v ),\n");
        String normalized = with(FormatterOptions.DEFAULTS, drl);
        assertThat(normalized).contains("import a.B;\n").contains("global java.util.List g\n").contains("X( $v: v );\n");
    }

    @Test
    void alignDeclarationsOffEmitsSingleSpacedRows() {
        FormatterOptions o = FormatterOptions.fromJson(JsonParser.parseString("{\"alignDeclarations\":false}").getAsJsonObject());
        String drl = "package p;\ndeclare enum E\n  Short(\"a\",1),\n  MuchLongerName(\"bb\",2);\nend\n"
                + "declare F\n  a : int\n  longerName : String\nend\n";
        String out = with(o, drl);
        assertThat(out).contains("  Short( \"a\", 1 ),\n  MuchLongerName( \"bb\", 2 );")
                .contains("  a: int\n  longerName: String");
        assertThat(with(FormatterOptions.DEFAULTS, drl)).contains("  Short(          \"a\",  1 ),");
    }

    private static final String HEADERS = "package p;\nrule R @Bar salience 10\n  when\n    X()\n  then\nend\n"
            + "query q() @Q\n  X()\nend\n"
            + "declare D @role( event )\n  a : int\nend\n";

    // The two declare shapes whose annotations bypassed emitHeader.
    private static final String ENTRY_POINT_AND_WINDOW =
            "package p;\ndeclare entry-point \"Orders\" @Foo\nend\n"
            + "declare window Ticks @Bar\n  StockTick() over window:time( 5s )\nend\n";

    @Test
    void headerMetadataIndentedIsTheDefault() {
        assertThat(with(FormatterOptions.DEFAULTS, HEADERS))
                .contains("rule R\n  @Bar\n  salience 10\n  when")
                .contains("query q()\n  @Q\n")
                .contains("declare D\n  @role( event )\n");
        assertThat(DRLFormatter.formatChecked(ENTRY_POINT_AND_WINDOW).refused()).isFalse();
        assertThat(with(FormatterOptions.DEFAULTS, ENTRY_POINT_AND_WINDOW))
                .contains("declare entry-point \"Orders\"\n  @Foo\nend")
                .contains("declare window Ticks\n  @Bar\n  StockTick()");
    }

    @Test
    void headerMetadataFlushPutsItemsAtColumnZero() {
        FormatterOptions o = FormatterOptions.fromJson(JsonParser.parseString("{\"headerMetadata\":\"flush\"}").getAsJsonObject());
        assertThat(with(o, HEADERS))
                .contains("rule R\n@Bar\nsalience 10\n  when")
                .contains("query q()\n@Q\n")
                .contains("declare D\n@role( event )\n");
        assertThat(with(o, ENTRY_POINT_AND_WINDOW))
                .contains("declare entry-point \"Orders\"\n@Foo\nend")
                .contains("declare window Ticks\n@Bar\n  StockTick()");
    }

    @Test
    void headerMetadataInlineRidesTheHeaderLineAndWrapsAtLineLength() {
        FormatterOptions o = FormatterOptions.fromJson(JsonParser.parseString("{\"headerMetadata\":\"inline\"}").getAsJsonObject());
        assertThat(with(o, HEADERS))
                .contains("rule R @Bar salience 10\n  when")
                .contains("query q() @Q\n")
                .contains("declare D @role( event )\n");
        assertThat(with(o, ENTRY_POINT_AND_WINDOW))
                .contains("declare entry-point \"Orders\" @Foo\nend")
                .contains("declare window Ticks @Bar\n  StockTick()");
        FormatterOptions narrow = FormatterOptions.fromJson(JsonParser.parseString(
                "{\"headerMetadata\":\"inline\",\"lineLength\":40}").getAsJsonObject());
        String wrapped = with(narrow, "package p;\nrule \"a rather long rule name here\" @Bar salience 10 no-loop\n  when\n    X()\n  then\nend\n");
        assertThat(wrapped).contains("rule \"a rather long rule name here\" @Bar\n  salience 10 no-loop\n  when");
    }

    /** Option interactions: every combination must be a fixed point on the exemplar. */
    @Test
    void everyOptionCombinationIsIdempotentOnTheExemplar() throws Exception {
        String input = java.nio.file.Files.readString(java.nio.file.Path.of("src", "test", "resources", "order_rules.drl"));
        for (boolean pad : new boolean[]{true, false})
        for (boolean colon : new boolean[]{true, false})
        for (boolean norm : new boolean[]{true, false})
        for (boolean align : new boolean[]{true, false})
        for (FormatterOptions.HeaderMetadata h : FormatterOptions.HeaderMetadata.values())
        for (FormatterOptions.LineEndings eol : FormatterOptions.LineEndings.values()) {
            FormatterOptions o = new FormatterOptions(2, true, 110, eol, pad, colon, norm, align, h);
            DRLFormatter.FormatResult r = DRLFormatter.formatChecked(input, o);
            assertThat(r.refused()).as(o.toString()).isFalse();
            assertThat(DRLFormatter.format(r.formatted(), o)).as(o.toString()).isEqualTo(r.formatted());
        }
    }

    /** The colon rule must hold on the reflow path too, not only for one-line patterns. */
    @Test
    void bindingColonSpaceAlsoAppliesWhenThePatternWraps() {
        String longRule = "package p;\nrule R\n  when\n    $p:Person( name == \"aaaaaaaaaaaaaaaaaaaa\", age > 18 )\n  then\nend\n";
        FormatterOptions spaced = FormatterOptions.fromJson(JsonParser.parseString(
                "{\"bindingColonSpace\":true,\"lineLength\":40}").getAsJsonObject());
        FormatterOptions tight = FormatterOptions.fromJson(JsonParser.parseString("{\"lineLength\":40}").getAsJsonObject());
        assertThat(with(spaced, longRule)).contains("$p : Person(\n");
        assertThat(with(tight, longRule)).contains("$p: Person(\n");
    }
}
