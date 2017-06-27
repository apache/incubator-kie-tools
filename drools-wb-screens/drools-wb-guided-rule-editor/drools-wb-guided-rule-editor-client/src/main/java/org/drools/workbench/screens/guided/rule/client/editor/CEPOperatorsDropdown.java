/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.commons.backend.rule.SharedConstants;
import org.drools.workbench.models.datamodel.rule.HasParameterizedOperator;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.widgets.client.datamodel.CEPOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.uberfire.ext.widgets.common.client.common.AbstractRestrictedEntryTextBox;

/**
 * Drop-down Widget for Operators including supplementary controls for CEP
 * operator parameters
 */
public class CEPOperatorsDropdown extends Composite
        implements
        HasValueChangeHandlers<OperatorSelection> {

    //Parameter key to store the current parameter set (i.e. which parameters are visible)
    private static final String VISIBLE_PARAMETER_SET = "org.drools.workbench.models.commons.backend.rule.visibleParameterSet";

    //Parameter value defining the server-side class used to generate DRL for CEP operator parameters (key is in droolsjbpm-ide-common)
    private static final String CEP_OPERATOR_PARAMETER_GENERATOR = "org.drools.workbench.models.commons.backend.rule.CEPOperatorParameterDRLBuilder";

    protected int visibleParameterSet = 0;

    protected List<Integer> parameterSets;

    protected HasParameterizedOperator hop;

    private String[] operators;

    private Image btnAddCEPOperators;

    private ListBox box;

    private HorizontalPanel container = new HorizontalPanel();

    private TextBox[] parameters = new TextBox[4];

    public CEPOperatorsDropdown(String[] operators,
                                HasParameterizedOperator hop) {
        this.operators = operators;
        this.hop = hop;

        //Initialise parameter sets for operator
        parameterSets = CEPOracle.getCEPOperatorParameterSets(hop.getOperator());

        //Retrieve last "visible state"
        String vps = hop.getParameter(VISIBLE_PARAMETER_SET);
        if (vps != null) {
            try {
                visibleParameterSet = Integer.parseInt(vps);
            } catch (NumberFormatException nfe) {
            }
        }

        for (int i = 0; i < this.parameters.length; i++) {
            parameters[i] = makeTextBox(i);
        }

        HorizontalPanel hp = new HorizontalPanel();
        hp.setStylePrimaryName(GuidedRuleEditorResources.INSTANCE.css().container());
        hp.add(getDropDown());
        hp.add(getOperatorExtension());

        initWidget(hp);
    }

    /**
     * Add ancillary items to drop-down
     * @param item
     * @param value
     */
    public void addItem(String item,
                        String value) {
        box.addItem(item,
                    value);
    }

    /**
     * Insert ancillary items to drop-down
     * @param item
     * @param value
     * @param index
     */
    public void insertItem(String item,
                           String value,
                           int index) {
        getBox().insertItem(item,
                            value,
                            index);
    }

    /**
     * Gets the index of the currently-selected item.
     * @return
     */
    public int getSelectedIndex() {
        return box.getSelectedIndex();
    }

    /**
     * Gets the value associated with the item at a given index.
     * @param index
     * @return
     */
    public String getValue(int index) {
        return box.getValue(index);
    }

    //Additional widget for CEP operator parameters
    private Widget getOperatorExtension() {
        container.setStylePrimaryName(GuidedRuleEditorResources.INSTANCE.css().container());

        btnAddCEPOperators = new Image(GuidedRuleEditorResources.INSTANCE.images().clock());
        btnAddCEPOperators.setVisible(parameterSets.size() > 0);
        btnAddCEPOperators.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                visibleParameterSet++;
                if (visibleParameterSet == parameterSets.size()) {
                    visibleParameterSet = 0;
                }
                hop.setParameter(VISIBLE_PARAMETER_SET,
                                 Integer.toString(visibleParameterSet));
                displayParameters();
            }
        });

        container.add(btnAddCEPOperators);
        for (int i = 0; i < this.parameters.length; i++) {
            container.add(parameters[i]);
        }

        return container;
    }

    //TextBox factory
    private TextBox makeTextBox(final int index) {
        AbstractRestrictedEntryTextBox txt = new CEPTimeParameterTextBox(hop,
                                                                         index);

        if (parameterSets.size() == 0) {
            txt.setVisible(false);
        } else {
            txt.setVisible(index < parameterSets.get(visibleParameterSet));
        }

        return txt;
    }

    //Hide\display the additional CEP widget is appropriate
    private void operatorChanged(OperatorSelection selection) {
        String operator = selection.getValue();
        if (CEPOracle.isCEPOperator(operator)) {
            container.setVisible(true);
            btnAddCEPOperators.setVisible(true);
            parameterSets = CEPOracle.getCEPOperatorParameterSets(operator);
            hop.setParameter(SharedConstants.OPERATOR_PARAMETER_GENERATOR,
                             CEP_OPERATOR_PARAMETER_GENERATOR);
        } else {
            visibleParameterSet = 0;
            container.setVisible(false);
            btnAddCEPOperators.setVisible(false);
            parameterSets = Collections.emptyList();
            hop.clearParameters();
        }
        displayParameters();
    }

    //Display the appropriate number of parameters
    private void displayParameters() {
        if (parameterSets.size() == 0) {

            //All boxes are hidden if there are no parameter sets
            for (int i = 0; i < parameters.length; i++) {
                parameters[i].setVisible(false);
            }
        } else {

            //Display text boxes indexed less that the value of the current 
            //parameter set, initialising the parameter value if necessary 
            //and removing any excess parameter values
            for (int i = 0; i < parameters.length; i++) {
                String key = Integer.toString(i);
                boolean isVisible = i < parameterSets.get(visibleParameterSet);
                if (isVisible) {
                    String value = hop.getParameter(key);
                    if (value == null) {
                        value = "";
                        hop.setParameter(key,
                                         value);
                    }
                    parameters[i].setText(value);
                    parameters[i].setVisible(true);
                } else {
                    hop.deleteParameter(key);
                    parameters[i].setVisible(false);
                }
            }
        }
    }

    public void addPlaceholder(final String item,
                               final String value) {
        insertItem(item,
                   value,
                   0);
        if (hop.getOperator() == null || hop.getOperator().isEmpty()) {
            getBox().setSelectedIndex(0);
        }
    }

    //Actual drop-down
    private Widget getDropDown() {

        String selected = "";
        String selectedText = "";
        box = new ListBox();

        for (int i = 0; i < operators.length; i++) {
            String op = operators[i];
            box.addItem(HumanReadable.getOperatorDisplayName(op),
                        op);
            if (op.equals(hop.getOperator())) {
                selected = op;
                selectedText = HumanReadable.getOperatorDisplayName(op);
                box.setSelectedIndex(i);
            }
        }

        //Fire event to ensure parent Widgets correct their state depending on selection
        final HasValueChangeHandlers<OperatorSelection> source = this;
        final OperatorSelection selection = new OperatorSelection(selected,
                                                                  selectedText);
        Scheduler.get().scheduleFinally(new Command() {

            public void execute() {
                operatorChanged(selection);
                ValueChangeEvent.fire(source,
                                      selection);
            }
        });

        //Signal parent Widget whenever a change happens
        box.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                String selected = box.getValue(box.getSelectedIndex());
                String selectedText = box.getItemText(box.getSelectedIndex());
                OperatorSelection selection = new OperatorSelection(selected,
                                                                    selectedText);
                operatorChanged(selection);
                ValueChangeEvent.fire(source,
                                      selection);
            }
        });

        return box;
    }

    /**
     * Allow parent Widgets to register for events when the operator changes
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<OperatorSelection> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    public ListBox getBox() {
        return box;
    }
}
