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
package org.uberfire.ext.wires.bpmn.client.commands.impl;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.bpmn.client.commands.Result;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;

/**
 * Result from the execution of a command
 */
public class DefaultResultImpl implements Result {

    private ResultType type;
    private String message;

    public DefaultResultImpl( final ResultType type,
                              final String message ) {
        this.type = PortablePreconditions.checkNotNull( "type",
                                                        type );
        this.message = PortablePreconditions.checkNotNull( "message",
                                                           message );
    }

    @Override
    public ResultType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DefaultResultImpl ) ) {
            return false;
        }

        DefaultResultImpl that = (DefaultResultImpl) o;

        if ( !message.equals( that.message ) ) {
            return false;
        }
        if ( type != that.type ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DefaultResultImpl{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }

}
