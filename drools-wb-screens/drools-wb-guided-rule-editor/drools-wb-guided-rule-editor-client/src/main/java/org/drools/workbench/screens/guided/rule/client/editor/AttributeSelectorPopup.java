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

package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.widgets.common.client.common.DirtyableHorizontalPane;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

public abstract class AttributeSelectorPopup extends FormStylePopup {

    protected TextBox box;
    protected ListBox list;

    public AttributeSelectorPopup() {
        super( GuidedRuleEditorImages508.INSTANCE.Configure(),
               GuidedRuleEditorResources.CONSTANTS.AddAnOptionToTheRule() );
    }

    protected final void initialize() {
        initialize( new TextBox(),
                    new ListBox() );
    }

    protected final void initialize( final TextBox box,
                                     final ListBox list ) {
        this.box = box;
        this.list = list;
        setMetadataPanel();
        setAttributesPanel();
    }

    private void setMetadataPanel() {
        box.getElement().setAttribute( "size",
                                       "15" );

        DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();
        horiz.add( box );
        horiz.add( getAddButton() );

        addAttribute( GuidedRuleEditorResources.CONSTANTS.Metadata3(),
                      horiz );

    }

    private void setAttributesPanel() {
        for ( String item : getAttributes() ) {
            list.addItem( item );
        }

        removeReservedAttributes();

        list.setSelectedIndex( 0 );

        list.addChangeHandler( getListHandler( list ) );
        addAttribute( GuidedRuleEditorResources.CONSTANTS.Attribute1(),
                      list );

    }

    private void removeReservedAttributes() {
        for ( final String reservedAttribute : getReservedAttributes() ) {
            for ( int iItem = 0; iItem < list.getItemCount(); iItem++ ) {
                if ( list.getItemText( iItem ).equals( reservedAttribute ) ) {
                    list.removeItem( iItem );
                    break;
                }
            }
        }
    }

    protected abstract String[] getAttributes();

    protected abstract String[] getReservedAttributes();

    private ChangeHandler getListHandler( final ListBox list ) {
        return (ChangeEvent event) -> {
            handleAttributeAddition( list.getSelectedItemText() );
            hide();
        };
    }

    protected abstract void handleAttributeAddition( final String attributeName );

    private Image getAddButton() {
        final Image addbutton = GuidedRuleEditorImages508.INSTANCE.NewItem();
        addbutton.setAltText( GuidedRuleEditorResources.CONSTANTS.AddMetadataToTheRule() );
        addbutton.setTitle( GuidedRuleEditorResources.CONSTANTS.AddMetadataToTheRule() );
        addbutton.addClickHandler( getMetadataHandler() );
        return addbutton;
    }

    protected ClickHandler getMetadataHandler() {
        return (ClickEvent event) -> {
            //Check MetaData has a name
            final String metaData = box.getText().trim();
            if ( metaData.isEmpty() ) {
                Window.alert( GuidedRuleEditorResources.CONSTANTS.MetadataNameEmpty() );
                return;
            }

            //Check MetaData is unique
            if ( !isMetadataUnique( metaData ) ) {
                Window.alert( metadataNotUniqueMessage( metaData ) );
                return;
            }

            handleMetadataAddition( metaData );

            hide();
        };
    }

    protected abstract boolean isMetadataUnique( final String metadataName );

    protected abstract String metadataNotUniqueMessage( final String metadataName );

    protected abstract void handleMetadataAddition( final String metadataName );
}
