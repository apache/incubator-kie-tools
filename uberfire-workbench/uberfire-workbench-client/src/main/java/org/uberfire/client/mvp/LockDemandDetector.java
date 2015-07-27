package org.uberfire.client.mvp;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

@Dependent
public class LockDemandDetector {

    private static final List<String> TAG_CLICK_LOCK_EXCLUSIONS = Arrays.asList( "a",
                                                                                 "select",
                                                                                 "table",
                                                                                 "tbody",
                                                                                 "tfoot",
                                                                                 "td",
                                                                                 "tr" );

    /**
     * Determines whether or not the provided event indicates a change and
     * therefore demands a lock. The decision is based on:
     * 
     * <ul>
     * <li>An optional custom DOM attribute which can be placed on the target
     * element or any of its parent elements (data-uf-lock="[true|false]")
     * 
     * <li>A global default list of tag exclusion for click events (i.e.
     * clicking on select element should cause a lock since the selection will
     * later cause a change event)
     * <ul>
     * 
     * @param event
     *            the DOM event
     * @return true, if a lock is required, otherwise false.
     */
    public boolean isLockRequired( Event event ) {
        final Element target = Element.as( event.getEventTarget() );
        final String lockAttribute = findLockAttribute( target );
        if ( lockAttribute != null && !lockAttribute.isEmpty() ) {
            return Boolean.parseBoolean( lockAttribute );
        }

        boolean eventExcluded = (event.getTypeInt() == Event.ONCLICK &&
                TAG_CLICK_LOCK_EXCLUSIONS.contains( target.getTagName().toLowerCase() ));

        return !eventExcluded;
    }

    /**
     * Returns the bitmask of all events that can potentially indicate a lock
     * demand. The actually event should be passed to
     * {@link #isLockRequired(Event)} to account for fine-tuning (i.e. via
     * custom configuration).
     */
    public int getLockDemandEventTypes() {
        return Event.KEYEVENTS | Event.ONCHANGE | Event.ONCLICK;
    }

    private String findLockAttribute( final Element element ) {
        if ( element == null ) {
            return null;
        }

        final String lockAttribute = element.getAttribute( "data-uf-lock" );
        if ( lockAttribute != null && !lockAttribute.isEmpty() ) {
            return lockAttribute;
        }

        return findLockAttribute( element.getParentElement() );
    }
}