package org.jboss.errai.ioc.client;

import com.google.gwt.view.client.HasData;
import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.guvnor.messageconsole.client.console.MessageConsoleService;
import org.guvnor.messageconsole.events.FilteredMessagesEvent;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.PublishMessagesEvent;
import org.guvnor.messageconsole.events.UnpublishMessagesEvent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.rpc.SessionInfo;

public class Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_Default extends Factory<MessageConsoleService> { private class Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends MessageConsoleService implements Proxy<MessageConsoleService> {
    private final ProxyHelper<MessageConsoleService> proxyHelper = new ProxyHelperImpl<MessageConsoleService>("Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final MessageConsoleService instance) {

    }

    public MessageConsoleService asBeanType() {
      return this;
    }

    public void setInstance(final MessageConsoleService instance) {
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

    @Override public void publishMessages(PublishMessagesEvent publishEvent) {
      if (proxyHelper != null) {
        final MessageConsoleService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.publishMessages(publishEvent);
      } else {
        super.publishMessages(publishEvent);
      }
    }

    @Override public void unpublishMessages(UnpublishMessagesEvent unpublishEvent) {
      if (proxyHelper != null) {
        final MessageConsoleService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unpublishMessages(unpublishEvent);
      } else {
        super.unpublishMessages(unpublishEvent);
      }
    }

    @Override public void publishBatchMessages(PublishBatchMessagesEvent publishBatchEvent) {
      if (proxyHelper != null) {
        final MessageConsoleService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.publishBatchMessages(publishBatchEvent);
      } else {
        super.publishBatchMessages(publishBatchEvent);
      }
    }

    @Override public void addDataDisplay(HasData display) {
      if (proxyHelper != null) {
        final MessageConsoleService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addDataDisplay(display);
      } else {
        super.addDataDisplay(display);
      }
    }

    @Override public void onPerspectiveChange(PerspectiveChange perspectiveChange) {
      if (proxyHelper != null) {
        final MessageConsoleService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPerspectiveChange(perspectiveChange);
      } else {
        super.onPerspectiveChange(perspectiveChange);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final MessageConsoleService proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MessageConsoleService.class, "Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MessageConsoleService.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.guvnor.messageconsole.events.PublishMessagesEvent", new AbstractCDIEventCallback<PublishMessagesEvent>() {
      public void fireEvent(final PublishMessagesEvent event) {
        final MessageConsoleService instance = Factory.maybeUnwrapProxy((MessageConsoleService) context.getInstance("Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_Default"));
        instance.publishMessages(event);
      }
      public String toString() {
        return "Observer: org.guvnor.messageconsole.events.PublishMessagesEvent []";
      }
    });
    CDI.subscribeLocal("org.guvnor.messageconsole.events.UnpublishMessagesEvent", new AbstractCDIEventCallback<UnpublishMessagesEvent>() {
      public void fireEvent(final UnpublishMessagesEvent event) {
        final MessageConsoleService instance = Factory.maybeUnwrapProxy((MessageConsoleService) context.getInstance("Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_Default"));
        instance.unpublishMessages(event);
      }
      public String toString() {
        return "Observer: org.guvnor.messageconsole.events.UnpublishMessagesEvent []";
      }
    });
    CDI.subscribeLocal("org.guvnor.messageconsole.events.PublishBatchMessagesEvent", new AbstractCDIEventCallback<PublishBatchMessagesEvent>() {
      public void fireEvent(final PublishBatchMessagesEvent event) {
        final MessageConsoleService instance = Factory.maybeUnwrapProxy((MessageConsoleService) context.getInstance("Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_Default"));
        instance.publishBatchMessages(event);
      }
      public String toString() {
        return "Observer: org.guvnor.messageconsole.events.PublishBatchMessagesEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PerspectiveChange", new AbstractCDIEventCallback<PerspectiveChange>() {
      public void fireEvent(final PerspectiveChange event) {
        final MessageConsoleService instance = Factory.maybeUnwrapProxy((MessageConsoleService) context.getInstance("Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_Default"));
        instance.onPerspectiveChange(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PerspectiveChange []";
      }
    });
  }

  public MessageConsoleService createInstance(final ContextManager contextManager) {
    final User _identity_3 = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    final SessionInfo _sessionInfo_2 = (SessionInfo) contextManager.getInstance("Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default");
    final SyncBeanManager _iocManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<FilteredMessagesEvent> _filteredMessagesEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { FilteredMessagesEvent.class }, new Annotation[] { });
    final PlaceManager _placeManager_1 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final MessageConsoleService instance = new MessageConsoleService(_iocManager_0, _placeManager_1, _sessionInfo_2, _identity_3, _filteredMessagesEvent_4);
    registerDependentScopedReference(instance, _identity_3);
    registerDependentScopedReference(instance, _iocManager_0);
    registerDependentScopedReference(instance, _filteredMessagesEvent_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<MessageConsoleService> proxyImpl = new Type_factory__o_g_m_c_c_MessageConsoleService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}