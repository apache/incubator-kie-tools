/*
* Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.server;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.guvnor.m2repo.service.M2RepoService;

import static org.mockito.Mockito.*;

@ApplicationScoped
@Priority(1) // needed in order to inject the @Alternatives outside of this bean bundle (aka maven module)
public class TestAppSetup {

    @Produces
    @Alternative
    public M2RepoService m2RepoService() {
        return mock(M2RepoService.class);
    }

    @Produces
    @Alternative
    @Named("uf")
    public ServletContext servletContext() {
        return mock(ServletContext.class);
    }
}
