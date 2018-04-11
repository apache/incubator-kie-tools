/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.backend.workitem.deploy;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionResources;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.deploy.WorkItemDefinitionDeployService;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionRemoteRequest;
import org.kie.workbench.common.stunner.bpmn.workitem.IconDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionService;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager.Asset;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager.AssetBuilder;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager.Assets;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It deploys the work item definitions specified in the JVM system properties by performing a remote
 * call to the service repository, if any.
 */
@ApplicationScoped
public class WorkItemDefinitionRemoteDeployService implements WorkItemDefinitionDeployService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkItemDefinitionRemoteDeployService.class.getName());

    public static final String PROPERTY_SERVICE_REPO = "org.jbpm.service.repository";
    public static final String PROPERTY_SERVICE_REPO_TASKNAMES = "org.jbpm.service.servicetasknames";
    private static final String DEPLOY_MESSAGE = "Deployment of the work item definitions from repository: ";

    private final BackendFileSystemManager backendFileSystemManager;
    private final WorkItemDefinitionService<WorkItemDefinitionRemoteRequest> remoteLookupService;
    private final WorkItemDefinitionResources resources;
    private final WorkItemDefinitionProjectInstaller projectInstaller;
    private final Function<WorkItemDefinition, Asset> widAssetBuilder;
    private final Function<WorkItemDefinition, Asset> iconAssetBuilder;

    // CDI Proxy.
    protected WorkItemDefinitionRemoteDeployService() {
        this(null, null, null, null);
    }

    @Inject
    public WorkItemDefinitionRemoteDeployService(final WorkItemDefinitionService<WorkItemDefinitionRemoteRequest> remoteLookupService,
                                                 final BackendFileSystemManager backendFileSystemManager,
                                                 final WorkItemDefinitionResources resources,
                                                 final WorkItemDefinitionProjectInstaller projectInstaller) {
        this(remoteLookupService,
             backendFileSystemManager,
             resources,
             projectInstaller,
             wid -> WorkItemDefinitionRemoteDeployService.createWIDAsset(wid).orElse(null),
             wid -> WorkItemDefinitionRemoteDeployService.createIconAsset(wid).orElse(null));
    }

    WorkItemDefinitionRemoteDeployService(final WorkItemDefinitionService<WorkItemDefinitionRemoteRequest> remoteLookupService,
                                          final BackendFileSystemManager backendFileSystemManager,
                                          final WorkItemDefinitionResources resources,
                                          final WorkItemDefinitionProjectInstaller projectInstaller,
                                          final Function<WorkItemDefinition, Asset> widAssetBuilder,
                                          final Function<WorkItemDefinition, Asset> iconAssetBuilder) {
        this.remoteLookupService = remoteLookupService;
        this.backendFileSystemManager = backendFileSystemManager;
        this.resources = resources;
        this.projectInstaller = projectInstaller;
        this.widAssetBuilder = widAssetBuilder;
        this.iconAssetBuilder = iconAssetBuilder;
    }

    @Override
    public void deploy(final Metadata metadata) {
        deploy(metadata,
               System.getProperty(PROPERTY_SERVICE_REPO),
               System.getProperty(PROPERTY_SERVICE_REPO_TASKNAMES));
    }

    void deploy(final Metadata metadata,
                final String url) {
        deploy(metadata,
               url,
               "");
    }

    void deploy(final Metadata metadata,
                final String url,
                final String names) {
        if (null != url && url.trim().length() > 0) {
            final Collection<WorkItemDefinition> items =
                    remoteLookupService
                            .execute(WorkItemDefinitionRemoteRequest.build(url, names));
            final List<Asset> assets = items.stream()
                    .flatMap(this::toAssets)
                    .collect(Collectors.toList());
            if (!assets.isEmpty()) {
                // Deploy into file system.
                backendFileSystemManager.deploy(resources.resolveResourcesPath(metadata),
                                                new Assets(assets),
                                                DEPLOY_MESSAGE + url);
                // Install into current KIE runtime.
                projectInstaller.install(items,
                                         metadata);
            }
        }
    }

    private Stream<Asset> toAssets(final WorkItemDefinition item) {
        return Stream.of(widAssetBuilder.apply(item),
                         iconAssetBuilder.apply(item))
                .filter(Objects::nonNull);
    }

    private static Optional<Asset> createWIDAsset(final WorkItemDefinition item) {
        final String uri = item.getUri();
        try {
            final Asset asset = new AssetBuilder()
                    .setFileName(parseFileName(uri))
                    .stringFromURI(uri)
                    .build();
            return Optional.of(asset);
        } catch (IOException e) {
            LOG.error("Error reading work item definition asset from URL [" + item.getUri() + "]", e);
            return Optional.empty();
        }
    }

    private static Optional<Asset> createIconAsset(final WorkItemDefinition item) {
        final IconDefinition icon = item.getIconDefinition();
        if (null != icon && null != icon.getUri()) {
            final String path = parsePath(item.getUri());
            final String iconUri = icon.getUri();
            final Asset iconAsset;
            try {
                iconAsset = new AssetBuilder()
                        .setFileName(parseFileName(iconUri))
                        .binaryFromURI(path + "/" + iconUri)
                        .build();
                return Optional.of(iconAsset);
            } catch (IOException e) {
                LOG.error("Error reading work item definition asset from URL [" + item.getUri() + "]", e);
            }
        }
        return Optional.empty();
    }

    static String parsePath(final String uri) {
        final int i = uri.lastIndexOf('/');
        return i >= 0 ?
                uri.substring(0, i) :
                uri;
    }

    static String parseFileName(final String uri) {
        final int i = uri.lastIndexOf('/');
        return i >= 0 ?
                uri.substring(i + 1, uri.length()) :
                uri;
    }
}
