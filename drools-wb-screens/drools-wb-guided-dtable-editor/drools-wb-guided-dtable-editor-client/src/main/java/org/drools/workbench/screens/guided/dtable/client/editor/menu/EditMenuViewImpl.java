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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;

@Dependent
@Templated
public class EditMenuViewImpl extends BaseMenuViewImpl<EditMenuBuilder> implements EditMenuView {

    @DataField("editMenuDropdown")
    ButtonElement editMenuDropdown = Document.get().createPushButtonElement();

    @DataField("editMenuCut")
    LIElement editMenuCut = Document.get().createLIElement();

    @DataField("editMenuCopy")
    LIElement editMenuCopy = Document.get().createLIElement();

    @DataField("editMenuPaste")
    LIElement editMenuPaste = Document.get().createLIElement();

    @DataField("editMenuDeleteCells")
    LIElement editMenuDeleteCells = Document.get().createLIElement();

    @DataField("editMenuDeleteColumns")
    LIElement editMenuDeleteColumns = Document.get().createLIElement();

    @DataField("editMenuDeleteRows")
    LIElement editMenuDeleteRows = Document.get().createLIElement();

    @DataField("editMenuOtherwiseCell")
    LIElement editMenuOtherwiseCell = Document.get().createLIElement();

    @DataField("editMenuOtherwiseCellIcon")
    Element editMenuOtherwiseCellIcon = Document.get().createElement( "i" );

    @Override
    public boolean isEnabled() {
        return !editMenuDropdown.isDisabled();
    }

    @Override
    public void setEnabled( final boolean enabled ) {
        editMenuDropdown.setDisabled( !enabled );
    }

    @Override
    public void setOtherwiseCell( final boolean otherwise ) {
        if ( otherwise ) {
            StyleHelper.addEnumStyleName( editMenuOtherwiseCellIcon,
                                          IconType.CHECK );
        } else {
            StyleHelper.removeEnumStyleName( editMenuOtherwiseCellIcon,
                                             IconType.CHECK );
        }
    }

    @Override
    public void enableCutMenuItem( final boolean enabled ) {
        enableElement( editMenuCut,
                       enabled );
    }

    @Override
    public void enableCopyMenuItem( final boolean enabled ) {
        enableElement( editMenuCopy,
                       enabled );
    }

    @Override
    public void enablePasteMenuItem( final boolean enabled ) {
        enableElement( editMenuPaste,
                       enabled );
    }

    @Override
    public void enableDeleteCellMenuItem( final boolean enabled ) {
        enableElement( editMenuDeleteCells,
                       enabled );
    }

    @Override
    public void enableDeleteColumnMenuItem( final boolean enabled ) {
        enableElement( editMenuDeleteColumns,
                       enabled );
    }

    @Override
    public void enableDeleteRowMenuItem( final boolean enabled ) {
        enableElement( editMenuDeleteRows,
                       enabled );
    }

    @Override
    public void enableOtherwiseCellMenuItem( boolean enabled ) {
        enableElement( editMenuOtherwiseCell,
                       enabled );
    }

    @SuppressWarnings("unused")
    @EventHandler("editMenuDropdown")
    public void onClickEditMenuDropdown( final ClickEvent e ) {
        presenter.initialise();
    }

    @SuppressWarnings("unused")
    @EventHandler("editMenuCut")
    public void onClickEditMenuCut( final ClickEvent e ) {
        presenter.onCut();
    }

    @SuppressWarnings("unused")
    @EventHandler("editMenuCopy")
    public void onClickEditMenuCopy( final ClickEvent e ) {
        presenter.onCopy();
    }

    @SuppressWarnings("unused")
    @EventHandler("editMenuPaste")
    public void onClickEditMenuPaste( final ClickEvent e ) {
        presenter.onPaste();
    }

    @SuppressWarnings("unused")
    @EventHandler("editMenuDeleteCells")
    public void onClickEditMenuDeleteCells( final ClickEvent e ) {
        presenter.onDeleteSelectedCells();
    }

    @SuppressWarnings("unused")
    @EventHandler("editMenuDeleteColumns")
    public void onClickEditMenuDeleteColumns( final ClickEvent e ) {
        presenter.onDeleteSelectedColumns();
    }

    @SuppressWarnings("unused")
    @EventHandler("editMenuDeleteRows")
    public void onClickEditMenuDeleteRows( final ClickEvent e ) {
        presenter.onDeleteSelectedRows();
    }

    @SuppressWarnings("unused")
    @EventHandler("editMenuOtherwiseCell")
    public void onClickEditMenuOtherwiseCell( final ClickEvent e ) {
        presenter.onOtherwiseCell();
    }

}
