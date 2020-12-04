package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
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
import org.kie.workbench.common.dmn.client.editors.search.DMNGraphSubIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSubIndex;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.widgets.client.search.common.HasSearchableElements;

public class Type_factory__o_k_w_c_d_c_e_s_DMNGraphSubIndex__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGraphSubIndex> { private class Type_factory__o_k_w_c_d_c_e_s_DMNGraphSubIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNGraphSubIndex implements Proxy<DMNGraphSubIndex> {
    private final ProxyHelper<DMNGraphSubIndex> proxyHelper = new ProxyHelperImpl<DMNGraphSubIndex>("Type_factory__o_k_w_c_d_c_e_s_DMNGraphSubIndex__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_s_DMNGraphSubIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null, null);
    }

    public void initProxyProperties(final DMNGraphSubIndex instance) {

    }

    public DMNGraphSubIndex asBeanType() {
      return this;
    }

    public void setInstance(final DMNGraphSubIndex instance) {
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

    @Override public List getSearchableElements() {
      if (proxyHelper != null) {
        final DMNGraphSubIndex proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getSearchableElements();
        return retVal;
      } else {
        return super.getSearchableElements();
      }
    }

    @Override public void onNoResultsFound() {
      if (proxyHelper != null) {
        final DMNGraphSubIndex proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onNoResultsFound();
      } else {
        super.onNoResultsFound();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNGraphSubIndex proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_s_DMNGraphSubIndex__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNGraphSubIndex.class, "Type_factory__o_k_w_c_d_c_e_s_DMNGraphSubIndex__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNGraphSubIndex.class, Object.class, DMNSubIndex.class, HasSearchableElements.class });
  }

  public DMNGraphSubIndex createInstance(final ContextManager contextManager) {
    final Event<DomainObjectSelectionEvent> _domainObjectSelectionEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DomainObjectSelectionEvent.class }, new Annotation[] { });
    final Event<CanvasFocusedShapeEvent> _canvasFocusedSelectionEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasFocusedShapeEvent.class }, new Annotation[] { });
    final Event<CanvasClearSelectionEvent> _canvasClearSelectionEventEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasClearSelectionEvent.class }, new Annotation[] { });
    final Event<CanvasSelectionEvent> _canvasSelectionEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final DMNGraphUtils _graphUtils_0 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphSubIndex instance = new DMNGraphSubIndex(_graphUtils_0, _canvasSelectionEvent_1, _canvasFocusedSelectionEvent_2, _canvasClearSelectionEventEvent_3, _domainObjectSelectionEvent_4);
    registerDependentScopedReference(instance, _domainObjectSelectionEvent_4);
    registerDependentScopedReference(instance, _canvasFocusedSelectionEvent_2);
    registerDependentScopedReference(instance, _canvasClearSelectionEventEvent_3);
    registerDependentScopedReference(instance, _canvasSelectionEvent_1);
    registerDependentScopedReference(instance, _graphUtils_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_s_DMNGraphSubIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.search.DMNGraphSubIndex an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.search.DMNGraphSubIndex ([org.kie.workbench.common.dmn.client.graph.DMNGraphUtils, javax.enterprise.event.Event, javax.enterprise.event.Event, javax.enterprise.event.Event, javax.enterprise.event.Event])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNGraphSubIndex> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}