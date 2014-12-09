/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.backend.service;

import java.util.Date;
import javax.inject.Inject;

import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.shared.source.SourceGenerationFailedException;
import org.kie.workbench.common.services.backend.source.SourceServices;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;

public abstract class KieService {

    private static final Logger logger = LoggerFactory.getLogger(KieService.class);

    @Inject
    protected MetadataServerSideService metadataService;

    @Inject
    protected SourceServices sourceServices;

    @Inject
    protected KieProjectService projectService;

    @Inject
    protected User identity;

    @Inject
    protected SessionInfo sessionInfo;

    public Overview loadOverview(final Path path) {
        final Overview overview = new Overview();

        try {
            // Some older versions in our example do not have metadata. This should be impossible in any kie-wb version
            overview.setMetadata(metadataService.getMetadata(path));
        } catch (Exception e) {
            logger.warn("No metadata found for file: " + path.getFileName() + ", full path [" + path.toString() + "]");
        }

        //Some resources are not within a Project (e.g. categories.xml) so don't assume we can set the project name
        final KieProject project = projectService.resolveProject(path);
        if (project == null) {
            logger.info("File: " + path.getFileName() + ", full path [" + path.toString() + "] was not within a Project. Project Name cannot be set.");
        } else {
            overview.setProjectName(project.getProjectName());
        }

        return overview;
    }

    public String getSource(final Path path)
            throws SourceGenerationFailedException {
        final org.uberfire.java.nio.file.Path convertedPath = Paths.convert(path);

        if (sourceServices.hasServiceFor(convertedPath)) {
            return sourceServices.getServiceFor(convertedPath).getSource(convertedPath);
        } else {
            return "";
        }
    }

    protected CommentedOption makeCommentedOption(final String commitMessage) {
        final String name = identity.getIdentifier();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( sessionInfo.getId(),
                                                        name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }

}
