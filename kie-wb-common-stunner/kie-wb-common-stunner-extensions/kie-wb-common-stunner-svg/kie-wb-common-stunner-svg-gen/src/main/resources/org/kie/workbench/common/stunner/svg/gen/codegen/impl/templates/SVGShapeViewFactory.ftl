package ${pkg};

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeStateHolder;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGBasicShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGBasicShapeViewImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGContainer;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitivePolicy;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGPrimitivePolicies;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGPrimitiveFactory;

@Generated("${genClassName}")
@ApplicationScoped
public class ${name}
    implements ${implementedTypeName} {

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
                                                        final LayoutContainer.Layout layout,
                                                        final SVGPrimitivePolicy policy) {
        return new SVGPrimitiveShape(primitive,
                                    scalable,
                                    layout,
                                    policy);
    }

}
