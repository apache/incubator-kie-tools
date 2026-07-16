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

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParsedDrlTest {

    private static final String DRL = """
            package demo;
            declare Person
              name : String
            end
            rule R
              when
                // Person here
                Person( )
              then
            end
            """;

    @Test
    void parsesTheDocumentExactlyOnce() {
        DRLParsers.resetParseCount();
        ParsedDrl parsed = ParsedDrl.of(DRL);
        parsed.declaredTypes(); // must reuse the parse tree, not re-parse
        parsed.declaredTypes();
        assertThat(DRLParsers.parseCount()).isEqualTo(1);
    }

    @Test
    void declaredTypesExtractsDeclareBlocks() {
        List<DeclaredType> types = ParsedDrl.of(DRL).declaredTypes();
        assertThat(types).extracting(t -> t.name).contains("Person");
    }

    @Test
    void tokenAtReturnsTheCommentTokenInsideAComment() {
        // Line 6 is "    // Person here"; caret on "Person" (col 9).
        Token t = ParsedDrl.of(DRL).tokenAt(new Position(6, 9));
        assertThat(t).isNotNull();
        assertThat(t.getText()).contains("Person here"); // the comment token, not the type use
    }

    @Test
    void tokenAtReturnsTheIdentifierTokenOnRealCode() {
        // Line 7 is "    Person( )"; caret on the pattern "Person" (col 5).
        Token t = ParsedDrl.of(DRL).tokenAt(new Position(7, 5));
        assertThat(t).isNotNull();
        assertThat(t.getText()).isEqualTo("Person");
    }

    @Test
    void tokenAtReturnsNullWhenOutOfRange() {
        assertThat(ParsedDrl.of(DRL).tokenAt(new Position(999, 0))).isNull();
    }
}
