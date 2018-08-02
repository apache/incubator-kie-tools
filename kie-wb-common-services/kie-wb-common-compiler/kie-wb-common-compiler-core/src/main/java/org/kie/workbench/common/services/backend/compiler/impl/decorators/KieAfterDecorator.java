/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.kie.builder.impl.FileKieModule;
import org.drools.core.rule.KieModuleMetaInfo;
import org.drools.core.rule.TypeMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils.*;

/***
 * After decorator that reads and store the Objects created by the Kie takari plugin and placed the3se Objects in the CompilationResponse
 */
public class KieAfterDecorator<T extends CompilationResponse, C extends AFCompiler<T>> implements CompilerDecorator {

    private static final Logger logger = LoggerFactory.getLogger(KieAfterDecorator.class);
    private C compiler;

    private String prjClassloaderStoreKey = "ProjectClassloaderStore";

    private String eventClassesKey = TypeMetaInfo.class.getName();

    public KieAfterDecorator(C compiler) {
        this.compiler = compiler;
    }

    //for test
    public C getCompiler() {
        return compiler;
    }

    @Override
    public Boolean cleanInternalCache() {
        return compiler.cleanInternalCache();
    }

    @Override
    public T compile(CompilationRequest req) {
        T res = compiler.compile(req);
        return handleAfter(req, res);
    }

    @Override
    public CompilationResponse compile(CompilationRequest req, Map override) {
        T res = (T) compiler.compile(req, override);
        return handleAfter(req, res);
    }

    private T handleAfter(CompilationRequest req, T res) {
        if (req.getInfo().isKiePluginPresent()) {
            return (T) handleKieMavenPlugin(req, res);
        }
        return (T) handleNormalBuild(req, res);
    }

    private KieCompilationResponse handleKieMavenPlugin(CompilationRequest req,
                                                        CompilationResponse res) {

        final KieTuple kieModuleMetaInfoTuple = read(req, KieModuleMetaInfo.class.getName(), "kieModuleMetaInfo not present in the map");
        final KieTuple kieModuleTuple = read(req, FileKieModule.class.getName(), "kieModule not present in the map");

        if (kieModuleMetaInfoTuple.getOptionalObject().isPresent() && kieModuleTuple.getOptionalObject().isPresent()) {
            final List<String> targetContent = getStringFromTargets(req.getInfo().getPrjPath());
            final KieTuple kieProjectClassloaderStore = read(req, prjClassloaderStoreKey, "ProjectClassLoaderStore Map not present in the map");
            final KieTuple eventClasses = read(req, eventClassesKey, "EventClasses Set not present in the map");
            final Set<String> events = getEventTypes(eventClasses);
            final Map<String, byte[]> store = getDroolsGeneratedClasses(kieProjectClassloaderStore);

            return new DefaultKieCompilationResponse(res.isSuccessful(),
                                                     (KieModuleMetaInfo) kieModuleMetaInfoTuple.getOptionalObject().get(),
                                                     (KieModule) kieModuleTuple.getOptionalObject().get(),
                                                     store,
                                                     res.getMavenOutput(),
                                                     targetContent,
                                                     res.getDependencies(),
                                                     req.getInfo().getPrjPath(),
                                                     events);
        } else {
            List<String> msgs = new ArrayList<>();
            if (kieModuleMetaInfoTuple.getErrorMsg().isPresent()) {
                msgs.add("[ERROR] Error in the kieModuleMetaInfo from the kieMap :" + kieModuleMetaInfoTuple.getErrorMsg().get());
            }
            if (kieModuleTuple.getErrorMsg().isPresent()) {
                msgs.add("[ERROR] Error in the kieModule :" + kieModuleTuple.getErrorMsg().get());
            }
            msgs.addAll(res.getMavenOutput());
            return new DefaultKieCompilationResponse(Boolean.FALSE, msgs, req.getInfo().getPrjPath());
        }
    }

    private Map<String, byte[]> getDroolsGeneratedClasses(KieTuple kieProjectClassloaderStore) {
        return (Map<String, byte[]>) kieProjectClassloaderStore.getOptionalObject().orElse(Collections.emptyMap());
    }

    private Set<String> getEventTypes(final KieTuple eventClasses) {
        return (Set<String>) eventClasses.getOptionalObject().orElse(Collections.emptySet());
    }

    private KieCompilationResponse handleNormalBuild(final CompilationRequest req,
                                                     final CompilationResponse res) {

        final List<String> targetContent = getStringFromTargets(req.getInfo().getPrjPath());
        if (res.isSuccessful()) {
            return new DefaultKieCompilationResponse(res.isSuccessful(), res.getMavenOutput(), targetContent, res.getDependencies(), req.getInfo().getPrjPath());
        } else {
            return new DefaultKieCompilationResponse(res.isSuccessful(), res.getMavenOutput(), req.getInfo().getPrjPath());
        }
    }

    private KieTuple read(CompilationRequest req, String keyName, String errorMsg) {
        final StringBuilder sb = new StringBuilder(req.getKieCliRequest().getRequestUUID()).append(".").append(keyName);
        Object o = req.getKieCliRequest().getMap().get(sb.toString());
        if (o != null) {
            KieTuple tuple = readObjectFromADifferentClassloader(o);
            if (tuple.getOptionalObject().isPresent()) {
                return new KieTuple(tuple.getOptionalObject().get());
            } else {
                return new KieTuple(tuple.getErrorMsg());
            }
        } else {
            return new KieTuple(errorMsg);
        }
    }

    private KieTuple readObjectFromADifferentClassloader(Object o) {

        ObjectInput in = null;
        ObjectOutput out;
        ByteArrayInputStream bis;
        ByteArrayOutputStream bos = null;

        try {
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.flush();
            byte[] objBytes = bos.toByteArray();
            bis = new ByteArrayInputStream(objBytes);
            in = new ObjectInputStream(bis);
            Object newObj = in.readObject();
            return new KieTuple(newObj);
        } catch (NotSerializableException nse) {
            nse.printStackTrace();
            StringBuilder sb = new StringBuilder("NotSerializableException:").append(nse.getMessage());
            return new KieTuple(sb.toString());
        } catch (IOException ioe) {
            StringBuilder sb = new StringBuilder("IOException:").append(ioe.getMessage());
            return new KieTuple(sb.toString());
        } catch (ClassNotFoundException cnfe) {
            StringBuilder sb = new StringBuilder("ClassNotFoundException:").append(cnfe.getMessage());
            return new KieTuple(sb.toString());
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("Exception:").append(e.getMessage());
            return new KieTuple(sb.toString());
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

}
