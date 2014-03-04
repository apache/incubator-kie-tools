/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.mvp;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

@Dependent
public class PlaceHistoryHandler {

    private static final Logger log = Logger.getLogger( PlaceHistoryHandler.class.getName() );

    /**
     * Default implementation of {@link Historian}, based on {@link History}.
     */
    public static class DefaultHistorian
    implements
    Historian {

        @Override
        public com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler( final ValueChangeHandler<String> valueChangeHandler ) {
            return History.addValueChangeHandler( valueChangeHandler );
        }

        @Override
        public String getToken() {
            return History.getToken();
        }

        @Override
        public void newItem( String token,
                boolean issueEvent ) {
            History.newItem( token, issueEvent );
        }
    }

    /**
     * Optional delegate in charge of History related events. Provides nice
     * isolation for unit testing, and allows pre- or post-processing of tokens.
     * Methods correspond to the like named methods on {@link History}.
     */
    public interface Historian {

        /**
         * Adds a {@link com.google.gwt.event.logical.shared.ValueChangeEvent}
         * handler to be informed of changes to the browser's history stack.
         * @param valueChangeHandler the handler
         * @return the registration used to remove this value change handler
         */
        com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler( final ValueChangeHandler<String> valueChangeHandler );

        /**
         * @return the current history token.
         */
        String getToken();

        /**
         * Adds a new browser history entry. Calling this method will cause
         * {@link ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)}
         * to be called as well.
         */
        void newItem( final String token,
                final boolean issueEvent );
    }

    private final Historian historian;

    @Inject
    private PlaceRequestHistoryMapper mapper;

    private PlaceManager placeManager;

    private PlaceRequest defaultPlaceRequest = PlaceRequest.NOWHERE;

    /**
     * Create a new PlaceHistoryHandler.
     */
    public PlaceHistoryHandler() {
        this.historian = (Historian) GWT.create( DefaultHistorian.class );
    }

    /**
     * Handle the current history token. Typically called at application start,
     * to ensure bookmark launches work.
     */
    public void handleCurrentHistory() {
        handleHistoryToken( historian.getToken() );
    }

    /**
     * Initialize this place history handler.
     * @return a registration object to de-register the handler
     */
    public HandlerRegistration register( final PlaceManager placeManager,
            final EventBus eventBus,
            final PlaceRequest defaultPlaceRequest ) {
        this.placeManager = placeManager;
        this.defaultPlaceRequest = defaultPlaceRequest;
        /*
         * final HandlerRegistration placeReg =
         * eventBus.addHandler(PlaceChangeEvent.TYPE, new
         * PlaceChangeEvent.Handler() { public void
         * onPlaceChange(PlaceChangeEvent event) { Place newPlace =
         * event.getNewPlace();
         * historian.newItem(tokenForPlace(newPlaceRequest), false); } });
         */

        final HandlerRegistration historyReg =
                historian.addValueChangeHandler( new ValueChangeHandler<String>() {
                    @Override
                    public void onValueChange( ValueChangeEvent<String> event ) {
                        String token = event.getValue();
                        handleHistoryToken( token );
                    }
                } );

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                PlaceHistoryHandler.this.defaultPlaceRequest = DefaultPlaceRequest.NOWHERE;
                PlaceHistoryHandler.this.placeManager = null;
                //placeReg.removeHandler();
                historyReg.removeHandler();
            }
        };
    }

    public void onPlaceChange( final PlaceRequest placeRequest ) {
        if ( placeRequest.isUpdateLocationBarAllowed() ) {
            historian.newItem( tokenForPlace( placeRequest ), false );
        }
    }

    /**
     * Visible for testing.
     */
    Logger log() {
        return log;
    }

    private void handleHistoryToken( String token ) {

        PlaceRequest newPlaceRequest = null;

        if ( "".equals( token ) ) {
            newPlaceRequest = defaultPlaceRequest;
        }

        if ( newPlaceRequest == null ) {
            newPlaceRequest = mapper.getPlaceRequest( token );
        }

        if ( newPlaceRequest == null ) {
            log().warning( "Unrecognized history token: " + token );
            newPlaceRequest = defaultPlaceRequest;
        }

        placeManager.goTo( newPlaceRequest );
    }

    private String tokenForPlace( final PlaceRequest newPlaceRequest ) {
        if ( defaultPlaceRequest.equals( newPlaceRequest ) ) {
            return "";
        }

        return newPlaceRequest.getFullIdentifier();
    }
}
