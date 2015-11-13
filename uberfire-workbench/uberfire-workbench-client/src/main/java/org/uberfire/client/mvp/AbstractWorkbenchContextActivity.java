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
package org.uberfire.client.mvp;

import org.uberfire.client.annotations.WorkbenchContext;
import org.uberfire.workbench.model.PanelDefinition;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Implementation of behaviour common to all context activities. Concrete implementations are typically not written by
 * hand; rather, they are generated from classes annotated with {@link WorkbenchContext}.
 */
public abstract class AbstractWorkbenchContextActivity extends AbstractActivity implements ContextActivity {

    protected PanelDefinition panel;

    public AbstractWorkbenchContextActivity( final PlaceManager placeManager ) {
        super( placeManager );
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
