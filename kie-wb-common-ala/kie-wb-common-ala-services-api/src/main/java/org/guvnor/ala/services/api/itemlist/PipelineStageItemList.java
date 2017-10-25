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
import org.guvnor.ala.services.api.PipelineStageItem;

public class PipelineStageItemList
        implements ItemList<PipelineStageItem> {

    private PipelineStageItem[] stageItems;

    /**
     * No-args constructor for enabling marshalling to work, please do not remove.
     */
    public PipelineStageItemList() {
    }

    public PipelineStageItemList(List<PipelineStageItem> stageItems) {
        this.stageItems = stageItems.toArray(new PipelineStageItem[stageItems.size()]);
    }

    public PipelineStageItemList(PipelineStageItem[] stageItems) {
        this.stageItems = stageItems;
    }

    public PipelineStageItem[] getStageItems() {
        return stageItems;
    }

    public void setStageItems(PipelineStageItem[] stageItems) {
        this.stageItems = stageItems;
    }

    @Override
    @JsonIgnore
    public List<PipelineStageItem> getItems() {
        if (stageItems == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(stageItems);
    }
}