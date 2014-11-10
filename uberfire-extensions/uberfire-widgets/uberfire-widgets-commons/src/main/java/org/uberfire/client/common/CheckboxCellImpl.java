/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.common;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * A {@link Cell} used to render a checkbox. The value of the checkbox may be
 * toggled using the ENTER key as well as via mouse click.
 */
public class CheckboxCellImpl extends AbstractEditableCell<Boolean, Boolean> {

    /**
     * An html string representation of a checked input box.
     */
    private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils.fromSafeConstant( "<input type=\"checkbox\" tabindex=\"-1\" checked/>" );

    /**
     * An html string representation of an unchecked input box.
     */
    private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils.fromSafeConstant( "<input type=\"checkbox\" tabindex=\"-1\"/>" );

    /**
     * An html string representation of a read-only checked input box.
     */
    private static final SafeHtml READ_ONLY_INPUT_CHECKED = SafeHtmlUtils.fromSafeConstant( "<input type=\"checkbox\" tabindex=\"-1\" checked disabled=\"disabled\"/>" );

    /**
     * An html string representation of a read-only unchecked input box.
     */
    private static final SafeHtml READ_ONLY_INPUT_UNCHECKED = SafeHtmlUtils.fromSafeConstant( "<input type=\"checkbox\" tabindex=\"-1\" disabled=\"disabled\"/>" );

    protected boolean isReadOnly;

    /**
     * Construct a new {@link CheckboxCellImpl}
     */
    public CheckboxCellImpl( boolean isReadOnly ) {
        super( "click",
               "keydown" );
        this.isReadOnly = isReadOnly;
    }

    CheckboxCellImpl( String... consumedEvents ) {
        super( consumedEvents );
    }

    @Override
    public boolean isEditing( Context context,
                              Element parent,
                              Boolean value ) {
        // A checkbox is never in "edit mode". There is no intermediate state
        // between checked and unchecked.
        return false;
    }

    @Override
    public void onBrowserEvent( Context context,
                                Element parent,
                                Boolean value,
                                NativeEvent event,
                                ValueUpdater<Boolean> valueUpdater ) {

        //If read-only ignore editing events
        if ( isReadOnly ) {
            return;
        }

        String type = event.getType();

        boolean enterPressed = "keydown".equals( type ) && event.getKeyCode() == KeyCodes.KEY_ENTER;
        if ( "click".equals( type ) || enterPressed ) {
            InputElement input = parent.getFirstChild().cast();
            Boolean isChecked = input.isChecked();

            /*
             * Toggle the value if the enter key was pressed and the cell
             * handles selection or doesn't depend on selection. If the cell
             * depends on selection but doesn't handle selection, then ignore
             * the enter key and let the SelectionEventManager determine which
             * keys will trigger a change.
             */
            if ( enterPressed ) {
                isChecked = !isChecked;
                input.setChecked( isChecked );
            }

            /*
             * Save the new value. However, if the cell depends on the
             * selection, then do not save the value because we can get into an
             * inconsistent state.
             */
            if ( value != isChecked ) {
                setViewData( context.getKey(),
                             isChecked );
            }

            if ( valueUpdater != null ) {
                valueUpdater.update( isChecked );
            }
        }
    }

    @Override
    public void render( Context context,
                        Boolean value,
                        SafeHtmlBuilder sb ) {
        // Get the view data.
        Object key = context.getKey();
        Boolean viewData = getViewData( key );
        if ( viewData != null && viewData.equals( value ) ) {
            clearViewData( key );
            viewData = null;
        }

        if ( value != null && ( ( viewData != null ) ? viewData : value ) ) {
            if ( isReadOnly ) {
                sb.append( READ_ONLY_INPUT_CHECKED );
            } else {
                sb.append( INPUT_CHECKED );
            }
        } else {
            if ( isReadOnly ) {
                sb.append( READ_ONLY_INPUT_UNCHECKED );
            } else {
                sb.append( INPUT_UNCHECKED );
            }
        }
    }
}
