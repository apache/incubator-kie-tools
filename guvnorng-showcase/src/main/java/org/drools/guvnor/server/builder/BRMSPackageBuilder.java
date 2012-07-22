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

package org.drools.guvnor.server.builder;

import org.drools.builder.conf.DefaultPackageNameOption;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepositoryException;
import org.drools.util.ChainedProperties;
//import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
//import org.jbpm.bpmn2.xml.BPMNSemanticModule;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * This decorates the drools-compiler PackageBuilder
 * with some functionality needed for the BRMS.
 * This can use the BRMS repo as a classpath.
 */
public class BRMSPackageBuilder extends PackageBuilder {

    private List<DSLTokenizedMappingFile> dslFiles;
    private DefaultExpander expander;

    /**
     * In the BRMS you should not need to use this, use the getInstance factory method instead.
     *
     * @param config
     */
    BRMSPackageBuilder(PackageBuilderConfiguration config) {
        super(config);
    }

    public BRMSPackageBuilder() {
        super(new PackageBuilderConfiguration());
    }

    public BRMSPackageBuilder(Properties properties, ClassLoader classLoader) {
        this(getPackageBuilderConfiguration(properties, classLoader));
    }

    public BRMSPackageBuilder(ModuleItem packageItem) {
        this(getPackageBuilderConfiguration(getProperties(packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat(AssetFormats.PROPERTIES, AssetFormats.CONFIGURATION),
                packageItem.getName()),
                new ClassLoaderBuilder(packageItem.listAssetsWithVersionsSpecifiedByDependenciesByFormat(AssetFormats.MODEL)).buildClassLoader()));
    }

    /**
     * This will reset the errors.
     */
    public void clearErrors() {
        super.resetErrors();
    }

    public void setDSLFiles(List<DSLTokenizedMappingFile> files) {
        this.dslFiles = files;
    }

    public List<DSLTokenizedMappingFile> getDSLMappingFiles() {
        return Collections.unmodifiableList(this.dslFiles);
    }

    public static Properties getProperties(AssetItemIterator assetItemIterator, String packageName) {
        // the default compiler. This is nominally JANINO but can be overridden by setting drools.dialect.java.compiler to ECLIPSE
        Properties properties = new Properties();
        properties.setProperty("drools.dialect.java.compiler", getChainedProperties().getProperty("drools.dialect.java.compiler", "ECLIPSE"));
        try {
            Properties properties1 = loadConfigurationProperties(assetItemIterator);

            properties1.setProperty(DefaultPackageNameOption.PROPERTY_NAME,
                    packageName);

            properties.putAll(properties1);
        } catch (IOException e) {
            // TODO: This is not a Exception in the Repository -Rikkola-
            throw new RulesRepositoryException("Unable to load configuration properties for package.",
                    e);
        }
        return properties;
    }


    private static ChainedProperties getChainedProperties() {
        // See if we can find a packagebuilder.conf
        // We do this manually here, as we cannot rely on PackageBuilder doing this correctly
        // note this chainedProperties already checks System properties too
        return new ChainedProperties("packagebuilder.conf", BRMSPackageBuilder.class.getClassLoader(), // pass this as it searches currentThread anyway
                false);
    }

    /**
     * Load all the .properties and .conf files into one big happy Properties instance.
     *
     * @param assetItemIterator
     */
    private static Properties loadConfigurationProperties(AssetItemIterator assetItemIterator) throws IOException {
        Properties bigHappyProperties = new Properties();
        while (assetItemIterator.hasNext()) {
            AssetItem assetItem = assetItemIterator.next();
            assetItem.getContent();
            Properties properties = new Properties();
            properties.load(assetItem.getBinaryContentAttachment());
            bigHappyProperties.putAll(properties);
        }

        return bigHappyProperties;
    }

    private static PackageBuilderConfiguration getPackageBuilderConfiguration(Properties properties, ClassLoader classLoader) {
        PackageBuilderConfiguration packageBuilderConfiguration = new PackageBuilderConfiguration(properties, classLoader);

        packageBuilderConfiguration.setAllowMultipleNamespaces(false);
        
        //JLIU: TODO
        //packageBuilderConfiguration.addSemanticModule(new BPMNSemanticModule());
        //packageBuilderConfiguration.addSemanticModule(new BPMNDISemanticModule());
        return packageBuilderConfiguration;
    }

    /**
     * This is used when loading Jars, DSLs etc to report errors.
     */
    public static interface DSLErrorEvent {
        public void recordError(AssetItem asset, String message);
    }

    /**
     * Returns true if this package uses a DSL.
     */
    public boolean hasDSL() {
        return this.dslFiles != null && this.dslFiles.size() > 0;
    }

    /**
     * Returns an expander for DSLs (only if there is a DSL configured for this package).
     */
    public DefaultExpander getDSLExpander() {
        if (this.expander == null) {
            expander = new DefaultExpander();
            for (DSLMappingFile file : this.dslFiles) {
                expander.addDSLMapping(file.getMapping());
            }
        }
        return expander;
    }

}
