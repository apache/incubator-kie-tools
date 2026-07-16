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

import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.SymbolKind;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DRLDocumentSymbolHelperTest {

    @Test
    void emptyOrNullYieldsNoSymbols() {
        assertThat(DRLDocumentSymbolHelper.symbols(null)).isEmpty();
        assertThat(DRLDocumentSymbolHelper.symbols("")).isEmpty();
    }

    @Test
    void outlinesAllTopLevelConstructs() {
        String drl = "package demo;\n"
                + "global java.util.List auditLog\n"
                + "function int add(int a, int b) { return a + b; }\n"
                + "declare Person\n"
                + "  name : String\n"
                + "  age : int\n"
                + "end\n"
                + "declare enum Color\n"
                + "  RED, GREEN, BLUE;\n"
                + "end\n"
                + "query \"adults\"\n"
                + "  $p : Person(age >= 18)\n"
                + "end\n"
                + "rule \"R1\"\n"
                + "  when\n"
                + "    Person()\n"
                + "  then\n"
                + "end\n";

        List<DocumentSymbol> symbols = DRLDocumentSymbolHelper.symbols(drl);

        assertThat(symbols).extracting(DocumentSymbol::getName)
                .containsExactly("auditLog", "add", "Person", "Color", "adults", "R1");
        assertThat(symbols).extracting(DocumentSymbol::getKind)
                .containsExactly(SymbolKind.Variable, SymbolKind.Function, SymbolKind.Class,
                                 SymbolKind.Enum, SymbolKind.Method, SymbolKind.Method);
    }

    @Test
    void declareNestsFieldsAsChildren() {
        String drl = "declare Person\n"
                + "  name : String\n"
                + "  age : int\n"
                + "end\n";

        DocumentSymbol person = DRLDocumentSymbolHelper.symbols(drl).get(0);

        assertThat(person.getName()).isEqualTo("Person");
        assertThat(person.getKind()).isEqualTo(SymbolKind.Class);
        assertThat(person.getChildren()).extracting(DocumentSymbol::getName)
                .containsExactly("name", "age");
        assertThat(person.getChildren()).allSatisfy(c ->
                assertThat(c.getKind()).isEqualTo(SymbolKind.Field));
        assertThat(person.getChildren().get(0).getDetail()).isEqualTo("String");
    }

    @Test
    void enumNestsConstantsAsChildren() {
        String drl = "declare enum Color\n"
                + "  RED, GREEN, BLUE;\n"
                + "end\n";

        DocumentSymbol color = DRLDocumentSymbolHelper.symbols(drl).get(0);

        assertThat(color.getKind()).isEqualTo(SymbolKind.Enum);
        assertThat(color.getChildren()).extracting(DocumentSymbol::getName)
                .containsExactly("RED", "GREEN", "BLUE");
        assertThat(color.getChildren()).allSatisfy(c ->
                assertThat(c.getKind()).isEqualTo(SymbolKind.EnumMember));
    }

    @Test
    void selectionRangeIsWithinFullRange() {
        String drl = "rule \"R1\"\n  when\n  then\nend\n";

        DocumentSymbol rule = DRLDocumentSymbolHelper.symbols(drl).get(0);

        // Full range spans the rule block; selection range marks the name and
        // sits inside it (LSP requirement).
        assertThat(rule.getRange().getStart().getLine()).isEqualTo(0);
        assertThat(rule.getSelectionRange().getStart().getLine())
                .isGreaterThanOrEqualTo(rule.getRange().getStart().getLine());
        assertThat(rule.getRange().getEnd().getLine())
                .isGreaterThan(rule.getRange().getStart().getLine());
    }
}
