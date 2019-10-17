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

package org.kie.workbench.common.services.verifier.reporting.client.reporting;

import com.google.gwt.safehtml.shared.SafeHtml;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.ImpossibleMatchIssue;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.MultipleValuesForOneActionIssue;
import org.drools.verifier.api.reporting.RedundantConditionsIssue;
import org.drools.verifier.api.reporting.SingleHitLostIssue;
import org.drools.verifier.api.reporting.ValueForActionIsSetTwiceIssue;
import org.drools.verifier.api.reporting.ValueForFactFieldIsSetTwiceIssue;
import org.kie.workbench.common.services.verifier.reporting.client.resources.i18n.AnalysisConstants;

public class ExplanationProvider {

    public static SafeHtml toHTML(final Issue issue) {
        if (issue == Issue.EMPTY) {
            return new SafeHtml() {
                @Override
                public String asString() {
                    return "---";
                }
            };
        }

        switch (issue.getCheckType()) {
            case ILLEGAL_VERIFIER_STATE:
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.VerifierFailed())
                        .toHTML();

            case CONFLICTING_ROWS:
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.ConflictingRowsP1())
                        .addParagraph(AnalysisConstants.INSTANCE.ConflictingRowsP2())
                        .toHTML();

            case DEFICIENT_ROW:
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.DeficientRowsP1())
                        .startNote()
                        .addParagraph(AnalysisConstants.INSTANCE.DeficientRowsNoteP1())
                        .startExampleTable()
                        .startHeader()
                        .headerConditions(AnalysisConstants.INSTANCE.Salary(),
                                          AnalysisConstants.INSTANCE.Savings())
                        .headerActions(AnalysisConstants.INSTANCE.ApproveLoan())
                        .end()
                        .startRow()
                        .addConditions("--",
                                       "100 000")
                        .addActions("true")
                        .end()
                        .startRow()
                        .addConditions("30 000",
                                       "--")
                        .addActions("false")
                        .end()
                        .end()
                        .end()
                        .addParagraph(AnalysisConstants.INSTANCE.DeficientRowsP2())
                        .toHTML();

            case IMPOSSIBLE_MATCH:
                ImpossibleMatchIssue impossibleMatchIssue = (ImpossibleMatchIssue) issue;
                return new Explanation()
                        .startNote()
                        .addParagraph(
                                AnalysisConstants.INSTANCE.ImpossibleMatchNote1P1((impossibleMatchIssue.getRuleId()),
                                                                                  impossibleMatchIssue.getFieldName(),
                                                                                  impossibleMatchIssue.getFieldFactType()))
                        .addParagraph(AnalysisConstants.INSTANCE.ImpossibleMatchNote1P2(impossibleMatchIssue.getConflictedItem(),
                                                                                        impossibleMatchIssue.getConflictingItem()))
                        .end()
                        .addParagraph(AnalysisConstants.INSTANCE.ImpossibleMatchP1(impossibleMatchIssue.getFieldName()))
                        .toHTML();

            case MISSING_ACTION:
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.WhenARuleHasNoActionItDoesFireButSinceTheActionSideIsEmptyNothingHappens())
                        .addParagraph(AnalysisConstants.INSTANCE.ItIsPossibleThatTheActionsWereLeftOutByAccidentInThisCasePleaseAddThemOtherwiseTheRuleCanNeRemoved())
                        .toHTML();
            case MISSING_RESTRICTION:
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.MissingConditionP1())
                        .toHTML();
            case MULTIPLE_VALUES_FOR_ONE_ACTION:
                MultipleValuesForOneActionIssue multipleValuesForOneActionIssue = (MultipleValuesForOneActionIssue) issue;
                return new Explanation()
                        .startNote()
                        .addParagraph(AnalysisConstants.INSTANCE.MultipleValuesNote1P1(multipleValuesForOneActionIssue.getConflictedItem(),
                                                                                       multipleValuesForOneActionIssue.getConflictingItem()))
                        .end()
                        .addParagraph(AnalysisConstants.INSTANCE.MultipleValuesP1())
                        .toHTML();

            case VALUE_FOR_FACT_FIELD_IS_SET_TWICE:
                ValueForFactFieldIsSetTwiceIssue valueForFactFieldIsSetTwiceIssue = (ValueForFactFieldIsSetTwiceIssue) issue;
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.RedundantActionsP1())
                        .startNote()
                        .addParagraph(AnalysisConstants.INSTANCE.RedundantActionsNote1P1(valueForFactFieldIsSetTwiceIssue.getFirstItem(),
                                                                                         valueForFactFieldIsSetTwiceIssue.getSecondItem()))
                        .end()
                        .toHTML();

            case VALUE_FOR_ACTION_IS_SET_TWICE:
                ValueForActionIsSetTwiceIssue valueForActionIsSetTwiceIssue = (ValueForActionIsSetTwiceIssue) issue;
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.RedundantActionsP1())
                        .startNote()
                        .addParagraph(AnalysisConstants.INSTANCE.RedundantActionsNote1P1(valueForActionIsSetTwiceIssue.getFirstItem(),
                                                                                         valueForActionIsSetTwiceIssue.getSecondItem()))
                        .end()
                        .toHTML();

            case REDUNDANT_CONDITIONS_TITLE:
                RedundantConditionsIssue redundantConditionsIssue = (RedundantConditionsIssue) issue;
                return new Explanation()
                        .startNote()
                        .addParagraph(AnalysisConstants.INSTANCE.RedundantConditionsNote1P1(redundantConditionsIssue.getFactType(),
                                                                                            redundantConditionsIssue.getName()))
                        .addParagraph(AnalysisConstants.INSTANCE.RedundantConditionsNote1P2(redundantConditionsIssue.getFirstItem(),
                                                                                            redundantConditionsIssue.getSecondItem()))
                        .end()
                        .addParagraph(AnalysisConstants.INSTANCE.RedundantConditionsP1())
                        .toHTML();

            case REDUNDANT_ROWS:
            case SUBSUMPTANT_ROWS:
                getExplanation(issue.getCheckType());

            case MISSING_RANGE:
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.MissingRangeP1(issue.getRowNumbers()
                                                                                        .iterator()
                                                                                        .next()))
                        .toHTML();

            case SINGLE_HIT_LOST:
                SingleHitLostIssue singleHitLostIssue = (SingleHitLostIssue) issue;
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.SingleHitP1(singleHitLostIssue.getFirstItem(),
                                                                             singleHitLostIssue.getSecondItem()))
                        .toHTML();
            case EMPTY_RULE:
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.ProvideAtLeastOneConditionAndOneActionForTheRule())
                        .toHTML();

            default:
                return new SafeHtml() {
                    @Override
                    public String asString() {
                        return "---";
                    }
                };
        }
    }

    private static Explanation getExplanation(final CheckType checkType) {
        switch (checkType) {
            case REDUNDANT_ROWS:
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.RedundantRowsP1())
                        .addParagraph(AnalysisConstants.INSTANCE.RedundantRowsP2())
                        .addParagraph(AnalysisConstants.INSTANCE.RedundantRowsP3());
            case SUBSUMPTANT_ROWS:
                return new Explanation()
                        .addParagraph(AnalysisConstants.INSTANCE.SubsumptantRowsP1())
                        .addParagraph(AnalysisConstants.INSTANCE.SubsumptantRowsP2());
            default:
                return new Explanation();
        }
    }

    public static String toTitle(final Issue issue) {
        if (issue == Issue.EMPTY) {
            return "---";
        }

        switch (issue.getCheckType()) {
            case ILLEGAL_VERIFIER_STATE:
                return AnalysisConstants.INSTANCE.VerifierFailedTitle();
            case CONFLICTING_ROWS:
                return AnalysisConstants.INSTANCE.ConflictingRows();
            case DEFICIENT_ROW:
                return AnalysisConstants.INSTANCE.DeficientRow();
            case IMPOSSIBLE_MATCH:
                return AnalysisConstants.INSTANCE.ImpossibleMatch();
            case MISSING_ACTION:
                return AnalysisConstants.INSTANCE.RuleHasNoAction();
            case MISSING_RESTRICTION:
                return AnalysisConstants.INSTANCE.RuleHasNoRestrictionsAndWillAlwaysFire();
            case MULTIPLE_VALUES_FOR_ONE_ACTION:
                return AnalysisConstants.INSTANCE.MultipleValuesForOneAction();
            case VALUE_FOR_FACT_FIELD_IS_SET_TWICE:
                ValueForFactFieldIsSetTwiceIssue valueForFactFieldIsSetTwiceIssue = (ValueForFactFieldIsSetTwiceIssue) issue;

                return AnalysisConstants.INSTANCE.ValueForFactFieldIsSetTwice(valueForFactFieldIsSetTwiceIssue.getBoundName(),
                                                                              valueForFactFieldIsSetTwiceIssue.getName());
            case VALUE_FOR_ACTION_IS_SET_TWICE:
                return AnalysisConstants.INSTANCE.ValueForAnActionIsSetTwice();
            case REDUNDANT_CONDITIONS_TITLE:
                return AnalysisConstants.INSTANCE.RedundantConditionsTitle();
            case REDUNDANT_ROWS:
                return AnalysisConstants.INSTANCE.RedundantRows();
            case SUBSUMPTANT_ROWS:
                return AnalysisConstants.INSTANCE.SubsumptantRows();
            case MISSING_RANGE:
                return AnalysisConstants.INSTANCE.MissingRangeTitle();
            case SINGLE_HIT_LOST:
                return AnalysisConstants.INSTANCE.SingleHitLost();
            case EMPTY_RULE:
                return AnalysisConstants.INSTANCE.EmptyRule();
            default:
                return "---";
        }
    }
}
