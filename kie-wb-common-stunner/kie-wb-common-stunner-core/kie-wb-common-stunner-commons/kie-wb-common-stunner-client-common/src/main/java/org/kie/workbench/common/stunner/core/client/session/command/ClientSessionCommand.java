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

package org.kie.workbench.common.stunner.core.client.session.command;

import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.uberfire.mvp.Command;

public interface ClientSessionCommand<S extends ClientSession> {

    interface Callback<T> {

        void onSuccess( T result );

        void onError( ClientRuntimeError error );

    }

    ClientSessionCommand<S> bind( S session );

    ClientSessionCommand<S> listen( Command statusCallback );

    <T> void execute( Callback<T> callback );

    boolean isEnabled();

    void unbind();

}
