/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.uberfire.client.views.pfly.modal;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * A popup that shows an error message
 */
@Dependent
public class ErrorPopupView extends Composite implements ErrorPopupPresenter.View {

    @Inject
    private Instance<Bs3Modal> modalFactory;

    @Override
    public void showMessage( final String msg,
                             final Command afterShow,
                             final Command afterClose ) {

        Bs3Modal modal = modalFactory.get();
        modal.setTitle( "Error" );
        modal.setContent( new HTML( msg ) );
        modal.show( afterShow, afterClose );
    }

}
