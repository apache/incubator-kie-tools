package org.kie.workbench.common.services.backend.compiler.nio.decorators.kie;

import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.LogUtils;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOKieMavenCompiler;

/***
 * After decorator to read and store the maven output into a List<String> in the CompilationResponse with NIO2 on a Kie Project
 */
public class NIOKieOutputLogAfterDecorator implements NIOKieCompilerDecorator {

    private NIOKieMavenCompiler compiler;

    public NIOKieOutputLogAfterDecorator(NIOKieMavenCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public KieCompilationResponse compileSync(NIOCompilationRequest req) {

        KieCompilationResponse res = compiler.compileSync(req);

        if (res.isSuccessful()) {
            return getDefaultCompilationResponse(Boolean.TRUE,
                                                 req);
        } else {
            return getDefaultCompilationResponse(Boolean.FALSE,
                                                 req);
        }
    }

    public DefaultKieCompilationResponse getDefaultCompilationResponse(Boolean result,
                                                                       NIOCompilationRequest req) {
        return new DefaultKieCompilationResponse(result,
                                                 LogUtils.getOutput(req.getInfo().getPrjPath().toAbsolutePath().toString(),
                                                                    req.getKieCliRequest().getRequestUUID())
        );
    }
}
