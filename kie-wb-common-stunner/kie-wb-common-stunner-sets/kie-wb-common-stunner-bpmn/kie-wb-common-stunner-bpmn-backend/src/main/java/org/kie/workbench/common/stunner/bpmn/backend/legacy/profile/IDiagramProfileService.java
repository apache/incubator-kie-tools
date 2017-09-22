/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.backend.legacy.profile;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * The profile service gives access to profiles.
 * @author Antoine Toulme
 */
public interface IDiagramProfileService {

    /**
     * @param request the context
     * @param name the name of the profile
     * @return the profile with the name in the context of that request
     */
    public IDiagramProfile findProfile(HttpServletRequest request,
                                       String name);

    /**
     * @param request the context
     * @return the profiles for the given context.
     */
    public Collection<IDiagramProfile> getProfiles(HttpServletRequest request);

    /**
     * Initialize the service with a particular context
     * @param servletContext
     */
    public void init(ServletContext servletContext);
}
