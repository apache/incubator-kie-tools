/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.dsl.factory.navigation.NavigationFactory;
import org.dashbuilder.dsl.model.Dashboard;
import org.dashbuilder.dsl.model.Navigation;
import org.dashbuilder.dsl.model.Page;
import org.dashbuilder.navigation.NavDivider;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavItemVisitor;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.dashbuilder.dsl.helper.ComponentsHelper.collectingPropertyValue;
import static org.dashbuilder.dsl.helper.ComponentsHelper.listComponentsIds;
import static org.dashbuilder.dsl.validation.ValidationResult.error;
import static org.dashbuilder.dsl.validation.ValidationResult.success;
import static org.dashbuilder.dsl.validation.ValidationResult.warning;
import static org.dashbuilder.external.model.ExternalComponent.COMPONENT_ID_KEY;

class DashboardValidatorImpl implements DashboardValidator {

    private static final String MISSING_NAVIGATION_ITEM = "Navigation item %s has no corresponding page";
    private static final String DUPLICATED_GROUPS = "The Navigation constains duplicate group names";
    private static final String VALID_NAVIGATION = "Navigation is valid";
    private static final String NO_MISSING_DATA_SET = "No missing data set dependencies for page %s";
    private static final String MISSING_DATA_SET = "The following data sets definitions used in page %s were not found: %s";

    private static final String NO_MISSING_COMPONENT = "No missing component dependencies for page %s";
    private static final String MISSING_COMPONENT_MESSAGE = "The following components used in page %s were not found: %s";

    DisplayerSettingsJSONMarshaller displayerSettingsMarshaller = DisplayerSettingsJSONMarshaller.get();

    DashboardValidatorImpl() {
        // empty
    }

    @Override
    public List<ValidationResult> validate(Dashboard dashboard) {
        List<ValidationResult> results = new ArrayList<>();
        results.addAll(checkDataSetsDependencies(dashboard));
        results.addAll(checkComponentsDependencies(dashboard));
        results.addAll(checkNavigation(dashboard));
        return results;
    }

    private List<ValidationResult> checkNavigation(Dashboard dashboard) {
        Navigation navigation = dashboard.getNavigation();
        NavigationPagesVisitor visitor = new NavigationPagesVisitor();
        List<ValidationResult> results = new ArrayList<>();
        if (navigation == null || navigation == NavigationFactory.emptyNavigation()) {
            return Collections.emptyList();
        }

        navigation.getNavTree().accept(visitor);

        List<String> visitedPages = visitor.getVisitedPages();
        List<String> visitedGroups = visitor.getVisitedGroups();
        if (!visitedPages.isEmpty()) {
            visitedPages.stream()
                        .filter(vp -> dashboard.getPages().stream().noneMatch(p -> p.getLayoutTemplate().getName().equals(vp)))
                        .map(vp -> error(format(MISSING_NAVIGATION_ITEM, vp)))
                        .forEach(results::add);
        }
        
        if (containDuplicates(visitedGroups)) {
            results.add(error(DUPLICATED_GROUPS));
        }
        
        if (results.isEmpty()) {
            results.add(ValidationResult.success(VALID_NAVIGATION));
        }
        return results;
    }

    List<ValidationResult> checkComponentsDependencies(Dashboard dashboard) {
        List<String> components = listComponentsIds(dashboard);
        return dashboard.getPages().stream()
                        .map(p -> validateComponentsUsage(p, components))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

    }

    List<ValidationResult> checkDataSetsDependencies(Dashboard dashboard) {
        return dashboard.getPages().stream()
                        .map(p -> validateDataSetsUsage(p, dashboard.getDataSets()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }

    ValidationResult validateDataSetsUsage(Page page, List<DataSetDef> dataSets) {
        List<String> dataSetsDeps = findDataSetsDependencies(page);
        if (dataSetsDeps.isEmpty()) {
            return null;
        }
        Set<String> missingDataSets = dataSetsDeps.stream()
                                                  .filter(uuid -> noneMatch(dataSets, ds -> ds.getUUID().equals(uuid)))
                                                  .collect(Collectors.toSet());
        return missingDataSets.isEmpty()
                ? success(format(NO_MISSING_DATA_SET, page.getLayoutTemplate().getName()))
                : error(format(MISSING_DATA_SET,
                               page.getLayoutTemplate().getName(),
                               missingDataSets.stream()
                                              .collect(joining(", "))));
    }

    ValidationResult validateComponentsUsage(Page page, List<String> components) {
        List<String> componentsDeps = findComponentsDependencies(page);
        if (componentsDeps.isEmpty()) {
            return null;
        }
        Set<String> missingComponents = componentsDeps.stream()
                                                      .filter(id -> noneMatch(components, cid -> cid.equals(id)))
                                                      .collect(Collectors.toSet());
        return missingComponents.isEmpty()
                ? success(format(NO_MISSING_COMPONENT, page.getLayoutTemplate().getName()))
                : warning(format(MISSING_COMPONENT_MESSAGE,
                                 page.getLayoutTemplate().getName(),
                                 missingComponents.stream()
                                                  .collect(joining(", "))));
    }

    private <T> boolean noneMatch(List<T> list, Predicate<T> test) {
        return list.isEmpty() || list.stream().noneMatch(test::test);
    }

    private List<String> findDataSetsDependencies(Page page) {
        return collectingPropertyValue(page, "json").map(displayerSettingsMarshaller::fromJsonString)
                                                    .map(DisplayerSettings::getDataSetLookup)
                                                    .filter(Objects::nonNull)
                                                    .map(DataSetLookup::getDataSetUUID)
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList());
    }

    private List<String> findComponentsDependencies(Page page) {
        return collectingPropertyValue(page, COMPONENT_ID_KEY).filter(c -> !c.endsWith("provided"))
                                                              .collect(Collectors.toList());

    }
    
    private boolean containDuplicates(List<String> list) {
        return list.size() > list.stream().distinct().count();
    }

    static class NavigationPagesVisitor implements NavItemVisitor {

        private List<String> visitedPages;
        
        private List<String> visitedGroups;

        NavigationPagesVisitor() {
            this.visitedPages = new ArrayList<>();
            this.visitedGroups = new ArrayList<>(); 
        }

        public List<String> getVisitedPages() {
            return visitedPages;
        }

        @Override
        public void visitGroup(NavGroup group) {
            visitedGroups.add(group.getName());

        }

        @Override
        public void visitItem(NavItem item) {
            visitedPages.add(item.getName());

        }

        @Override
        public void visitDivider(NavDivider divider) {
            // empty
        }
        
        public List<String> getVisitedGroups() {
            return visitedGroups;
        }

    }

}