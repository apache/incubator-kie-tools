/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.menu;

import com.google.gwt.dom.client.LIElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.Event;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenuTest;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoGridContextMenuTest extends GridContextMenuTest {

    @Before
    public void setup() {
        gridContextMenuSpy = spy(new KogitoGridContextMenu() {

            {
                this.insertRowAboveLIElement = insertRowAboveLIElementMock;
                this.insertRowBelowLIElement= insertRowBelowLIElementMock;
                this.duplicateRowLIElement = duplicateRowLIElementMock;
                this.deleteRowLIElement = deleteRowLIElementMock;
                this.gridTitleElement = gridTitleElementMock;
            }

            @Override
            public LIElement addExecutableMenuItem(String id, String label, String i18n) {
                return createdElementMock;
            }

            @Override
            public void mapEvent(LIElement executableMenuItem, Event toBeMapped) {
                //Do nothing
            }

            @Override
            protected void updateExecutableMenuItemAttributes(LIElement toUpdate, String id, String label, String i18n) {
                //Do nothing
            }

            @Override
            public LIElement addMenuItem(String id, String label, String i18n) {
                return createdElementMock;
            }

            @Override
            public void removeMenuItem(LIElement toRemove) {
                //Do nothing
            }

            @Override
            public void show(GridWidget gridWidget, int mx, int my) {
                //Do nothing
            }
        });
    }

}
