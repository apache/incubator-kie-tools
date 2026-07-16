/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.antlr4;

import org.drools.drl.parser.antlr4.DRL10Parser.BooleanAttributeContext;
import org.drools.drl.parser.antlr4.DRL10Parser.CompilationUnitContext;
import org.drools.drl.parser.antlr4.DRL10Parser.ConstraintContext;
import org.drools.drl.parser.antlr4.DRL10Parser.ExpressionAttributeContext;
import org.drools.drl.parser.antlr4.DRL10Parser.ImportStandardDefContext;
import org.drools.drl.parser.antlr4.DRL10Parser.LabelContext;
import org.drools.drl.parser.antlr4.DRL10Parser.LhsPatternContext;
import org.drools.drl.parser.antlr4.DRL10Parser.LhsUnarySingleContext;
import org.drools.drl.parser.antlr4.DRL10Parser.PackagedefContext;
import org.drools.drl.parser.antlr4.DRL10Parser.RuledefContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is a quick test to check if the antlr4 parser is working.
 * The real tests are done in the Drools project.
 */
class SmokeTest {

    private static final String BASIC_DRL = """
            package org.test;
            import org.test.model.Person;
            global String result;
            rule TestRule
                no-loop
                salience 15
              when
                $p:Person( age >= 18 )
              then
                int a = 4;
                System.out.println($p.getName());
            end
            """;

    @Test
    void basicDrl() {
        DRL10Parser parser = DRLParserHelper.createDrlParser(BASIC_DRL);
        CompilationUnitContext compilationUnitContext = parser.compilationUnit();

        PackagedefContext packagedef = compilationUnitContext.packagedef();
        assertThat(packagedef.drlQualifiedName().getText()).isEqualTo("org.test");

        ImportStandardDefContext importdef = (ImportStandardDefContext) compilationUnitContext.drlStatementdef(0).importdef();
        assertThat(importdef.drlQualifiedName().getText()).isEqualTo("org.test.model.Person");

        DRL10Parser.GlobaldefContext globaldef = compilationUnitContext.drlStatementdef(1).globaldef();
        assertThat(globaldef.type().getText()).isEqualTo("String");
        assertThat(globaldef.drlIdentifier().getText()).isEqualTo("result");

        RuledefContext ruledef = compilationUnitContext.drlStatementdef(2).ruledef();
        assertThat(ruledef.name.getText()).isEqualTo("TestRule");

        BooleanAttributeContext booleanAttribute = (BooleanAttributeContext) ruledef.attributes().attribute(0);
        assertThat(booleanAttribute.name.getText()).isEqualTo("no-loop");
        ExpressionAttributeContext expressionAttribute = (ExpressionAttributeContext) ruledef.attributes().attribute(1);
        assertThat(expressionAttribute.name.getText()).isEqualTo("salience");
        assertThat(expressionAttribute.conditionalAttributeValue().getText()).isEqualTo("15");

        LhsUnarySingleContext lhsUnarySingleContext = (LhsUnarySingleContext) ruledef.lhs().lhsExpression(0);
        LabelContext label = lhsUnarySingleContext.lhsUnary().lhsPatternBind().label();
        assertThat(label.drlIdentifier().getText()).isEqualTo("$p");
        LhsPatternContext lhsPattern = lhsUnarySingleContext.lhsUnary().lhsPatternBind().lhsPattern(0);
        assertThat(lhsPattern.objectType.getText()).isEqualTo("Person");
        ConstraintContext constraint = lhsPattern.constraints().constraint(0);
        assertThat(constraint.getText()).isEqualTo("age>=18");

        assertThat(ruledef.rhs().consequenceBody().getText()).isEqualTo("inta=4;System.out.println($p.getName());");
    }
}
