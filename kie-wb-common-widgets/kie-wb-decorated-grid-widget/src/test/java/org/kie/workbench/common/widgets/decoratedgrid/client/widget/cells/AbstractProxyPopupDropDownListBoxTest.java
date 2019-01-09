package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class AbstractProxyPopupDropDownListBoxTest {

    private final String operator;
    private final boolean isMultiSelect;

    @GwtMock
    private ListBox listBox;

    public AbstractProxyPopupDropDownListBoxTest(final String operator,
                                                 final boolean isMultiSelect) {

        this.operator = operator;
        this.isMultiSelect = isMultiSelect;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> caseSensitivity() {
        return Arrays.asList(
                new Object[][]{
                        // operator, is multiselect
                        {"==", false},
                        {"!=", false},
                        {"<", false},
                        {"<=", false},
                        {">", false},
                        {">=", false},
                        {"contains", false},
                        {"excludes", false},
                        {"memberOf", false},
                        {"matches", false},
                        {"soundslike", false},
                        {"in", true}
                }
        );
    }

    public void makeAbstractProxyPopupDropDownListBox(final String operator) {

        new AbstractProxyPopupDropDownListBox<Double>(mock(AbstractProxyPopupDropDownEditCell.class),
                                                      operator) {
            @Override
            public String convertToString(Double value) {
                return value.toString();
            }

            @Override
            public Double convertFromString(String value) {
                return new Double(value);
            }
        };
    }

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);
    }

    @Test
    public void testIsMultiSelect() {
        makeAbstractProxyPopupDropDownListBox(operator);

        verify(listBox).setMultipleSelect(isMultiSelect);
    }
}