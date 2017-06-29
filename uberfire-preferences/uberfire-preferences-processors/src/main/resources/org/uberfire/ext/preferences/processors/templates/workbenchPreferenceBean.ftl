/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package ${targetPackage};

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.annotation.Generated;

import org.uberfire.annotations.Customizable;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.bean.BasePreferenceBean;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
@Generated("org.uberfire.ext.preferences.processors.WorkbenchPreferenceProcessor")
/*
* WARNING! This class is generated. Do not modify.
*/
public class ${targetClassName} extends ${sourceClassName} implements BasePreferenceBean<${sourceClassName}> {

    private PreferenceBeanStore store;
    
    private PreferenceScopeResolutionStrategy resolutionStrategy;

    @Inject
    public ${targetClassName}( final PreferenceBeanStore store,
                               @Customizable final PreferenceScopeResolutionStrategy resolutionStrategy ) {
        this.store = store;
        this.resolutionStrategy = resolutionStrategy;
    }

    @Override
    public void load() {
        load( ( ParameterizedCommand<Throwable> ) null );
    }

    @Override
    public void load( final ParameterizedCommand<Throwable> errorCallback ) {
        load( ( ParameterizedCommand<${sourceClassName}> ) null, errorCallback );
    }

    @Override
    public void load( final ParameterizedCommand<${sourceClassName}> successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        final ${targetClassName} preferenceBean = this;

        store.load( new ${sourceClassName}PortableGeneratedImpl(),
                    getLoadSuccessCallback( successCallback ),
                    errorCallback );
    }

    @Override
    public void load( final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy ) {
        load( customScopeResolutionStrategy, null );
    }

    @Override
    public void load( final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        load( customScopeResolutionStrategy, null, errorCallback );
    }

    @Override
    public void load( final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                      final ParameterizedCommand<${sourceClassName}> successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        final ${targetClassName} preferenceBean = this;

        store.load( new ${sourceClassName}PortableGeneratedImpl(),
                    customScopeResolutionStrategy,
                    getLoadSuccessCallback( successCallback ),
                    errorCallback );
    }

    private ParameterizedCommand<BasePreferencePortable<${sourceClassName}>> getLoadSuccessCallback( final ParameterizedCommand<${sourceClassName}> successCallback ) {
        final ${sourceClassName}BeanGeneratedImpl preferenceBean = this;

        return new ParameterizedCommand<BasePreferencePortable<${sourceClassName}>>() {
            @Override
            public void execute( final BasePreferencePortable<${sourceClassName}> portablePreference ) {
                copy( (${sourceClassName}PortableGeneratedImpl) portablePreference, preferenceBean );
                if ( successCallback != null ) {
                    successCallback.execute( preferenceBean );
                }
            }
        };
    }

    private void copy( final ${sourceClassName} from,
                       final ${sourceClassName} to ) {
    <#list properties as property>
        <#if property.isPrivateAccess()>
        to.set${property.getCapitalizedFieldName()}( from.${property.getFieldAccessorCommand()} );
        <#else>
        to.${property.getFieldName()} = from.${property.getFieldName()};
        </#if>
    </#list>
    }

    @Override
    public void save() {
        save( ( ParameterizedCommand<Throwable> ) null );
    }

    @Override
    public void save( final ParameterizedCommand<Throwable> errorCallback ) {
        save( ( Command ) null, errorCallback );
    }

    @Override
    public void save( final Command successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        store.save( createPortableCopy(),
                    successCallback,
                    errorCallback );
    }

    @Override
    public void save( final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy ) {
        save( customScopeResolutionStrategy, null );
    }

    @Override
    public void save( final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        save( customScopeResolutionStrategy, null, errorCallback );
    }

    @Override
    public void save( final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy,
                      final Command successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        store.save( createPortableCopy(),
                    customScopeResolutionStrategy,
                    successCallback,
                    errorCallback );
    }

    @Override
    public void save( final PreferenceScope customScope ) {
        save( customScope, null );
    }

    @Override
    public void save( final PreferenceScope customScope,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        save( customScope, null, errorCallback );
    }

    @Override
    public void save( final PreferenceScope customScope,
                      final Command successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        store.save( createPortableCopy(),
                    customScope,
                    successCallback,
                    errorCallback );
    }

    private BasePreferencePortable<${sourceClassName}> createPortableCopy() {
        ${sourceClassName}PortableGeneratedImpl portablePreference = new ${sourceClassName}PortableGeneratedImpl();

        copy( this, portablePreference );

        return portablePreference;
    }
}
