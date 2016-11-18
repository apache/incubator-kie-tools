/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.shapes.client.view.glyph;

import com.ait.lienzo.client.core.shape.Group;
import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.AbstractGlyphBuilder;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.shapes.client.factory.PictureProvidersManager;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class PictureGlyphBuilder extends AbstractGlyphBuilder<Group, PictureGlyphDef<Object, ?>> {

    private static Logger LOGGER = Logger.getLogger( PictureGlyphBuilder.class.getName() );

    private final PictureProvidersManager pictureProvidersManager;

    protected PictureGlyphBuilder() {
        this( null );
    }

    @Inject
    public PictureGlyphBuilder( final PictureProvidersManager pictureProvidersManager ) {
        this.pictureProvidersManager = pictureProvidersManager;
    }

    @Override
    public Class<?> getType() {
        return PictureGlyphDef.class;
    }

    @Override
    public Glyph<Group> build() {
        final Object source = glyphDefinition.getSource( type );
        final SafeUri uri = pictureProvidersManager.getUri( source );
        if ( null != uri ) {
            return new PictureGlyph( uri.asString(), width, height );
        }
        LOGGER.log( Level.WARNING, "No picture uri resolved for picture source [" + source + "]" );
        return null;
    }

}
