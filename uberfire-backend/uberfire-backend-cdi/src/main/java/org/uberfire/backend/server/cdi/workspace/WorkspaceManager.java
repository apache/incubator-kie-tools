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

package org.uberfire.backend.server.cdi.workspace;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.cdi.workspace.Workspace;
import org.uberfire.backend.server.cdi.model.WorkspaceImpl;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Contains every workspace created in the application and the beans for those workspaces.
 * Beans are stored into a cache, with size and time expiration.
 */
@ApplicationScoped
public class WorkspaceManager {

    private Logger logger = LoggerFactory.getLogger(WorkspaceManager.class);
    private WorkspaceManagerPreferences preferences;
    private ConcurrentHashMap<Workspace, Cache<String, Object>> workspaces;

    public WorkspaceManager() {
    }

    @Inject
    public WorkspaceManager(WorkspaceManagerPreferences workspaceManagerPreferences) {
        this.preferences = workspaceManagerPreferences;
    }

    @PostConstruct
    public void initialize() {
        this.workspaces = new ConcurrentHashMap<>();
    }

    /**
     * Returns a workspace, but if it does not exists, it creates a new one.
     *
     * @param name The name of the workspace.
     * @return The existent or the new workspace.
     */
    public Workspace getOrCreateWorkspace(String name) {
        checkNotNull("name",
                     name);
        Workspace workspace = new WorkspaceImpl(name);
        workspaces.computeIfAbsent(new WorkspaceImpl(name),
                                   w -> this.createCache());
        return this.getWorkspace(name);
    }

    protected synchronized Cache<String, Object> createCache() {
        preferences.load();
        final Cache<String, Object> cache = CacheBuilder.newBuilder()
                .maximumSize(preferences.getCacheMaximumSize())
                .expireAfterAccess(preferences.getCacheExpirationTime(),
                                   TimeUnit.valueOf(preferences.getCacheExpirationUnit()))
                .removalListener(removalNotification -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[{},{}] {}",
                                     removalNotification.getKey().toString(),
                                     removalNotification.getValue().toString(),
                                     removalNotification.getCause().toString());
                    }
                })
                .build();
        return cache;
    }

    /**
     * Returns a workspace. If the workspace does ont exists it throws {@link NoSuchElementException}
     *
     * @param name Workspace name
     * @return The workspace object
     */
    public Workspace getWorkspace(String name) {
        checkNotNull("name",
                     name);
        Optional<Workspace> optionalWorkspace = this.workspaces.keySet()
                .stream()
                .filter(w -> name.equals(w.getName()))
                .findAny();
        return optionalWorkspace
                .orElseThrow(() -> new NoSuchElementException(String.format("Workspace <<%s>> not found",
                                                                            name)));
    }

    /**
     * Returns a bean based on a workspace and a bean name. If the bean does not exist, returns null
     *
     * @param workspace The workspace name.
     * @param beanName  The bean name for that workspace.
     * @return the bean instance
     */
    public <T> T getBean(Workspace workspace,
                         String beanName) {
        checkNotNull("workspace",
                     workspace);
        checkNotNull("beanName",
                     beanName);
        return (T) this.workspaces.get(workspace).getIfPresent(beanName);
    }

    /**
     * Put a bean instance into a Workspace.
     *
     * @param workspace The workspace to store beans
     * @param beanName  The bean name
     * @param instance  The bean instance
     */
    public <T> void putBean(Workspace workspace,
                            String beanName,
                            T instance) {
        try {
            checkNotNull("beanName",
                         beanName);
            this.workspaces.get(workspace).get(beanName,
                                               () -> instance);
        } catch (ExecutionException e) {
            logger.error("An error ocurred trying to store bean <<{}>>",
                         instance.getClass().getSimpleName(),
                         e);
        }
    }

    /**
     * Deletes a workspace and its beans
     *
     * @param workspace the workspace to delete
     */
    public void delete(final Workspace workspace) {
        this.workspaces.remove(workspace);
    }

    /**
     * Returns the workspace count
     *
     * @return the number of workspaces
     */
    public int getWorkspaceCount() {
        return this.workspaces.size();
    }

    /**
     * Return the beans count for a workspace
     *
     * @param workspace The workspace to count beans
     * @return The number of beans for a workspace
     */
    public long getBeansCount(final Workspace workspace) {
        return this.workspaces.get(workspace).size();
    }
}
