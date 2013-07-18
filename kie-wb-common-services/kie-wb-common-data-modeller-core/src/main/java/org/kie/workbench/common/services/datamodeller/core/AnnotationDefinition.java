/**
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.core;

import java.util.List;

public interface AnnotationDefinition extends HasClassName {

    /**
     * Annotation name e.g. @DataObject, @Key, @Entity, @Column
     *
     * @return
     */
    String getName();

    /**
     * A marker annotation has no members.
     *
     * @return true if the given annotation is a marker annotation, false in any other case.
     *
     */
    boolean isMarker();

    /**
     *
     * @return Annotation's short description.
     */
    String getShortDescription();

    /**
     * 
     * @return Annotation's long description.
     * 
     */
    String getDescription();


    /**
     * Set of supported parameters by this attribute.
     *
     * @return
     */
    List<AnnotationMemberDefinition> getAnnotationMembers();

    /**
     *
     * @return true if the annotation applies to objects, false in any other case.
     */
    boolean isObjectAnnotation();


    /**
     *
     * @return true if the annotation applies to properties, false in any other case.
     */
    boolean isPropertyAnnotation();


    /**
     *
     * @param name
     *
     * @return true if the annotation has a member with the given name, false in any other case.
     */
    boolean hasMember(String name);
       
}
