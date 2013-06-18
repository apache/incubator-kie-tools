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

package org.kie.workbench.common.widgets.client.handlers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.context.KieWorkbenchContext;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.PackageChangeEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberView;

@ApplicationScoped
public class NewResourcePresenter {

    public interface View
            extends
            UberView<NewResourcePresenter> {

        void show();

        void hide();

        void setActiveHandler( final NewResourceHandler activeHandler );

        void setHandlers( final List<NewResourceHandler> handlers );

        String getFileName();

        void enableHandler( final NewResourceHandler handler,
                            final boolean enabled );

    }

    @Inject
    protected KieWorkbenchContext context;

    @Inject
    private IOCBeanManager iocBeanManager;

    @Inject
    private Caller<ProjectService> projectService;

    @Inject
    private View view;

    private NewResourceHandler activeHandler = null;

    private final List<NewResourceHandler> handlers = new LinkedList<NewResourceHandler>();

    @PostConstruct
    private void setup() {
        view.init( this );
        final Collection<IOCBeanDef<NewResourceHandler>> handlerBeans = iocBeanManager.lookupBeans( NewResourceHandler.class );
        for ( IOCBeanDef<NewResourceHandler> handlerBean : handlerBeans ) {
            final NewResourceHandler handler = handlerBean.getInstance();
            handlers.add( handler );
        }
        view.setHandlers( handlers );
    }

    public void selectedPackageChanged( @Observes final PackageChangeEvent event ) {
        final Package pkg = event.getPackage();
        enableNewResourceHandlers( pkg );
    }

    private void enableNewResourceHandlers( final Package pkg ) {
        final Path path = pkg.getPackageMainResourcesPath();
        for ( final NewResourceHandler handler : this.handlers ) {
            handler.acceptPath( path,
                                new Callback<Boolean, Void>() {
                                    @Override
                                    public void onFailure( Void reason ) {
                                        // Nothing to do there right now.
                                    }

                                    @Override
                                    public void onSuccess( final Boolean result ) {
                                        if ( result != null ) {
                                            view.enableHandler( handler,
                                                                result );
                                        }
                                    }
                                } );

        }
    }

    public void show() {
        show( null );
    }

    public void show( final NewResourceHandler handler ) {
        activeHandler = handler;
        if ( activeHandler == null ) {
            activeHandler = handlers.get( 0 );
        }
        view.show();
        view.setActiveHandler( activeHandler );
    }

    void setActiveHandler( final NewResourceHandler handler ) {
        activeHandler = handler;
    }

    public void makeItem() {
        if ( activeHandler != null ) {
            if ( activeHandler.validate() ) {
                activeHandler.create( context.getActivePackage(),
                                      view.getFileName(),
                                      NewResourcePresenter.this );
            }
        }
    }

    public void complete() {
        view.hide();
    }

}
