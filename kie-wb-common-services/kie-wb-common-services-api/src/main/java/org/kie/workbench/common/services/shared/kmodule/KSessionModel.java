/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.shared.kmodule;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.model.HasListFormComboPanelProperties;
import org.guvnor.common.services.project.model.WorkItemHandlerModel;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class KSessionModel
        implements HasListFormComboPanelProperties {

    private String name;
    private String type = "stateless";
    private ClockTypeOption clockType = ClockTypeOption.REALTIME;
    private boolean theDefault = false;
    private String scope;
    private List<WorkItemHandlerModel> workItemHandelerModels = new ArrayList<WorkItemHandlerModel>();
    private List<ListenerModel> listeners=new ArrayList<ListenerModel>();
    private KSessionLogger logger;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ClockTypeOption getClockType() {
        return clockType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setClockType(ClockTypeOption clockTypeEnum) {
        this.clockType = clockTypeEnum;
    }

    public boolean isDefault() {
        return theDefault;
    }

    @Override
    public void setDefault(boolean theDefault) {
        this.theDefault = theDefault;
    }

    public String getScope() {
        return scope;
    }

    public List<WorkItemHandlerModel> getWorkItemHandelerModels() {
        return workItemHandelerModels;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setLogger(KSessionLogger logger) {
        this.logger = logger;
    }

    public KSessionLogger getLogger() {
        return logger;
    }

    public List<ListenerModel> getListeners() {
        return listeners;
    }

    public void setListeners(List<ListenerModel> listeners) {
        this.listeners = listeners;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        KSessionModel that = ( KSessionModel ) o;

        if ( theDefault != that.theDefault ) {
            return false;
        }
        if ( clockType != that.clockType ) {
            return false;
        }
        if ( listeners != null ? !listeners.equals( that.listeners ) : that.listeners != null ) {
            return false;
        }
        if ( logger != null ? !logger.equals( that.logger ) : that.logger != null ) {
            return false;
        }
        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }
        if ( scope != null ? !scope.equals( that.scope ) : that.scope != null ) {
            return false;
        }
        if ( type != null ? !type.equals( that.type ) : that.type != null ) {
            return false;
        }
        if ( workItemHandelerModels != null ? !workItemHandelerModels.equals( that.workItemHandelerModels ) : that.workItemHandelerModels != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( type != null ? type.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( clockType != null ? clockType.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( theDefault ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( scope != null ? scope.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( workItemHandelerModels != null ? workItemHandelerModels.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( listeners != null ? listeners.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( logger != null ? logger.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
