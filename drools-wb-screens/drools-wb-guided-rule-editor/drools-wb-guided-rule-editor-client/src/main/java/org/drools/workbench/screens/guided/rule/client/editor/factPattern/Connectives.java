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

package org.drools.workbench.screens.guided.rule.client.editor.factPattern;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.editor.CEPOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.ConstraintValueEditor;
import org.drools.workbench.screens.guided.rule.client.editor.OperatorSelection;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

public class Connectives {

    private final RuleModeller modeller;
    private final EventBus eventBus;
    private final FactPattern pattern;
    private final Boolean isReadOnly;

    public Connectives(RuleModeller modeller,
                       EventBus eventBus,
                       FactPattern pattern,
                       Boolean isReadOnly) {
        this.pattern = pattern;
        this.modeller = modeller;
        this.eventBus = eventBus;
        this.isReadOnly = isReadOnly;
    }

    /**
     * Returns the pattern.
     */
    public FactPattern getPattern() {
        return pattern;
    }

    /**
     * Returns the oracle.
     */
    public AsyncPackageDataModelOracle getDataModelOracle() {
        return this.modeller.getDataModelOracle();
    }

    public Widget connectives(final SingleFieldConstraint c) {
        final HorizontalPanel hp = new HorizontalPanel();
        if (c.getConnectives() != null && c.getConnectives().length > 0) {
            hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
            hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            for (int i = 0; i < c.getConnectives().length; i++) {
                final int index = i;
                final ConnectiveConstraint con = c.getConnectives()[i];
                connectiveOperatorDropDown(con,
                                           new Callback<Widget>() {
                                               @Override
                                               public void callback(final Widget w) {
                                                   hp.add(w);

                                                   final ConstraintValueEditor editor = connectiveValueEditor(con);
                                                   editor.init();
                                                   hp.add(editor);

                                                   if (!isReadOnly) {
                                                       Image clear = GuidedRuleEditorImages508.INSTANCE.DeleteItemSmall();
                                                       clear.setAltText(GuidedRuleEditorResources.CONSTANTS.RemoveThisRestriction());
                                                       clear.setTitle(GuidedRuleEditorResources.CONSTANTS.RemoveThisRestriction());
                                                       clear.addClickHandler(createClickHandlerForClearImageButton(c,
                                                                                                                   index));
                                                       hp.add(clear);
                                                   }
                                               }
                                           });
            }
        }
        return hp;
    }

    ConstraintValueEditor connectiveValueEditor(final BaseSingleFieldConstraint con) {

        return new ConstraintValueEditor(con,
                                         pattern.getConstraintList(),
                                         this.modeller,
                                         this.eventBus,
                                         isReadOnly);
    }

    void connectiveOperatorDropDown(final ConnectiveConstraint cc,
                                    final Callback<Widget> callback) {

        if (!isReadOnly) {

            final String factType = cc.getFactType();
            final String fieldName = cc.getFieldName();

            this.getDataModelOracle().getConnectiveOperatorCompletions(factType,
                                                                       fieldName,
                                                                       new Callback<String[]>() {
                                                                           @Override
                                                                           public void callback(final String[] operators) {
                                                                               final CEPOperatorsDropdown dropdown = getDropdown(operators,
                                                                                                                                 cc);
                                                                               dropdown.addPlaceholder(GuidedRuleEditorResources.CONSTANTS.pleaseChoose(),
                                                                                                       "");
                                                                               dropdown.addValueChangeHandler(new ValueChangeHandler<OperatorSelection>() {

                                                                                   public void onValueChange(ValueChangeEvent<OperatorSelection> event) {
                                                                                       OperatorSelection selection = event.getValue();
                                                                                       String selected = selection.getValue();
                                                                                       cc.setOperator(selected);
                                                                                   }
                                                                               });
                                                                               callback.callback(dropdown);
                                                                           }
                                                                       });
        } else {
            final SmallLabel w = new SmallLabel("<b>" + (cc.getOperator() == null ? GuidedRuleEditorResources.CONSTANTS.pleaseChoose() : HumanReadable.getOperatorDisplayName(cc.getOperator())) + "</b>");
            callback.callback(w);
        }
    }

    CEPOperatorsDropdown getDropdown(String[] operators,
                                     ConnectiveConstraint connectiveConstraint) {
        return new CEPOperatorsDropdown(operators,
                                        connectiveConstraint);
    }

    private ClickHandler createClickHandlerForClearImageButton(final SingleFieldConstraint sfc,
                                                               final int index) {
        return new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (Window.confirm(GuidedRuleEditorResources.CONSTANTS.RemoveThisItem())) {
                    sfc.removeConnective(index);
                    modeller.refreshWidget();
                }
            }
        };
    }
}
