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

package org.drools.guvnor.client.editor;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;


public class MyAdminAreaPlace2 extends Place {

    private String MY_ADMIN_AREA2 = "MyAdminArea2";

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        MyAdminAreaPlace2 place = (MyAdminAreaPlace2) o;

        if ( MY_ADMIN_AREA2 != null ? !MY_ADMIN_AREA2.equals( place.MY_ADMIN_AREA2 ) : place.MY_ADMIN_AREA2 != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return MY_ADMIN_AREA2 != null ? MY_ADMIN_AREA2.hashCode() : 0;
    }

    public static class Tokenizer implements PlaceTokenizer<MyAdminAreaPlace2> {

        public String getToken(MyAdminAreaPlace2 place) {
            return "MY_ADMIN_AREA2";
        }

        public MyAdminAreaPlace2 getPlace(String token) {
            return new MyAdminAreaPlace2();
        }
    }
}