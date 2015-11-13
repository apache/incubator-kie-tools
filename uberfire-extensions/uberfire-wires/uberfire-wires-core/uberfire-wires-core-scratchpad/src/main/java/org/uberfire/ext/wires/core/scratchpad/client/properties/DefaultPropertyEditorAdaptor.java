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

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.google.common.collect.Lists;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.ext.wires.core.api.properties.PropertyEditorAdaptor;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.properties.DoubleValidator;

/**
 * Default PropertyEditorAdaptor to extract X and Y
 */
@ApplicationScoped
public class DefaultPropertyEditorAdaptor implements PropertyEditorAdaptor {

    private static final String POSITION_NODE = "Position Node";

    @Override
    public boolean supports( final WiresBaseShape shape ) {
        return true;
    }

    @Override
    public List<PropertyEditorCategory> getProperties( final WiresBaseShape shape ) {
        final PropertyEditorFieldInfo fieldInfo1 = new PropertyEditorFieldInfo( "X",
                                                                                String.valueOf( shape.getX() ),
                                                                                PropertyEditorType.NATURAL_NUMBER ) {
            @Override
            public void setCurrentStringValue( final String currentStringValue ) {
                super.setCurrentStringValue( currentStringValue );
                try {
                    final double x = Double.parseDouble( currentStringValue );
                    shape.setX( x );
                    shape.getLayer().batch();
                } catch ( NumberFormatException e ) {
                    //Swallow
                }
            }
        };
        final PropertyEditorFieldInfo fieldInfo2 = new PropertyEditorFieldInfo( "Y",
                                                                                String.valueOf( shape.getY() ),
                                                                                PropertyEditorType.NATURAL_NUMBER ) {
            @Override
            public void setCurrentStringValue( final String currentStringValue ) {
                super.setCurrentStringValue( currentStringValue );
                try {
                    final double y = Double.parseDouble( currentStringValue );
                    shape.setY( y );
                    shape.getLayer().batch();
                } catch ( NumberFormatException e ) {
                    //Swallow
                }
            }
        };

        //Setup Validators
        fieldInfo1.getValidators().clear();
        fieldInfo2.getValidators().clear();
        fieldInfo1.getValidators().add( new DoubleValidator() );
        fieldInfo2.getValidators().add( new DoubleValidator() );

        final PropertyEditorCategory position = new PropertyEditorCategory( POSITION_NODE ).withField( fieldInfo1 ).withField( fieldInfo2 );

        return Lists.newArrayList( position );
    }

}
