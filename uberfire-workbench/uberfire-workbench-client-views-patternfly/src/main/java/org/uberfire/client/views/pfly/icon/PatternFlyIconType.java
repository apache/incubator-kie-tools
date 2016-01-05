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

package org.uberfire.client.views.pfly.icon;

import com.google.gwt.dom.client.Style;
import org.gwtbootstrap3.client.ui.base.helper.EnumHelper;
import org.gwtbootstrap3.client.ui.constants.Type;

/**
 * You can use the patternfly icons
 */
public enum PatternFlyIconType implements Type,
                                          Style.HasCssName {
    ADD_CIRCLE_O( "pficon-add-circle-o" ),
    CLOSE( "pficon-close" ),
    CLUSTER( "pficon-cluster" ),
    CONTAINER_NODE( "pficon-container-node" ),
    DELETE( "pficon-delete" ),
    EDIT( "pficon-edit" ),
    ERROR_CIRCLE_O( "pficon-error-circle-o" ),
    EXPORT( "pficon-export" ),
    FLAG( "pficon-flag" ),
    FOLDER_CLOSE( "pficon-folder-close" ),
    FOLDER_OPEN( "pficon-folder-open" ),
    HELP( "pficon-help" ),
    HOME( "pficon-home" ),
    HISTORY( "pficon-history" ),
    IMAGE( "pficon-image" ),
    IMPORT( "pficon-import" ),
    INFO( "pficon-info" ),
    KUBERNETES( "pficon-kubernetes" ),
    OK( "pficon-ok" ),
    OPENSHIFT( "pficon-openshift" ),
    PRINT( "pficon-print" ),
    PROJECT( "pficon-project" ),
    REGISTRY( "pficon-registry" ),
    REPLICATOR( "pficon-replicator" ),
    RESTART( "pficon-restart" ),
    ROUTE( "pficon-route" ),
    RUNNING( "pficon-running" ),
    SAVE( "pficon-save" ),
    SCREEN( "pficon-screen" ),
    SERVICE( "pficon-service" ),
    SETTINGS( "pficon-settings" ),
    USER( "pficon-user" ),
    USERS( "pficon-users" ),
    WARNING_TRIANGLE_O( "pficon-warning-triangle-o" );

    private final String cssClass;

    PatternFlyIconType( final String cssClass ) {
        this.cssClass = cssClass;
    }

    @Override
    public String getCssName() {
        return cssClass;
    }

    public static PatternFlyIconType fromStyleName( final String styleName ) {
        return EnumHelper.fromStyleName( styleName, PatternFlyIconType.class, null );
    }

}
