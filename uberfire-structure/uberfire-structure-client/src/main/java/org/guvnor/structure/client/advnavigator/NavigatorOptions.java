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

package org.guvnor.structure.client.advnavigator;

public interface NavigatorOptions {

    public static final NavigatorOptions DEFAULT = new NavigatorOptions() {
        @Override
        public boolean showFiles() {
            return true;
        }

        @Override
        public boolean showHiddenFiles() {
            return false;
        }

        @Override
        public boolean showDirectories() {
            return true;
        }

        @Override
        public boolean allowUpLink() {
            return true;
        }

        @Override
        public boolean showItemAge() {
            return true;
        }

        @Override
        public boolean showItemLastUpdater() {
            return true;
        }

        @Override
        public boolean showItemMessage() {
            return true;
        }
    };

    boolean showFiles();

    boolean showHiddenFiles();

    boolean showDirectories();

    boolean allowUpLink();

    boolean showItemAge();

    boolean showItemMessage();

    boolean showItemLastUpdater();
}
