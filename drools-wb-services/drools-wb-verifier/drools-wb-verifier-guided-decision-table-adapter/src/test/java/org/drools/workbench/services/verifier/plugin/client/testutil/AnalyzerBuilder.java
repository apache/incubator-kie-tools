/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.plugin.client.testutil;

import java.util.ArrayList;

import org.drools.verifier.core.main.Analyzer;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.DataType;

public class AnalyzerBuilder
        extends ExtendedGuidedDecisionTableBuilder {

    private AnalyzerProvider analyzerProvider;

    public AnalyzerBuilder(final AnalyzerProvider analyzerProvider) {
        super("org.test",
              new ArrayList<Import>(),
              "mytable");
        this.analyzerProvider = analyzerProvider;
    }

    public Analyzer buildAnalyzer() {
        return analyzerProvider.getAnalyser(buildTable());
    }

    @Override
    public AnalyzerBuilder withConditionBRLColumn() {
        return (AnalyzerBuilder) super.withConditionBRLColumn();
    }

    @Override
    public AnalyzerBuilder withActionBRLFragment() {
        return (AnalyzerBuilder) super.withActionBRLFragment();
    }

    @Override
    public AnalyzerBuilder withNumericColumn(final String boundName,
                                             final String factType,
                                             final String field,
                                             final String operator) {
        return (AnalyzerBuilder) super.withNumericColumn(boundName,
                                                         factType,
                                                         field,
                                                         operator);
    }

    @Override
    public AnalyzerBuilder withConditionBooleanColumn(final String boundName,
                                                      final String factType,
                                                      final String field,
                                                      final String operator) {
        return (AnalyzerBuilder) super.withConditionBooleanColumn(boundName,
                                                                  factType,
                                                                  field,
                                                                  operator);
    }

    @Override
    public AnalyzerBuilder withConditionDoubleColumn(final String boundName,
                                                     final String factType,
                                                     final String field,
                                                     final String operator) {
        return (AnalyzerBuilder) super.withConditionDoubleColumn(boundName,
                                                                 factType,
                                                                 field,
                                                                 operator);
    }

    @Override
    public AnalyzerBuilder withNoOperatorConditionIntegerColumn(final String boundName,
                                                                final String factType,
                                                                final String field) {
        return (AnalyzerBuilder) super.withNoOperatorConditionIntegerColumn(boundName,
                                                                            factType,
                                                                            field);
    }

    @Override
    public AnalyzerBuilder withConditionIntegerColumn(final String boundName,
                                                      final String factType,
                                                      final String field,
                                                      final String operator) {
        return (AnalyzerBuilder) super.withConditionIntegerColumn(boundName,
                                                                  factType,
                                                                  field,
                                                                  operator);
    }

    @Override
    public AnalyzerBuilder withStringColumn(final String boundName,
                                            final String factType,
                                            final String field,
                                            final String operator) {
        return (AnalyzerBuilder) super.withStringColumn(boundName,
                                                        factType,
                                                        field,
                                                        operator);
    }

    @Override
    public AnalyzerBuilder withEnumColumn(final String boundName,
                                          final String factType,
                                          final String field,
                                          final String operator,
                                          final String valueList) {
        return (AnalyzerBuilder) super.withEnumColumn(boundName,
                                                      factType,
                                                      field,
                                                      operator,
                                                      valueList);
    }

    @Override
    public AnalyzerBuilder withActionSetField(final String boundName,
                                              final String factField,
                                              final String typeNumericInteger) {
        return (AnalyzerBuilder) super.withActionSetField(boundName,
                                                          factField,
                                                          typeNumericInteger);
    }

    @Override
    public AnalyzerBuilder withActionInsertFact(final String factType,
                                                final String boundName,
                                                final String factField,
                                                final String typeNumericInteger) {
        return (AnalyzerBuilder) super.withActionInsertFact(factType,
                                                            boundName,
                                                            factField,
                                                            typeNumericInteger);
    }

    @Override
    public AnalyzerBuilder withData(final Object[][] data) {
        return (AnalyzerBuilder) super.withData(data);
    }

    public AnalyzerBuilder withAttributeColumn(final Attribute attribute) {
        return (AnalyzerBuilder)super.withAttributeColumn(attribute);
    }

    public AnalyzerBuilder withApplicationApprovedColumn(final String operator) {
        return withConditionBooleanColumn("a",
                                          "Application",
                                          "approved",
                                          operator);
    }

    public AnalyzerBuilder withApplicationApprovedSetField() {
        return withActionSetField("a",
                                  "approved",
                                  DataType.TYPE_BOOLEAN);
    }

    public AnalyzerBuilder withPersonAgeColumn(final String operator) {
        return withConditionIntegerColumn("a",
                                          "Person",
                                          "age",
                                          operator);
    }

    public AnalyzerBuilder withAccountDepositColumn(final String operator) {
        return withConditionDoubleColumn("d",
                                         "Account",
                                         "deposit",
                                         operator);
    }

    public AnalyzerBuilder withPersonNameColumn(final String operator) {
        return withStringColumn("a",
                                "Person",
                                "name",
                                operator);
    }

    public AnalyzerBuilder withPersonLastNameColumn(final String operator) {
        return withConditionIntegerColumn("a",
                                          "Person",
                                          "lastName",
                                          operator);
    }

    public AnalyzerBuilder withPersonApprovedActionInsertFact() {
        return withActionInsertFact("Person",
                                    "a",
                                    "approved",
                                    DataType.TYPE_STRING);
    }

    public AnalyzerBuilder withPersonApprovedActionSetField() {
        return withActionSetField("a",
                                  "approved",
                                  DataType.TYPE_STRING);
    }

    public AnalyzerBuilder withPersonSalarySetFieldAction() {
        return withActionSetField("a",
                                  "salary",
                                  DataType.TYPE_NUMERIC_INTEGER);
    }

    public AnalyzerBuilder withPersonDescriptionSetActionField() {
        return withActionSetField("a",
                                  "description",
                                  DataType.TYPE_STRING);
    }

    public AnalyzerBuilder withPersonApprovedColumn(final String operator) {
        return withConditionBooleanColumn("a",
                                          "Person",
                                          "approved",
                                          operator);
    }

    public Conditions conditionColumn() {
        return new Conditions();
    }

    public class Conditions {

        public PersonFactType person(final String variableName) {
            return new PersonFactType(variableName);
        }
    }

    public class PersonFactType {

        private final String variableName;

        public PersonFactType(final String variableName) {
            this.variableName = variableName;
        }

        public AbstractDecisionTableBuilder age(final String operator) {
            return withConditionIntegerColumn(variableName,
                                              "Person",
                                              "age",
                                              operator);
        }
    }
}
