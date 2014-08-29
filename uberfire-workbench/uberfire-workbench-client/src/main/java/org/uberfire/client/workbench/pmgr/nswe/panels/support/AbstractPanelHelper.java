/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.pmgr.nswe.panels.support;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.workbench.model.CompassPosition;

/**
 * Operations to add or remove child panels of Docking WorkbenchPanel views.
 */
public abstract class AbstractPanelHelper {

    protected final BeanFactory factory;

    protected AbstractPanelHelper( BeanFactory factory ) {
        this.factory = PortablePreconditions.checkNotNull( "factory", factory );
    }

    public abstract void remove( final WorkbenchPanelView panel );

    public static AbstractPanelHelper forPosition( CompassPosition position, BeanFactory factory ) {
        switch ( position ) {
            case NORTH: return new PanelHelperNorth( factory );
            case SOUTH: return new PanelHelperSouth( factory );
            case EAST: return new PanelHelperEast( factory );
            case WEST: return new PanelHelperWest( factory );
            default: throw new IllegalArgumentException( "Unsupported helper type: " + position );
        }
    }
}
