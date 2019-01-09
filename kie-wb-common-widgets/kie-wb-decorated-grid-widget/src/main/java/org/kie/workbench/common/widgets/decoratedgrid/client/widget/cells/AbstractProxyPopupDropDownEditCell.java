/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellTableDropDownDataValueMapProvider;

/**
 * A Popup drop-down Editor proxy that delegates operation to different implementations depending on whether
 * the cell should represent a list of values or single value. The need arose from incomplete dependent enumeration
 * definitions; e.g. Fact.field1=['A', 'B'] Fact.field2[field1=A]=['A1', 'A2']; where no dependent enumeration has
 * been defined for Fact.field2[field1=B]. In this scenario a TextBox for field2 should be shown when field1=B
 */
public abstract class AbstractProxyPopupDropDownEditCell<C, V> extends
                                                               AbstractPopupEditCell<C, V> {

    private final String factType;
    private final String factField;

    private final AsyncPackageDataModelOracle dmo;
    private final CellTableDropDownDataValueMapProvider dropDownManager;

    private final ProxyPopupDropDown<C> singleValueEditor;
    private final ProxyPopupDropDown<C> multipleValueEditor;
    private ProxyPopupDropDown<C> delegate;

    public AbstractProxyPopupDropDownEditCell(final String factType,
                                              final String factField,
                                              final String operator,
                                              final AsyncPackageDataModelOracle dmo,
                                              final CellTableDropDownDataValueMapProvider dropDownManager,
                                              final boolean isReadOnly) {
        super(isReadOnly);
        this.factType = factType;
        this.factField = factField;
        this.dropDownManager = dropDownManager;
        this.singleValueEditor = getSingleValueEditor();
        this.multipleValueEditor = getMultipleValueEditor(operator);
        this.dmo = dmo;
    }

    @Override
    public void render(final Context context,
                       final C value,
                       final SafeHtmlBuilder sb) {

        //We need to get the list of potential values to lookup the "Display" value from the "Stored" value.
        //Since the content of the list may be different for each cell (dependent enumerations) the list
        //has to be populated "on demand". 
        DropDownData dd = dmo.getEnums(this.factType,
                                       this.factField,
                                       this.dropDownManager.getCurrentValueMap(context));

        if (dd == null) {
            //If no enumeration exists show a TextBox
            delegate = singleValueEditor;
            delegate.setValue(value);
            vPanel.clear();
            vPanel.add(delegate.asWidget());
        } else {
            //Otherwise show a drop-down list box
            delegate = multipleValueEditor;
            delegate.setDropDownData(dd);
            vPanel.clear();
            vPanel.add(delegate.asWidget());
        }

        delegate.render(context,
                        value,
                        sb,
                        renderer);
    }

    @Override
    protected void commit() {
        final C value = delegate.getValue();

        setValue(lastContext,
                 lastParent,
                 value);
        if (valueUpdater != null) {
            valueUpdater.update(value);
        }
        panel.hide();
    }

    // Start editing the cell
    @Override
    protected void startEditing(final Context context,
                                final Element parent,
                                final C value) {

        //We need to get the list of potential values for the enumeration. Since the content 
        //of the list may be different for each cell (dependent enumerations) the list
        //has to be populated "on demand". 
        DropDownData dd = dmo.getEnums(this.factType,
                                       this.factField,
                                       this.dropDownManager.getCurrentValueMap(context));
        if (dd == null) {
            //If no enumeration exists show a TextBox
            delegate = singleValueEditor;
            delegate.setValue(value);
            vPanel.clear();
            vPanel.add(delegate.asWidget());
        } else {
            //Otherwise show a drop-down list box
            delegate = multipleValueEditor;
            delegate.setDropDownData(dd);
            vPanel.clear();
            vPanel.add(delegate.asWidget());
        }
        delegate.startEditing(context,
                              parent,
                              value);

        panel.setPopupPositionAndShow(new PositionCallback() {
            public void setPosition(int offsetWidth,
                                    int offsetHeight) {
                panel.setPopupPosition(parent.getAbsoluteLeft()
                                               + offsetX,
                                       parent.getAbsoluteTop()
                                               + offsetY);

                // Focus the first enabled control
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    public void execute() {
                        delegate.setFocus(true);
                    }
                });
            }
        });
    }

    AsyncPackageDataModelOracle getDataModelOracle() {
        return this.dmo;
    }

    protected abstract ProxyPopupDropDown<C> getSingleValueEditor();

    protected abstract ProxyPopupDropDown<C> getMultipleValueEditor(final String operator);
}
