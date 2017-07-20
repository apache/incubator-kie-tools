package org.kie.workbench.common.services.backend.compiler.nio.decorators;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.LogUtils;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.kie.NIOKieAfterDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * After decorator to read and store the maven output into a List<String> in the CompilationResponse with NIO2 impl
 */
public class NIOOutputLogAfterDecorator implements NIOCompilerDecorator {

    private static final Logger logger = LoggerFactory.getLogger(NIOKieAfterDecorator.class);
    private NIOMavenCompiler compiler;

    public NIOOutputLogAfterDecorator(NIOMavenCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public CompilationResponse compileSync(NIOCompilationRequest req) {

        CompilationResponse res = compiler.compileSync(req);

        if (res.isSuccessful()) {
            return getDefaultCompilationResponse(Boolean.TRUE,
                                                 req);
        } else {
            return getDefaultCompilationResponse(Boolean.FALSE,
                                                 req);
        }
    }

    public DefaultCompilationResponse getDefaultCompilationResponse(Boolean result,
                                                                    NIOCompilationRequest req) {
        return new DefaultCompilationResponse(result,
                                              LogUtils.getOutput(req.getInfo().getPrjPath().toAbsolutePath().toString(),
                                                                 req.getKieCliRequest().getRequestUUID())
        );
    }
}
