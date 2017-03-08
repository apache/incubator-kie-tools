/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.plugin.client.editor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.uberfire.ext.plugin.model.DynamicMenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DynamicMenuUpdateIndexTest {

    private DynamicMenuEditorPresenter presenter;
    private DynamicMenuItem firstMenuItem;
    private DynamicMenuItem secondMenuItem;

    @Test
    public void validateItemIndexes() {
        presenter = createDynamicMenuEditorPresenter(mock(DynamicMenuEditorPresenter.View.class));

        firstMenuItem = new DynamicMenuItem("firstId",
                                            "firstLabel");
        secondMenuItem = new DynamicMenuItem("secondId",
                                             "secondLabel");

        presenter.addMenuItem(firstMenuItem);
        presenter.addMenuItem(secondMenuItem);

        presenter.updateIndex(firstMenuItem,
                              0,
                              DynamicMenuEditorPresenter.UpdateIndexOperation.UP);
        checkMenuItemIndexes(0,
                             1);

        presenter.updateIndex(secondMenuItem,
                              1,
                              DynamicMenuEditorPresenter.UpdateIndexOperation.DOWN);
        checkMenuItemIndexes(0,
                             1);

        presenter.updateIndex(firstMenuItem,
                              0,
                              DynamicMenuEditorPresenter.UpdateIndexOperation.DOWN);
        checkMenuItemIndexes(1,
                             0);

        presenter.updateIndex(firstMenuItem,
                              1,
                              DynamicMenuEditorPresenter.UpdateIndexOperation.UP);
        checkMenuItemIndexes(0,
                             1);
    }

    private DynamicMenuEditorPresenter createDynamicMenuEditorPresenter(DynamicMenuEditorPresenter.View view) {

        return new DynamicMenuEditorPresenter(view) {

            private List<DynamicMenuItem> dynamicMenuItems = new ArrayList<DynamicMenuItem>();

            @Override
            public List<DynamicMenuItem> getDynamicMenuItems() {
                return dynamicMenuItems;
            }

            @Override
            public void addMenuItem(final DynamicMenuItem menuItem) {
                dynamicMenuItems.add(menuItem);
            }
        };
    }

    private void checkMenuItemIndexes(int firstMenuItemIndex,
                                      int secondMenuItemIndex) {
        assertEquals(firstMenuItemIndex,
                     presenter.getDynamicMenuItems().indexOf(firstMenuItem));
        assertEquals(secondMenuItemIndex,
                     presenter.getDynamicMenuItems().indexOf(secondMenuItem));
    }
}
