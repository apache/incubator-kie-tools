package org.uberfire.client.views.pfly.widgets;

import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KebabMenuTest {

    @Mock
    HTMLUListElement dropdownMenu;

    @Mock
    HTMLDocument document;

    @Mock
    HTMLDivElement kebab;

    @InjectMocks
    KebabMenu kebabMenu;

    @Before
    public void setup() {
        final HTMLLIElement element = mock(HTMLLIElement.class);
        element.classList = mock(DOMTokenList.class);
        when(document.createElement("li")).thenReturn(element);
        kebab.classList = mock(DOMTokenList.class);
    }

    @Test
    public void testAddSeparator() {
        kebabMenu.addSeparator();

        verify(dropdownMenu).appendChild(any(HTMLLIElement.class));
    }

    @Test
    public void testDropPositionUp() {
        kebabMenu.setDropPosition(KebabMenu.DropPosition.UP);

        verify(kebab.classList).add("dropup");
        verify(kebab.classList).remove("dropdown");
        verifyNoMoreInteractions(kebab.classList);
    }

    @Test
    public void testDropPositionDown() {
        kebabMenu.setDropPosition(KebabMenu.DropPosition.DOWN);

        verify(kebab.classList).remove("dropup");
        verify(kebab.classList).add("dropdown");
        verifyNoMoreInteractions(kebab.classList);
    }
}
