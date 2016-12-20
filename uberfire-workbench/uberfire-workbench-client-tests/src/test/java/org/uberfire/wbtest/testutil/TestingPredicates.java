/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.testutil;

import org.uberfire.wbtest.client.main.DefaultScreenActivity;
import org.uberfire.wbtest.client.panels.docking.NestingScreen;

import com.google.common.base.Predicate;
import com.google.gwt.user.client.DOM;


public class TestingPredicates {

    /**
     * Returns true as long as no instances of DefaultScreenActivity have been created since
     * {@link DefaultScreenActivity#instanceCount} has been reset to 0.
     */
    public static final Predicate<Void> DEFAULT_SCREEN_NOT_LOADED = new Predicate<Void>() {
        @Override
        public boolean apply( Void input ) {
            return DefaultScreenActivity.instanceCount == 0;
        }
    };

    /**
     * Returns true as long as there are no instances of DefaultScreenActivity's view in the DOM.
     */
    public static final Predicate<Void> DEFAULT_SCREEN_NOT_VISIBLE = new Predicate<Void>() {
        @Override
        public boolean apply( Void input ) {
            return DOM.getElementById( "gwt-debug-" + DefaultScreenActivity.DEBUG_ID ) == null;
        }
    };

    /**
     * Returns true as long as there are no instances of NestingScreen loaded in the ActivityManager.
     */
    public static final Predicate<Void> NESTING_SCREEN_NOT_LOADED = new Predicate<Void>() {
        @Override
        public boolean apply( Void input ) {
            return NestingScreen.instanceCount == 0;
        }
    };
}
