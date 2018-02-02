/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.backend;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.navigator.DataContent;
import org.guvnor.structure.navigator.FileNavigatorService;
import org.guvnor.structure.navigator.NavigatorContent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.ocpsoft.prettytime.PrettyTime;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.spaces.Space;

import static java.util.Collections.reverse;
import static java.util.Collections.sort;

@Service
@ApplicationScoped
public class FileNavigatorServiceImpl implements FileNavigatorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private RepositoryService repositoryService;

    private final PrettyTime p = new PrettyTime();

    @Override
    public NavigatorContent listContent(final org.uberfire.backend.vfs.Path _path) {
        final ArrayList<DataContent> result = new ArrayList<>();
        final ArrayList<org.uberfire.backend.vfs.Path> breadcrumbs = new ArrayList<>();

        Path path = Paths.convert(_path);
        final DirectoryStream<Path> stream = ioService.newDirectoryStream(path);

        for (final Path activePath : stream) {
            final VersionAttributeView versionAttributeView = ioService.getFileAttributeView(activePath,
                                                                                             VersionAttributeView.class);
            int index = versionAttributeView.readAttributes().history().records().size() - 1;

            final String authorEmail = versionAttributeView.readAttributes().history().records().get(index).email();
            final String author = versionAttributeView.readAttributes().history().records().get(index).author();
            final String comment = versionAttributeView.readAttributes().history().records().get(index).comment();

            final String time = p.format(new Date(Files.getLastModifiedTime(activePath).toMillis()));
            result.add(new DataContent(Files.isDirectory(activePath),
                                       comment,
                                       author,
                                       authorEmail,
                                       time,
                                       Paths.convert(activePath)));
        }

        sort(result,
             new Comparator<DataContent>() {
                 @Override
                 public int compare(final DataContent dataContent,
                                    final DataContent dataContent2) {

                     int fileCompare = dataContent.getPath().getFileName().toLowerCase().compareTo(dataContent2.getPath().getFileName().toLowerCase());
                     if (dataContent.isDirectory() && dataContent2.isDirectory()) {
                         return fileCompare;
                     }

                     if (dataContent.isDirectory()) {
                         return -1;
                     }

                     if (dataContent2.isDirectory()) {
                         return 1;
                     }

                     return fileCompare;
                 }
             });

        if (!path.equals(path.getRoot())) {
            while (!path.getParent().equals(path.getRoot())) {
                path = path.getParent();
                breadcrumbs.add(Paths.convert(path));
            }

            reverse(breadcrumbs);
        }
        final org.uberfire.backend.vfs.Path root = Paths.convert(path.getRoot());

        return new NavigatorContent(repositoryService.getRepository(root).getAlias(),
                                    root,
                                    breadcrumbs,
                                    result);
    }

    @Override
    public List<Repository> listRepositories(final Space space) {
        return new ArrayList<>(repositoryService.getRepositories(space));
    }
}
