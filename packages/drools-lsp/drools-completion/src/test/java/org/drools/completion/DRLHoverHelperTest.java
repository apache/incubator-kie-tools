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
import java.util.List;
import java.util.Map;

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

    @Test
    void hoverOnDeclareExtendingAJavaClassShowsTheInheritedJavaFields() {
        String drl = """
                package demo;

                import org.drools.completion.fixtures.Pet;

                declare SpecialPet extends Pet
                  nickname : String
                end

                rule R
                  when
                    SpecialPet( )
                  then
                end
                """;
        ClassMemberIndex memberIndex = new ClassMemberIndex(getClass().getClassLoader());
        ClasspathTypeMembers.install(name -> "Pet".equals(name)
                ? memberIndex.membersOf("org.drools.completion.fixtures.Pet")
                : List.of());
        try {
            // Caret on the declare's own name (line 4: "declare SpecialPet extends Pet").
            Hover hover = DRLHoverHelper.hover(drl, new Position(4, 10),
                    ClassIndex.empty(), memberIndex, null);

            String md = content(hover);
            assertThat(md).contains("nickname");
            // Inherited from the Java supertype, which the declared-type index
            // cannot describe on its own.
            assertThat(md).contains("legs").contains("name").contains("friendly");
        } finally {
            ClasspathTypeMembers.install(null);
        }
    }

    @Test
    void hoverOnFullyQualifiedTypeNameResolvesTheType() {
        String drl = """
                package demo;

                rule R
                  when
                    org.drools.completion.fixtures.Pet( )
                  then
                end
                """;
        ClassIndex classIndex = ClassIndex.of(
                Map.of("Pet", List.of("org.drools.completion.fixtures.Pet")));
        ClassMemberIndex memberIndex = new ClassMemberIndex(getClass().getClassLoader());

        // Caret on the "Pet" segment of the qualified name.
        Hover hover = DRLHoverHelper.hover(drl, new Position(4, 36), classIndex, memberIndex, null);

        String md = content(hover);
        assertThat(md).contains("org.drools.completion.fixtures.Pet");
        assertThat(md).contains("name").contains("legs");
    }

    /**
     * The package segments of a qualified name resolve to nothing on their own,
     * so the chain walk cannot start from one. Any segment of the name describes
     * the type the whole name identifies.
     */
    @Test
    void hoverOnAPackageSegmentOfAQualifiedNameStillDescribesTheType() {
        String drl = """
                package demo;

                rule R
                  when
                    org.drools.completion.fixtures.Pet( )
                  then
                end
                """;
        ClassIndex classIndex = ClassIndex.of(
                Map.of("Pet", List.of("org.drools.completion.fixtures.Pet")));
        ClassMemberIndex memberIndex = new ClassMemberIndex(getClass().getClassLoader());

        // Caret on "org", the first package segment.
        Hover hover = DRLHoverHelper.hover(drl, new Position(4, 5), classIndex, memberIndex, null);

        assertThat(content(hover)).contains("org.drools.completion.fixtures.Pet");
    }

    @Test
    void hoverOnClasspathTypeShowsFieldsGettersAndConstructorsAsSections() {
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
        int fieldsAt = md.indexOf("**Fields**");
        int gettersAt = md.indexOf("**Getters**");
        int constructorsAt = md.indexOf("**Constructors**");
        assertThat(fieldsAt).isPositive();
        assertThat(gettersAt).isGreaterThan(fieldsAt);
        assertThat(constructorsAt).isGreaterThan(gettersAt);
        assertThat(md).contains("- legs : int");
        assertThat(md).contains("- name : String");
        assertThat(md).contains("- `Pet()`");
    }

    @Test
    void hoverOnClasspathTypeWithNoMembersOrConstructorsRendersHeaderOnly() {
        // java.lang.Math: only static fields/methods, so membersOf is empty,
        // and its sole constructor is private, so constructorsOf is empty too.
        String drl = """
                package demo;

                rule R
                  when
                    Math()
                  then
                end
                """;
        ClassMemberIndex memberIndex = new ClassMemberIndex(getClass().getClassLoader());

        Hover hover = DRLHoverHelper.hover(drl, new Position(4, 4),
                ClassIndex.empty(), memberIndex, null);

        String md = content(hover);
        assertThat(md).contains("java.lang.Math");
        assertThat(md).doesNotContain("**Fields**")
                .doesNotContain("**Getters**")
                .doesNotContain("**Constructors**")
                .doesNotContain("**Constants**");
    }

    @Test
    void hoverOnClasspathEnumShowsConstantsSection() {
        String drl = """
                package demo;

                import org.drools.completion.fixtures.PetKind;

                rule R
                  when
                    PetKind( )
                  then
                end
                """;
        ClassMemberIndex memberIndex = new ClassMemberIndex(getClass().getClassLoader());

        Hover hover = DRLHoverHelper.hover(drl, new Position(6, 9),
                ClassIndex.empty(), memberIndex, null);

        String md = content(hover);
        // PetKind also picks up "declaringClass" as an inherited Enum getter,
        // so only Fields (no FIELD-origin members exist) is asserted absent.
        assertThat(md).contains("**Constants**");
        assertThat(md).contains("- CAT");
        assertThat(md).contains("- DOG");
        assertThat(md).doesNotContain("**Fields**");
        assertThat(md.indexOf("**Constants**")).isLessThan(md.indexOf("**Getters**"));
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
    void hoverOnDocumentedFunctionShowsItsDocComment() {
        String drl = """
                package org.example;
                /**
                 * Computes the risk score for a finding.
                 */
                function int riskScore(int base) {
                    return base * 2;
                }
                rule R
                    when
                    then
                        int x = riskScore(1);
                end
                """;
        // Caret on "riskScore" in the call on the RHS (line 10, 0-based).
        Hover hover = DRLHoverHelper.hover(drl, new Position(10, 17),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(content(hover)).contains("Computes the risk score for a finding.");
    }

    @Test
    void hoverOnDocumentedGlobalShowsItsDocComment() {
        String drl = """
                package org.example;
                /**
                 * Shared results collector.
                 */
                global java.util.List results;
                rule R
                    when
                    then
                        results.add("x");
                end
                """;
        // Caret on "results" in the RHS usage.
        Hover hover = DRLHoverHelper.hover(drl, new Position(8, 9),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(content(hover)).contains("Shared results collector.");
    }

    @Test
    void hoverOnDocumentedQueryShowsItsDocComment() {
        String drl = """
                package org.example;
                declare Person
                    name : String
                end
                /**
                 * All persons with the given name.
                 */
                query personsNamed(String n)
                    Person(name == n)
                end
                """;
        // Caret on "personsNamed" in the query header.
        Hover hover = DRLHoverHelper.hover(drl, new Position(7, 8),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(content(hover)).contains("All persons with the given name.");
    }

    @Test
    void undocumentedFunctionNameYieldsNoDocHover() {
        String drl = """
                package org.example;
                function int plain(int base) {
                    return base;
                }
                """;
        // Caret on "plain" in the function header; no preceding doc comment.
        Hover hover = DRLHoverHelper.hover(drl, new Position(1, 14),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(hover).isNull();
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

    private static final String ENUM_CHAIN_DRL = """
            package demo;

            declare enum Status
              ACTIVE, CLOSED;
            end

            declare Ticket
              status : Status
            end

            rule R
              when
                Ticket( status == Status.ACTIVE )
              then
            end
            """;

    @Test
    void hoverOnQualifiedEnumConstantShowsTheEnum() {
        // Caret on "ACTIVE" in "Status.ACTIVE": line 12 is
        // `    Ticket( status == Status.ACTIVE )`, "ACTIVE" spans cols 29-34.
        Hover hover = DRLHoverHelper.hover(ENUM_CHAIN_DRL, new Position(12, 30),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("Status.ACTIVE");
        assertThat(md).contains("declare enum Status");
        assertThat(md).contains("CLOSED");
    }

    @Test
    void hoverOnEnumQualifierShowsTheEnumItself() {
        // Caret on "Status" in "Status.ACTIVE" (cols 22-27 of line 12): the
        // enum's own render, not the constant header.
        Hover hover = DRLHoverHelper.hover(ENUM_CHAIN_DRL, new Position(12, 23),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("declare enum Status");
        assertThat(md).doesNotContain("Status.ACTIVE");
    }

    @Test
    void hoverOnChainedFieldResolvesThroughTheBinding() {
        String drl = """
                package demo;

                declare Order
                  total : double
                end

                declare Line
                  amount : double
                end

                rule R
                  when
                    $o : Order( )
                    Line( amount > $o.total )
                  then
                end
                """;
        // Caret on "total" in "$o.total": line 13 is
        // `    Line( amount > $o.total )`, "total" spans cols 22-26.
        Hover hover = DRLHoverHelper.hover(drl, new Position(13, 23),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("**total**").contains("double");
        assertThat(md).contains("Field of `Order`");
    }

    @Test
    void hoverMidChainSegmentRendersThatSegment() {
        String drl = """
                package demo;

                declare Inner
                  c : int
                end

                declare Outer
                  b : Inner
                end

                rule R
                  when
                    $a : Outer( )
                    Inner( c > $a.b.c )
                  then
                end
                """;
        // Caret on "b" in "$a.b.c": line 13 is `    Inner( c > $a.b.c )`,
        // "b" is col 18. The hover renders b (type Inner, owner Outer), not c.
        Hover hover = DRLHoverHelper.hover(drl, new Position(13, 18),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("**b**").contains("Inner");
        assertThat(md).contains("Field of `Outer`");
        assertThat(md).doesNotContain("**c**").doesNotContain("int");
    }

    @Test
    void unresolvableChainYieldsNull() {
        // `$nope` binds nothing, so the chain must not resolve — and must not
        // fall back to hovering "age" as a field of the enclosing Person
        // pattern (here it is $nope's member, not Person's).
        String drl = """
                package demo;

                declare Person
                  age : int
                end

                rule R
                  when
                    Person( age > $nope.age )
                  then
                end
                """;
        // Caret on "age" in "$nope.age": line 8 is
        // `    Person( age > $nope.age )`, the chained "age" spans cols 24-26.
        assertThat(DRLHoverHelper.hover(drl, new Position(8, 25),
                ClassIndex.empty(), ClassMemberIndex.empty(), null)).isNull();
    }

    private static final String ACCUMULATE_DRL = """
            package demo;

            declare Person
              name : String
            end

            rule R
              when
                accumulate( $p : Person(); $c : count() )
              then
            end
            """;

    @Test
    void hoverOnAccumulateFunctionShowsResultType() {
        // Caret on "count" in "$c : count()": line 8 is
        // `    accumulate( $p : Person(); $c : count() )`, "count" spans
        // cols 36-40.
        Hover hover = DRLHoverHelper.hover(ACCUMULATE_DRL, new Position(8, 38),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("count : Long");
        assertThat(md).contains("accumulate function");
    }

    private static final String COUNT_FIELD_DRL = """
            package demo;

            declare Bucket
              count : int
            end

            rule R
              when
                Bucket( count > 0 )
              then
            end
            """;

    @Test
    void countOutsideAccumulateIsNotAFunctionHover() {
        // "count" here is a plain field of Bucket, not an accumulate function:
        // line 8 is `    Bucket( count > 0 )`, "count" spans cols 12-16. It must
        // render as a field, never as the accumulate-function hover.
        Hover hover = DRLHoverHelper.hover(COUNT_FIELD_DRL, new Position(8, 14),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("count").contains("int");
        assertThat(md).doesNotContain("accumulate function");
    }

    private static final String COLLECT_ARG_DRL = """
            package demo;

            declare Person
              name : String
            end

            rule R
              when
                accumulate( $p : Person(); $l : collectList($p) )
              then
            end
            """;

    @Test
    void hoverOnAccumulateArgumentResolvesAsTheBinding() {
        // Caret on "$p" inside "collectList($p)" (line 8, cols 48-49): the
        // argument must resolve through the binding step, never as the
        // function itself.
        Hover hover = DRLHoverHelper.hover(COLLECT_ARG_DRL, new Position(8, 48),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("declare Person");
        assertThat(md).doesNotContain("accumulate function");
    }

    private static final String SUM_ARG_DRL = """
            package demo;

            declare Person
              weight : int
            end

            rule R
              when
                accumulate( $p : Person(); $t : sum(sum) )
              then
            end
            """;

    @Test
    void accumulateArgumentTextEqualToTheFunctionNameIsNotAFunctionHover() {
        // Line 8 is `    accumulate( $p : Person(); $t : sum(sum) )`: the
        // function identifier spans cols 36-38, the argument cols 40-42. Only
        // the identifier itself may render the function hover; the argument
        // resolves as nothing here and must yield no hover at all.
        Hover hover = DRLHoverHelper.hover(SUM_ARG_DRL, new Position(8, 41),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(hover).isNull();
    }

    private static final String DOCUMENTED_SUM_DRL = """
            package demo;

            declare Person
              weight : int
            end

            /**
             * Adds two numbers the slow way.
             */
            function int sum(int a, int b) {
                return a + b;
            }

            rule R
              when
                accumulate( Person( $w : weight ); $t : sum($w) )
              then
            end
            """;

    @Test
    void accumulateUsageOutranksASameNamedDocumentedFunction() {
        // Line 15 is `    accumulate( Person( $w : weight ); $t : sum($w) )`;
        // the accumulate identifier "sum" spans cols 44-46. At that position
        // the name is structurally an accumulate function, so the hover must
        // show the function's result type — not the doc of the DRL function
        // that happens to share its name.
        Hover hover = DRLHoverHelper.hover(DOCUMENTED_SUM_DRL, new Position(15, 45),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("sum : Double").contains("accumulate function");
        assertThat(md).doesNotContain("slow way");
    }

    @Test
    void documentedFunctionStillShowsItsDocOutsideAccumulate() {
        // Line 9 is `function int sum(int a, int b) {`; "sum" spans cols
        // 13-15. Outside an accumulate span the doc-comment hover applies.
        Hover hover = DRLHoverHelper.hover(DOCUMENTED_SUM_DRL, new Position(9, 14),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(content(hover)).contains("Adds two numbers the slow way.");
    }

    @Test
    void hoverOnAccumulateResultBindingResolvesAsTheBinding() {
        // Caret on "$c" in "$c : count()" (line 8, cols 31-32): the label
        // sits inside the accumulateFunction node, but only the function
        // identifier itself may render the function hover — the label resolves
        // through the binding step to the function's result type.
        Hover hover = DRLHoverHelper.hover(ACCUMULATE_DRL, new Position(8, 31),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("Long");
        assertThat(md).doesNotContain("accumulate function");
    }

    private static final String BIG_DECIMAL_ACCUMULATE_DRL = """
            package demo;

            declare Person
              amount : java.math.BigDecimal
            end

            rule R
              when
                accumulate( Person( $a : amount ); $m : maxBD($a) )
              then
            end
            """;

    @Test
    void hoverOnABigDecimalAccumulateFunctionShowsItsResultType() {
        // Line 8 is `    accumulate( Person( $a : amount ); $m : maxBD($a) )`;
        // the function identifier "maxBD" spans cols 44-48.
        Hover hover = DRLHoverHelper.hover(BIG_DECIMAL_ACCUMULATE_DRL, new Position(8, 46),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("maxBD : BigDecimal").contains("accumulate function");
    }

    @Test
    void hoverOnABindingUsedInTheConsequenceIgnoresAConditionCommentSayingThen() {
        String drl = """
                package demo;

                global java.util.List results;

                declare Fact
                  code : String
                end

                rule R
                  when
                    Fact( $first : code )
                    // match this and then the other
                    Fact( $second : code )
                  then
                    results.add($second);
                end
                """;
        // Line 14 is `    results.add($second);`; "$second" spans cols 16-22.
        Hover hover = DRLHoverHelper.hover(drl, new Position(14, 18),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(content(hover)).contains("String");
    }

    private static final String PRIMITIVE_BINDING_DRL = """
            package demo;

            global java.util.List results;

            declare Fact
              size : int
            end

            rule R
              when
                Fact( $size : size )
              then
                results.add($size);
            end
            """;

    @Test
    void hoverOnAPrimitiveTypedBindingNamesItsType() {
        // Line 10 is `    Fact( $size : size )`; the binding spans cols 10-14.
        Hover hover = DRLHoverHelper.hover(PRIMITIVE_BINDING_DRL, new Position(10, 12),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(content(hover)).contains("$size").contains("int");
    }

    @Test
    void hoverOnAPrimitiveTypedBindingInTheConsequenceNamesItsType() {
        // Line 12 is `    results.add($size);`; the binding spans cols 16-20.
        Hover hover = DRLHoverHelper.hover(PRIMITIVE_BINDING_DRL, new Position(12, 18),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        assertThat(content(hover)).contains("$size").contains("int");
    }

    @Test
    void aDocumentedGlobalDoesNotShadowAFieldOfTheSameName() {
        String drl = """
                package demo;

                /** Running order total. */
                global java.math.BigDecimal total

                declare Order
                  total : int
                end

                rule R
                  when
                    Order( total > 5 )
                  then
                end
                """;
        // Line 11 is `    Order( total > 5 )`; "total" spans cols 11-15. The
        // doc-comment parser maps names document-wide, with no position scoping,
        // so the global's doc must not answer for the field in the constraint.
        Hover hover = DRLHoverHelper.hover(drl, new Position(11, 13),
                ClassIndex.empty(), ClassMemberIndex.empty(), null);

        String md = content(hover);
        assertThat(md).contains("total").contains("int").contains("Order");
        assertThat(md).doesNotContain("Running order total.");
    }
}
