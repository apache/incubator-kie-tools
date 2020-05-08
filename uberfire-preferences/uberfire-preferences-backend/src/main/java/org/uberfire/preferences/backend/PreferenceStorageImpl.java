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

package org.uberfire.preferences.backend;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.Files.walkFileTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.Customizable;
import org.uberfire.backend.server.io.object.ObjectStorage;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.PreferenceStorage;
import org.uberfire.preferences.shared.event.PreferenceUpdatedEvent;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.preferences.shared.impl.PreferenceScopedValue;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

@ApplicationScoped
public class PreferenceStorageImpl implements PreferenceStorage {

    public static final String FILE_FORMAT = ".preferences";
    public static final int FILE_FORMAT_SIZE = FILE_FORMAT.length();
    private static final Logger logger = LoggerFactory.getLogger(PreferenceStorageImpl.class);

    private SpacesAPI spaces;

    private IOService ioService;

    private SessionInfo sessionInfo;

    private PreferenceScopeTypes scopeTypes;

    private PreferenceScopeFactory scopeFactory;

    private ObjectStorage objectStorage;
    
    private Event<PreferenceUpdatedEvent> preferenceUpdatedEvent;

    protected PreferenceStorageImpl() {
    }

    @Inject
    public PreferenceStorageImpl(@Named("ioStrategy") final IOService ioService,
                                 final SessionInfo sessionInfo,
                                 @Customizable final PreferenceScopeTypes scopeTypes,
                                 final PreferenceScopeFactory scopeFactory,
                                 final ObjectStorage objectStorage,
                                 final SpacesAPI spaces,
                                 final Event<PreferenceUpdatedEvent> preferenceUpdatedEvent) {
        this.ioService = ioService;
        this.sessionInfo = sessionInfo;
        this.scopeTypes = scopeTypes;
        this.scopeFactory = scopeFactory;
        this.objectStorage = objectStorage;
        this.spaces = spaces;
        this.preferenceUpdatedEvent = preferenceUpdatedEvent;
    }

    @PostConstruct
    public void init() {
        objectStorage.init(spaces.resolveFileSystemURI(SpacesAPI.Scheme.DEFAULT,
                                                       SpacesAPI.DEFAULT_SPACE,
                                                       "preferences"));
    }

    @Override
    public boolean exists(final PreferenceScope preferenceScope,
                          final String key) {
        return objectStorage.exists(buildScopedPreferencePath(preferenceScope,
                                                              key));
    }

    @Override
    public boolean exists(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                          final String key) {
        for (PreferenceScope preferenceScope : scopeResolutionStrategyInfo.order()) {
            boolean exists = exists(preferenceScope,
                                    key);
            if (exists) {
                return true;
            }
        }

        return false;
    }

    @Override
    public <T> T read(final PreferenceScope preferenceScope,
                      final String key) {
        final String path = buildScopedPreferencePath(preferenceScope,
                                                      key);
        return objectStorage.read(path);
    }

    @Override
    public <T> T read(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                      final String key) {
        for (PreferenceScope preferenceScope : scopeResolutionStrategyInfo.order()) {
            T result = read(preferenceScope,
                            key);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    @Override
    public <T> PreferenceScopedValue<T> readWithScope(final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo,
                                                      final String key) {
        for (PreferenceScope scope : scopeResolutionStrategyInfo.order()) {
            T result = read(scope,
                            key);
            if (result != null) {
                return new PreferenceScopedValue<>(result,
                                                   scopeFactory.cloneScope(scope));
            }
        }

        return null;
    }

    @Override
    public void write(final PreferenceScope preferenceScope,
                      final String key,
                      final Object value) {
        objectStorage.write(buildScopedPreferencePath(preferenceScope,
                                                      key),
                                                      value);
        preferenceUpdatedEvent.fire(new PreferenceUpdatedEvent(key, 
                                                               value));
    }

    @Override
    public void delete(final PreferenceScope preferenceScope,
                       final String key) {
        objectStorage.delete(buildScopedPreferencePath(preferenceScope,
                                                       key));
    }

    @Override
    public Collection<String> allKeys(final PreferenceScope scope) {
        Collection<String> keys = new ArrayList<>();
        Path path = this.objectStorage.getPath(buildScopePath(scope));

        if (ioService.exists(path)) {
            walkFileTree(checkNotNull("path",
                                      path),
                         new SimpleFileVisitor<Path>() {
                             @Override
                             public FileVisitResult visitFile(final Path file,
                                                              final BasicFileAttributes attrs) throws IOException {
                                 try {
                                     checkNotNull("file",
                                                  file);

                                     final String fileName = file.getFileName().toString();
                                     final int keyEndIndex = fileName.length() - FILE_FORMAT_SIZE;
                                     final String fileNameWithoutFormat = fileName.substring(0,
                                                                                             keyEndIndex);

                                     keys.add(fileNameWithoutFormat);
                                 } catch (final Exception ex) {
                                     logger.error("An unexpected exception was thrown: ",
                                                  ex);
                                     return FileVisitResult.TERMINATE;
                                 }

                                 return FileVisitResult.CONTINUE;
                             }
                         });
        }

        return keys;
    }

    @Override
    public Collection<String> allKeys(final List<PreferenceScope> scopes) {
        Collection<String> keys = new LinkedHashSet<>();

        scopes.forEach(scope -> keys.addAll(allKeys(scope)));

        return keys;
    }

    String buildScopePath(final PreferenceScope scope) {
        if (scope == null) {
            throw new InvalidPreferenceScopeException("The scope must not be null when building a scope path.");
        }

        String path = "/config/";

        for (PreferenceScope currentScope = scope; currentScope != null; currentScope = currentScope.childScope()) {
            path += currentScope.type() + "/" + currentScope.key() + "/";
        }

        return path;
    }

    String buildScopedPreferencePath(final PreferenceScope scope,
                                     final String key) {
        return buildScopePath(scope) + key + FILE_FORMAT;
    }
}
