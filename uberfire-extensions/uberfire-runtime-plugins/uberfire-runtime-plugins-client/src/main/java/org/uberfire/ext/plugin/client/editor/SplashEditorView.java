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

package org.uberfire.ext.plugin.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.plugin.model.Framework;

import static org.uberfire.ext.plugin.client.code.CodeList.*;

@Dependent
public class SplashEditorView
        extends RuntimePluginBaseView
        implements RequiresResize {

    interface ViewBinder
            extends
            UiBinder<Widget, SplashEditorView> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    FlowPanel htmlPanel;

    @UiField
    FlowPanel formArea;

    @UiField
    ListBox framework;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        editor.setup( MAIN, DIVIDER, ON_OPEN, ON_CLOSE, ON_STARTUP, ON_SHUTDOWN, DIVIDER, TITLE, BODY_HEIGHT, INTERCEPTION_POINTS );
        htmlPanel.add( editor );
    }

    @Override
    protected void setFramework( final Collection<Framework> frameworks ) {
        if ( frameworks != null && !frameworks.isEmpty() ) {
            final Framework framework = frameworks.iterator().next();
            for ( int i = 0; i < this.framework.getItemCount(); i++ ) {
                if ( this.framework.getItemText( i ).equalsIgnoreCase( framework.toString() ) ) {
                    this.framework.setSelectedIndex( i );
                    return;
                }
            }
        }
        this.framework.setSelectedIndex( 0 );
    }

    @Override
    protected Collection<Framework> getFrameworks() {
        if ( framework.getSelectedValue().equalsIgnoreCase( "(Framework)" ) ) {
            return Collections.emptyList();
        }
        return new ArrayList<Framework>() {{
            add( Framework.valueOf( framework.getSelectedValue().toUpperCase() ) );
        }};
    }

    @Override
    public void onResize() {
        htmlPanel.setHeight( getParent().getParent().getOffsetHeight() + "px" );
        editor.onResize();
    }
}