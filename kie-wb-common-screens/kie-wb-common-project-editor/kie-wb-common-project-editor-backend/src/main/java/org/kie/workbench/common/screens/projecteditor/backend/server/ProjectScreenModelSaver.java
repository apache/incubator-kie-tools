/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.backend.server;

import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

public class ProjectScreenModelSaver {

    private POMService pomService;
    private KModuleService kModuleService;
    private ProjectImportsService importsService;
    private ProjectRepositoriesService repositoriesService;
    private PackageNameWhiteListService whiteListService;
    private CommentedOptionFactory commentedOptionFactory;
    private IOService ioService;

    public ProjectScreenModelSaver() {
    }

    @Inject
    public ProjectScreenModelSaver( final POMService pomService,
                                    final KModuleService kModuleService,
                                    final ProjectImportsService importsService,
                                    final ProjectRepositoriesService repositoriesService,
                                    final PackageNameWhiteListService whiteListService,
                                    final CommentedOptionFactory commentedOptionFactory,
                                    final @Named("ioStrategy") IOService ioService ) {
        this.pomService = pomService;
        this.kModuleService = kModuleService;
        this.importsService = importsService;
        this.repositoriesService = repositoriesService;
        this.whiteListService = whiteListService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.ioService = ioService;
    }

    public void save( final Path pathToPomXML,
                      final ProjectScreenModel model,
                      final String comment ) {
        try {
            ioService.startBatch( Paths.convert( pathToPomXML ).getFileSystem(),
                                  commentedOptionFactory.makeCommentedOption( comment ) );
            pomService.save( pathToPomXML,
                             model.getPOM(),
                             model.getPOMMetaData(),
                             comment );
            kModuleService.save( model.getPathToKModule(),
                                 model.getKModule(),
                                 model.getKModuleMetaData(),
                                 comment );
            importsService.save( model.getPathToImports(),
                                 model.getProjectImports(),
                                 model.getProjectImportsMetaData(),
                                 comment );
            repositoriesService.save( model.getPathToRepositories(),
                                      model.getRepositories(),
                                      comment );
            whiteListService.save( model.getPathToWhiteList(),
                                   model.getWhiteList(),
                                   model.getWhiteListMetaData(),
                                   comment );

        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        } finally {
            ioService.endBatch();
        }
    }
}
