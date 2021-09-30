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
package org.dashbuilder.dsl.serialization.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.Position;
import org.dashbuilder.dsl.model.Dashboard;
import org.dashbuilder.dsl.model.Navigation;
import org.dashbuilder.dsl.model.Page;
import org.dashbuilder.navigation.json.NavTreeJSONMarshaller;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.css.CssProperty;

import static java.util.Arrays.asList;
import static org.dashbuilder.dataset.def.DataSetDefFactory.newCSVDataSetDef;
import static org.dashbuilder.displayer.DisplayerSettingsFactory.newLineChartSettings;
import static org.dashbuilder.displayer.DisplayerSettingsFactory.newSelectorSettings;
import static org.dashbuilder.dsl.factory.component.ComponentFactory.external;
import static org.dashbuilder.dsl.factory.dashboard.DashboardFactory.dashboard;
import static org.dashbuilder.dsl.factory.navigation.NavigationFactory.group;
import static org.dashbuilder.dsl.factory.navigation.NavigationFactory.item;
import static org.dashbuilder.dsl.factory.navigation.NavigationFactory.navigation;
import static org.dashbuilder.dsl.factory.page.PageFactory.page;
import static org.dashbuilder.dsl.factory.page.PageFactory.pageBuilder;
import static org.dashbuilder.dsl.factory.page.PageFactory.row;
import static org.junit.Assert.assertEquals;

public class DashboardZipSerializerTest {

    String csvFilePath = this.getClass().getResource("/data/un_world_pop_medium_variant.csv").getFile();
    private DashboardZipSerializer serializer;

    @Before
    public void before() {
        serializer = new DashboardZipSerializer();
    }

    @Test
    public void testExportedStaticFiles() throws IOException {
        DataSetDef def = newCSVDataSetDef().uuid("myDs")
                                           .filePath(csvFilePath)
                                           .separatorChar(',')
                                           .quoteChar('"')
                                           .column("Year", ColumnType.NUMBER)
                                           .cacheOff()
                                           .buildDef();

        Page page = page("My Page", row(external("comp1")));
        Path componentsPath = Paths.get(this.getClass().getResource("/components").getFile());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(dashboard(asList(page), asList(def), componentsPath), out);

        Map<String, String> content = serializer.readAllEntriesContent(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(read("/components/comp1/manifest.json"), content.get("dashbuilder/components/comp1/manifest.json"));
        assertEquals(read("/components/comp1/index.html"), content.get("dashbuilder/components/comp1/index.html"));
        assertEquals(read("/components/comp1/index.js"), content.get("dashbuilder/components/comp1/index.js"));

        assertEquals(read("/data/un_world_pop_medium_variant.csv"), content.get("dashbuilder/datasets/definitions/myDs.csv"));

    }

    @Test
    public void testExportedZip() throws Exception {
        DataSetDef def = newCSVDataSetDef().uuid(UUID.randomUUID().toString())
                                           .filePath(csvFilePath)
                                           .separatorChar(',')
                                           .quoteChar('"')
                                           .buildDef();

        DisplayerSettings filterByYear = newSelectorSettings().subtype(DisplayerSubType.SELECTOR_SLIDER)
                                                              .width(1200)
                                                              .filterOn(false, true, false)
                                                              .dataset(def.getUUID())
                                                              .column("Year")
                                                              .buildSettings();

        DisplayerSettings evolutionChart = newLineChartSettings().subType_SmoothLine()
                                                                 .dataset(def.getUUID())
                                                                 .width(1200)
                                                                 .legendOn(Position.IN)
                                                                 .xAxisAngle(0)
                                                                 .margins(0, 30, 0, 0)
                                                                 .filterOn(false, false, true)
                                                                 .group("Year")
                                                                 .column("Year")
                                                                 .expression("parseInt(value)")
                                                                 .column("PopTotal", AggregateFunctionType.SUM, "Total")
                                                                 .column("PopFemale", AggregateFunctionType.SUM, "Female")
                                                                 .column("PopMale", AggregateFunctionType.SUM, "Male")
                                                                 .buildSettings();

        Page page = pageBuilder("Population").cssProperty(CssProperty.MARGIN_LEFT, "10px")
                                             .rows(row("<h3>Population growth until 2100</h3>"),
                                                   row(filterByYear),
                                                   row(evolutionChart))
                                             .build();

        Navigation navigation = navigation(group("Test Group", item(page)));
        Dashboard db = dashboard(asList(page), asList(def), navigation);

        String navTreeString = NavTreeJSONMarshaller.get().toJson(navigation.getNavTree()).toString();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        serializer.serialize(db, out);

        Dashboard deserialized = serializer.internalDeserialize(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(page.getLayoutTemplate().getName(),
                     deserialized.getPages().get(0).getLayoutTemplate().getName());
        assertEquals(def.getUUID(),
                     deserialized.getDataSets().get(0).getUUID());
        assertEquals(navTreeString,
                     NavTreeJSONMarshaller.get().toJson(deserialized.getNavigation().getNavTree()).toString());
    }

    private String read(String path) throws IOException {
        return Files.readAllLines(Paths.get(this.getClass().getResource(path).getFile())).stream().collect(Collectors.joining("\n"));
    }

}