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
package org.drools.workbench.jcr2vfsmigration.vfsImport;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.util.FileManager;
import org.drools.workbench.jcr2vfsmigration.xml.format.CategoriesXmlFormat;
import org.drools.workbench.jcr2vfsmigration.xml.model.Categories;
import org.drools.workbench.jcr2vfsmigration.xml.model.Category;
import org.guvnor.common.services.shared.metadata.CategoriesService;
import org.guvnor.common.services.shared.metadata.model.CategoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@ApplicationScoped
public class CategoryImporter {

    protected static final Logger logger = LoggerFactory.getLogger(CategoryImporter.class);

    @Inject
    FileManager fileManager;

    @Inject   
    CategoriesService categoriesService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    private CategoriesXmlFormat categoriesXmlFormat = new CategoriesXmlFormat();

    public void importAll() {
        System.out.println( "  Category import started" );

        Document xml = null;
        try {
            File categoriesXmlFile = fileManager.getCategoriesExportFile();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            xml = db.parse( categoriesXmlFile );

            NodeList children = xml.getChildNodes();
            if ( children.getLength() > 1 ) throw new IllegalArgumentException( "Wrong categories.xml format" );

            Categories xmlCategories = categoriesXmlFormat.parse( children.item( 0 ) );

            // Transform xml categories to vfs categories
            org.guvnor.common.services.shared.metadata.model.Categories vfsCategories =
                    new org.guvnor.common.services.shared.metadata.model.Categories();

            importCategories( xmlCategories.getCategories(), vfsCategories );

            categoriesService.save(migrationPathManager.generatePathForModule("categories.xml"), vfsCategories, null, "");
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        System.out.println( "  Category import ended" );
    }

    private void importCategories( Collection<Category> xmlCategoryCollection, CategoryItem vfsCategoryItem ) {
        for ( Iterator<Category> categoryIterator = xmlCategoryCollection.iterator(); categoryIterator.hasNext(); ) {
            Category xmlCategory = categoryIterator.next();

            CategoryItem categoryItem = vfsCategoryItem.addChildren( xmlCategory.getName(), "" );

            Categories xmlSubCategories = xmlCategory.getCategories();
            if ( xmlSubCategories != null ) {
                importCategories( xmlSubCategories.getCategories(), categoryItem );
            }
        }
    }
}
