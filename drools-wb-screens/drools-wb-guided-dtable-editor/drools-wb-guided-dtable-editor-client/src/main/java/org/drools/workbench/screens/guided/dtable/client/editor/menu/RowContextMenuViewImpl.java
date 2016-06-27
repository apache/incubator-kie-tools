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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class RowContextMenuViewImpl extends BaseMenuViewImpl<RowContextMenu> implements RowContextMenuView {

    @DataField("rowContextMenuDropdown")
    UListElement rowContextMenuDropdown = Document.get().createULElement();

    @DataField("rowContextMenuCut")
    LIElement rowContextMenuCut = Document.get().createLIElement();

    @DataField("rowContextMenuCopy")
    LIElement rowContextMenuCopy = Document.get().createLIElement();

    @DataField("rowContextMenuPaste")
    LIElement rowContextMenuPaste = Document.get().createLIElement();

    @DataField("rowContextMenuInsertRowAbove")
    LIElement rowContextMenuInsertRowAbove = Document.get().createLIElement();

    @DataField("rowContextMenuInsertRowBelow")
    LIElement rowContextMenuInsertRowBelow = Document.get().createLIElement();

    @DataField("rowContextMenuDeleteRows")
    LIElement rowContextMenuDeleteRows = Document.get().createLIElement();

    @Override
    public void show( final int mx,
                      final int my ) {
        //See https://issues.jboss.org/browse/ERRAI-936
        //Errai @Templated proxied beans have their "attached" state set to
        //true, even though they are not physically attached to the DOM.
        hide();
        RootPanel.get().add( this );
        rowContextMenuDropdown.getStyle().setLeft( mx,
                                                   Style.Unit.PX );
        rowContextMenuDropdown.getStyle().setTop( my,
                                                  Style.Unit.PX );
        rowContextMenuDropdown.getStyle().setDisplay( Style.Display.BLOCK );
    }

    @Override
    public void hide() {
        if ( isAttached() ) {
            RootPanel.get().remove( this );
            rowContextMenuDropdown.getStyle().setDisplay( Style.Display.NONE );
        }
    }

    @Override
    public void enableCutMenuItem( final boolean enabled ) {
        enableElement( rowContextMenuCut,
                       enabled );
    }

    @Override
    public void enableCopyMenuItem( final boolean enabled ) {
        enableElement( rowContextMenuCopy,
                       enabled );
    }

    @Override
    public void enablePasteMenuItem( final boolean enabled ) {
        enableElement( rowContextMenuPaste,
                       enabled );
    }

    @Override
    public void enableInsertRowAboveMenuItem( final boolean enabled ) {
        enableElement( rowContextMenuInsertRowAbove,
                       enabled );
    }

    @Override
    public void enableInsertRowBelowMenuItem( final boolean enabled ) {
        enableElement( rowContextMenuInsertRowBelow,
                       enabled );
    }

    @Override
    public void enableDeleteRowMenuItem( final boolean enabled ) {
        enableElement( rowContextMenuDeleteRows,
                       enabled );
    }

    @SuppressWarnings("unused")
    @EventHandler("rowContextMenuCut")
    public void onClickRowContextMenuCut( final ClickEvent e ) {
        if ( isDisabled( rowContextMenuCut ) ) {
            presenter.hide();
            return;
        }
        presenter.onCut();
    }

    @SuppressWarnings("unused")
    @EventHandler("rowContextMenuCopy")
    public void onClickRowContextMenuCopy( final ClickEvent e ) {
        if ( isDisabled( rowContextMenuCopy ) ) {
            presenter.hide();
            return;
        }
        presenter.onCopy();
    }

    @SuppressWarnings("unused")
    @EventHandler("rowContextMenuPaste")
    public void onClickRowContextMenuPaste( final ClickEvent e ) {
        if ( isDisabled( rowContextMenuPaste ) ) {
            presenter.hide();
            return;
        }
        presenter.onPaste();
    }

    @SuppressWarnings("unused")
    @EventHandler("rowContextMenuInsertRowAbove")
    public void onClickRowContextMenuInsertRowAbove( final ClickEvent e ) {
        if ( isDisabled( rowContextMenuInsertRowAbove ) ) {
            presenter.hide();
            return;
        }
        presenter.onInsertRowAbove();
    }

    @SuppressWarnings("unused")
    @EventHandler("rowContextMenuInsertRowBelow")
    public void onClickRowContextMenuInsertRowBelow( final ClickEvent e ) {
        if ( isDisabled( rowContextMenuInsertRowBelow ) ) {
            presenter.hide();
            return;
        }
        presenter.onInsertRowBelow();
    }

    @SuppressWarnings("unused")
    @EventHandler("rowContextMenuDeleteRows")
    public void onClickRowContextMenuDeleteRows( final ClickEvent e ) {
        if ( isDisabled( rowContextMenuDeleteRows ) ) {
            presenter.hide();
            return;
        }
        presenter.onDeleteSelectedRows();
    }

}
