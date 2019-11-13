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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.refactoring.service.AssetsUsageService;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class MockAssetsUsageServiceImpl implements AssetsUsageService {

    @Override
    public List<Path> getAssetUsages(final String resourceFQN,
                                     final ResourceType resourceType,
                                     final Path assetPath) {
        throw new UnsupportedOperationException("Not available in kogito");
    }

    @Override
    public List<Path> getAssetPartUsages(final String resourceFQN,
                                         final String resourcePart,
                                         final PartType partType,
                                         final Path assetPath) {
        throw new UnsupportedOperationException("Not available in kogito");
    }
}
