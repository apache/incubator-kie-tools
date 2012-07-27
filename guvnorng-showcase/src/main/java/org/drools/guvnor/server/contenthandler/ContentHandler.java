/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server.contenthandler;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All content handlers must implement this, and be registered in content_types.properties
 */
public abstract class ContentHandler {

    /**
     * When loading asset content.
     *
     * @param asset The target.
     * @param item  The source.
     * @throws SerializationException
     */
    public abstract void retrieveAssetContent(Asset asset,
                                              AssetItem item) throws SerializationException;

    /**
     * For storing the asset content back into the repo node (any changes).
     *
     * @param asset
     * @param repoAsset
     * @throws SerializationException
     */
    public abstract void storeAssetContent(Asset asset,
                                           AssetItem repoAsset) throws SerializationException;

    /**
     * @return true if the current content type is for a rule asset.
     *         If it is a rule asset, then it can be assembled into a package.
     *         If its not, then it is there, nominally to support compiling or
     *         validation/testing of the package (eg a model, or a dsl file).
     */
    public boolean isRuleAsset() {
        return this instanceof IRuleAsset;
    }

    private String findParentCategory(AssetItem asset,
                                      String currentCat) {
    	//JLIU: TODO
    	
    	return null;
/*        //Start your search at the top
        CategoryItem item = asset.getRulesRepository().loadCategory("/");
        return findCategoryInChild(item,
                currentCat);*/
    }

    @SuppressWarnings("rawtypes")
    private String findCategoryInChild(CategoryItem item,
                                       String currentCat) {
        List children = item.getChildTags();
        for (Object aChildren : children) {
            if (((CategoryItem) aChildren).getName().equals(currentCat)) {
                return item.getName();
            } else {
                String check = findCategoryInChild((CategoryItem) aChildren,
                        currentCat);
                if (check != null && check.length() > 0) {
                    return check;
                }
            }

        }
        return "";
    }

    @SuppressWarnings("rawtypes")
    private String findKeyforValue(HashMap<String, String> catRules,
                                   String catToFind) {
        for (Map.Entry<String, String> stringStringEntry : catRules.entrySet()) {
            //Found rule name that should be used to extend current rule as defined in the Category Rule
            if (stringStringEntry.getValue().equals(catToFind)) {
                return (String) stringStringEntry.getKey();
            }
        }
        return "";
    }

    /**
     * Search Categories in a package against the current rule to see if the current rule should be extended,
     * via another rule. IE rule rule1 extends rule2
     * This is an implementation of that DRL feature, via Category to Rule mappings in Guvnor
     *
     * @param asset
     * @return rule that should be extended, based on categories
     */
    protected String parentNameFromCategory(AssetItem asset,
                                            String currentParent) {

        List<CategoryItem> cats = asset.getCategories();
        String catName = null;
        if (cats.size() > 0) {

            catName = cats.get(0).getName();
        }
        //get all Category Rules for Package
        HashMap<String, String> categoryRules = asset.getModule().getCategoryRules();

        String newParent = currentParent;
        if (null != categoryRules && null != catName) {
            //Asset or Rule is actually used in the Category Rule, so ignore the category of the normal rule
            //Either extend from the parent category rule or none at all
            String ruleName = asset.getName();
            if (categoryRules.containsKey(ruleName)) {
                //find Cat for your rule
                String parentCategory = findParentCategory(asset,
                        categoryRules.get(ruleName));
                //This rule name is in our Category Rules
                //See if there is a Parent and it has a rule defined, if so extend that rule, to create a chain
                if (parentCategory != null && parentCategory.length() > 0 && categoryRules.containsValue(parentCategory)) {
                    newParent = findKeyforValue(categoryRules,
                            parentCategory);
                } else {
                    //Must be blank to avoid circular reference
                    newParent = "";
                }
                //else make sure parent is ALWAYS blank, to avoid circle references

                //If the rule is not defined in the Category Rule, check to make sure currentParent isnt already set
                //If you wanted to override the Category Rule, with a extends on the rule manually, honor it
            } else if (currentParent != null && currentParent.length() > 0) {
                newParent = currentParent;
                //Normal use case
                //Category of the current asset has been defined in Category Rules for the current package
            } else if (categoryRules.containsValue(catName)) {
                newParent = findKeyforValue(categoryRules,
                        catName);
            }
        }
        return newParent;
    }

}
