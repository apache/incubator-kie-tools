package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.AbstractInlineTextEditorBox;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineEditorBoxView.Presenter;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxImpl;
import org.kie.workbench.common.stunner.client.widgets.inlineeditor.InlineTextEditorBoxViewImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditorBox;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.TextEditorBox;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;

public class Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox extends Factory<InlineTextEditorBoxImpl> { public Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox() {
    super(new FactoryHandleImpl(InlineTextEditorBoxImpl.class, "Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { InlineTextEditorBoxImpl.class, AbstractInlineTextEditorBox.class, Object.class, Presenter.class, TextEditorBox.class, IsElement.class, RequiresCommandManager.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new InlineTextEditorBox() {
        public Class annotationType() {
          return InlineTextEditorBox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditorBox()";
        }
    } });
  }

  public InlineTextEditorBoxImpl createInstance(final ContextManager contextManager) {
    final InlineTextEditorBoxViewImpl _view_0 = (InlineTextEditorBoxViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_i_InlineTextEditorBoxViewImpl__quals__j_e_i_Any_o_k_w_c_s_c_c_c_c_i_InlineTextEditorBox");
    final InlineTextEditorBoxImpl instance = new InlineTextEditorBoxImpl(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((InlineTextEditorBoxImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final InlineTextEditorBoxImpl instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final InlineTextEditorBoxImpl instance) {
    instance.setup();
  }
}