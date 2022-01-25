/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.reflections.scanners;

import org.jboss.errai.reflections.vfs.Vfs;

/** collects all resources that are not classes in a collection
 * <p>key: value - {web.xml: WEB-INF/web.xml} */
public class ResourcesScanner extends AbstractScanner {
    public boolean acceptsInput(String file) {
        return !file.endsWith(".class"); //not a class
    }

    public void scan(Vfs.File file) {
        getStore().put(file.getName(), file.getRelativePath());
    }

    public void scan(Object cls) {
        throw new UnsupportedOperationException(); //shouldn't get here
    }
}
