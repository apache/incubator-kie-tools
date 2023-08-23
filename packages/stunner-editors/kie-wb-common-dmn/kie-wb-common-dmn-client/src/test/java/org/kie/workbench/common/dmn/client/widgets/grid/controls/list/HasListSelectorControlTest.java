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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorHeaderItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorTextItem;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class HasListSelectorControlTest {

    private static final String TEXT = "text";

    private static final boolean ENABLED = true;

    @Mock
    private Command command;

    @Test
    public void testSelectorTextItemBuild() {
        final ListSelectorTextItem item = ListSelectorTextItem.build(TEXT, ENABLED, command);
        assertThat(item.getText()).isEqualTo(TEXT);
        assertThat(item.isEnabled()).isEqualTo(ENABLED);
        assertThat(item.getCommand()).isEqualTo(command);
    }

    @Test
    public void testSelectorHeaderItemBuild() {
        final ListSelectorHeaderItem item = ListSelectorHeaderItem.build(TEXT);
        assertThat(item.getText()).isEqualTo(TEXT);
        assertThat(item.getIconClass()).isEqualTo(EMPTY);
    }
}
