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

package org.kie.workbench.common.screens.datamodeller.model.persistence;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PersistenceDescriptorModel {

    String version;

    PersistenceUnitModel persistenceUnit;

    public PersistenceDescriptorModel() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
    }

    public PersistenceUnitModel getPersistenceUnit() {
        return persistenceUnit;
    }

    public void setPersistenceUnit( PersistenceUnitModel persistenceUnit ) {
        this.persistenceUnit = persistenceUnit;
    }

    @Override public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PersistenceDescriptorModel that = ( PersistenceDescriptorModel ) o;

        if ( version != null ? !version.equals( that.version ) : that.version != null ) {
            return false;
        }
        return !( persistenceUnit != null ? !persistenceUnit.equals( that.persistenceUnit ) : that.persistenceUnit != null );

    }

    @Override public int hashCode() {
        int result = version != null ? version.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( persistenceUnit != null ? persistenceUnit.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
