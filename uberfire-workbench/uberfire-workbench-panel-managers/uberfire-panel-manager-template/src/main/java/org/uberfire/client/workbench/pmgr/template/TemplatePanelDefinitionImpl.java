/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.pmgr.template;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.client.mvp.TemplatePerspectiveActivity;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

/**
 * Default implementation of PanelDefinition
 */
@Portable
public class TemplatePanelDefinitionImpl
        extends PanelDefinitionImpl {

    private transient TemplatePerspectiveActivity perspective;

    private String fieldName;

    public TemplatePanelDefinitionImpl() {

    }

    public TemplatePanelDefinitionImpl( TemplatePerspectiveActivity perspective,
                                        PanelType type,
                                        String fieldName ) {
        super( type );
        this.perspective = perspective;
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void appendChild( final Position position,
                             final PanelDefinition panel ) {

        super.appendChild( position, panel );
    }

    @Override
    public PanelDefinition getChild( final Position position ) {
        return null;
    }

    public void setPerspective( Widget widget ) {
        perspective.setWidget( getFieldName(), widget );
    }

    public Widget getRealPresenterWidget() {
        return perspective.getRealPresenterWidget();
    }
}
