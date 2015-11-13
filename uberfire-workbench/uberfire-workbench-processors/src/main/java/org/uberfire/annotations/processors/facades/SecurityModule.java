/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.annotations.processors.facades;


/**
 * A collection of type names in the UberFire Security module.
 * Due to a bug in Eclipse annotation processor dependencies, we refer to all UberFire type names using Strings,
 * Elements, and TypeMirrors. We cannot refer to the annotation types as types themselves.
 */
public class SecurityModule {

    private SecurityModule() {}

    public static final String rolesType =  "org.uberfire.security.annotations.RolesType" ;
    public static final String securityTrait =  "org.uberfire.security.annotations.SecurityTrait" ;

    public static String getSecurityTraitClass() {
        return securityTrait;
    }

    public static String getRolesTypeClass() {
        return rolesType;
    }

}
