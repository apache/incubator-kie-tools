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

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;

public interface Navigator extends IsWidget {

    void loadContent(final Path path);

    boolean isAttached();

    public static interface NavigatorItem {

        public void addDirectory(final Path child);

        public void addFile(final Path child);
    }
}
