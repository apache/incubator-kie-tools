/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;

public class DataModelUtils {

    /**
     * @return true if a given property is assignable and thus can be used in setter/getter methods and constructors.
     */
    public static boolean isAssignable( ObjectProperty property ) {
        return property != null && !property.isFinal() && !property.isStatic();
    }

    /**
     * @return true if a given property is marked as a key field property.
     */
    public static boolean isKeyField( ObjectProperty property ) {
        return property != null && property.getAnnotation( Key.class.getName() ) != null;
    }

    /**
     * @return true if a given property is marked as a position field property.
     */
    public static boolean isPositionField( ObjectProperty property ) {
        return property != null && property.getAnnotation( Position.class.getName() ) != null;
    }

    public static int keyFieldsCount( DataObject dataObject ) {
        int result = 0;
        for ( ObjectProperty property : dataObject.getProperties() ) {
            if ( isKeyField( property ) ) {
                result++;
            }
        }
        return result;
    }

    public static List<ObjectProperty> filterAssignableFields( List<ObjectProperty> properties ) {
        List<ObjectProperty> result = new ArrayList<ObjectProperty>();
        if ( properties != null ) {
            for ( ObjectProperty property : properties ) {
                if ( isAssignable( property ) ) {
                    result.add( property );
                }
            }
        }
        return result;
    }

    public static List<ObjectProperty> filterAssignableFields( DataObject dataObject ) {
        List<ObjectProperty> result = new ArrayList<ObjectProperty>();
        if ( dataObject.getProperties() != null && dataObject.getProperties().size() > 0 ) {
            result.addAll( dataObject.getProperties() );
            result = filterAssignableFields( result );
        }
        return result;
    }

    public static List<ObjectProperty> filterKeyFields( List<ObjectProperty> properties ) {
        List<ObjectProperty> result = new ArrayList<ObjectProperty>();
        for ( ObjectProperty property : filterAssignableFields( properties ) ) {
            if ( isKeyField( property ) ) {
                result.add( property );
            }
        }
        return result;
    }

    public static List<ObjectProperty> filterKeyFields( DataObject dataObject ) {
        List<ObjectProperty> result = new ArrayList<ObjectProperty>();
        if ( dataObject.getProperties() != null && dataObject.getProperties().size() > 0 ) {
            result.addAll( dataObject.getProperties() );
            result = filterKeyFields( result );
        }
        return result;
    }

    public static List<ObjectProperty> filterPositionFields( List<ObjectProperty> properties ) {
        List<ObjectProperty> result = new ArrayList<ObjectProperty>();
        for ( ObjectProperty property : filterAssignableFields( properties ) ) {
            if ( isPositionField( property ) ) {
                result.add( property );
            }
        }
        return result;
    }

    public static List<ObjectProperty> filterPositionFields( DataObject dataObject ) {
        List<ObjectProperty> result = new ArrayList<ObjectProperty>();
        if ( dataObject.getProperties() != null && dataObject.getProperties().size() > 0 ) {
            result.addAll( dataObject.getProperties() );
            result = filterPositionFields( result );
        }
        return result;
    }

    public static boolean equalsByFieldName( List<ObjectProperty> fields1, List<ObjectProperty> fields2 ) {
        if ( fields1 == null ) {
            return fields2 == null;
        }
        if ( fields2 == null ) {
            return false;
        }
        if ( fields1.size() != fields2.size() ) {
            return false;
        }
        for ( int i = 0; i < fields1.size(); i++ ) {
            if ( !fields1.get( i ).getName().equals( fields2.get( i ).getName() ) ) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsByFieldType( List<ObjectProperty> fields1, List<ObjectProperty> fields2 ) {
        if ( fields1 == null ) {
            return fields2 == null;
        }
        if ( fields2 == null ) {
            return false;
        }
        if ( fields1.size() != fields2.size() ) {
            return false;
        }
        ObjectProperty field1;
        ObjectProperty field2;
        for ( int i = 0; i < fields1.size(); i++ ) {
            field1 = fields1.get( i );
            field2 = fields2.get( i );
            if ( !Objects.equals( field1.getClassName(), field2.getClassName() ) ) {
                return false;
            }
            if ( field1.isMultiple() != field2.isMultiple() ) {
                return false;
            }
            if ( !Objects.equals( field1.getBag(), field2.getBag() ) ) {
                return false;
            }
        }
        return true;
    }

    public static int positionFieldsCount( DataObject dataObject ) {
        int result = 0;
        for ( ObjectProperty property : dataObject.getProperties() ) {
            if ( isPositionField( property ) ) {
                result++;
            }
        }
        return result;
    }

    public static int assignableFieldsCount( DataObject dataObject ) {
        int result = 0;
        for ( ObjectProperty property : dataObject.getProperties() ) {
            if ( isAssignable( property ) ) {
                result++;
            }
        }
        return result;
    }

    public static List<ObjectProperty> sortByPosition( List<ObjectProperty> properties ) {
        Collections.sort( properties, new Comparator<ObjectProperty>() {
            public int compare( ObjectProperty o1, ObjectProperty o2 ) {

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
                Object value;

                Annotation position1 = o1.getAnnotation( Position.class.getName() );
                if ( position1 != null ) {
                    try {
                        value = position1.getValue( "value" );
                        if ( value != null ) {
                            key1 = new Integer( value.toString()  );
                        } else {
                            key1 = null;
                        }
                    } catch ( NumberFormatException e ) {
                        key1 = null;
                    }
                }

                Annotation position2 = o2.getAnnotation( Position.class.getName() );
                if ( position2 != null ) {
                    try {
                        value = position2.getValue( "value" );
                        if ( value != null ) {
                            key2 = new Integer( value.toString() );
                        } else {
                            key2 = null;
                        }
                    } catch ( NumberFormatException e ) {
                        key2 = null;
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
        } );
        return properties;
    }

    public static List<ObjectProperty> sortByFileOrder( List<ObjectProperty> properties ) {
        Collections.sort( properties, new Comparator<ObjectProperty>() {
            public int compare( ObjectProperty o1, ObjectProperty o2 ) {

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

                int ikey1 = ( ( ObjectPropertyImpl ) o1 ).getFileOrder();
                int ikey2 = ( ( ObjectPropertyImpl ) o2 ).getFileOrder();

                key1 = ikey1 >= 0 ? ikey1 : null;
                key2 = ikey2 >= 0 ? ikey2 : null;

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
        } );
        return properties;
    }

}
