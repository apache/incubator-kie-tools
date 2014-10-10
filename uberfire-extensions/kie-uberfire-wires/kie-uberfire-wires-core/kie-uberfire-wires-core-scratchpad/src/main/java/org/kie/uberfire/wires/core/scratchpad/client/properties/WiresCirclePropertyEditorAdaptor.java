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
package org.kie.uberfire.wires.core.scratchpad.client.properties;

import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.google.common.collect.Lists;
import org.kie.uberfire.properties.editor.model.PropertyEditorCategory;
import org.kie.uberfire.properties.editor.model.PropertyEditorFieldInfo;
import org.kie.uberfire.properties.editor.model.PropertyEditorType;
import org.kie.uberfire.wires.core.api.properties.PropertyEditorAdaptor;
import org.kie.uberfire.wires.core.api.shapes.WiresBaseShape;
import org.kie.uberfire.wires.core.client.properties.DoubleValidator;
import org.kie.uberfire.wires.core.scratchpad.client.shapes.dynamic.WiresCircle;

/**
 * PropertyEditorAdaptor for WiresCircle to extract radius
 */
@ApplicationScoped
public class WiresCirclePropertyEditorAdaptor implements PropertyEditorAdaptor {

    private static final String ATTRIBUTES = "Attributes";

    @Override
    public boolean supports( final WiresBaseShape shape ) {
        return shape instanceof WiresCircle;
    }

    @Override
    public List<PropertyEditorCategory> getProperties( final WiresBaseShape shape ) {
        if ( !supports( shape ) ) {
            return Collections.emptyList();
        }
        final WiresCircle w = (WiresCircle) shape;
        final PropertyEditorFieldInfo fieldInfo1 = new PropertyEditorFieldInfo( "Radius",
                                                                                String.valueOf( w.getRadius() ),
                                                                                PropertyEditorType.NATURAL_NUMBER ) {
            @Override
            public void setCurrentStringValue( final String currentStringValue ) {
                super.setCurrentStringValue( currentStringValue );
                try {
                    final double r = Double.parseDouble( currentStringValue );
                    w.setRadius( r );
                    w.getLayer().draw();
                } catch ( NumberFormatException e ) {
                    //Swallow
                }
            }
        };

        //Setup Validators
        fieldInfo1.getValidators().clear();
        fieldInfo1.getValidators().add( new DoubleValidator() );

        final PropertyEditorCategory attributes = new PropertyEditorCategory( ATTRIBUTES ).withField( fieldInfo1 );

        return Lists.newArrayList( attributes );
    }

}
