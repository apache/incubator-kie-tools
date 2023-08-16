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


package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.Objects;

import javax.annotation.PostConstruct;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

public abstract class AbstractSubDockPresenter<T extends SubDockView> extends AbstractActivity implements SubDockView.Presenter {

    public static final int DEFAULT_PREFERRED_WIDHT = 300;

    protected String title;
    protected T view;
    protected ObservablePath currentPath;

    public AbstractSubDockPresenter() {
    }

    public AbstractSubDockPresenter(T view) {
        this.view = view;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public IsWidget getWidget() {
        return asWidget();
    }

    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void setCurrentPath(ObservablePath path) {
        currentPath = path;
    }

    @Override
    public boolean isCurrentlyShow(ObservablePath path) {
        return Objects.equals(currentPath, path);
    }

   @Override
   public ResourceType getResourceType() {
        return ActivityResourceType.DOCK;
   }

    public boolean isOpen() {
        return open;
    }
}