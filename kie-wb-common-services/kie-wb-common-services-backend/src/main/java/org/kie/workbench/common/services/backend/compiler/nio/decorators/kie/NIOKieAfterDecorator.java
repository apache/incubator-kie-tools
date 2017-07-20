package org.kie.workbench.common.services.backend.compiler.nio.decorators.kie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.kie.builder.impl.FileKieModule;
import org.drools.core.rule.KieModuleMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.workbench.common.services.backend.compiler.AFClassLoaderProvider;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOKieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIOClassLoaderProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * After decorator working with the NIO2 , to read and store the Object created by the Kie takari plugin and placed in the CompilationResponse
 */
public class NIOKieAfterDecorator implements NIOKieCompilerDecorator {

    private static final Logger logger = LoggerFactory.getLogger(NIOKieAfterDecorator.class);
    private NIOKieMavenCompiler compiler;

    public NIOKieAfterDecorator(NIOKieMavenCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public KieCompilationResponse compileSync(NIOCompilationRequest req) {
        KieCompilationResponse res = compiler.compileSync(req);
        if (res.isSuccessful()) {

            if (req.getInfo().isKiePluginPresent()) {
                return handleKieMavenPlugin(req,
                                            res);
            }
        }
        return res;
    }

    private KieCompilationResponse handleKieMavenPlugin(NIOCompilationRequest req,
                                                        CompilationResponse res) {

        KieTuple kieModuleMetaInfoTuple = readKieModuleMetaInfo(req);
        KieTuple kieModuleTuple = readKieModule(req);
        if (kieModuleMetaInfoTuple.getOptionalObject().isPresent() && kieModuleTuple.getOptionalObject().isPresent()) {

            AFClassLoaderProvider provider = new NIOClassLoaderProviderImpl();
            Optional<List<URI>> optionalDeps = provider.getURISFromAllDependencies(req.getInfo().getPrjPath().toAbsolutePath().toString());
            if (req.getKieCliRequest().isLogRequested()) {
                return new DefaultKieCompilationResponse(Boolean.TRUE,
                                                         (KieModuleMetaInfo) kieModuleMetaInfoTuple.getOptionalObject().get(),
                                                         (KieModule) kieModuleTuple.getOptionalObject().get(),
                                                         res.getMavenOutput().get(),
                                                         optionalDeps.get());
            } else {
                return new DefaultKieCompilationResponse(Boolean.TRUE,
                                                         (KieModuleMetaInfo) kieModuleMetaInfoTuple.getOptionalObject().get(),
                                                         (KieModule) kieModuleTuple.getOptionalObject().get(),
                                                         optionalDeps.get());
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

    private KieTuple readKieModuleMetaInfo(NIOCompilationRequest req) {
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

    private KieTuple readKieModule(NIOCompilationRequest req) {

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
        ObjectOutput out = null;
        ByteArrayInputStream bis = null;
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
