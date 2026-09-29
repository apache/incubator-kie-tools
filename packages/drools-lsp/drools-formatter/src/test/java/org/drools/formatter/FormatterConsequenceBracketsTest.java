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

/**
 * Brackets in a consequence are Java's: array types and indexes, which hug
 * what they follow and never pad their contents. Braces keep the block
 * spacing the rest of the consequence uses.
 */
class FormatterConsequenceBracketsTest {

    private static String rule(String consequence) {
        return "package p;\n\nrule R\n  when\n    $o : Order()\n  then\n" + consequence + "\nend\n";
    }

    @Test
    void arrayTypesAndIndexesStayTight() {
        String out = DRLFormatter.format(rule(
                "    int [ ] counts = new int [ ] { 1, 2 };\n"
                + "    String [ ] parts = line.split( \",\" );\n"
                + "    results.add( counts [ 0 ] + parts [ i ] [ j ].length() );"));

        assertThat(out)
                .contains("    int[] counts = new int[] { 1, 2 };\n")
                .contains("    String[] parts = line.split( \",\" );\n")
                .contains("    results.add( counts[0] + parts[i][j].length() );\n");
        assertThat(DRLFormatter.format(out)).isEqualTo(out);
    }
}
