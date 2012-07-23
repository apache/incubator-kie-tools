/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.Files;
import org.drools.java.nio.file.Path;
import org.drools.rule.MapBackedClassLoader;

public class ClassLoaderBuilderVFS {

    private final List<JarInputStream> jarInputStreams;

    public ClassLoaderBuilderVFS(Path packageRootDir) {
        this.jarInputStreams = getJars(packageRootDir);
    }

    public ClassLoaderBuilderVFS(List<JarInputStream> jarInputStreams) {
        this.jarInputStreams = jarInputStreams;
    }

    /**
     * Load up all the Jars for the given package.
     *
     * @param assetItemIterator
     */
    private List<JarInputStream> getJars(Path packageRootDir) {
        List<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();

        DirectoryStream<Path> paths = Files.newDirectoryStream(packageRootDir);
        for ( final Path assetPath : paths ) {
            if(assetPath.getFileName().endsWith(AssetFormats.MODEL)) {
                try {
                    final InputStream is = Files.newInputStream(assetPath);
                    jarInputStreams.add(new JarInputStream(is, false));
                } catch (IOException e) {
                    //TODO: Not a place for RulesRepositoryException -Rikkola-
                    //throw new RulesRepositoryException(e);
                }
            }
        }

/*            
            AssetItem item = assetItemIterator.next();
            if (item.getBinaryContentAttachment() != null) {
                try {
                    jarInputStreams.add(new JarInputStream(item.getBinaryContentAttachment(), false));
                } catch (IOException e) {
                    //TODO: Not a place for RulesRepositoryException -Rikkola-
                    throw new RulesRepositoryException(e);
                }
            }*/
        
        return jarInputStreams;
    }

    public List<JarInputStream> getJarInputStreams() {
        return jarInputStreams;
    }

    /**
     * For a given list of Jars, create a class loader.
     */
    public MapBackedClassLoader buildClassLoader() {
        MapBackedClassLoader mapBackedClassLoader = getMapBackedClassLoader();

        try {
            for (JarInputStream jis : jarInputStreams) {
                JarEntry entry = null;
                byte[] buf = new byte[1024];
                int len = 0;
                while ((entry = jis.getNextJarEntry()) != null) {
                    if (!entry.isDirectory() && !entry.getName().endsWith(".java")) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        while ((len = jis.read(buf)) >= 0) {
                            out.write(buf, 0, len);
                        }

                        mapBackedClassLoader.addResource(entry.getName(), out.toByteArray());
                    }
                }

            }
        } catch (IOException e) {
            //TODO: Not a place for RulesRepositoryException -Rikkola-
            //throw new RulesRepositoryException(e);
        }

        return mapBackedClassLoader;
    }

    private MapBackedClassLoader getMapBackedClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<MapBackedClassLoader>() {
            public MapBackedClassLoader run() {
                return new MapBackedClassLoader(getParentClassLoader());
            }
        });
    }

    private ClassLoader getParentClassLoader() {
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        if (parentClassLoader == null) {
            //TODO:
            //parentClassLoader = BRMSPackageBuilder.class.getClassLoader();
        }
        return parentClassLoader;
    }

    public boolean hasJars() {
        return jarInputStreams != null && !jarInputStreams.isEmpty();
    }
}
