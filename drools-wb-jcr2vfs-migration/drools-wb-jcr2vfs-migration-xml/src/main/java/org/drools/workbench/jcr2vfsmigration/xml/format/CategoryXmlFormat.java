/*
 * Copyright 2014 JBoss Inc
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
package org.drools.workbench.jcr2vfsmigration.xml.format;

import org.drools.workbench.jcr2vfsmigration.xml.model.Categories;
import org.drools.workbench.jcr2vfsmigration.xml.model.Category;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CategoryXmlFormat implements XmlFormat<Category> {

    public static final String CATEGORY = "category";
    public static final String CATEGORY_NAME = "name";

    private CategoriesXmlFormat categoriesXmlFormat;

    public CategoryXmlFormat() {
    }

    @Override
    public void format( StringBuilder sb, Category category ) {
        if ( sb == null || category == null ) throw new IllegalArgumentException( "No output or Category specified" );

        initialize();
        sb.append( LT ).append( CATEGORY ).append( " " ).append( CATEGORY_NAME ).append( "=\"" ).append( category.getName() ).append( "\"" ).append( GT );
        if ( category.getCategories() != null ) {
            if ( categoriesXmlFormat == null ) categoriesXmlFormat = new CategoriesXmlFormat();
            categoriesXmlFormat.format( sb, category.getCategories() );
        }
        sb.append( LT_SLASH ).append( CATEGORY ).append( GT );
        System.out.format( "Category [%s] exported. %n", category.getName() );
    }

    @Override
    public Category parse( Node categoryNode ) {
        if ( categoryNode == null || !CATEGORY.equals( categoryNode.getNodeName() ) ) throw new IllegalArgumentException( "No input category node specified for parsing" );

        initialize();
        String name = null;
        Categories categories = null;

        NamedNodeMap attrs = categoryNode.getAttributes();
        if ( attrs != null ) {
            name = attrs.getNamedItem( CATEGORY_NAME ).getNodeValue();
        }

        NodeList subCategories = categoryNode.getChildNodes();
        if ( subCategories.getLength() > 1 ) throw new RuntimeException( "Only one Categories element is allowed inside a Category" );

        if ( subCategories.getLength() == 1 ) {
            categories = categoriesXmlFormat.parse( subCategories.item( 0 ) );
        }

        return new Category( name, categories );
    }

    // Don't do this in constructor: will lead to stackoverflow because of recursive calls
    private void initialize() {
        if ( categoriesXmlFormat == null ) categoriesXmlFormat = new CategoriesXmlFormat();
    }
}
