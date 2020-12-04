package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.client.commands.clone.DMNDeepCloneProcess;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenuService;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.DMNUnmarshaller;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;

public class Type_factory__o_k_w_c_d_c_e_d_DRDContextMenuService__quals__j_e_i_Any_j_e_i_Default extends Factory<DRDContextMenuService> { private class Type_factory__o_k_w_c_d_c_e_d_DRDContextMenuService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DRDContextMenuService implements Proxy<DRDContextMenuService> {
    private final ProxyHelper<DRDContextMenuService> proxyHelper = new ProxyHelperImpl<DRDContextMenuService>("Type_factory__o_k_w_c_d_c_e_d_DRDContextMenuService__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_d_DRDContextMenuService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null, null, null);
    }

    public void initProxyProperties(final DRDContextMenuService instance) {

    }

    public DRDContextMenuService asBeanType() {
      return this;
    }

    public void setInstance(final DRDContextMenuService instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public List getDiagrams() {
      if (proxyHelper != null) {
        final DRDContextMenuService proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getDiagrams();
        return retVal;
      } else {
        return super.getDiagrams();
      }
    }

    @Override public void addToNewDRD(Collection selectedNodes) {
      if (proxyHelper != null) {
        final DRDContextMenuService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addToNewDRD(selectedNodes);
      } else {
        super.addToNewDRD(selectedNodes);
      }
    }

    @Override public void addToExistingDRD(DMNDiagramTuple dmnDiagram, Collection selectedNodes) {
      if (proxyHelper != null) {
        final DRDContextMenuService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addToExistingDRD(dmnDiagram, selectedNodes);
      } else {
        super.addToExistingDRD(dmnDiagram, selectedNodes);
      }
    }

    @Override public void removeFromCurrentDRD(Collection selectedNodes) {
      if (proxyHelper != null) {
        final DRDContextMenuService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeFromCurrentDRD(selectedNodes);
      } else {
        super.removeFromCurrentDRD(selectedNodes);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DRDContextMenuService proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_d_DRDContextMenuService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DRDContextMenuService.class, "Type_factory__o_k_w_c_d_c_e_d_DRDContextMenuService__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DRDContextMenuService.class, Object.class });
  }

  public DRDContextMenuService createInstance(final ContextManager contextManager) {
    final FactoryManager _factoryManager_1 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession _dmnDiagramsSession_0 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final Event<DMNDiagramSelected> _selectedEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DMNDiagramSelected.class }, new Annotation[] { });
    final DMNDeepCloneProcess _dmnDeepCloneProcess_4 = (DMNDeepCloneProcess) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_c_DMNDeepCloneProcess__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramUtils _dmnDiagramUtils_3 = (DMNDiagramUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_a_g_DMNDiagramUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNUnmarshaller _dmnUnmarshaller_5 = (DMNUnmarshaller) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_u_DMNUnmarshaller__quals__j_e_i_Any_j_e_i_Default");
    final DRDContextMenuService instance = new DRDContextMenuService(_dmnDiagramsSession_0, _factoryManager_1, _selectedEvent_2, _dmnDiagramUtils_3, _dmnDeepCloneProcess_4, _dmnUnmarshaller_5);
    registerDependentScopedReference(instance, _selectedEvent_2);
    registerDependentScopedReference(instance, _dmnDiagramUtils_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_d_DRDContextMenuService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenuService an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenuService ([org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession, org.kie.workbench.common.stunner.core.api.FactoryManager, javax.enterprise.event.Event, org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils, org.kie.workbench.common.dmn.client.commands.clone.DMNDeepCloneProcess, org.kie.workbench.common.dmn.client.marshaller.unmarshall.DMNUnmarshaller])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DRDContextMenuService> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}