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

package org.kie.workbench.common.stunner.sw.client;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.annotation.Application;
import org.kie.j2cl.tools.processors.annotations.GWT3EntryPoint;
import org.kie.workbench.common.stunner.client.lienzo.StunnerLienzoCore;
import org.kie.workbench.common.stunner.sw.client.editor.DiagramEditorActivity;
import org.uberfire.client.workbench.WorkbenchEntryPoint;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Application(packages = {"org.kie", "org.uberfire", "org.appformer"})
public class MainEntryPoint {


    @Inject
    private DiagramEditorActivity diagramEditor;

    @Inject
    private WorkbenchEntryPoint workbenchEntryPoint;

    @GWT3EntryPoint
    public void onModuleLoad() {
        new StunnerLienzoCore().init();

        new MainEntryPointBootstrap(this).initialize();
    }

    @PostConstruct
    public void initialize() {
        diagramEditor.onStartup(new DefaultPlaceRequest());
        workbenchEntryPoint.afterInitialization();
    }

}
