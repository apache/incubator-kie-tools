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

package org.kie.workbench.common.stunner.core.backend.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.api.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;

@Startup(value = StartupType.EAGER)
@ApplicationScoped
public class BackendFileSystemManager {

    private static final Logger LOG = LoggerFactory.getLogger(BackendFileSystemManager.class.getName());

    private static final FilenameFilter FILTER_NONE = (dir, name) -> true;
    private static final String WEBINF_PATH = "WEB-INF";

    private final IOService ioService;

    // CDI proxy.
    protected BackendFileSystemManager() {
        this(null);
    }

    @Inject
    public BackendFileSystemManager(final @Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

    public String getPathRelativeToApp(final String path) {
        final String wbp = null != path ? (path.trim().length() == 0 || ".".equals(path) ?
                WEBINF_PATH :
                WEBINF_PATH + "/" + path) :
                WEBINF_PATH;
        return RpcContext.getServletRequest()
                .getServletContext()
                .getRealPath(wbp)
                .replaceAll("\\\\",
                            "/");
    }

    public void findAndDeployFiles(final File directory,
                                   final org.uberfire.java.nio.file.Path targetPath) {
        findAndDeployFiles(directory,
                           FILTER_NONE,
                           targetPath);
    }

    public void findAndDeployFiles(final File directory,
                                   final FilenameFilter filter,
                                   final org.uberfire.java.nio.file.Path targetPath) {
        // Look for data sets deploy
        final File[] files = directory.listFiles(filter);
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    registerIntoFileSystem(f,
                                           f.getName(),
                                           targetPath);
                } else {
                    findAndDeployFiles(f,
                                       filter,
                                       targetPath.resolve(f.getName()));
                }
            }
        }
    }

    private void registerIntoFileSystem(final File file,
                                        final String name,
                                        final org.uberfire.java.nio.file.Path targetPath) {
        final org.uberfire.java.nio.file.Path targetFilePath = targetPath.resolve(name);
        try {
            ioService.copy(new FileInputStream(file),
                           targetFilePath);
        } catch (Exception e) {
            LOG.error("Error writing file [" + name + "] into " +
                              "path [" + targetFilePath + "]",
                      e);
        }
    }

    public IOService getIoService() {
        return ioService;
    }
}
