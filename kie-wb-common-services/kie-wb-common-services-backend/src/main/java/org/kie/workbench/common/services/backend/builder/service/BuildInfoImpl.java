/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.service;

import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.core.TypeSourceResolver;

public class BuildInfoImpl
        implements BuildInfo {

    private Builder builder;

    public BuildInfoImpl( Builder builder ) {
        this.builder = builder;
    }

    @Override
    public KieModule getKieModule( ) {
        return builder.getKieModule();
    }

    @Override
    public KieModule getKieModuleIgnoringErrors( ) {
        return builder.getKieModuleIgnoringErrors();
    }

    @Override
    public KieModuleMetaData getKieModuleMetaDataIgnoringErrors( ) {
        return builder.getKieModuleMetaDataIgnoringErrors();
    }

    @Override
    public TypeSourceResolver getTypeSourceResolver( KieModuleMetaData kieModuleMetaData ) {
        return builder.getTypeSourceResolver( kieModuleMetaData );
    }

    @Override
    public KieContainer getKieContainer( ) {
        return builder.getKieContainer();
    }

    public Builder getBuilder() {
        return builder;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass( ) != o.getClass( ) ) return false;

        BuildInfoImpl buildInfo = ( BuildInfoImpl ) o;

        return builder != null ? builder.equals( buildInfo.builder ) : buildInfo.builder == null;

    }

    @Override
    public int hashCode( ) {
        return builder != null ? builder.hashCode( ) : 0;
    }
}
