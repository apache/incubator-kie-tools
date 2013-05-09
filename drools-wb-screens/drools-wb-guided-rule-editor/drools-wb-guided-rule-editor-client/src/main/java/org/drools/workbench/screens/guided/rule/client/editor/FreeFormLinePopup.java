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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.uberfire.client.common.Popup;

/**
 * A pop-up in which to edit 'free-form DRL'
 */
public class FreeFormLinePopup extends Popup {

    private static FreeFormLinePopupBinder uiBinder = GWT.create( FreeFormLinePopupBinder.class );

    //UI
    interface FreeFormLinePopupBinder
            extends
            UiBinder<Widget, FreeFormLinePopup> {

    }

    private String newText;
    private String originalText;

    @UiField()
    protected DynamicTextArea textArea = new DynamicTextArea();

    @UiField()
    protected Button btnOK = new Button( Constants.INSTANCE.OK() );

    @UiField()
    protected Button btnCancel = new Button( Constants.INSTANCE.Cancel() );

    @UiField()
    protected VerticalPanel content = new VerticalPanel();

    public FreeFormLinePopup( String title,
                              String text ) {
        uiBinder.createAndBindUi( this );

        this.setModal( true );
        this.setTitle( title );
        this.newText = text;
        this.originalText = text;
        this.textArea.setText( text );
        this.content.add( textArea );

        btnCancel.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                cancelPopup();
            }

        } );

        textArea.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange( ValueChangeEvent<String> event ) {
                newText = textArea.getText();
            }

        } );

        textArea.addResizeHandler( new ResizeHandler() {

            public void onResize( ResizeEvent event ) {
                center();
            }
        } );

        HorizontalPanel hp = new HorizontalPanel();
        hp.add( btnOK );
        hp.add( new HTML( "&nbsp;" ) );
        hp.add( btnCancel );
        this.content.add( hp );

    }

    @Override
    public Widget getContent() {
        return content;
    }

    public String getText() {
        return this.newText;
    }

    public HandlerRegistration addOKClickHandler( ClickHandler handler ) {
        return btnOK.addClickHandler( handler );
    }

    private void cancelPopup() {
        newText = originalText;
        this.hide();
    }

}
