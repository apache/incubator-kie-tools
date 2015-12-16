/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.testscenario.client;

import java.util.Set;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;

public class AuditLog
        extends Composite {

    final ScrollPanel content = new ScrollPanel() {{
        setHeight( "300px" );
    }};

    public AuditLog() {
        initWidget( new Panel() {{
            add( new PanelGroup() {{

                final PanelCollapse collapse = new PanelCollapse() {{
                    add( new PanelBody() {{
                        add( content );
                    }} );
                }};
                add( new PanelHeader() {{
                    setDataToggle( Toggle.COLLAPSE );
                    setDataParent( getId() );
                    setDataTargetWidget( collapse );
                    add( new Heading( HeadingSize.H4 ) {{
                        add( new Anchor() {{
                            setIcon( IconType.CERTIFICATE );
                            setText( TestScenarioConstants.INSTANCE.AuditLogColon() );
                        }} );
                    }} );
                }} );
                getElement().getStyle().setMarginBottom( 0, Style.Unit.PX );
                add( collapse );
            }} );
        }} );

        getElement().getStyle().setMarginTop( 2, Style.Unit.PX );
        getElement().getStyle().setMarginBottom( 2, Style.Unit.PX );
    }

    public void fill( final Set<String> log ) {
        setVisible( true );
        content.clear();

        final Container list = new Container() {{
            setFluid( true );
        }};

        for ( final String line : log ) {
            list.add( new Row() {{
                add( new Line( line ) {{
                    addStyleName( ColumnSize.MD_12.getCssName() );
                }} );
            }} );
        }

        content.add( list );
    }

}
