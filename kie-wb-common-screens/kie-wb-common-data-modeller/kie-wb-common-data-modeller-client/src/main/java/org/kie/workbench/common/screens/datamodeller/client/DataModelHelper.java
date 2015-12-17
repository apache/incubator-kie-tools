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

package org.kie.workbench.common.screens.datamodeller.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerValueChangeEvent;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

import static org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils.*;

public class DataModelHelper {

    private DataModel dataModel;

    // Map linking Objects with the Objects they are being referenced by (e.g. x.y.A --> {u.v.B} means B holds a reference to A)
    private Map<String, Set<String>> referencedBy = new HashMap<String, Set<String>>(10);

    // Map linking DataObjects with the Objects they are referencing by (e.g. u.v.B --> {x.y.A} means B references A)
    private Map<String, Set<String>> references = new HashMap<String, Set<String>>(10);

    // Map that keeps track of the offspring a parent class has.
    private Map<String, Set<String>> offspringMap = new HashMap<String, Set<String>>(10);

    // Map of all class names and their corresponding labels (if any) that coexist within a project
    private Map<String, String> classNames = new HashMap<String, String>(10);

    // Map of all labelled class names that coexist within a project
    private Map<String, String> labelledClassNames = new TreeMap<String, String>();

    Map <String, PropertyType> orderedBaseTypes = new TreeMap<String, PropertyType>();

    Map <String, PropertyType> baseTypesByClassName = new HashMap<String, PropertyType>();

    String contextId;

    public DataModelHelper( String contextId ) {
        this.contextId = contextId;
    }

    public Collection<String> getDataObjectReferences( String className ) {
        Collection<String> c = new TreeSet<String>();

        Set<String> s = referencedBy.get( className );
        if ( s != null ) {
            c.addAll( s );
        }

        s = offspringMap.get( className );
        if ( s != null ) {
            c.addAll( s );
        }
        return c;
    }

    public String getObjectLabelByClassName(String className) {
        return classNames.get(className);
    }

    public List<String> getClassList() {
        List<String> l = new ArrayList<String>(classNames.size());
        l.addAll(classNames.keySet());
        return Collections.unmodifiableList(l);
    }

    public Map<String, String> getLabelledClassMap() {
        return Collections.unmodifiableMap(labelledClassNames);
    }

    public Map <String, PropertyType> getOrderedBaseTypes() {
        return orderedBaseTypes;
    }

    // DataModelHelper methods

    public void dataModelChanged(DataModelerValueChangeEvent changeEvent) {
        if (changeEvent.isFromContext( contextId )) {
            if (DataModelerEvent.DATA_OBJECT_EDITOR.equalsIgnoreCase(changeEvent.getSource())) {
                // If any object referenced the object whose name or package just changed, we need to adjust those internally
                if ("name".equals(changeEvent.getValueName())) nameChanged( changeEvent.getCurrentDataObject(),
                                                                               (String) changeEvent.getOldValue(),
                                                                               (String) changeEvent.getNewValue());
                else if ("packageName".equals(changeEvent.getValueName())) packageChanged( changeEvent.getCurrentDataObject(),
                                                                                              (String) changeEvent.getOldValue(),
                                                                                              (String) changeEvent.getNewValue());
            }
            reset();
        }
    }

    private void nameChanged(DataObject object, String oldName, String newName) {
        adjustDataObjects( assembleClassName(object.getPackageName(), oldName),
                           assembleClassName(object.getPackageName(), newName) );
    }

    private void packageChanged(DataObject object, String oldPackage, String newPackage) {
        adjustDataObjects( assembleClassName(oldPackage, object.getName()),
                           assembleClassName(newPackage, object.getName()) );
    }

    private void adjustDataObjects(String oldClassName, String newClassname) {
        //TODO, WM, this method seems to be no longer needed after we changed to 1:1 edition
        /*
        Set<String> s = referencedBy.get(oldClassName);
        if ( s != null && s.size() != 0 ) {
            // Get the object referencing the modified object
            for (String refHolderClassName : s) {
                // Go get the referencing object (in case of a 'self' reference, find it through the new name!)
                DataObjectTO refHolder = dataModel.getDataObjectByClassName(oldClassName.equals(refHolderClassName) ? newClassname : refHolderClassName);
                for (ObjectPropertyTO prop : refHolder.getProperties()) {
                    if (oldClassName.equals(prop.getClassName())) {
                        prop.setClassName(newClassname);
                    }
                }
            }
        }
        s = offspringMap.get(oldClassName);
        if ( s != null && s.size() != 0 ) {
            for (String offspringClassName : s) {
                DataObjectTO offspring = dataModel.getDataObjectByClassName(offspringClassName);
                offspring.setSuperClassName(newClassname);
            }
        }
        */
    }

    public void dataObjectReferenced(String objectClassName, String subjectClassName) {
        objectReferenced(objectClassName, subjectClassName);
    }

    public void dataObjectUnReferenced(String objectClassName, String subjectClassName) {
        objectUnReferenced(objectClassName, subjectClassName);
    }

    public void dataObjectExtended(String parentClassName, String offspringClassName, Boolean _extends) {
        objectExtended(parentClassName, offspringClassName, _extends);
    }

    public void dataObjectDeleted(String objectClassName) {
        reset();
    }

    public void dataObjectCreated(String objectClassName) {
        reset();
    }

    public void dataObjectSelected(String objectClassName) {
    }

    public void dataObjectUnSelected(String objectClassName) {
    }

    public Boolean isBaseType(String type) {
        return baseTypesByClassName.containsKey(type);
    }

    public Boolean isPrimitiveType(String type) {
        PropertyType propertyType;
        return ( propertyType = baseTypesByClassName.get( type ) ) != null && propertyType.isPrimitive();
    }

    /**
     * Evaluate if an object can safely extend another one (at least as far as the extension hierarchy is concerned).
     * @param offspringCandidate The class name of the extending object
     * @param parentCandidate The class name of the extended object
     * @return True if the extension does not provoke a conflict with the existing extension hierarchy.
     */
    public Boolean isAssignableFrom(String offspringCandidate, String parentCandidate) {
        if (offspringCandidate == null || offspringCandidate.length() == 0 ||
            parentCandidate == null || parentCandidate.length() == 0 ||
            offspringCandidate.equals(parentCandidate)) return false;

        Set<String> candidatesOffspring = offspringMap.get(offspringCandidate);
        boolean candidateHasOffspring = candidatesOffspring != null && candidatesOffspring.size() > 0;

        if (candidateHasOffspring) {
            if (candidatesOffspring.contains(parentCandidate)) return false;
            for (String newOffspringCandidate : candidatesOffspring) {
                if (!isAssignableFrom(newOffspringCandidate, parentCandidate)) return false;
            }
        }

        return true;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
        reset();
    }

    public void setBaseTypes(List<PropertyType> baseTypes) {
        if (baseTypes != null) {
            for (PropertyType type : baseTypes) {
                orderedBaseTypes.put(type.getName(), type);
                baseTypesByClassName.put(type.getClassName(), type);
            }
        }
    }

    // Todo can be improved if required for performance reasons
    // Brute force recalculate all
    private void reset() {
        referencedBy.clear();
        references.clear();
        classNames.clear();
        labelledClassNames.clear();
        offspringMap.clear();
        if (dataModel != null) {
            for (DataObject extClassName : dataModel.getExternalClasses()) {
                classNames.put(extClassName.getClassName(), null);
                labelledClassNames.put(extClassName.getClassName(), extClassName.getClassName());
            }
            for (DataObject object : dataModel.getDataObjects()) {
                String className = object.getClassName();
                classNames.put(className, AnnotationValueHandler.getStringValue( object, MainDomainAnnotations.LABEL_ANNOTATION ));
                labelledClassNames.put(DataModelerUtils.getDataObjectFullLabel(object), className);

                String superClassName = object.getSuperClassName();
                if (superClassName != null &&  !"".equals(superClassName)) objectExtended(superClassName, className, true);

                for (ObjectProperty prop : object.getProperties()) {
                    if (!prop.isBaseType()) {
                        objectReferenced(prop.getClassName(), object.getClassName());
                    }
                }
            }
        }
    }

    private void objectReferenced(String objectClassName, String subjectClassName) {
        Set<String> refs = referencedBy.get(objectClassName);
        if (refs == null) refs = new HashSet<String>();
        refs.add(subjectClassName);
        referencedBy.put(objectClassName, refs);

        refs = references.get(subjectClassName);
        if (refs == null) refs = new HashSet<String>();
        refs.add(objectClassName);
        references.put(subjectClassName, refs);
    }

    private void objectUnReferenced(String objectClassName, String subjectClassName) {
        Set<String> refs = referencedBy.get(objectClassName);
        if (refs != null && refs.size() > 0) {
            refs.remove(subjectClassName);
        }
//        else ("Error de-referencing data object (referenced object)."));

        refs = references.get(subjectClassName);
        if (refs != null && refs.size() > 0) {
            refs.remove(objectClassName);
        }
//        else ("Error de-referencing data object (referring object)."));
    }

    private void objectExtended(String parentClassName, String offspringClassName, Boolean _extends) {
        Set<String> _offspring = offspringMap.get(parentClassName);

        if (_extends) {
            if (_offspring != null ) {
                _offspring.add( offspringClassName );
            } else {
                _offspring = new HashSet<String>();
                _offspring.add(offspringClassName);
                offspringMap.put(parentClassName, _offspring);
            }
        } else {
            if (_offspring != null ) {
                if ( _offspring.size() > 0) {
                    _offspring.remove(offspringClassName);
                }
                if (_offspring.size() == 0) {
                    offspringMap.remove(parentClassName);
                }
//            else ("Superclass referencing error"));
            }
        }
    }
}
