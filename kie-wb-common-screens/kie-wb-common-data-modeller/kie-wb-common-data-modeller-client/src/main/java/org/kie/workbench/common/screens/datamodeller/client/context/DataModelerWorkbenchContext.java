/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.context;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;

@ApplicationScoped
public class DataModelerWorkbenchContext {

    @Inject
    private Event<DataModelerWorkbenchContextChangeEvent> dataModelerWBContextEvent;

    private DataModelerContext activeContext;

    @Inject
    private Caller<DataModelerService> modelerService;

    /**
     * Definition of the annotations that can be managed from the domain editors. This definitions remains constant
     * once application has been started and it's desirable to have them in client side for optimization purposes.
     * And of course this is a lightweight piece of information.
     */
    private Map<String, AnnotationDefinition> annotationDefinitions;

    /**
     * Definition of the property types like String, BigDecimal, that can be managed from the domain editors.
     * This definitions remains constant once application has been started and it's desirable to have them in client
     * side for optimization purposes. And of course this is a lightweight piece of information.
     */
    private List<PropertyType> propertyTypes;

    public DataModelerWorkbenchContext() {
    }

    public void setActiveContext( DataModelerContext activeContext ) {
        this.activeContext = activeContext;
        dataModelerWBContextEvent.fire( new DataModelerWorkbenchContextChangeEvent() );
    }

    public DataModelerContext getActiveContext() {
        return activeContext;
    }

    public void clearContext() {
        this.activeContext = null;
        dataModelerWBContextEvent.fire( new DataModelerWorkbenchContextChangeEvent()  );
    }

    public Map<String, AnnotationDefinition> getAnnotationDefinitions() {
        return annotationDefinitions;
    }

    public List<PropertyType> getPropertyTypes() {
        return propertyTypes;
    }

    public void setAnnotationDefinitions( Map<String, AnnotationDefinition> annotationDefinitions ) {
        this.annotationDefinitions = annotationDefinitions;
    }

    public void setPropertyTypes( List<PropertyType> propertyTypes ) {
        this.propertyTypes = propertyTypes;
    }

    public boolean isTypesInfoLoaded() {
        return propertyTypes != null && annotationDefinitions != null;
    }
}
