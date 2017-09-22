/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.adf.engine.backend.formGeneration.util;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BackendPropertyValueExtractorTest {
    private static final String STRING_PROPERTY = "Cordon Bleu";
    private static final int INT_PROPERTY = 109;
    private static final boolean BOOL_PROPERTY = true;

    private BackendPropertyValueExtractor extractor;
    private MyTestBean bean;

    @Before
    public void setUp() throws Exception {
        extractor = new BackendPropertyValueExtractor();
        bean = new MyTestBean();
    }

    @Test
    public void testWithMyTestBean() {
        bean.setStringProperty(STRING_PROPERTY);
        bean.setIntProperty(INT_PROPERTY);
        bean.setBoolProperty(BOOL_PROPERTY);

        assertEquals(INT_PROPERTY, extractor.readValue(bean, "intProperty"));
        assertEquals(STRING_PROPERTY, extractor.readValue(bean, "stringProperty"));
        assertEquals(BOOL_PROPERTY, extractor.readValue(bean, "boolProperty"));
    }

    @Test
    public void testWithWrongProperty() {
        assertNull(extractor.readValue(bean, "nonExistingProperty"));
    }

    @Test
    public void testWithEmptyModel() {
        assertNull(extractor.readValue(null, null));
    }

    public class MyTestBean implements Serializable {

        private String stringProperty;
        public int intProperty;
        private boolean boolProperty;

        public MyTestBean() {
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public int getIntProperty() {
            return intProperty;
        }

        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }

        public boolean getBoolProperty() {
            return boolProperty;
        }

        public void setBoolProperty(boolean boolProperty) {
            this.boolProperty = boolProperty;
        }
    }

}
