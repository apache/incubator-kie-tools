package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSessionState;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

public class Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramsSession> { private class Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDiagramsSession implements Proxy<DMNDiagramsSession> {
    private final ProxyHelper<DMNDiagramsSession> proxyHelper = new ProxyHelperImpl<DMNDiagramsSession>("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DMNDiagramsSession instance) {

    }

    public DMNDiagramsSession asBeanType() {
      return this;
    }

    public void setInstance(final DMNDiagramsSession instance) {
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

    @Override public void destroyState(Metadata metadata) {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroyState(metadata);
      } else {
        super.destroyState(metadata);
      }
    }

    @Override public DMNDiagramsSessionState setState(Metadata metadata, Map diagramsByDiagramElementId, Map dmnDiagramsByDiagramElementId) {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final DMNDiagramsSessionState retVal = proxiedInstance.setState(metadata, diagramsByDiagramElementId, dmnDiagramsByDiagramElementId);
        return retVal;
      } else {
        return super.setState(metadata, diagramsByDiagramElementId, dmnDiagramsByDiagramElementId);
      }
    }

    @Override public boolean isSessionStatePresent() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isSessionStatePresent();
        return retVal;
      } else {
        return super.isSessionStatePresent();
      }
    }

    @Override public DMNDiagramsSessionState getSessionState() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final DMNDiagramsSessionState retVal = proxiedInstance.getSessionState();
        return retVal;
      } else {
        return super.getSessionState();
      }
    }

    @Override public String getCurrentSessionKey() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getCurrentSessionKey();
        return retVal;
      } else {
        return super.getCurrentSessionKey();
      }
    }

    @Override public String getSessionKey(Metadata metadata) {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getSessionKey(metadata);
        return retVal;
      } else {
        return super.getSessionKey(metadata);
      }
    }

    @Override public void add(DMNDiagramElement dmnDiagram, Diagram stunnerDiagram) {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.add(dmnDiagram, stunnerDiagram);
      } else {
        super.add(dmnDiagram, stunnerDiagram);
      }
    }

    @Override public void remove(DMNDiagramElement dmnDiagram) {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.remove(dmnDiagram);
      } else {
        super.remove(dmnDiagram);
      }
    }

    @Override public Diagram getDiagram(String dmnDiagramElementId) {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final Diagram retVal = proxiedInstance.getDiagram(dmnDiagramElementId);
        return retVal;
      } else {
        return super.getDiagram(dmnDiagramElementId);
      }
    }

    @Override public String getCurrentDiagramId() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getCurrentDiagramId();
        return retVal;
      } else {
        return super.getCurrentDiagramId();
      }
    }

    @Override public DMNDiagramElement getDMNDiagramElement(String dmnDiagramElementId) {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final DMNDiagramElement retVal = proxiedInstance.getDMNDiagramElement(dmnDiagramElementId);
        return retVal;
      } else {
        return super.getDMNDiagramElement(dmnDiagramElementId);
      }
    }

    @Override public DMNDiagramTuple getDiagramTuple(String dmnDiagramElementId) {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final DMNDiagramTuple retVal = proxiedInstance.getDiagramTuple(dmnDiagramElementId);
        return retVal;
      } else {
        return super.getDiagramTuple(dmnDiagramElementId);
      }
    }

    @Override public List getDMNDiagrams() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getDMNDiagrams();
        return retVal;
      } else {
        return super.getDMNDiagrams();
      }
    }

    @Override public void onDMNDiagramSelected(DMNDiagramSelected selected) {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onDMNDiagramSelected(selected);
      } else {
        super.onDMNDiagramSelected(selected);
      }
    }

    @Override public boolean belongsToCurrentSessionState(DMNDiagramElement diagramElement) {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.belongsToCurrentSessionState(diagramElement);
        return retVal;
      } else {
        return super.belongsToCurrentSessionState(diagramElement);
      }
    }

    @Override public Optional getCurrentDMNDiagramElement() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getCurrentDMNDiagramElement();
        return retVal;
      } else {
        return super.getCurrentDMNDiagramElement();
      }
    }

    @Override public Optional getCurrentDiagram() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getCurrentDiagram();
        return retVal;
      } else {
        return super.getCurrentDiagram();
      }
    }

    @Override public Diagram getDRGDiagram() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final Diagram retVal = proxiedInstance.getDRGDiagram();
        return retVal;
      } else {
        return super.getDRGDiagram();
      }
    }

    @Override public DMNDiagramElement getDRGDiagramElement() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final DMNDiagramElement retVal = proxiedInstance.getDRGDiagramElement();
        return retVal;
      } else {
        return super.getDRGDiagramElement();
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        super.clear();
      }
    }

    @Override public List getModelDRGElements() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getModelDRGElements();
        return retVal;
      } else {
        return super.getModelDRGElements();
      }
    }

    @Override public List getModelImports() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getModelImports();
        return retVal;
      } else {
        return super.getModelImports();
      }
    }

    @Override public boolean isGlobalGraphSelected() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isGlobalGraphSelected();
        return retVal;
      } else {
        return super.isGlobalGraphSelected();
      }
    }

    @Override public List getGraphs() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getGraphs();
        return retVal;
      } else {
        return super.getGraphs();
      }
    }

    @Override public List getAllNodes() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAllNodes();
        return retVal;
      } else {
        return super.getAllNodes();
      }
    }

    @Override public Diagram getCurrentGraphDiagram() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final Diagram retVal = proxiedInstance.getCurrentGraphDiagram();
        return retVal;
      } else {
        return super.getCurrentGraphDiagram();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDiagramsSession proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDiagramsSession.class, "Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDiagramsSession.class, Object.class, GraphsProvider.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected", new AbstractCDIEventCallback<DMNDiagramSelected>() {
      public void fireEvent(final DMNDiagramSelected event) {
        final DMNDiagramsSession instance = Factory.maybeUnwrapProxy((DMNDiagramsSession) context.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default"));
        instance.onDMNDiagramSelected(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected []";
      }
    });
  }

  public DMNDiagramsSession createInstance(final ContextManager contextManager) {
    final ManagedInstance<DMNDiagramsSessionState> _dmnDiagramsSessionStates_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DMNDiagramsSessionState.class }, new Annotation[] { });
    final SessionManager _sessionManager_1 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramUtils _dmnDiagramUtils_2 = (DMNDiagramUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_a_g_DMNDiagramUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession instance = new DMNDiagramsSession(_dmnDiagramsSessionStates_0, _sessionManager_1, _dmnDiagramUtils_2);
    registerDependentScopedReference(instance, _dmnDiagramsSessionStates_0);
    registerDependentScopedReference(instance, _dmnDiagramUtils_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDiagramsSession> proxyImpl = new Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}