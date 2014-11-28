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
package org.drools.workbench.jcr2vfsmigration.jcrExport.asset;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.rpc.Module;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;

public class BaseAssetExporter {

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
        List<CategoryItem> assetCategories = jcrAssetItem.getCategories();
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
