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


package org.gwtbootstrap3.client.ui.constants;

import org.gwtbootstrap3.client.ui.base.helper.EnumHelper;
import org.gwtproject.dom.client.Style;

/**
 * @author Joshua Godi
 */
public enum Alignment implements Style.HasCssName {
    DEFAULT(""),
    LEFT("text-left"),
    CENTER("text-center"),
    RIGHT("text-right");

    private final String cssClass;

    private Alignment(final String cssClass) {
        this.cssClass = cssClass;
    }

    @Override
    public String getCssName() {
        return cssClass;
    }

    public static Alignment fromStyleName(final String styleName) {
        return EnumHelper.fromStyleName(styleName, Alignment.class, DEFAULT);
    }
}
