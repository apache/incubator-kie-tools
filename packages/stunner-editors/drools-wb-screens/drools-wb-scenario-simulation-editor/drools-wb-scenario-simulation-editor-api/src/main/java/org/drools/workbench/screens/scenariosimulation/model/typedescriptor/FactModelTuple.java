/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.model.typedescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Class used to recursively represent a given fact with its ModelFields eventually expanded
 */
@Portable
public class FactModelTuple {

    private SortedMap<String, FactModelTree> visibleFacts;
    private SortedMap<String, FactModelTree> hiddenFacts;
    private List<String> multipleNestedCollectionError = new ArrayList<>();
    private List<String> multipleNestedObjectError = new ArrayList<>();

    public FactModelTuple() {
        // CDI
    }

    public FactModelTuple(SortedMap<String, FactModelTree> visibleFacts, SortedMap<String, FactModelTree> hiddenFacts) {
        this.visibleFacts = visibleFacts;
        this.hiddenFacts = hiddenFacts;
    }

    public SortedMap<String, FactModelTree> getVisibleFacts() {
        return visibleFacts;
    }

    public SortedMap<String, FactModelTree> getHiddenFacts() {
        return hiddenFacts;
    }

    public void addMultipleNestedCollectionError(String error) {
        this.multipleNestedCollectionError.add(error);
    }

    public void addMultipleNestedObjectError(String error) {
        this.multipleNestedObjectError.add(error);
    }

    public List<String> getMultipleNestedCollectionError() {
        return multipleNestedCollectionError;
    }

    public List<String> getMultipleNestedObjectError() {
        return multipleNestedObjectError;
    }
}
