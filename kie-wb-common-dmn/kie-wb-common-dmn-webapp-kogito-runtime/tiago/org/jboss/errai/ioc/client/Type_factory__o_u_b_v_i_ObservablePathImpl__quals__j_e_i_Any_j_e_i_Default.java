package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.backend.vfs.IsVersioned;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

public class Type_factory__o_u_b_v_i_ObservablePathImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ObservablePathImpl> { public Type_factory__o_u_b_v_i_ObservablePathImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ObservablePathImpl.class, "Type_factory__o_u_b_v_i_ObservablePathImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ObservablePathImpl.class, Object.class, ObservablePath.class, Path.class, Comparable.class, Disposable.class, IsVersioned.class });
  }

  public ObservablePathImpl createInstance(final ContextManager contextManager) {
    final ObservablePathImpl instance = new ObservablePathImpl();
    setIncompleteInstance(instance);
    final SessionInfo ObservablePathImpl_sessionInfo = (SessionInfo) contextManager.getInstance("Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default");
    ObservablePathImpl_SessionInfo_sessionInfo(instance, ObservablePathImpl_sessionInfo);
    thisInstance.setReference(instance, "onResourceRenamedSubscription", CDI.subscribeLocal("org.uberfire.workbench.events.ResourceRenamedEvent", new AbstractCDIEventCallback<ResourceRenamedEvent>() {
      public void fireEvent(final ResourceRenamedEvent event) {
        ObservablePathImpl_onResourceRenamed_ResourceRenamedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.workbench.events.ResourceRenamedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onResourceDeletedSubscription", CDI.subscribeLocal("org.uberfire.workbench.events.ResourceDeletedEvent", new AbstractCDIEventCallback<ResourceDeletedEvent>() {
      public void fireEvent(final ResourceDeletedEvent event) {
        ObservablePathImpl_onResourceDeleted_ResourceDeletedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.workbench.events.ResourceDeletedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onResourceUpdatedSubscription", CDI.subscribeLocal("org.uberfire.workbench.events.ResourceUpdatedEvent", new AbstractCDIEventCallback<ResourceUpdatedEvent>() {
      public void fireEvent(final ResourceUpdatedEvent event) {
        ObservablePathImpl_onResourceUpdated_ResourceUpdatedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.workbench.events.ResourceUpdatedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onResourceCopiedSubscription", CDI.subscribeLocal("org.uberfire.workbench.events.ResourceCopiedEvent", new AbstractCDIEventCallback<ResourceCopiedEvent>() {
      public void fireEvent(final ResourceCopiedEvent event) {
        ObservablePathImpl_onResourceCopied_ResourceCopiedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.workbench.events.ResourceCopiedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onResourceBatchEventSubscription", CDI.subscribeLocal("org.uberfire.workbench.events.ResourceBatchChangesEvent", new AbstractCDIEventCallback<ResourceBatchChangesEvent>() {
      public void fireEvent(final ResourceBatchChangesEvent event) {
        ObservablePathImpl_onResourceBatchEvent_ResourceBatchChangesEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.workbench.events.ResourceBatchChangesEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ObservablePathImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ObservablePathImpl instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onResourceRenamedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onResourceDeletedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onResourceUpdatedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onResourceCopiedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onResourceBatchEventSubscription", Subscription.class)).remove();
  }

  native static SessionInfo ObservablePathImpl_SessionInfo_sessionInfo(ObservablePathImpl instance) /*-{
    return instance.@org.uberfire.backend.vfs.impl.ObservablePathImpl::sessionInfo;
  }-*/;

  native static void ObservablePathImpl_SessionInfo_sessionInfo(ObservablePathImpl instance, SessionInfo value) /*-{
    instance.@org.uberfire.backend.vfs.impl.ObservablePathImpl::sessionInfo = value;
  }-*/;

  public native static void ObservablePathImpl_onResourceRenamed_ResourceRenamedEvent(ObservablePathImpl instance, ResourceRenamedEvent a0) /*-{
    instance.@org.uberfire.backend.vfs.impl.ObservablePathImpl::onResourceRenamed(Lorg/uberfire/workbench/events/ResourceRenamedEvent;)(a0);
  }-*/;

  public native static void ObservablePathImpl_onResourceBatchEvent_ResourceBatchChangesEvent(ObservablePathImpl instance, ResourceBatchChangesEvent a0) /*-{
    instance.@org.uberfire.backend.vfs.impl.ObservablePathImpl::onResourceBatchEvent(Lorg/uberfire/workbench/events/ResourceBatchChangesEvent;)(a0);
  }-*/;

  public native static void ObservablePathImpl_onResourceUpdated_ResourceUpdatedEvent(ObservablePathImpl instance, ResourceUpdatedEvent a0) /*-{
    instance.@org.uberfire.backend.vfs.impl.ObservablePathImpl::onResourceUpdated(Lorg/uberfire/workbench/events/ResourceUpdatedEvent;)(a0);
  }-*/;

  public native static void ObservablePathImpl_onResourceCopied_ResourceCopiedEvent(ObservablePathImpl instance, ResourceCopiedEvent a0) /*-{
    instance.@org.uberfire.backend.vfs.impl.ObservablePathImpl::onResourceCopied(Lorg/uberfire/workbench/events/ResourceCopiedEvent;)(a0);
  }-*/;

  public native static void ObservablePathImpl_onResourceDeleted_ResourceDeletedEvent(ObservablePathImpl instance, ResourceDeletedEvent a0) /*-{
    instance.@org.uberfire.backend.vfs.impl.ObservablePathImpl::onResourceDeleted(Lorg/uberfire/workbench/events/ResourceDeletedEvent;)(a0);
  }-*/;
}