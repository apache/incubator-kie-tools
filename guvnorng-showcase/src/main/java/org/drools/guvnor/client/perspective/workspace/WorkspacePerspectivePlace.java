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

package org.drools.guvnor.client.perspective.workspace;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class WorkspacePerspectivePlace extends Place {

    public String toString() {
        return "workspace_perspective";
    }

    public static class Tokenizer implements PlaceTokenizer<WorkspacePerspectivePlace> {

        public String getToken(final WorkspacePerspectivePlace place) {
            return place.toString();
        }

        public WorkspacePerspectivePlace getPlace(final String token) {
            return new WorkspacePerspectivePlace();
        }
    }

}
