/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client;

import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.displayer.client.resources.i18n.DisplayerConstants;

public class DisplayerGwtExprEval implements AbstractDisplayer.ExpressionEval {

    public static final String[] _jsMalicious = {"document.", "window.", "alert(", "eval(", ".innerHTML"};

    AbstractDisplayer presenter = null;

    public DisplayerGwtExprEval(AbstractDisplayer presenter) {
        this.presenter = presenter;
    }

    @Override
    public String evalExpression(String val, String expr) {
        if (StringUtils.isBlank(expr)) {
            return val;
        }
        for (String keyword : _jsMalicious) {
            if (expr.contains(keyword)) {
                presenter.handleError(DisplayerConstants.INSTANCE.displayer_keyword_not_allowed(expr));
                throw new RuntimeException(DisplayerConstants.INSTANCE.displayer_keyword_not_allowed(expr));
            }
        }
        try {
            return _evalExpression(val, expr);
        } catch (Exception e) {
            presenter.handleError(DisplayerConstants.INSTANCE.displayer_expr_invalid_syntax(expr), e);
            throw new RuntimeException(DisplayerConstants.INSTANCE.displayer_expr_invalid_syntax(expr));
        }
    }

    protected native String _evalExpression(String val, String expr) /*-{
        value = val;
        return eval(expr) + '';
    }-*/;
}
