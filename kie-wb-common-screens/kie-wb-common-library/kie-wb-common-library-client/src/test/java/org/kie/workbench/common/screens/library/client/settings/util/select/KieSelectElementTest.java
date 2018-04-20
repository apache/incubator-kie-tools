package org.kie.workbench.common.screens.library.client.settings.util.select;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.Node;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieSelectElement.Option;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KieSelectElementTest {

    @Mock
    private KieSelectElement.View view;

    @Mock
    private KieSelectElement.OptionsListPresenter optionsListPresenter;

    private KieSelectElement kieSelectElement;

    @Before
    public void before() {
        kieSelectElement = spy(new KieSelectElement(view, optionsListPresenter, new Elemental2DomUtil()));
    }

    @Test
    public void testSetup() {
        final HTMLElement viewRoot = spy(new HTMLElement());
        viewRoot.innerHTML = "bar";
        doReturn(viewRoot).when(view).getElement();

        final HTMLSelectElement selectElement = spy(new HTMLSelectElement());
        doReturn(selectElement).when(view).getSelect();

        final Element container = spy(new Element() {
            @Override
            public Node appendChild(final Node node) {
                if (node instanceof HTMLElement) {
                    this.innerHTML += ((HTMLElement) node).innerHTML;
                }
                return node;
            }
        });

        container.innerHTML = "";

        final List<Option> options =
                singletonList(new Option("Label", "Value"));

        kieSelectElement.setup(
                container,
                options,
                "Value",
                value -> {
                });

        verify(view).setValue(eq("Value"));
        verify(view).initSelect();
        verify(optionsListPresenter).setup(eq(selectElement), eq(options), any());
        assertEquals("bar", container.innerHTML);
    }

    @Test
    public void testOnChange() {
        final AtomicInteger i = new AtomicInteger(0);
        doReturn("Test").when(kieSelectElement).getValue();

        kieSelectElement.onChange = value -> {
            assertEquals("Test", value);
            i.incrementAndGet();
        };

        kieSelectElement.onChange();

        assertEquals(1, i.get());
    }
}