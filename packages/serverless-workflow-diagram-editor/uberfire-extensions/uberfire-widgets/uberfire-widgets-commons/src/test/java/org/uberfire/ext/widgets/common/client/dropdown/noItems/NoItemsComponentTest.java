package org.uberfire.ext.widgets.common.client.dropdown.noItems;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//@RunWith(GwtMockitoTestRunner.class)
public class NoItemsComponentTest {

    private static final String MESSAGE = "a message";

    //@Mock
    private NoItemsComponentView view;

    private NoItemsComponent component;

    //@Test
    public void testFunctionality() {
        component = new NoItemsComponent(view);

        component.getElement();
        verify(view).getElement();

        component.show();
        verify(view).show();

        component.hide();
        verify(view).hide();

        component.setMessage(MESSAGE);
        verify(view).setMessage(MESSAGE);

        component.setMessage(null);
        verify(view, times(2)).setMessage(anyString());
    }
}
