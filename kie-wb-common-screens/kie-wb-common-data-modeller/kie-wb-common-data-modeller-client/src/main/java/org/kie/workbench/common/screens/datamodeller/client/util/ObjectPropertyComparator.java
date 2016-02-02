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

package org.kie.workbench.common.screens.datamodeller.client.util;

import java.util.Comparator;

import org.kie.workbench.common.screens.datamodeller.model.droolsdomain.DroolsDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

public class ObjectPropertyComparator implements Comparator<ObjectProperty> {

    private String field;

    private boolean ascending = true;

    public ObjectPropertyComparator( String field ) {
        this.field = field;
        this.ascending = true;
    }

    public ObjectPropertyComparator( String field, boolean ascending ) {
        this.field = field;
        this.ascending = ascending;
    }

    @Override
    public int compare( ObjectProperty o1, ObjectProperty o2 ) {
        return ascending ? _compare( o1, o2 ) : -_compare( o1, o2 );
    }

    /**
     * Implements the ascending comparation.
     */
    private int _compare( ObjectProperty o1, ObjectProperty o2 ) {
        if ( o1 == null && o2 == null ) {
            return 0;
        }
        if ( o1 == null && o2 != null ) {
            return -1;
        }
        if ( o1 != null && o2 == null ) {
            return 1;
        }

        Comparable key1 = null;
        Comparable key2 = null;

        if ( "className".equals( field ) ) {
            key1 = o1.getClassName();
            key2 = o2.getClassName();
        } else if ( "name".equals( field ) ) {
            // By default compare by name
            key1 = o1.getName();
            key2 = o2.getName();
        } else if ( "label".equals( field ) ) {
            key1 = AnnotationValueHandler.getStringValue( o1, MainDomainAnnotations.LABEL_ANNOTATION, MainDomainAnnotations.VALUE_PARAM );
            key2 = AnnotationValueHandler.getStringValue( o2, MainDomainAnnotations.LABEL_ANNOTATION, MainDomainAnnotations.VALUE_PARAM );
        } else if ( "position".equals( field ) ) {
            key1 = AnnotationValueHandler.getStringValue( o1, DroolsDomainAnnotations.POSITION_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM );
            key2 = AnnotationValueHandler.getStringValue( o2, DroolsDomainAnnotations.POSITION_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM );
            if ( key1 != null ) {
                try {
                    key1 = new Integer( key1.toString() );
                } catch ( NumberFormatException e ) {
                    key1 = null;
                }
            }
            if ( key2 != null ) {
                try {
                    key2 = new Integer( key2.toString() );
                } catch ( NumberFormatException e ) {
                    key2 = null;
                }
            }
        }

        if ( key1 == null && key2 == null ) {
            return 0;
        }
        if ( key1 != null && key2 != null ) {
            return key1.compareTo( key2 );
        }

        if ( key1 == null && key2 != null ) {
            return -1;
        }

        //if (key1 != null && key2 == null) return 1;
        return 1;

    }
}