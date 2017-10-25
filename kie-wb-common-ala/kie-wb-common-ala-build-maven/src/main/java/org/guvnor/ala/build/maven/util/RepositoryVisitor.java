/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.build.maven.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

public class RepositoryVisitor {

    private static final Random RND = new Random();
    private File root;
    private final Map<String, String> identityHash = new ConcurrentHashMap<>();
    private Map<String, String> oldIdentityHash;

    public RepositoryVisitor(final Path projectPath,
                             final String projectName) {
        this(projectPath,
             System.getProperty("java.io.tmpdir") + File.separatorChar + "guvnor" + RND.nextLong() + File.separatorChar + projectName,
             false);
    }

    public RepositoryVisitor(final Path projectPath,
                             final String _projectRoot,
                             final boolean cleanTempDir) {
        this.root = makeTempRootDirectory(_projectRoot,
                                          cleanTempDir);

        try {
            if (_projectRoot != null && !_projectRoot.equals("")) {
                loadIndex(root.getAbsolutePath());
            }
            visitPaths(root,
                       Files.newDirectoryStream(projectPath));
            if (oldIdentityHash != null) {
                MapDifference<String, String> difference = Maps.difference(oldIdentityHash,
                                                                           identityHash);
                Map<String, String> deletedFiles = difference.entriesOnlyOnLeft();
                for (String path : deletedFiles.keySet()) {
                    boolean deleted = new File(root.getAbsolutePath().replace(projectPath.toString(),
                                                                              "") + "/" + path).delete();
                    System.out.println("Deleted: " + root.getAbsolutePath().replace(projectPath.toString(),
                                                                                    "") + "/" + path + " -> " + deleted);
                }
            }
            storeIndex(root.getAbsolutePath());
        } catch (IOException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public File getRoot() {
        return root;
    }

    private void storeIndex(String path) {
        final File identityHashFile = new File(path + "/index.json");
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(identityHash);
        try (FileOutputStream output = new FileOutputStream(identityHashFile)) {
            output.write(json.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(RepositoryVisitor.class.getName()).log(Level.SEVERE,
                                                                    null,
                                                                    ex);
        }
    }

    private void loadIndex(String path) {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(path + "/index.json"));
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            oldIdentityHash = gson.fromJson(reader,
                                            type);
        } catch (FileNotFoundException ex) {
            //The Index doesn't exist yet, it will be generated after the Repository visitor is used for the first time.
        }
    }

    private void visitPaths(final File parent,
                            final DirectoryStream<Path> directoryStream) throws IOException, NoSuchAlgorithmException {
        for (final org.uberfire.java.nio.file.Path path : directoryStream) {
            if (Files.isDirectory(path)) {
                final File newParent = makeTempDirectory(parent,
                                                         path.getFileName().toString());
                visitPaths(newParent,
                           Files.newDirectoryStream(path));
            } else {
                makeTempFile(parent,
                             path);
            }
        }
    }

    private File makeTempDirectory(final File parent,
                                   final String filePath) {
        File tempDirectory = new File(parent,
                                      filePath);
        if (tempDirectory.exists()) {
            return tempDirectory;
        }
        if (!tempDirectory.isFile()) {
            tempDirectory.mkdir();
        }
        return tempDirectory;
    }

    private File makeTempRootDirectory(final String tempRoot,
                                       final boolean cleanTempDir) {
        final File tempRootDir = new File(tempRoot);
        if (tempRootDir.exists()) {
            if (cleanTempDir) {
                try {
                    FileUtils.deleteDirectory(tempRootDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            tempRootDir.mkdirs();
        }
        return tempRootDir;
    }

    private void makeTempFile(final File parent,
                              final Path path) throws IOException, NoSuchAlgorithmException {

        final int BUFFER = 2048; //Dangerous stuff here ???
        byte data[] = new byte[BUFFER];

        MessageDigest md = MessageDigest.getInstance("MD5");
        DigestInputStream dis = new DigestInputStream(Files.newInputStream(path),
                                                      md);
        try (BufferedInputStream origin = new BufferedInputStream(dis,
                                                                  BUFFER)) {
            String resourcePath = path.toString();
            readFile(origin,
                     data,
                     BUFFER);
            identityHash.put(resourcePath,
                             getMD5String(dis.getMessageDigest().digest()));
        }

        FileOutputStream output = null;
        dis = new DigestInputStream(Files.newInputStream(path),
                                    md);
        try (BufferedInputStream origin = new BufferedInputStream(dis,
                                                                  BUFFER)) {
            String resourcePath = path.toString();
            if (oldIdentityHash != null) {
                //if the key exist in the old map and the hash is different then we need to override the file
                if (oldIdentityHash.containsKey(resourcePath) && oldIdentityHash.get(resourcePath) != null
                        && !oldIdentityHash.get(resourcePath).equals(identityHash.get(resourcePath))) {

                    output = writeFile(parent,
                                       path,
                                       output,
                                       origin,
                                       data,
                                       BUFFER);
                    System.out.println("Overriding existing file content : " + resourcePath);
                } else if (!oldIdentityHash.containsKey(resourcePath)) {

                    output = writeFile(parent,
                                       path,
                                       output,
                                       origin,
                                       data,
                                       BUFFER);
                }
            } else {
                output = writeFile(parent,
                                   path,
                                   output,
                                   origin,
                                   data,
                                   BUFFER);
            }
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /*
     * reads the file to generate the MD5 Digest
     */
    private void readFile(final BufferedInputStream origin,
                          byte[] data,
                          final int BUFFER) throws IOException {
        while ((origin.read(data,
                            0,
                            BUFFER)) != -1) {
        }
    }

    private FileOutputStream writeFile(final File parent,
                                       final Path path,
                                       FileOutputStream output,
                                       final BufferedInputStream origin,
                                       byte[] data,
                                       final int BUFFER) throws IOException, FileNotFoundException {
        final File tempFile = new File(parent,
                                       path.getFileName().toString());
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
        output = new FileOutputStream(tempFile);
        int count;
        while ((count = origin.read(data,
                                    0,
                                    BUFFER)) != -1) {
            output.write(data,
                         0,
                         count);
        }
        return output;
    }

    public Map<String, String> getIdentityHash() {
        return identityHash;
    }

    /*
     * Generates teh MD5 String based on the hash Byte[]
     */
    private String getMD5String(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                sb.append("0"
                                  + Integer.toHexString((0xFF & hash[i])));
            } else {
                sb.append(Integer.toHexString(0xFF & hash[i]));
            }
        }
        return sb.toString();
    }
}
