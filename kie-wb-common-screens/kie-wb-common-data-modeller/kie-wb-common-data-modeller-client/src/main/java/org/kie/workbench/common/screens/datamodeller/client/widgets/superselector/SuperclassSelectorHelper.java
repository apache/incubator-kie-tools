/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.superselector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.uberfire.commons.data.Pair;

public class SuperclassSelectorHelper {

    public static List<Pair<String, String>> buildSuperclassSelectorOptions( DataModel dataModel,
            DataObject currentDataObject ) {

        List<Pair<String, String>> options = new ArrayList<Pair<String, String>>();

        if ( dataModel != null ) {
            SortedMap<String, String> sortedModelClasses = new TreeMap<String, String>();
            SortedMap<String, String> sortedExternalClasses = new TreeMap<String, String>();
            boolean isExtensible = false;
            String className;
            String classLabel;
            String currentClassName;


            // first, all data objects form this model in order
            for ( DataObject internalDataObject : dataModel.getDataObjects() ) {
                className = internalDataObject.getClassName();
                classLabel = DataModelerUtils.getDataObjectFullLabel( internalDataObject );
                isExtensible = !internalDataObject.isAbstract() && !internalDataObject.isFinal() && !internalDataObject.isInterface();
                if ( isExtensible ) {
                    if ( currentDataObject != null && className.toLowerCase().equals( currentDataObject.getClassName().toLowerCase() ) )
                        continue;
                    sortedModelClasses.put( classLabel, className );
                }
            }

            // Then add all external types, ordered
            for ( DataObject externalDataObject : dataModel.getExternalClasses() ) {
                className = externalDataObject.getClassName();
                classLabel = DataModelerUtils.EXTERNAL_PREFIX + className;
                isExtensible = !externalDataObject.isAbstract() && !externalDataObject.isFinal() && !externalDataObject.isInterface();
                if ( isExtensible ) {
                    if ( currentDataObject != null && className.toLowerCase().equals( currentDataObject.getClassName().toLowerCase() ) )
                        continue;
                    sortedExternalClasses.put( classLabel, className );
                }
            }

            if ( currentDataObject != null && currentDataObject.getSuperClassName() != null ) {
                currentClassName = currentDataObject.getSuperClassName();
                if ( !sortedModelClasses.containsKey( currentClassName ) && !sortedExternalClasses.containsKey( currentClassName ) ) {
                    //the model was loaded but the super class is not a model class nor an external class, e.g. java.lang.Object. Still needs to be loaded.
                    sortedModelClasses.put( currentClassName, currentClassName );
                }
            }

            for ( Map.Entry<String, String> classNameEntry : sortedModelClasses.entrySet() ) {
                options.add( new Pair( classNameEntry.getKey(), classNameEntry.getValue()) );
            }

            for ( Map.Entry<String, String> classNameEntry : sortedExternalClasses.entrySet() ) {
                options.add( new Pair( classNameEntry.getKey(), classNameEntry.getValue()) );
            }
        }
        return options;
    }

}
