/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.social.hp.client;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.workbench.common.screens.social.hp.client.resources.ContainerResources;
import org.kie.workbench.common.screens.social.hp.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.social.hp.config.SocialConfigurationService;

@EntryPoint
public class SocialHomePageEntryPoint {

    private Caller<SocialConfigurationService> socialConfigurationService;

    @Inject
    public SocialHomePageEntryPoint( final Caller<SocialConfigurationService> socialConfigurationService ) {
        this.socialConfigurationService = socialConfigurationService;
    }

    @AfterInitialization
    public void startApp() {
        ContainerResources.INSTANCE.CSS().ensureInjected();
        configureMessages();
    }

    private void configureMessages() {
        final Map<String, String> messages = new HashMap<String, String>();
        messages.put( "added", Constants.INSTANCE.Added() );
        messages.put( "created", Constants.INSTANCE.Created() );
        messages.put( "edited", Constants.INSTANCE.Edited() );

        socialConfigurationService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void aVoid ) {
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                GWT.log( "Error when registering social messages: ", throwable );
                return false;
            }
        } ).registerSocialMessages( messages );
    }
}
