/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.guvnor.guided.rule.client.editor;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextArea;

/**
 * A TextArea that resizes itself to specified minimum and maximum as text is
 * inserted.
 */
public class DynamicTextArea extends TextArea
        implements
        HasResizeHandlers {

    public DynamicTextArea() {
        super();
        this.getElement().setAttribute( "wrap",
                                        "off" );
    }

    //Defaults
    protected int minWidth = 60;
    protected int maxWidth = 100;
    protected int minLines = 1;
    protected int maxLines = 20;

    @Override
    public void setText( String text ) {
        super.setText( text );
        assertTextAreaDimensions();

        //Add handlers for all keyboard events so that the TextArea
        //can be resized as text is inserted (or deleted) and when
        //the keyboard is 'auto-repeating'
        addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown( KeyDownEvent event ) {
                assertTextAreaDimensions();
            }

        } );
        addKeyUpHandler( new KeyUpHandler() {

            public void onKeyUp( KeyUpEvent event ) {
                assertTextAreaDimensions();
            }
        } );
        addKeyPressHandler( new KeyPressHandler() {

            public void onKeyPress( KeyPressEvent event ) {
                assertTextAreaDimensions();
            }
        } );
    }

    //Set the TextArea's size
    private void assertTextAreaDimensions() {
        String text = getText();
        int oldLines = getVisibleLines();
        int oldCharacters = getCharacterWidth();
        setNumberOfLines( text );
        setMaxLineWidth( text );

        //Fire a resize event, if applicable
        boolean resizeContainer = false;
        if ( oldLines != getVisibleLines() ) {
            resizeContainer = true;
        }
        if ( oldCharacters != getCharacterWidth() ) {
            resizeContainer = true;
        }
        if ( resizeContainer ) {
            ResizeEvent.fire( this,
                              getVisibleLines(),
                              getCharacterWidth() );
        }
    }

    //Get the maximum length of any line. Returns true if the 
    //required size is greater than the maximum configured size
    private boolean setMaxLineWidth( String text ) {
        boolean overflow = false;
        if ( text == null || text.length() == 0 ) {
            this.setCharacterWidth( minWidth );
            return overflow;
        }
        int maxFoundWidth = 0;
        String[] aLines = text.split( "\\n" );
        for ( int i = 0; i < aLines.length; i++ ) {
            String aLine = aLines[ i ];
            if ( aLine.length() > maxFoundWidth ) {
                maxFoundWidth = aLine.length();
            }
        }
        if ( maxFoundWidth < minWidth ) {
            maxFoundWidth = minWidth;
        }
        if ( maxFoundWidth > maxWidth ) {
            maxFoundWidth = maxWidth;
            overflow = true;
        }

        this.setCharacterWidth( maxFoundWidth );
        return overflow;
    }

    //Get the number of lines. Returns true if the required number 
    //of lines is greater than the maximum configured size
    private boolean setNumberOfLines( String text ) {
        boolean overflow = false;
        if ( text == null || text.length() == 0 ) {
            this.setVisibleLines( minLines );
            return overflow;
        }

        int lines = 1;
        String[] aLines = text.split( "\\n" );
        lines = aLines.length;
        if ( text.endsWith( "\n" ) ) {
            lines++;
        }
        if ( lines < minLines ) {
            lines = minLines;
        }
        if ( lines > maxLines ) {
            lines = maxLines;
            overflow = true;
        }
        this.setVisibleLines( lines );
        return overflow;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth( int minWidth ) {
        this.minWidth = minWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth( int maxWidth ) {
        this.maxWidth = maxWidth;
    }

    public int getMinLines() {
        return minLines;
    }

    public void setMinLines( int minLines ) {
        this.minLines = minLines;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines( int maxLines ) {
        this.maxLines = maxLines;
    }

    public HandlerRegistration addResizeHandler( ResizeHandler handler ) {
        return addHandler( handler,
                           ResizeEvent.getType() );
    }

}
