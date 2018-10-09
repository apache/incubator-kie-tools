package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item;

import javax.enterprise.event.Event;

import elemental2.dom.HTMLInputElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.KnowledgeBasesPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.KnowledgeSessionsModal;
import org.kie.workbench.common.services.shared.kmodule.SingleValueItemObjectModel;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieEnumSelectElement;
import org.kie.workbench.common.services.shared.kmodule.AssertBehaviorOption;
import org.kie.workbench.common.services.shared.kmodule.EventProcessingOption;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KnowledgeBaseItemPresenterTest {

    private KnowledgeBaseItemPresenter knowledgeBaseItemPresenter;

    @Mock
    private KnowledgeBaseItemPresenter.View view;

    @Mock
    private Event<DefaultKnowledgeBaseChange> defaultKnowledgeBaseChangeEvent;

    @Mock
    private KieEnumSelectElement<AssertBehaviorOption> equalsBehaviorSelect;

    @Mock
    private KieEnumSelectElement<EventProcessingOption> eventProcessingModeSelect;

    @Mock
    private KnowledgeSessionsModal knowledgeSessionsModal;

    @Mock
    private KnowledgeBaseItemPresenter.IncludedKnowledgeBasesListPresenter includedKnowledgeBasesListPresenter;

    @Mock
    private KnowledgeBaseItemPresenter.PackageListPresenter packageListPresenter;

    @Before
    public void before() {
        knowledgeBaseItemPresenter = spy(new KnowledgeBaseItemPresenter(view,
                                                                        defaultKnowledgeBaseChangeEvent,
                                                                        equalsBehaviorSelect,
                                                                        eventProcessingModeSelect,
                                                                        knowledgeSessionsModal,
                                                                        includedKnowledgeBasesListPresenter,
                                                                        packageListPresenter));
    }

    @Test
    public void testSetup() {
        final KnowledgeBaseItemPresenter setup = knowledgeBaseItemPresenter.setup(new KBaseModel(),
                                                                                  mock(KnowledgeBasesPresenter.class));

        Assert.assertEquals(setup, knowledgeBaseItemPresenter);

        verify(view).setKnowledgeSessionsCount(anyInt());
        verify(view).setDefault(anyBoolean());
        verify(view).setName(any());

        verify(knowledgeSessionsModal).setup(any());

        verify(packageListPresenter).setup(any(), any(), any());
        verify(includedKnowledgeBasesListPresenter).setup(any(), any(), any());

        verify(eventProcessingModeSelect).setup(any(), any(), any(), any());
        verify(equalsBehaviorSelect).setup(any(), any(), any(), any());
    }

    @Test
    public void testAddNewIncludedKnowledgeBase() {
        KnowledgeBasesPresenter parentPresenter = mock(KnowledgeBasesPresenter.class);
        knowledgeBaseItemPresenter.setup(new KBaseModel(),
                                         parentPresenter);

        knowledgeBaseItemPresenter.addNewIncludedKnowledgeBase();
        verify(includedKnowledgeBasesListPresenter).add(any());
        verify(parentPresenter).fireChangeEvent();
    }

    @Test
    public void testAddPackage() {
        KnowledgeBasesPresenter parentPresenter = mock(KnowledgeBasesPresenter.class);
        knowledgeBaseItemPresenter.setup(new KBaseModel(),
                                         parentPresenter);

        knowledgeBaseItemPresenter.addPackage();
        verify(packageListPresenter).add(any());
        verify(parentPresenter).fireChangeEvent();
    }

    @Test
    public void testShowKnowledgeSessionsModal() {
        knowledgeBaseItemPresenter.showKnowledgeSessionsModal();
        verify(knowledgeSessionsModal).show();
    }

    @Test
    public void testSignalAddedOrRemoved() {
        final KBaseModel kBaseModel = new KBaseModel();
        final KnowledgeBasesPresenter parentPresenter = mock(KnowledgeBasesPresenter.class);

        knowledgeBaseItemPresenter.kBaseModel = kBaseModel;
        knowledgeBaseItemPresenter.parentPresenter = parentPresenter;

        knowledgeBaseItemPresenter.signalAddedOrRemoved();

        verify(parentPresenter).fireChangeEvent();
        verify(view).setKnowledgeSessionsCount(eq(0));
    }

    @Test
    public void testSetDefault() {
        final KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setDefault(false);
        final KnowledgeBasesPresenter parentPresenter = mock(KnowledgeBasesPresenter.class);

        knowledgeBaseItemPresenter.kBaseModel = kBaseModel;
        knowledgeBaseItemPresenter.parentPresenter = parentPresenter;

        knowledgeBaseItemPresenter.setDefault(true);

        Assert.assertTrue(kBaseModel.isDefault());
        verify(defaultKnowledgeBaseChangeEvent).fire(any());
        verify(parentPresenter).fireChangeEvent();
    }

    @Test
    public void testOnDefaultKnowledgeSessionChanged() {
        final KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setDefault(true);
        knowledgeBaseItemPresenter.kBaseModel = kBaseModel;

        knowledgeBaseItemPresenter.onDefaultKnowledgeSessionChanged(new DefaultKnowledgeBaseChange(kBaseModel));

        Assert.assertTrue(kBaseModel.isDefault());
    }

    @Test
    public void testOnDefaultKnowledgeSessionChangedWithAnotherKBaseModel() {
        final KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setDefault(true);
        knowledgeBaseItemPresenter.kBaseModel = kBaseModel;

        knowledgeBaseItemPresenter.onDefaultKnowledgeSessionChanged(new DefaultKnowledgeBaseChange(mock(KBaseModel.class)));

        Assert.assertFalse(kBaseModel.isDefault());
    }

    @Test
    public void testRemove() {
        knowledgeBaseItemPresenter.setListPresenter(mock(KnowledgeBasesPresenter.KnowledgeBaseListPresenter.class));
        doNothing().when(this.knowledgeBaseItemPresenter).fireChangeEvent();

        knowledgeBaseItemPresenter.remove();

        verify(knowledgeBaseItemPresenter).remove();
        verify(knowledgeBaseItemPresenter).fireChangeEvent();
    }
}