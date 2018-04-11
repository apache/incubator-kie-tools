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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.jboss.errai.bus.server.api.RpcContext;
import org.kie.workbench.common.stunner.core.backend.util.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;

@ApplicationScoped
public class BackendFileSystemManager {

    private static final Logger LOG = LoggerFactory.getLogger(BackendFileSystemManager.class.getName());

    public static final Charset UT8 = StandardCharsets.UTF_8;
    public static final String UT8_ENC = StandardCharsets.UTF_8.name();
    private static final FilenameFilter FILTER_NONE = (dir, name) -> true;
    private static final String WEBINF_PATH = "WEB-INF";

    private final IOService ioService;
    private final CommentedOptionFactory optionFactory;

    // CDI proxy.
    protected BackendFileSystemManager() {
        this(null, null);
    }

    @Inject
    public BackendFileSystemManager(final @Named("ioStrategy") IOService ioService,
                                    final CommentedOptionFactory optionFactory) {
        this.ioService = ioService;
        this.optionFactory = optionFactory;
    }

    public static class AssetBuilder {

        private String fileName;
        private InputStream stream;

        public AssetBuilder setFileName(final String fileName) {
            this.fileName = fileName;
            return this;
        }

        public AssetBuilder fromClasspathResouce(final String resource) {
            this.stream = getClass().getClassLoader().getResourceAsStream(resource);
            return this;
        }

        public AssetBuilder fromString(final String content) throws UnsupportedEncodingException {
            this.stream = toBytes(content);
            return this;
        }

        public AssetBuilder stringFromURI(final String uri) throws IOException {
            this.stream = toBytes(URLUtils.readFromURL(uri));
            return this;
        }

        private static ByteArrayInputStream toBytes(final String s) throws UnsupportedEncodingException {
            return new ByteArrayInputStream(s.getBytes(UT8_ENC));
        }

        public AssetBuilder binaryFromURI(final String uri) throws IOException {
            this.stream = new ByteArrayInputStream(URLUtils.readBytesFromURL(uri));
            return this;
        }

        public Asset build() {
            return new Asset(fileName,
                             stream);
        }
    }

    public static class Assets {

        private final Collection<Asset> assets;

        public Assets(final Collection<Asset> assets) {
            this.assets = assets;
        }

        public Assets add(final Asset asset) {
            assets.add(asset);
            return this;
        }

        public Collection<Asset> getAssets() {
            return assets;
        }
    }

    public static class Asset {

        private final String fileName;
        private final InputStream stream;

        private Asset(final String fileName,
                      final InputStream stream) {
            this.fileName = fileName;
            this.stream = stream;
        }
    }

    // NEXT: Improve this by do not handling all file payload in memory
    public void deploy(final org.uberfire.java.nio.file.Path global,
                       final Assets assets,
                       final String message) {
        if (!ioService.exists(global)) {
            ioService.createDirectories(global);
        }
        try {
            ioService.startBatch(global.getFileSystem());
            for (final Asset asset : assets.assets) {
                final org.uberfire.java.nio.file.Path assetPath = global.resolve(asset.fileName);
                final InputStream resource = asset.stream;
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                final byte[] chunk = new byte[4096];
                int bytesRead;
                while ((bytesRead = resource.read(chunk)) > 0) {
                    os.write(chunk, 0, bytesRead);
                }
                os.flush();
                resource.close();
                ioService.write(assetPath,
                                os.toByteArray(),
                                optionFactory.makeCommentedOption(message));
            }
        } catch (final Exception e) {
            LOG.error("Error while deploying assets.", e);
        } finally {
            ioService.endBatch();
        }
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
