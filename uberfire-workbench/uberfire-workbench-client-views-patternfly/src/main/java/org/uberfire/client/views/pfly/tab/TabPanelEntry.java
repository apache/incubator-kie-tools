/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.tab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.base.HasActive;

/**
 * Represents an entry in a {@link TabPanelWithDropdowns}. Keeps track of the current title, the tab widget (which could
 * be one of two different types depending on whether the entry is at top-level tab or nested in a dropdown tab), and
 * the associated content widget.
 */
public class TabPanelEntry implements HasActive {

    private String title;
    private final DropDownTabListItem tab;

    /**
     * Container for {@link #contents}.
     */
    private final TabPane contentPane;

    /**
     * The application-provided content widget that should show up when the tab is clicked.
     */
    private final Widget contents;

    public TabPanelEntry( String title, Widget contents ) {
        this.title = title;
        this.tab = new DropDownTabListItem( title );
        this.contents = contents;

        contentPane = new TabPane();
        contentPane.add( contents );

        tab.setDataTargetWidget( contentPane );
    }

    public DropDownTabListItem getTabWidget() {
        return tab;
    }

    public Widget getContents() {
        return contents;
    }

    /**
     * Returns the intermediate container that holds the real contents.
     */
    public TabPane getContentPane() {
        return contentPane;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
        tab.setText( title );
    }

    public void setInDropdown( boolean inDropdown ) {
        tab.setInDropdown( inDropdown );
    }

    /**
     * Represents the tab widget that lives in the tab bar or under a dropdown tab.
     */
    public static class DropDownTabListItem extends TabListItem implements HasClickHandlers,
            HasMouseDownHandlers {

        public DropDownTabListItem( String label ) {
            super( label );
            addStyleName( "uf-dropdown-tab-list-item" );
        }

        /**
         * Sets this tab for use in the top-level tab bar (isDropdown false) or inside a dropdown tab (isDropdown true).
         */
        public void setInDropdown( boolean inDropdown ) {
            anchor.setTabIndex( inDropdown ? -1 : 0 );
        }

        /**
         * Adds the given widget as a child of the anchor within the tab.
         */
        public void addToAnchor( Widget w ) {
            anchor.add( w );
        }

        @Override
        public HandlerRegistration addClickHandler( ClickHandler handler ) {
            return addDomHandler( handler, ClickEvent.getType() );
        }

        @Override
        public HandlerRegistration addMouseDownHandler( MouseDownHandler handler ) {
            return addDomHandler( handler, MouseDownEvent.getType() );
        }
    }

    /**
     * Returns true if this tab panel entry believes it's currently the active (displayed) tab in its tab panel.
     */
    @Override
    public boolean isActive() {
        return contentPane.isActive();
    }

    /**
     * Sets or clears the active state on this tab. Does not actually cause the tab to hide or show.
     */
    @Override
    public void setActive( boolean b ) {
        tab.setActive( b );
        contentPane.setActive( b );
    }

    /**
     * Makes this tab show itself and become the active tab, replacing whatever tab was previously active.
     */
    public void showTab() {
        tab.showTab( false );
    }

    @Override
    public String toString() {
        return "TabPanelEntry \"" + title + "\"";
    }

}
