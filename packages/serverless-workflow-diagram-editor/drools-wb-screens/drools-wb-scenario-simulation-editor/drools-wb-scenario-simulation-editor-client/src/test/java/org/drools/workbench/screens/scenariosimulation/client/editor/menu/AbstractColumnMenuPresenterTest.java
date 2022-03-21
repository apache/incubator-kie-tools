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

package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import java.util.Objects;

import com.google.gwt.dom.client.LIElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateInstanceEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractColumnMenuPresenterTest extends AbstractMenuTest {

    @Before
    public void setup() {
        abstractColumnMenuPresenter = new AbstractColumnMenuPresenter() {

            @Override
            protected void updateExecutableMenuItemAttributes(LIElement toUpdate, String id, String label, String i18n) {
                //Do nothing
            }

            @Override
            public void show(GridWidget gridWidget, int mx, int my) {
                //Do nothing
            }
        };
        super.setup();
    }

    @Test
    public void initMenu() {
        assertNull(abstractColumnMenuPresenter.columnContextLIElement);
        assertNull(abstractColumnMenuPresenter.insertColumnLeftLIElement);
        assertNull(abstractColumnMenuPresenter.insertColumnRightLIElement);
        assertNull(abstractColumnMenuPresenter.deleteColumnInstanceLIElement);
        assertNull(abstractColumnMenuPresenter.duplicateInstanceLIElement);
        abstractColumnMenuPresenter.initMenu();
        assertNotNull(abstractColumnMenuPresenter.columnContextLIElement);
        assertNotNull(abstractColumnMenuPresenter.insertColumnLeftLIElement);
        assertNotNull(abstractColumnMenuPresenter.insertColumnRightLIElement);
        assertNotNull(abstractColumnMenuPresenter.deleteColumnInstanceLIElement);
        assertNotNull(abstractColumnMenuPresenter.duplicateInstanceLIElement);
    }

    @Test
    public void showFalseFalseSimulation() {
        commonShow(GridWidget.SIMULATION, 4, 5, false, false, false);
    }

    @Test
    public void showFalseTrueSimulation() {
        commonShow(GridWidget.SIMULATION, 4, 5, false, true, false);
    }

    @Test
    public void showTrueFalseSimulation() {
        commonShow(GridWidget.SIMULATION, 4, 5, true, false, false);
    }

    @Test
    public void showTrueTrueSimulation() {
        commonShow(GridWidget.SIMULATION, 4, 5, true, true, false);
    }

    @Test
    public void showTrueTrueSimulation_NullDuplicateElement() {
        commonShow(GridWidget.SIMULATION, 4, 5, true, true, true);
    }

    @Test
    public void showFalseFalseBackground() {
        commonShow(GridWidget.BACKGROUND, 4, 5, false, false, false);
    }

    @Test
    public void showFalseTrueBackground() {
        commonShow(GridWidget.BACKGROUND, 4, 5, false, true, false);
    }

    @Test
    public void showTrueFalseBackground() {
        commonShow(GridWidget.BACKGROUND, 4, 5, true, false, false);
    }

    @Test
    public void showTrueTrueBackground_NotNullDuplicateInstanceLIElement() {
        commonShow(GridWidget.BACKGROUND, 4, 5, true, true, false);
    }

    @Test
    public void showTrueTrueBackground_NullDuplicateInstanceLIElement() {
        commonShow(GridWidget.BACKGROUND, 4, 5, true, true, true);
    }

    private void commonShow(GridWidget gridWidget, int mx, int my, boolean asProperty, boolean showDuplicateInstance, boolean nullDuplicateElement) {
        abstractColumnMenuPresenterSpy.initMenu();
        if (nullDuplicateElement) {
            abstractColumnMenuPresenterSpy.duplicateInstanceLIElement = null;
        }
        final LIElement duplicateInstanceLIElementOriginal = abstractColumnMenuPresenterSpy.duplicateInstanceLIElement;
        final LIElement deleteColumnInstanceLIElementOriginal = abstractColumnMenuPresenterSpy.deleteColumnInstanceLIElement;
        abstractColumnMenuPresenterSpy.show(gridWidget, mx, my, 1, "GIVEN", asProperty, showDuplicateInstance);
        if (Objects.equals(GridWidget.BACKGROUND, gridWidget)) {
            verify(abstractColumnMenuPresenterSpy, times(1)).updateMenuItemAttributes(eq(gridTitleElementMock), any(), eq(ScenarioSimulationEditorConstants.INSTANCE.background()), eq("background"));
        } else {
            verify(abstractColumnMenuPresenterSpy, times(1)).updateMenuItemAttributes(eq(gridTitleElementMock), any(), eq(ScenarioSimulationEditorConstants.INSTANCE.scenario()), eq("scenario"));
        }
        if (!(Objects.equals(GridWidget.SIMULATION, gridWidget) && showDuplicateInstance)) {
            if (!nullDuplicateElement) {
               verify(abstractColumnMenuPresenterSpy, times(1)).removeMenuItem(eq(duplicateInstanceLIElementOriginal));
            }
            assertNull(abstractColumnMenuPresenterSpy.duplicateInstanceLIElement);
        } else if (nullDuplicateElement) {
            verify(abstractColumnMenuPresenterSpy, times(1)).addExecutableMenuItemAfter(eq(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_DUPLICATE_INSTANCE), eq(abstractColumnMenuPresenter.constants.duplicateInstance()), eq("duplicateInstance"), eq(deleteColumnInstanceLIElementOriginal));
        }
        verify(abstractColumnMenuPresenterSpy, atLeastOnce()).mapEvent(eq(abstractColumnMenuPresenterSpy.insertColumnLeftLIElement), isA(InsertColumnEvent.class));
        verify(abstractColumnMenuPresenterSpy, atLeastOnce()).mapEvent(eq(abstractColumnMenuPresenterSpy.insertColumnRightLIElement), isA(InsertColumnEvent.class));
        if (asProperty) {
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).updateExecutableMenuItemAttributes(eq(abstractColumnMenuPresenterSpy.deleteColumnInstanceLIElement), eq(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_DELETE_COLUMN), eq(abstractColumnMenuPresenter.constants.deleteColumn()), eq("deleteColumn"));
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).mapEvent(eq(abstractColumnMenuPresenterSpy.deleteColumnInstanceLIElement), isA(DeleteColumnEvent.class));
        } else {
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).updateExecutableMenuItemAttributes(eq(deleteColumnInstanceLIElementOriginal), eq(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_DELETE_INSTANCE), eq(abstractColumnMenuPresenter.constants.deleteInstance()), eq("deleteInstance"));
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).mapEvent(eq(abstractColumnMenuPresenterSpy.deleteColumnInstanceLIElement), isA(DeleteColumnEvent.class));
        }
        if (abstractColumnMenuPresenter.duplicateInstanceLIElement != null) {
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).mapEvent(eq(abstractColumnMenuPresenterSpy.duplicateInstanceLIElement), isA(DuplicateInstanceEvent.class));
        }
        verify(abstractColumnMenuPresenterSpy, atLeastOnce()).show(eq(gridWidget), eq(mx), eq(my));
        reset(abstractColumnMenuPresenterSpy);
    }
}