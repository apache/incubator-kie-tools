/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

@Dependent
public class LockDemandDetector {

    private static final List<String> TAG_CLICK_LOCK_EXCLUSIONS = Arrays.asList( "a",
                                                                                 "select",
                                                                                 "input",
                                                                                 "textarea",
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
     * <li>A global default list of tag exclusions for click events (i.e.
     * clicking on select element shouldn't cause a lock since the selection 
     * will later cause a change event) and a DOM attribute to override this
     * default behavior for click events (data-uf-lock-on-click="[true|false]")
     * <ul>
     * 
     * @param event
     *            the DOM event
     * @return true, if a lock is required, otherwise false.
     */
    public boolean isLockRequired( final Event event ) {
        final Element target = Element.as( event.getEventTarget() );
        final String lockAttribute = findLockAttribute( "data-uf-lock", target );
        if ( lockAttribute != null && !lockAttribute.isEmpty() ) {
            return Boolean.parseBoolean( lockAttribute );
        }

        final boolean click = (event.getTypeInt() == Event.ONCLICK);
        
        if (click) {
            final String lockOnClickAttribute = findLockAttribute( "data-uf-lock-on-click", target );
            if ( lockOnClickAttribute != null && !lockOnClickAttribute.isEmpty() ) {
               return Boolean.parseBoolean( lockOnClickAttribute );
            } else {
               return !TAG_CLICK_LOCK_EXCLUSIONS.contains( target.getTagName().toLowerCase() ); 
            }
        }
        
        return true;
    }

    /**
     * Returns the bitmask of all events that can potentially indicate a lock
     * demand. The actually event should be passed to
     * {@link #isLockRequired(Event)} to account for fine-tuning (i.e. via
     * custom configuration).
     */
    public int getLockDemandEventTypes() {
        return Event.KEYEVENTS | Event.ONCHANGE | Event.ONCLICK | Event.ONPASTE;
    }

    private String findLockAttribute( final String attributeName, final Element element ) {
        if ( element == null ) {
            return null;
        }

        final String lockAttribute = element.getAttribute( attributeName );
        if ( lockAttribute != null && !lockAttribute.isEmpty() ) {
            return lockAttribute;
        }

        return findLockAttribute( attributeName, element.getParentElement() );
    }
}