package org.kie.workbench.common.screens.library.client.settings.util.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.includedknowledgebases.IncludedKnowledgeBaseItemPresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.KnowledgeBaseItemPresenter.IncludedKnowledgeBasesListPresenter;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListPresenterTest {

    @Mock
    private ManagedInstance<IncludedKnowledgeBaseItemPresenter> includedKnowledgeBaseItemPresenters;

    private IncludedKnowledgeBasesListPresenter listPresenter;

    @Before
    public void before() {
        listPresenter = spy(new IncludedKnowledgeBasesListPresenter(includedKnowledgeBaseItemPresenters));
    }

    @Test
    public void testSetup() {
        doNothing().when(listPresenter).addToListElement(any());
        final List<String> objs = Arrays.asList("foo", "bar");

        listPresenter.setup(mock(HTMLElement.class), objs, (a, b) -> {
        });

        assertEquals(objs, listPresenter.getObjectsList());
        verify(listPresenter, times(2)).addToListElement(any());
    }

    @Test
    public void testSetupWithPresenters() {
        doNothing().when(listPresenter).addPresenter(any());

        final IncludedKnowledgeBaseItemPresenter p1 = mock(IncludedKnowledgeBaseItemPresenter.class);
        doReturn("foo").when(p1).getObject();

        final IncludedKnowledgeBaseItemPresenter p2 = mock(IncludedKnowledgeBaseItemPresenter.class);
        doReturn("bar").when(p2).getObject();

        final List<IncludedKnowledgeBaseItemPresenter> presenters = Arrays.asList(p1, p2);

        listPresenter.setupWithPresenters(mock(HTMLElement.class), presenters, (a, b) -> {
        });

        assertEquals(presenters, listPresenter.getPresenters());
        assertEquals(Arrays.asList("foo", "bar"), listPresenter.getObjectsList());
        verify(listPresenter, times(2)).addPresenter(any());
    }

    @Test
    public void addTest() {
        doNothing().when(listPresenter).addToListElement(any());
        final List<String> objs = new ArrayList<>(Arrays.asList("foo", "bar"));
        listPresenter.setup(mock(HTMLElement.class), objs, (a, b) -> {
        });

        listPresenter.add("dee");

        verify(listPresenter).addToListElement(eq("dee"));
        assertEquals(Arrays.asList("foo", "bar", "dee"), listPresenter.getObjectsList());
    }

    @Test
    public void newPresenterForTest() {
        final IncludedKnowledgeBaseItemPresenter p1 = spy(new IncludedKnowledgeBaseItemPresenter(null));
        doReturn(p1).when(includedKnowledgeBaseItemPresenters).get();

        listPresenter.setup(mock(HTMLElement.class), new ArrayList<>(), (o, p) -> {
            assertEquals("foo", o);
            assertEquals(p1, p);
        });

        listPresenter.newPresenterFor("foo");

        verify(p1).setListPresenter(eq(listPresenter));
    }
}