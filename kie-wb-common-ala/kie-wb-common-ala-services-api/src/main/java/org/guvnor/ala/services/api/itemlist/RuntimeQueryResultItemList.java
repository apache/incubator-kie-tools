/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.services.api.itemlist;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.guvnor.ala.services.api.ItemList;
import org.guvnor.ala.services.api.RuntimeQueryResultItem;

public class RuntimeQueryResultItemList
        implements ItemList<RuntimeQueryResultItem> {

    private RuntimeQueryResultItem[] resultItems;

    /**
     * No-args constructor for enabling marshalling to work, please do not remove.
     */
    public RuntimeQueryResultItemList() {
    }

    public RuntimeQueryResultItemList(List<RuntimeQueryResultItem> resultItems) {
        this.resultItems = resultItems.toArray(new RuntimeQueryResultItem[resultItems.size()]);
    }

    public RuntimeQueryResultItemList(RuntimeQueryResultItem[] resultItems) {
        this.resultItems = resultItems;
    }

    public RuntimeQueryResultItem[] getResultItems() {
        return resultItems;
    }

    public void setResultItems(RuntimeQueryResultItem[] resultItems) {
        this.resultItems = resultItems;
    }

    @Override
    @JsonIgnore
    public List<RuntimeQueryResultItem> getItems() {
        if (resultItems == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(resultItems);
    }
}