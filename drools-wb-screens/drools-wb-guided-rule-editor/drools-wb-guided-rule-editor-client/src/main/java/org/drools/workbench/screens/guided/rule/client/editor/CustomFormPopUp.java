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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.guvnor.common.services.workingset.client.factconstraints.customform.CustomFormConfiguration;
import org.uberfire.client.common.FormStylePopup;

public class CustomFormPopUp extends FormStylePopup {

    private final CustomFormConfiguration configuration;
    private final Button                  okButton;
    private final Button                  cancelButton;
    private       Frame                   externalFrame;

    public CustomFormPopUp( Image image,
                            String title,
                            CustomFormConfiguration configuration ) {
        super( image, title );
        this.configuration = configuration;

        this.externalFrame = new Frame();
        this.externalFrame.setWidth( configuration.getCustomFormWidth() + "px" );
        this.externalFrame.setHeight( configuration.getCustomFormHeight() + "px" );

        VerticalPanel vp = new VerticalPanel();
        vp.setWidth( "100%" );
        vp.setHeight( "100%" );
        vp.add( this.externalFrame );

        okButton = new Button( GuidedRuleEditorResources.CONSTANTS.OK() );

        //cancel button with default handler
        cancelButton = new Button( GuidedRuleEditorResources.CONSTANTS.Cancel(),
                                   new ClickHandler() {
                                       public void onClick( ClickEvent event ) {
                                           hide();
                                       }
                                   } );

        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth( "100%" );
        hp.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
        hp.add( okButton );
        hp.add( cancelButton );

        vp.add( hp );

        this.addRow( vp );

    }

    public void addOkButtonHandler( ClickHandler handler ) {
        this.okButton.addClickHandler( handler );
    }

    public void addCancelButtonHandler( ClickHandler handler ) {
        this.cancelButton.addClickHandler( handler );
    }

    public void show( String selectedId,
                      String selectedValue ) {

        String url = configuration.getCustomFormURL();
        if ( url == null || url.trim().equals( "" ) ) {
            //TODO: show an error
            return;
        } else {
            String parameters = "cf_id=" + selectedId + "&cf_value=" + selectedValue + "&factType=" + this.configuration.getFactType() + "&fieldName=" + this.configuration.getFieldName();
            //advanced url parsing for adding attributes :P
            url = url + ( url.contains( "?" ) ? "&" : "?" ) + parameters;
            this.externalFrame.setUrl( url );
            this.show();
        }
    }

    private Element getExternalFrameElement( String id ) {
        IFrameElement iframe = IFrameElement.as( this.externalFrame.getElement() );
        return iframe.getContentDocument().getElementById( id );
    }

    public String getFormId() {
        return this.getExternalFrameElement( "cf_id" ).getPropertyString( "value" );
    }

    public String getFormValue() {
        return this.getExternalFrameElement( "cf_value" ).getPropertyString( "value" );
    }

}
