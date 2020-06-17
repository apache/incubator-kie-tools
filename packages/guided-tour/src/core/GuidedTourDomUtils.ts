/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export class GuidedTourDomUtils {
  private elementId = "kgt-parent";

  private guidedTourElement?: HTMLElement;

  public getGuidedTourHTMLElement() {
    this.guidedTourElement = this.guidedTourElement || this.findGuidedTourElement();
    return this.guidedTourElement;
  }

  public removeGuidedTourHTMLElement() {
    const guidedTourElement = this.getGuidedTourHTMLElement();
    guidedTourElement?.parentElement?.removeChild(guidedTourElement);
  }

  private findGuidedTourElement() {
    const idSelector = "#" + this.elementId;
    const existingElement = document.getElementById(idSelector);
    return existingElement || this.createGuidedTourElement();
  }

  private createGuidedTourElement() {
    const div = this.createDivHTMLElement();
    div.id = this.elementId;
    div.setAttribute("style", "z-index: 999999; position: fixed");
    return div;
  }

  private createDivHTMLElement() {
    const div = document.createElement("div");
    return document.body.appendChild(div);
  }
}
