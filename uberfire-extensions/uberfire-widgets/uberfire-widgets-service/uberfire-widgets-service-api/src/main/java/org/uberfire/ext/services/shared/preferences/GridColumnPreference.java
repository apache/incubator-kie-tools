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
package org.uberfire.ext.services.shared.preferences;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class GridColumnPreference implements Comparable {

    private String name;
    private Integer position;
    private String width;

    public GridColumnPreference( @MapsTo("name") String name,
                                 @MapsTo("position") Integer position,
                                 @MapsTo("width") String width ) {
        this.name = name;
        this.position = position;
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public Integer getPosition() {
        return position;
    }

    public String getWidth() {
        return width;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setPosition( Integer position ) {
        this.position = position;
    }

    public void setWidth( String width ) {
        this.width = width;
    }

    @Override
    public int compareTo( Object o ) {
        if ( !( o instanceof GridColumnPreference ) ) {
            return 0;
        }
        if ( position < ( (GridColumnPreference) o ).getPosition() ) {
            return -1;
        } else if ( position > ( (GridColumnPreference) o ).getPosition() ) {
            return 1;
        } else {
            return 0;
        }

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + ( this.name != null ? this.name.hashCode() : 0 );
        hash = 79 * hash + ( this.position != null ? this.position.hashCode() : 0 );
        hash = 79 * hash + ( this.width != null ? this.width.hashCode() : 0 );
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final GridColumnPreference other = (GridColumnPreference) obj;
        if ( ( this.name == null ) ? ( other.name != null ) : !this.name.equals( other.name ) ) {
            return false;
        }
        if ( this.position != other.position && ( this.position == null || !this.position.equals( other.position ) ) ) {
            return false;
        }
        if ( ( this.width == null ) ? ( other.width != null ) : !this.width.equals( other.width ) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GridColumnPreference{" + "name=" + name + ", position=" + position + ", width=" + width + '}';
    }

}
