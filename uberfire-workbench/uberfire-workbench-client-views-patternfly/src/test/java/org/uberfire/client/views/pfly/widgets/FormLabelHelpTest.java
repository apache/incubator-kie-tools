package org.uberfire.client.views.pfly.widgets;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FormLabelHelpTest {

    @Mock
    private FormLabel formLabel;

    @Mock
    private FlowPanel panel;

    private FormLabelHelp formLabelHelp;

    @Before
    public void setUp() {
        this.formLabelHelp = new FormLabelHelp( formLabel, panel );
    }

    @Test
    public void setHelpTitleNull() {
        Mockito.reset( panel );

        formLabelHelp.setHelpTitle( null );

        verify( panel, times( 0 ) ).add( isA( HelpIcon.class ) );
    }


    @Test
    public void setHelpTitleNotNull() {
        Mockito.reset( panel );

        formLabelHelp.setHelpTitle( "testTitle" );

        verify( panel ).add( isA( HelpIcon.class ) );
    }

    @Test
    public void setHelpContentNull() {
        Mockito.reset( panel );

        formLabelHelp.setHelpContent( null );

        verify( panel, times( 0 ) ).add( isA( HelpIcon.class ) );
    }

    @Test
    public void setHelpContentNotNull() {
        Mockito.reset( panel );

        formLabelHelp.setHelpContent( "testContent" );

        verify( panel ).add( isA( HelpIcon.class ) );
    }

    @Test
    public void setText() {
        formLabelHelp.setText( "testText" );

        verify( formLabel ).setText( "testText" );
    }

    @Test
    public void getText() {
        Mockito.when( formLabel.getText() ).thenReturn( "testText" );

        assertEquals( "testText", formLabelHelp.getText() );
    }

    @Test
    public void setFor() {
        formLabelHelp.setFor( "testFor" );

        verify( formLabel ).setFor( "testFor" );
    }

}
