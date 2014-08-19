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
package org.uberfire.workbench.services;

import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.SplashScreenFilter;

/**
 * Workbench services
 */
@Remote
public interface WorkbenchServices {

    public void save( String perspectiveId, final PerspectiveDefinition perspective );

    public void save( final SplashScreenFilter splashFilter );

    public PerspectiveDefinition loadPerspective( final String perspectiveId );

    public SplashScreenFilter loadSplashScreenFilter( final String filterName );

    public Map<String, String> loadDefaultEditorsMap();

    void saveDefaultEditors( Map<String, String> properties );
}
