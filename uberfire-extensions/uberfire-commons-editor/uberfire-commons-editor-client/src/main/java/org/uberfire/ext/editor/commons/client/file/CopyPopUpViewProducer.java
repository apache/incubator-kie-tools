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

package org.uberfire.ext.editor.commons.client.file;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpView;

@Startup(value = StartupType.BOOTSTRAP, priority = -1)
@ApplicationScoped
public class CopyPopUpViewProducer {

    @Inject
    private Instance<CopyPopUpPresenter.View> copyPopUpViewInstance;

    @Produces
    @Customizable
    public CopyPopUpPresenter.View copyPopUpViewProducer() {
        if ( this.copyPopUpViewInstance.isUnsatisfied() ) {
            return new CopyPopUpView();
        }

        return this.copyPopUpViewInstance.get();
    }
}
