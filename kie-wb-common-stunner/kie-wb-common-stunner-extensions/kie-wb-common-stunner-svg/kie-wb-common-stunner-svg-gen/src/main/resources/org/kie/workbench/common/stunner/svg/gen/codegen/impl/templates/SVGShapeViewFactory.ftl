package ${pkg};

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGBasicShapeViewImpl;

@Generated("${genClassName}")
@ApplicationScoped
public class ${name}
    implements ${implementedTypeName} {

    <#list fmethods as fmethod>

        ${fmethod}

    </#list>

}
