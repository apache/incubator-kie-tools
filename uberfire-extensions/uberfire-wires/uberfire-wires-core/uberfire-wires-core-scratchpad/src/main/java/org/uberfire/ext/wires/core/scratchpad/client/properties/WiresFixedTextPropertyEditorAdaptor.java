/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.core.scratchpad.client.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.shared.core.types.TextAlign;
import com.google.common.collect.Lists;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.ext.wires.core.api.properties.PropertyEditorAdaptor;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.properties.WiresComboPropertyEditorFieldInfo;
import org.uberfire.ext.wires.core.scratchpad.client.shapes.fixed.WiresFixedText;

/**
 * PropertyEditorAdaptor for WiresFixedText to extract text
 */
@ApplicationScoped
public class WiresFixedTextPropertyEditorAdaptor implements PropertyEditorAdaptor {

    private static final String ATTRIBUTES = "Attributes";

    @Override
    public boolean supports( final WiresBaseShape shape ) {
        return shape instanceof WiresFixedText;
    }

    @Override
    public List<PropertyEditorCategory> getProperties( final WiresBaseShape shape ) {
        if ( !supports( shape ) ) {
            return Collections.emptyList();
        }
        final WiresFixedText w = (WiresFixedText) shape;
        final PropertyEditorFieldInfo fieldInfo1 = new PropertyEditorFieldInfo( "Text",
                                                                                String.valueOf( w.getText() ),
                                                                                PropertyEditorType.TEXT ) {
            @Override
            public void setCurrentStringValue( final String currentStringValue ) {
                super.setCurrentStringValue( currentStringValue );
                w.setText( currentStringValue );
                w.getLayer().batch();
            }
        };

        final PropertyEditorFieldInfo fieldInfo2 = new WiresComboPropertyEditorFieldInfo( "Text Align",
                                                                                          w.getTextAlign().getValue(),
                                                                                          getTextAlignValues() ) {
            @Override
            public void setCurrentStringValue( final String currentStringValue ) {
                super.setCurrentStringValue( currentStringValue );
                w.setTextAlign( TextAlign.lookup( currentStringValue ) );
                w.getLayer().batch();
            }

        };

        final PropertyEditorFieldInfo fieldInfo3 = new PropertyEditorFieldInfo( "Stroke colour",
                                                                                String.valueOf( w.getStrokeColour() ),
                                                                                PropertyEditorType.TEXT ) {
            @Override
            public void setCurrentStringValue( final String currentStringValue ) {
                super.setCurrentStringValue( currentStringValue );
                w.setStrokeColour( currentStringValue );
                w.getLayer().batch();
            }
        };

        final PropertyEditorFieldInfo fieldInfo4 = new PropertyEditorFieldInfo( "Fill colour",
                                                                                String.valueOf( w.getFillColour() ),
                                                                                PropertyEditorType.TEXT ) {
            @Override
            public void setCurrentStringValue( final String currentStringValue ) {
                super.setCurrentStringValue( currentStringValue );
                w.setFillColour( currentStringValue );
                w.getLayer().batch();
            }
        };

        final PropertyEditorFieldInfo fieldInfo5 = new WiresComboPropertyEditorFieldInfo( "Font size",
                                                                                          String.valueOf( w.getFontSize() ),
                                                                                          getFontSizeValues() ) {
            @Override
            public void setCurrentStringValue( final String currentStringValue ) {
                super.setCurrentStringValue( currentStringValue );
                try {
                    final double fontSize = Double.parseDouble( currentStringValue );
                    w.setFontSize( fontSize );
                    w.getLayer().batch();
                } catch ( NumberFormatException e ) {
                    //Swallow
                }
            }
        };

        //We're issued with a default TextValidator that demands values are longer than 8 characters; so get rid of it
        fieldInfo1.getValidators().clear();
        fieldInfo2.getValidators().clear();
        fieldInfo3.getValidators().clear();
        fieldInfo4.getValidators().clear();
        fieldInfo5.getValidators().clear();

        fieldInfo3.getValidators().add( new CssHexColourValidator() );
        fieldInfo4.getValidators().add( new CssHexColourValidator() );

        final PropertyEditorCategory attributes = new PropertyEditorCategory( ATTRIBUTES )
                .withField( fieldInfo1 )
                .withField( fieldInfo2 )
                .withField( fieldInfo3 )
                .withField( fieldInfo4 )
                .withField( fieldInfo5 );

        return Lists.newArrayList( attributes );
    }

    private List<String> getTextAlignValues() {
        final List<String> values = new ArrayList<String>();
        for ( TextAlign ta : TextAlign.values() ) {
            values.add( ta.getValue() );
        }
        return values;
    }

    private List<String> getFontSizeValues() {
        return Arrays.asList( new String[]{ "15", "20", "25", "30", "35", "40", "45", "50" } );
    }

}
