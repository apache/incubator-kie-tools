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


package org.uberfire.ext.widgets.common.client.select;

public class SelectOptionImpl implements SelectOption {

    private final String selector;
    private final String name;

    public SelectOptionImpl(String selector,
                            String name) {

        this.selector = checkNotEmpty("selector",
                                      selector).toUpperCase();
        this.name = name;
    }

    private static String checkNotEmpty(String name, String parameter) {
        if (parameter == null || parameter.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter named '" + name + "' should be filled!");
        }
        return parameter;
    }

    public String getSelector() {
        return selector;
    }

    public String getName() {
        if (this.name == null || this.name.isEmpty()) {
            return this.selector;
        } else {
            return this.name;
        }
    }
}
