/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import java.util.Map;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.InputSize;
import org.uberfire.ext.layout.editor.client.components.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.components.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups.EditHTML;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;

@Dependent
public class HTMLLayoutDragComponent implements PerspectiveEditorDragComponent,
                                                HasModalConfiguration {

    public static final String HTML_CODE_PARAMETER = "HTML_CODE";

    @Override
    public IsWidget getDragWidget() {
        TextBox textBox = GWT.create( TextBox.class );
        textBox.setPlaceholder( CommonConstants.INSTANCE.HTMLComponent() );
        textBox.setReadOnly( true );
        textBox.setSize( InputSize.DEFAULT );
        return textBox;
    }

    @Override
    public IsWidget getPreviewWidget( RenderingContext container ) {
        return getShowWidget( container );
    }

    @Override
    public IsWidget getShowWidget( RenderingContext context ) {
        Map<String, String> properties = context.getComponent().getProperties();
        String html = properties.get( HTMLLayoutDragComponent.HTML_CODE_PARAMETER );
        return html == null ? null : new HTMLPanel( html );
    }

    @Override
    public Modal getConfigurationModal( ModalConfigurationContext ctx ) {
        return new EditHTML( ctx );
    }
}