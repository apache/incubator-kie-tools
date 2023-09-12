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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.function.Consumer;

import org.uberfire.mvp.Command;

public class DecisionNavigatorItemBuilder {

    private DecisionNavigatorItem navigatorItem;

    public DecisionNavigatorItemBuilder() {
        this.navigatorItem = new DecisionNavigatorItem();
    }

    public DecisionNavigatorItemBuilder withUUID(final String uuid) {
        navigatorItem.setUUID(uuid);
        return this;
    }

    public DecisionNavigatorItemBuilder withLabel(final String label) {
        navigatorItem.setLabel(label);
        return this;
    }

    public DecisionNavigatorItemBuilder withParentUUID(final String parentUUID) {
        navigatorItem.setParentUUID(parentUUID);
        return this;
    }

    public DecisionNavigatorItemBuilder withType(final DecisionNavigatorItem.Type type) {
        navigatorItem.setType(type);
        return this;
    }

    public DecisionNavigatorItemBuilder withOnClick(final Command onClick) {
        navigatorItem.setOnClick(onClick);
        return this;
    }

    public DecisionNavigatorItemBuilder withOnUpdate(final Consumer<DecisionNavigatorItem> onUpdate) {
        navigatorItem.setOnUpdate(onUpdate);
        return this;
    }

    public DecisionNavigatorItemBuilder withOnRemove(final Consumer<DecisionNavigatorItem> onRemove) {
        navigatorItem.setOnRemove(onRemove);
        return this;
    }

    public DecisionNavigatorItemBuilder withIsDRG(final boolean isDRG) {
        navigatorItem.setIsDRG(isDRG);
        return this;
    }

    public DecisionNavigatorItem build() {
        return navigatorItem;
    }
}
