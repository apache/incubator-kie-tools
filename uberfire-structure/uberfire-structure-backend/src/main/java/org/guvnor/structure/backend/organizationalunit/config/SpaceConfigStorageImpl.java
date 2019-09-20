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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.organizationalunit.config.RolePermissions;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.io.object.ObjectStorage;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.spaces.SpacesAPI;
import org.uberfire.util.URIUtil;

public class SpaceConfigStorageImpl implements SpaceConfigStorage {

    public static final String FILE_FORMAT = ".json";

    public static final String BRANCH_PERMISSIONS = "BranchPermissions";
    public static final String SPACE_INFO = "SpaceInfo";

    private static final String CHANGE_REQUESTS_FOLDER = "change_requests";
    private static final String CHANGE_REQUESTS_FILE = "information.cr";
    private static final String CHANGE_REQUEST_COMMENTS_FOLDER = "comments";
    private static final String CHANGE_REQUEST_COMMENT_FILE_EXT = "comment";


    private static final Logger logger = LoggerFactory.getLogger(SpaceConfigStorageImpl.class);

    private ObjectStorage objectStorage;
    private IOService ioService;
    private String spaceName;

    public SpaceConfigStorageImpl() {
    }

    @Inject
    public SpaceConfigStorageImpl(final ObjectStorage objectStorage,
                                  final @Named("configIO") IOService ioService) {
        this.objectStorage = objectStorage;
        this.ioService = ioService;
    }

    public void setup(final String spaceName) {
        this.spaceName = spaceName;
        objectStorage.init(getRootURI(spaceName));
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

    private URI getRootURI(String spaceName) {
        return URI.create(SpacesAPI.resolveConfigFileSystemPath(SpacesAPI.Scheme.DEFAULT,
                                                                spaceName));
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

    @Override
    public void deleteRepository(final String repositoryAlias) {
        deleteAllChangeRequests(repositoryAlias);
        objectStorage.delete(buildRepositoryFolderPath(repositoryAlias));
    }

    @Override
    public List<ChangeRequest> loadChangeRequests(final String repositoryAlias) {
        return getChangeRequestIds(repositoryAlias).stream()
                .map(changeRequestId -> loadChangeRequest(repositoryAlias, changeRequestId))
                .collect(Collectors.toList());
    }

    @Override
    public ChangeRequest loadChangeRequest(final String repositoryAlias,
                                           final Long changeRequestId) {
        return objectStorage.read(buildChangeRequestFilePath(repositoryAlias,
                                                             changeRequestId));
    }

    @Override
    public void saveChangeRequest(final String repositoryAlias,
                                  final ChangeRequest changeRequest) {
        objectStorage.write(buildChangeRequestFilePath(repositoryAlias,
                                                       changeRequest.getId()),
                            changeRequest);
    }

    @Override
    public void deleteAllChangeRequests(final String repositoryAlias) {
        getChangeRequestIds(repositoryAlias).forEach(changeRequestId -> deleteChangeRequest(repositoryAlias,
                                                                                            changeRequestId));
    }

    @Override
    public void deleteChangeRequest(final String repositoryAlias,
                                    final Long changeRequestId) {
        deleteAllChangeRequestComments(repositoryAlias,
                                       changeRequestId);
        objectStorage.delete(buildChangeRequestFilePath(repositoryAlias,
                                                        changeRequestId));
    }

    @Override
    public List<Long> getChangeRequestIds(String repositoryAlias) {
        List<Long> changeRequestIds = new ArrayList<>();

        final String changeRequestsFolderPath = buildChangeRequestsFolderPath(repositoryAlias);

        if (objectStorage.exists(changeRequestsFolderPath)) {
            final Path changeRequestsFolder = objectStorage.getPath(changeRequestsFolderPath);

            try (DirectoryStream<Path> directoryStream =
                         ioService.newDirectoryStream(changeRequestsFolder,
                                                      Files::isDirectory)) {
                directoryStream.forEach(crDir -> {
                    try {
                        Long id = Long.valueOf(crDir.getFileName().toString());
                        changeRequestIds.add(id);
                    } catch (NumberFormatException e) {
                        logger.error("Cannot convert folder name to long: ", e);
                    } catch (Exception e) {
                        logger.error("An unexpected exception was thrown: ", e);
                    }
                });
            }
        }

        return changeRequestIds;
    }

    @Override
    public List<ChangeRequestComment> loadChangeRequestComments(final String repositoryAlias,
                                                                final Long changeRequestId) {
        return getChangeRequestCommentIds(repositoryAlias,
                                          changeRequestId)
                .stream()
                .map(changeRequestCommentId -> loadChangeRequestComment(repositoryAlias,
                                                                        changeRequestId,
                                                                        changeRequestCommentId))
                .collect(Collectors.toList());
    }

    @Override
    public ChangeRequestComment loadChangeRequestComment(final String repositoryAlias,
                                                         final Long changeRequestId,
                                                         final Long changeRequestCommentId) {
        return objectStorage.read(buildChangeRequestCommentFilePath(repositoryAlias,
                                                                    changeRequestId,
                                                                    changeRequestCommentId));
    }

    @Override
    public void saveChangeRequestComment(final String repositoryAlias,
                                         final Long changeRequestId,
                                         final ChangeRequestComment changeRequestComment) {
        objectStorage.write(buildChangeRequestCommentFilePath(repositoryAlias,
                                                              changeRequestId,
                                                              changeRequestComment.getId()),
                            changeRequestComment);
    }

    @Override
    public void deleteAllChangeRequestComments(final String repositoryAlias,
                                               final Long changeRequestId) {
        getChangeRequestCommentIds(repositoryAlias,
                                   changeRequestId)
                .forEach(changeRequestCommentId -> deleteChangeRequestComment(repositoryAlias,
                                                                              changeRequestId,
                                                                              changeRequestCommentId));
    }

    @Override
    public void deleteChangeRequestComment(final String repositoryAlias,
                                           final Long changeRequestId,
                                           final Long changeRequestCommentId) {
        objectStorage.delete(buildChangeRequestCommentFilePath(repositoryAlias,
                                                               changeRequestId,
                                                               changeRequestCommentId));
    }

    @Override
    public List<Long> getChangeRequestCommentIds(final String repositoryAlias,
                                                 final Long changeRequestId) {
        List<Long> changeRequestCommentIds = new ArrayList<>();

        final String changeRequestCommentsPathStr = buildChangeRequestCommentFolderPath(repositoryAlias,
                                                                                        changeRequestId);

        if (objectStorage.exists(changeRequestCommentsPathStr)) {
            final Path changeRequestCommentsFolder = objectStorage.getPath(changeRequestCommentsPathStr);

            try (DirectoryStream<Path> directoryStream =
                         ioService.newDirectoryStream(changeRequestCommentsFolder,
                                                      Files::isRegularFile)) {
                directoryStream.forEach(commentFile -> {
                    try {
                        Long id = Long.valueOf(FilenameUtils.getBaseName(commentFile.getFileName().toString()));
                        changeRequestCommentIds.add(id);
                    } catch (NumberFormatException e) {
                        logger.error("Cannot convert folder name to long: ", e);
                    } catch (Exception e) {
                        logger.error("An unexpected exception was thrown: ", e);
                    }
                });
            }
        }

        return changeRequestCommentIds;
    }

    BranchPermissions getDefaultBranchPermissions(String branchName) {
        final Map<String, RolePermissions> defaultPermissions = new LinkedHashMap<>();
        defaultPermissions.put(ContributorType.OWNER.name(),
                               new RolePermissions(ContributorType.OWNER.name(),
                                                   true,
                                                   true,
                                                   true,
                                                   true));
        defaultPermissions.put(ContributorType.ADMIN.name(),
                               new RolePermissions(ContributorType.ADMIN.name(),
                                                   true,
                                                   true,
                                                   false,
                                                   true));
        defaultPermissions.put(ContributorType.CONTRIBUTOR.name(),
                               new RolePermissions(ContributorType.CONTRIBUTOR.name(),
                                                   true,
                                                   true,
                                                   false,
                                                   false));

        return new BranchPermissions(branchName,
                                     defaultPermissions);
    }

    public SpaceInfo loadSpaceInfo() {
        return objectStorage.read(buildSpaceConfigFilePath(SPACE_INFO));
    }

    @Override
    public void saveSpaceInfo(final SpaceInfo spaceInfo) {
        objectStorage.write(buildSpaceConfigFilePath(SPACE_INFO),
                            spaceInfo,
                            false);
    }

    @Override
    public void startBatch() {
        ioService.startBatch(ioService.get(this.getRootURI(spaceName)).getFileSystem());
    }

    @Override
    public void endBatch() {
        ioService.endBatch();
    }

    @Override
    public void close() {
        this.objectStorage.close();
    }

    @Override
    public boolean isInitialized() {
        return this.loadSpaceInfo() != null;
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

    private String buildRepositoryFolderPath(final String repositoryAlias) {
        return String.format("/%s", encode(repositoryAlias));
    }

    private String buildChangeRequestsFolderPath(final String repositoryAlias) {
        return String.format("%s/%s",
                             buildRepositoryFolderPath(repositoryAlias),
                             CHANGE_REQUESTS_FOLDER);
    }

    private String buildChangeRequestFilePath(final String repositoryAlias,
                                              final Long changeRequestId) {
        return String.format("%s/%s/%s",
                             buildChangeRequestsFolderPath(repositoryAlias),
                             changeRequestId,
                             CHANGE_REQUESTS_FILE);
    }

    private String buildChangeRequestCommentFolderPath(final String repositoryAlias,
                                                       final Long changeRequestId) {
        return String.format("%s/%s/%s",
                             buildChangeRequestsFolderPath(repositoryAlias),
                             changeRequestId,
                             CHANGE_REQUEST_COMMENTS_FOLDER);
    }

    private String buildChangeRequestCommentFilePath(final String repositoryAlias,
                                                     final Long changeRequestId,
                                                     final Long changeRequestCommentId) {
        return String.format("%s/%s.%s",
                             buildChangeRequestCommentFolderPath(repositoryAlias,
                                                                 changeRequestId),
                             changeRequestCommentId,
                             CHANGE_REQUEST_COMMENT_FILE_EXT);
    }

    private String encode(final String text) {
        return URIUtil.encodeQueryString(text);
    }

    public Path getPath() {
        final URI configPathURI = getConfigPathUri();
        return ioService.get(configPathURI);
    }

    private URI getConfigPathUri() {
        return URI.create(SpacesAPI.resolveConfigFileSystemPath(SpacesAPI.Scheme.DEFAULT,
                                                                this.spaceName));
    }
}
