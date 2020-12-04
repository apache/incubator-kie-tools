package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.common.CanvasFocusUtils;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;

public class Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasFocusUtils> { private class Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends CanvasFocusUtils implements Proxy<CanvasFocusUtils> {
    private final ProxyHelper<CanvasFocusUtils> proxyHelper = new ProxyHelperImpl<CanvasFocusUtils>("Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null);
    }

    public void initProxyProperties(final CanvasFocusUtils instance) {

    }

    public CanvasFocusUtils asBeanType() {
      return this;
    }

    public void setInstance(final CanvasFocusUtils instance) {
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

    @Override public void focus(String nodeUUID) {
      if (proxyHelper != null) {
        final CanvasFocusUtils proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.focus(nodeUUID);
      } else {
        super.focus(nodeUUID);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final CanvasFocusUtils proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CanvasFocusUtils.class, "Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CanvasFocusUtils.class, Object.class });
  }

  public CanvasFocusUtils createInstance(final ContextManager contextManager) {
    final DMNGraphUtils _dmnGraphUtils_0 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasFocusedShapeEvent> _canvasFocusedSelectionEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasFocusedShapeEvent.class }, new Annotation[] { });
    final Event<CanvasSelectionEvent> _canvasSelectionEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final CanvasFocusUtils instance = new CanvasFocusUtils(_dmnGraphUtils_0, _canvasFocusedSelectionEvent_1, _canvasSelectionEvent_2);
    registerDependentScopedReference(instance, _dmnGraphUtils_0);
    registerDependentScopedReference(instance, _canvasFocusedSelectionEvent_1);
    registerDependentScopedReference(instance, _canvasSelectionEvent_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.docks.navigator.common.CanvasFocusUtils an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.docks.navigator.common.CanvasFocusUtils ([org.kie.workbench.common.dmn.client.graph.DMNGraphUtils, javax.enterprise.event.Event, javax.enterprise.event.Event])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<CanvasFocusUtils> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}