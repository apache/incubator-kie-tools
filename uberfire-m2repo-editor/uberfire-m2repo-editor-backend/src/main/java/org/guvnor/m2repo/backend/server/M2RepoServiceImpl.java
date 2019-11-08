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

package org.guvnor.m2repo.backend.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.appformer.maven.support.MinimalPomParser;
import org.appformer.maven.support.PomModel;
import org.eclipse.aether.artifact.Artifact;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.guvnor.m2repo.model.JarListPageRequest;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.paging.PageResponse;

@Service
@ApplicationScoped
// Implementation needs to implement both interfaces even though one extends the other
// otherwise the implementation discovery mechanism for the @Service annotation fails.
public class M2RepoServiceImpl implements M2RepoService,
                                          ExtendedM2RepoService {

    private Logger logger;

    private GuvnorM2Repository repository;

    public M2RepoServiceImpl() {}

    @Inject
    public M2RepoServiceImpl(GuvnorM2Repository repository) {
        this(LoggerFactory.getLogger(M2RepoServiceImpl.class), repository);
    }

    public M2RepoServiceImpl(final Logger logger,
                             GuvnorM2Repository repository) {
        this.logger = logger;
        this.repository = repository;

        if (!isURLValid()) {
            logger.error(String.format("The property %s is not correctly set. The workbench will use a direct file path to the m2 repository and this should only be used when test the workbench.", ArtifactRepositoryService.GLOBAL_M2_REPO_URL));
        }
    }

    @Override
    public void deployJar(final InputStream is,
                          final GAV gav) {
        repository.deployArtifact(is,
                                  gav,
                                  true);
    }

    @Override
    public void deployJarInternal(final InputStream is,
                                  final GAV gav) {
        repository.deployArtifact(is,
                                  gav,
                                  false);
    }

    @Override
    public void deployPom(final InputStream is,
                          final GAV gav) {
        repository.deployPom(is,
                             gav);
    }

    @Override
    public String getPomText(final String path) {
        checkPathTraversal(path);

        return repository.getPomText(path);
    }

    @Override
    public GAV loadGAVFromJar(final String path) {
        checkPathTraversal(path);

        final GAV gav = repository.loadGAVFromJar(path);
        return gav;
    }

    @Override
    public PageResponse<JarListPageRow> listArtifacts(final JarListPageRequest pageRequest) {
        //Get unsorted files matching filter
        final String filters = pageRequest.getFilters();
        final List<String> fileFormats = pageRequest.getFileFormats();
        final String dataSourceName = pageRequest.getDataSourceName();
        final boolean isAscending = pageRequest.isAscending();
        final Collection<Artifact> files = repository.listArtifacts(filters,
                                                                    fileFormats);

        //Convert files to JarListPageRow
        final List<JarListPageRow> jarPageRowList = new ArrayList<JarListPageRow>();
        for (Artifact artifact : files) {
            final File file = artifact.getFile();
            JarListPageRow jarListPageRow = new JarListPageRow();
            jarListPageRow.setName(file.getName());
            jarListPageRow.setPath(getJarPath(file.getPath(),
                                              File.separator));
            jarListPageRow.setGav(getGAV(jarListPageRow.getPath()));
            jarListPageRow.setLastModified(new Date(file.lastModified()));
            jarListPageRow.setRepositoryName(artifact.getProperty("repository",
                                                                  "undefined"));
            jarPageRowList.add(jarListPageRow);
        }

        //Sort JarListPageRow entries, if required
        if (dataSourceName != null) {
            final int order = (isAscending ? 1 : -1);
            if (dataSourceName.equals(JarListPageRequest.COLUMN_NAME)) {
                Collections.sort(jarPageRowList,
                                 new Comparator<JarListPageRow>() {
                                     @Override
                                     public int compare(final JarListPageRow o1,
                                                        final JarListPageRow o2) {
                                         return o1.getName().compareTo(o2.getName()) * order;
                                     }
                                 });
            } else if (dataSourceName.equals(JarListPageRequest.COLUMN_PATH)) {
                Collections.sort(jarPageRowList,
                                 new Comparator<JarListPageRow>() {
                                     @Override
                                     public int compare(final JarListPageRow o1,
                                                        final JarListPageRow o2) {
                                         return o1.getPath().compareTo(o2.getPath()) * order;
                                     }
                                 });
            } else if (dataSourceName.equals(JarListPageRequest.COLUMN_GAV)) {
                Collections.sort(jarPageRowList,
                                 new Comparator<JarListPageRow>() {
                                     @Override
                                     public int compare(final JarListPageRow o1,
                                                        final JarListPageRow o2) {
                                         final GAV gav1 = o1.getGav();
                                         final GAV gav2 = o2.getGav();
                                         return gav1.toString().compareToIgnoreCase(gav2.toString()) * order;
                                     }
                                 });
            } else if (dataSourceName.equals(JarListPageRequest.COLUMN_LAST_MODIFIED)) {
                Collections.sort(jarPageRowList,
                                 new Comparator<JarListPageRow>() {
                                     @Override
                                     public int compare(final JarListPageRow o1,
                                                        final JarListPageRow o2) {
                                         final Long ft1 = o1.getLastModified().getTime();
                                         final Long ft2 = o2.getLastModified().getTime();
                                         return ft1.compareTo(ft2) * order;
                                     }
                                 });
            }
        }

        //Copy request "page" of entries to response
        final Integer pageSize = pageRequest.getPageSize();
        final int startRowIndex = pageRequest.getStartRowIndex();
        final int endRowIndex = Math.min(jarPageRowList.size(),
                                         (pageSize == null ? jarPageRowList.size() : startRowIndex + pageSize));
        final List<JarListPageRow> responsePageRowList = new ArrayList<JarListPageRow>();
        if (startRowIndex < jarPageRowList.size()) {
            int i = startRowIndex;
            while (i < endRowIndex && i < jarPageRowList.size()) {
                responsePageRowList.add(jarPageRowList.get(i));
                i++;
            }
        }

        final PageResponse<JarListPageRow> response = new PageResponse<JarListPageRow>();
        response.setPageRowList(responsePageRowList);
        response.setStartRowIndex(pageRequest.getStartRowIndex());
        response.setTotalRowSize(files.size());
        response.setTotalRowSizeExact(true);

        return response;
    }

    // The file separator is provided as a parameter so that we can test for correct JAR path creation on both
    // Windows and Linux based Operating Systems in Unit tests running on either platform. See JarPathTest.
    String getJarPath(final String path,
                      final String separator) {
        //Strip "Repository" prefix
        String pathToDir = repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME);
        String jarPath = path.substring(pathToDir.length() + 1);
        //Replace OS-dependent file separators with HTTP path separators
        jarPath = jarPath.replaceAll("\\" + separator,
                                     "/");
        return jarPath;
    }

    GAV getGAV(final String path) {
        GAV gav = null;
        InputStream is = null;
        try {
            final String pom = getPomText(path);
            is = new ByteArrayInputStream(pom.getBytes(Charset.forName("UTF-8")));
            final PomModel model = MinimalPomParser.parse(path,
                                                          is);
            gav = new GAV(model.getReleaseId().getGroupId(),
                          model.getReleaseId().getArtifactId(),
                          model.getReleaseId().getVersion());
        } catch (RuntimeException rte) {
            //RuntimeException is thrown by MinimalPomParser for any Exception..
            gav = new GAV("<undetermined>",
                          "<undetermined>",
                          "<undetermined>");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    //Swallow
                }
            }
        }
        return gav;
    }

    /**
     * URL point to local file system if URL property is not available.
     * @return String
     */
    @Override
    public String getRepositoryURL() {
        if (isURLValid()) {
            return System.getProperty(ArtifactRepositoryService.GLOBAL_M2_REPO_URL);
        } else {
            return repository.getRepositoryURL(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME);
        }
    }

    @Override
    public String getKModuleText(String path) {
        checkPathTraversal(path);
        return repository.getKModuleText(path);
    }

    @Override
    public String getKieDeploymentDescriptorText(String path) {
        checkPathTraversal(path);
        return repository.getKieDeploymentDescriptorText(path);
    }

    private boolean isURLValid() {
        final String urlProperty = System.getProperty(ArtifactRepositoryService.GLOBAL_M2_REPO_URL);
        try {
            new URL(urlProperty);
        } catch (MalformedURLException e) {
            logger.warn(String.format("The url %s is not valid. Using the default.", urlProperty));
            return false;
        }
        return true;
    }

    /**
     * Asserts that the path does not cause traversal.
     * @param path the path to check, must not be null.
     */
    private void checkPathTraversal(String path) {
        // There's no more decoding / unescaping happening on the String beyond 
        // this point so we don't have to check for %u002e, %252e, etc., as these 
        // paths would result in a FileNotFoundException anyway.
        if (path.contains("..")) {
            throw new RuntimeException("Invalid path provided!");
        }
    }
}
