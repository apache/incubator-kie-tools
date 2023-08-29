/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.widgets.client.docks;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;

/**
 * Base for any panel that is opened into the an editor dock.
 * To use this dock it is necessary to register the dock for the editor that uses it.
 *
 * @see org.kie.workbench.common.widgets.metadata.client.KieEditor.registerDock(String, IsWidget)
 */
@ApplicationScoped
@Named(DockPlaceHolder.IDENTIFIER)
public class DockPlaceHolder extends AbstractActivity {

    public static final String IDENTIFIER = "org.docks.PlaceHolder";

    private DockPlaceHolderBaseView view;

    public DockPlaceHolder() {
        // CDI
    }

    @Inject
    public void init(final DockPlaceHolderBaseView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.DOCK;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public IsWidget getView() {
        return view;
    }

    @Override
    public IsWidget getWidget() {
        return view;
    }

    public void setView(final IsWidget widget) {
        view.clear();
        view.setWidget(widget);
    }
}
