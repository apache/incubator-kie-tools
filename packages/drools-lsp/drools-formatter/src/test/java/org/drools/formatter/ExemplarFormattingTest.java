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

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * The exemplar is deliberately badly formatted; the formatter must put it into
 * canonical form without losing content. Content is compared modulo the three
 * sanctioned normalizations — whitespace, statement terminators and the
 * accumulate source separator — and the result must parse, keep every rule,
 * and be a fixed point.
 */
class ExemplarFormattingTest {

    @Test
    void exemplarFormatsCanonicallyAndLosslessly() throws Exception {
        String input = Files.readString(Path.of("src", "test", "resources", "order_rules.drl"));

        DRLFormatter.FormatResult r = DRLFormatter.formatChecked(input);
        assertThat(r.syntaxErrors()).isZero();
        assertThat(r.outputSyntaxErrors()).isZero();
        assertThat(normalize(r.formatted())).isEqualTo(normalize(input));
        assertThat(countRules(r.formatted())).isEqualTo(countRules(input));
        assertThat(DRLFormatter.format(r.formatted())).isEqualTo(r.formatted());
    }

    private static String normalize(String s) { return s.replaceAll("[\\s;,]", ""); }

    private static int countRules(String s) {
        Matcher m = Pattern.compile("(?m)^\\s*rule\\s").matcher(s);
        int n = 0;
        while (m.find()) n++;
        return n;
    }
}
