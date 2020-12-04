package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorWorkbenchEditor;

public class Type_factory__o_u_e_w_c_c_e_t_TextEditorWorkbenchEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<TextEditorWorkbenchEditor> { public Type_factory__o_u_e_w_c_c_e_t_TextEditorWorkbenchEditor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TextEditorWorkbenchEditor.class, "Type_factory__o_u_e_w_c_c_e_t_TextEditorWorkbenchEditor__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TextEditorWorkbenchEditor.class, TextEditorPresenter.class, Object.class });
  }

  public TextEditorWorkbenchEditor createInstance(final ContextManager contextManager) {
    final TextEditorWorkbenchEditor instance = new TextEditorWorkbenchEditor();
    setIncompleteInstance(instance);
    final Caller TextEditorPresenter_vfsServices = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { VFSService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, TextEditorPresenter_vfsServices);
    TextEditorPresenter_Caller_vfsServices(instance, TextEditorPresenter_vfsServices);
    final Caller TextEditorWorkbenchEditor_vfsServices = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { VFSService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, TextEditorWorkbenchEditor_vfsServices);
    TextEditorWorkbenchEditor_Caller_vfsServices(instance, TextEditorWorkbenchEditor_vfsServices);
    final TextEditorView TextEditorPresenter_view = (TextEditorView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_e_t_TextEditorView__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, TextEditorPresenter_view);
    instance.view = TextEditorPresenter_view;
    final Event TextEditorWorkbenchEditor_changeTitleWidgetEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ChangeTitleWidgetEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, TextEditorWorkbenchEditor_changeTitleWidgetEvent);
    TextEditorWorkbenchEditor_Event_changeTitleWidgetEvent(instance, TextEditorWorkbenchEditor_changeTitleWidgetEvent);
    setIncompleteInstance(null);
    return instance;
  }

  native static Caller TextEditorPresenter_Caller_vfsServices(TextEditorPresenter instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorPresenter::vfsServices;
  }-*/;

  native static void TextEditorPresenter_Caller_vfsServices(TextEditorPresenter instance, Caller<VFSService> value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorPresenter::vfsServices = value;
  }-*/;

  native static Event TextEditorWorkbenchEditor_Event_changeTitleWidgetEvent(TextEditorWorkbenchEditor instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorWorkbenchEditor::changeTitleWidgetEvent;
  }-*/;

  native static void TextEditorWorkbenchEditor_Event_changeTitleWidgetEvent(TextEditorWorkbenchEditor instance, Event<ChangeTitleWidgetEvent> value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorWorkbenchEditor::changeTitleWidgetEvent = value;
  }-*/;

  native static Caller TextEditorWorkbenchEditor_Caller_vfsServices(TextEditorWorkbenchEditor instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorWorkbenchEditor::vfsServices;
  }-*/;

  native static void TextEditorWorkbenchEditor_Caller_vfsServices(TextEditorWorkbenchEditor instance, Caller<VFSService> value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorWorkbenchEditor::vfsServices = value;
  }-*/;
}