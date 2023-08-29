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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverImpl;

@Dependent
public class ListSelector extends AbstractPopoverImpl<ListSelectorView, HasListSelectorControl> implements ListSelectorView.Presenter {

    public ListSelector() {
        //CDI proxy
    }

    @Inject
    public ListSelector(final ListSelectorView view) {
        super(view);
        this.view.init(this);
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        binding.ifPresent(b -> b.onItemSelected(item));
    }

    @Override
    public void bind(final HasListSelectorControl bound,
                     final int uiRowIndex,
                     final int uiColumnIndex) {
        super.bind(bound, uiRowIndex, uiColumnIndex);

        binding.ifPresent(b -> {
            final List<ListSelectorItem> items = b.getItems(uiRowIndex,
                                                            uiColumnIndex);
            if (items.isEmpty()) {
                //If there are no items to display unbind to prevent empty popups being shown
                bind(null,
                     uiRowIndex,
                     uiColumnIndex);
            } else {
                view.setItems(b.getItems(uiRowIndex,
                                         uiColumnIndex));
            }
        });
    }
}
