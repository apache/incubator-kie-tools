package org.drools.workbench.screens.guided.rule.service;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface EnumDropdownService {

    /**
     * @param valuePairs key=value pairs to be interpolated into the expression.
     * @param expression The expression, which will then be eval'ed to generate a
     * String[]
     */
    public String[] loadDropDownExpression(final String[] valuePairs,
                                           String expression);

}
