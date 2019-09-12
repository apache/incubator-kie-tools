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

import com.google.gwt.dom.client.LIElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.events.DeleteColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.DuplicateInstanceEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.InsertColumnEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractColumnMenuPresenterTest extends AbstractMenuTest {

    @Before
    public void setup() {
        abstractColumnMenuPresenter = new AbstractColumnMenuPresenter() {

            @Override
            public void callSuperShow(int mx, int my) {
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
    public void showFalseFalse() {
        commonShow(4, 5, false, false);
    }

    @Test
    public void showFalseTrue() {
        commonShow(4, 5, false, true);
    }

    @Test
    public void showTrueFalse() {
        commonShow(4, 5, true, false);
    }

    @Test
    public void showTrueTrue() {
        commonShow(4, 5, true, true);
    }

    private void commonShow(int mx, int my, boolean asProperty, boolean showDuplicateInstance) {
        abstractColumnMenuPresenterSpy.initMenu();
        final LIElement duplicateInstanceLIElementOriginal = abstractColumnMenuPresenterSpy.duplicateInstanceLIElement;
        final LIElement deleteColumnInstanceLIElementOriginal = abstractColumnMenuPresenterSpy.deleteColumnInstanceLIElement;
        abstractColumnMenuPresenterSpy.show(mx, my, 1, "GIVEN", asProperty, showDuplicateInstance);
        if (!showDuplicateInstance) {
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).removeMenuItem(eq(duplicateInstanceLIElementOriginal));
            assertNull(abstractColumnMenuPresenterSpy.duplicateInstanceLIElement);
        }
        verify(abstractColumnMenuPresenterSpy, atLeastOnce()).mapEvent(eq(abstractColumnMenuPresenterSpy.insertColumnLeftLIElement), isA(InsertColumnEvent.class));
        verify(abstractColumnMenuPresenterSpy, atLeastOnce()).mapEvent(eq(abstractColumnMenuPresenterSpy.insertColumnRightLIElement), isA(InsertColumnEvent.class));
        if (asProperty) {
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).updateMenuItemAttributes(eq(abstractColumnMenuPresenterSpy.deleteColumnInstanceLIElement), eq(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_DELETE_COLUMN), eq(abstractColumnMenuPresenter.constants.deleteColumn()), eq("deleteColumn"));
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).mapEvent(eq(abstractColumnMenuPresenterSpy.deleteColumnInstanceLIElement), isA(DeleteColumnEvent.class));
        } else {
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).updateMenuItemAttributes(eq(deleteColumnInstanceLIElementOriginal), eq(abstractColumnMenuPresenter.COLUMNCONTEXTMENU_DELETE_INSTANCE), eq(abstractColumnMenuPresenter.constants.deleteInstance()), eq("deleteInstance"));
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).mapEvent(eq(abstractColumnMenuPresenterSpy.deleteColumnInstanceLIElement), isA(DeleteColumnEvent.class));
        }
        if (abstractColumnMenuPresenter.duplicateInstanceLIElement != null) {
            verify(abstractColumnMenuPresenterSpy, atLeastOnce()).mapEvent(eq(abstractColumnMenuPresenterSpy.duplicateInstanceLIElement), isA(DuplicateInstanceEvent.class));
        }
        verify(abstractColumnMenuPresenterSpy, atLeastOnce()).callSuperShow(eq(mx), eq(my));
        reset(abstractColumnMenuPresenterSpy);
    }
}