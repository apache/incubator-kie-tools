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
package org.uberfire.client.mvp;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;

/**
 * Base class for Context Activities
 */
public abstract class AbstractWorkbenchContextActivity
        extends AbstractActivity
        implements ContextActivity {

    protected PanelDefinition panel;

    public AbstractWorkbenchContextActivity( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void launch( final PlaceRequest place,
                        final Command callback ) {
        super.launch( place, callback );
        onOpen();
    }

    @Override
    public void onAttach( final PanelDefinition panel ) {
        this.panel = panel;
    }

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }
}
