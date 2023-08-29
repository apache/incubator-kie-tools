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

package org.dashbuilder.dataset.json;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataprovider.DefaultProviderType;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.ExternalDataSetDef;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataSetDefJsonTest {

    private static final String UTF_8 = "UTF-8";
    private static final String EXTERNAL_DEF_PATH = "externalDataSetDef.dset";
    private static final String CUSTOM_DEF_PATH = "customDataSetDef.dset";

    private static final DataSetProviderType CUSTOM_PROVIDER_TYPE = new DefaultProviderType("CUSTOM");

    DataSetDefJSONMarshaller jsonMarshaller;
    
    @Before
    public void init() {
        jsonMarshaller = new DataSetDefJSONMarshaller(CUSTOM_PROVIDER_TYPE);
    }

    @Test
    public void testExternal() throws Exception {
        jsonMarshaller = new DataSetDefJSONMarshaller(DataSetProviderType.EXTERNAL);
        var json = getFileAsString(EXTERNAL_DEF_PATH);
        var def = (ExternalDataSetDef) jsonMarshaller.fromJson(json);
        assertEquals("http://datasets.com/dataset", def.getUrl());
        assertEquals(true, def.isDynamic());
        assertEquals("v1", def.getHeaders().get("h1"));
    }

    @Test
    public void testCustom() throws Exception {
        final DataSetDef dataSetDef = new DataSetDef();
        dataSetDef.setName("custom data set name");
        dataSetDef.setUUID("custom-test-uuid");
        dataSetDef.setProvider(CUSTOM_PROVIDER_TYPE);
        dataSetDef.setCacheEnabled(false);
        dataSetDef.setCacheMaxRows(100);
        dataSetDef.setPublic(true);
        dataSetDef.setPushEnabled(false);
        dataSetDef.setPushMaxSize(10);
        dataSetDef.setRefreshAlways(false);
        dataSetDef.setRefreshTime("1second");
        dataSetDef.setProperty("prop1", "Hello");

        String json = jsonMarshaller.toJsonString(dataSetDef);
        String customJSONContent = getFileAsString(CUSTOM_DEF_PATH);
        assertDataSetDef(json, customJSONContent);

        DataSetDef fromJson = jsonMarshaller.fromJson(customJSONContent);
        assertEquals(dataSetDef, fromJson);
    }

    private void assertDataSetDef(final String def1, final String def2) throws Exception {
        if (def1 == null && def2 != null)
            Assert.assertTrue("JSON string for Def1 is null and for Def2 is not null", false);
        if (def1 != null && def2 == null)
            Assert.assertTrue("JSON string for Def1 is not null and for Def2 is null", false);
        if (def1 == null)
            Assert.assertTrue("JSON string for both definitions is null", false);

        DataSetDef def1Object = jsonMarshaller.fromJson(def1);
        DataSetDef def2Object = jsonMarshaller.fromJson(def2);

        Assert.assertEquals(def1Object, def2Object);
    }

    protected static String getFileAsString(String file) throws Exception {
        InputStream mappingsFileUrl = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        StringWriter writer = null;
        String fileContent = null;

        try {
            writer = new StringWriter();
            IOUtils.copy(mappingsFileUrl, writer, UTF_8);
            fileContent = writer.toString();
        } finally {
            if (writer != null)
                writer.close();
        }

        // Ensure newline characters meet the HTTP specification formatting requirements.
        return fileContent.replaceAll("\n", "\r\n");
    }
}
