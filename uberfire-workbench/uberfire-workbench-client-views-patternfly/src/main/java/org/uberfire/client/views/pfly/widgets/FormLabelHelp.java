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

package org.uberfire.client.views.pfly.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import org.gwtbootstrap3.client.ui.FormLabel;

/**
 * Created by Cristiano Nicolai.
 */
public class FormLabelHelp extends Composite implements HasText {

    private HelpIcon helpIcon;
    private final FormLabel formLabel = new FormLabel();
    private final FlowPanel panel = new FlowPanel();

    public FormLabelHelp() {
        initWidget( panel );
        addStyleName( "uf-form-label" );
        panel.add( formLabel );
    }

    public void setHelpTitle( final String title ) {
        getHelpIcon().setHelpTitle( title );
    }

    public void setHelpContent( final String content ) {
        getHelpIcon().setHelpContent( content );
    }

    private HelpIcon getHelpIcon() {
        if ( helpIcon == null ) {
            helpIcon = new HelpIcon();
            panel.add( helpIcon );
        }
        return helpIcon;
    }

    @Override
    public void setText( final String text ) {
        formLabel.setText( text );
    }

    @Override
    public String getText() {
        return formLabel.getText();
    }

    public void setFor( final String forValue ) {
        formLabel.setFor( forValue );
    }

    public void setShowRequiredIndicator( final boolean required ){
        formLabel.setShowRequiredIndicator( required );
    }
}
