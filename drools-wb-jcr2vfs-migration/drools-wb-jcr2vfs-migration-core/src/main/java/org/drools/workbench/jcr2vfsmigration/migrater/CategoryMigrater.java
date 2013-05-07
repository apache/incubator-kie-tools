package org.drools.workbench.jcr2vfsmigration.migrater;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.server.RepositoryCategoryService;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO Maybe we should make one per asset type? Or delegate to one per asset type?
@ApplicationScoped
public class CategoryMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(CategoryMigrater.class);

    @Inject
    protected RepositoryCategoryService jcrRepositoryCategoryService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    public void migrateAll() {
        logger.info("  Category migration started");
        // TODO similar like ModuleMigrater and assetMigrater
        logger.debug("      TODO migrate categories.");
        logger.info("  Category migration ended");
    }

}
