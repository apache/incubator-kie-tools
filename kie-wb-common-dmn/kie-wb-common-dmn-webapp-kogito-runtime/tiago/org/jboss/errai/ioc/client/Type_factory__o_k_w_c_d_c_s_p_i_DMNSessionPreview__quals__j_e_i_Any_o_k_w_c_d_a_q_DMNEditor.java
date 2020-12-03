package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.session.presenters.impl.DMNSessionPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.Viewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.canvas.CanvasViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPreviewImpl;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;

public class Type_factory__o_k_w_c_d_c_s_p_i_DMNSessionPreview__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNSessionPreview> { public Type_factory__o_k_w_c_d_c_s_p_i_DMNSessionPreview__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNSessionPreview.class, "Type_factory__o_k_w_c_d_c_s_p_i_DMNSessionPreview__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNSessionPreview.class, Object.class, SessionDiagramPreview.class, SessionPreview.class, SessionViewer.class, CanvasViewer.class, Viewer.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DMNSessionPreview createInstance(final ContextManager contextManager) {
    final SessionDiagramPreview<AbstractSession> _delegate_0 = (SessionPreviewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPreviewImpl__quals__j_e_i_Any_j_e_i_Default");
    final DMNSessionPreview instance = new DMNSessionPreview(_delegate_0);
    registerDependentScopedReference(instance, _delegate_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNSessionPreview instance) {
    instance.init();
  }
}