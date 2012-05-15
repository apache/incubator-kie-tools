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

package org.drools.guvnor.client.perspective.monitoring;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class MonitoringPerspectivePlace extends Place {

    public String toString() {
        return "monitoring_perspective";
    }

    public static class Tokenizer implements PlaceTokenizer<MonitoringPerspectivePlace> {

        public String getToken(final MonitoringPerspectivePlace place) {
            return place.toString();
        }

        public MonitoringPerspectivePlace getPlace(final String token) {
            return new MonitoringPerspectivePlace();
        }
    }

}
