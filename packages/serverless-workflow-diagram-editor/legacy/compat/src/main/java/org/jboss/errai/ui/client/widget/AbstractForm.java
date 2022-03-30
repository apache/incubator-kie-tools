/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ui.client.widget;

import javax.annotation.PostConstruct;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLIFrameElement;
import org.gwtproject.dom.style.shared.Display;
import org.gwtproject.user.client.ui.Composite;

/**
 * Base class for {@link Template Templated} widgets that require native form
 * support for asynchronous {@code GET} or {@code POST} requests.
 * 
 * <p>
 * Calls to {@link AbstractForm#submit()} trigger a form submission that is
 * null-routed and targets a hidden iframe.
 * 
 * <p>
 * The typical usage will be for login forms, where calling
 * {@link AbstractForm#submit()} after a successful login will prompt the
 * browser to remember a user's credentials.
 * 
 * <p>
 * If you use an {@link AbstractForm} and still encounter issues getting proper
 * browser form support:
 * 
 * <ul>
 * <li>make sure that your text-fields are actual form inputs
 * 
 * <li>make sure the UI "submit" button triggers a {@link ClickHandler} that
 * calls {@link AbstractForm}{@link #submit()}
 * 
 * <li>make sure the UI "submit" button does not directly trigger submission
 * (i.e. it should <b>not</b> have {@code type="submit"}
 * 
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class AbstractForm extends Composite {

  public static final String DEFAULT_FORM_ACTION = "0.0.0.0";
  public static final String ERRAI_FORM_FRAME_ID = "ERRAI-FORM-FRAME";

  private HTMLIFrameElement iFrame;

  @PostConstruct
  public void setupIFrame() {
    iFrame = getOrMakeIFrame();

    iFrame.style.display = Display.NONE.getCssName();
    final String uniqueId = ERRAI_FORM_FRAME_ID;
    iFrame.id = (uniqueId);
    iFrame.name = (uniqueId);

    DomGlobal.document.body.appendChild(iFrame);
  }

  private void prepareFormForSubmission(final HTMLFormElement form) {
    form.method = ("post");
    form.target= (iFrame.name);
    form.action = (getFormAction());
  }

  /**
   * Subclasses may override this method if they want the form to submit to a
   * different url than {@link AbstractForm#DEFAULT_FORM_ACTION}.
   * 
   * @return The value that will be set to the
   *         {@link AbstractForm#getFormElement()} {@code action} attribute
   *         before submission.
   */
  protected String getFormAction() {
    return DEFAULT_FORM_ACTION;
  }

  private HTMLIFrameElement getOrMakeIFrame() {
    HTMLIFrameElement iFrame = null;
    try {
      iFrame = (HTMLIFrameElement) DomGlobal.document.getElementById(ERRAI_FORM_FRAME_ID);
    }
    catch (Exception e) {
    }

    if (iFrame == null)
      iFrame = (HTMLIFrameElement) DomGlobal.document.createElement("iframe");

    return iFrame;
  }

  /**
   * Submit the form returned from {@link AbstractForm#getFormElement()}. Before
   * the form is submitted, it will be modifed so that it targets a hidden
   * iframe with the id {@link AbstractForm#ERRAI_FORM_FRAME_ID} with the
   * {@code action} set to the return value of
   * {@link AbstractForm#getFormAction()}.
   */
  public void submit() {
    final HTMLFormElement form = getFormElement();
    prepareFormForSubmission(form);
    form.submit();
  }

  /**
   * @return The form element that will be submitted when
   *         {@link AbstractForm#submit()} is called.
   */
  protected abstract HTMLFormElement getFormElement();

}
