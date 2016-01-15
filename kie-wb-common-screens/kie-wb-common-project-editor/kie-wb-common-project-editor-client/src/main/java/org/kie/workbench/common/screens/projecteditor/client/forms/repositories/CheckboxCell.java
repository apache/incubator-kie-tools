/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.client.forms.repositories;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * A clone of GWT's CheckboxCell that supports being enabled and disabled.
 */
public class CheckboxCell extends AbstractEditableCell<Boolean, Boolean> implements HasEnabled {

    /**
     * An html string representation of an enabled checked input box.
     */
    private static final SafeHtml INPUT_CHECKED_ENABLED = SafeHtmlUtils.fromSafeConstant( "<input type=\"checkbox\" tabindex=\"-1\" checked/>" );

    /**
     * An html string representation of an enabled unchecked input box.
     */
    private static final SafeHtml INPUT_UNCHECKED_ENABLED = SafeHtmlUtils.fromSafeConstant( "<input type=\"checkbox\" tabindex=\"-1\"/>" );

    /**
     * An html string representation of a disabled checked input box.
     */
    private static final SafeHtml INPUT_CHECKED_DISABLED = SafeHtmlUtils.fromSafeConstant( "<input type=\"checkbox\" tabindex=\"-1\" checked disabled/>" );

    /**
     * An html string representation of a disabled unchecked input box.
     */
    private static final SafeHtml INPUT_UNCHECKED_DISABLED = SafeHtmlUtils.fromSafeConstant( "<input type=\"checkbox\" tabindex=\"-1\"  disabled/>" );

    private boolean enabled = true;

    /**
     * Construct a new {@link org.guvnor.common.services.project.client.repositories.CheckboxCell}.
     */
    public CheckboxCell() {
        super( BrowserEvents.CHANGE,
               BrowserEvents.KEYDOWN );
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled( final boolean enabled ) {
        this.enabled = enabled;

    }

    @Override
    public boolean isEditing( final Context context,
                              final Element parent,
                              final Boolean value ) {
        // A checkbox is never in "edit mode". There is no intermediate state
        // between checked and unchecked.
        return false;
    }

    @Override
    public void onBrowserEvent( final Context context,
                                final Element parent,
                                final Boolean value,
                                final NativeEvent event,
                                final ValueUpdater<Boolean> valueUpdater ) {
        String type = event.getType();

        boolean enterPressed = BrowserEvents.KEYDOWN.equals( type )
                && event.getKeyCode() == KeyCodes.KEY_ENTER;
        if ( BrowserEvents.CHANGE.equals( type ) || enterPressed ) {
            InputElement input = parent.getFirstChild().cast();
            Boolean isChecked = input.isChecked();

            // Toggle the value if the enter key was pressed and the cell handles
            // selection or doesn't depend on selection. If the cell depends on
            // selection but doesn't handle selection, then ignore the enter key and
            // let the SelectionEventManager determine which keys will trigger a
            // change.
            if ( enterPressed && ( handlesSelection() || !dependsOnSelection() ) ) {
                isChecked = !isChecked;
                input.setChecked( isChecked );
            }

            // Save the new value. However, if the cell depends on the selection, then
            // do not save the value because we can get into an inconsistent state.
            if ( value != isChecked && !dependsOnSelection() ) {
                setViewData( context.getKey(), isChecked );
            } else {
                clearViewData( context.getKey() );
            }

            if ( valueUpdater != null ) {
                valueUpdater.update( isChecked );
            }
        }
    }

    @Override
    public void render( final Context context,
                        final Boolean value,
                        final SafeHtmlBuilder sb ) {
        // Get the view data.
        Object key = context.getKey();
        Boolean viewData = getViewData( key );
        if ( viewData != null && viewData.equals( value ) ) {
            clearViewData( key );
            viewData = null;
        }

        if ( value != null && ( ( viewData != null ) ? viewData : value ) ) {
            sb.append( isEnabled() ? INPUT_CHECKED_ENABLED : INPUT_CHECKED_DISABLED );
        } else {
            sb.append( isEnabled() ? INPUT_UNCHECKED_ENABLED : INPUT_UNCHECKED_DISABLED );
        }
    }
}