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

package org.kie.guvnor.guided.rule.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.models.commons.shared.rule.FreeFormLine;
import org.kie.guvnor.guided.rule.client.editor.DynamicTextArea;
import org.kie.guvnor.guided.rule.client.editor.FreeFormLinePopup;
import org.kie.guvnor.guided.rule.client.editor.RuleModeller;
import org.kie.guvnor.guided.rule.client.resources.GuidedRuleEditorResources;
import org.kie.guvnor.guided.rule.client.resources.i18n.Constants;
import org.kie.guvnor.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.uberfire.client.common.DirtyableFlexTable;

/**
 * Free form DRL line widget
 */
public class FreeFormLineWidget extends RuleModellerWidget {

    private FreeFormLine action;
    private DirtyableFlexTable layout = new DirtyableFlexTable();
    private DynamicTextArea textArea = new DynamicTextArea();
    private boolean readOnly;

    public FreeFormLineWidget( RuleModeller mod,
                               EventBus eventBus,
                               FreeFormLine p ) {
        this( mod,
              eventBus,
              p,
              null );
    }

    /**
     * Creates a new FactPatternWidget
     * @param mod
     * @param p
     * @param readOnly if the widget should be in RO mode. If this parameter is null,
     * the readOnly attribute is calculated.
     */
    public FreeFormLineWidget( RuleModeller mod,
                               EventBus eventBus,
                               FreeFormLine p,
                               Boolean readOnly ) {
        super( mod,
               eventBus );
        this.action = p;

        if ( readOnly == null ) {
            this.readOnly = false;
        } else {
            this.readOnly = readOnly;
        }

        textArea.setMaxLines( 5 );

        layout.setWidget( 0,
                          0,
                          createTextBox() );
        layout.setWidget( 0,
                          1,
                          createEditIcon() );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setAlignment( 0,
                                0,
                                HasHorizontalAlignment.ALIGN_LEFT,
                                HasVerticalAlignment.ALIGN_BOTTOM );
        //Let the TextArea expand the first cell to fit
        formatter.setWidth( 0,
                            0,
                            "1px" );

        formatter.setAlignment( 0,
                                1,
                                HasHorizontalAlignment.ALIGN_LEFT,
                                HasVerticalAlignment.ALIGN_TOP );

        if ( this.readOnly ) {
            this.layout.addStyleName( "editor-disabled-widget" );
        }

        initWidget( layout );

    }

    private Widget createTextBox() {
        textArea.setTitle( Constants.INSTANCE.ThisIsADrlExpressionFreeForm() );
        textArea.setText( this.action.getText() );
        textArea.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange( ValueChangeEvent<String> event ) {
                action.setText( textArea.getText() );
                setModified( true );
            }

        } );
        return textArea;
    }

    private Widget createEditIcon() {

        Image btn;
        if ( !this.readOnly ) {
            btn = GuidedRuleEditorImages508.INSTANCE.Edit();
            btn.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    final FreeFormLinePopup popup = new FreeFormLinePopup( Constants.INSTANCE.FreeFormDrl(),
                                                                           action.getText() );
                    popup.addOKClickHandler( new ClickHandler() {

                        public void onClick( ClickEvent event ) {
                            action.setText( popup.getText() );
                            textArea.setText( action.getText() );
                            setModified( true );
                            popup.hide();
                        }

                    } );
                    popup.show();
                }

            } );
        } else {
            btn = new Image( GuidedRuleEditorResources.INSTANCE.images().editDisabled() );
        }

        return btn;
    }

    @Override
    public boolean isDirty() {
        return layout.hasDirty();
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isFactTypeKnown() {
        return true;
    }

}
