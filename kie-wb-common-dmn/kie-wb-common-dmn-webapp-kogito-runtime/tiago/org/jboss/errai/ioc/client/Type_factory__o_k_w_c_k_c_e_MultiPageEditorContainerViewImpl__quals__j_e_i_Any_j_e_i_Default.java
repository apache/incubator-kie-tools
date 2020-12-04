package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorViewImpl;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;

public class Type_factory__o_k_w_c_k_c_e_MultiPageEditorContainerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiPageEditorContainerViewImpl> { public Type_factory__o_k_w_c_k_c_e_MultiPageEditorContainerViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultiPageEditorContainerViewImpl.class, "Type_factory__o_k_w_c_k_c_e_MultiPageEditorContainerViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultiPageEditorContainerViewImpl.class, MultiPageEditorImpl.class, Object.class, MultiPageEditor.class, IsWidget.class, MultiPageEditorContainerView.class, UberView.class, HasPresenter.class });
  }

  public MultiPageEditorContainerViewImpl createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final MultiPageEditorContainerViewImpl instance = new MultiPageEditorContainerViewImpl(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    final Event MultiPageEditorImpl_selectedPageEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { MultiPageEditorSelectedPageEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, MultiPageEditorImpl_selectedPageEvent);
    MultiPageEditorImpl_Event_selectedPageEvent(instance, MultiPageEditorImpl_selectedPageEvent);
    final MultiPageEditorViewImpl MultiPageEditorImpl_view = (MultiPageEditorViewImpl) contextManager.getInstance("Type_factory__o_u_c_v_p_m_MultiPageEditorViewImpl__quals__j_e_i_Any_o_u_c_w_w_m_Multiple");
    registerDependentScopedReference(instance, MultiPageEditorImpl_view);
    MultiPageEditorImpl_MultiPageEditorViewImpl_view(instance, MultiPageEditorImpl_view);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final MultiPageEditorContainerViewImpl instance) {
    instance.init();
  }

  native static Event MultiPageEditorImpl_Event_selectedPageEvent(MultiPageEditorImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl::selectedPageEvent;
  }-*/;

  native static void MultiPageEditorImpl_Event_selectedPageEvent(MultiPageEditorImpl instance, Event<MultiPageEditorSelectedPageEvent> value) /*-{
    instance.@org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl::selectedPageEvent = value;
  }-*/;

  native static MultiPageEditorViewImpl MultiPageEditorImpl_MultiPageEditorViewImpl_view(MultiPageEditorImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl::view;
  }-*/;

  native static void MultiPageEditorImpl_MultiPageEditorViewImpl_view(MultiPageEditorImpl instance, MultiPageEditorViewImpl value) /*-{
    instance.@org.uberfire.client.views.pfly.multipage.MultiPageEditorImpl::view = value;
  }-*/;
}