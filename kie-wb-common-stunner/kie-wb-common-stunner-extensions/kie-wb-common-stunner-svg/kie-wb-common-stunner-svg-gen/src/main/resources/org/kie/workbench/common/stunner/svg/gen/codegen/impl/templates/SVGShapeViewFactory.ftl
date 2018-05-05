package ${pkg};

import javax.annotation.Generated;
import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
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
    implements ${implementedTypeName} {

    <#list fields as field>
        ${field}
    </#list>

    <#list fmethods as fmethod>

        ${fmethod}

    </#list>

    private static SVGContainer newSVGContainer(final String id,
                                                final Group group,
                                                final boolean scalable,
                                                final LayoutContainer.Layout layout) {
        return new SVGContainer(id,
                                group,
                                scalable,
                                layout);
    }

    private static SVGPrimitiveShape newSVGPrimitiveShape(final Shape<?> primitive,
                                                        final boolean scalable,
                                                        final LayoutContainer.Layout layout) {
        return new SVGPrimitiveShape(primitive,
                                    scalable,
                                    layout);
    }

}
