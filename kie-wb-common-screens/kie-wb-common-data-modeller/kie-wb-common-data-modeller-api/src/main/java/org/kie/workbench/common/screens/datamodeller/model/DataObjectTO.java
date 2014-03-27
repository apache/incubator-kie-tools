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

import java.util.ArrayList;
import java.util.List;

@Portable
public class DataObjectTO {

    /*
     * Data objects that was read form persistent status, .java files.
     */
    public static final Integer PERSISTENT = 0;


    /*
     * Data objects that was created in memory an was not saved to persistent .java file yet.
     */
    public static final Integer VOLATILE = 1;


    /*
     * Data objects that wasn't created by the data modeller, or was modified by an external editor and pushed to
     * the project repository.
     */
    public static final Integer PERSISTENT_EXTERNALLY_MODIFIED = 2;

    private String name;
    
    private String packageName;
    
    private String superClassName;
    
    private DataModelTO.TOStatus status = DataModelTO.TOStatus.VOLATILE;


    //Remembers the original name for the DataObject.
    //This value shouldn't be changed.
    private String originalClassName;

    private List<ObjectPropertyTO> properties = new ArrayList<ObjectPropertyTO>();

    private List<AnnotationTO> annotations = new ArrayList<AnnotationTO>();

    private String fingerPrint;

    private boolean abstractModifier = false;

    private boolean interfaceModifier = false;

    private boolean finalModifier = false;

    public DataObjectTO() {
    }

    public DataObjectTO(String name, String packageName, String superClassName) {
        this(name, packageName, superClassName, false, false, false);
    }

    public DataObjectTO(String name, String packageName, String superClassName, boolean abstractModifier, boolean interfaceModifier, boolean finalModifier) {
        this.name = name;
        this.packageName = packageName;
        this.superClassName = superClassName;
        this.abstractModifier = abstractModifier;
        this.interfaceModifier = interfaceModifier;
        this.finalModifier = finalModifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return ( (packageName != null && !"".equals(packageName)) ? packageName+"." : "") + getName();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<ObjectPropertyTO> getProperties() {
        return properties;
    }

    public ObjectPropertyTO getProperty(String name) {
        for (ObjectPropertyTO property : properties) {
            if (property.getName().equals(name)) return property;
        }
        return null;
    }

    public void setProperties(List<ObjectPropertyTO> properties) {
        this.properties = properties;
    }

    public String getOriginalClassName() {
        return originalClassName;
    }

    public void setOriginalClassName(String originalClassName) {
        this.originalClassName = originalClassName;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public DataModelTO.TOStatus getStatus() {
        return status;
    }

    public void setStatus(DataModelTO.TOStatus status) {
        this.status = status;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public boolean isVolatile() {
        return getStatus() == DataModelTO.TOStatus.VOLATILE;
    }
    
    public boolean isPersistent() {
        return getStatus() == DataModelTO.TOStatus.PERSISTENT;
    }

    public boolean isExternallyModified() {
        return getStatus() == DataModelTO.TOStatus.PERSISTENT_EXTERNALLY_MODIFIED;
    }

    public boolean classNameChanged() {
        return !isVolatile() && !getClassName().equals(getOriginalClassName());
    }

    public boolean packageNameChanged() {
        if (!isVolatile()) {
            //extract package name.
            int index = getOriginalClassName().lastIndexOf(".");
            String originalPackageName = "";
            if (index > 0) {
                originalPackageName = getOriginalClassName().substring(0, index);
                return originalPackageName.equals(getPackageName());
            } else {
                return getPackageName() != null;
            }
        }
        return false;
    }

    public String getLabel() {
        AnnotationTO annotation = getAnnotation(AnnotationDefinitionTO.LABEL_ANNOTATION);
        if (annotation != null) {
            String label = annotation.getValue(AnnotationDefinitionTO.VALUE_PARAM).toString();
            return (label != null && !"".equals(label)) ? label : getName();
        }
        return null;
    }

    public List<AnnotationTO> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<AnnotationTO> annotations) {
        this.annotations = annotations;
    }

    public AnnotationTO getAnnotation(String annotationClassName) {
        AnnotationTO annotation = null;
        int index = _getAnnotation(annotationClassName);
        if (index >= 0) annotation = annotations.get(_getAnnotation(annotationClassName));
        return annotation;
    }

    public void addAnnotation(AnnotationTO annotation) {
        annotations.add(annotation);
    }

    public AnnotationTO addAnnotation(AnnotationDefinitionTO annotationDefinitionTO, String memberName, Object value) {
        AnnotationTO annotation = new AnnotationTO(annotationDefinitionTO);
        annotation.setValue(memberName, value);
        addAnnotation(annotation);
        return annotation;
    }

    public void removeAnnotation(AnnotationTO annotation) {
        if (annotation != null) {
            int index = _getAnnotation(annotation.getClassName());
            if (index >= 0) annotations.remove(index);
        }
    }

    private Integer _getAnnotation(String annotationClassName) {
        if (annotationClassName == null || "".equals(annotationClassName)) return -1;
        for (int i = 0; i < annotations.size(); i++) {
            AnnotationTO _annotation = annotations.get(i);
            if (annotationClassName.equals(_annotation.getClassName())) return i;
        }
        return -1;
    }
    
    public String getStringId() {
        StringBuilder strId = new StringBuilder();
        strId.append(getClassName());
        strId.append(getSuperClassName());
        for (AnnotationTO annotationTO : getAnnotations()) {
            strId.append(annotationTO.getStringId());
        }
        for (ObjectPropertyTO property : getProperties()) {
            strId.append(property.getStringId());
        }
        return strId.toString();
    }

    public boolean isAbstract() {
        return abstractModifier;
    }

    public boolean isFinal() {
        return finalModifier;
    }

    public boolean isInterface() {
        return interfaceModifier;
    }

}