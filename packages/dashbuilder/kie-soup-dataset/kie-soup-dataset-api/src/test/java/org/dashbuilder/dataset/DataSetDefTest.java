/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.dashbuilder.dataset;

import org.dashbuilder.dataset.def.BeanDataSetDef;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataSetDefTest {

    BeanDataSetDef beanDef1 = (BeanDataSetDef) DataSetDefFactory.newBeanDataSetDef()
            .uuid("uuid")
            .name("bean")
            .refreshOn("100s", true)
            .pushOn(100)
            .cacheOn(100)
            .generatorClass("class1")
            .generatorParam("p1", "v1")
            .buildDef();

    BeanDataSetDef beanDef2 = (BeanDataSetDef) DataSetDefFactory.newBeanDataSetDef()
            .uuid("uuid")
            .name("bean")
            .refreshOn("100s", true)
            .pushOn(100)
            .cacheOn(100)
            .generatorClass("class1")
            .generatorParam("p1", "v1")
            .buildDef();

    CSVDataSetDef csvDef1 = (CSVDataSetDef) DataSetDefFactory.newCSVDataSetDef()
            .uuid("expenseReports")
            .name("bean")
            .filePath("expenseReports.csv")
            .refreshOn("2s", false)
            .pushOn(1024)
            .cacheOn(100)
            .separatorChar(';')
            .quoteChar('"')
            .escapeChar('\\')
            .numberPattern("#,###.##")
            .datePattern("MM-dd-yyyy")
            .buildDef();

    @Test
    public void testEquals() throws Exception {
        assertTrue(beanDef1.equals(beanDef2));
    }

    @Test
    public void testBeanHashCode() throws Exception {
        assertEquals(beanDef1.hashCode(), beanDef2.hashCode());
        beanDef1.getParamaterMap().put("p1", "v2");
        assertNotEquals(beanDef1.hashCode(), beanDef2.hashCode());
    }

    @Test
    public void testCsvHashCode() throws Exception {
        System.out.println(csvDef1.hashCode());
        System.out.println(csvDef1.toString());
        assertEquals(csvDef1.hashCode(), csvDef1.clone().hashCode());
    }
}
