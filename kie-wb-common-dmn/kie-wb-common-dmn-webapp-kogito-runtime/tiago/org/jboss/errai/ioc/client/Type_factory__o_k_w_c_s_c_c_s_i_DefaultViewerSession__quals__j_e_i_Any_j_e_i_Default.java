package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManagerImpl;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultViewerSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;

public class Type_factory__o_k_w_c_s_c_c_s_i_DefaultViewerSession__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultViewerSession> { public Type_factory__o_k_w_c_s_c_c_s_i_DefaultViewerSession__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultViewerSession.class, "Type_factory__o_k_w_c_s_c_c_s_i_DefaultViewerSession__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultViewerSession.class, ViewerSession.class, AbstractSession.class, Object.class, ClientSession.class });
  }

  public DefaultViewerSession createInstance(final ContextManager contextManager) {
    final ManagedSession _session_0 = (ManagedSession) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_i_ManagedSession__quals__j_e_i_Any_j_e_i_Default");
    final CanvasCommandManager<AbstractCanvasHandler> _canvasCommandManager_1 = (CanvasCommandManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefaultViewerSession instance = new DefaultViewerSession(_session_0, _canvasCommandManager_1);
    registerDependentScopedReference(instance, _session_0);
    registerDependentScopedReference(instance, _canvasCommandManager_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DefaultViewerSession instance) {
    instance.constructInstance();
  }
}