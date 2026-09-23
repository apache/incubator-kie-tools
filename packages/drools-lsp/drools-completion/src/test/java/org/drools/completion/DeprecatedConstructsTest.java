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

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Deprecated constructs the engine's legacy parser still compiles. Most
 * fixtures are parse failures for the DRL10 grammar — which is why the raw
 * message needs replacing — so they also pin that recognition survives whatever
 * the parser's recovery does around them. Custom-operator cases declare their
 * operator the way a project does, through {@code META-INF/kie.properties.conf}
 * on a classpath entry; the operator cache is JVM-wide, so the id stays
 * registered for the rest of the run.
 */
class DeprecatedConstructsTest {

    private static final String HEAD =
            "package p;\ndeclare Person\n  name : String\n  age : int\nend\n";

    private static void declareCustomOperator(Path dir, String id) throws Exception {
        CustomOperators.register(Set.of(
                CustomOperatorsTest.classDirDeclaring(dir, "META-INF/kie.properties.conf", id)));
    }

    @BeforeEach
    @AfterEach
    void reset() {
        System.clearProperty(DeprecatedConstructs.PROP_DEPRECATED);
    }

    private static List<Diagnostic> diagnose(String drl) {
        return DRLDiagnosticHelper.parse(drl).diagnostics;
    }

    private static Diagnostic deprecation(String drl) {
        List<Diagnostic> found = diagnose(drl).stream()
                .filter(d -> "drools-deprecated".equals(d.getSource()))
                .toList();
        assertThat(found).as("expected exactly one deprecation diagnostic in:\n" + drl).hasSize(1);
        return found.get(0);
    }

    @Test
    void namesAHalfConstraint() {
        Diagnostic d = deprecation(HEAD + "rule R when Person( age > 18 && < 65 ) then end\n");
        assertThat(d.getMessage()).contains("half constraint").contains("&& <")
                .contains("repeat the left operand");
        assertThat(d.getSeverity()).isEqualTo(DiagnosticSeverity.Warning);
    }

    @Test
    void namesAHalfConstraintAgainstABinding() {
        Diagnostic d = deprecation(HEAD
                + "rule R when $p : Person( age > 1 ) Person( age > 18 && < $p.age ) then end\n");
        assertThat(d.getMessage()).contains("half constraint");
    }

    @Test
    void namesPatternsConnectedWithAnd() {
        Diagnostic d = deprecation(HEAD
                + "rule R when Person( age > 1 ) && Person( name != null ) then end\n");
        assertThat(d.getMessage()).contains("connecting patterns with '&&'").contains("use 'and'");
    }

    @Test
    void namesPatternsConnectedWithOr() {
        Diagnostic d = deprecation(HEAD
                + "rule R when Person( age > 1 ) || Person( name != null ) then end\n");
        assertThat(d.getMessage()).contains("connecting patterns with '||'").contains("use 'or'");
    }

    @Test
    void namesAnAnnotationInsideAnLhsPattern() {
        Diagnostic d = deprecation(HEAD + "rule R when Person( @watch(age) age > 1 ) then end\n");
        assertThat(d.getMessage()).contains("annotations inside LHS patterns");
    }

    @Test
    void namesAgendaGroup() {
        Diagnostic d = deprecation(HEAD
                + "rule R\n  agenda-group \"g\"\n  when Person( age > 1 ) then end\n");
        assertThat(d.getMessage()).contains("'agenda-group'").contains("ruleflow-group");
    }

    @Test
    void namesACustomOperatorMissingItsPrefix(@TempDir Path dir) throws Exception {
        declareCustomOperator(dir, "customOp");

        Diagnostic d = deprecation(HEAD + "rule R when Person( name customOp \"x\" ) then end\n");
        assertThat(d.getMessage()).contains("custom operator 'customOp'").contains("##customOp");
    }

    /** An identifier no configuration declares is a typo until proven otherwise. */
    @Test
    void doesNotFlagAnUndeclaredIdentifierAsACustomOperator() {
        assertThat(diagnose(HEAD + "rule R when Person( name undeclaredOp \"x\" ) then end\n"))
                .isNotEmpty()
                .allSatisfy(d -> assertThat(d.getSource()).isNotEqualTo("drools-deprecated"));
    }

    /** The construct's line loses its follow-on parse errors; other lines keep theirs. */
    @Test
    void replacesTheParseErrorsOnTheConstructsLine() {
        List<Diagnostic> all = diagnose(HEAD + "rule R when Person( age > 18 && < 65 ) then end\n");
        assertThat(all).allSatisfy(d ->
                assertThat(d.getSource()).isEqualTo("drools-deprecated"));
    }

    @Test
    void severityIsConfigurableAndOffLeavesTheParseErrors() {
        String drl = HEAD + "rule R when Person( age > 18 && < 65 ) then end\n";

        System.setProperty(DeprecatedConstructs.PROP_DEPRECATED, "error");
        assertThat(deprecation(drl).getSeverity()).isEqualTo(DiagnosticSeverity.Error);

        System.setProperty(DeprecatedConstructs.PROP_DEPRECATED, "off");
        assertThat(diagnose(drl))
                .isNotEmpty()
                .allSatisfy(d -> assertThat(d.getSource()).isNotEqualTo("drools-deprecated"));
    }

    /** A mistyped constraint is not a deprecation: no operand precedes the literal. */
    @Test
    void doesNotMisreportAMistypedConstraintAsACustomOperator() {
        assertThat(diagnose(HEAD + "rule R when Person( age 18 ) then end\n"))
                .as("a missing operator should stay a syntax error")
                .allSatisfy(d -> assertThat(d.getSource()).isNotEqualTo("drools-deprecated"));
    }

    /** Real rules span lines; the error need not land on the construct's line. */
    @Test
    void namesAHalfConstraintInAMultiLineRule() {
        Diagnostic d = deprecation(HEAD + """
                rule R
                  when
                    Person(
                      age > 18
                      && < 65,
                      name != null
                    )
                  then
                end
                """);
        assertThat(d.getMessage()).contains("half constraint");
    }

    /**
     * A half constraint whose operands are bound paths parses cleanly under the
     * DRL10 grammar, so there is no parse error to replace — detection must not
     * depend on one, or the construct stays invisible in exactly the files that
     * contain it.
     */
    @Test
    void namesAHalfConstraintInADocumentThatParsesCleanly() {
        String drl = """
                package p;
                rule R
                  when
                    $ref : Fact( order > 0 )
                    Fact( ref.order > $ref.order && < $presentOrder )
                  then
                end
                """;
        assertThat(DRLDiagnosticHelper.parse(drl).diagnostics)
                .as("the grammar accepts this, so any parse error would invalidate the premise")
                .allSatisfy(d -> assertThat(d.getSource()).isEqualTo("drools-deprecated"));
        assertThat(deprecation(drl).getMessage()).contains("half constraint");
    }

    /** Parenthesised constraints are not pattern connectives. */
    @Test
    void doesNotFlagParenthesisedConstraintsAsPatternConnectives() {
        assertThat(diagnose(HEAD
                + "rule R when Person( (age > 1) && (age < 5) ) then end\n"))
                .as("&& inside a constraint list is ordinary DRL")
                .isEmpty();
    }

    /** Rule-level annotations sit outside the when section and are not deprecated. */
    @Test
    void doesNotFlagRuleLevelAnnotations() {
        assertThat(diagnose(HEAD
                + "rule R\n  @implements(\"X-1\")\n  when Person( age > 1 ) then end\n"))
                .isEmpty();
    }

    /**
     * The half-constraint fix repeats the operand the preceding comparison used,
     * so applying it to the diagnostic's range yields valid DRL10.
     */
    @Test
    void halfConstraintFixRepeatsTheLeftOperand() {
        Diagnostic d = deprecation("""
                package p;
                rule R
                  when
                    $fact : Fact( order > 0 )
                    Fact( order > $fact.order && < $presentOrder )
                  then
                end
                """);
        assertThat(d.getData()).isEqualTo("&& order");
    }

    @Test
    void patternConnectiveFixIsTheKeyword() {
        assertThat(deprecation(HEAD
                + "rule R when Person( age > 1 ) && Person( name != null ) then end\n")
                .getData()).isEqualTo("and");
        assertThat(deprecation(HEAD
                + "rule R when Person( age > 1 ) || Person( name != null ) then end\n")
                .getData()).isEqualTo("or");
    }

    @Test
    void customOperatorFixAddsThePrefix(@TempDir Path dir) throws Exception {
        declareCustomOperator(dir, "customOp");

        assertThat(deprecation(HEAD + "rule R when Person( name customOp \"x\" ) then end\n")
                .getData()).isEqualTo("##customOp");
    }

    /** Constructs needing a human decision or a rulebase-wide pass offer no fix. */
    @Test
    void noFixIsOfferedForAnnotationsOrAgendaGroup() {
        assertThat(deprecation(HEAD + "rule R when Person( @watch(age) age > 1 ) then end\n")
                .getData()).isNull();
        assertThat(deprecation(HEAD
                + "rule R\n  agenda-group \"g\"\n  when Person( age > 1 ) then end\n")
                .getData()).isNull();
    }

    /** No fix rather than a wrong one when the preceding comparison is unreadable. */
    @Test
    void halfConstraintWithoutAPrecedingComparisonOffersNoFix() {
        assertThat(deprecation(HEAD + "rule R when Person( && < 65 ) then end\n").getData())
                .isNull();
    }

    @Test
    void cleanDrlProducesNothing() {
        assertThat(diagnose(HEAD + "rule R when Person( age > 18 ) then end\n")).isEmpty();
    }
}
