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

package org.dashbuilder.dataprovider.external;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.scheduler.DataSetInvalidationTask;
import org.dashbuilder.scheduler.Scheduler;
import org.dashbuilder.scheduler.SchedulerTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ExternalDataSetProviderTest {

    private static String DEF_NAME = "def";
    private static String DEF_UUID = "ds1";
    private static String DEF_NAME_URL_PROP = "dashbuilder.dataset." + DEF_NAME + ".url";
    private static String DEF_UUID_URL_PROP = "dashbuilder.dataset." + DEF_UUID + ".url";
    private ExternalDataSetProvider provider;
    private ExternalDataSetDef def;
    private String dataset2Url;
    private String dataset3Url;
    private Scheduler scheduler;

    @Before
    public void setup() {
        System.clearProperty(DEF_NAME_URL_PROP);
        System.clearProperty(DEF_UUID_URL_PROP);
        scheduler = DataSetCore.get().getScheduler();
        scheduler.init(10);

        provider = new ExternalDataSetProvider(ExternalDataSetCaller.get(),
                DataSetCore.get().getStaticDataSetProvider(),
                scheduler);
        var datasetUrl = this.getClass().getResource("/dataset.json").toExternalForm();
        dataset2Url = this.getClass().getResource("/dataset2.json").toExternalForm();
        dataset3Url = this.getClass().getResource("/dataset3.json").toExternalForm();
        def = (ExternalDataSetDef) DataSetDefFactory.newExternalDataSetDef().name(DEF_NAME).uuid(DEF_UUID).url(
                datasetUrl)
                .buildDef();
    }

    @After
    public void shutdown() {
        scheduler.unscheduleAll();
        scheduler.getRunningTasks().forEach(SchedulerTask::cancel);
    }

    @Test
    public void testFullLookup() throws Exception {
        var dataset = provider.lookupDataSet(def, null);
        dataSet1Check(dataset);
    }

    @Test
    public void testLookupWithFilter() throws Exception {
        var lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(def.getUUID())
                .filter("C2", FilterFactory.greaterThan(3.0))
                .buildLookup();
        var dataset = provider.lookupDataSet(def, lookup);
        assertEquals(2, dataset.getRowCount());
        assertEquals(4.0, dataset.getValueAt(0, 1));
        assertEquals(5.0, dataset.getValueAt(1, 1));
    }

    @Test
    public void testLookupWithSort() throws Exception {
        var lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(def.getUUID())
                .sort("C1", SortOrder.DESCENDING)
                .buildLookup();
        var dataset = provider.lookupDataSet(def, lookup);
        assertEquals(5, dataset.getRowCount());
        assertEquals("G3", dataset.getValueAt(0, 0));
        assertEquals("G2", dataset.getValueAt(1, 0));
        assertEquals("G2", dataset.getValueAt(2, 0));
        assertEquals("G1", dataset.getValueAt(3, 0));
        assertEquals("G1", dataset.getValueAt(4, 0));
    }

    @Test
    public void testLookupWithGroup() throws Exception {
        var lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(def.getUUID())
                .group("C1")
                .column("C1")
                .column("C2", AggregateFunctionType.MAX)
                .sort("C1", SortOrder.DESCENDING)
                .buildLookup();
        var dataset = provider.lookupDataSet(def, lookup);
        assertEquals(3, dataset.getRowCount());
        assertEquals("G3", dataset.getValueAt(0, 0));
        assertEquals("G2", dataset.getValueAt(1, 0));
        assertEquals("G1", dataset.getValueAt(2, 0));

        assertEquals(5.0, dataset.getValueAt(0, 1));
        assertEquals(4.0, dataset.getValueAt(1, 1));
        assertEquals(2.0, dataset.getValueAt(2, 1));
    }

    @Test
    public void testShouldNotKeepTestLookup() throws Exception {
        var lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(def.getUUID())
                .buildLookup();
        var previousUrl = def.getUrl();
        def.setUrl(dataset2Url);
        lookup.setTestMode(true);
        var dataset = provider.lookupDataSet(def, lookup);
        assertEquals(3, dataset.getRowCount());
        assertEquals("OTHER_G1", dataset.getValueAt(0, 0));
        assertEquals("OTHER_G2", dataset.getValueAt(1, 0));
        assertEquals("OTHER_G3", dataset.getValueAt(2, 0));

        assertEquals(1.0, dataset.getValueAt(0, 1));
        assertEquals(2.0, dataset.getValueAt(1, 1));
        assertEquals(3.0, dataset.getValueAt(2, 1));

        def.setUrl(previousUrl);
        lookup.setTestMode(false);
        dataset = provider.lookupDataSet(def, lookup);
        dataSet1Check(dataset);
    }

    @Test
    public void testCacheScheduler() throws Exception {
        provider.staticDataSetProvider.removeDataSet(def.getUUID());
        def.setCacheEnabled(true);
        def.setRefreshTime("1 second");

        provider.lookupDataSet(def, null);

        var taskKey = DataSetInvalidationTask.key(def);
        var task = scheduler.getTaskByKey(taskKey);
        var ds = provider.staticDataSetProvider.lookupDataSet(def, null);
        assertNotNull(task);
        assertNotNull(ds);

        Thread.sleep(1100);

        ds = provider.staticDataSetProvider.lookupDataSet(def, null);
        assertNull(ds);
    }

    @Test
    public void testNoCache() throws Exception {
        provider.lookupDataSet(def, null);

        var taskKey = DataSetInvalidationTask.key(def);
        var task = scheduler.getTaskByKey(taskKey);
        var ds = provider.staticDataSetProvider.lookupDataSet(def, null);
        assertNull(task);
        assertNull(ds);
    }

    @Test
    public void testUrlSetByUUIDProperty() throws Exception {
        System.setProperty(DEF_UUID_URL_PROP, dataset3Url);
        var ds = provider.lookupDataSet(def, null);
        
        assertEquals(1, ds.getRowCount());
        assertEquals(1, ds.getColumns().size());
    }
    
    @Test
    public void testUrlSetByNameProperty() throws Exception {
        System.setProperty(DEF_NAME_URL_PROP, dataset3Url);
        var ds = provider.lookupDataSet(def, null);
        
        assertEquals(1, ds.getRowCount());
        assertEquals(1, ds.getColumns().size());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNoUrlSetException() throws Exception {
        def.setUrl(null);
        provider.lookupDataSet(def, null);
    }

    private void dataSet1Check(DataSet dataset) {
        assertEquals(5, dataset.getRowCount());
        assertEquals("G1", dataset.getValueAt(0, 0));
        assertEquals("G1", dataset.getValueAt(1, 0));
        assertEquals("G2", dataset.getValueAt(2, 0));
        assertEquals("G2", dataset.getValueAt(3, 0));
        assertEquals("G3", dataset.getValueAt(4, 0));
    }

}
