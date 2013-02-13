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
package org.uberfire.client.workbench.services;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.client.workbench.model.PerspectiveDefinition;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * Workbench services
 */
@Remote
public interface WorkbenchServices {

    public void save(final PerspectiveDefinition perspective);

    public PerspectiveDefinition load(final String perspectiveName);

    public Map<String, String> loadDefaultEditorsMap();

    void saveDefaultEditors(Map<String, String> properties);
}
