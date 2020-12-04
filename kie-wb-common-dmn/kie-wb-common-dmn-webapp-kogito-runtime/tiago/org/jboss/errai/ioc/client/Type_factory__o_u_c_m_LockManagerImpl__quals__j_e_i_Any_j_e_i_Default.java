package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.mvp.LockDemandDetector;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.LockManagerImpl;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.client.mvp.RenameInProgressEvent;
import org.uberfire.client.mvp.SaveInProgressEvent;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.client.workbench.VFSLockServiceProxyClientImpl;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

public class Type_factory__o_u_c_m_LockManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LockManagerImpl> { public Type_factory__o_u_c_m_LockManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LockManagerImpl.class, "Type_factory__o_u_c_m_LockManagerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LockManagerImpl.class, Object.class, LockManager.class });
  }

  public LockManagerImpl createInstance(final ContextManager contextManager) {
    final LockManagerImpl instance = new LockManagerImpl();
    setIncompleteInstance(instance);
    final LockDemandDetector LockManagerImpl_lockDemandDetector = (LockDemandDetector) contextManager.getInstance("Type_factory__o_u_c_m_LockDemandDetector__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, LockManagerImpl_lockDemandDetector);
    LockManagerImpl_LockDemandDetector_lockDemandDetector(instance, LockManagerImpl_lockDemandDetector);
    final VFSLockServiceProxyClientImpl LockManagerImpl_lockService = (VFSLockServiceProxyClientImpl) contextManager.getInstance("Type_factory__o_u_c_w_VFSLockServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, LockManagerImpl_lockService);
    LockManagerImpl_VFSLockServiceProxy_lockService(instance, LockManagerImpl_lockService);
    final Event LockManagerImpl_changeTitleEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ChangeTitleWidgetEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, LockManagerImpl_changeTitleEvent);
    LockManagerImpl_Event_changeTitleEvent(instance, LockManagerImpl_changeTitleEvent);
    final Event LockManagerImpl_updatedLockStatusEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { UpdatedLockStatusEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, LockManagerImpl_updatedLockStatusEvent);
    LockManagerImpl_Event_updatedLockStatusEvent(instance, LockManagerImpl_updatedLockStatusEvent);
    final User LockManagerImpl_user = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, LockManagerImpl_user);
    LockManagerImpl_User_user(instance, LockManagerImpl_user);
    final Event LockManagerImpl_lockNotification = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, LockManagerImpl_lockNotification);
    LockManagerImpl_Event_lockNotification(instance, LockManagerImpl_lockNotification);
    thisInstance.setReference(instance, "updateLockInfoSubscription", CDI.subscribeLocal("org.uberfire.backend.vfs.impl.LockInfo", new AbstractCDIEventCallback<LockInfo>() {
      public void fireEvent(final LockInfo event) {
        LockManagerImpl_updateLockInfo_LockInfo(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.backend.vfs.impl.LockInfo []";
      }
    }));
    thisInstance.setReference(instance, "onResourceAddedSubscription", CDI.subscribeLocal("org.uberfire.workbench.events.ResourceAddedEvent", new AbstractCDIEventCallback<ResourceAddedEvent>() {
      public void fireEvent(final ResourceAddedEvent event) {
        LockManagerImpl_onResourceAdded_ResourceAddedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.workbench.events.ResourceAddedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onResourceUpdatedSubscription", CDI.subscribeLocal("org.uberfire.workbench.events.ResourceUpdatedEvent", new AbstractCDIEventCallback<ResourceUpdatedEvent>() {
      public void fireEvent(final ResourceUpdatedEvent event) {
        LockManagerImpl_onResourceUpdated_ResourceUpdatedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.workbench.events.ResourceUpdatedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onSaveInProgressSubscription", CDI.subscribeLocal("org.uberfire.client.mvp.SaveInProgressEvent", new AbstractCDIEventCallback<SaveInProgressEvent>() {
      public void fireEvent(final SaveInProgressEvent event) {
        LockManagerImpl_onSaveInProgress_SaveInProgressEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.mvp.SaveInProgressEvent []";
      }
    }));
    thisInstance.setReference(instance, "onRenameInProgressSubscription", CDI.subscribeLocal("org.uberfire.client.mvp.RenameInProgressEvent", new AbstractCDIEventCallback<RenameInProgressEvent>() {
      public void fireEvent(final RenameInProgressEvent event) {
        LockManagerImpl_onRenameInProgress_RenameInProgressEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.mvp.RenameInProgressEvent []";
      }
    }));
    thisInstance.setReference(instance, "onLockRequiredSubscription", CDI.subscribeLocal("org.uberfire.client.mvp.LockRequiredEvent", new AbstractCDIEventCallback<LockRequiredEvent>() {
      public void fireEvent(final LockRequiredEvent event) {
        LockManagerImpl_onLockRequired_LockRequiredEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.mvp.LockRequiredEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LockManagerImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final LockManagerImpl instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "updateLockInfoSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onResourceAddedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onResourceUpdatedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onSaveInProgressSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onRenameInProgressSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onLockRequiredSubscription", Subscription.class)).remove();
  }

  native static VFSLockServiceProxy LockManagerImpl_VFSLockServiceProxy_lockService(LockManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.LockManagerImpl::lockService;
  }-*/;

  native static void LockManagerImpl_VFSLockServiceProxy_lockService(LockManagerImpl instance, VFSLockServiceProxy value) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::lockService = value;
  }-*/;

  native static Event LockManagerImpl_Event_changeTitleEvent(LockManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.LockManagerImpl::changeTitleEvent;
  }-*/;

  native static void LockManagerImpl_Event_changeTitleEvent(LockManagerImpl instance, Event<ChangeTitleWidgetEvent> value) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::changeTitleEvent = value;
  }-*/;

  native static Event LockManagerImpl_Event_updatedLockStatusEvent(LockManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.LockManagerImpl::updatedLockStatusEvent;
  }-*/;

  native static void LockManagerImpl_Event_updatedLockStatusEvent(LockManagerImpl instance, Event<UpdatedLockStatusEvent> value) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::updatedLockStatusEvent = value;
  }-*/;

  native static User LockManagerImpl_User_user(LockManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.LockManagerImpl::user;
  }-*/;

  native static void LockManagerImpl_User_user(LockManagerImpl instance, User value) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::user = value;
  }-*/;

  native static LockDemandDetector LockManagerImpl_LockDemandDetector_lockDemandDetector(LockManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.LockManagerImpl::lockDemandDetector;
  }-*/;

  native static void LockManagerImpl_LockDemandDetector_lockDemandDetector(LockManagerImpl instance, LockDemandDetector value) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::lockDemandDetector = value;
  }-*/;

  native static Event LockManagerImpl_Event_lockNotification(LockManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.LockManagerImpl::lockNotification;
  }-*/;

  native static void LockManagerImpl_Event_lockNotification(LockManagerImpl instance, Event<NotificationEvent> value) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::lockNotification = value;
  }-*/;

  public native static void LockManagerImpl_onResourceUpdated_ResourceUpdatedEvent(LockManagerImpl instance, ResourceUpdatedEvent a0) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::onResourceUpdated(Lorg/uberfire/workbench/events/ResourceUpdatedEvent;)(a0);
  }-*/;

  public native static void LockManagerImpl_onRenameInProgress_RenameInProgressEvent(LockManagerImpl instance, RenameInProgressEvent a0) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::onRenameInProgress(Lorg/uberfire/client/mvp/RenameInProgressEvent;)(a0);
  }-*/;

  public native static void LockManagerImpl_onResourceAdded_ResourceAddedEvent(LockManagerImpl instance, ResourceAddedEvent a0) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::onResourceAdded(Lorg/uberfire/workbench/events/ResourceAddedEvent;)(a0);
  }-*/;

  public native static void LockManagerImpl_onLockRequired_LockRequiredEvent(LockManagerImpl instance, LockRequiredEvent a0) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::onLockRequired(Lorg/uberfire/client/mvp/LockRequiredEvent;)(a0);
  }-*/;

  public native static void LockManagerImpl_updateLockInfo_LockInfo(LockManagerImpl instance, LockInfo a0) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::updateLockInfo(Lorg/uberfire/backend/vfs/impl/LockInfo;)(a0);
  }-*/;

  public native static void LockManagerImpl_onSaveInProgress_SaveInProgressEvent(LockManagerImpl instance, SaveInProgressEvent a0) /*-{
    instance.@org.uberfire.client.mvp.LockManagerImpl::onSaveInProgress(Lorg/uberfire/client/mvp/SaveInProgressEvent;)(a0);
  }-*/;
}