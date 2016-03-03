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

package org.uberfire.ext.plugin.client.validation;

import java.util.Collection;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.plugin.client.info.PluginsInfo;
import org.uberfire.ext.plugin.model.Activity;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.service.PluginServices;

@ApplicationScoped
public class PluginNameValidator implements Validator {

    @Inject
    private PluginsInfo pluginsInfo;

    @Inject
    private DefaultFileNameValidator defaultFileNameValidator;

    @Inject
    Caller<PluginServices> pluginServices;

    @Override
    public void validate( final String name,
                          final ValidatorCallback callback ) {
        validateName( name, new ValidatorWithReasonCallback() {

            @Override
            public void onFailure( final String reason ) {
                if ( shouldGiveReasonOfValidationError( callback ) ) {
                    ( (ValidatorWithReasonCallback) callback ).onFailure( reason );
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onSuccess() {
                defaultFileNameValidator.validate( name, callback );
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        } );
    }

    private boolean shouldGiveReasonOfValidationError( final ValidatorCallback callback ) {
        return callback instanceof ValidatorWithReasonCallback;
    }

    protected void validateName( final String name,
                                 final ValidatorWithReasonCallback callback ) {
        final String nameWithoutExtension = ( name.lastIndexOf( "." ) >= 0
                ? name.substring( 0, name.lastIndexOf( "." ) ) : name );
        final RuleValidator nameValidator = getNameValidator();

        if ( !nameValidator.isValid( nameWithoutExtension ) ) {
            callback.onFailure( nameValidator.getValidationError() );
            return;
        }

        pluginServices.call( new RemoteCallback<Collection<Plugin>>() {
            @Override
            public void callback( final Collection<Plugin> plugins ) {
                Set<Activity> activities = pluginsInfo.getAllPlugins( plugins );

                for ( Activity activity : activities ) {
                    if ( activity.getName().equalsIgnoreCase( nameWithoutExtension ) ) {
                        callback.onFailure( ValidationErrorReason.DUPLICATED_NAME.name() );
                        return;
                    }
                }

                callback.onSuccess();
            }
        } ).listPlugins();
    }

    private RuleValidator getNameValidator() {
        return NameValidator.createNameValidator( ValidationErrorReason.EMPTY_NAME.name(), ValidationErrorReason.INVALID_NAME.name() );
    }
}
