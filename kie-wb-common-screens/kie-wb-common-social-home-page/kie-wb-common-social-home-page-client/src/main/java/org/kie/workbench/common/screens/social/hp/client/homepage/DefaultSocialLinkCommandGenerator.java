/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.social.hp.client.homepage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.structure.social.OrganizationalUnitEventType;
import org.kie.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.kie.uberfire.social.activities.model.SocialFileSelectedEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class DefaultSocialLinkCommandGenerator {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<SocialFileSelectedEvent> socialFileSelectedEvent;

    public DefaultSocialLinkCommandGenerator() {
    }

    public ParameterizedCommand<LinkCommandParams> generateLinkCommand( ) {

        return new ParameterizedCommand<LinkCommandParams>() {
            @Override public void execute( LinkCommandParams parameters ) {
                if ( parameters.isVFSLink() ) {
                    placeManager.goTo( "AuthoringPerspective" );
                    socialFileSelectedEvent.fire( new SocialFileSelectedEvent( parameters.getEventType(), parameters.getLink() ) );
                } else if ( OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT.name().equals( parameters.getEventType() ) ) {
                    Window.alert( "Open organizational units screen for: " + parameters.getLink() );
                }
            }
        };

    }

}
