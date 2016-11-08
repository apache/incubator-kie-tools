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

package org.kie.workbench.common.forms.editor.client.handler.formModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.kie.workbench.common.forms.model.FormModel;
import org.uberfire.backend.vfs.Path;

@Dependent
public class FormModelsPresenter implements IsWidget {

    protected FormModelsView view;

    protected List<FormModelCreationView> creationViews = new ArrayList();

    @Inject
    public FormModelsPresenter( FormModelsView view ) {
        this.view = view;
    }

    @PostConstruct
    protected void init() {
        creationViews = registerCreationViews();

        creationViews.sort( ( o1, o2 ) -> o1.getPriority() - o2.getPriority() );

        view.setCreationViews( creationViews );
    }

    protected List<FormModelCreationView> registerCreationViews() {
        List<FormModelCreationView> creationViews = new ArrayList();

        Collection<SyncBeanDef<FormModelCreationView>> viewDefs = IOC.getBeanManager().lookupBeans( FormModelCreationView.class );

        viewDefs.forEach( viewDef -> creationViews.add( viewDef.newInstance() ) );

        return creationViews;
    }

    public void initialize( Path projectPath ) {
        view.reset();
        creationViews.forEach( view -> {
            view.reset();
            view.init( projectPath );
        } );
    }


    public boolean isValid() {
        return view.isValid();
    }

    public FormModel getFormModel() {
        return view.getFormModel();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
