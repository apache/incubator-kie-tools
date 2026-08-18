/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * The {@link WorkspaceSiblingResolver} shipped with the server: it groups DRL
 * files the way the project's own configuration says they compile together.
 *
 * <p>Before this existed the SPI had no implementation that could be installed
 * without running custom Java inside the server process, which meant grouping
 * other than by directory required shipping a patched server. Configuration is
 * now enough.
 *
 * <p>Groups are resolved from two tiers, in precedence order:
 * <ol>
 *   <li>{@value WorkspaceScan#CONFIG_FILE_NAME} — explicit groups, and existing
 *       rule manifests adopted through its {@code sources}.</li>
 *   <li>{@code META-INF/kmodule.xml} — the {@code <kbase>} declarations the
 *       project already builds with.</li>
 * </ol>
 *
 * <p>The kmodule tier is what makes grouping work with no configuration at all
 * for a conventional project: it reads the same {@code packages} attribute the
 * compiler uses to decide which resources form a knowledge base, so the editor's
 * idea of scope matches the build's by construction rather than by convention.
 *
 * <p>Files that no group claims — and workspaces with no configuration at all —
 * fall through to the registry's same-directory default, so installing this
 * resolver can only ever add grouping, never take it away.
 */
public final class ConfiguredGroupingResolver implements WorkspaceSiblingResolver {

    private static final Logger logger = Logger.getLogger(ConfiguredGroupingResolver.class.getName());

    /** Group chosen by the user for a specific file, overriding what the config says. */
    private final Map<Path, String> overrides = new ConcurrentHashMap<>();

    private volatile DrlFileGrouping grouping = DrlFileGrouping.EMPTY;
    private volatile Path workspaceRoot;

    /**
     * Grouping declared in the editor's settings rather than a file, as raw
     * JSON. Takes precedence over anything on disk, and lets a workspace be
     * grouped without committing a config file — the file remains the option
     * for a team that wants the grouping shared and reviewed.
     */
    private volatile String settingsConfig;

    /**
     * The workspace's files as reported by the client, or {@code null} when it
     * reported none and this resolver has to find them itself.
     */
    private volatile List<Path> providedFiles;

    @Override
    public void setWorkspaceRoot(Path workspaceRoot) {
        this.workspaceRoot = (workspaceRoot == null) ? null : workspaceRoot.toAbsolutePath().normalize();
        // Overrides are keyed by absolute path and pin a file to a group by
        // name; a new workspace shares neither, so they are dropped with it.
        overrides.clear();
        reload();
    }

    /**
     * Replaces the grouping declared in the editor's settings and reloads.
     * {@code null} or blank clears it, falling back to whatever is on disk.
     */
    @Override
    public void setSettingsConfig(String json) {
        this.settingsConfig = json;
        reload();
    }

    @Override
    public void setWorkspaceFiles(List<Path> files) {
        this.providedFiles = (files == null) ? null : List.copyOf(files);
        reload();
    }

    /**
     * Rebuilds the grouping from disk. Called when the workspace root is set and
     * whenever a config file changes, so an edit takes effect without a restart.
     *
     * <p>Synchronized because reloads genuinely overlap: initialization runs
     * asynchronously while file watchers and settings changes each trigger their
     * own. Without it, two reloads read different configuration, scan for
     * different lengths of time, and whichever finishes last wins — which is not
     * necessarily the one that read the newer configuration. Serializing means a
     * reload always reads the state left by the one before it.
     */
    @Override
    public synchronized void reload() {
        Path root = workspaceRoot;
        DrlFileGrouping loaded = load(root, settingsConfig, providedFiles);
        grouping = loaded;
        if (!loaded.isEmpty()) {
            logger.info("DRL file grouping active: " + loaded.asMap().size() + " group(s) under " + root);
        }
    }

    @Override
    public List<Path> resolveSiblings(Path currentFile) {
        if (currentFile == null) {
            return List.of();
        }
        Path normalized = currentFile.toAbsolutePath().normalize();

        String groupName = overrides.get(normalized);
        if (groupName == null) {
            groupName = grouping.groupFor(normalized);
        }
        if (groupName == null) {
            return WorkspaceSiblingResolvers.sameDirectorySiblings(currentFile);
        }
        List<Path> files = grouping.filesFor(groupName);
        if (files.isEmpty()) {
            // A pin can name a group that a later config edit removed.
            return WorkspaceSiblingResolvers.sameDirectorySiblings(currentFile);
        }
        List<Path> siblings = new ArrayList<>(files.size());
        for (Path file : files) {
            if (!file.equals(normalized)) {
                siblings.add(file);
            }
        }
        return Collections.unmodifiableList(siblings);
    }

    @Override
    public Map<String, Group> resolveAllGroups() {
        return grouping.asMap();
    }

    @Override
    public void setGroupOverride(Path file, String groupName) {
        if (file == null) {
            return;
        }
        Path normalized = file.toAbsolutePath().normalize();
        if (groupName == null || groupName.isBlank()) {
            overrides.remove(normalized);
            return;
        }
        overrides.put(normalized, groupName.trim());
        logger.fine(() -> "Pinned " + normalized.getFileName() + " to DRL file group '" + groupName.trim() + "'");
    }

    // ── loading ──────────────────────────────────────────────────────────────

    /** Resolves the workspace's groups. Never {@code null}. */
    private static DrlFileGrouping load(Path workspaceRoot, String settingsConfig, List<Path> providedFiles) {
        if (workspaceRoot == null) {
            return DrlFileGrouping.EMPTY;
        }
        // Use the client's file list when it gave one; walking is the fallback.
        WorkspaceScan scan = (providedFiles == null)
                ? WorkspaceScan.of(workspaceRoot)
                : WorkspaceScan.ofProvided(providedFiles);

        List<KieBaseDecl> declarations = new ArrayList<>();
        List<KBaseConfigFile.SourceSpec> sources = new ArrayList<>();

        // Settings first, so they win the first-wins index over anything on disk.
        KBaseConfigFile.Parsed fromSettings = KBaseConfigFile.parseInline(settingsConfig, workspaceRoot);
        declarations.addAll(fromSettings.declarations());
        sources.addAll(fromSettings.sources());

        for (Path configFile : scan.configFiles()) {
            KBaseConfigFile.Parsed parsed = KBaseConfigFile.parse(configFile);
            declarations.addAll(parsed.declarations());
            sources.addAll(parsed.sources());
        }
        declarations.addAll(adopt(sources, workspaceRoot));

        for (Path kmoduleFile : scan.kmoduleFiles()) {
            declarations.addAll(KModuleParser.parse(kmoduleFile));
        }

        if (declarations.isEmpty()) {
            logger.fine(() -> "No DRL file grouping declared under " + workspaceRoot
                    + "; falling back to same-directory grouping");
            return DrlFileGrouping.EMPTY;
        }
        return DrlFileGrouping.resolve(declarations, scan.drlFiles(), workspaceRoot);
    }

    /**
     * Resolves each adopted source's globs and reads the manifests they match.
     * All sources share one glob walk.
     *
     * <p>TODO: this walk ignores a client-supplied file list, so a manifest the
     * user excluded can still contribute groups — unlike every other kind of
     * file, which the client decides on. It cannot simply reuse the supplied
     * list: that list is enumerated before any config is read, and these globs
     * are only known afterwards, from a config the client deliberately does not
     * parse. Closing it needs the server to ask the client to resolve a glob
     * (a {@code drools/findFiles}-style request), which {@link #load} cannot
     * await today — it runs synchronously on the initialization path, where
     * blocking on the client risks deadlock. Doing it properly means making
     * loading asynchronous.
     */
    private static List<KieBaseDecl> adopt(List<KBaseConfigFile.SourceSpec> sources, Path workspaceRoot) {
        if (sources.isEmpty()) {
            return List.of();
        }
        Set<String> fileGlobs = new LinkedHashSet<>();
        Set<String> directoryGlobs = new LinkedHashSet<>();
        for (KBaseConfigFile.SourceSpec source : sources) {
            fileGlobs.add(source.includeGlob());
            directoryGlobs.addAll(source.pathsRelativeTo());
        }
        WorkspaceScan.GlobMatches matches = WorkspaceScan.matching(
                workspaceRoot, List.copyOf(fileGlobs), List.copyOf(directoryGlobs));

        List<KieBaseDecl> declarations = new ArrayList<>();
        for (KBaseConfigFile.SourceSpec source : sources) {
            List<Path> manifests = WorkspaceScan.filterByGlob(
                    workspaceRoot, matches.files(), source.includeGlob());
            List<Path> pathRoots = rootsFor(source, matches, workspaceRoot);

            for (Path manifest : manifests) {
                declarations.addAll(KBaseConfigFile.parseAdopted(manifest, source.aliases(), pathRoots));
            }
            logger.fine(() -> "Adopted " + manifests.size() + " manifest(s) matching '"
                    + source.includeGlob() + "'");
        }
        return declarations;
    }

    /**
     * The directories a source's relative paths resolve against: its declared
     * {@code pathsRelativeTo}, expanded through the glob walk, with any literal
     * (wildcard-free) entry resolved directly. Empty when the source declared
     * none, which leaves each manifest anchored at its own directory.
     */
    private static List<Path> rootsFor(KBaseConfigFile.SourceSpec source,
                                       WorkspaceScan.GlobMatches matches,
                                       Path workspaceRoot) {
        if (source.pathsRelativeTo().isEmpty()) {
            return List.of();
        }
        List<Path> roots = new ArrayList<>();
        for (String declared : source.pathsRelativeTo()) {
            if (declared.indexOf('*') < 0 && declared.indexOf('?') < 0) {
                roots.add(workspaceRoot.resolve(declared).toAbsolutePath().normalize());
            }
        }
        roots.addAll(matches.directories());
        if (roots.isEmpty()) {
            logger.warning("No directory matched 'pathsRelativeTo' " + source.pathsRelativeTo()
                    + " for grouping source '" + source.includeGlob()
                    + "'; its relative paths cannot be resolved");
        }
        return roots;
    }
}
