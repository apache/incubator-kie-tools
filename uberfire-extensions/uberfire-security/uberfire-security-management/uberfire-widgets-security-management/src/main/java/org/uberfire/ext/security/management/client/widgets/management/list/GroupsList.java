/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.list;

import org.jboss.errai.security.shared.api.Group;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * <p>Presenter class for listing groups, includes the super-type pagination features.</p>
 */
@Dependent
public class GroupsList extends EntitiesPagedList<Group> {

    ClientUserSystemManager userSystemManager;
    
    @Inject
    public GroupsList(LoadingBox loadingBox, View view, ClientUserSystemManager userSystemManager) {
        super(loadingBox, view);
        this.userSystemManager = userSystemManager;
    }

}
