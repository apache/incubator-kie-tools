/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets.business;

import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;

/**
 * Business View Presenter definition
 */
public interface BusinessViewPresenter {

    public void handleIncompleteContext();

    public void groupSelected( final Group group );

    public void repositorySelected( final Repository repository );

    public void projectSelected( final Project project );

    public void packageSelected( final Package pkg );

    public void itemSelected( final Item item );

}
