/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class InsertMenuViewImpl extends BaseMenuViewImpl<InsertMenuBuilder> implements InsertMenuView {

    @DataField("insertMenuDropdown")
    ButtonElement insertMenuDropdown = Document.get().createPushButtonElement();

    @DataField("insertMenuAppendRow")
    LIElement insertMenuAppendRow = Document.get().createLIElement();

    @DataField("insertMenuInsertRowAbove")
    LIElement insertMenuInsertRowAbove = Document.get().createLIElement();

    @DataField("insertMenuInsertRowBelow")
    LIElement insertMenuInsertRowBelow = Document.get().createLIElement();

    @DataField("insertMenuAppendColumn")
    LIElement insertMenuAppendColumn = Document.get().createLIElement();

    @Override
    public boolean isEnabled() {
        return !insertMenuDropdown.isDisabled();
    }

    @Override
    public void setEnabled( final boolean enabled ) {
        insertMenuDropdown.setDisabled( !enabled );
    }

    @Override
    public void enableAppendRowMenuItem( final boolean enabled ) {
        enableElement( insertMenuAppendRow,
                       enabled );
    }

    @Override
    public void enableInsertRowAboveMenuItem( final boolean enabled ) {
        enableElement( insertMenuInsertRowAbove,
                       enabled );
    }

    @Override
    public void enableInsertRowBelowMenuItem( final boolean enabled ) {
        enableElement( insertMenuInsertRowBelow,
                       enabled );
    }

    @Override
    public void enableAppendColumnMenuItem( final boolean enabled ) {
        enableElement( insertMenuAppendColumn,
                       enabled );
    }

    @SuppressWarnings("unused")
    @EventHandler("insertMenuDropdown")
    public void onClickInsertMenuDropdown( final ClickEvent e ) {
        presenter.initialise();
    }

    @SuppressWarnings("unused")
    @EventHandler("insertMenuAppendRow")
    public void onClickInsertMenuAppendRow( final ClickEvent e ) {
        presenter.onAppendRow();
    }

    @SuppressWarnings("unused")
    @EventHandler("insertMenuInsertRowAbove")
    public void onClickInsertMenuInsertRowAbove( final ClickEvent e ) {
        presenter.onInsertRowAbove();
    }

    @SuppressWarnings("unused")
    @EventHandler("insertMenuInsertRowBelow")
    public void onClickInsertMenuInsertRowBelow( final ClickEvent e ) {
        presenter.onInsertRowBelow();
    }

    @SuppressWarnings("unused")
    @EventHandler("insertMenuAppendColumn")
    public void onClickInsertMenuAppendColumn( final ClickEvent e ) {
        presenter.onAppendColumn();
    }

}
