/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.devconsole.commons.forms.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jbpm.devconsole.commons.forms.FormsStorage;
import org.jbpm.devconsole.commons.forms.model.Form;
import org.jbpm.devconsole.commons.forms.model.FormConfiguration;
import org.jbpm.devconsole.commons.forms.model.FormContent;
import org.jbpm.devconsole.commons.forms.model.FormFilter;
import org.jbpm.devconsole.commons.forms.model.FormInfo;
import org.jbpm.devconsole.commons.forms.model.FormResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filesystem-backed storage for the custom forms of the running project.
 *
 * Forms are read from the {@code custom-forms-dev} folder available on the classpath. A form is a
 * pair of files: the form source ({@code <name>.html} or {@code <name>.tsx}) plus a
 * {@code <name>.config} JSON file. Updates made through the Dev Console are written back to the project
 * sources ({@code src/main/resources/custom-forms-dev}) so that they survive a rebuild.
 */
public class FormsStorageImpl implements FormsStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormsStorageImpl.class);

    private static final String FORMS_STORAGE_PATH = "custom-forms-dev";
    private static final String CONFIG_EXT = ".config";
    private static final String CLASSES_PATH_SEGMENT = "target/classes/" + FORMS_STORAGE_PATH;
    private static final String SOURCES_PATH_SEGMENT = "src/main/resources/" + FORMS_STORAGE_PATH;

    private final Map<String, FormInfo> formInfoMap = new ConcurrentHashMap<>();
    private final Map<String, Form> modifiedForms = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Path classpathFormsPath;
    private Path persistentFormsPath;

    public FormsStorageImpl(String configuredStorageFolder) {
        this(resolveClasspathFormsPath(), configuredStorageFolder);
    }

    FormsStorageImpl(Path classpathFormsPath, String configuredStorageFolder) {
        this.classpathFormsPath = classpathFormsPath;
        this.persistentFormsPath = resolvePersistentFormsPath(classpathFormsPath, configuredStorageFolder);
        init();
    }

    private static Path resolveClasspathFormsPath() {
        URL formsUrl = Thread.currentThread().getContextClassLoader().getResource(FORMS_STORAGE_PATH);
        if (formsUrl == null) {
            LOGGER.info("No '{}' folder found on the classpath. Custom forms won't be available in the Dev Console.", FORMS_STORAGE_PATH);
            return null;
        }
        if (!"file".equals(formsUrl.getProtocol())) {
            LOGGER.warn("The '{}' folder is not available on the filesystem ('{}'). Custom forms are only supported when running from exploded classes (e.g. spring-boot:run).", FORMS_STORAGE_PATH, formsUrl);
            return null;
        }
        try {
            return Paths.get(formsUrl.toURI());
        } catch (URISyntaxException e) {
            LOGGER.warn("Cannot resolve the '{}' folder from the classpath", FORMS_STORAGE_PATH, e);
            return null;
        }
    }

    private static Path resolvePersistentFormsPath(Path classpathFormsPath, String configuredStorageFolder) {
        if (configuredStorageFolder != null && !configuredStorageFolder.isBlank()) {
            return Paths.get(configuredStorageFolder);
        }
        if (classpathFormsPath == null) {
            return null;
        }
        // By default, write form updates back to the project sources rather than the build output folder.
        String classpathLocation = classpathFormsPath.toString().replace('\\', '/');
        if (classpathLocation.endsWith(CLASSES_PATH_SEGMENT)) {
            Path sources = Paths.get(classpathLocation.replace(CLASSES_PATH_SEGMENT, SOURCES_PATH_SEGMENT));
            if (Files.isDirectory(sources)) {
                return sources;
            }
        }
        return classpathFormsPath;
    }

    @Override
    public int getFormsCount() {
        return formInfoMap.size();
    }

    @Override
    public Collection<FormInfo> getFormInfoList(FormFilter filter) {
        if (filter != null && !filter.getNames().isEmpty()) {
            return formInfoMap.entrySet().stream()
                    .filter(entry -> containsAnyIgnoreCase(entry.getKey(), filter.getNames()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }
        return formInfoMap.values();
    }

    private static boolean containsAnyIgnoreCase(String value, Collection<String> tokens) {
        String lowerCaseValue = value.toLowerCase(Locale.ROOT);
        return tokens.stream().anyMatch(token -> lowerCaseValue.contains(token.toLowerCase(Locale.ROOT)));
    }

    @Override
    public Form getFormContent(String formName) throws IOException {
        FormInfo formInfo = formInfoMap.get(formName);

        if (formInfo == null) {
            throw new IllegalArgumentException("Cannot find form '" + formName + "'");
        }

        Form modified = modifiedForms.get(formName);
        return modified != null ? modified : loadForm(formInfo);
    }

    private Form loadForm(FormInfo formInfo) throws IOException {
        Path formFile = classpathFormsPath.resolve(formInfo.getName() + "." + formInfo.getType().getValue());
        Path configFile = classpathFormsPath.resolve(formInfo.getName() + CONFIG_EXT);

        if (!Files.isRegularFile(formFile)) {
            throw new FileNotFoundException("Cannot find source file for form '" + formInfo.getName() + "'");
        }

        String configuration = Files.isRegularFile(configFile) ? Files.readString(configFile, StandardCharsets.UTF_8) : "";

        return new Form(formInfo, Files.readString(formFile, StandardCharsets.UTF_8), readFormConfiguration(configuration));
    }

    private FormConfiguration readFormConfiguration(String configStr) throws IOException {
        if (configStr == null || configStr.isBlank()) {
            return new FormConfiguration("", new FormResources());
        }

        JsonNode configJson = objectMapper.readTree(configStr);
        FormResources resources = new FormResources();
        JsonNode resourcesJson = configJson.path("resources");

        resourcesJson.path("scripts").fields()
                .forEachRemaining(entry -> resources.getScripts().put(entry.getKey(), entry.getValue().asText()));
        resourcesJson.path("styles").fields()
                .forEachRemaining(entry -> resources.getStyles().put(entry.getKey(), entry.getValue().asText()));

        return new FormConfiguration(configJson.path("schema").asText(""), resources);
    }

    @Override
    public void updateFormContent(String formName, FormContent formContent) throws IOException {
        if (persistentFormsPath == null) {
            throw new IllegalStateException("Cannot store form '" + formName + "'. Form storage couldn't be properly initialized.");
        }

        FormInfo formInfo = formInfoMap.get(formName);

        if (formInfo == null) {
            throw new IllegalArgumentException("Cannot find form '" + formName + "'");
        }

        if (formContent == null) {
            throw new IllegalArgumentException("Invalid form content");
        }

        Path formFile = persistentFormsPath.resolve(formName + "." + formInfo.getType().getValue());
        Path configFile = persistentFormsPath.resolve(formName + CONFIG_EXT);

        if (!Files.isRegularFile(formFile) || !Files.isRegularFile(configFile)) {
            throw new IllegalStateException("Cannot store form '" + formName + "'. Unable to find form files in '" + persistentFormsPath + "'");
        }

        Files.writeString(formFile, formContent.getSource(), StandardCharsets.UTF_8);
        Files.writeString(configFile, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(formContent.getConfiguration()), StandardCharsets.UTF_8);

        FormInfo newInfo = new FormInfo(formName, formInfo.getType(), LocalDateTime.now());

        formInfoMap.put(formName, newInfo);
        modifiedForms.put(formName, new Form(newInfo, formContent.getSource(), formContent.getConfiguration()));
    }

    @Override
    public void refresh() {
        init();
    }

    private void init() {
        if (classpathFormsPath == null || !Files.isDirectory(classpathFormsPath)) {
            return;
        }

        try (Stream<Path> files = Files.list(classpathFormsPath)) {
            files.filter(Files::isRegularFile)
                    .filter(file -> FormInfo.FormType.fromExtension(extensionOf(file)) != null)
                    .filter(file -> Files.isRegularFile(classpathFormsPath.resolve(baseNameOf(file) + CONFIG_EXT)))
                    .forEach(file -> {
                        String formName = baseNameOf(file);
                        formInfoMap.put(formName, new FormInfo(formName, FormInfo.FormType.fromExtension(extensionOf(file)), lastModifiedOf(file)));
                    });
        } catch (IOException e) {
            LOGGER.warn("Error while reading the forms folder '{}'", classpathFormsPath, e);
        }
    }

    private static String baseNameOf(Path file) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
    }

    private static String extensionOf(Path file) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
    }

    private static LocalDateTime lastModifiedOf(Path file) {
        try {
            Instant instant = Files.getLastModifiedTime(file).toInstant();
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (IOException e) {
            return LocalDateTime.now();
        }
    }
}
