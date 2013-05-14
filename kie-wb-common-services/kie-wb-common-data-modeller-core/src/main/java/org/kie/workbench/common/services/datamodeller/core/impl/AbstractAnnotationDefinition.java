package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationMemberDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AbstractAnnotationDefinition implements AnnotationDefinition {
    
    protected String name;

    protected String shortDescription;

    protected String description;
    
    protected String className;

    protected List<AnnotationMemberDefinition> annotationMembers = new ArrayList<AnnotationMemberDefinition> ();
    
    protected Map<String, AnnotationMemberDefinition> annotationMemberMap = new HashMap<String, AnnotationMemberDefinition>();

    protected boolean objectAnnotation = false;

    protected boolean propertyAnnotation = false;

    protected AbstractAnnotationDefinition(String name, String className, String shortDescription, String description, boolean objectAnnotation, boolean propertyAnnotation) {
        this.name = name;
        this.className = className;
        this.shortDescription = shortDescription;
        this.description = description;
        this.objectAnnotation = objectAnnotation;
        this.propertyAnnotation = propertyAnnotation;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getShortDescription() {
        return shortDescription;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<AnnotationMemberDefinition> getAnnotationMembers() {
        return annotationMembers;
    }

    public void addMember(AnnotationMemberDefinition annotationMember) {
        annotationMembers.add(annotationMember);
        annotationMemberMap.put(annotationMember.getName(), annotationMember);
    }

    @Override
    public boolean isMarker() {
        return annotationMembers == null || annotationMembers.size() == 0;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public boolean isObjectAnnotation() {
        return objectAnnotation;
    }

    @Override
    public boolean isPropertyAnnotation() {
        return propertyAnnotation;
    }

    @Override
    public boolean hasMember(String name) {
        return annotationMemberMap.containsKey(name);
    }
}
