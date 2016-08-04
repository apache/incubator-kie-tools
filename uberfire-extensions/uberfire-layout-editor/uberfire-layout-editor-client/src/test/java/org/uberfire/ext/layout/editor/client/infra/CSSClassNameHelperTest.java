package org.uberfire.ext.layout.editor.client.infra;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CSSClassNameHelperTest {

    HTMLElement element = mock( HTMLElement.class );

    @Test
    public void hasClassName() {
        when( element.getClassName() ).thenReturn( "dora dora1" );
        assertTrue( CSSClassNameHelper.hasClassName( element, "dora" ) );

        when( element.getClassName() ).thenReturn( "dora1 dora2" );
        assertFalse( CSSClassNameHelper.hasClassName( element, "dora" ) );

        when( element.getClassName() ).thenReturn( "   " );
        assertFalse( CSSClassNameHelper.hasClassName( element, "dora" ) );

        when( element.getClassName() ).thenReturn( "" );
        assertFalse( CSSClassNameHelper.hasClassName( element, "dora" ) );

        when( element.getClassName() ).thenReturn( "     dora       dora1" );
        assertTrue( CSSClassNameHelper.hasClassName( element, "dora" ) );

        when( element.getClassName() ).thenReturn( "dora dora1 dora2" );
        assertTrue( CSSClassNameHelper.hasClassName( element, "dora2" ) );
    }

    @Test
    public void removeClassName() {
        when( element.getClassName() ).thenReturn( "dora dora1" );
        CSSClassNameHelper.removeClassName( element, "dora" );
        verify( element ).setClassName( "dora1" );

        CSSClassNameHelper.removeClassName( element, "dora2" );
        verify( element ).setClassName( "dora1" );

        when( element.getClassName() ).thenReturn( "dora" );
        CSSClassNameHelper.removeClassName( element, "dora" );
        verify( element ).setClassName( "" );

    }


    @Test
    public void addClassName() {

        when( element.getClassName() ).thenReturn( "" );
        CSSClassNameHelper.addClassName( element, "dora" );
        verify( element ).setClassName( "dora" );


        when( element.getClassName() ).thenReturn( "dora dora1" );
        CSSClassNameHelper.addClassName( element, "dora" );
        verify( element, never() ).setClassName( "dora dora1" );

        CSSClassNameHelper.addClassName( element, "dora2" );
        verify( element ).setClassName( "dora dora1 dora2" );

        when( element.getClassName() ).thenReturn( "dora" );
        CSSClassNameHelper.addClassName( element, "dora" );
        verify( element ).setClassName( "dora" );

    }

}