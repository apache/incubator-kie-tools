package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.canvas.controls.inlineeditor.DMNCanvasInlineTextEditorControl;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxImpl;
import org.kie.workbench.common.stunner.client.widgets.views.AnimatedFloatingWidgetView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.AbstractCanvasInlineTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.CanvasInlineTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.TextEditorBox;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.graph.Element;

public class Type_factory__o_k_w_c_d_c_c_c_i_DMNCanvasInlineTextEditorControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_b_i_Observer extends Factory<DMNCanvasInlineTextEditorControl> { public Type_factory__o_k_w_c_d_c_c_c_i_DMNCanvasInlineTextEditorControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_b_i_Observer() {
    super(new FactoryHandleImpl(DMNCanvasInlineTextEditorControl.class, "Type_factory__o_k_w_c_d_c_c_c_i_DMNCanvasInlineTextEditorControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_b_i_Observer", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNCanvasInlineTextEditorControl.class, CanvasInlineTextEditorControl.class, AbstractCanvasInlineTextEditorControl.class, AbstractCanvasHandlerRegistrationControl.class, AbstractCanvasHandlerControl.class, Object.class, CanvasControl.class, CanvasRegistrationControl.class, org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasInlineTextEditorControl.class, RequiresCommandManager.class, SessionAware.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
      }, new Observer() {
        public Class annotationType() {
          return Observer.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer()";
        }
    } });
  }

  public DMNCanvasInlineTextEditorControl createInstance(final ContextManager contextManager) {
    final FloatingView<IsWidget> _floatingView_0 = (AnimatedFloatingWidgetView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_AnimatedFloatingWidgetView__quals__j_e_i_Any_j_e_i_Default");
    final TextEditorBox<AbstractCanvasHandler, Element> _textEditorBox_1 = (InlineTextEditorBoxImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox");
    final DMNCanvasInlineTextEditorControl instance = new DMNCanvasInlineTextEditorControl(_floatingView_0, _textEditorBox_1);
    registerDependentScopedReference(instance, _floatingView_0);
    registerDependentScopedReference(instance, _textEditorBox_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNCanvasInlineTextEditorControl instance) {
    CanvasInlineTextEditorControl_initParameters(instance);
    DMNCanvasInlineTextEditorControl_initParameters(instance);
  }

  public native static void CanvasInlineTextEditorControl_initParameters(CanvasInlineTextEditorControl instance) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.CanvasInlineTextEditorControl::initParameters()();
  }-*/;

  public native static void DMNCanvasInlineTextEditorControl_initParameters(DMNCanvasInlineTextEditorControl instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.canvas.controls.inlineeditor.DMNCanvasInlineTextEditorControl::initParameters()();
  }-*/;
}