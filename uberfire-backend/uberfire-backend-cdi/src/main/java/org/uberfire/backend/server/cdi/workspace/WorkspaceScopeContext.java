/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.cdi.workspace;

import java.lang.annotation.Annotation;
import java.util.NoSuchElementException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.cdi.workspace.Workspace;
import org.uberfire.rpc.SessionInfo;

/**
 * Workspace context.
 * Uses {@link WorkspaceManager} to create beans. Those beans must be annotated with
 * {@link WorkspaceScoped} annotation. Every bean has only one instance per workspace.
 */
public class WorkspaceScopeContext implements Context {

    private static Logger logger = LoggerFactory.getLogger( WorkspaceScopeContext.class );
    private final BeanManager beanManager;

    public WorkspaceScopeContext( BeanManager beanManager ) {
        this.beanManager = beanManager;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return WorkspaceScoped.class;
    }

    @Override
    public <T> T get( final Contextual<T> contextual,
                      final CreationalContext<T> creationalContext ) {

        Bean<T> bean = getBean( contextual );
        Workspace workspace = this.getWorkspaceManager().getOrCreateWorkspace( getWorkspaceName() );
        final T instance = getWorkspaceManager().getBean( workspace, bean.getBeanClass().getSimpleName() );

        if ( instance == null ) {
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Creating Bean <<{}>> with creational context for workspace <<{}>>", bean.getBeanClass(), workspace.getName() );
            }
            final T created = bean.create( creationalContext );
            this.getWorkspaceManager().putBean( workspace, bean.getBeanClass().getSimpleName(), created );
            return created;
        } else {
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Bean <<{}>> found for workspace <<{}>>", bean.getBeanClass(), workspace.getName() );
            }
            return instance;
        }

    }

    @Override
    public <T> T get( final Contextual<T> contextual ) {
        Bean<T> bean = getBean( contextual );
        Workspace workspace = this.getWorkspaceManager().getOrCreateWorkspace( getWorkspaceName() );
        if ( logger.isDebugEnabled() ) {
            logger.debug( "Getting Bean <<{}>> for workspace <<{}>>", bean.getBeanClass(), workspace.getName() );
        }
        return this.getWorkspaceManager().getBean( workspace, bean.getBeanClass().toString() );
    }

    private String getWorkspaceName() {
        try {
            return this.getSessionInfo().getIdentity().getIdentifier();
        } catch ( NoSuchElementException e ) {
            return "default";
        }
    }

    @Override
    public boolean isActive() {
        return true;
    }

    private <T> Bean<T> getBean( final Contextual contextual ) {
        return (Bean<T>) contextual;

    }

    protected SessionInfo getSessionInfo() {
        final Bean<SessionInfo> bean = (Bean<SessionInfo>) this.beanManager.getBeans( SessionInfo.class ).iterator().next();
        final CreationalContext<SessionInfo> creationalContext = this.beanManager.createCreationalContext( bean );
        return (SessionInfo) this.beanManager.getReference( bean, SessionInfo.class, creationalContext );
    }

    protected WorkspaceManager getWorkspaceManager() {
        final Bean<WorkspaceManager> bean = (Bean<WorkspaceManager>) this.beanManager.getBeans( WorkspaceManager.class ).iterator().next();
        final CreationalContext<WorkspaceManager> creationalContext = this.beanManager.createCreationalContext( bean );
        return (WorkspaceManager) this.beanManager.getReference( bean, WorkspaceManager.class, creationalContext );
    }
}
