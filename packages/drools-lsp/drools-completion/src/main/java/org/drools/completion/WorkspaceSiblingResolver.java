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
import java.util.List;
import java.util.Map;

/**
 * Resolves the set of DRL files that should be considered "siblings" of a
 * given document — files in the same logical group whose declared types are
 * in scope for completion and navigation.
 *
 * <p>{@link ConfiguredGroupingResolver} is the implementation the server ships
 * with: it derives groups from {@code META-INF/kmodule.xml} and from a
 * {@code drl-lsp-kbases.json} config file. A host with a grouping model that
 * neither expresses can supply its own implementation, either through
 * {@link java.util.ServiceLoader} (a provider on the server's classpath is
 * preferred over the shipped one) or by calling
 * {@link WorkspaceSiblingResolvers#setActive}.
 *
 * <p>Only {@link #resolveSiblings} must be implemented. The rest carry defaults
 * so that an implementation without a named-group model — one that just answers
 * "what else is in scope here?" — stays a one-method interface.
 */
public interface WorkspaceSiblingResolver {

    /**
     * Returns the absolute paths of the files grouped with
     * {@code currentFile} (excluding the file itself), or an empty list when
     * no grouping applies.
     */
    List<Path> resolveSiblings(Path currentFile);

    /**
     * One named group of DRL files, with enough provenance for a client to
     * explain it to the user.
     *
     * @param name       the group's name, unique within the workspace
     * @param files      the absolute paths it contains, in declaration order
     * @param kind       a short display noun for what this group <em>is</em>, when
     *                   the resolver can say something more specific than
     *                   "group" — the shipped resolver reports {@code "KIE base"}
     *                   for a group that came from a {@code <kbase>} in a
     *                   {@code kmodule.xml}, since there it genuinely is one.
     *                   {@code null} means the generic case, and a client should
     *                   fall back to its own neutral wording
     * @param declaredIn the file that declared the group, or {@code null} when
     *                   the resolver has no single file to point at. Answers
     *                   "why is this file in this group?", which is the question
     *                   a user actually has when the answer surprises them
     */
    record Group(String name, List<Path> files, String kind, Path declaredIn) {

        public Group {
            files = List.copyOf(files);
        }
    }

    /**
     * Returns every named group in the workspace, keyed by name, so a client can
     * show which group a file is in and offer the others. The default returns an
     * empty map, meaning "this resolver has no groups to name".
     */
    default Map<String, Group> resolveAllGroups() {
        return Map.of();
    }

    /**
     * Pins {@code file} to {@code groupName}, overriding whatever the
     * configuration resolves it to, until the workspace root changes. A blank
     * or {@code null} name clears the pin.
     *
     * <p>A file can legitimately belong to several groups — overlapping
     * {@code packages} patterns are normal — so this is how a user settles which
     * one the editor should work in. The default is a no-op.
     */
    default void setGroupOverride(Path file, String groupName) {
        // no-op: resolvers without named groups have nothing to pin to
    }

    /**
     * Supplies the workspace root once it is known, on LSP {@code initialize}.
     * Resolvers that read grouping configuration from the workspace load it
     * here. The default is a no-op.
     */
    default void setWorkspaceRoot(Path workspaceRoot) {
        // no-op: resolvers with no workspace-dependent state need no root
    }

    /**
     * Re-reads whatever this resolver derives its grouping from, after something
     * it depends on has changed on disk. The default is a no-op.
     */
    default void reload() {
        // no-op: resolvers that hold no loaded state have nothing to re-read
    }

    /**
     * Supplies grouping declared in the editor's own settings, as raw JSON,
     * rather than in a file the workspace contains. {@code null} clears it.
     *
     * <p>Part of the interface rather than the shipped implementation so that a
     * host installing its own resolver still receives the setting, instead of
     * the server having to know which implementation is active. The default is a
     * no-op — a resolver that takes no configuration can ignore it.
     */
    default void setSettingsConfig(String json) {
        // no-op: resolvers with no settings-driven configuration ignore this
    }

    /**
     * Supplies the workspace's files, sparing the resolver from discovering them
     * itself. {@code null} withdraws the list and restores self-discovery.
     *
     * <p>A client knows which files its user considers part of the project — it
     * applies their exclude settings and ignore files — whereas a server can only
     * guess, and a guess encoded as a list of directory names to skip goes stale
     * and silently drops files. Where the client can answer, it should. The
     * default is a no-op.
     */
    default void setWorkspaceFiles(List<Path> files) {
        // no-op: resolvers that discover their own files ignore this
    }
}
