package ${pkg};

import javax.annotation.Generated;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGViewUtils;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGBasicShapeViewImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGContainer;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGPrimitiveFactory;

@Generated("${genClassName}")
@Dependent
public class ${name}
    extends ${extendsTypeName}
    implements ${implementsTypeName} {

    public ${name}() {
        super(${viewBuilder});
    }

    <#list fields as field>
        ${field}
    </#list>

    <#list fmethods as fmethod>

        ${fmethod}

    </#list>

}
