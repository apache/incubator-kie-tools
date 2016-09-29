/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.backend;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.preferences.shared.PropertyFormType;
import org.uberfire.ext.preferences.shared.annotations.PortablePreference;
import org.uberfire.ext.preferences.shared.bean.BasePreferencePortable;

@Portable(mapSuperTypes = true)
@PortablePreference
@Generated("org.uberfire.ext.preferences.processors.WorkbenchPreferenceProcessor")
/*
* WARNING! This class is generated. Do not modify.
*/
public class MyInnerPreference2PortableGeneratedImpl extends MyInnerPreference2 implements BasePreferencePortable<MyInnerPreference2> {

    public MyInnerPreference2PortableGeneratedImpl() {
        this.myInheritedPreference2 = new org.uberfire.ext.preferences.backend.MyInheritedPreference2PortableGeneratedImpl();
    }

    public MyInnerPreference2PortableGeneratedImpl( @MapsTo("text") String text,
                                                    @MapsTo("myInheritedPreference2") org.uberfire.ext.preferences.backend.MyInheritedPreference2 myInheritedPreference2 ) {
        this.text = text;
        this.myInheritedPreference2 = myInheritedPreference2;
    }

    @Override
    public Class<MyInnerPreference2> getPojoClass() {
        return MyInnerPreference2.class;
    }

    @Override
    public String bundleKey() {
        return "MyInnerPreference2.Label";
    }

    @Override
    public String key() {
        return "org.uberfire.ext.preferences.backend.MyInnerPreference2";
    }

    @Override
    public void set( String property,
                     Object value ) {
        if ( property.equals( "text" ) ) {
            text = (String) value;
        } else {
            throw new RuntimeException( "Unknown property: " + property );
        }
    }

    @Override
    public Object get( String property ) {
        if ( property.equals( "text" ) ) {
            return text;
        } else {
            throw new RuntimeException( "Unknown property: " + property );
        }
    }

    @Override
    public Map<String, PropertyFormType> getPropertiesTypes() {
        Map<String, PropertyFormType> propertiesTypes = new HashMap<>();

        propertiesTypes.put( "text", PropertyFormType.TEXT );

        return propertiesTypes;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final MyInnerPreference2PortableGeneratedImpl that = (MyInnerPreference2PortableGeneratedImpl) o;

        if ( text != null ? !text.equals( that.text ) : that.text != null ) {
            return false;
        }
        if ( myInheritedPreference2 != null ? !myInheritedPreference2.equals( that.myInheritedPreference2 ) : that.myInheritedPreference2 != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;

        result = 31 * result + ( text != null ? text.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( myInheritedPreference2 != null ? myInheritedPreference2.hashCode() : 0 );
        result = ~~result;

        return result;
    }
}
