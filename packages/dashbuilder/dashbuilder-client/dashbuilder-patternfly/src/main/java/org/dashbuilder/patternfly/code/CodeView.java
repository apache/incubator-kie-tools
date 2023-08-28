package org.dashbuilder.patternfly.code;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class CodeView {

    @Inject
    @DataField
    HTMLDivElement root;

    @Inject
    @DataField
    @Named("code")
    HTMLElement content;

    public void setContent(String content) {
        this.content.textContent = content;
    }
    
    public HTMLElement getElement() {
        return root;
    }
}
