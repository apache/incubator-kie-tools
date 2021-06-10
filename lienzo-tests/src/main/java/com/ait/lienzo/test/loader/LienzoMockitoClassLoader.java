/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.test.loader;

import com.ait.lienzo.test.settings.Settings;
import com.ait.lienzo.test.translator.LienzoMockitoClassTranslator;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.Translator;

/**
 * I know, very trick class :/ But it's the only way I found to integrate with current GwtMockito junit
 * runner using same class pool and avoiding building another one on top of it.
 * <p>
 * This class loader does not loads any class, neither the classpath is really set. It always delegates to parent, which use to be the app one from the testing context.
 * <p>
 * It just waits for the GwtMockitoClassLoader present into the current's thread context, and then add a custom javassist translator, that wraps the one from gwt as well,
 * to apply custom stuff in top of th gwt one at runtime class loading time.
 * <p>
 * The reason for this class is that this translation wrapping job must be done on the junit runner constructor,
 * before loading any of our class from interest,
 * so the test class itself and all the classes loaded during tests executions will be loaded and handled by our custom translators.
 *
 * @author Roger Martinez
 * @since 1.0
 */
public class LienzoMockitoClassLoader extends Loader {

    private final Settings settings;

    private boolean initialized = false;

    public LienzoMockitoClassLoader(final Settings settings, final ClassLoader parent, final ClassPool classPool) {
        super(parent, classPool);

        this.settings = settings;
    }

    /**
     * Delegates always to parent class loader.
     */
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        initIfApplies();

        return null;
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        initIfApplies();

        return super.loadClass(name);
    }

    private void initIfApplies() {
        if (!initialized) {
            final ClassLoader l = Thread.currentThread().getContextClassLoader();

            if (l instanceof Translator) {
                final Loader gwtMockitoLoader = (Loader) l;

                final Translator gwtMockitoTranslator = (Translator) gwtMockitoLoader;

                updateLoaderWithLienzoTranslator(gwtMockitoLoader, gwtMockitoTranslator);

                initialized = true;
            }
        }
    }

    public void updateLoaderWithLienzoTranslator(final Loader loader, final Translator translator) {
        try {
            final LienzoMockitoClassTranslator lienzoTranslator = new LienzoMockitoClassTranslator(settings, translator);

            loader.addTranslator(ClassPool.getDefault(), lienzoTranslator);
        } catch (final NotFoundException e) {
            e.printStackTrace();
        } catch (final CannotCompileException e) {
            e.printStackTrace();
        }
    }
}
