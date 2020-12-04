package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;

public class Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default extends Factory<PropertiesPanelNotifier> { private class Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PropertiesPanelNotifier implements Proxy<PropertiesPanelNotifier> {
    private final ProxyHelper<PropertiesPanelNotifier> proxyHelper = new ProxyHelperImpl<PropertiesPanelNotifier>("Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final PropertiesPanelNotifier instance) {

    }

    public PropertiesPanelNotifier asBeanType() {
      return this;
    }

    public void setInstance(final PropertiesPanelNotifier instance) {
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

    @Override public PropertiesPanelNotifier withOldLocalPart(String oldLocalPart) {
      if (proxyHelper != null) {
        final PropertiesPanelNotifier proxiedInstance = proxyHelper.getInstance(this);
        final PropertiesPanelNotifier retVal = proxiedInstance.withOldLocalPart(oldLocalPart);
        return retVal;
      } else {
        return super.withOldLocalPart(oldLocalPart);
      }
    }

    @Override public PropertiesPanelNotifier withNewQName(QName newQName) {
      if (proxyHelper != null) {
        final PropertiesPanelNotifier proxiedInstance = proxyHelper.getInstance(this);
        final PropertiesPanelNotifier retVal = proxiedInstance.withNewQName(newQName);
        return retVal;
      } else {
        return super.withNewQName(newQName);
      }
    }

    @Override public void notifyPanel() {
      if (proxyHelper != null) {
        final PropertiesPanelNotifier proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.notifyPanel();
      } else {
        super.notifyPanel();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PropertiesPanelNotifier proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PropertiesPanelNotifier.class, "Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PropertiesPanelNotifier.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent", new AbstractCDIEventCallback<CanvasSelectionEvent>() {
      public void fireEvent(final CanvasSelectionEvent event) {
        final PropertiesPanelNotifier instance = Factory.maybeUnwrapProxy((PropertiesPanelNotifier) context.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default"));
        PropertiesPanelNotifier_onCanvasSelectionEvent_CanvasSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent", new AbstractCDIEventCallback<DomainObjectSelectionEvent>() {
      public void fireEvent(final DomainObjectSelectionEvent event) {
        final PropertiesPanelNotifier instance = Factory.maybeUnwrapProxy((PropertiesPanelNotifier) context.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default"));
        PropertiesPanelNotifier_onDomainObjectSelectionEvent_DomainObjectSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent []";
      }
    });
  }

  public PropertiesPanelNotifier createInstance(final ContextManager contextManager) {
    final Event<RefreshFormPropertiesEvent> _refreshFormPropertiesEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshFormPropertiesEvent.class }, new Annotation[] { });
    final SessionManager _sessionManager_1 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final PropertiesPanelNotifier instance = new PropertiesPanelNotifier(_refreshFormPropertiesEvent_0, _sessionManager_1);
    registerDependentScopedReference(instance, _refreshFormPropertiesEvent_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier ([javax.enterprise.event.Event, org.kie.workbench.common.stunner.core.client.api.SessionManager])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PropertiesPanelNotifier> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void PropertiesPanelNotifier_onCanvasSelectionEvent_CanvasSelectionEvent(PropertiesPanelNotifier instance, CanvasSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier::onCanvasSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasSelectionEvent;)(a0);
  }-*/;

  public native static void PropertiesPanelNotifier_onDomainObjectSelectionEvent_DomainObjectSelectionEvent(PropertiesPanelNotifier instance, DomainObjectSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier::onDomainObjectSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/DomainObjectSelectionEvent;)(a0);
  }-*/;
}