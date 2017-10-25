/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.openshift.service;

import org.guvnor.ala.ui.openshift.model.DefaultSettings;
import org.guvnor.ala.ui.openshift.model.TemplateDescriptorModel;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * This service has specific methods that are required by the OpenShift ui.
 */
@Remote
public interface OpenShiftClientService {

    /**
     * Gets the OpenShift provisioning by default settings.
     */
    DefaultSettings getDefaultSettings();

    /**
     * Gets the template descriptor model for a template pointed by a given url.
     * @param url location of the template file.
     * @return the template descriptor model.
     */
    TemplateDescriptorModel getTemplateModel(final String url);

    /**
     * Indicates if an OpenShift project name is valid.
     * @param projectName an OpenShift project name to check.
     * @return true if the project name is valid, false in any other case.
     */
    boolean isValidProjectName(final String projectName);
}
