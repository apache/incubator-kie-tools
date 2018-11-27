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

package org.guvnor.structure.backend.repositories.git.hooks.impl;

import org.apache.commons.io.FilenameUtils;
import org.jboss.errai.bus.server.api.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;

@Dependent
public class MessageReader {

    private static final Logger LOG = LoggerFactory.getLogger(MessageReader.class);

    private Path bundlePath;

    private String bundleName;

    private Supplier<Locale> localeSupplier;

    public MessageReader() {
        this(() -> RpcContext.getServletRequest().getLocale());
    }

    MessageReader(Supplier<Locale> localeSupplier) {
        this.localeSupplier = localeSupplier;
    }

    public void init(String bundleParam) {

        if (bundleParam != null) {

            Path fullPath = Paths.get(bundleParam);

            File bundleFile = fullPath.toFile();

            if (bundleFile.exists()) {
                bundlePath = bundleFile.toPath().getParent();

                bundleName = FilenameUtils.getBaseName(bundleFile.getName());
            } else {
                LOG.error("Invalid bundle '" + bundleParam + "': file doesn't exist");
            }
        }
    }

    public Optional<String> resolveMessage(int exitCode) {
        String result = null;

        if (bundlePath != null && bundleName != null) {
            try {

                // Setting up the bundle classloader based on the path specified on the param
                URL[] urls = new URL[]{bundlePath.toUri().toURL()};

                ClassLoader bundleClassLoader = new URLClassLoader(urls);

                // Getting the bundle from the current generated classloader
                ResourceBundle bundle = ResourceBundle.getBundle(bundleName, localeSupplier.get(), bundleClassLoader);

                result = bundle.getString(String.valueOf(exitCode));
            } catch (MissingResourceException e) {
                LOG.info("Cannot find key for code '" + exitCode + "' bundle '" + bundlePath.resolve(bundleName).toString() + "'");
            } catch (MalformedURLException e) {
                LOG.warn("Cannot load bundle '" + bundlePath.resolve(bundleName).toString() + "': ", e);
            }
        }

        return Optional.ofNullable(result);
    }

}
