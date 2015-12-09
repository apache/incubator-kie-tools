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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import static com.google.gwt.dom.client.BrowserEvents.*;

/**
 * A Popup Editor.
 */
public abstract class AbstractPopupEditCell<C, V> extends
                                                  AbstractEditableCell<C, V> {

    protected int offsetX = 5;
    protected int offsetY = 5;
    protected       Context                  lastContext;
    protected       Element                  lastParent;
    protected       C                        lastValue;
    protected final PopupPanel               panel;
    protected final VerticalPanel            vPanel;
    protected final SafeHtmlRenderer<String> renderer;
    protected final boolean                  isReadOnly;
    protected       ValueUpdater<C>          valueUpdater;

    /**
     * Boiler-plate and scaffolding for a general "Popup". Subclasses should
     * call this default constructor and append their specific child controls
     * for the "Popup" to <code>vPanel</code>.
     */
    public AbstractPopupEditCell( boolean isReadOnly ) {
        super( DBLCLICK,
               KEYDOWN );
        this.renderer = SimpleSafeHtmlRenderer.getInstance();
        this.vPanel = new VerticalPanel();
        this.isReadOnly = isReadOnly;

        // Pressing ESCAPE dismisses the pop-up loosing any changes
        this.panel = new PopupPanel( true,
                                     true ) {
            @Override
            protected void onPreviewNativeEvent( NativePreviewEvent event ) {
                if ( Event.ONKEYUP == event.getTypeInt() ) {
                    if ( event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE ) {
                        panel.hide();
                    }
                }
            }

        };

        // Closing the pop-up commits the change
        panel.addCloseHandler( new CloseHandler<PopupPanel>() {
            public void onClose( CloseEvent<PopupPanel> event ) {
                if ( lastParent != null
                        && !event.isAutoClosed() ) {
                    getTableCellElementAncestor( lastParent ).focus();
                } else if ( event.isAutoClosed() ) {
                    commit();
                }
                lastContext = null;
                lastParent = null;
                lastValue = null;
            }
        } );

        panel.add( vPanel );

    }

    @Override
    public boolean isEditing( Context context,
                              Element parent,
                              C value ) {
        return lastContext != null
                && lastContext.equals( context );
    }

    @Override
    public void onBrowserEvent( Context context,
                                Element parent,
                                C value,
                                NativeEvent event,
                                ValueUpdater<C> valueUpdater ) {

        //If read-only ignore editing events
        if ( isReadOnly ) {
            return;
        }

        // KeyDown and "Enter" key-press is handled here
        super.onBrowserEvent( context,
                              parent,
                              value,
                              event,
                              valueUpdater );

        if ( event.getType().equals( "dblclick" ) ) {
            this.lastContext = context;
            this.lastParent = parent;
            this.lastValue = value;
            this.valueUpdater = valueUpdater;
            startEditing( context,
                          parent,
                          value );
        }
    }

    // Find the nearest TableCellElement ancestor
    private Element getTableCellElementAncestor( Element e ) {
        Element parent = e.getParentElement();
        while ( !TableSectionElement.is( parent )
                && !TableCellElement.is( parent ) ) {
            parent = parent.getParentElement();
        }
        return parent;
    }

    /**
     * Commit the change to the underlying model. Implementations should use the
     * protected <code>valueUpdater</code> initialised in onBrowseEvent to pass
     * new values to the model. Implementations should also invoke
     * <code>setValue</code> to write the new value back to the Cell's HTML
     */
    protected abstract void commit();

    @Override
    protected void onEnterKeyDown( Context context,
                                   Element parent,
                                   C value,
                                   NativeEvent event,
                                   ValueUpdater<C> valueUpdater ) {
        this.lastContext = context;
        this.lastParent = parent;
        this.lastValue = value;
        this.valueUpdater = valueUpdater;
        startEditing( context,
                      parent,
                      value );
    }

    /**
     * Initiate editing within the "Popup". Implementations should populate the
     * child controls within the "Popup" before showing the Popup
     * <code>panel</code>
     * @param parent
     * @param value
     */
    protected abstract void startEditing( final Context context,
                                          final Element parent,
                                          final C value );

}
