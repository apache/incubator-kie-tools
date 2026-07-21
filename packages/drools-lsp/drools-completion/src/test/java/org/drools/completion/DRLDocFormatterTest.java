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

package org.drools.completion;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DRLDocFormatterTest {

    @Test
    void inlineCodeBecomesBackticks() {
        assertThat(DRLDocFormatter.format("uses {@code getX()} here", null))
                .isEqualTo("uses `getX()` here");
    }

    @Test
    void literalContentIsMarkdownEscaped() {
        assertThat(DRLDocFormatter.format("{@literal *not bold*}", null))
                .isEqualTo("\\*not bold\\*");
    }

    @Test
    void linkWithTargetRendersMarkdownLink() {
        Map<String, String> targets = Map.of("Person", "file:///types.drl");
        assertThat(DRLDocFormatter.format("see {@link Person}", targets))
                .isEqualTo("see [Person](file:///types.drl)");
    }

    @Test
    void linkLabelIsUsedWhenPresent() {
        Map<String, String> targets = Map.of("Person", "file:///types.drl");
        assertThat(DRLDocFormatter.format("see {@link Person the patient}", targets))
                .isEqualTo("see [the patient](file:///types.drl)");
    }

    @Test
    void memberReferenceLooksUpTheTypePart() {
        Map<String, String> targets = Map.of("Person", "file:///types.drl");
        assertThat(DRLDocFormatter.format("{@link Person#name}", targets))
                .isEqualTo("[Person#name](file:///types.drl)");
    }

    @Test
    void unresolvedLinkFallsBackToCode() {
        assertThat(DRLDocFormatter.format("see {@link Person}", null))
                .isEqualTo("see `Person`");
    }

    @Test
    void unresolvedLinkplainFallsBackToPlainText() {
        assertThat(DRLDocFormatter.format("see {@linkplain Person}", null))
                .isEqualTo("see Person");
    }

    @Test
    void unsupportedTagsAreLeftUntouched() {
        assertThat(DRLDocFormatter.format("{@value Config#MAX}", null))
                .isEqualTo("{@value Config#MAX}");
    }

    @Test
    void nullAndEmptyPassThrough() {
        assertThat(DRLDocFormatter.format(null, null)).isNull();
        assertThat(DRLDocFormatter.format("", null)).isEmpty();
    }
}
