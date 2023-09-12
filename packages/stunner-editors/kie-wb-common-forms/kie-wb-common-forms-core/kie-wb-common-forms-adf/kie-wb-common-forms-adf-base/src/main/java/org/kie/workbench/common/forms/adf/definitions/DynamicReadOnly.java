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


package org.kie.workbench.common.forms.adf.definitions;

/**
 * Allows a property to be dynamically set as readonly or not.
 */
public interface DynamicReadOnly {

    default ReadOnly getReadOnly(final String fieldName) {
        return ReadOnly.NOT_SET;
    }

    void setAllowOnlyVisualChange(final boolean allowOnlyVisualChange);

    boolean isAllowOnlyVisualChange();

    enum ReadOnly {
        /**
         * isAllowOnlyVisualChange() is false.
         */
        NOT_SET,

        /**
         * isAllowOnlyVisualChange() is true and the property is readonly.
         */
        TRUE,

        /**
         * isAllowOnlyVisualChange() is true and the property is NOT readonly.
         */
        FALSE
    }
}