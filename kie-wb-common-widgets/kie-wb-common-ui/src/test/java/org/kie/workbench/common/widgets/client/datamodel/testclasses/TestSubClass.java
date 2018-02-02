/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.datamodel.testclasses;

/**
 * Test class to check data-types are extracted correctly by ModuleDataModelOracleBuilder for subclasses and delegated classes
 */
public class TestSubClass extends TestSuperClass {

    private String field2;

    public String getField2() {
        return field2;
    }

    public void setField2(final String field2) {
        this.field2 = field2;
    }
}
