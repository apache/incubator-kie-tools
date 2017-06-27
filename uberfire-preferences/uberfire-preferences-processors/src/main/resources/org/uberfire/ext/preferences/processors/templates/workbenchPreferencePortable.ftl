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

package ${targetPackage};

import java.lang.RuntimeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.preferences.shared.annotations.PortablePreference;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.PropertyValidator;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Portable( mapSuperTypes = true )
@PortablePreference
@Generated("org.uberfire.ext.preferences.processors.WorkbenchPreferenceProcessor")
/*
* WARNING! This class is generated. Do not modify.
*/
public class ${targetClassName} extends ${sourceClassName} implements BasePreferencePortable<${sourceClassName}> {

<#if constructorParamsText != "">
    public ${targetClassName}() {
    <#list subPreferences as subPreference>
        <#if subPreference.isPrivateAccess()>
        set${subPreference.getCapitalizedFieldName()}( new ${subPreference.getTypeFullName()}PortableGeneratedImpl() );
        <#else>
        this.${subPreference.getFieldName()} = new ${subPreference.getTypeFullName()}PortableGeneratedImpl();
        </#if>
    </#list>
    }
</#if>

    public ${targetClassName}( ${constructorParamsText} ) {
    <#list properties as property>
        <#if property.isPrivateAccess()>
        set${property.getCapitalizedFieldName()}( ${property.getFieldName()} );
        <#else>
        this.${property.getFieldName()} = ${property.getFieldName()};
        </#if>
    </#list>
    }

    @Override
    public Class<${sourceClassName}> getPojoClass() {
        return ${sourceClassName}.class;
    }

    @Override
    public String identifier() {
        return "${identifier}";
    }

    @Override
    public String[] parents() {
        return new String[] { "${parentsIdentifiers}" };
    }

    @Override
    public String bundleKey() {
    <#if bundleKey == "">
        return "${identifier}";
    <#else>
        return "${bundleKey}";
    </#if>
    }

    @Override
    public void set( String property, Object value ) {
    <#list simpleProperties as property>
        if ( property.equals( "${property.getFieldName()}" ) ) {
        <#if property.isPrivateAccess()>
            set${property.getCapitalizedFieldName()}( (${property.getTypeFullName()}) value );
        <#else>
            ${property.getFieldName()} = (${property.getTypeFullName()}) value;
        </#if>
        } else
    </#list>
        {
            throw new RuntimeException( "Unknown property: " + property );
        }
    }

    @Override
    public Object get( String property ) {
    <#list simpleProperties as property>
        if ( property.equals( "${property.getFieldName()}" ) ) {
            return ${property.getFieldAccessorCommand()};
        } else
    </#list>
        {
            throw new RuntimeException( "Unknown property: " + property );
        }
    }

    @Override
    public Map<String, PropertyFormType> getPropertiesTypes() {
        Map<String, PropertyFormType> propertiesTypes = new HashMap<>();

    <#list simpleProperties as property>
        propertiesTypes.put( "${property.getFieldName()}", PropertyFormType.${property.getFormType()});
    </#list>

        return propertiesTypes;
    }

    @Override
    public Map<String, List<PropertyValidator>> getPropertiesValidators() {
        Map<String, List<PropertyValidator>> validatorsByProperty = new HashMap<>();

    <#list simpleProperties as property>
        <#if property.hasValidators()>
        List<PropertyValidator> validators${property.getCapitalizedFieldName()} = new ArrayList<>();
            <#list property.validators as validator>
        validators${property.getCapitalizedFieldName()}.add(new ${validator}());
            </#list>    
        validatorsByProperty.put("${property.getFieldName()}", validators${property.getCapitalizedFieldName()});

        </#if>
    </#list>

        return validatorsByProperty;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final ${targetClassName} that = (${targetClassName}) o;

<#list properties as property>
    <#if property.isPrimitive()>
        if ( ${property.getFieldAccessorCommand()} != that.${property.getFieldAccessorCommand()} ) {
            return false;
        }
    <#else>
        if ( ${property.getFieldAccessorCommand()} != null ? !${property.getFieldAccessorCommand()}.equals( that.${property.getFieldAccessorCommand()} ) : that.${property.getFieldAccessorCommand()} != null ) {
            return false;
        }
    </#if>
</#list>

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;

    <#list properties as property>
        result = 31 * result + ${property.getHashCodeFormula()};
        result = ~~result;
    </#list>

        return result;
    }

    @Override
    public boolean isPersistable() {
        return ${isPersistable};
    }
}
