/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.JavaTypeInfo;

public class JavaTypeInfoImpl implements JavaTypeInfo {

    String _name;

    String _packageName;

    boolean _class;

    boolean _interface;

    boolean _annotation;

    boolean _enum;

    boolean _packagePrivate;

    boolean _public;

    boolean _private;

    boolean _protected;

    public JavaTypeInfoImpl() {
        //errai marshalling
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getPackageName() {
        return _packageName;
    }

    @Override
    public boolean isClass() {
        return _class;
    }

    @Override
    public boolean isEnum() {
        return _enum;
    }

    @Override
    public boolean isInterface() {
        return _interface;
    }

    @Override
    public boolean isAnnotation() {
        return _annotation;
    }

    @Override
    public boolean isPackagePrivate() {
        return _packagePrivate;
    }

    @Override
    public boolean isPublic() {
        return _public;
    }

    @Override
    public boolean isPrivate() {
        return _private;
    }

    @Override
    public boolean isProtected() {
        return _protected;
    }

    public void setName( String _name ) {
        this._name = _name;
    }

    public void setPackageName( String _packageName ) {
        this._packageName = _packageName;
    }

    public void setClass( boolean _class ) {
        this._class = _class;
    }

    public void setInterface( boolean _interface ) {
        this._interface = _interface;
    }

    public void setAnnotation( boolean _annotation ) {
        this._annotation = _annotation;
    }

    public void setEnum( boolean _enum ) {
        this._enum = _enum;
    }

    public void setPackagePrivate( boolean _packagePrivate ) {
        this._packagePrivate = _packagePrivate;
    }

    public void setPublic( boolean _public ) {
        this._public = _public;
    }

    public void setPrivate( boolean _private ) {
        this._private = _private;
    }

    public void setProtected( boolean _protected ) {
        this._protected = _protected;
    }

    @Override public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        JavaTypeInfoImpl that = ( JavaTypeInfoImpl ) o;

        if ( _class != that._class ) {
            return false;
        }
        if ( _interface != that._interface ) {
            return false;
        }
        if ( _annotation != that._annotation ) {
            return false;
        }
        if ( _enum != that._enum ) {
            return false;
        }
        if ( _packagePrivate != that._packagePrivate ) {
            return false;
        }
        if ( _public != that._public ) {
            return false;
        }
        if ( _private != that._private ) {
            return false;
        }
        if ( _protected != that._protected ) {
            return false;
        }
        if ( _name != null ? !_name.equals( that._name ) : that._name != null ) {
            return false;
        }
        return !( _packageName != null ? !_packageName.equals( that._packageName ) : that._packageName != null );

    }

    @Override public int hashCode() {
        int result = _name != null ? _name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( _packageName != null ? _packageName.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( _class ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( _interface ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( _annotation ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( _enum ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( _packagePrivate ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( _public ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( _private ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( _protected ? 1 : 0 );
        result = ~~result;
        return result;
    }
}
