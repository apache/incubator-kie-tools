/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.organizationalunit.config;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;

import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.organizationalunit.config.RolePermissions;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.uberfire.backend.server.io.object.ObjectStorage;
import org.uberfire.spaces.SpacesAPI;
import org.uberfire.util.URIUtil;

public class SpaceConfigStorageImpl implements SpaceConfigStorage {

    public static final String FILE_FORMAT = ".json";

    public static final String BRANCH_PERMISSIONS = "BranchPermissions";

    private ObjectStorage objectStorage;

    public SpaceConfigStorageImpl() {
    }

    @Inject
    public SpaceConfigStorageImpl(final ObjectStorage objectStorage) {
        this.objectStorage = objectStorage;
    }

    public void setup(final String spaceName) {
        objectStorage.init(URI.create(SpacesAPI.resolveConfigFileSystemPath(SpacesAPI.Scheme.DEFAULT, spaceName)));
    }

    @Override
    public BranchPermissions loadBranchPermissions(final String branchName,
                                                   final String projectIdentifier) {
        final BranchPermissions branchPermissions = objectStorage.read(buildBranchConfigFilePath(branchName,
                                                                                                 projectIdentifier,
                                                                                                 BRANCH_PERMISSIONS));

        if (branchPermissions == null) {
            return getDefaultBranchPermissions(branchName);
        }

        return branchPermissions;
    }

    @Override
    public void saveBranchPermissions(final String branchName,
                                      final String projectIdentifier,
                                      final BranchPermissions branchPermissions) {
        objectStorage.write(buildBranchConfigFilePath(branchName,
                                                      projectIdentifier,
                                                      BRANCH_PERMISSIONS),
                            branchPermissions);
    }

    @Override
    public void deleteBranchPermissions(final String branchName,
                                        final String projectIdentifier) {
        objectStorage.delete(buildBranchConfigFilePath(branchName,
                                                       projectIdentifier,
                                                       BRANCH_PERMISSIONS));
    }

    BranchPermissions getDefaultBranchPermissions(String branchName) {
        final Map<String, RolePermissions> defaultPermissions = new LinkedHashMap<>();
        defaultPermissions.put(ContributorType.OWNER.name(), new RolePermissions(ContributorType.OWNER.name(), true, true, true, true));
        defaultPermissions.put(ContributorType.ADMIN.name(), new RolePermissions(ContributorType.ADMIN.name(), true, true, false, true));
        defaultPermissions.put(ContributorType.CONTRIBUTOR.name(), new RolePermissions(ContributorType.CONTRIBUTOR.name(), true, true, false, false));

        return new BranchPermissions(branchName, defaultPermissions);
    }

    String buildSpaceConfigFilePath(final String configName) {
        return "/config/" + configName + FILE_FORMAT;
    }

    String buildProjectConfigFilePath(final String projectIdentifier,
                                      final String configName) {
        return "/config/" + encode(projectIdentifier) + "/" + configName + FILE_FORMAT;
    }

    String buildBranchConfigFilePath(final String branchName,
                                     final String projectIdentifier,
                                     final String configName) {
        return "/config/" + encode(projectIdentifier) + "/" + encode(branchName) + "/" + configName + FILE_FORMAT;
    }

    private String encode(final String text) {
        return URIUtil.encodeQueryString(text);
    }
}