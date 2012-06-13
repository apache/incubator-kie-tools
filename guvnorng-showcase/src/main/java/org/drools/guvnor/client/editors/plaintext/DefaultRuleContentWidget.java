/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.editors.plaintext;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.EditorWidget;
import org.drools.guvnor.shared.common.vo.assets.plaintext.RuleContentText;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.TextArea;

/**
 * This is the default rule editor widget (just text editor based) - more to
 * come later.
 */
public class DefaultRuleContentWidget extends DirtyableComposite
    implements
    EditorWidget {

    private TextArea              text;
    final private RuleContentText data;

    public DefaultRuleContentWidget(RuleContentText asset) {
        this( asset,
              16 );
    }

    public DefaultRuleContentWidget(RuleContentText asset,
                                    int visibleLines) {
        data = asset;

        if ( data.getContent() == null ) {
            data.setContent( "" );
        }

        text = new TextArea();
        text.setWidth( "100%" );
        text.setVisibleLines( (visibleLines == -1) ? 16 : visibleLines );
        text.setText( data.getContent() );

        text.getElement().setAttribute( "spellcheck",
                                        "false" ); //NON-NLS

        text.setStyleName( "default-text-Area" ); //NON-NLS

        text.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                data.setContent( text.getText() );
            }
        } );

        text.addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_TAB ) {
                    int pos = text.getCursorPos();
                    insertText( "\t" );
                    text.setCursorPos( pos + 1 );
                    text.cancelKey();
                    text.setFocus( true );
                }
            }
        } );

        initWidget( text );

    }

    public void insertText(String ins) {
        int i = text.getCursorPos();
        String left = text.getText().substring( 0,
                                                i );
        String right = text.getText().substring( i,
                                                 text.getText().length() );
        text.setText( left + ins + right );
        this.data.setContent( text.getText() );
    }

}
