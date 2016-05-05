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
package org.uberfire.ext.wires.core.client.palette;

import java.io.Serializable;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;

/**
 * A PaletteShape is an icon and text, bound by a rectangle, used in the Palette.
 */
public class PaletteShape extends Group implements Serializable {

    private static final long serialVersionUID = -6555009991474610157L;

    /**
     * Add a bounding Rectangle to the PaletteShape.
     * @param bounding If null this parameter is ignored
     */
    public void setBounding( final Rectangle bounding ) {
        if ( bounding != null ) {
            add( bounding );
        }
    }

    /**
     * Add a Group to the PaletteShape.
     * @param group If null this parameter is ignored
     */
    public void setGroup( final Group group ) {
        if ( group != null ) {
            add( group );
        }
    }

    /**
     * Add Text to the PaletteShape.
     * @param description If null this parameter is ignored
     */
    public void setDescription( final Text description ) {
        if ( description != null ) {
            add( description );
        }
    }

}
