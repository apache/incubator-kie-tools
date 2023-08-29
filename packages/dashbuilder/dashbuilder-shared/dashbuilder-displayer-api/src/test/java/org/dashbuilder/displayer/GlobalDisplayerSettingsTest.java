/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.dashbuilder.displayer;

import java.util.Optional;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GlobalDisplayerSettingsTest {

    GlobalDisplayerSettings globalDisplayerSettings = new GlobalDisplayerSettings() {

        private DisplayerSettings displayerSettings;

        @Override
        public void setDisplayerSettings(DisplayerSettings settings) {
            this.displayerSettings = settings;
        }

        @Override
        public Optional<DisplayerSettings> getSettings() {
            return Optional.ofNullable(displayerSettings);
        };
    };

    @Test
    public void testApply() {
        var globalSettings = new DisplayerSettings();
        var settings = new DisplayerSettings();
        var title = "Testing";

        globalSettings.setTitle(title);
        globalDisplayerSettings.setDisplayerSettings(globalSettings);

        globalDisplayerSettings.apply(settings);

        assertEquals(title, settings.getTitle());

    }

    @Test
    public void testDoNotOverrideUserSetting() {
        var globalSettings = new DisplayerSettings();
        var settings = new DisplayerSettings();
        var userTitle = "User Title";
        var globalTitle = "Global Title";

        settings.setTitle(userTitle);
        globalSettings.setTitle(globalTitle);

        globalDisplayerSettings.setDisplayerSettings(globalSettings);
        globalDisplayerSettings.apply(settings);

        assertEquals(userTitle, settings.getTitle());

    }

    @Test
    public void testGlobalLookup() {
        var globalSettings = new DisplayerSettings();
        var settings = new DisplayerSettings();

        var globalLookup = new DataSetLookup("global", new DataSetSort());
        globalLookup.setRowOffset(5);
        globalLookup.setNumberOfRows(20);
        globalSettings.setDataSetLookup(globalLookup);

        globalDisplayerSettings.setDisplayerSettings(globalSettings);
        globalDisplayerSettings.apply(settings);

        assertEquals(globalLookup.getDataSetUUID(), settings.getDataSetLookup().getDataSetUUID());
        assertEquals(globalLookup.getRowOffset(), settings.getDataSetLookup().getRowOffset());
        assertEquals(globalLookup.getNumberOfRows(), settings.getDataSetLookup().getNumberOfRows());
        assertEquals(globalLookup.getOperationList().size(), settings.getDataSetLookup().getOperationList().size());
    }

    @Test
    public void testKeepUserLookup() {
        var globalSettings = new DisplayerSettings();
        var settings = new DisplayerSettings();

        var userUUID = "user lookup";
        var userRowOffset = 12;
        var userNumberOfRows = 10;
        var userLookup = new DataSetLookup(userUUID, new DataSetSort());
        userLookup.setRowOffset(userRowOffset);
        userLookup.setNumberOfRows(userNumberOfRows);
        settings.setDataSetLookup(userLookup);

        var globalLookup = new DataSetLookup("GLOBAL UUID", new DataSetSort());
        globalLookup.setRowOffset(5);
        globalLookup.setNumberOfRows(20);
        globalSettings.setDataSetLookup(globalLookup);

        globalDisplayerSettings.setDisplayerSettings(globalSettings);
        globalDisplayerSettings.apply(settings);

        assertEquals(userUUID, settings.getDataSetLookup().getDataSetUUID());
        assertEquals(userRowOffset, settings.getDataSetLookup().getRowOffset());
        assertEquals(userNumberOfRows, settings.getDataSetLookup().getNumberOfRows());
        assertEquals(2, settings.getDataSetLookup().getOperationList().size());
    }

    @Test
    public void testKeepSomeOfUserLookup() {
        var globalSettings = new DisplayerSettings();
        var settings = new DisplayerSettings();

        var userNumberOfRows = 6;
        var userLookup = new DataSetLookup(null);
        userLookup.setNumberOfRows(userNumberOfRows);
        settings.setDataSetLookup(userLookup);

        var globalLookup = new DataSetLookup("GLOBAL UUID", new DataSetSort());
        globalLookup.setRowOffset(5);
        globalLookup.setNumberOfRows(20);
        globalSettings.setDataSetLookup(globalLookup);

        globalDisplayerSettings.setDisplayerSettings(globalSettings);
        globalDisplayerSettings.apply(settings);

        assertEquals(globalLookup.getDataSetUUID(), settings.getDataSetLookup().getDataSetUUID());
        assertEquals(globalLookup.getRowOffset(), settings.getDataSetLookup().getRowOffset());
        assertEquals(userNumberOfRows, settings.getDataSetLookup().getNumberOfRows());
        assertEquals(1, settings.getDataSetLookup().getOperationList().size());
    }

    @Test
    public void testLookupOpOrder() {
        var globalSettings = new DisplayerSettings();
        var settings = new DisplayerSettings();

        var userLookup = new DataSetLookup(null);
        userLookup.addOperation(new DataSetFilter());
        settings.setDataSetLookup(userLookup);

        var globalLookup = new DataSetLookup("GLOBAL UUID");
        globalLookup.addOperation(new DataSetSort());
        globalSettings.setDataSetLookup(globalLookup);

        globalDisplayerSettings.setDisplayerSettings(globalSettings);
        globalDisplayerSettings.apply(settings);

        assertEquals(globalLookup.getDataSetUUID(), settings.getDataSetLookup().getDataSetUUID());
        assertEquals(DataSetOpType.SORT, settings.getDataSetLookup().getOperation(0).getType());
        assertEquals(DataSetOpType.FILTER, settings.getDataSetLookup().getOperation(1).getType());
    }

    @Test
    public void testGlobalColumnsSettings() {
        var globalSettings = new DisplayerSettings();
        var settings = new DisplayerSettings();
        var userSettingsColumnId = "user columns";
        var globalSettingsColumnId = "global column";
        settings.getColumnSettingsList().add(new ColumnSettings(userSettingsColumnId));
        globalSettings.getColumnSettingsList().add(new ColumnSettings(globalSettingsColumnId));
        globalDisplayerSettings.setDisplayerSettings(globalSettings);
        globalDisplayerSettings.apply(settings);

        assertEquals(2, settings.getColumnSettingsList().size());
        assertEquals(globalSettingsColumnId, settings.getColumnSettingsList().get(0).getColumnId());
        assertEquals(userSettingsColumnId, settings.getColumnSettingsList().get(1).getColumnId());

    }

}
