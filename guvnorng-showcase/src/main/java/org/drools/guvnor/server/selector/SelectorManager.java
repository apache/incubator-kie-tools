/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.server.selector;

import org.drools.repository.AssetItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class SelectorManager {

    private static final Logger             log                        = LoggerFactory.getLogger( SelectorManager.class );
    private static final String             SELECTOR_CONFIG_PROPERTIES = "selectors.properties";
    private static final SelectorManager    INSTANCE                   = new SelectorManager( SELECTOR_CONFIG_PROPERTIES );

    public static final String              BUILT_IN_SELECTOR          = "BuiltInSelector";
    public static final String              CUSTOM_SELECTOR            = "customSelector";

    /**
     * This is a map of the selectors to use.
     */
    public final Map<String, AssetSelector> selectors                  = new HashMap<String, AssetSelector>();

    SelectorManager(String configPath) {
        log.debug( "Loading selectors" );
        Properties props = new Properties();
        try {
            props.load( this.getClass().getResourceAsStream( configPath ) );
            props.put( "BuiltInSelector",
                       "org.drools.guvnor.server.selector.BuiltInSelector" );
            for ( Object o : props.keySet() ) {
                String selectorName = (String) o;
                String val = props.getProperty( selectorName );
                detemineSelector( selectorName,
                                  val );
            }
        } catch ( IOException e ) {
            log.error( "Unable to load selectors.",
                       e );
        }
    }

    private void detemineSelector(String selectorName,
                                  String val) {
        try {
            if ( val.endsWith( "drl" ) ) {
                selectors.put( selectorName,
                               loadRuleSelector( val ) );
            } else {
                selectors.put( selectorName,
                               loadSelectorImplementation( val ) );
            }
        } catch ( Exception e ) {
            log.error( "Unable to load a selector [" + val + "]",
                       e );
        }
    }

    /**
     * Return a selector. If the name is null or empty it will return a
     * nil/default selector (one that lets everything through). If the selector
     * is not found, it will return null;
     */
    public AssetSelector getSelector(String name) {
        if ( name == null || "".equals( name.trim() ) ) {
            return nilSelector();
        } else {
            if ( this.selectors.containsKey( name ) ) {
                return this.selectors.get( name );
            } else {
                log.debug( "No selector found by the name of " + name );
                return null;
            }
        }
    }

    public String[] getCustomSelectors() {
        Set<String> s = selectors.keySet();
        List<String> selectorList = new ArrayList<String>();
        selectorList.addAll( s );
        selectorList.remove( "BuiltInSelector" );
        String[] result = new String[selectorList.size()];
        return selectorList.toArray( result );
    }

    private AssetSelector nilSelector() {
        return new AssetSelector() {
            public boolean isAssetAllowed(AssetItem asset) {
                return true;
            }
        };
    }

    private AssetSelector loadSelectorImplementation(String val) throws Exception {
        return (AssetSelector) Thread.currentThread().getContextClassLoader().loadClass( val ).newInstance();
    }

    private AssetSelector loadRuleSelector(String val) {
        return new RuleBasedSelector( val );
    }

    public static SelectorManager getInstance() {
        return INSTANCE;
    }
}
