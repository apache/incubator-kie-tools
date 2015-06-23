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

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.project.KieProjectService;

public class MockProjectServiceCaller
        implements Caller<KieProjectService> {

    @Override
    public KieProjectService call() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public KieProjectService call( RemoteCallback<?> remoteCallback ) {
        return null;  //TODO: -Rikkola-
    }

    @Override
    public KieProjectService call( RemoteCallback<?> remoteCallback,
                                   ErrorCallback<?> errorCallback ) {
        return null;  //TODO: -Rikkola-
    }
}
