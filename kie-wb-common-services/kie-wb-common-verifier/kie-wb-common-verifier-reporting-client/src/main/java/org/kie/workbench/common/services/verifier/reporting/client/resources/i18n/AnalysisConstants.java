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

package org.kie.workbench.common.services.verifier.reporting.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface AnalysisConstants
        extends
        Messages {

    public static final AnalysisConstants INSTANCE = GWT.create(AnalysisConstants.class);

    String ConflictingRows();

    String ImpossibleMatch();

    String RuleHasNoAction();

    String RuleHasNoRestrictionsAndWillAlwaysFire();

    String MultipleValuesForOneAction();

    String ConstrainsForFieldXOfFactYAreRedundantTheyWillAlwaysPass(String factField,
                                                                    String factType);

    String RedundantRows();

    String SubsumptantRows();

    String DeficientRow();

    String ValueForFactFieldIsSetTwice(String factName,
                                       String fieldName);

    String ValueForAnActionIsSetTwice();

    String RedundantConditionsNote1P1(String patternName,
                                      String factField);

    String AffectedRows();

    String WhenARuleHasNoActionItDoesFireButSinceTheActionSideIsEmptyNothingHappens();

    String ItIsPossibleThatTheActionsWereLeftOutByAccidentInThisCasePleaseAddThemOtherwiseTheRuleCanNeRemoved();

    String ConflictingRowsP1();

    String ConflictingRowsP2();

    String DeficientRowsP1();

    String DeficientRowsNoteP1();

    String Salary();

    String Savings();

    String ApproveLoan();

    String DeficientRowsP2();

    String ImpossibleMatchNote1P1(String rowNumber,
                                  String factField,
                                  String factType);

    String ImpossibleMatchNote1P2(String condition1,
                                  String condition2);

    String ImpossibleMatchP1(String factField);

    String MissingConditionP1();

    String MultipleValuesP1();

    String MultipleValuesNote1P1(String condition1,
                                 String condition2);

    String RedundantActionsP1();

    String RedundantActionsNote1P1(String condition1,
                                   String condition2);

    String RedundantConditionsTitle();

    String RedundantConditionsP1();

    String RedundantConditionsNote1P2(String condition1,
                                      String condition2);

    String RedundantRowsP1();

    String RedundantRowsP2();

    String RedundantRowsP3();

    String SubsumptantRowsP1();

    String SubsumptantRowsP2();

    String AnalysingChecks0To1Of2(int startIndex,
                                  int endIndex,
                                  int amountOfChecks);

    String MissingRangeTitle();

    String MissingRangeP1(int rowNumber);

    String SingleHitLost();

    String SingleHitP1(String row1,
                       String row2);

    String AnalysisComplete();

    String EmptyRule();

    String ProvideAtLeastOneConditionAndOneActionForTheRule();

    String Analysis();
}
