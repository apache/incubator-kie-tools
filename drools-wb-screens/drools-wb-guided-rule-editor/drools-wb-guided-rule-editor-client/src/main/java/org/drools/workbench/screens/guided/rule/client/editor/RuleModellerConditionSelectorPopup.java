/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Objects;
import java.util.function.Predicate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.rule.CompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromEntryPointFactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.uberfire.ext.widgets.common.client.common.InfoPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

/**
 * Pop-up for adding Conditions to the (RuleModeller) guided editor
 */
public class RuleModellerConditionSelectorPopup extends AbstractRuleModellerSelectorPopup {

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            selectSomething();
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    public RuleModellerConditionSelectorPopup(final RuleModel model,
                                              final RuleModeller ruleModeller,
                                              final Integer position,
                                              final AsyncPackageDataModelOracle oracle) {
        super(model,
              ruleModeller,
              position,
              oracle);

        add(new ModalBody() {{
            add(getContent());
        }});
        add(new ModalFooterOKCancelButtons(okCommand,
                                           cancelCommand));
    }

    @Override
    protected String getPopupTitle() {
        return GuidedRuleEditorResources.CONSTANTS.AddAConditionToTheRule();
    }

    @Override
    public Widget getContent() {
        if (position == null) {
            positionCbo.addItem(GuidedRuleEditorResources.CONSTANTS.Bottom(),
                                String.valueOf(this.model.lhs.length));
            positionCbo.addItem(GuidedRuleEditorResources.CONSTANTS.Top(),
                                "0");
            for (int i = 1; i < model.lhs.length; i++) {
                positionCbo.addItem(GuidedRuleEditorResources.CONSTANTS.Line0(i),
                                    String.valueOf(i));
            }
        } else {
            //if position is fixed, we just add one element to the drop down.
            positionCbo.addItem(String.valueOf(position));
            positionCbo.setSelectedIndex(0);
        }

        if (oracle.getDSLConditions().size() == 0 && oracle.getFactTypes().length == 0) {
            layoutPanel.addRow(new HTML("<div class='highlight'>" + GuidedRuleEditorResources.CONSTANTS.NoModelTip() + "</div>"));
        }

        //only show the drop down if we are not using fixed position.
        if (position == null) {
            HorizontalPanel hp0 = new HorizontalPanel();
            hp0.add(new HTML(GuidedRuleEditorResources.CONSTANTS.PositionColon()));
            hp0.add(positionCbo);
            hp0.add(new InfoPopup(GuidedRuleEditorResources.CONSTANTS.PositionColon(),
                                  GuidedRuleEditorResources.CONSTANTS.ConditionPositionExplanation()));
            layoutPanel.addRow(hp0);
            layoutPanel.addRow(new HTML("<hr/>"));
        }

        //Add a widget to filter DSLs if applicable
        final RuleModellerSelectorFilter filterWidget = GWT.create(RuleModellerSelectorFilter.class);
        filterWidget.setFilterChangeConsumer((filter) -> choicesPanel.setWidget(makeChoicesListBox(filter)));
        layoutPanel.add(filterWidget);

        choices = makeChoicesListBox(filterWidget.getFilterText());
        choicesPanel.add(choices);
        layoutPanel.addRow(choicesPanel);

        //DSL might be prohibited (e.g. editing a DRL file. Only DSLR files can contain DSL)
        if (ruleModeller.isDSLEnabled()) {
            CheckBox chkOnlyDisplayDSLConditions = new CheckBox();
            chkOnlyDisplayDSLConditions.setText(GuidedRuleEditorResources.CONSTANTS.OnlyDisplayDSLConditions());
            chkOnlyDisplayDSLConditions.setValue(onlyShowDSLStatements);
            chkOnlyDisplayDSLConditions.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    onlyShowDSLStatements = event.getValue();
                    choicesPanel.setWidget(makeChoicesListBox(filterWidget.getFilterText()));
                }
            });
            layoutPanel.addRow(chkOnlyDisplayDSLConditions);
        }

        return layoutPanel;
    }

    @Override
    public void show() {
        super.show();
        choices.setFocus(true);
    }

    private ListBox makeChoicesListBox(final String filter) {
        choices = GWT.create(ListBox.class);
        choices.setMultipleSelect(true);
        choices.setPixelSize(getChoicesWidth(),
                             getChoicesHeight());

        choices.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    selectSomething();
                }
            }
        });

        final Predicate<String> predicate = (item) -> item.toLowerCase().contains(Objects.isNull(filter) ? "" : filter.toLowerCase());
        boolean itemsAdded = addDSLSentences(predicate);
        if (!onlyShowDSLStatements) {
            itemsAdded = addFacts(predicate, itemsAdded) || itemsAdded;
            addExistentialConditionalElements(itemsAdded);
            addFromConditionalElements();
            addFreeFormDrl();
        }

        return choices;
    }

    // The list of DSL sentences
    private boolean addDSLSentences(final Predicate<String> predicate) {
        boolean moreItemsAdded = false;

        //DSL might be prohibited (e.g. editing a DRL file. Only DSLR files can contain DSL)
        if (ruleModeller.isDSLEnabled()) {
            for (final DSLSentence sen : oracle.getDSLConditions()) {
                final String sentence = sen.toString();
                if (predicate.test(sentence)) {
                    final String key = "DSL" + sentence;
                    choices.addItem(sentence,
                                    key);
                    cmds.put(key,
                             new Command() {

                                 public void execute() {
                                     addNewDSLLhs(sen,
                                                  Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                                     hide();
                                 }
                             });
                    moreItemsAdded = true;
                }
            }
        }

        return moreItemsAdded;
    }

    // The list of facts
    private boolean addFacts(final Predicate<String> predicate,
                             final boolean previousItemsAdded) {
        boolean moreItemsAdded = false;

        if (oracle.getFactTypes().length > 0) {
            if (anyItemsMatch(predicate, oracle.getFactTypes()) && previousItemsAdded) {
                choices.addItem(SECTION_SEPARATOR, SECTION_SEPARATOR);
            }

            for (int i = 0; i < oracle.getFactTypes().length; i++) {
                final String f = oracle.getFactTypes()[i];
                if (predicate.test(f)) {
                    String key = "NF" + f;

                    choices.addItem(f + " ...",
                                    key);
                    cmds.put(key,
                             new Command() {

                                 public void execute() {
                                     addNewFact(f,
                                                Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                                     hide();
                                 }
                             });
                    moreItemsAdded = true;
                }
            }
        }

        return moreItemsAdded;
    }

    // The list of existential CEs
    private void addExistentialConditionalElements(final boolean previousItemsAdded) {
        String ces[] = HumanReadable.CONDITIONAL_ELEMENTS;

        if (previousItemsAdded) {
            choices.addItem(SECTION_SEPARATOR, SECTION_SEPARATOR);
        }

        for (int i = 0; i < ces.length; i++) {
            final String ce = ces[i];
            String key = "CE" + ce;
            choices.addItem(HumanReadable.getCEDisplayName(ce) + " ...",
                            key);
            cmds.put(key,
                     new Command() {

                         public void execute() {
                             addNewCE(ce,
                                      Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }
    }

    // The list of from CEs
    private void addFromConditionalElements() {
        String fces[] = HumanReadable.FROM_CONDITIONAL_ELEMENTS;

        choices.addItem(SECTION_SEPARATOR, SECTION_SEPARATOR);
        for (int i = 0; i < fces.length; i++) {
            final String ce = fces[i];
            String key = "FCE" + ce;
            choices.addItem(HumanReadable.getCEDisplayName(ce) + " ...",
                            key);
            cmds.put(key,
                     new Command() {

                         public void execute() {
                             addNewFCE(ce,
                                       Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                             hide();
                         }
                     });
        }
    }

    // Free form DRL
    private void addFreeFormDrl() {
        choices.addItem(SECTION_SEPARATOR, SECTION_SEPARATOR);
        choices.addItem(GuidedRuleEditorResources.CONSTANTS.FreeFormDrl(),
                        "FF");
        cmds.put("FF",
                 new Command() {

                     public void execute() {
                         model.addLhsItem(new FreeFormLine(),
                                          Integer.parseInt(positionCbo.getValue(positionCbo.getSelectedIndex())));
                         hide();
                     }
                 });
    }

    private void addNewDSLLhs(final DSLSentence sentence,
                              int position) {
        model.addLhsItem(sentence.copy(),
                         position);
    }

    private void addNewFact(String itemText,
                            int position) {
        this.model.addLhsItem(new FactPattern(itemText),
                              position);
    }

    private void addNewCE(String s,
                          int position) {
        this.model.addLhsItem(new CompositeFactPattern(s),
                              position);
    }

    private void addNewFCE(String type,
                           int position) {
        FromCompositeFactPattern p = null;
        if (type.equals("from")) {
            p = new FromCompositeFactPattern();
        } else if (type.equals("from accumulate")) {
            p = new FromAccumulateCompositeFactPattern();
        } else if (type.equals("from collect")) {
            p = new FromCollectCompositeFactPattern();
        } else if (type.equals("from entry-point")) {
            p = new FromEntryPointFactPattern();
        }

        this.model.addLhsItem(p,
                              position);
    }
}
