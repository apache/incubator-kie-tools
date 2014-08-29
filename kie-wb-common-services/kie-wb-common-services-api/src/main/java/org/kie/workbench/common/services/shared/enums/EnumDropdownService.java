package org.kie.workbench.common.services.shared.enums;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.services.shared.project.KieProject;

@Remote
public interface EnumDropdownService {

    /**
     * @param valuePairs key=value pairs to be interpolated into the expression.
     * @param expression The expression, which will then be eval'ed to generate a String[]
     */
    @Deprecated
    public String[] loadDropDownExpression( final String[] valuePairs,
                                            String expression );

    /**
     * @param project The project on which this enumeration is being inspected
     * @param valuePairs key=value pairs to be interpolated into the expression.
     * @param expression The expression, which will then be eval'ed to generate a String[]
     */
    public String[] loadDropDownExpression( final KieProject project,
                                            final String[] valuePairs,
                                            String expression );

}
