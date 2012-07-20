package org.drools.guvnor.client.mvp;

import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
        public com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(
                                                                                     ValueChangeHandler<String> valueChangeHandler) {
            return History.addValueChangeHandler( valueChangeHandler );
        }

        public String getToken() {
            return History.getToken();
        }

        public void newItem(String token,
                            boolean issueEvent) {
            History.newItem( token,
                             issueEvent );
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
         * 
         * @param valueChangeHandler
         *            the handler
         * @return the registration used to remove this value change handler
         */
        com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(
                                                                              ValueChangeHandler<String> valueChangeHandler);

        /**
         * @return the current history token.
         */
        String getToken();

        /**
         * Adds a new browser history entry. Calling this method will cause
         * {@link ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)}
         * to be called as well.
         */
        void newItem(String token,
                     boolean issueEvent);
    }

    private final Historian                 historian;

    private final PlaceRequestHistoryMapper mapper;

    private PlaceManager                    placeManager;

    private PlaceRequest                    defaultPlaceRequest = new PlaceRequest( "NOWHERE" );

    /**
     * Create a new PlaceHistoryHandler with a {@link DefaultHistorian}. The
     * DefaultHistorian is created via a call to GWT.create(), so an alternative
     * default implementation can be provided through &lt;replace-with&gt; rules
     * in a {@code gwt.xml} file.
     * 
     * @param mapper
     *            a {@link PlaceRequestHistoryMapper} instance
     */
    @Inject
    public PlaceHistoryHandler(PlaceRequestHistoryMapper mapper) {
        this( mapper,
              (Historian) GWT.create( DefaultHistorian.class ) );
    }

    /**
     * Create a new PlaceHistoryHandler.
     * 
     * @param mapper
     *            a {@link PlaceRequestHistoryMapper} instance
     * @param historian
     *            a {@link Historian} instance
     */
    public PlaceHistoryHandler(PlaceRequestHistoryMapper mapper,
                               Historian historian) {
        this.mapper = mapper;
        this.historian = historian;
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
     * 
     * @return a registration object to de-register the handler
     */
    public HandlerRegistration register(PlaceManager placeManager,
                                        EventBus eventBus,
                                        PlaceRequest defaultPlaceRequest) {
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
                    public void onValueChange(ValueChangeEvent<String> event) {
                        String token = event.getValue();
                        handleHistoryToken( token );
                    }
                } );

        return new HandlerRegistration() {
            public void removeHandler() {
                PlaceHistoryHandler.this.defaultPlaceRequest = new PlaceRequest( "NOWHERE" );
                PlaceHistoryHandler.this.placeManager = null;
                //placeReg.removeHandler();
                historyReg.removeHandler();
            }
        };
    }

    public void onPlaceChange(PlaceRequest placeRequest) {
        historian.newItem( tokenForPlace( placeRequest ),
                           false );
    }

    /**
     * Visible for testing.
     */
    Logger log() {
        return log;
    }

    private void handleHistoryToken(String token) {

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

    private String tokenForPlace(PlaceRequest newPlaceRequest) {
        if ( defaultPlaceRequest.equals( newPlaceRequest ) ) {
            return "";
        }

        String token = mapper.getIdentifier( newPlaceRequest );
        if ( token != null ) {
            return token;
        }

        log().warning( "PlaceRequest not mapped to a token: " + newPlaceRequest );
        return "";
    }
}
