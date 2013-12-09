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

package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.uberfire.client.common.DirtyableHorizontalPane;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.InfoPopup;

public class AttributeSelectorPopup extends FormStylePopup {

    private final TextBox box = new TextBox();

    public AttributeSelectorPopup( final RuleModel model,
                                   boolean lockLHS,
                                   boolean lockRHS,
                                   final Command refresh ) {
        super( getImage(),
                GuidedRuleEditorResources.CONSTANTS.AddAnOptionToTheRule() );

        setTextBox( model,
                    refresh );

        setListBox( model,
                    refresh );

        setFreezePanel( model,
                        lockLHS,
                        lockRHS,
                        refresh );

    }

    private static Image getImage() {
        Image image = new Image( GuidedRuleEditorResources.INSTANCE.images().config() );
        image.setAltText( GuidedRuleEditorResources.CONSTANTS.Config() );
        return image;
    }

    private void setTextBox( final RuleModel model,
                             final Command refresh ) {
        box.setVisibleLength( 15 );

        DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();
        horiz.add( box );
        horiz.add( getAddButton( model,
                                 refresh,
                                 box ) );

        addAttribute( GuidedRuleEditorResources.CONSTANTS.Metadata3(),
                      horiz );

    }

    private void setListBox( final RuleModel model,
                             final Command refresh ) {

        final ListBox list = RuleAttributeWidget.getAttributeList();

        // Remove any attributes already added
        for ( RuleAttribute at : model.attributes ) {
            for ( int iItem = 0; iItem < list.getItemCount(); iItem++ ) {
                if ( list.getItemText( iItem ).equals( at.getAttributeName() ) ) {
                    list.removeItem( iItem );
                    break;
                }
            }
        }

        list.setSelectedIndex( 0 );

        list.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                String attr = list.getItemText( list.getSelectedIndex() );
                if ( attr.equals( RuleAttributeWidget.LOCK_LHS ) || attr.equals( RuleAttributeWidget.LOCK_RHS ) ) {
                    model.addMetadata( new RuleMetadata( attr,
                                                         "true" ) );
                } else {
                    model.addAttribute( new RuleAttribute( attr,
                                                           "" ) );
                }
                refresh.execute();
                hide();
            }
        } );
        addAttribute( GuidedRuleEditorResources.CONSTANTS.Attribute1(),
                      list );

    }

    private void setFreezePanel( final RuleModel model,
                                 boolean lockLHS,
                                 boolean lockRHS,
                                 final Command refresh ) {
        Button freezeConditions = new Button( GuidedRuleEditorResources.CONSTANTS.Conditions() );
        freezeConditions.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                model.addMetadata( new RuleMetadata( RuleAttributeWidget.LOCK_LHS,
                                                     "true" ) );
                refresh.execute();
                hide();
            }
        } );
        Button freezeActions = new Button( GuidedRuleEditorResources.CONSTANTS.Actions() );
        freezeActions.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                model.addMetadata( new RuleMetadata( RuleAttributeWidget.LOCK_RHS,
                                                     "true" ) );
                refresh.execute();
                hide();
            }
        } );
        HorizontalPanel hz = new HorizontalPanel();
        if ( !lockLHS ) {
            hz.add( freezeConditions );
        }
        if ( !lockRHS ) {
            hz.add( freezeActions );
        }
        hz.add( new InfoPopup( GuidedRuleEditorResources.CONSTANTS.FrozenAreas(),
                GuidedRuleEditorResources.CONSTANTS.FrozenExplanation() ) );

        if ( hz.getWidgetCount() > 1 ) {
            addAttribute( GuidedRuleEditorResources.CONSTANTS.FreezeAreasForEditing(),
                          hz );
        }
    }

    private Image getAddButton( final RuleModel model,
                                final Command refresh,
                                final TextBox box ) {
        final Image addbutton = GuidedRuleEditorImages508.INSTANCE.NewItem();
        addbutton.setAltText( GuidedRuleEditorResources.CONSTANTS.AddMetadataToTheRule() );
        addbutton.setTitle( GuidedRuleEditorResources.CONSTANTS.AddMetadataToTheRule() );

        addbutton.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {

                //Check MetaData has a name
                final String metaData = box.getText().trim();
                if ( metaData.isEmpty() ) {
                    Window.alert( GuidedRuleEditorResources.CONSTANTS.MetadataNameEmpty() );
                    return;
                }

                //Check MetaData is unique
                boolean isUnique = true;
                for ( RuleMetadata rm : model.metadataList ) {
                    if ( rm.getAttributeName().equals( metaData ) ) {
                        isUnique = false;
                        break;
                    }
                }
                if ( !isUnique ) {
                    Window.alert( GuidedRuleEditorResources.CONSTANTS.MetadataNotUnique0( metaData ) );
                    return;
                }

                model.addMetadata( new RuleMetadata( box.getText(),
                                                     "" ) );
                refresh.execute();
                hide();
            }
        } );
        return addbutton;
    }

}
