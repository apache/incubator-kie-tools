package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxImpl;
import org.kie.workbench.common.stunner.client.widgets.views.AnimatedFloatingWidgetView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.AbstractCanvasInlineTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.CanvasInlineTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditorBox;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.TextEditorBox;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.graph.Element;

public class Type_factory__o_k_w_c_s_c_c_c_c_i_CanvasInlineTextEditorControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox extends Factory<CanvasInlineTextEditorControl> { public Type_factory__o_k_w_c_s_c_c_c_c_i_CanvasInlineTextEditorControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox() {
    super(new FactoryHandleImpl(CanvasInlineTextEditorControl.class, "Type_factory__o_k_w_c_s_c_c_c_c_i_CanvasInlineTextEditorControl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CanvasInlineTextEditorControl.class, AbstractCanvasInlineTextEditorControl.class, AbstractCanvasHandlerRegistrationControl.class, AbstractCanvasHandlerControl.class, Object.class, CanvasControl.class, CanvasRegistrationControl.class, org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasInlineTextEditorControl.class, RequiresCommandManager.class, SessionAware.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, new InlineTextEditorBox() {
        public Class annotationType() {
          return InlineTextEditorBox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditorBox()";
        }
    } });
  }

  public CanvasInlineTextEditorControl createInstance(final ContextManager contextManager) {
    final FloatingView<IsWidget> _floatingView_0 = (AnimatedFloatingWidgetView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_AnimatedFloatingWidgetView__quals__j_e_i_Any_j_e_i_Default");
    final TextEditorBox<AbstractCanvasHandler, Element> _textEditorBox_1 = (InlineTextEditorBoxImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox");
    final CanvasInlineTextEditorControl instance = new CanvasInlineTextEditorControl(_floatingView_0, _textEditorBox_1);
    registerDependentScopedReference(instance, _floatingView_0);
    registerDependentScopedReference(instance, _textEditorBox_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final CanvasInlineTextEditorControl instance) {
    CanvasInlineTextEditorControl_initParameters(instance);
  }

  public native static void CanvasInlineTextEditorControl_initParameters(CanvasInlineTextEditorControl instance) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.CanvasInlineTextEditorControl::initParameters()();
  }-*/;
}