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

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class DRLHoverHelperTest {

    private static final String DECLARE_DRL = """
            package demo;

            /** A person known to the rules. */
            declare Person
              name : String
              age : int
            end

            rule R
              when
                Person( name == "x" )
              then
            end
            """;

    private static String content(Hover hover) {
        assertThat(hover).isNotNull();
        return hover.getContents().getRight().getValue();
    }

    @Test
    void hoverParsesTheCurrentDocumentOnce() {
        // Declared-type hover (the path that also reads doc + link targets).
        DRLParsers.resetParseCount();
        DRLHoverHelper.hover(DECLARE_DRL, new Position(10, 6),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);
        assertThat(DRLParsers.parseCount()).isEqualTo(1);
    }

    @Test
    void hoverOnDeclaredTypeShowsFieldsAndDoc() {
        // Caret on "Person" in the pattern.
        Hover hover = DRLHoverHelper.hover(DECLARE_DRL, new Position(10, 6),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("declare Person");
        assertThat(md).contains("name : String");
        assertThat(md).contains("age : int");
        assertThat(md).contains("A person known to the rules.");
    }

    @Test
    void hoverDocCommentExpandsJavadocInlineTags() {
        String drl = """
                package demo;

                /** Tracks {@code name} values. */
                declare Person
                  name : String
                end

                rule R
                  when
                    Person( )
                  then
                end
                """;
        Hover hover = DRLHoverHelper.hover(drl, new Position(9, 6),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(content(hover)).contains("Tracks `name` values.");
    }

    @Test
    void hoverDocLinkResolvesToDeclarationLink(@TempDir Path dir) throws Exception {
        Path current = dir.resolve("rules.drl");
        // "Address" is declared on line 2, so its link anchor is #L3.
        String drl = """
                package demo;

                declare Address
                  code : String
                end

                /** Lives at an {@link Address}. */
                declare Person
                  name : String
                end

                rule R
                  when
                    Person( )
                  then
                end
                """;
        Files.writeString(current, drl);

        // Caret on "Person" in the pattern.
        Hover hover = DRLHoverHelper.hover(drl, new Position(13, 6),
                ClassIndex.empty(), ClassMemberIndex.empty(), current);

        assertThat(content(hover))
                .contains("[Address](" + current.toUri() + "#L3)");
    }

    @Test
    void hoverOnFieldShowsItsTypeAndOwner() {
        // Caret on "name" inside the constraint.
        Hover hover = DRLHoverHelper.hover(DECLARE_DRL, new Position(10, 13),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("name");
        assertThat(md).contains("String");
        assertThat(md).contains("Person");
    }

    @Test
    void hoverOnSiblingDeclaredType(@TempDir Path dir) throws Exception {
        Files.writeString(dir.resolve("Types.drl"),
                "package demo;\ndeclare Address\n  code : String\nend\n");
        Path current = dir.resolve("rules.drl");
        String drl = "package demo;\nrule R\n  when\n    Address( )\n  then\nend\n";
        Files.writeString(current, drl);

        Hover hover = DRLHoverHelper.hover(drl, new Position(3, 6),
                ClassIndex.empty(), ClassMemberIndex.empty(), current);

        assertThat(content(hover)).contains("declare Address").contains("code : String");
    }

    @Test
    void hoverOnClasspathTypeViaImport() {
        String drl = """
                package demo;

                import org.drools.completion.fixtures.Pet;

                rule R
                  when
                    Pet( )
                  then
                end
                """;
        ClassMemberIndex memberIndex = new ClassMemberIndex(getClass().getClassLoader());

        Hover hover = DRLHoverHelper.hover(drl, new Position(6, 5),
                ClassIndex.empty(), memberIndex, null);

        String md = content(hover);
        assertThat(md).contains("org.drools.completion.fixtures.Pet");
        assertThat(md).contains("name").contains("friendly").contains("legs");
    }

    private static final String EXTENDS_DRL = """
            package demo;

            declare Person
              name : String
            end

            declare Employee extends Person
              salary : double
            end

            rule R
              when
                Employee( name == "x" )
              then
            end
            """;

    @Test
    void hoverOnDeclaredTypeShowsInheritedFields() {
        // Caret on "Employee" in the pattern.
        Hover hover = DRLHoverHelper.hover(EXTENDS_DRL, new Position(12, 6),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("declare Employee extends Person");
        assertThat(md).contains("salary : double");
        assertThat(md).contains("name : String"); // inherited
    }

    @Test
    void hoverOnInheritedFieldResolvesThroughTheParent() {
        // Caret on "name" — declared on Person, used in an Employee pattern.
        Hover hover = DRLHoverHelper.hover(EXTENDS_DRL, new Position(12, 15),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("name").contains("String");
    }

    @Test
    void hoverOnUnknownSymbolReturnsNull() {
        assertThat(DRLHoverHelper.hover(DECLARE_DRL, new Position(7, 3),
                ClassIndex.empty(), ClassMemberIndex.empty(), null)).isNull();
    }

    @Test
    void hoverOnNullTextReturnsNull() {
        assertThat(DRLHoverHelper.hover(null, new Position(0, 0),
                ClassIndex.empty(), ClassMemberIndex.empty(), null)).isNull();
    }

    @Test
    void hoverOnBoundVariableResolvesToItsType() {
        String drl = """
                package demo;

                declare Person
                  name : String
                end

                rule R
                  when
                    $p : Person( name == "x" )
                  then
                end
                """;
        // Caret on "$p".
        Hover hover = DRLHoverHelper.hover(drl, new Position(8, 4),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("declare Person").contains("name : String");
    }

    @Test
    void hoverOnFieldBoundVariableResolvesViaBindingEngine() {
        // $ref is bound to the `ref` field (type QuestionRef), not an explicit
        // `$ref : QuestionRef(...)` pattern. The old regex couldn't resolve this;
        // routing through LhsBindingResolver now shows QuestionRef's structure.
        String drl = """
                package demo;

                declare QuestionRef
                  order : int
                end

                declare LesionState
                  ref : QuestionRef
                end

                rule R
                  when
                    LesionState( $ref : ref )
                  then
                    use($ref);
                end
                """;
        // Caret on "$ref" in the RHS usage (line 14).
        Hover hover = DRLHoverHelper.hover(drl, new Position(14, 9),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("declare QuestionRef").contains("order : int");
    }

    @Test
    void hoverOnReusedBindingNameResolvesToTheRuleUnderTheCaret() {
        // `$p` is bound to a different type in each rule. Hovering `$p` in the
        // first rule must show Person (the rule under the caret), not Account
        // (the last rule in the file, which whole-file merging would win).
        String drl = """
                package demo;

                declare Person
                  name : String
                end

                declare Account
                  balance : int
                end

                rule R1
                  when
                    $p : Person( name == "x" )
                  then
                    use($p);
                end

                rule R2
                  when
                    $p : Account( balance > 0 )
                  then
                    use($p);
                end
                """;
        // Caret on "$p" in R1's RHS usage: line 14 `    use($p);`, col 9 = 'p'.
        Hover hover = DRLHoverHelper.hover(drl, new Position(14, 9),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("declare Person").contains("name : String");
        assertThat(md).doesNotContain("Account");
    }

    @Test
    void hoverOnJavaLangTypeResolvesWithoutImport() {
        // java.lang.Object is implicitly available and has no bean getters, yet
        // the FQN header should still render (the members-empty guard is gone).
        String drl = """
                package demo;

                rule R
                  when
                    Object()
                  then
                end
                """;
        Hover hover = DRLHoverHelper.hover(drl, new Position(4, 4),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(content(hover)).contains("java.lang.Object");
    }
}
