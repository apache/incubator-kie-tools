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


package org.jboss.errai.reflections.scanners;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.common.collect.Lists;
import org.jboss.errai.reflections.ReflectionsException;
import org.jboss.errai.reflections.serializers.JavaCodeSerializer;
import org.jboss.errai.reflections.util.Utils;
import org.jboss.errai.reflections.vfs.Vfs;

/** scans classes and stores fqn as key and full path as value */
public class TypesScanner extends AbstractScanner {
    public boolean acceptsInput(String file) {
        return file.endsWith(".class"); //classes only
    }

    //duplicated from AbstractScanner
    public void scan(Vfs.File file) {
        InputStream inputStream = null;
        try {
            inputStream = file.openInputStream();
            Object cls = getMetadataAdapter().createClassObject(inputStream);
            scan(cls, file);
        } catch (IOException e) {
            throw new ReflectionsException("could not create class file from " + file.getName(), e);
        } finally {
            Utils.close(inputStream);
        }
    }

    private void scan(Object cls, Vfs.File file) {
        //avoid scanning JavaCodeSerializer outputs
        //noinspection unchecked
        if (TypesScanner.isJavaCodeSerializer(getMetadataAdapter().getInterfacesNames(cls))) return;

        @SuppressWarnings({"unchecked"}) String className = getMetadataAdapter().getClassName(cls);

        getStore().put(className, file.getFullPath());
    }

    @Override
    public void scan(Object cls) {
        throw new UnsupportedOperationException("should not get here");
    }

    //
    public static boolean isJavaCodeSerializer(List<String> interfacesNames) {
        return interfacesNames.size() == 1 && javaCodeSerializerInterfaces.contains(interfacesNames.get(0));
    }

    private final static List<String> javaCodeSerializerInterfaces = Lists.newArrayList(
            JavaCodeSerializer.IElement.class.getName(),
            JavaCodeSerializer.IPackage.class.getName(),
            JavaCodeSerializer.IClass.class.getName(),
            JavaCodeSerializer.IField.class.getName(),
            JavaCodeSerializer.IMethod.class.getName()
    );
}
