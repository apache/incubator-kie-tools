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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import java.util.Collections;
import java.util.List;

import org.uberfire.mvp.Command;

public interface HasListSelectorControl {

    interface ListSelectorItem {

    }

    interface ListSelectorTextItem extends ListSelectorItem {

        String getText();

        boolean isEnabled();

        Command getCommand();

        static ListSelectorTextItem build(final String text,
                                          final boolean isEnabled,
                                          final Command command) {
            return new ListSelectorTextItem() {
                @Override
                public String getText() {
                    return text;
                }

                @Override
                public boolean isEnabled() {
                    return isEnabled;
                }

                @Override
                public Command getCommand() {
                    return command;
                }
            };
        }
    }

    interface ListSelectorHeaderItem extends ListSelectorItem {

        String getText();

        static ListSelectorHeaderItem build(final String text) {
            return new ListSelectorHeaderItem() {
                @Override
                public String getText() {
                    return text;
                }
            };
        }
    }

    class ListSelectorDividerItem implements ListSelectorItem {

    }

    default List<ListSelectorItem> getItems(final int uiRowIndex,
                                            final int uiColumnIndex) {
        return Collections.emptyList();
    }

    default void onItemSelected(final ListSelectorItem item) {
        //NOP by default
    }
}
