/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.ui.rebind;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import org.apache.commons.io.IOUtils;
import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ConstructorBlockBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.util.Implementations;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.metadata.ErraiAppPropertiesFiles;
import org.jboss.errai.common.metadata.RebindUtils;
import org.jboss.errai.config.rebind.AbstractAsyncGenerator;
import org.jboss.errai.config.rebind.GenerateAsync;
import org.jboss.errai.config.util.ClassScanner;
import org.jboss.errai.reflections.Configuration;
import org.jboss.errai.reflections.Reflections;
import org.jboss.errai.reflections.scanners.ResourcesScanner;
import org.jboss.errai.reflections.util.ConfigurationBuilder;
import org.jboss.errai.reflections.util.FilterBuilder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.rebind.chain.TemplateCatalog;
import org.jboss.errai.ui.shared.DomVisit;
import org.jboss.errai.ui.shared.MessageBundle;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.TemplateVisitor;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.shared.api.annotations.TranslationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generates a concrete subclass of {@link TranslationService}. This class is responsible for
 * scanning the classpath for all bundles, and then making them available during template
 * translation.
 *
 * The {@link TranslationService} can also be used directly in the Errai application by injecting
 * it. This allows translated strings to be used from Errai Java code, not just from templates.
 *
 * @author eric.wittmann@redhat.com
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
@GenerateAsync(TranslationService.class)
public class TranslationServiceGenerator extends AbstractAsyncGenerator {

  private static final String GENERATED_CLASS_NAME = "TranslationServiceImpl";
  private static Pattern LOCALE_IN_FILENAME_PATTERN = Pattern.compile("([^_]*)_(\\w\\w)?(_\\w\\w)?\\.(json|properties)");

  private static final Logger log = LoggerFactory.getLogger(TranslationServiceGenerator.class);

  @Override
  public String generate(final TreeLogger logger, final GeneratorContext context, final String typeName)
          throws UnableToCompleteException {
    return startAsyncGeneratorsAndWaitFor(TranslationService.class, context, logger,
            TranslationService.class.getPackage().getName(), GENERATED_CLASS_NAME);
  }

  @Override
  public String generate(final TreeLogger logger, final GeneratorContext context) {
    // The class we will be building is GeneratedTranslationService
    final ClassStructureBuilder<?> classBuilder = Implementations.extend(
            TranslationService.class, GENERATED_CLASS_NAME);
    final ConstructorBlockBuilder<?> ctor = classBuilder.publicConstructor();

    // The i18n keys found (per locale) while processing the bundles.
    final Map<String, Set<String>> discoveredI18nMap = new HashMap<>();

    // Find all fields annotated with @TranslationKey and generate code
    // in the c'tor to register each one as a translation key for the
    // default (null) locale. These values may get overridden by keys
    // found in the default bundle. This is why we do this before we do
    // the bundle work.
    final Map<String, String> translationKeyFieldMap = new HashMap<>();
    final Collection<MetaField> translationKeyFields = ClassScanner.getFieldsAnnotatedWith(TranslationKey.class, null, context);
    for (final MetaField metaField : translationKeyFields) {
      // Figure out the translation key name
      String name = null;
      final String fieldName = metaField.getName();
      final String defaultName = metaField.getDeclaringClass().getFullyQualifiedName() + "." + fieldName;
      if (!metaField.getType().isAssignableFrom(String.class)) {
        throw new GenerationException("Translation key fields must be of type java.lang.String: " + defaultName);
      }
      try {
        final Class<?> asClass = metaField.getDeclaringClass().asClass();
        final Field field = asClass.getField(fieldName);
        final Object fieldVal = field.get(null);
        if (fieldVal == null) {
          throw new GenerationException("Translation key fields cannot be null: " + defaultName);
        }
        name = fieldVal.toString();
      }
      catch (final Exception e) {
        log.warn("There was an error while processing a TranslationKey", e);
      }

      // Figure out the translation key value (for the null locale).
      String value = null;
      final TranslationKey annotation = metaField.getAnnotation(TranslationKey.class);
      final String defaultValue = annotation.defaultValue();
      if (defaultValue != null) {
        value = defaultValue;
      }
      else {
        value = "!!" + defaultName + "!!";
      }

      // Generate code to register the null locale mapping
      if (translationKeyFieldMap.containsKey(name)) {
        throw new GenerationException("Duplicate translation key found: " + defaultName);
      }
      translationKeyFieldMap.put(name, value);
      ctor.append(Stmt.loadVariable("this").invoke("registerTranslation", name, value, null));
    }

    // Scan for all @Bundle annotations.
    final Collection<MetaClass> bundleAnnotatedClasses = ClassScanner.getTypesAnnotatedWith(Bundle.class, context);

    final Set<String> bundlePaths = new HashSet<>();
    for (final MetaClass bundleAnnotatedClass : bundleAnnotatedClasses) {
      final String bundlePath = getMessageBundlePath(bundleAnnotatedClass);
      bundlePaths.add(bundlePath);
    }

    // Now get all files in the message bundle (all localized versions)
    final Collection<URL> scannableUrls = getScannableUrls(bundleAnnotatedClasses);
    log.info("Preparing to scan for i18n bundle files.");
    final MessageBundleScanner scanner = new MessageBundleScanner(
            new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().include(".*json").include(".*properties"))
                .setUrls(scannableUrls)
                .setScanners(new MessageBundleResourceScanner(bundlePaths)));

    // For each one, generate the code to load the translation and put that generated
    // code in the c'tor of the generated class (GeneratedTranslationService)
    final Collection<String> resources = scanner.getStore().get(MessageBundleResourceScanner.class).values();
    for (final String bundlePath : bundlePaths) {
      // If we didn't find at least the specified root bundle file, that's a problem.
      if (!resources.contains(bundlePath)) {
        throw new GenerationException("Missing i18n bundle (specified in @Bundle): " + bundlePath);
      }
    }

    // Now generate code to load up each of the JSON files and register them
    // with the translation service.
    final Set<String> duplicates = new HashSet<>();
    int i = 0;
    for (final String resource : resources) {
      // Generate this component's ClientBundle resource interface
      final BuildMetaClass messageBundleResourceInterface = generateMessageBundleResourceInterface(resource);
      // Add it as an inner class to the generated translation service
      classBuilder.getClassDefinition().addInnerClass(new InnerClass(messageBundleResourceInterface));

      // Instantiate the ClientBundle MessageBundle resource
      final String msgBundleVarName = "var" + (i++);
      ctor.append(Stmt.declareVariable(messageBundleResourceInterface).named(msgBundleVarName)
              .initializeWith(Stmt.invokeStatic(GWT.class, "create", messageBundleResourceInterface)));

      // Create a dictionary from the message bundle and register it.
      final String locale = getLocaleFromBundlePath(resource);

      final String registerBundleMethod = (isJsonBundle(resource)) ? "registerJsonBundle" : "registerPropertiesBundle";
      ctor.append(Stmt.loadVariable("this").invoke(registerBundleMethod,
              Stmt.loadVariable(msgBundleVarName).invoke("getContents").invoke("getText"), locale));

      duplicates.addAll(recordBundleKeys(discoveredI18nMap, locale, resource));
    }

    if (!duplicates.isEmpty()) {
      log.warn("Multiple bundles contain translations for these keys: {}", duplicatesToString(duplicates));
    }

    // We're done generating the c'tor
    ctor.finish();

    generateI18nHelperFilesInto(discoveredI18nMap, translationKeyFieldMap, RebindUtils.getErraiCacheDir());

    return classBuilder.toJavaString();
  }

  private String duplicatesToString(final Set<String> duplicates) {
    final StringBuilder builder = new StringBuilder();

    builder.append('[');
    duplicates.forEach(dup -> builder.append(dup).append(", "));
    builder.replace(builder.length() - ", ".length(), builder.length(), "]");

    return builder.toString();
  }

  private Collection<URL> getScannableUrls(final Collection<MetaClass> bundleAnnotatedClasses) {
    final Collection<URL> urls = new HashSet<>();

    addUrlsFromBundleAnnotations(bundleAnnotatedClasses, urls);
    addErraiAppPropertiesModulesUrls(urls);

    return urls;
  }

  private void addErraiAppPropertiesModulesUrls(final Collection<URL> urls) {
    urls.addAll(ErraiAppPropertiesFiles.getModulesUrls());
  }

  private void addUrlsFromBundleAnnotations(final Collection<MetaClass> bundleAnnotatedClasses, final Collection<URL> urls) {
    final Set<String> completedPaths = new HashSet<>();

    for (final MetaClass bundleClass : bundleAnnotatedClasses) {
      final String bundlePath = getMessageBundlePath(bundleClass);
      if (bundlePath != null && !completedPaths.contains(bundlePath)) {
        final URL resource = getClass().getClassLoader().getResource(bundlePath);
        if (resource == null) {
          throw new GenerationException("Failed to load bundle " + bundlePath +
                  " defined on class " + bundleClass.getFullyQualifiedName());
        }

        final URL classpathElement;
        final String pathRoot = getPathRoot(bundleClass, resource);
        try {
          final String urlString = new URI(pathRoot).toURL().toString();

          // URLs returned by the classloader are UTF-8 encoded. The URLDecoder assumes
          // a HTML form encoded String, which is why we escape the plus symbols here.
          // Otherwise, they would be decoded into space characters.
          // The pound character still must not appear anywhere in the path!
          classpathElement = new URL(URLDecoder.decode(urlString.replaceAll("\\+", "%2b"), "UTF-8"));
        } catch (final Exception e) {
          log.warn("Failed to construct URL for i18n bundle defined in " + bundleClass);
          continue;
        }
        urls.add(classpathElement);
        completedPaths.add(bundlePath);
      }
    }
  }

  private String getPathRoot(final MetaClass bundleClass, final URL resource) {
    final String fullPath = resource.getPath();
    final String resourcePath = bundleClass.getAnnotation(Bundle.class).value();
    final String protocol = resource.getProtocol();

    final String relativePath;
    if (resourcePath.startsWith("/"))
      relativePath = resourcePath;
    else
      // Do NOT use File.separatorChar here: Url.getPath() always uses forward-slashes
      relativePath = bundleClass.getPackageName().replace('.', '/');

    final String pathRoot = fullPath.substring(0, fullPath.indexOf(relativePath));
    return protocol + ":" + pathRoot;
  }

  /**
   * Records all of the i18n keys found in the given bundle.
   *
   * @param discoveredI18nMap
   * @param locale
   * @param bundlePath
   *
   * @return A set keys that were duplicates of already discovered keys.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected static Set<String> recordBundleKeys(final Map<String, Set<String>> discoveredI18nMap, final String locale, final String bundlePath) {
    InputStream is = null;
    final Set<String> duplicates = new HashSet<>();
    try {
      final Set<String> keys = discoveredI18nMap.computeIfAbsent(locale, l -> new HashSet<>());
      is = TranslationServiceGenerator.class.getClassLoader().getResourceAsStream(bundlePath);

      if (isJsonBundle(bundlePath)) {
        final JsonFactory jsonFactory = new JsonFactory();
        try(final JsonParser jp = jsonFactory.createParser(is)) {
          JsonToken token = jp.nextToken();
          while (token != null) {
            token = jp.nextToken();
            if (token == JsonToken.FIELD_NAME) {
              final String name = jp.getCurrentName();
              if (keys.contains(name)) {
                duplicates.add(name);
              }
              keys.add(name);
            }
          }
        }
      }
      else {
        final Properties properties = new Properties();
        properties.load(is);
        final Set<String> propertFileKeys = (Set) properties.keySet();
        propertFileKeys
          .stream()
          .filter(key -> keys.contains(key))
          .collect(Collectors.toCollection(() -> duplicates));
        keys.addAll(propertFileKeys);
      }
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      IOUtils.closeQuietly(is);
    }

    return duplicates;
  }

  private static boolean isJsonBundle(final String path) {
    return path.endsWith(".json");
  }

  /**
   * Gets the bundle name from the @Bundle annotation.
   *
   * @param bundleAnnotatedClass
   */
  private String getMessageBundlePath(final MetaClass bundleAnnotatedClass) {
    final Bundle annotation = bundleAnnotatedClass.getAnnotation(Bundle.class);
    final String name = annotation.value();
    if (name == null) {
      throw new GenerationException("@Bundle: bundle name must not be null].");
    }
    // Absolute path vs. relative path.
    if (name.startsWith("/")) {
      return name.substring(1);
    }
    else {
      final String packageName = bundleAnnotatedClass.getPackageName();
      return packageName.replace('.', '/') + "/" + name;
    }
  }

  /**
   * Gets the name of the {@link MessageBundle} class.
   *
   * @param bundlePath
   */
  private String getMessageBundleTypeName(final String bundlePath) {
    final String typeName =
        bundlePath.replace(".json", "MessageBundleResource").replace('/', '.').replace('-', '_').replace('.', '_');
    return typeName;
  }

  /**
   * Create an inner interface for the given {@link MessageBundle} class and its corresponding JSON
   * resource.
   *
   * @param bundlePath
   *          path to the message bundle
   */
  private BuildMetaClass generateMessageBundleResourceInterface(final String bundlePath) {
    final ClassStructureBuilder<?> componentMessageBundleResource = ClassBuilder
            .define(getMessageBundleTypeName(bundlePath)).publicScope()
            .interfaceDefinition().implementsInterface(MessageBundle.class)
            .implementsInterface(ClientBundle.class).body()
            .publicMethod(TextResource.class, "getContents")
            .annotatedWith(new Source() {
              @Override
              public Class<? extends Annotation> annotationType() {
                return Source.class;
              }

              @Override
              public String[] value() {
                return new String[] { bundlePath };
              }
            }).finish();
    return componentMessageBundleResource.getClassDefinition();
  }

  /**
   * Gets the locale information from the given bundle path. For example, if the bundle path is
   * "org/example/myBundle_en_US.json" then this method will return "en_US".
   *
   * @param bundlePath
   *          path to the message bundle
   */
  public static String getLocaleFromBundlePath(final String bundlePath) {
    final Matcher matcher = LOCALE_IN_FILENAME_PATTERN.matcher(bundlePath);
    if (matcher != null && matcher.matches()) {
      final StringBuilder locale = new StringBuilder();
      final String lang = matcher.group(2);
      if (lang != null)
        locale.append(lang);
      final String region = matcher.group(3);
      if (region != null)
        locale.append("_").append(region.substring(1));
      return locale.toString();
    }
    else {
      return null;
    }
  }

  /**
   * Generates all helper files that developers can use to assist with i18n work. This includes the
   * "missing i18n keys" report(s) as well as a set of JSON files that can be used as a
   * starting-point for translations.
   *
   * @param discoveredI18nMap
   *          a map of keys found in all scanned bundles
   * @param translationKeyFieldMap
   *          a map of translation keys found in {@link TranslationKey} annotated fields
   * @param destDir
   *          where to write the *.json files
   * @param context
   *          the generator context
   */
  protected static void generateI18nHelperFilesInto(final Map<String, Set<String>> discoveredI18nMap,
      final Map<String, String> translationKeyFieldMap, final File destDir) {
    final Map<String, String> allI18nValues = new HashMap<>();

    // Make sure to put the *usages* of translation keys that we found by scanning the
    // Java code for @TranslationKey annotated static fields into the all-i18n-values map
    allI18nValues.putAll(translationKeyFieldMap);

    // Find all *usages* of translation keys by scanning and processing all templates.
    final Collection<MetaClass> templatedAnnotatedClasses = ClassScanner.getTypesAnnotatedWith(Templated.class);
    for (final MetaClass templatedAnnotatedClass : templatedAnnotatedClasses) {
      if (!templatedAnnotatedClass.getAnnotation(Templated.class)
              .provider().equals(Templated.DEFAULT_PROVIDER.class))
        continue;

      final String templateFileName = TemplatedCodeDecorator.getTemplateFileName(templatedAnnotatedClass);
      final String templateFragment = TemplatedCodeDecorator.getTemplateFragmentName(templatedAnnotatedClass);
      final String i18nPrefix = TemplateUtil.getI18nPrefix(templateFileName);
      final URL resource = TranslationServiceGenerator.class.getClassLoader().getResource(templateFileName);
      if (resource == null) {
        throw new IllegalArgumentException("Could not find template " + templateFileName + " for @Templated class "
            + templatedAnnotatedClass.getName());
      }
      final Document templateNode = new TemplateCatalog().parseTemplate(resource);
      if (templateNode == null) // TODO log that the template failed to parse
        continue;
      final Element templateRoot = getTemplateRootNode(templateNode, templateFragment);
      if (templateRoot == null) // TODO log that the template root couldn't be found
        continue;
      final Map<String, String> i18nValues = getTemplateI18nValues(templateRoot, i18nPrefix);
      allI18nValues.putAll(i18nValues);
    }

    // Output a JSON file containing *all* of the keys that need translation.
    final File allI18nValuesFile = new File(destDir, "errai-bundle-all.json");
    if (allI18nValuesFile.isFile())
      allI18nValuesFile.delete();
    outputBundleFile(allI18nValues, allI18nValuesFile, null);

    // Only bother with the missing/extra files if we discovered *something*
    // while processing. If zero bundles were found, then they aren't currently
    // using i18n in any way.
    if (!discoveredI18nMap.isEmpty()) {
      // Output a JSON file containing only the keys that were found in existing JSON
      // bundle files but that are *not* needed (not found in a template).
      Set<String> discoveredDefaultI18nKeys = discoveredI18nMap.get(null);
      if (discoveredDefaultI18nKeys == null)
        discoveredDefaultI18nKeys = Collections.emptySet();
      final Set<String> extraI18nKeys = new HashSet<>(discoveredDefaultI18nKeys);
      extraI18nKeys.removeAll(allI18nValues.keySet());
      final Map<String, String> m = new HashMap<>();
      for (final String extraKey : extraI18nKeys)
        m.put(extraKey, "");
      final File extraI18nValuesFile = new File(destDir, "errai-bundle-extra.json");
      if (extraI18nValuesFile.isFile())
        extraI18nValuesFile.delete();
      outputBundleFile(m, extraI18nValuesFile, extraI18nKeys);

      // Ouput a JSON file containing just the i18n keys that are missing from the
      // existing i18n bundles (found in a template but missing from the bundles).
      final Set<String> missingI18nKeys = new HashSet<>(allI18nValues.keySet());
      missingI18nKeys.removeAll(discoveredDefaultI18nKeys);
      final File missingI18nValuesFile = new File(destDir, "errai-bundle-missing.json");
      if (missingI18nValuesFile.isFile())
        missingI18nValuesFile.delete();
      outputBundleFile(allI18nValues, missingI18nValuesFile, missingI18nKeys);

      // TODO output -missing bundle files for each locale
    }
  }

  /**
   * Gets the root node of the template (within a potentially larger template HTML file).
   *
   * @param templateNode
   * @param templateFragment
   */
  private static Element getTemplateRootNode(final Document templateNode, final String templateFragment) {
    try {
      final XPath xpath = XPathFactory.newInstance().newXPath();
      final Element documentElement = templateNode.getDocumentElement();
      if (templateFragment == null || templateFragment.trim().length() == 0) {
        return (Element) xpath.evaluate("//body", documentElement, XPathConstants.NODE);
      }
      else {
        return (Element) xpath.evaluate("//*[@data-field='" + templateFragment + "']", documentElement,
            XPathConstants.NODE);
      }
    }
    catch (final XPathExpressionException e) {
      return null;
    }
  }

  /**
   * Gets all of the i18n key/value pairs from the given template root. In other words, returns
   * everything that needs to be translated.
   *
   * @param templateRoot
   * @param i18nPrefix
   */
  private static Map<String, String> getTemplateI18nValues(final Element templateRoot, final String i18nPrefix) {
    final TemplateVisitor visitor = new TemplateVisitor(i18nPrefix);
    DomVisit.visit(templateRoot, visitor);
    return visitor.getI18nValues();
  }

  /**
   * Writes out a bundle (JSON) file to the given location.
   *
   * @param i18nValues
   * @param bundleFile
   * @param onlyTheseKeys
   */
  private static void outputBundleFile(final Map<String, String> i18nValues, final File bundleFile, final Set<String> onlyTheseKeys) {
    if (onlyTheseKeys != null && onlyTheseKeys.isEmpty())
      return;

    try {
      final JsonFactory f = new JsonFactory();
      final JsonGenerator g = f.createGenerator(bundleFile, JsonEncoding.UTF8);
      g.useDefaultPrettyPrinter();
      g.writeStartObject();
      final Set<String> orderedKeys = new TreeSet<>(i18nValues.keySet());
      for (final String key : orderedKeys) {
        final String value = i18nValues.get(key);
        if (onlyTheseKeys == null || onlyTheseKeys.contains(key))
          g.writeStringField(key, value);
      }
      g.writeEndObject();
      g.close();
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * A scanner that finds i18n message bundles.
   *
   * @author eric.wittmann@redhat.com
   */
  private static class MessageBundleResourceScanner extends ResourcesScanner {
    private final List<String> bundlePrefixes = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param bundlePath
     */
    public MessageBundleResourceScanner(final Set<String> bundlePaths) {
      Assert.notNull(bundlePaths);
      for (final String path : bundlePaths) {
        final String prefix = path.substring(0, Math.max(path.lastIndexOf(".json"), path.lastIndexOf(".properties")));
        bundlePrefixes.add(prefix);
      }
    }

    /**
     * @see org.jboss.errai.reflections.scanners.ResourcesScanner#acceptsInput(java.lang.String)
     */
    @Override
    public boolean acceptsInput(final String file) {
      for (final String bundlePrefix : bundlePrefixes) {
        if (file.startsWith(bundlePrefix)) {
          return true;
        }
      }

      return false;
    }
  }

  /**
   * Scanner used to find i18n message bundles on the classpath.
   *
   * @author eric.wittmann@redhat.com
   */
  private static class MessageBundleScanner extends Reflections {

    /**
     * Constructor.
     *
     * @param config
     */
    public MessageBundleScanner(final Configuration config) {
      super(config);
      scan();
    }

  }

}
