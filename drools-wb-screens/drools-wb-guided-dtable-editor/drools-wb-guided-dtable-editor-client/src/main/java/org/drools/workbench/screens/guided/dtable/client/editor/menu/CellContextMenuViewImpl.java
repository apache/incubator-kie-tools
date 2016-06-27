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
public class CellContextMenuViewImpl extends BaseMenuViewImpl<CellContextMenu> implements CellContextMenuView {

    @DataField("cellContextMenuDropdown")
    UListElement cellContextMenuDropdown = Document.get().createULElement();

    @DataField("cellContextMenuCut")
    LIElement cellContextMenuCut = Document.get().createLIElement();

    @DataField("cellContextMenuCopy")
    LIElement cellContextMenuCopy = Document.get().createLIElement();

    @DataField("cellContextMenuPaste")
    LIElement cellContextMenuPaste = Document.get().createLIElement();

    @DataField("cellContextMenuDeleteCells")
    LIElement cellContextMenuDeleteCells = Document.get().createLIElement();

    @Override
    public void show( final int mx,
                      final int my ) {
        //See https://issues.jboss.org/browse/ERRAI-936
        //Errai @Templated proxied beans have their "attached" state set to
        //true, even though they are not physically attached to the DOM.
        hide();
        RootPanel.get().add( this );
        cellContextMenuDropdown.getStyle().setLeft( mx,
                                                    Style.Unit.PX );
        cellContextMenuDropdown.getStyle().setTop( my,
                                                   Style.Unit.PX );
        cellContextMenuDropdown.getStyle().setDisplay( Style.Display.BLOCK );
    }

    @Override
    public void hide() {
        if ( isAttached() ) {
            RootPanel.get().remove( this );
            cellContextMenuDropdown.getStyle().setDisplay( Style.Display.NONE );
        }
    }

    @Override
    public void enableCutMenuItem( final boolean enabled ) {
        enableElement( cellContextMenuCut,
                       enabled );
    }

    @Override
    public void enableCopyMenuItem( final boolean enabled ) {
        enableElement( cellContextMenuCopy,
                       enabled );
    }

    @Override
    public void enablePasteMenuItem( final boolean enabled ) {
        enableElement( cellContextMenuPaste,
                       enabled );
    }

    @Override
    public void enableDeleteCellMenuItem( final boolean enabled ) {
        enableElement( cellContextMenuDeleteCells,
                       enabled );
    }

    @SuppressWarnings("unused")
    @EventHandler("cellContextMenuCut")
    public void onClickCellContextMenuCut( final ClickEvent e ) {
        if ( isDisabled( cellContextMenuCut ) ) {
            presenter.hide();
            return;
        }
        presenter.onCut();
    }

    @SuppressWarnings("unused")
    @EventHandler("cellContextMenuCopy")
    public void onClickCellContextMenuCopy( final ClickEvent e ) {
        if ( isDisabled( cellContextMenuCopy ) ) {
            presenter.hide();
            return;
        }
        presenter.onCopy();
    }

    @SuppressWarnings("unused")
    @EventHandler("cellContextMenuPaste")
    public void onClickCellContextMenuPaste( final ClickEvent e ) {
        if ( isDisabled( cellContextMenuPaste ) ) {
            presenter.hide();
            return;
        }
        presenter.onPaste();
    }

    @SuppressWarnings("unused")
    @EventHandler("cellContextMenuDeleteCells")
    public void onClickCellContextMenuDeleteCells( final ClickEvent e ) {
        if ( isDisabled( cellContextMenuDeleteCells ) ) {
            presenter.hide();
            return;
        }
        presenter.onDeleteSelectedCells();
    }

}
