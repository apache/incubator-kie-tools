/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.test;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;

public class MavenProjectHelper {

    public static final String JAVA_FOLDER = "java";

    public static File getModuleDir(String moduleName) {
        File rootDir = MavenProjectHelper.getRootDir();
        if (rootDir == null) throw new NullPointerException("Root directory not found");

        List<File> javaFolders = getFolders(rootDir, FileFilterUtils.nameFileFilter(moduleName));
        if (!javaFolders.isEmpty()) return javaFolders.get(0);

        throw new RuntimeException("Module " + moduleName + " dir not found. Root=" + rootDir.getPath());
    }

    public static File getRootDir() {
        File rootDir = new File(System.getProperty("user.dir"));
        File parentPom = new File(rootDir.getParent(), "pom.xml");
        while (parentPom.exists()) {
            rootDir = rootDir.getParentFile();
            parentPom = new File(rootDir.getParent(), "pom.xml");
        }
        return rootDir;
    }

    public static Collection<String> getJavaPackages(File root) {
        Set<String> result = new HashSet<String>();
        List<File> folders = getSourceFolders(root);
        for (File folder : folders) {
            String fullPath = folder.getAbsolutePath();
            int index = fullPath.indexOf("/" + JAVA_FOLDER + "/");
            if (index == -1) continue;

            String javaPath = fullPath.substring(index + 6);
            String javaPackage = StringUtils.replace(javaPath, "/", ".");
            if (!javaPackage.contains(".client")) {
                result.add(javaPackage);
            }
        }
        return result;
    }

    public static List<File> getSourceFolders(File root) {
        List<File> javaFolders = getFolders(root, FileFilterUtils.nameFileFilter(JAVA_FOLDER));
        List<File> result = new ArrayList<File>();
        for (File javaFolder : javaFolders) {
            List<File> _folders = getFolders(javaFolder, null);
            result.add(javaFolder);
            result.addAll(_folders);
        }
        return result;
    }

    public static List<File> getFolders(File root, FileFilter filter) {
        List<File> result = new ArrayList<File>();
        if (root == null || !root.isDirectory()) return result;

        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (filter == null || filter.accept(file)) result.add(file);
                result.addAll(getFolders(file, filter));
            }
        }
        return result;
    }
}
