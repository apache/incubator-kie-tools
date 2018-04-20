package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.KnowledgeBaseItemPresenter;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KnowledgeSessionsModalTest {

    private KnowledgeSessionsModal knowledgeSessionsModal;

    @Mock
    private KnowledgeSessionsModal.View view;

    @Mock
    private KnowledgeSessionsModal.KnowledgeBasesListPresenter knowledgeBasesListPresenter;

    @Before
    public void before() {
        knowledgeSessionsModal = spy(new KnowledgeSessionsModal(view,
                                                                knowledgeBasesListPresenter));
    }

    @Test
    public void testSetup() {
        final KnowledgeBaseItemPresenter parentPresenter = mock(KnowledgeBaseItemPresenter.class);
        doReturn(new KBaseModel()).when(parentPresenter).getObject();
        doNothing().when(knowledgeSessionsModal).superSetup();
        doNothing().when(knowledgeSessionsModal).setWidth(any());

        knowledgeSessionsModal.setup(parentPresenter);

        verify(knowledgeSessionsModal).superSetup();
        verify(knowledgeBasesListPresenter).setup(any(), any(), any());
    }

    @Test
    public void testAdd() {
        doNothing().when(knowledgeSessionsModal).signalKnowledgeBaseAddedOrRemoved();
        doReturn(mock(KSessionModel.class)).when(knowledgeSessionsModal).newKSessionModel();

        knowledgeSessionsModal.add();

        verify(knowledgeBasesListPresenter).add(any());
        verify(knowledgeSessionsModal).signalKnowledgeBaseAddedOrRemoved();
    }

    @Test
    public void testNewKSessionModelEmptyList() {
        doReturn(emptyList()).when(knowledgeBasesListPresenter).getObjectsList();

        final KSessionModel kSessionModel = knowledgeSessionsModal.newKSessionModel();

        Assert.assertEquals("", kSessionModel.getName());
        Assert.assertTrue(kSessionModel.isDefault());
    }

    @Test
    public void testNewKSessionModelNonEmptyList() {
        doReturn(singletonList(mock(KSessionModel.class))).when(knowledgeBasesListPresenter).getObjectsList();

        final KSessionModel kSessionModel = knowledgeSessionsModal.newKSessionModel();

        Assert.assertEquals("", kSessionModel.getName());
        Assert.assertEquals(false, kSessionModel.isDefault());
    }

    @Test
    public void testDone() {
        final KnowledgeSessionListItemPresenter mock1 = mock(KnowledgeSessionListItemPresenter.class);
        final KnowledgeSessionListItemPresenter mock2 = mock(KnowledgeSessionListItemPresenter.class);

        doReturn(Arrays.asList(mock1, mock2)).when(knowledgeBasesListPresenter).getPresenters();
        doNothing().when(knowledgeSessionsModal).hide();

        knowledgeSessionsModal.done();

        verify(mock1).closeAllExpandableListItems();
        verify(mock2).closeAllExpandableListItems();
        verify(knowledgeSessionsModal).hide();
    }
}