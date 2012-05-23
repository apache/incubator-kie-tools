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

package org.drools.guvnor.client.place;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import org.drools.guvnor.client.content.editor.TextEditorPlace;

public class PlaceBuilderUtil {

    public static Place buildPlaceFromWindow() {
//        ?id=org.drools.guvnor.editors.DRL
//        ?uuid=VFS.file_id
//        final String id = Window.Location.getParameter("id");
        final String uuid = Window.Location.getParameter("uuid");

        return new TextEditorPlace("SomeName|" + URL.decode(uuid));
    }

}
