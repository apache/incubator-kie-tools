/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.annotations.processors;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Types;

/**
 * A wrapper for Types that works around bug 434378 in Eclipse.
 */
public class EclipseWorkaroundTypeUtils implements Types {

    private final Types realImpl;

    public EclipseWorkaroundTypeUtils( Types realImpl ) {
        this.realImpl = realImpl;
    }

    @Override
    public Element asElement( TypeMirror arg0 ) {
        return realImpl.asElement( arg0 );
    }

    @Override
    public TypeMirror asMemberOf( DeclaredType arg0, Element arg1 ) {
        return realImpl.asMemberOf( arg0, arg1 );
    }

    @Override
    public TypeElement boxedClass( PrimitiveType arg0 ) {
        return realImpl.boxedClass( arg0 );
    }

    @Override
    public TypeMirror capture( TypeMirror arg0 ) {
        return realImpl.capture( arg0 );
    }

    @Override
    public boolean contains( TypeMirror arg0, TypeMirror arg1 ) {
        return realImpl.contains( arg0, arg1 );
    }

    @Override
    public List<? extends TypeMirror> directSupertypes( TypeMirror arg0 ) {
        return realImpl.directSupertypes( arg0 );
    }

    @Override
    public TypeMirror erasure( TypeMirror arg0 ) {
        return realImpl.erasure( arg0 );
    }

    @Override
    public ArrayType getArrayType( TypeMirror arg0 ) {
        return realImpl.getArrayType( arg0 );
    }

    @Override
    public DeclaredType getDeclaredType( DeclaredType arg0, TypeElement arg1, TypeMirror... arg2 ) {
        return realImpl.getDeclaredType( arg0, arg1, arg2 );
    }

    @Override
    public DeclaredType getDeclaredType( TypeElement arg0, TypeMirror... arg1 ) {
        return realImpl.getDeclaredType( arg0, arg1 );
    }

    @Override
    public NoType getNoType( TypeKind arg0 ) {
        return realImpl.getNoType( arg0 );
    }

    @Override
    public NullType getNullType() {
        return realImpl.getNullType();
    }

    @Override
    public PrimitiveType getPrimitiveType( TypeKind arg0 ) {
        return realImpl.getPrimitiveType( arg0 );
    }

    @Override
    public WildcardType getWildcardType( TypeMirror arg0, TypeMirror arg1 ) {
        return realImpl.getWildcardType( arg0, arg1 );
    }

    @Override
    public boolean isAssignable( TypeMirror arg0, TypeMirror arg1 ) {
        return (arg0.getKind() == TypeKind.VOID && arg1.getKind() == TypeKind.VOID) || realImpl.isAssignable( arg0, arg1 );
    }

    @Override
    public boolean isSameType( TypeMirror arg0, TypeMirror arg1 ) {
        return realImpl.isSameType( arg0, arg1 );
    }

    @Override
    public boolean isSubsignature( ExecutableType arg0, ExecutableType arg1 ) {
        return realImpl.isSubsignature( arg0, arg1 );
    }

    @Override
    public boolean isSubtype( TypeMirror arg0, TypeMirror arg1 ) {
        return realImpl.isSubtype( arg0, arg1 );
    }

    @Override
    public PrimitiveType unboxedType( TypeMirror arg0 ) {
        return realImpl.unboxedType( arg0 );
    }


}
