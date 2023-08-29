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


package org.jboss.errai.reflections;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.google.common.base.Predicates;
import org.jboss.errai.reflections.util.ClasspathHelper;
import org.jboss.errai.reflections.vfs.Vfs;
import org.junit.Assert;
import org.junit.Test;

/** */
public class VfsTest {

    private void testVfsDir(URL url) {
        Assert.assertNotNull(url);

        Vfs.Dir dir = Vfs.fromURL(url);
        Assert.assertNotNull(dir);

        Iterable<Vfs.File> files = dir.getFiles();
        Vfs.File first = files.iterator().next();
        Assert.assertNotNull(first);

        first.getName();
        try {
            first.openInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dir.close();
    }

    @Test
    public void vfsFromJar() {
        testVfsDir(getSomeJar());
    }

    @Test
    public void vfsFromDir() {
        testVfsDir(getSomeDirectory());
    }

    @Test
    public void vfsFromJarFileUrl() throws MalformedURLException {
        testVfsDir(new URL("jar:file:" + getSomeJar().getPath() + "!/"));
    }

    @Test
    public void findFilesFromEmptyMatch() throws MalformedURLException {
        final URL jar = getSomeJar();
        final Iterable<Vfs.File> files = Vfs.findFiles(java.util.Arrays.asList(jar), Predicates.<Vfs.File>alwaysFalse());
        Assert.assertNotNull(files);
        Assert.assertFalse(files.iterator().hasNext());
    }

    @Test
    public void vfsFromJarWithInnerJars() {
        //todo?
    }

    //
    public URL getSomeJar() {
        Collection<URL> urls = ClasspathHelper.forClassLoader();
        for (URL url : urls) {
            if (url.getFile().endsWith(".jar")) {
                return url;
            }
        }

        Assert.fail("could not find jar url");
        return null;
    }

    private URL getSomeDirectory() {
        try {
            return new File(ReflectionsTest.getUserDir()).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
