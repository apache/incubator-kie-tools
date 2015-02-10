/*
 * Copyright 2015 JBoss Inc
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
package org.uberfire.ext.wires.bpmn.api.model.impl.nodes;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.api.model.Content;

/**
 * Content of a BpmnNode
 */
@Portable
public class DefaultContentImpl implements Content {

    private String title;
    private String description;

    //Errai marshalling
    public DefaultContentImpl() {
    }

    public DefaultContentImpl( final String title,
                               final String description ) {
        this.title = PortablePreconditions.checkNotNull( "title",
                                                         title );
        this.description = PortablePreconditions.checkNotNull( "description",
                                                               description );
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DefaultContentImpl ) ) {
            return false;
        }

        DefaultContentImpl that = (DefaultContentImpl) o;

        if ( !description.equals( that.description ) ) {
            return false;
        }
        if ( !title.equals( that.title ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = ~~result;
        result = 31 * result + description.hashCode();
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "DefaultContentImpl{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
