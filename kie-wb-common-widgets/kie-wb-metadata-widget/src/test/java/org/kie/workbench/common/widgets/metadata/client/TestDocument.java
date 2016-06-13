/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.metadata.client;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mvp.PlaceRequest;

public class TestDocument implements KieDocument {

    private String version;
    private ObservablePath latestPath;
    private ObservablePath currentPath;
    private PlaceRequest placeRequest;
    private boolean isReadOnly;
    private Integer originalHashCode;
    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateEvent;

    public TestDocument( final ObservablePath currentPath,
                         final PlaceRequest placeRequest ) {
        this.currentPath = currentPath;
        this.latestPath = currentPath;
        this.placeRequest = placeRequest;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion( final String version ) {
        this.version = version;
    }

    @Override
    public ObservablePath getLatestPath() {
        return this.latestPath;
    }

    @Override
    public void setLatestPath( final ObservablePath latestPath ) {
        this.latestPath = latestPath;
    }

    @Override
    public ObservablePath getCurrentPath() {
        return this.currentPath;
    }

    @Override
    public void setCurrentPath( final ObservablePath currentPath ) {
        this.currentPath = currentPath;
    }

    @Override
    public PlaceRequest getPlaceRequest() {
        return this.placeRequest;
    }

    @Override
    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    @Override
    public void setReadOnly( final boolean isReadOnly ) {
        this.isReadOnly = isReadOnly;
    }

    @Override
    public Integer getOriginalHashCode() {
        return this.originalHashCode;
    }

    @Override
    public void setOriginalHashCode( final Integer originalHashCode ) {
        this.originalHashCode = originalHashCode;
    }

    @Override
    public ObservablePath.OnConcurrentUpdateEvent getConcurrentUpdateSessionInfo() {
        return this.concurrentUpdateEvent;
    }

    @Override
    public void setConcurrentUpdateSessionInfo( final ObservablePath.OnConcurrentUpdateEvent concurrentUpdateEvent ) {
        this.concurrentUpdateEvent = concurrentUpdateEvent;
    }

}
