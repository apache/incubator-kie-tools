/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.driver.testclasses;

/**
 * Helper class for testing the loading of external dependencies information provided by the DMO into a data model.
 */
public class ExternalPojo2 {

    private String field3;

    private String field4;

    public ExternalPojo2() {
    }

    public String getField3() {
        return field3;
    }

    public void setField3( String field3 ) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4( String field4 ) {
        this.field4 = field4;
    }
}