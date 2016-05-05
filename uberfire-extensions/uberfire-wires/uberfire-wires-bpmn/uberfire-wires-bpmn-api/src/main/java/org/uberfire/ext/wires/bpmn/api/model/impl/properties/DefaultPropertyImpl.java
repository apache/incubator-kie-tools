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
package org.uberfire.ext.wires.bpmn.api.model.impl.properties;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.Property;

@Portable
public class DefaultPropertyImpl implements Property {

    private String id;
    private Type type;
    private String caption;
    private String description;
    private boolean isReadOnly;
    private boolean isOptional;

    public DefaultPropertyImpl( @MapsTo("id") final String id,
                                @MapsTo("type") final Type type,
                                @MapsTo("caption") final String caption,
                                @MapsTo("description") final String description,
                                @MapsTo("isReadOnly") final boolean isReadOnly,
                                @MapsTo("isOptional") final boolean isOptional ) {
        this.id = PortablePreconditions.checkNotNull( "id",
                                                      id );
        this.type = PortablePreconditions.checkNotNull( "type",
                                                        type );
        this.caption = PortablePreconditions.checkNotNull( "caption",
                                                           caption );
        this.description = PortablePreconditions.checkNotNull( "description",
                                                               description );
        this.isReadOnly = isReadOnly;
        this.isOptional = isOptional;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public boolean isOptional() {
        return isOptional;
    }

    @Override
    public Property copy() {
        return new DefaultPropertyImpl( this.id,
                                        this.type,
                                        this.caption,
                                        this.description,
                                        this.isReadOnly,
                                        this.isOptional );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DefaultPropertyImpl ) ) {
            return false;
        }

        DefaultPropertyImpl that = (DefaultPropertyImpl) o;

        if ( isOptional != that.isOptional ) {
            return false;
        }
        if ( isReadOnly != that.isReadOnly ) {
            return false;
        }
        if ( !caption.equals( that.caption ) ) {
            return false;
        }
        if ( !description.equals( that.description ) ) {
            return false;
        }
        if ( !id.equals( that.id ) ) {
            return false;
        }
        if ( !type.equals( that.type ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = ~~result;
        result = 31 * result + type.hashCode();
        result = ~~result;
        result = 31 * result + caption.hashCode();
        result = ~~result;
        result = 31 * result + description.hashCode();
        result = ~~result;
        result = 31 * result + ( isReadOnly ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( isOptional ? 1 : 0 );
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "DefaultPropertyImpl{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", caption='" + caption + '\'' +
                ", description='" + description + '\'' +
                ", isReadOnly=" + isReadOnly +
                ", isOptional=" + isOptional +
                '}';
    }

}
