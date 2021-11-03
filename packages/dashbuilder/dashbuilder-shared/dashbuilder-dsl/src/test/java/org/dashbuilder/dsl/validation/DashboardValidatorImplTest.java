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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.dsl.model.Dashboard;
import org.dashbuilder.dsl.model.Page;
import org.dashbuilder.dsl.validation.ValidationResult.ValidationResultType;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.dashbuilder.dataset.def.DataSetDefFactory.newStaticDataSetDef;
import static org.dashbuilder.displayer.DisplayerSettingsFactory.newAreaChartSettings;
import static org.dashbuilder.displayer.DisplayerSettingsFactory.newExternalDisplayerSettings;
import static org.dashbuilder.dsl.factory.component.ComponentFactory.displayer;
import static org.dashbuilder.dsl.factory.component.ComponentFactory.external;
import static org.dashbuilder.dsl.factory.dashboard.DashboardFactory.dashboard;
import static org.dashbuilder.dsl.factory.navigation.NavigationFactory.group;
import static org.dashbuilder.dsl.factory.navigation.NavigationFactory.item;
import static org.dashbuilder.dsl.factory.navigation.NavigationFactory.navigation;
import static org.dashbuilder.dsl.factory.page.PageFactory.page;
import static org.dashbuilder.dsl.factory.page.PageFactory.row;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DashboardValidatorImplTest {

    private DashboardValidatorImpl impl;
    private Path componentsPath;

    @Before
    public void before() throws IOException {
        impl = new DashboardValidatorImpl();
        componentsPath = Paths.get(this.getClass().getResource("/components").getFile());
    }

    @Test
    public void testMissingDataSet() {
        DisplayerSettings settings = newAreaChartSettings().subType_Area().dataset("nonExisting").buildSettings();
        Page page = page("abc", row(displayer(settings)));
        Dashboard dashboard = dashboard(asList(page));
        List<ValidationResult> results = impl.validate(dashboard);
        ValidationResult result = results.get(0);
        assertEquals(1, results.size());
        assertEquals("The following data sets definitions used in page abc were not found: nonExisting", result.getMessage());
        assertEquals(ValidationResult.ValidationResultType.ERROR, result.getType());
    }

    @Test
    public void testDataSetFound() {
        DataSetDef def = newStaticDataSetDef().uuid("myds").buildDef();
        DisplayerSettings settings = newAreaChartSettings().subType_Area().dataset(def.getUUID()).buildSettings();
        Page page = page("abc", row(displayer(settings)));
        Dashboard dashboard = dashboard(asList(page), asList(def));
        List<ValidationResult> results = impl.validate(dashboard);
        ValidationResult result = results.get(0);
        assertEquals(1, results.size());
        assertEquals("No missing data set dependencies for page abc", result.getMessage());
        assertEquals(ValidationResult.ValidationResultType.SUCCESS, result.getType());
    }

    @Test
    public void testMissingComponent() {
        DisplayerSettings settings = DisplayerSettingsFactory.newExternalDisplayerSettings().buildSettings();
        Page page = page("abc", row(external("missingComp", settings)));
        Dashboard dashboard = dashboard(asList(page));
        List<ValidationResult> results = impl.validate(dashboard);
        ValidationResult result = results.get(0);
        assertEquals(1, results.size());
        assertEquals("The following components used in page abc were not found: missingComp", result.getMessage());
        assertEquals(ValidationResult.ValidationResultType.WARNING, result.getType());
    }

    @Test
    public void testExistingComponent() {
        DisplayerSettings settings = newExternalDisplayerSettings().buildSettings();
        Page page = page("abc", row(external("comp1", settings)));
        Dashboard dashboard = dashboard(asList(page), componentsPath);
        List<ValidationResult> results = impl.validate(dashboard);
        assertEquals(1, results.size());
        assertEquals("No missing component dependencies for page abc", results.get(0).getMessage());
    }

    @Test
    public void testMissingNavigationItem() {
        Page page = page("page");
        Page page2 = page("page2");
        Dashboard dashboard = dashboard(asList(page), navigation(group("test", item(page2))));
        List<ValidationResult> results = impl.validate(dashboard);
        ValidationResult result = results.get(0);
        assertEquals(1, results.size());
        assertEquals("Navigation item page2 has no corresponding page", result.getMessage());
        assertEquals(ValidationResult.ValidationResultType.ERROR, result.getType());
    }
    
    @Test
    public void testNavigationWithMissingGroup() {
        Page page = page("page");
        Dashboard dashboard = dashboard(asList(page), navigation(group("test", item(page)), group("test", item(page))));
        List<ValidationResult> results = impl.validate(dashboard);
        ValidationResult result = results.get(0);
        assertEquals(1, results.size());
        assertEquals("The Navigation constains duplicate group names", result.getMessage());
        assertEquals(ValidationResult.ValidationResultType.ERROR, result.getType());
    }

    @Test
    public void testNavigationCorrect() {
        Page page = page("page");
        Dashboard dashboard = dashboard(asList(page), navigation(group("test", item(page))));
        List<ValidationResult> results = impl.validate(dashboard);
        ValidationResult result = results.get(0);
        assertEquals(1, results.size());
        assertEquals("Navigation is valid", result.getMessage());
        assertEquals(ValidationResult.ValidationResultType.SUCCESS, result.getType());
    }

    @Test
    public void testSuccessfulValidation() {
        DataSetDef def = newStaticDataSetDef().uuid("myds").buildDef();
        DisplayerSettings settings = newAreaChartSettings().subType_Area().dataset(def.getUUID()).buildSettings();
        DisplayerSettings externalSettings = newExternalDisplayerSettings().buildSettings();
        Page page = page("abc", row(external("comp1", externalSettings)), row(displayer(settings)));
        Dashboard dashboard = dashboard(asList(page), asList(def), navigation(group("test", item(page))), componentsPath);
        List<ValidationResult> results = impl.validate(dashboard);
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(r -> r.type == ValidationResultType.SUCCESS));
    }

}