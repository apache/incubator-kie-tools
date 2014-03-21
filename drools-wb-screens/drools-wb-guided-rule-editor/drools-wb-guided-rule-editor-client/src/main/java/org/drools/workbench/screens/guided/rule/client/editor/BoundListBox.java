/*
 * Copyright 2014 JBoss Inc
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

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.ListBox;
import org.drools.workbench.models.datamodel.rule.ActionFieldFunction;
import org.drools.workbench.screens.guided.rule.client.editor.util.SuperTypeMatcher;
import org.uberfire.client.callbacks.Callback;

public class BoundListBox
        extends ListBox {

    private RuleModeller model;
    private ActionFieldFunction methodParameter;
    private SuperTypeMatcher matcher;
    private HashMap<String, String> factTypesByVariables = new HashMap<String, String>();

    public BoundListBox(
            RuleModeller model,
            ActionFieldFunction methodParameter,
            SuperTypeMatcher matcher) {
        this.model = model;
        this.methodParameter = methodParameter;
        this.matcher = matcher;

        addItem("...");

        addBoundFacts();
    }

    private void addBoundFacts() {

        getRHSFacTypes();
        getLHSFacTypes();

        addVariables(factTypesByVariables.keySet().iterator());
    }

    private void getRHSFacTypes() {
        for (String variable : model.getModel().getAllRHSVariables()) {
            factTypesByVariables.put(variable, model.getModel().getRHSBoundFact(variable).getFactType());
        }
    }

    private void getLHSFacTypes() {
        for (String variable : model.getModel().getAllLHSVariables()) {
            factTypesByVariables.put(variable, model.getModel().getLHSBindingType(variable));
        }
    }

    private void setSelectedIndex() {
        if (methodParameter.getValue().equals("=")) {
            setSelectedIndex(0);
        } else {
            for (int i = 0; i < getItemCount(); i++) {
                if (getItemText(i).equals(methodParameter.getValue())) {
                    setSelectedIndex(i);
                }
            }
        }
    }

    private void addVariables(
            final Iterator<String> variables) {

        if (variables.hasNext()) {

            final String variable = variables.next();
            final String factType = factTypesByVariables.get(variable);

            if (factType.equals(this.methodParameter.getType())) {
                addItem(variable);
                addVariables(variables);
            } else {
                matcher.isThereAMatchingSuperType(
                        factType,
                        methodParameter.getType(),
                        new Callback<Boolean>() {
                            @Override
                            public void callback(Boolean result) {
                                if (result) {
                                    addItem(variable);
                                }

                                addVariables(variables);
                            }
                        });
            }
        } else {
            setSelectedIndex();
        }
    }

}
