/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.mvp;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.client.workbench.services.WorkbenchServices;

@ApplicationScoped
public class DefaultPlaceResolver {

    @Inject
    private Caller<WorkbenchServices> wbServices;

    private Map<String, String> properties;

    @AfterInitialization
    public void init() {
        wbServices.call(
                new RemoteCallback<Map<String, String>>() {
                    @Override
                    public void callback( Map<String, String> properties ) {
                        DefaultPlaceResolver.this.properties = properties;
                    }
                } ).loadDefaultEditorsMap();
    }

    public String getEditorId( String key ) {
        return properties.get( key );
    }

    public void saveDefaultEditor( final String fullIdentifier,
                                   final String signatureId ) {
        properties.put( fullIdentifier, signatureId );

        wbServices.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void response ) {

            }
        } ).saveDefaultEditors( properties );
    }
}
