package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions;

import javax.enterprise.event.Event;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.KnowledgeSessionsModal.KnowledgeBasesListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieEnumSelectElement;
import org.kie.workbench.common.services.shared.kmodule.ClockTypeOption;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KnowledgeSessionListItemPresenterTest {

    private KnowledgeSessionListItemPresenter knowledgeSessionListItemPresenter;

    @Mock
    private KnowledgeSessionListItemPresenter.View view;

    @Mock
    private Event<DefaultKnowledgeSessionChange> defaultKnowledgeSessionChangeEvent;

    @Mock
    private KnowledgeSessionListItemPresenter.WorkItemHandlersListPresenter workItemHandlersListPresenter;

    @Mock
    private KnowledgeSessionListItemPresenter.ListenersListPresenter listenersListPresenter;

    @Mock
    private KieEnumSelectElement<ClockTypeOption> clockSelect;

    @Before
    public void before() {
        knowledgeSessionListItemPresenter = spy(new KnowledgeSessionListItemPresenter(view,
                                                                                      defaultKnowledgeSessionChangeEvent,
                                                                                      workItemHandlersListPresenter,
                                                                                      listenersListPresenter));
    }

    @Test
    public void testSetup() {
        knowledgeSessionListItemPresenter.setup(mock(KSessionModel.class), mock(KnowledgeSessionsModal.class));
        verify(view).init(eq(knowledgeSessionListItemPresenter));
        verify(view).setIsDefault(anyBoolean());
        verify(view).setName(any());
        verify(view).setType(any());
        verify(view).setListenersCount(anyInt());
        verify(view).setWorkItemHandlersCount(anyInt());
        verify(view).initListViewCompoundExpandableItems();
        verify(listenersListPresenter).setup(any(), any(), any());
        verify(workItemHandlersListPresenter).setup(any(), any(), any());
        verify(view).setupClockElement(any(), any());
    }

    @Test
    public void testSetName() {
        final KSessionModel kSessionModel = new KSessionModel();
        final KnowledgeSessionsModal parentPresenter = mock(KnowledgeSessionsModal.class);

        knowledgeSessionListItemPresenter.parentPresenter = parentPresenter;
        knowledgeSessionListItemPresenter.kSessionModel = kSessionModel;

        knowledgeSessionListItemPresenter.setName("Name");

        Assert.assertEquals("Name", kSessionModel.getName());
        verify(parentPresenter).fireChangeEvent();
    }

    @Test
    public void testSetType() {
        final KSessionModel kSessionModel = new KSessionModel();
        final KnowledgeSessionsModal parentPresenter = mock(KnowledgeSessionsModal.class);

        knowledgeSessionListItemPresenter.parentPresenter = parentPresenter;
        knowledgeSessionListItemPresenter.kSessionModel = kSessionModel;

        knowledgeSessionListItemPresenter.setType("Type");

        Assert.assertEquals("Type", kSessionModel.getType());
        verify(parentPresenter).fireChangeEvent();
    }

    @Test
    public void testSetDefault() {
        final KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setDefault(false);

        final KnowledgeSessionsModal parentPresenter = mock(KnowledgeSessionsModal.class);

        knowledgeSessionListItemPresenter.parentPresenter = parentPresenter;
        knowledgeSessionListItemPresenter.kSessionModel = kSessionModel;

        knowledgeSessionListItemPresenter.setDefault(true);

        Assert.assertTrue(kSessionModel.isDefault());
        verify(defaultKnowledgeSessionChangeEvent).fire(any());
        verify(parentPresenter).fireChangeEvent();
    }

    @Test
    public void testRemove() {
        final KnowledgeSessionsModal parentPresenter = mock(KnowledgeSessionsModal.class);
        final KnowledgeBasesListPresenter listPresenter = mock(KnowledgeBasesListPresenter.class);

        knowledgeSessionListItemPresenter.parentPresenter = parentPresenter;
        knowledgeSessionListItemPresenter.setListPresenter(listPresenter);

        knowledgeSessionListItemPresenter.remove();

        verify(listPresenter).remove(eq(knowledgeSessionListItemPresenter));
        verify(parentPresenter).signalKnowledgeBaseAddedOrRemoved();
    }

    @Test
    public void testAddListener() {
        doNothing().when(knowledgeSessionListItemPresenter).fireChangeEvent();
        knowledgeSessionListItemPresenter.kSessionModel = new KSessionModel();
        knowledgeSessionListItemPresenter.addListener();
        verify(listenersListPresenter).add(any());
        verify(knowledgeSessionListItemPresenter).signalListenerAddedOrRemoved();
        verify(knowledgeSessionListItemPresenter).fireChangeEvent();
    }

    @Test
    public void testAddWorkItemHandler() {
        doNothing().when(knowledgeSessionListItemPresenter).fireChangeEvent();
        knowledgeSessionListItemPresenter.kSessionModel = new KSessionModel();
        knowledgeSessionListItemPresenter.addWorkItemHandler();
        verify(workItemHandlersListPresenter).add(any());
        verify(knowledgeSessionListItemPresenter).signalWorkItemHandlerAddedOrRemoved();
        verify(knowledgeSessionListItemPresenter).fireChangeEvent();
    }

    @Test
    public void testOnDefaultKnowledgeSessionChanged() {
        final KBaseModel kBaseModel = new KBaseModel();
        final KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setDefault(true);

        final KnowledgeSessionsModal parentPresenter = mock(KnowledgeSessionsModal.class);
        doReturn(kBaseModel).when(parentPresenter).getObject();

        knowledgeSessionListItemPresenter.parentPresenter = parentPresenter;
        knowledgeSessionListItemPresenter.kSessionModel = kSessionModel;

        knowledgeSessionListItemPresenter.onDefaultKnowledgeSessionChanged(new DefaultKnowledgeSessionChange(kBaseModel, kSessionModel));

        Assert.assertTrue(kSessionModel.isDefault());
    }

    @Test
    public void testOnDefaultKnowledgeSessionChangedWithAnotherKSessionModel() {
        final KBaseModel kBaseModel = new KBaseModel();
        final KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setDefault(true);

        final KnowledgeSessionsModal parentPresenter = mock(KnowledgeSessionsModal.class);
        doReturn(kBaseModel).when(parentPresenter).getObject();

        knowledgeSessionListItemPresenter.parentPresenter = parentPresenter;
        knowledgeSessionListItemPresenter.kSessionModel = kSessionModel;

        final KSessionModel anotherKSessionModel = new KSessionModel();
        anotherKSessionModel.setName("Distinguishing name");

        knowledgeSessionListItemPresenter.onDefaultKnowledgeSessionChanged(new DefaultKnowledgeSessionChange(kBaseModel, anotherKSessionModel));

        Assert.assertEquals(false, kSessionModel.isDefault());
    }
}