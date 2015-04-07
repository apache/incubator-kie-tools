/*
* Copyright 2013 JBoss Inc
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
package org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups;

import java.util.Map;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.layout.editor.client.LayoutEditorPluginAPI;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class EditHTML
        extends BaseModal {

    private final EditorWidget parent;

    private final LayoutEditorPluginAPI layoutEditorPluginAPI;

    private static final String DEFAULT_HTML = "Add your HTML here...";

    @UiField
    TextArea textArea;

    public interface Listener {

        void onSave();

        void onClose();
    }

    interface Binder
            extends
            UiBinder<Widget, EditHTML> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public EditHTML( EditorWidget parent,
                     LayoutEditorPluginAPI layoutEditorPluginAPI ) {
        this.parent = parent;
        this.layoutEditorPluginAPI = layoutEditorPluginAPI;
        setTitle( CommonConstants.INSTANCE.EditHtml() );
        add( uiBinder.createAndBindUi( this ) );
        setupHTMLEditor();

        add( new ModalFooterOKCancelButtons(
                     new Command() {
                         @Override
                         public void execute() {
                             okButton();
                         }
                     },
                     new Command() {
                         @Override
                         public void execute() {
                             cancelButton();
                         }
                     } )
           );
    }

    protected KeyDownHandler getEnterDomHandler() {
        return new KeyDownHandler() {
            @Override
            public void onKeyDown( KeyDownEvent event ) {
                if ( !isInsideEditHTMLWidget( event ) && event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    if ( handleDefaultAction() ) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                }
            }

            private boolean isInsideEditHTMLWidget( KeyDownEvent event ) {
                return event.getSource() == EditHTML.this;
            }
        };
    }

    private void setupHTMLEditor() {
        final Map<String, String> parameters = layoutEditorPluginAPI.getLayoutComponentProperties( parent );

        final String html = parameters.get( HTMLLayoutDragComponent.HTML_CODE_PARAMETER );

        if ( html == null || html.isEmpty() ) {
            this.textArea.setText( DEFAULT_HTML );
        } else {
            this.textArea.setText( html );
        }
    }

    public void show() {
        super.show();
    }

    void cancelButton() {
        super.hide();
    }

    void okButton() {
        layoutEditorPluginAPI.addLayoutComponentProperty( parent, HTMLLayoutDragComponent.HTML_CODE_PARAMETER, textArea.getText() );
        super.hide();
    }

}
