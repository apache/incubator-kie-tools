/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.properties.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyEditorEventTest {

    @Test
    public void sortCategoryByPriority() {
        List<PropertyEditorCategory> categories = new ArrayList<PropertyEditorCategory>();
        PropertyEditorCategory category1 = new PropertyEditorCategory( "cat1", 2 );
        categories.add( category1 );
        PropertyEditorCategory category2 = new PropertyEditorCategory( "catHighPriority", 1 );
        categories.add( category2 );
        PropertyEditorEvent event = new PropertyEditorEvent( "event", categories );
        assertEquals( category2, event.getSortedProperties().get( 0 ) );
        assertEquals( category1, event.getSortedProperties().get( 1 ) );
    }

    @Test
    public void sortFieldsByPriority() {
        PropertyEditorFieldInfo highPriority = new PropertyEditorFieldInfo( "highPriority", PropertyEditorType.BOOLEAN ).withPriority( 1 );
        PropertyEditorFieldInfo lowPriority = new PropertyEditorFieldInfo( "lowPriority", PropertyEditorType.BOOLEAN ).withPriority( 10 );
        PropertyEditorCategory category = new PropertyEditorCategory( "cat1", 2 )
                .withField( lowPriority )
                .withField( highPriority );

        PropertyEditorEvent event = new PropertyEditorEvent( "event", category );
        assertEquals( highPriority, event.getSortedProperties().get( 0 ).getFields().get( 0 ) );
        assertEquals( lowPriority, event.getSortedProperties().get( 0 ).getFields().get( 1 ) );

    }
}
