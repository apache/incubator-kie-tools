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

package org.drools.guvnor.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import org.drools.guvnor.client.perspective.monitoring.MonitoringPerspectivePlace;
import org.drools.guvnor.client.perspective.monitoring.MonitoringPerspectivePresenter;
import org.drools.guvnor.client.perspective.workspace.DefaultActivity;
import org.drools.guvnor.client.perspective.workspace.DefaultPlace;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class PerspectiveActivityMapper implements ActivityMapper {

    @Inject
    private IOCBeanManager manager;

    private Activity actual = null;

    public Activity getActivity(final Place place) {

        if (place instanceof DefaultPlace) {
            this.actual = manager.lookupBean(DefaultActivity.class).getInstance();
        }

        if (place instanceof MonitoringPerspectivePlace) {
            this.actual = manager.lookupBean(MonitoringPerspectivePresenter.class).getInstance();
        }

        return this.actual;
    }
}
