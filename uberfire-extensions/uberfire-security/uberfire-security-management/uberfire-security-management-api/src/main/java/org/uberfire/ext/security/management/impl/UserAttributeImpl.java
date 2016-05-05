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

package org.uberfire.ext.security.management.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.security.management.api.UserManager;

/**
 * <p>Default portable User attribute implementation class.</p>
 * @since 0.8.0
 */
@Portable
public class UserAttributeImpl implements UserManager.UserAttribute {

    private String name;
    private boolean isEditable;
    private boolean isMandatory;
    private String defaultValue;

    public UserAttributeImpl( @MapsTo("name") String name,
                              @MapsTo("isMandatory") boolean isMandatory,
                              @MapsTo("isEditable") boolean isEditable,
                              @MapsTo("defaultValue") String defaultValue ) {
        this.name = name;
        this.isMandatory = isMandatory;
        this.isEditable = isEditable;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isMandatory() {
        return isMandatory;
    }

    @Override
    public boolean isEditable() {
        return isEditable;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( name == null ) {
            return false;
        }

        try {
            UserManager.UserAttribute d = (UserManager.UserAttribute) obj;
            return name.equals( d.getName() );
        } catch ( ClassCastException e ) {
            return false;
        }
    }
}
