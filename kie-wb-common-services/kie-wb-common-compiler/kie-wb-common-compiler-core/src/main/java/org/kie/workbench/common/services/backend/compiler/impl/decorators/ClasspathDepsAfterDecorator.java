/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl.decorators;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;

/***
 * After decorator that reads the List<String> with the dependencies from the project's modules cretaed by the
 * org.kie.workbench.common.services.backend.maven.plugins.dependency.BuildInMemoryClasspathMojo
 * and store the values in the CompilationResponse
 */
public class ClasspathDepsAfterDecorator<T extends CompilationResponse, C extends AFCompiler<T>> implements CompilerDecorator {

    /**
     * Key used to share the string classpath in the kieMap
     */
    private final String STRING_CLASSPATH_KEY = "stringClasspathKey";

    private C compiler;

    public ClasspathDepsAfterDecorator(C compiler) {
        this.compiler = compiler;
    }

    public C getCompiler() {
        return compiler;
    }

    @Override
    public Boolean cleanInternalCache() {
        return compiler.cleanInternalCache();
    }

    @Override
    public CompilationResponse compile(CompilationRequest req) {
        T res = compiler.compile(req);
        return handleClasspath(req, res);
    }

    @Override
    public CompilationResponse compile(CompilationRequest req, Map override) {
        T res = (T) compiler.compile(req, override);
        return handleClasspath(req, res);
    }

    private T handleClasspath(CompilationRequest req, T res) {
        T t;
        Map<String,Object> kieMap = req.getMap();
        String classpathKey = req.getRequestUUID() + "." + STRING_CLASSPATH_KEY;
        Object o = kieMap.get(classpathKey);
        if(o != null){
            Set<String> depsModules = (Set<String>) o;
            List<String> deps =CompilerClassloaderUtils.readItemsFromClasspathString(depsModules);
            t = (T) new DefaultCompilationResponse(res.isSuccessful(),
                                                   res.getMavenOutput(),
                                                   req.getInfo().getPrjPath(),
                                                   deps);
        }else{
            t = (T) new DefaultCompilationResponse(res.isSuccessful(),
                                                   res.getMavenOutput(),
                                                   req.getInfo().getPrjPath());
        }
        return t;
    }
}
