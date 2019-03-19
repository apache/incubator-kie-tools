/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.uberfire.ext.metadata.MetadataConfig;

import static org.mockito.Mockito.mock;

@ApplicationScoped
public class TestAppSetup {

    @Produces
    public User user() {
        return new UserImpl("admin");
    }

    @Produces
    public MVELEvaluator mvelEvaluator() {
        return new RawMVELEvaluator();
    }

    @Produces
    @Named("luceneConfig")
    public MetadataConfig metadataConfig() {
        return mock(MetadataConfig.class);
    }

    @Produces
    @Alternative
    public M2RepoService m2RepoService() {
        return mock(M2RepoService.class);
    }
}
