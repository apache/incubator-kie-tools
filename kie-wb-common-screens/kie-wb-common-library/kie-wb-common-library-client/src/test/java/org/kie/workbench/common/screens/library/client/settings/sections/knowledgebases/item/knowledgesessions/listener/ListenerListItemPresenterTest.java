package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.listener;

import elemental2.dom.HTMLElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.KnowledgeSessionListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.KnowledgeSessionListItemPresenter.ListenersListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieEnumSelectElement;
import org.kie.workbench.common.services.shared.kmodule.ListenerModel;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListenerListItemPresenterTest {

    private ListenerListItemPresenter listenerListItemPresenter;

    @Mock
    private ListenerListItemPresenter.View view;

    @Mock
    private KieEnumSelectElement<ListenerModel.Kind> kindSelect;

    @Before
    public void before() {
        listenerListItemPresenter = spy(new ListenerListItemPresenter(view,
                                                                      kindSelect));
    }

    @Test
    public void testSetup() {
        final ListenerModel listenerModel = new ListenerModel();
        listenerModel.setType("Type");
        listenerModel.setKind(ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER);

        final HTMLElement selectContainer = mock(HTMLElement.class);
        doReturn(selectContainer).when(view).getKindSelectContainer();

        final ListenerListItemPresenter result = listenerListItemPresenter.setup(listenerModel,
                                                                                 mock(KnowledgeSessionListItemPresenter.class));

        Assert.assertEquals(result, listenerListItemPresenter);
        verify(view).init(eq(listenerListItemPresenter));
        verify(view).setType(eq("Type"));
        verify(kindSelect).setup(eq(selectContainer),
                                 eq(ListenerModel.Kind.values()),
                                 eq(ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER),
                                 any());
    }

    @Test
    public void testSetType() {
        listenerListItemPresenter.model = new ListenerModel();
        listenerListItemPresenter.parentPresenter = mock(KnowledgeSessionListItemPresenter.class);

        listenerListItemPresenter.setType("Type");

        Assert.assertEquals("Type", listenerListItemPresenter.model.getType());
        verify(listenerListItemPresenter.parentPresenter).fireChangeEvent();
    }

    @Test
    public void testRemove() {
        final KnowledgeSessionListItemPresenter parentPresenter = mock(KnowledgeSessionListItemPresenter.class);
        final ListenersListPresenter listPresenter = mock(ListenersListPresenter.class);

        listenerListItemPresenter.parentPresenter = parentPresenter;
        listenerListItemPresenter.setListPresenter(listPresenter);

        listenerListItemPresenter.remove();

        verify(listPresenter).remove(eq(listenerListItemPresenter));
        verify(parentPresenter).fireChangeEvent();
        verify(parentPresenter).signalListenerAddedOrRemoved();
    }
}