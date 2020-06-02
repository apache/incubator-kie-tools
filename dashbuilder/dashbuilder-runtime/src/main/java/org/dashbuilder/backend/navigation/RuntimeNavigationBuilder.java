/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.backend.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavItemContext;
import org.dashbuilder.navigation.NavItemVisitor;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.navigation.json.NavTreeJSONMarshaller;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

/**
 * Builds the navigation for Dashbuilder Runtime
 *
 */
@ApplicationScoped
public class RuntimeNavigationBuilder {

    static final String ORPHAN_GROUP_ID = "__runtime_dashboards";
    static final String ORPHAN_GROUP_NAME = "Runtime Dashboards";
    static final String ORPHAN_GROUP_DESC = "Dashboards";

    Logger logger = LoggerFactory.getLogger(RuntimeNavigationBuilder.class);

    public NavTree build(Optional<String> navTreeJson, List<LayoutTemplate> layoutTemplates) {
        if (navTreeJson.isPresent()) {
            NavTree navTree = NavTreeJSONMarshaller.get().fromJson(navTreeJson.get());
            return buildRuntimeTree(navTree, layoutTemplates);
        }
        return navTreeForTemplates(layoutTemplates);
    }

    protected NavTree buildRuntimeTree(NavTree navTree, List<LayoutTemplate> layoutTemplates) {
        RuntimeNavItemVisitor visitor = new RuntimeNavItemVisitor(layoutTemplates);

        navTree.accept(visitor);

        List<NavGroup> groups = visitor.getGroups();
        List<String> notExported = visitor.getNotExportedPerspectives();
        List<LayoutTemplate> orphanTemplates = visitor.getOrphanItems();

        groups.forEach(grp -> grp.getChildren().removeIf(i -> notExported.contains(i.getId())));
        groups.removeIf(grp -> grp.getChildren().isEmpty());

        NavTreeBuilder builder = new NavTreeBuilder();

        if (!orphanTemplates.isEmpty()) {
            logger.info("Found {} layout components without a group", orphanTemplates.size());
            buildLayoutTemplatesGroup(orphanTemplates, builder);
        }

        NavTree runtimeNavTree = builder.build();
        runtimeNavTree.getRootItems().addAll(groups);
        return runtimeNavTree;
    }

    private NavTree navTreeForTemplates(List<LayoutTemplate> layoutTemplates) {
        NavTreeBuilder treeBuilder = new NavTreeBuilder();
        return buildLayoutTemplatesGroup(layoutTemplates, treeBuilder).build();
    }

    private NavTreeBuilder buildLayoutTemplatesGroup(List<LayoutTemplate> layoutTemplates, NavTreeBuilder treeBuilder) {
        treeBuilder.group(ORPHAN_GROUP_ID, ORPHAN_GROUP_NAME, ORPHAN_GROUP_DESC, false);
        layoutTemplates.forEach(lt -> {
            NavItemContext ctx = NavWorkbenchCtx.perspective(lt.getName());
            treeBuilder.item(lt.getName(), lt.getName(), "", true, ctx);
        });
        treeBuilder.endGroup();
        return treeBuilder;
    }

    List<LayoutTemplate> checkOrphansLayoutTemplates(NavTree navTree,
                                                     List<LayoutTemplate> layoutTemplates) {
        return layoutTemplates.stream()
                              .filter(lt -> navTree.getItemById(lt.getName()) == null)
                              .collect(Collectors.toList());
    }

    /**
     * 
     * Remove groups with child groups that have no children
     * @param navGroup
     * @param originTree
     */
    void removedEmptyNestedGroups(NavGroup navGroup, List<String> itemsToRemove) {

        filteringGroups(navGroup.getChildren()).forEach(group -> removedEmptyNestedGroups(group, itemsToRemove));

        if (navGroup.getChildren().isEmpty()) {
            logger.info("Removing groups {}", navGroup.getName());
            itemsToRemove.add(navGroup.getId());
        }

    }

    Stream<NavGroup> filteringGroups(List<NavItem> items) {
        return items.stream()
                    .filter(item -> item instanceof NavGroup)
                    .map(item -> (NavGroup) item);
    }

    /**
     * Collects non empty groups and create a flatten tree (all groups as root items). 
     *
     */
    class RuntimeNavItemVisitor implements NavItemVisitor {

        List<NavGroup> groups;
        List<LayoutTemplate> layoutTemplates;
        List<LayoutTemplate> orphanItems;
        List<String> notExportedPerspectives;

        public RuntimeNavItemVisitor(List<LayoutTemplate> layoutTemplates) {
            this.groups = new ArrayList<>();
            this.notExportedPerspectives = new ArrayList<>();
            this.orphanItems = new ArrayList<>(layoutTemplates);
            this.layoutTemplates = layoutTemplates;
        }

        @Override
        public void visitGroup(NavGroup group) {
            if (!group.getChildren().isEmpty()) {
                NavGroup clonnedGroup = (NavGroup) group.cloneItem();
                clonnedGroup.setParent(null);
                clonnedGroup.getChildren().removeIf(item -> item instanceof NavGroup);
                groups.add(clonnedGroup);
            }
        }

        @Override
        public void visitItem(NavItem item) {
            String resourceId = NavWorkbenchCtx.get(item.getContext()).getResourceId();
            if (layoutTemplates.stream().noneMatch(lt -> lt.getName().equals(resourceId))) {
                notExportedPerspectives.add(item.getId());
            }

            orphanItems.stream()
                       .filter(lt -> lt.getName().equals(resourceId))
                       .findFirst().ifPresent(notOrphanItem -> orphanItems.remove(notOrphanItem));
        }

        @Override
        public void visitDivider(NavDivider divider) {
            // do nothing
        }

        public List<NavGroup> getGroups() {
            return groups;
        }

        public List<LayoutTemplate> getOrphanItems() {
            return orphanItems;
        }

        public List<String> getNotExportedPerspectives() {
            return notExportedPerspectives;
        }

    }

}