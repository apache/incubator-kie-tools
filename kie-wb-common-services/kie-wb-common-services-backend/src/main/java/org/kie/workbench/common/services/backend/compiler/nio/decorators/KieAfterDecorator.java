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
package org.kie.workbench.common.services.backend.compiler.nio.decorators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.kie.builder.impl.FileKieModule;
import org.drools.core.rule.KieModuleMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.workbench.common.services.backend.compiler.AFClassLoaderProvider;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.nio.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.impl.ClassLoaderProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * After decorator that reads and store the Object created by the Kie takari plugin and placed in the CompilationResponse
 */
public class KieAfterDecorator<T extends CompilationResponse, C extends AFCompiler<T>> implements CompilerDecorator {

    private static final Logger logger = LoggerFactory.getLogger(KieAfterDecorator.class);
    private C compiler;

    public KieAfterDecorator(C compiler) {
        this.compiler = compiler;
    }

    @Override
    public T compileSync(CompilationRequest req) {
        T res = compiler.compileSync(req);
        if (res.isSuccessful()) {

            if (req.getInfo().isKiePluginPresent()) {
                return (T) handleKieMavenPlugin(req,
                                                res);
            }
        }
        return res;
    }

    @Override
    public T buildDefaultCompilationResponse(final Boolean value) {
        return compiler.buildDefaultCompilationResponse(value);
    }

    @Override
    public T buildDefaultCompilationResponse(final Boolean value,
                                             final List output) {
        return compiler.buildDefaultCompilationResponse(value);
    }

    private KieCompilationResponse handleKieMavenPlugin(CompilationRequest req,
                                                        CompilationResponse res) {

        KieTuple kieModuleMetaInfoTuple = readKieModuleMetaInfo(req);
        KieTuple kieModuleTuple = readKieModule(req);
        if (kieModuleMetaInfoTuple.getOptionalObject().isPresent() && kieModuleTuple.getOptionalObject().isPresent()) {

            AFClassLoaderProvider provider = new ClassLoaderProviderImpl();
            Optional<List<URI>> optionalDeps = provider.getURISFromAllDependencies(req.getInfo().getPrjPath().toAbsolutePath().toString());
            List<URI> uris = Collections.emptyList();
            if(optionalDeps.isPresent()){
                uris = optionalDeps.get();
            }
            if (req.getKieCliRequest().isLogRequested()) {
                return new DefaultKieCompilationResponse(Boolean.TRUE,
                                                         (KieModuleMetaInfo) kieModuleMetaInfoTuple.getOptionalObject().get(),
                                                         (KieModule) kieModuleTuple.getOptionalObject().get(),
                                                         res.getMavenOutput().get(),
                                                         uris);
            } else {
                return new DefaultKieCompilationResponse(Boolean.TRUE,
                                                         (KieModuleMetaInfo) kieModuleMetaInfoTuple.getOptionalObject().get(),
                                                         (KieModule) kieModuleTuple.getOptionalObject().get(),
                                                         uris);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            if (kieModuleMetaInfoTuple.getErrorMsg().isPresent()) {
                sb.append(" Error in the kieModuleMetaInfo from the kieMap:").append(kieModuleMetaInfoTuple.getErrorMsg().get());
            }
            if (kieModuleTuple.getErrorMsg().isPresent()) {
                sb.append(" Error in the kieModule:").append(kieModuleTuple.getErrorMsg().get());
            }
            if (req.getKieCliRequest().isLogRequested()) {
                return new DefaultKieCompilationResponse(Boolean.FALSE,
                                                         sb.toString(),
                                                         res.getMavenOutput().get());
            } else {
                return new DefaultKieCompilationResponse(Boolean.FALSE,
                                                         sb.toString());
            }
        }
    }

    private KieTuple readKieModuleMetaInfo(CompilationRequest req) {
        /** This part is mandatory because the object loaded in the kie maven plugin is
         * loaded in a different classloader and every accessing cause a ClassCastException
         * Standard for the kieMap's keys -> compilationID + dot + classname
         * */
        StringBuilder sb = new StringBuilder(req.getKieCliRequest().getRequestUUID()).append(".").append(KieModuleMetaInfo.class.getName());
        Object o = req.getKieCliRequest().getMap().get(sb.toString());
        if (o != null) {
            KieTuple tuple = readObjectFromADifferentClassloader(o);

            if (tuple.getOptionalObject().isPresent()) {

                return new KieTuple(tuple.getOptionalObject().get());
            } else {

                return new KieTuple(
                        tuple.getErrorMsg());
            }
        } else {
            return new KieTuple("kieModuleMetaInfo not present in the map");
        }
    }

    private KieTuple readKieModule(CompilationRequest req) {
        /** This part is mandatory because the object loaded in the kie maven plugin is
         * loaded in a different classloader and every accessing cause a ClassCastException
         * Standard for the kieMap's keys -> compilationID + dot + classname
         * */
        StringBuilder sb = new StringBuilder(req.getKieCliRequest().getRequestUUID()).append(".").append(FileKieModule.class.getName());
        Object o = req.getKieCliRequest().getMap().get(sb.toString());

        if (o != null) {
            KieTuple tuple = readObjectFromADifferentClassloader(o);

            if (tuple.getOptionalObject().isPresent()) {

                return new KieTuple(tuple.getOptionalObject().get());
            } else {

                return new KieTuple(
                        tuple.getErrorMsg());
            }
        } else {

            return new KieTuple("kieModule not present in the map");
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

    static class KieTuple {

        private Object optionalObj;
        private String errorMsg;

        public KieTuple(Object optionalObj) {
            this.optionalObj = optionalObj;
        }

        public KieTuple(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public Optional<Object> getOptionalObject() {
            return Optional.ofNullable(optionalObj);
        }

        public Optional<String> getErrorMsg() {
            return Optional.ofNullable(errorMsg);
        }
    }
}
