package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.session.DMNViewerSession;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManagerImpl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultViewerSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;

public class Type_factory__o_k_w_c_d_c_s_DMNViewerSession__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNViewerSession> { public Type_factory__o_k_w_c_d_c_s_DMNViewerSession__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNViewerSession.class, "Type_factory__o_k_w_c_d_c_s_DMNViewerSession__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNViewerSession.class, DefaultViewerSession.class, ViewerSession.class, AbstractSession.class, Object.class, ClientSession.class, DMNSession.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DMNViewerSession createInstance(final ContextManager contextManager) {
    final ManagedSession _session_0 = (ManagedSession) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_i_ManagedSession__quals__j_e_i_Any_j_e_i_Default");
    final CanvasCommandManager<AbstractCanvasHandler> _canvasCommandManager_1 = (CanvasCommandManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DMNViewerSession instance = new DMNViewerSession(_session_0, _canvasCommandManager_1);
    registerDependentScopedReference(instance, _session_0);
    registerDependentScopedReference(instance, _canvasCommandManager_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNViewerSession instance) {
    instance.constructInstance();
  }
}