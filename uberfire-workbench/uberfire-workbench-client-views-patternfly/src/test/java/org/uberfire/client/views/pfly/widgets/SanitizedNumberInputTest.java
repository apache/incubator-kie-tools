package org.uberfire.client.views.pfly.widgets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jboss.errai.common.client.dom.KeyboardEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SanitizedNumberInputTest {

    @InjectMocks
    SanitizedNumberInput input;

    @Before
    public void setup() {
    }

    private boolean allowNegative = false;
    private boolean allowDecimal = false;

    @Test
    public void testNumericInput() {
        testValidKeyCode("9");
        testValidKeyCode("8");
        testValidKeyCode("0");
        testValidKeyCode("Backspace");

        testInvalidKeyCode("-");
        testInvalidKeyCode("+");
        testInvalidKeyCode(" ");
        testInvalidKeyCode(".");
    }

    @Test
    public void testNumericInputNegative() {
        allowNegative = true;
        allowDecimal = false;
        testValidKeyCode("-");
        testInvalidKeyCode(".");
    }

    @Test
    public void testNumericInputDecimal() {
        allowNegative = false;
        allowDecimal = true;
        testInvalidKeyCode("-");
        testValidKeyCode(".");
    }

    @Test
    public void testNumericInputNegativeDecimal() {
        allowNegative = true;
        allowDecimal = true;
        testValidKeyCode("-");
        testValidKeyCode(".");
    }

    protected void testValidKeyCode(String key) {
        testKeyCode(key,
                    0);
    }

    protected void testInvalidKeyCode(String key) {
        testKeyCode(key,
                    1);
    }

    protected void testKeyCode(String key,
                               int wantedNumberOfInvocations) {
        final KeyboardEvent event = mock(KeyboardEvent.class);
        when(event.getKey()).thenReturn(key);
        input.getEventListener(allowNegative, allowDecimal).call(event);
        verify(event,
               times(wantedNumberOfInvocations)).preventDefault();
    }
}
