/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import java.util.List;

public class Variable {

    public enum VariableType {
        INPUT,
        OUTPUT,
        PROCESS
    }

    private VariableType variableType = VariableType.PROCESS;

    private String name;

    private String dataType;

    private String customDataType;

    public Variable( VariableType variableType ) {
        this.variableType = variableType;
    }

    public Variable( String name, VariableType variableType ) {
        this.name = name;
        this.variableType = variableType;
    }

    public Variable( String name, VariableType variableType, String dataType, String customDataType ) {
        this.name = name;
        this.variableType = variableType;
        this.dataType = dataType;
        this.customDataType = customDataType;
    }

    public Variable( VariableRow row ) {
        this.name = row.getName();
        this.variableType = row.getVariableType();
        this.dataType = row.getDataType();
        this.customDataType = row.getCustomDataType();
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setVariableType( VariableType variableType ) {
        this.variableType = variableType;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType( String dataType ) {
        this.dataType = dataType;
    }

    public String getCustomDataType() {
        return customDataType;
    }

    public void setCustomDataType( String customDataType ) {
        this.customDataType = customDataType;
    }

    public String toString() {
        if ( name != null && !name.isEmpty() ) {
            StringBuilder sb = new StringBuilder().append( name );
            if ( customDataType != null && !customDataType.isEmpty() ) {
                sb.append( ':' ).append( customDataType );
            } else if ( dataType != null && !dataType.isEmpty() ) {
                sb.append( ':' ).append( dataType );
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Deserializes a variable, checking whether the datatype is custom or not
     *
     * @param s
     * @param variableType
     * @param dataTypes
     * @return
     */
    public static Variable deserialize( String s, VariableType variableType, List<String> dataTypes ) {
        Variable var = new Variable( variableType );
        String[] varParts = s.split( ":" );
        if ( varParts.length > 0 ) {
            String name = varParts[ 0 ];
            if ( !name.isEmpty() ) {
                var.setName( name );
                if ( varParts.length == 2 ) {
                    String dataType = varParts[ 1 ];
                    if ( !dataType.isEmpty() ) {
                        if ( dataTypes != null && dataTypes.contains( dataType ) ) {
                            var.setDataType( dataType );
                        } else {
                            var.setCustomDataType( dataType );
                        }
                    }
                }
            }
        }
        return var;
    }

    /**
     * Deserializes a variable, NOT checking whether the datatype is custom
     *
     * @param s
     * @param variableType
     * @return
     */
    public static Variable deserialize( String s, VariableType variableType ) {
        return deserialize( s, variableType, null );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Variable ) ) return false;
        Variable variable = ( Variable ) o;
        if ( getVariableType() != variable.getVariableType() ) return false;
        if ( getName() != null ? !getName().equals( variable.getName() ) : variable.getName() != null ) return false;
        if ( getDataType() != null ? !getDataType().equals( variable.getDataType() ) : variable.getDataType() != null )
            return false;
        return getCustomDataType() != null ? getCustomDataType().equals( variable.getCustomDataType() ) : variable.getCustomDataType() == null;

    }

    @Override
    public int hashCode() {
        int result = getVariableType() != null ? getVariableType().hashCode() : 0;
        result = 31 * result + ( getName() != null ? getName().hashCode() : 0 );
        result = 31 * result + ( getDataType() != null ? getDataType().hashCode() : 0 );
        result = 31 * result + ( getCustomDataType() != null ? getCustomDataType().hashCode() : 0 );
        return result;
    }
}