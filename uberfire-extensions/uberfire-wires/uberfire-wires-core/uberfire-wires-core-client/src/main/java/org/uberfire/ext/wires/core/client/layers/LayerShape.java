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
package org.uberfire.ext.wires.core.client.layers;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;

public class LayerShape extends Group {

    /**
     * Add a bounding Rectangle to the LayerShape.
     * @param bounding If null this parameter is ignored
     */
    public void setBounding( final Rectangle bounding ) {
        if ( bounding != null ) {
            add( bounding );
        }
    }

    /**
     * Add a Group to the LayerShape.
     * @param group If null this parameter is ignored
     */
    public void setGroup( final Group group ) {
        if ( group != null ) {
            add( group );
        }
    }

    /**
     * Add Text to the LayerShape.
     * @param description If null this parameter is ignored
     */
    public void setDescription( final Text description ) {
        if ( description != null ) {
            add( description );
        }
    }

}
