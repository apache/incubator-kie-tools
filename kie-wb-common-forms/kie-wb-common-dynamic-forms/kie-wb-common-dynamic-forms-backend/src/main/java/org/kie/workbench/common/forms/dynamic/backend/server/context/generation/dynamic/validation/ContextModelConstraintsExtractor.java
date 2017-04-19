package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.validation;

import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;

/**
 * Component able to read the constraints on form models
 */
public interface ContextModelConstraintsExtractor {

    /**
     * Checks for bean validation constraints on the form models and initializes the context validations
     * @param clientRenderingContext
     * @param classLoader
     */
    void readModelConstraints(MapModelRenderingContext clientRenderingContext,
                              ClassLoader classLoader);
}
