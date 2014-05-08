package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.kie.workbench.common.services.datamodeller.core.AnnotationMemberDefinition;

public class AnnotationMemberDefinitionImpl implements AnnotationMemberDefinition {

    private String name;

    private String shortDescription;

    private String description;
    
    private String className;
    
    private Object defaultValue;

    private boolean enumMember = false;

    public AnnotationMemberDefinitionImpl(String name, String className, boolean enumMember, String shortDescription, String description) {
        this(name, className, enumMember, null, shortDescription, description);
    }

    public AnnotationMemberDefinitionImpl(String name, String className, boolean enumMember, Object defaultValue, String shortDescription, String description) {
        this.name = name;
        this.className = className;
        this.enumMember = enumMember;
        this.defaultValue = defaultValue;
        this.shortDescription = shortDescription;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object defaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isPrimitiveType() {
        return NamingUtils.isPrimitiveTypeClass(className);
    }

    @Override
    public boolean isString() {
        return String.class.getName().equals(className);
    }

    @Override
    public boolean isEnum() {
        return enumMember;
    }

    @Override
    public boolean isArray() {
        //return getClassName() != null && className.endsWith("[]");
        //not supported yet.
        return false;
    }
}