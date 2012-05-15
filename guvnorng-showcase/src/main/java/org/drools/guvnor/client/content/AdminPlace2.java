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

package org.drools.guvnor.client.content;

import com.google.gwt.place.shared.PlaceTokenizer;
import org.drools.guvnor.client.common.content.multi.ContentPlace;

public class AdminPlace2 extends ContentPlace {

    private final String helloName;
    private final String tabName;

    public AdminPlace2() {
        this.helloName = "Admin2";
        this.tabName = "admin2Tab";
    }

    public AdminPlace2(final String token) {
        String[] parts = token.split("\\|");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid token");
        }
        this.helloName = parts[0];
        this.tabName = parts[1];
    }

    public String getTabName() {
        return tabName;
    }

    @Override
    public String toString() {
        return helloName + '|' + tabName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AdminPlace2 that = (AdminPlace2) o;

        if (!helloName.equals(that.helloName)) {
            return false;
        }
        if (!tabName.equals(that.tabName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = helloName.hashCode();
        result = 31 * result + tabName.hashCode();
        return result;
    }

    public static class Tokenizer implements PlaceTokenizer<AdminPlace2> {

        public String getToken(final AdminPlace2 place) {
            return place.toString();
        }

        public AdminPlace2 getPlace(final String token) {
            return new AdminPlace2(token);
        }
    }

}
