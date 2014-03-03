package org.drools.workbench.jcr2vfsmigration.migrater;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.server.RepositoryCategoryService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.guvnor.common.services.shared.metadata.CategoriesService;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.guvnor.common.services.shared.metadata.model.CategoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;

@ApplicationScoped
public class CategoryMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(CategoryMigrater.class);

    @Inject
    protected RepositoryCategoryService jcrRepositoryCategoryService;

    @Inject   
    CategoriesService categoriesService;

    @Inject
    protected MigrationPathManager migrationPathManager;
    
    public void migrateAll() {
        System.out.println( "  Category migration started" );

        Categories vfsCategories = new Categories();
        loadChildCategories("/", vfsCategories);        

        categoriesService.save(migrationPathManager.generatePathForModule("categories.xml"), vfsCategories,null,"");
        
        System.out.println( "  Category migration ended" );
    }
    
    private void loadChildCategories(String category, CategoryItem vfsCategoryItem) {
        String[] categories = jcrRepositoryCategoryService.loadChildCategories(category);

        for(String c : categories) {
        	String categoryPath = getItemPath(c, vfsCategoryItem.getFullPath());
        	CategoryItem categoryItem = vfsCategoryItem.addChildren(c, "");
            loadChildCategories(categoryPath,categoryItem);
        }
    }
    private String getItemPath(String categoryName, String parentItemPath) {
        String path;
        if ( isParentRoot( parentItemPath ) ) {
            path = parentItemPath + categoryName;
        } else {
            path = parentItemPath + "/" + categoryName;
        }
        return path;
    }

    private boolean isParentRoot(String parentItemPath) {
        return parentItemPath.equals( "/" );
    }
}
