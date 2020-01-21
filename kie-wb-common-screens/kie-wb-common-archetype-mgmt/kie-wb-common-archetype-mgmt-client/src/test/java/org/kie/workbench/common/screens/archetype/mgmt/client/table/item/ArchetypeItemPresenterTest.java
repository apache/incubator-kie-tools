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

package org.kie.workbench.common.screens.archetype.mgmt.client.table.item;

import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.item.model.ArchetypeItem;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.AbstractArchetypeTablePresenter;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeStatus;
import org.kie.workbench.common.screens.archetype.mgmt.shared.services.ArchetypeService;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ArchetypeItemPresenterTest {

    private ArchetypeItemPresenter presenter;

    @Mock
    private ArchetypeItemPresenter.View view;

    @Mock
    private TranslationService ts;

    @Mock
    private ArchetypeService archetypeService;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private AbstractArchetypeTablePresenter tablePresenter;

    @Before
    public void setup() {
        presenter = spy(new ArchetypeItemPresenter(view,
                                                   ts,
                                                   new CallerMock<>(archetypeService),
                                                   busyIndicatorView));
    }

    @Test
    public void setupWhenAllEnabledTest() {
        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.VALID);
        final ArchetypeItem archetypeItem = new ArchetypeItem(archetype,
                                                              true,
                                                              true);

        doReturn(true).when(tablePresenter).isShowIncludeColumn();
        doReturn(true).when(tablePresenter).isShowStatusColumn();
        doReturn(true).when(tablePresenter).isShowDeleteAction();
        doReturn(true).when(tablePresenter).isShowValidateAction();

        presenter.setup(archetypeItem,
                        tablePresenter);

        verify(view).init(presenter);
        verify(view).setIncluded(archetypeItem.isSelected());
        verify(view).setGroupId(archetype.getGav().getGroupId());
        verify(view).setArtifactId(archetype.getGav().getArtifactId());
        verify(view).setVersion(archetype.getGav().getVersion());
        verify(view).setCreatedDate(ArchetypeItemPresenter.DATE_FORMAT.format(archetype.getCreatedDate()));
        verify(view).showInclude(true);
        verify(view).showStatus(true);
        verify(view).showDeleteAction(true);
        verify(view).showValidateAction(true);
        verify(view).showDefaultBadge(true);
        verify(view).enableIncludeCheckbox(true);
        verify(view).enableSetDefault(true);
        verify(view).setDeleteCommand(any());
        verify(view).setDefaultBadgeTooltip(anyString());
    }

    @Test
    public void setupWhenInvalidStatusTest() {
        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.INVALID);
        final ArchetypeItem archetypeItem = new ArchetypeItem(archetype,
                                                              true,
                                                              true);

        presenter.setup(archetypeItem,
                        tablePresenter);

        verify(view).enableIncludeCheckbox(false);
        verify(view).enableSetDefault(false);

        verify(view).showValidStatus(false);
        verify(view).showInvalidStatus(true);
        verify(view).setInvalidTooltip(anyString());
        verify(view, never()).setValidTooltip(anyString());
    }

    @Test
    public void setupWhenValidStatusTest() {
        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).enableIncludeCheckbox(true);
        verify(view).enableSetDefault(true);

        verify(view).showValidStatus(true);
        verify(view).showInvalidStatus(false);
        verify(view, never()).setInvalidTooltip(anyString());
        verify(view).setValidTooltip(anyString());
    }

    @Test
    public void setupWhenIsIncludedTest() {
        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).setIncluded(true);
    }

    @Test
    public void setupWhenIsNotIncludedTest() {
        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.VALID);
        final ArchetypeItem archetypeItem = new ArchetypeItem(archetype,
                                                              false,
                                                              true);

        presenter.setup(archetypeItem,
                        tablePresenter);

        verify(view).setIncluded(false);
    }

    @Test
    public void setupWhenShowIncludeTest() {
        doReturn(true).when(tablePresenter).isShowIncludeColumn();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).showInclude(true);
    }

    @Test
    public void setupWhenHideIncludeTest() {
        doReturn(false).when(tablePresenter).isShowIncludeColumn();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).showInclude(false);
    }

    @Test
    public void setupWhenShowStatusColumnTest() {
        doReturn(true).when(tablePresenter).isShowStatusColumn();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).showStatus(true);
    }

    @Test
    public void setupWhenHideStatusColumnTest() {
        doReturn(false).when(tablePresenter).isShowStatusColumn();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).showStatus(false);
    }

    @Test
    public void setupWhenShowDeleteActionTest() {
        doReturn(true).when(tablePresenter).isShowDeleteAction();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).showDeleteAction(true);
        verify(view).setDeleteCommand(any());
    }

    @Test
    public void setupWhenHideDeleteActionTest() {
        doReturn(false).when(tablePresenter).isShowDeleteAction();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).showDeleteAction(false);
        verify(view, never()).setDeleteCommand(any());
    }

    @Test
    public void setupWhenShowValidateActionTest() {
        doReturn(true).when(tablePresenter).isShowValidateAction();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).showValidateAction(true);
    }

    @Test
    public void setupWhenHideValidateActionTest() {
        doReturn(false).when(tablePresenter).isShowValidateAction();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).showValidateAction(false);
    }

    @Test
    public void setupWhenShowDefaultBadgeTest() {
        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).showDefaultBadge(true);
    }

    @Test
    public void setupWhenHideDefaultBadgeTest() {
        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.VALID);
        final ArchetypeItem archetypeItem = new ArchetypeItem(archetype,
                                                              true,
                                                              false);

        presenter.setup(archetypeItem,
                        tablePresenter);

        verify(view).showDefaultBadge(false);
    }

    @Test
    public void setupWhenEnableIncludeTest() {
        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).enableIncludeCheckbox(true);
    }

    @Test
    public void setupWhenDisableIncludeTest() {
        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.INVALID);
        final ArchetypeItem archetypeItem = new ArchetypeItem(archetype,
                                                              true,
                                                              true);

        presenter.setup(archetypeItem,
                        tablePresenter);

        verify(view).enableIncludeCheckbox(false);
    }

    @Test
    public void setupWhenEnableSetDefaultTest() {
        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        verify(view).enableSetDefault(true);
    }

    @Test
    public void setupWhenDisableSetDefaultTest() {
        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.INVALID);
        final ArchetypeItem archetypeItem = new ArchetypeItem(archetype,
                                                              true,
                                                              true);

        presenter.setup(archetypeItem,
                        tablePresenter);

        verify(view).enableSetDefault(false);
    }

    @Test
    public void getObjectTest() {
        final ArchetypeItem archetypeItem = createArchetypeItem();
        presenter.setup(archetypeItem,
                        tablePresenter);

        assertEquals(archetypeItem, presenter.getObject());
    }

    @Test
    public void showBusyIndicatorTest() {
        final String msg = "Loading";

        presenter.showBusyIndicator(msg);

        verify(busyIndicatorView).showBusyIndicator(msg);
    }

    @Test
    public void hideBusyIndicatorTest() {
        presenter.hideBusyIndicator();

        verify(busyIndicatorView).hideBusyIndicator();
    }

    @Test
    public void setIncludedWhenCannotMakeChangesTest() {
        doReturn(false).when(tablePresenter).canMakeChanges();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);
        presenter.setIncluded(true);

        verify(view, never()).checkIncluded(anyBoolean());
        verify(tablePresenter, never()).setSelected(any(),
                                                    anyBoolean());
    }

    @Test
    public void setIncludedWhenIsNotValidTest() {
        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.INVALID);
        final ArchetypeItem archetypeItem = new ArchetypeItem(archetype,
                                                              true,
                                                              true);
        doReturn(true).when(tablePresenter).canMakeChanges();

        presenter.setup(archetypeItem,
                        tablePresenter);
        presenter.setIncluded(true);

        verify(view, never()).checkIncluded(anyBoolean());
        verify(tablePresenter, never()).setSelected(any(),
                                                    anyBoolean());
    }

    @Test
    public void setIncludedSuccessWhenFalseTest() {
        doReturn(true).when(tablePresenter).canMakeChanges();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);
        presenter.setIncluded(false);

        verify(view).checkIncluded(false);
        verify(tablePresenter).setSelected(any(ArchetypeItem.class),
                                           eq(false));
    }

    @Test
    public void setIncludedSuccessWhenTrueTest() {
        doReturn(true).when(tablePresenter).canMakeChanges();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);
        presenter.setIncluded(true);

        verify(view).checkIncluded(true);
        verify(tablePresenter).setSelected(any(ArchetypeItem.class),
                                           eq(true));
    }

    @Test
    public void makeDefaultWhenCannotMakeChangesTest() {
        doReturn(false).when(tablePresenter).canMakeChanges();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);
        presenter.makeDefault();

        verify(presenter, never()).setIncluded(true);
        verify(tablePresenter, never()).makeDefaultValue(any(),
                                                         anyBoolean());
    }

    @Test
    public void makeDefaultWhenIsNotValidTest() {
        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.INVALID);
        final ArchetypeItem archetypeItem = new ArchetypeItem(archetype,
                                                              true,
                                                              true);
        doReturn(true).when(tablePresenter).canMakeChanges();

        presenter.setup(archetypeItem,
                        tablePresenter);
        presenter.makeDefault();

        verify(presenter, never()).setIncluded(true);
        verify(tablePresenter, never()).makeDefaultValue(any(),
                                                         anyBoolean());
    }

    @Test
    public void makeDefaultSuccessTest() {
        doReturn(true).when(tablePresenter).canMakeChanges();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);
        presenter.makeDefault();

        verify(presenter).setIncluded(true);
        verify(tablePresenter).makeDefaultValue("myArchetype",
                                                true);
    }

    @Test
    public void validateWhenCannotMakeChangesTest() {
        doReturn(false).when(tablePresenter).canMakeChanges();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        presenter.validate();

        verify(archetypeService, never()).validate(anyString());
    }

    @Test
    public void validateSuccessTest() {
        doReturn(true).when(tablePresenter).canMakeChanges();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        presenter.validate();

        verify(archetypeService).validate("myArchetype");
    }

    @Test
    public void deleteCommandWhenCannotMakeChangesTest() {
        doReturn(false).when(tablePresenter).canMakeChanges();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        final Command deleteCommand = presenter.createDeleteCommand(createArchetypeItem());

        deleteCommand.execute();

        verify(archetypeService, never()).delete(anyString());
    }

    @Test
    public void deleteCommandSuccessTest() {
        doReturn(true).when(tablePresenter).canMakeChanges();

        presenter.setup(createArchetypeItem(),
                        tablePresenter);

        final Command deleteCommand = presenter.createDeleteCommand(createArchetypeItem());

        deleteCommand.execute();

        verify(archetypeService).delete("myArchetype");
    }

    private Archetype createArchetypeWithStatus(final ArchetypeStatus status) {
        return new Archetype("myArchetype",
                             mock(GAV.class),
                             new Date(),
                             status);
    }

    private ArchetypeItem createArchetypeItem() {
        final Archetype archetype = createArchetypeWithStatus(ArchetypeStatus.VALID);
        return new ArchetypeItem(archetype,
                                 true,
                                 true);
    }
}