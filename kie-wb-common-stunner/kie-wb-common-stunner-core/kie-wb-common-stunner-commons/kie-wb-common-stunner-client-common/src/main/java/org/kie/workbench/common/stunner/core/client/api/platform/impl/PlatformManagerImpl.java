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

package org.kie.workbench.common.stunner.core.client.api.platform.impl;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.core.client.api.platform.ClientPlatform;
import org.kie.workbench.common.stunner.core.client.api.platform.PlatformManager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class PlatformManagerImpl implements PlatformManager {

    private static Logger LOGGER = Logger.getLogger( PlatformManagerImpl.class.getName() );

    SyncBeanManager beanManager;
    private final List<ClientPlatform> supportedPlatforms = new LinkedList<>();

    protected PlatformManagerImpl() {
        this( null );
    }

    @Inject
    public PlatformManagerImpl( final SyncBeanManager beanManager ) {
        this.beanManager = beanManager;
    }

    @PostConstruct
    public void init() {
        // Client platforms.
        Collection<SyncBeanDef<ClientPlatform>> sets = beanManager.lookupBeans( ClientPlatform.class );
        for ( SyncBeanDef<ClientPlatform> set : sets ) {
            ClientPlatform platform = set.getInstance();
            supportedPlatforms.add( platform );
        }

    }

    @Override
    public Iterable<ClientPlatform> getSupportedPlatforms() {
        return Collections.unmodifiableCollection( supportedPlatforms );
    }

    @Override
    public ClientPlatform getCurrentPlatform() {
        final String platform = Window.Navigator.getPlatform();
        for ( final ClientPlatform clientPlatform : supportedPlatforms ) {
            if ( clientPlatform.supports( platform ) ) {
                return clientPlatform;

            }

        }
        log( Level.SEVERE, "No client plaform found for [" + platform + "]" );
        return null;
    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
