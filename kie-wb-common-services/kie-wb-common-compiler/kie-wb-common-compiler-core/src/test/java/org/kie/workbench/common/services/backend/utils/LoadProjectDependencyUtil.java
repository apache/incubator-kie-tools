/*
 * Copyright 2018 JBoss by Red Hat.
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
package org.kie.workbench.common.services.backend.utils;

import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import org.slf4j.Logger;

public class LoadProjectDependencyUtil {

    public static void loadLoggerFactory(ClassLoader prjClassloader) throws Exception {
        //we try to load the only dep in the prj with a simple call method to see if is loaded or not
        Class clazz;
        try {
            clazz = prjClassloader.loadClass("org.slf4j.LoggerFactory");
            assertThat(clazz.isInterface()).isFalse();

            Method m = clazz.getMethod("getLogger", String.class);
            Logger logger = (Logger) m.invoke(clazz, "Dummy");
            assertThat(logger.getName()).isEqualTo("Dummy");
            logger.info("dependency loaded from the prj classpath");
        } catch (ClassNotFoundException e) {
            fail("Test fail due ClassNotFoundException.", e);
        }
    }

    public static void loadDummyB(ClassLoader prjClassloader) throws Exception {
        //we try to load the only dep in the prj with a simple call method to see if is loaded or not
        Class clazz;
        try {
            clazz = prjClassloader.loadClass("dummy.DummyB");
            assertThat(clazz.isInterface()).isFalse();
            Object obj = clazz.newInstance();

            assertThat(obj.toString()).startsWith("dummy.DummyB");

            Method m = clazz.getMethod("greetings",
                    new Class[]{});
            Object greeting = m.invoke(obj,
                    new Object[]{});
            assertThat(greeting.toString()).isEqualTo("Hello World !");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("Test fail due ClassNotFoundException.", e);
        }
    }
}
