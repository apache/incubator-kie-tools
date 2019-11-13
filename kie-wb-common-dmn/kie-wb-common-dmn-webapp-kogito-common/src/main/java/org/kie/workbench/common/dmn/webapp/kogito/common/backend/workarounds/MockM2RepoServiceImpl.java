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
package org.kie.workbench.common.dmn.webapp.kogito.common.backend.workarounds;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.model.JarListPageRequest;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.paging.PageResponse;

@Service
@ApplicationScoped
public class MockM2RepoServiceImpl implements M2RepoService {

    @Override
    public String getPomText(final String path) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public GAV loadGAVFromJar(final String path) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public PageResponse<JarListPageRow> listArtifacts(final JarListPageRequest pageRequest) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public String getRepositoryURL() {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public String getKModuleText(final String path) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public String getKieDeploymentDescriptorText(final String path) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }
}
