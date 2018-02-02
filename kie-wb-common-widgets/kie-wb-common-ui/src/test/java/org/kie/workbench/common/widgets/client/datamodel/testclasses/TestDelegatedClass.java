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

import java.util.List;

/**
 * Test class to check data-types are extracted correctly by ModuleDataModelOracleBuilder for subclasses and delegated classes
 */
public class TestDelegatedClass {

    private TestSuperClass superClass;

    public TestDelegatedClass(final TestSuperClass superClass) {
        this.superClass = superClass;
    }

    public String getField1() {
        return superClass.getField1();
    }

    public void setField1(final String field1) {
        this.superClass.setField1(field1);
    }

    public List<String> getList() {
        return this.superClass.getList();
    }
}
