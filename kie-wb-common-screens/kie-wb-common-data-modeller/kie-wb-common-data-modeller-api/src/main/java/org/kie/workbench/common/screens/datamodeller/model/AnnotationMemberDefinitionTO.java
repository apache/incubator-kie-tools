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

package org.kie.workbench.common.screens.datamodeller.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AnnotationMemberDefinitionTO {

    private String name;

    private Object defaultValue;

    private boolean array = false;

    private String className;

    private boolean enumMember = false;

    private boolean primitiveType = false;

    public AnnotationMemberDefinitionTO() {
    }

    public AnnotationMemberDefinitionTO(String name, String className, boolean primitiveType, boolean enumMember, Object defaultValue) {
        this.name = name;
        this.className = className;
        this.primitiveType = primitiveType;
        this.enumMember = enumMember;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }


    public String getClassName() {
        return className;
    }
    
    public boolean isEnum() {
        return enumMember;
    }

    public boolean isPrimitiveType() {
        return primitiveType;
    }

    public boolean isArray() {
        return array;
    }

    public boolean isString() {
        return "java.lang.String".equals(className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AnnotationMemberDefinitionTO that = (AnnotationMemberDefinitionTO) o;

        if (array != that.array) {
            return false;
        }
        if (enumMember != that.enumMember) {
            return false;
        }
        if (primitiveType != that.primitiveType) {
            return false;
        }
        if (className != null ? !className.equals(that.className) : that.className != null) {
            return false;
        }
        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (array ? 1 : 0);
        result = ~~result;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (enumMember ? 1 : 0);
        result = ~~result;
        result = 31 * result + (primitiveType ? 1 : 0);
        result = ~~result;
        return result;
    }
}
