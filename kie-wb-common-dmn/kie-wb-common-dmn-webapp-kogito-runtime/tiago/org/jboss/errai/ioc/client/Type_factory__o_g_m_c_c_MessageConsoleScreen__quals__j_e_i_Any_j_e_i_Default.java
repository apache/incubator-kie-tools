package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.messageconsole.client.console.MessageConsoleScreen;
import org.guvnor.messageconsole.client.console.MessageConsoleViewImpl;
import org.guvnor.messageconsole.events.FilteredMessagesEvent;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder.SupportsRefresh;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.Position;

public class Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<MessageConsoleScreen> { private class Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends MessageConsoleScreen implements Proxy<MessageConsoleScreen> {
    private final ProxyHelper<MessageConsoleScreen> proxyHelper = new ProxyHelperImpl<MessageConsoleScreen>("Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final MessageConsoleScreen instance) {

    }

    public MessageConsoleScreen asBeanType() {
      return this;
    }

    public void setInstance(final MessageConsoleScreen instance) {
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

    @Override public void onFilteredMessagesEvent(FilteredMessagesEvent filteredMessagesEvent) {
      if (proxyHelper != null) {
        final MessageConsoleScreen proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onFilteredMessagesEvent(filteredMessagesEvent);
      } else {
        super.onFilteredMessagesEvent(filteredMessagesEvent);
      }
    }

    @Override public void onRefresh() {
      if (proxyHelper != null) {
        final MessageConsoleScreen proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onRefresh();
      } else {
        super.onRefresh();
      }
    }

    @Override public void copyMessages() {
      if (proxyHelper != null) {
        final MessageConsoleScreen proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.copyMessages();
      } else {
        super.copyMessages();
      }
    }

    @Override public void selectedProjectChanged(WorkspaceProjectContextChangeEvent event) {
      if (proxyHelper != null) {
        final MessageConsoleScreen proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.selectedProjectChanged(event);
      } else {
        super.selectedProjectChanged(event);
      }
    }

    @Override public Position getDefaultPosition() {
      if (proxyHelper != null) {
        final MessageConsoleScreen proxiedInstance = proxyHelper.getInstance(this);
        final Position retVal = proxiedInstance.getDefaultPosition();
        return retVal;
      } else {
        return super.getDefaultPosition();
      }
    }

    @Override public String getTitle() {
      if (proxyHelper != null) {
        final MessageConsoleScreen proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle();
        return retVal;
      } else {
        return super.getTitle();
      }
    }

    @Override public Widget asWidget() {
      if (proxyHelper != null) {
        final MessageConsoleScreen proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = proxiedInstance.asWidget();
        return retVal;
      } else {
        return super.asWidget();
      }
    }

    @Override public void getMenus(Consumer menusConsumer) {
      if (proxyHelper != null) {
        final MessageConsoleScreen proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.getMenus(menusConsumer);
      } else {
        super.getMenus(menusConsumer);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final MessageConsoleScreen proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MessageConsoleScreen.class, "Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MessageConsoleScreen.class, Object.class, SupportsRefresh.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.guvnor.messageconsole.events.FilteredMessagesEvent", new AbstractCDIEventCallback<FilteredMessagesEvent>() {
      public void fireEvent(final FilteredMessagesEvent event) {
        final MessageConsoleScreen instance = Factory.maybeUnwrapProxy((MessageConsoleScreen) context.getInstance("Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_Default"));
        instance.onFilteredMessagesEvent(event);
      }
      public String toString() {
        return "Observer: org.guvnor.messageconsole.events.FilteredMessagesEvent []";
      }
    });
    CDI.subscribeLocal("org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent", new AbstractCDIEventCallback<WorkspaceProjectContextChangeEvent>() {
      public void fireEvent(final WorkspaceProjectContextChangeEvent event) {
        final MessageConsoleScreen instance = Factory.maybeUnwrapProxy((MessageConsoleScreen) context.getInstance("Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_Default"));
        instance.selectedProjectChanged(event);
      }
      public String toString() {
        return "Observer: org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent []";
      }
    });
  }

  public MessageConsoleScreen createInstance(final ContextManager contextManager) {
    final Caller<BuildService> _buildService_0 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { BuildService.class }, new Annotation[] { });
    final Event<PublishBatchMessagesEvent> _publishBatchMessagesEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PublishBatchMessagesEvent.class }, new Annotation[] { });
    final MessageConsoleViewImpl _view_2 = (MessageConsoleViewImpl) contextManager.getInstance("Type_factory__o_g_m_c_c_MessageConsoleViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<NotificationEvent> _workbenchNotification_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    final MessageConsoleScreen instance = new MessageConsoleScreen(_buildService_0, _publishBatchMessagesEvent_1, _view_2, _workbenchNotification_3);
    registerDependentScopedReference(instance, _buildService_0);
    registerDependentScopedReference(instance, _publishBatchMessagesEvent_1);
    registerDependentScopedReference(instance, _workbenchNotification_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<MessageConsoleScreen> proxyImpl = new Type_factory__o_g_m_c_c_MessageConsoleScreen__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}