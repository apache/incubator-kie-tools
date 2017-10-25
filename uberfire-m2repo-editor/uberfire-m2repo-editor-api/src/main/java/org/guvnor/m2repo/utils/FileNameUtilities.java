/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.m2repo.utils;

/**
 * Utilities to validate file being uploaded to Guvnor's M2 Repository
 */
public class FileNameUtilities {

    private FileNameUtilities() {
    }

    /**
     * Is the file valid; i.e. either a JAR, KJAR or pom.xml file.
     * @param fileName
     * @return true if the file is either a JAR, KJAR or pom.xml file.
     */
    public static boolean isValid(final String fileName) {
        return isJar(fileName) || isKJar(fileName) || isPom(fileName);
    }

    /**
     * Is the file a JAR file. File names ending with ".jar" are considered valid JAR files.
     * @param fileName
     * @return true if the file is a JAR file
     */
    public static boolean isJar(final String fileName) {
        return endsWith(fileName,
                        ".jar");
    }

    /**
     * Is the file a KJAR file. File names ending with ".kjar" are considered valid KJAR files.
     * It should be noted KJAR is not a recognised file extension; however it appears some Users
     * are confusing talk of KJARS in Drools' documentation with a file extension and therefore,
     * in the absence of any clarification from the Drools Core developers, we include such suffix.
     * See https://bugzilla.redhat.com/show_bug.cgi?id=1201154 for examples.
     * @param fileName
     * @return true if the file is a KJAR file
     */
    public static boolean isKJar(final String fileName) {
        return endsWith(fileName,
                        ".kjar");
    }

    /**
     * Is the file a pom.xml file
     * @param fileName
     * @return true if the file is a pom.xml file
     */
    public static boolean isPom(final String fileName) {
        return endsWith(fileName,
                        "pom.xml");
    }

    /**
     * Returns whether or not the provided path points to a .pom file.
     * @param path path to the file
     * @return true if the path points to a .pom file, otherwise false.
     */
    public static boolean isDeployedPom(final String path) {
        return endsWith(path,
                        ".pom");
    }

    /**
     * Returns whether or not the provided path ends in the provided suffix.
     * @param path path to the file
     * @param suffix the suffix to check
     * @return true if the path ends in the provided suffix, otherwise false.
     */
    private static boolean endsWith(final String path,
                                    final String suffix) {

        return path != null && !path.trim().isEmpty() && path.toLowerCase().endsWith(suffix);
    }
}
