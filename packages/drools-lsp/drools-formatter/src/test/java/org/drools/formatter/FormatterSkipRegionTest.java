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

import static org.assertj.core.api.Assertions.assertThat;

class FormatterSkipRegionTest {

  @Test
  void freezesEnclosingStatementVerbatimWhileNeighboursReformat() {
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
        "// @formatter:on",
        "rule \"C\"",
        "when",
        "$p : Person()",
        "then",
        "end");

    String out = DRLFormatter.format(input).replace("\r\n", "\n");

    // Neighbours reformat: 'when' indented to 2 spaces under the rule header.
    assertThat(out).contains("rule \"A\"\n  when");
    assertThat(out).contains("rule \"C\"\n  when");

    // Frozen rule B kept verbatim (its 4-space 'when' and odd inner spacing),
    // markers included.
    assertThat(out).contains(String.join("\n",
        "// @formatter:off",
        "rule \"B\"",
        "    when",
        "  $p:Person(  age>18 )",
        "    then",
        "end",
        "// @formatter:on"));
  }

  @Test
  void offWithoutOnFreezesToEndOfFile() {
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
        "end");

    String out = DRLFormatter.format(input).replace("\r\n", "\n");

    assertThat(out).contains("rule \"A\"\n  when");            // before off: reformatted
    assertThat(out).contains(String.join("\n",                // frozen to EOF
        "// @formatter:off",
        "rule \"B\"",
        "    when",
        "  $p:Person(  age>18 )",
        "    then",
        "end"));
  }

  @Test
  void onWithoutPrecedingOffIsNoOp() {
    String input = String.join("\n",
        "// @formatter:on",
        "rule \"A\"",
        "when",
        "$p : Person()",
        "then",
        "end");

    String out = DRLFormatter.format(input).replace("\r\n", "\n");

    // No region was opened, so rule A reformats as usual.
    assertThat(out).contains("rule \"A\"\n  when");
  }

  @Test
  void markerInsideRuleFreezesTheWholeRule() {
    String input = String.join("\n",
        "rule \"B\"",
        "  when",
        "$p:Person(age>18)",          // messy, in 'when' — OUTSIDE the markers
        "  then",
        "// @formatter:off",
        "    x = 1 ;",
        "// @formatter:on",
        "end");

    String out = DRLFormatter.format(input).replace("\r\n", "\n");

    // The off-region overlaps rule B's span, so the WHOLE rule freezes — the
    // messy 'when' line (not itself between the markers) is preserved verbatim.
    // If it had reformatted it would read "    $p : Person( age > 18 )".
    assertThat(out).contains("\n$p:Person(age>18)\n");
  }

  @Test
  void withoutMarkersOutputIsNormalFormatting() {
    String input = "rule \"A\"\nwhen\n$p : Person()\nthen\nend";

    String out = DRLFormatter.format(input).replace("\r\n", "\n");

    assertThat(out).contains("rule \"A\"\n  when");
    assertThat(out).doesNotContain("@formatter");
  }

  @Test
  void formattingIsIdempotentWithFrozenRegions() {
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

    String once = DRLFormatter.format(input);
    String twice = DRLFormatter.format(once);

    assertThat(twice).isEqualTo(once);
  }

  @Test
  void adjacentFrozenRegionsKeepTheirMarkersExactlyOnce() {
    String input = String.join("\n",
        "// @formatter:off",
        "rule \"A\"",
        "    when",
        "$p:Person(age>18)",
        "    then",
        "end",
        "// @formatter:on",
        "// @formatter:off",
        "rule \"B\"",
        "    when",
        "$q:Person(age>21)",
        "    then",
        "end",
        "// @formatter:on");

    String out = DRLFormatter.format(input).replace("\r\n", "\n");

    // Both rules frozen verbatim.
    assertThat(out).contains("rule \"A\"\n    when\n$p:Person(age>18)");
    assertThat(out).contains("rule \"B\"\n    when\n$q:Person(age>21)");
    // Each physical marker is preserved exactly once (no drop, no duplication):
    // the input has two off-markers and two on-markers.
    assertThat(out.split("// @formatter:off", -1).length - 1).isEqualTo(2);
    assertThat(out.split("// @formatter:on", -1).length - 1).isEqualTo(2);
  }

  @Test
  void freezesANonRuleConstruct() {
    String input = String.join("\n",
        "// @formatter:off",
        "function int    foo( int x ) {",
        "return x+1 ;",
        "}",
        "// @formatter:on");

    String out = DRLFormatter.format(input).replace("\r\n", "\n");

    // The function freezes verbatim: the deliberately wide signature spacing is
    // kept, whereas normal formatting would collapse it to "function int foo(".
    assertThat(out).contains("function int    foo( int x ) {");
  }

  @Test
  void aMarkerOnTheSameLineAsCodeFreezesTheEnclosingRule() {
    String input = String.join("\n",
        "rule \"B\"",
        "when",
        "$p:Person(age>18)",
        "then",
        "x=1; // @formatter:off",
        "end",
        "// @formatter:on");

    String out = DRLFormatter.format(input).replace("\r\n", "\n");

    // The off-marker shares the 'x=1;' line, but the whole enclosing rule still
    // freezes, so the messy 'when' line (which normal formatting would indent
    // and space out to "    $p : Person( age > 18 )") is preserved verbatim.
    assertThat(out).contains("\n$p:Person(age>18)\n");
  }

  @Test
  void frozenRegionKeepsTrailingSpacesAndBlankLines() {
    String frozen = "// @formatter:off\nrule \"B\"   \n\n\n  when   \n  then\nend\n// @formatter:on\n";
    String drl = "package p;\n" + frozen;
    String out = DRLFormatter.format(drl).replace("\r\n", "\n");
    assertThat(out).contains("rule \"B\"   \n");          // trailing spaces preserved
    assertThat(out).contains("end\n// @formatter:on");
    assertThat(out).contains("\n\n\n  when   ");          // consecutive blank lines preserved
  }
}
