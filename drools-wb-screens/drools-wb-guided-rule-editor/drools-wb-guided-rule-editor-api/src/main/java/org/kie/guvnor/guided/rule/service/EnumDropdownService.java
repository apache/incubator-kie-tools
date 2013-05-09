package org.kie.guvnor.guided.rule.service;

public interface EnumDropdownService {

    /**
     * @param valuePairs key=value pairs to be interpolated into the expression.
     * @param expression The expression, which will then be eval'ed to generate a
     * String[]
     */
    public String[] loadDropDownExpression(final String[] valuePairs,
                                           String expression);

}
