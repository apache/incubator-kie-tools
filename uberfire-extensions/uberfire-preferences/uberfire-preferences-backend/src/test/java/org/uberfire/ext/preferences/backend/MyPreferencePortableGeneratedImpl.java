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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.preferences.shared.PropertyFormType;
import org.uberfire.ext.preferences.shared.annotations.PortablePreference;
import org.uberfire.ext.preferences.shared.annotations.RootPreference;
import org.uberfire.ext.preferences.shared.bean.BasePreferencePortable;

@Portable(mapSuperTypes = true)
@PortablePreference
@RootPreference
public class MyPreferencePortableGeneratedImpl extends MyPreference implements BasePreferencePortable<MyPreference> {

    public MyPreferencePortableGeneratedImpl() {
    }

    public MyPreferencePortableGeneratedImpl( @MapsTo("text") String text,
                                              @MapsTo("sendReports") boolean sendReports,
                                              @MapsTo("backgroundColor") String backgroundColor,
                                              @MapsTo("age") int age,
                                              @MapsTo("password") String password,
                                              @MapsTo("myInnerPreference") MyInnerPreference myInnerPreference,
                                              @MapsTo("myInheritedPreference") MyInheritedPreference myInheritedPreference ) {
        this.text = text;
        this.sendReports = sendReports;
        this.backgroundColor = backgroundColor;
        this.age = age;
        this.password = password;
        this.myInnerPreference = myInnerPreference;
        this.myInheritedPreference = myInheritedPreference;
    }

    @Override
    public Class<MyPreference> getPojoClass() {
        return MyPreference.class;
    }

    @Override
    public String bundleKey() {
        return "MyPreference.Label";
    }

    @Override
    public String key() {
        return "org.uberfire.ext.preferences.backend.MyPreference";
    }

    @Override
    public void set( String property,
                     Object value ) {
        if ( property.equals( "text" ) ) {
            text = (String) value;
        } else if ( property.equals( "sendReports" ) ) {
            sendReports = (boolean) value;
        } else if ( property.equals( "backgroundColor" ) ) {
            backgroundColor = (String) value;
        } else if ( property.equals( "age" ) ) {
            age = (int) value;
        } else if ( property.equals( "password" ) ) {
            password = (String) value;
        } else {
            throw new RuntimeException( "Unknown property: " + property );
        }
    }

    @Override
    public Object get( String property ) {
        if ( property.equals( "text" ) ) {
            return text;
        } else if ( property.equals( "sendReports" ) ) {
            return sendReports;
        } else if ( property.equals( "backgroundColor" ) ) {
            return backgroundColor;
        } else if ( property.equals( "age" ) ) {
            return age;
        } else if ( property.equals( "password" ) ) {
            return password;
        } else {
            throw new RuntimeException( "Unknown property: " + property );
        }
    }

    @Override
    public Map<String, PropertyFormType> getPropertiesTypes() {
        Map<String, PropertyFormType> propertiesTypes = new HashMap<>();

        propertiesTypes.put( "text", PropertyFormType.TEXT );
        propertiesTypes.put( "sendReports", PropertyFormType.BOOLEAN );
        propertiesTypes.put( "backgroundColor", PropertyFormType.COLOR );
        propertiesTypes.put( "age", PropertyFormType.NATURAL_NUMBER );
        propertiesTypes.put( "password", PropertyFormType.SECRET_TEXT );

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

        final MyPreferencePortableGeneratedImpl that = (MyPreferencePortableGeneratedImpl) o;

        if ( text != null ? !text.equals( that.text ) : that.text != null ) {
            return false;
        }
        if ( sendReports != that.sendReports ) {
            return false;
        }
        if ( backgroundColor != null ? !backgroundColor.equals( that.backgroundColor ) : that.backgroundColor != null ) {
            return false;
        }
        if ( age != that.age ) {
            return false;
        }
        if ( password != null ? !password.equals( that.password ) : that.password != null ) {
            return false;
        }
        if ( myInnerPreference != null ? !myInnerPreference.equals( that.myInnerPreference ) : that.myInnerPreference != null ) {
            return false;
        }
        if ( myInheritedPreference != null ? !myInheritedPreference.equals( that.myInheritedPreference ) : that.myInheritedPreference != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;

        result = 31 * result + ( text != null ? text.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + java.lang.Boolean.hashCode( sendReports );
        result = ~~result;
        result = 31 * result + ( backgroundColor != null ? backgroundColor.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + java.lang.Integer.hashCode( age );
        result = ~~result;
        result = 31 * result + ( password != null ? password.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( myInnerPreference != null ? myInnerPreference.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( myInheritedPreference != null ? myInheritedPreference.hashCode() : 0 );
        result = ~~result;

        return result;
    }
}