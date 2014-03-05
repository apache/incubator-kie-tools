package org.drools.workbench.jcr2vfsmigration.migrater.asset;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.rpc.DiscussionRecord;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryAssetService;
import org.drools.guvnor.server.util.Discussion;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import com.google.gwt.user.client.rpc.SerializationException;

@ApplicationScoped
public class BaseAssetMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(BaseAssetMigrater.class);

    @Inject
    protected RepositoryAssetService jcrRepositoryAssetService;

    @Inject
    private Paths paths;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    protected MetadataService metadataService;

    public Map<String, Object> migrateMetaData(Module jcrModule, AssetItem jcrAssetItem) {
/*        System.out.format("    Metadata: Asset [%s] with format [%s] is being migrated... \n",
        		jcrAssetItem.getName(), jcrAssetItem.getFormat());       */

        //avoid using RepositoryAssetService as it calls assets' content handler
        Metadata metadata = new Metadata();

        List<DiscussionRecord> discussions = new Discussion().fromString(jcrAssetItem.getStringProperty(Discussion.DISCUSSION_PROPERTY_KEY));

        if (discussions.size() != 0) {
            //final org.kie.commons.java.nio.file.Path nioPath = paths.convert( path );
            for (DiscussionRecord discussion : discussions) {
                metadata.addDiscussion(new org.guvnor.common.services.shared.metadata.model.DiscussionRecord(discussion.timestamp, discussion.author, discussion.note));
            }
        }

        //System.out.format("    Metadata: setDescription... \n" + jcrAssetItem.getDescription());

        metadata.setDescription(jcrAssetItem.getDescription());
        metadata.setSubject(jcrAssetItem.getSubject());
        metadata.setExternalRelation(jcrAssetItem.getExternalRelation());
        metadata.setExternalSource(jcrAssetItem.getExternalSource());
        List<CategoryItem> jcrCategories = jcrAssetItem.getCategories();
        for (CategoryItem c : jcrCategories) {
            //System.out.format("    Metadata: addCategory... \n" + c.getFullPath());
            metadata.addCategory(c.getFullPath());
        }

        Path path = migrationPathManager.generatePathForAsset(jcrModule, jcrAssetItem);
        return metadataService.setUpAttributes(path, metadata);

        //System.out.format("    Metadata migration done.\n");
    }

    /**
     * Retrieves form jcrModule the categoryRules and deduce the rule to extend depending of the assetItem categories
     * retrieve the rule name between ruleDelimiter
     * @param jcrModule module which has the category rule defined
     * @param jcrAssetItem asset with the categories, which can extend the rule
     * @param ruleDelimiter The delimiter used to contruct the return value
     * @return the rule to extend depending of the asset category and the category rules defined by package between ruleDelimiter
     */
    public String getExtendedRuleFromCategoryRules(Module jcrModule, AssetItem jcrAssetItem,String ruleDelimiter) {

        HashMap catRuleHashMap = new HashMap();
        String ruleName;
        // Retrieve the module ruleCategories and constuct a hashmap, catRuleHashMap {"categoryName","ruleToExtend"}
        if (jcrModule.getCatRules() != null &&
                jcrModule.getCatRules().keySet() != null &&
                jcrModule.getCatRules().keySet().size() > 0) {  // categoryRules threatament
            for (Iterator it = jcrModule.getCatRules().keySet().iterator(); it.hasNext(); ) {
                ruleName = (String) it.next();
                catRuleHashMap.put(jcrModule.getCatRules().get(ruleName), ruleName);
            }
        }
        // Now iterate by the asset categories, and construct  the extendRuleExpression if the category is in catRuleHashMap
        List<org.drools.repository.CategoryItem> assetCategories = jcrAssetItem.getCategories();
        StringBuilder extendCategoriesBuilder = new StringBuilder();
        int i = 0;
        for (CategoryItem categoryItem : assetCategories) {
            ruleName = (String) catRuleHashMap.get(categoryItem.getName());
            if (ruleName != null) {
                if (i != 0) extendCategoriesBuilder.append(", ");
                // prepared for multiple hierarchy,
                // but in the old platform the multiple hierarchy was not supported
                extendCategoriesBuilder.append(ruleDelimiter);
                extendCategoriesBuilder.append(ruleName);
                extendCategoriesBuilder.append(ruleDelimiter);
                i++;
            }
        }
        // extendCategories has Delimiter+ rule1Name + Delimiter + added by the packageCategoryRules definition
        return extendCategoriesBuilder.toString();
    }

    /**
     * Constructs the extends expression, using the asset categories and the module categoryRules, and adds to the
     * passed content. If passed content has an "extend" expression this function returns the same content with the extra
     * extend added, If not, constructs another "extend" with the new rule and modify the content.
     *
     * @param jcrModule module with the categoryRules
     * @param jcrAssetItem asset with the categories to decide the extend expression to add
     * @param content string to be completed with the necessary extend
     * @return the content passed with the extend expression if it's necessary.
     */
    // If content has an extend expression adds the rules added by the module hierarchy category rules

    public String getExtendExpression(Module jcrModule, AssetItem jcrAssetItem, String content) {
        String extendedRules = getExtendedRuleFromCategoryRules(jcrModule, jcrAssetItem,"\"");
        if (extendedRules != null && extendedRules.trim().length() > 0) {
            String[] contentSplit = content.split("\n");
            String ruleName = contentSplit[0];
            if (ruleName.indexOf(" extends ") == -1) {
                contentSplit[0] += " extends " + extendedRules;
            } else {
                contentSplit[0] += "," + extendedRules;
            }
            StringBuilder contentWithExtendsBuilder = new StringBuilder();
            for (String s : contentSplit) {
                contentWithExtendsBuilder.append(s);
                contentWithExtendsBuilder.append("\n");
            }
            return contentWithExtendsBuilder.toString();
        }
        return content;
    }


}
