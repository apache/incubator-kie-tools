/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class DependencySelectorPopupViewImpl
        extends BaseModal
        implements DependencySelectorPopupView {

    private DependencySelectorPresenter presenter;
    private DependencyListWidget dependencyPagedJarTable;
    
    @Inject
    private javax.enterprise.event.Event<LockRequiredEvent> lockRequired;

    @AfterInitialization
    public void init() {
        dependencyPagedJarTable = IOC.getBeanManager().lookupBean( DependencyListWidget.class ).getInstance();

        dependencyPagedJarTable.addOnSelect( new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                presenter.onPathSelection( parameter );
                lockRequired.fire( new LockRequiredEvent() );
            }
        } );

        setTitle( "Artifacts" );
        add( new ModalBody() {{
            add( dependencyPagedJarTable );
        }} );
        setPixelSize( 800,
                      500 );

        //Need to refresh the grid to load content after the popup is shown
        addShownHandler( new ModalShownHandler() {

            @Override
            public void onShown( final ModalShownEvent shownEvent ) {
                dependencyPagedJarTable.refresh();
            }

        } );
    }

    @Override
    public void init( final DependencySelectorPresenter presenter ) {
        this.presenter = presenter;
    }

}
