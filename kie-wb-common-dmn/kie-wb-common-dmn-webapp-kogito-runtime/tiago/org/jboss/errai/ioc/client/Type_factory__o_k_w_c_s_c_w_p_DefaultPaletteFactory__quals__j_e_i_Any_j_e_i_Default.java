package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.palette.DefaultPaletteFactory;
import org.kie.workbench.common.stunner.client.widgets.palette.DefaultPaletteWidget;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.CanvasShapeDragStartEvent;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.CanvasShapeDragUpdateEvent;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteFactory;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_w_p_DefaultPaletteFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPaletteFactory> { public Type_factory__o_k_w_c_s_c_w_p_DefaultPaletteFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultPaletteFactory.class, "Type_factory__o_k_w_c_s_c_w_p_DefaultPaletteFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultPaletteFactory.class, Object.class, PaletteFactory.class });
  }

  public DefaultPaletteFactory createInstance(final ContextManager contextManager) {
    final Event<CanvasShapeDragUpdateEvent> _canvasShapeDragUpdateEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasShapeDragUpdateEvent.class }, new Annotation[] { });
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<PaletteDefinitionBuilder> _paletteDefinitionBuilders_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { PaletteDefinitionBuilder.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final Event<CanvasShapeDragStartEvent> _canvasShapeDragStartEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasShapeDragStartEvent.class }, new Annotation[] { });
    final ManagedInstance<DefaultPaletteWidget> _palettes_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefaultPaletteWidget.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final Event<BuildCanvasShapeEvent> _buildCanvasShapeEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { BuildCanvasShapeEvent.class }, new Annotation[] { });
    final DefaultPaletteFactory instance = new DefaultPaletteFactory(_definitionUtils_0, _paletteDefinitionBuilders_1, _palettes_2, _buildCanvasShapeEvent_3, _canvasShapeDragStartEvent_4, _canvasShapeDragUpdateEvent_5);
    registerDependentScopedReference(instance, _canvasShapeDragUpdateEvent_5);
    registerDependentScopedReference(instance, _paletteDefinitionBuilders_1);
    registerDependentScopedReference(instance, _canvasShapeDragStartEvent_4);
    registerDependentScopedReference(instance, _palettes_2);
    registerDependentScopedReference(instance, _buildCanvasShapeEvent_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefaultPaletteFactory) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefaultPaletteFactory instance, final ContextManager contextManager) {
    instance.destroy();
  }
}