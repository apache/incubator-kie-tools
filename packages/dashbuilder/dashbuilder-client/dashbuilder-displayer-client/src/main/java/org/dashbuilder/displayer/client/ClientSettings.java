/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.displayer.DisplayerType;

/**
 * This class provides some methods for defining the behaviour of the Dashbuilder client layer
 */
@ApplicationScoped
public class ClientSettings {

    @Inject RendererManager rendererManager;
    @Inject DataSetClientServices dataSetClientServices;

    /**
     * Turns off the ability to push data sets from server (is enabled by default). Push is very useful when dealing
     * with small size data sets as the performance of any lookup request is much faster on client.
     */
    public void turnOffDataSetPush() {
        dataSetClientServices.setPushRemoteDataSetEnabled(false);
    }

    /**
     * It's possible to have one or more renderer libs available per displayer type. If a displayer does not define
     * its renderer lib then the default one is taken. This method can be used to define the default renderers.
     *
     * @param displayerType The type of the displayer we want to configure.
     * @param rendererLib The UUID of the renderer library.
     */
    public void setDefaultRenderer(DisplayerType displayerType, String rendererLib) {
        rendererManager.setDefaultRenderer(displayerType, rendererLib);
    }
}
