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

package org.uberfire.client;

import static com.google.gwt.core.client.ScriptInjector.*;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.client.plugin.RuntimePluginsServiceProxy;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.mvp.ParameterizedCommand;

import com.google.gwt.core.client.ScriptInjector;

@EntryPoint
public class JSEntryPoint {

    @Inject
    private Workbench workbench;

    @Inject
    private RuntimePluginsServiceProxy runtimePluginsService;

    @PostConstruct
    public void init() {
        workbench.addStartupBlocker( JSEntryPoint.class );
    }

    @AfterInitialization
    public void setup() {
        runtimePluginsService.listFrameworksContent( new ParameterizedCommand<Collection<String>>() {
            @Override
            public void execute( final Collection<String> response ) {
                for ( final String s : response ) {
                    ScriptInjector.fromString( s ).setWindow( TOP_WINDOW ).inject();
                }
                runtimePluginsService.listPluginsContent( new ParameterizedCommand<Collection<String>>() {
                    @Override
                    public void execute( final Collection<String> response ) {
                        try {
                            for ( final String s : response ) {
                                ScriptInjector.fromString( s ).setWindow( TOP_WINDOW ).inject();
                            }
                        } finally {
                            workbench.removeStartupBlocker( JSEntryPoint.class );
                        }

                    }
                } );
            }
        } );
    }
}
