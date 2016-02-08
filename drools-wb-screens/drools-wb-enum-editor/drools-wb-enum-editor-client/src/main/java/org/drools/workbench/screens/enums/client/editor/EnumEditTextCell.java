/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.enums.client.editor;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.drools.workbench.screens.enums.client.resources.EnumEditorResources;
import org.drools.workbench.screens.enums.client.resources.i18n.EnumEditorConstants;

public class EnumEditTextCell extends EditTextCell {

    interface Template extends SafeHtmlTemplates {

        @Template("<div class=\"{0}\" title=\"{1}\">{2}</div>")
        SafeHtml cellContent( final String css,
                              final String tooltip,
                              final String value );
    }

    private static Template template;

    public EnumEditTextCell() {
        if ( template == null ) {
            template = GWT.create( Template.class );
        }
    }

    @Override
    public void onBrowserEvent( final Context context,
                                final Element parent,
                                final String value,
                                final NativeEvent event,
                                final ValueUpdater<String> valueUpdater ) {
        final EnumRow enumRow = (EnumRow) context.getKey();
        if ( !enumRow.disabled() ) {
            doOnBrowserEvent( context,
                              parent,
                              value,
                              event,
                              valueUpdater );
        }
    }

    //Package protected to verify behaviour in Unit Tests
    void doOnBrowserEvent( final Context context,
                           final Element parent,
                           final String value,
                           final NativeEvent event,
                           final ValueUpdater<String> valueUpdater ) {
        super.onBrowserEvent( context,
                              parent,
                              value,
                              event,
                              valueUpdater );

    }

    @Override
    public void render( final Context context,
                        final String value,
                        final SafeHtmlBuilder sb ) {
        final EnumRow enumRow = (EnumRow) context.getKey();
        if ( !enumRow.disabled() ) {
            doRender( context,
                      value,
                      sb );

        } else {
            if ( !( value == null || value.isEmpty() ) ) {
                sb.append( template.cellContent( EnumEditorResources.INSTANCE.css().disabled(),
                                                 EnumEditorConstants.INSTANCE.invalidDefinitionDisabled(),
                                                 value ) );
            } else {
                sb.append( template.cellContent( EnumEditorResources.INSTANCE.css().disabled(),
                                                 EnumEditorConstants.INSTANCE.invalidDefinitionDisabled(),
                                                 "\u00A0" ) );
            }
        }
    }

    //Package protected to verify behaviour in Unit Tests
    void doRender( final Context context,
                   final String value,
                   final SafeHtmlBuilder sb ) {
        super.render( context,
                      value,
                      sb );
    }

}
