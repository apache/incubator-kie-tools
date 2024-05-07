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

package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest22 {



    @WorkbenchPanel( parts = "noParameterScreen" )
    Object nopParameter = new FlowPanel();

    @WorkbenchPanel( isDefault = true,
                     parts = "oneParameterScreen?uber=fire" )
    Object oneParameter = new FlowPanel();

    @WorkbenchPanel( parts = "twoParametersScreen?uber=fire&uber1=fire1" )
    Object twoParameters = new FlowPanel();

    @PostConstruct
    public void setup() {

    }

    @OnStartup
    public void onStartup() {
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
    }
}
