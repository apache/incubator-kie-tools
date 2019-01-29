/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.adf.processors.models;

import java.util.List;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;

@FormDefinition(startElement = "name")
public class Parent extends AbstractPerson {

    @FormField(afterElement = "birthDay")
    private Children favouriteSon;

    @FormField(afterElement = "favouriteSon")
    private List<Children> allChildren;

    public Children getFavouriteSon() {
        return favouriteSon;
    }

    public void setFavouriteSon(Children favouriteSon) {
        this.favouriteSon = favouriteSon;
    }

    public List<Children> getAllChildren() {
        return allChildren;
    }

    public void setAllChildren(List<Children> allChildren) {
        this.allChildren = allChildren;
    }
}
