/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.command.impl;

import java.util.Collection;
import java.util.LinkedList;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.stunner.core.command.CommandResult;

@NonPortable
public abstract class CommandResultBuilder<V> {

    protected static final String RESULT_SUCCESS = "Success";
    protected static final String RESULT_FAILED = "Failed";

    private CommandResult.Type type;
    private String message = RESULT_SUCCESS;
    private final Collection<V> violations = new LinkedList<>();

    public abstract boolean isError( final V violation );

    public abstract String getMessage( final V violation );

    public CommandResultBuilder() {
    }

    public CommandResultBuilder( final Collection<V> violations ) {
        this.violations.addAll( violations );
    }

    public CommandResultBuilder<V> addViolation( final V violation ) {
        this.violations.add( violation );
        return this;
    }

    public CommandResultBuilder<V> addViolations( final Collection<V> violations ) {
        this.violations.addAll( violations );
        return this;
    }

    public void setType( final CommandResult.Type type ) {
        this.type = type;
    }

    public void setMessage( final String message ) {
        this.message = message;
    }

    public CommandResult<V> build() {
        if ( null != violations && !violations.isEmpty() ) {
            StringBuilder messages = new StringBuilder();
            String message = null;
            int c = 0;
            for ( final V violation : violations ) {
                if ( isError( violation ) ) {
                    message = getMessage( violation );
                    messages.append( " {" ).append( message ).append( " } " );
                    c++;
                }
            }
            if ( c > 1 ) {
                message = "Found " + c + " violations - " + messages.toString();
            }
            if ( c > 0 ) {
                this.message = message;
                this.type = CommandResult.Type.ERROR;
            }
        }
        // Default values.
        this.type = this.type == null ? CommandResult.Type.INFO : this.type;
        this.message = ( this.message == null || this.message.trim().length() == 0 )
                ? RESULT_SUCCESS : this.message;
        return new CommandResultImpl<>( this.type,
                                        this.message,
                                        this.violations );
    }
}
