/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.ModalSize;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Dependent
public class DependencySelectorPopupViewImpl extends BaseModal implements DependencySelectorPopupView {

    private DependencySelectorPresenter presenter;
    private DependencyListWidget dependencyPagedJarTable;

    @Inject
    private Event<LockRequiredEvent> lockRequired;

    @Inject
    private ManagedInstance<DependencyListWidget> dependencyPagedJarTables;

    @PostConstruct
    public void init() {

        dependencyPagedJarTable = dependencyPagedJarTables.get();

        dependencyPagedJarTable.addOnSelect(parameter -> {
            presenter.onPathSelection(parameter);
            lockRequired.fire(new LockRequiredEvent());
        });

        setTitle("Artifacts");
        setBody(dependencyPagedJarTable);
        setSize(ModalSize.LARGE);

        //Need to refresh the grid to load content after the popup is shown
        addShownHandler(shownEvent -> dependencyPagedJarTable.search());
    }

    @Override
    public void init(final DependencySelectorPresenter presenter) {
        this.presenter = presenter;
    }
}
