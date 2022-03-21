/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client.dom;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/GlobalEventHandlers">Web API</a>
 */
@JsType(isNative = true)
@Deprecated
public interface GlobalEventHandlers {
  @JsProperty EventListener<?> getOnabort();
  @JsProperty void setOnabort(EventListener<?> onabort);

  @JsProperty EventListener<FocusEvent> getOnblur();
  @JsProperty void setOnblur(EventListener<FocusEvent> onblur);

  @JsProperty EventListener<Event> getOncancel();
  @JsProperty void setOncancel(EventListener<Event> oncancel);

  @JsProperty EventListener<Event> getOncanplay();
  @JsProperty void setOncanplay(EventListener<Event> oncanplay);

  @JsProperty EventListener<Event> getOncanplaythrough();
  @JsProperty void setOncanplaythrough(EventListener<Event> oncanplaythrough);

  @JsProperty EventListener<Event> getOnchange();
  @JsProperty void setOnchange(EventListener<Event> onchange);

  @JsProperty EventListener<MouseEvent> getOnclick();
  @JsProperty void setOnclick(EventListener<MouseEvent> onclick);

  @JsProperty EventListener<Event> getOnclose();
  @JsProperty void setOnclose(EventListener<Event> onclose);

  @JsProperty EventListener<MouseEvent> getOncontextmenu();
  @JsProperty void setOncontextmenu(EventListener<MouseEvent> oncontextmenu);

  @JsProperty EventListener<Event> getOncuechange();
  @JsProperty void setOncuechange(EventListener<Event> oncuechange);

  @JsProperty EventListener<MouseEvent> getOndblclick();
  @JsProperty void setOndblclick(EventListener<MouseEvent> ondblclick);

  @JsProperty EventListener<DragEvent> getOndrag();
  @JsProperty void setOndrag(EventListener<DragEvent> ondrag);

  @JsProperty EventListener<DragEvent> getOndragend();
  @JsProperty void setOndragend(EventListener<DragEvent> ondragend);

  @JsProperty EventListener<DragEvent> getOndragenter();
  @JsProperty void setOndragenter(EventListener<DragEvent> ondragenter);

  @JsProperty EventListener<DragEvent> getOndragexit();
  @JsProperty void setOndragexit(EventListener<DragEvent> ondragexit);

  @JsProperty EventListener<DragEvent> getOndragleave();
  @JsProperty void setOndragleave(EventListener<DragEvent> ondragleave);

  @JsProperty EventListener<DragEvent> getOndragover();
  @JsProperty void setOndragover(EventListener<DragEvent> ondragover);

  @JsProperty EventListener<DragEvent> getOndragstart();
  @JsProperty void setOndragstart(EventListener<DragEvent> ondragstart);

  @JsProperty EventListener<DragEvent> getOndrop();
  @JsProperty void setOndrop(EventListener<DragEvent> ondrop);

  @JsProperty EventListener<Event> getOndurationchange();
  @JsProperty void setOndurationchange(EventListener<Event> ondurationchange);

  @JsProperty EventListener<Event> getOnemptied();
  @JsProperty void setOnemptied(EventListener<Event> onemptied);

  @JsProperty EventListener<Event> getOnended();
  @JsProperty void setOnended(EventListener<Event> onended);

  @JsProperty EventListener<?> getOnerror();
  @JsProperty void setOnerror(EventListener<?> onerror);

  @JsProperty EventListener<FocusEvent> getOnfocus();
  @JsProperty void setOnfocus(EventListener<FocusEvent> onfocus);

  @JsProperty EventListener<Event> getOninput();
  @JsProperty void setOninput(EventListener<Event> oninput);

  @JsProperty EventListener<Event> getOninvalid();
  @JsProperty void setOninvalid(EventListener<Event> oninvalid);

  @JsProperty EventListener<KeyboardEvent> getOnkeydown();
  @JsProperty void setOnkeydown(EventListener<KeyboardEvent> onkeydown);

  @JsProperty EventListener<KeyboardEvent> getOnkeypress();
  @JsProperty void setOnkeypress(EventListener<KeyboardEvent> onkeypress);

  @JsProperty EventListener<KeyboardEvent> getOnkeyup();
  @JsProperty void setOnkeyup(EventListener<KeyboardEvent> onkeyup);

  @JsProperty EventListener<UIEvent> getOnload();
  @JsProperty void setOnload(EventListener<UIEvent> onload);

  @JsProperty EventListener<Event> getOnloadeddata();
  @JsProperty void setOnloadeddata(EventListener<Event> onloadeddata);

  @JsProperty EventListener<Event> getOnloadedmetadata();
  @JsProperty void setOnloadedmetadata(EventListener<Event> onloadedmetadata);

  @JsProperty EventListener<ProgressEvent> getOnloadstart();
  @JsProperty void setOnloadstart(EventListener<ProgressEvent> onloadstart);

  @JsProperty EventListener<MouseEvent> getOnmousedown();
  @JsProperty void setOnmousedown(EventListener<MouseEvent> onmousedown);

  @JsProperty EventListener<MouseEvent> getOnmousemove();
  @JsProperty void setOnmousemove(EventListener<MouseEvent> onmousemove);

  @JsProperty EventListener<MouseEvent> getOnmouseout();
  @JsProperty void setOnmouseout(EventListener<MouseEvent> onmouseout);

  @JsProperty EventListener<MouseEvent> getOnmouseover();
  @JsProperty void setOnmouseover(EventListener<MouseEvent> onmouseover);

  @JsProperty EventListener<MouseEvent> getOnmouseup();
  @JsProperty void setOnmouseup(EventListener<MouseEvent> onmouseup);

  @JsProperty EventListener<Event> getOnmousewheel();
  @JsProperty void setOnmousewheel(EventListener<Event> onmousewheel);

  @JsProperty EventListener<Event> getOnpause();
  @JsProperty void setOnpause(EventListener<Event> onpause);

  @JsProperty EventListener<Event> getOnplay();
  @JsProperty void setOnplay(EventListener<Event> onplay);

  @JsProperty EventListener<Event> getOnplaying();
  @JsProperty void setOnplaying(EventListener<Event> onplaying);

  @JsProperty EventListener<?> getOnpointerdown();
  @JsProperty void setOnpointerdown(EventListener<?> onpointerdown);

  @JsProperty EventListener<?> getOnpointermove();
  @JsProperty void setOnpointermove(EventListener<?> onpointermove);

  @JsProperty EventListener<?> getOnpointerup();
  @JsProperty void setOnpointerup(EventListener<?> onpointerup);

  @JsProperty EventListener<?> getOnpointercancel();
  @JsProperty void setOnpointercancel(EventListener<?> onpointercancel);

  @JsProperty EventListener<?> getOnpointerover();
  @JsProperty void setOnpointerover(EventListener<?> onpointerover);

  @JsProperty EventListener<?> getOnpointerout();
  @JsProperty void setOnpointerout(EventListener<?> onpointerout);

  @JsProperty EventListener<?> getOnpointerenter();
  @JsProperty void setOnpointerenter(EventListener<?> onpointerenter);

  @JsProperty EventListener<?> getOnpointerleave();
  @JsProperty void setOnpointerleave(EventListener<?> onpointerleave);

  @JsProperty EventListener<?> getOnpointerlockchange();
  @JsProperty void setOnpointerlockchange(EventListener<?> onpointerlockchange);

  @JsProperty EventListener<?> getOnpointerlockerror();
  @JsProperty void setOnpointerlockerror(EventListener<?> onpointerlockerror);

  @JsProperty EventListener<ProgressEvent> getOnprogress();
  @JsProperty void setOnprogress(EventListener<ProgressEvent> onprogress);

  @JsProperty EventListener<Event> getOnratechange();
  @JsProperty void setOnratechange(EventListener<Event> onratechange);

  @JsProperty EventListener<Event> getOnreadystatechange();
  @JsProperty void setOnreadystatechange(EventListener<Event> onreadystatechange);

  @JsProperty EventListener<Event> getOnreset();
  @JsProperty void setOnreset(EventListener<Event> onreset);

  @JsProperty EventListener<UIEvent> getOnscroll();
  @JsProperty void setOnscroll(EventListener<UIEvent> onscroll);

  @JsProperty EventListener<Event> getOnseeked();
  @JsProperty void setOnseeked(EventListener<Event> onseeked);

  @JsProperty EventListener<Event> getOnseeking();
  @JsProperty void setOnseeking(EventListener<Event> onseeking);

  @JsProperty EventListener<?> getOnselect();
  @JsProperty void setOnselect(EventListener<?> onselect);

  @JsProperty EventListener<?> getOnselectionchange();
  @JsProperty void setOnselectionchange(EventListener<?> onselectionchange);

  @JsProperty EventListener<Event> getOnshow();
  @JsProperty void setOnshow(EventListener<Event> onshow);

  @JsProperty EventListener<Event> getOnstalled();
  @JsProperty void setOnstalled(EventListener<Event> onstalled);

  @JsProperty EventListener<Event> getOnsubmit();
  @JsProperty void setOnsubmit(EventListener<Event> onsubmit);

  @JsProperty EventListener<Event> getOnsuspend();
  @JsProperty void setOnsuspend(EventListener<Event> onsuspend);

  @JsProperty EventListener<Event> getOntimeupdate();
  @JsProperty void setOntimeupdate(EventListener<Event> ontimeupdate);

  @JsProperty EventListener<Event> getOnvolumechange();
  @JsProperty void setOnvolumechange(EventListener<Event> onvolumechange);

  @JsProperty EventListener<TouchEvent> getOntouchcancel();
  @JsProperty void setOntouchcancel(EventListener<TouchEvent> ontouchcancel);

  @JsProperty EventListener<TouchEvent> getOntouchend();
  @JsProperty void setOntouchend(EventListener<TouchEvent> ontouchend);

  @JsProperty EventListener<TouchEvent> getOntouchmove();
  @JsProperty void setOntouchmove(EventListener<TouchEvent> ontouchmove);

  @JsProperty EventListener<TouchEvent> getOntouchenter();
  @JsProperty void setOntouchenter(EventListener<TouchEvent> ontouchenter);

  @JsProperty EventListener<TouchEvent> getOntouchstart();
  @JsProperty void setOntouchstart(EventListener<TouchEvent> ontouchstart);

  @JsProperty EventListener<TouchEvent> getOntouchleave();
  @JsProperty void setOntouchleave(EventListener<TouchEvent> ontouchleave);

  @JsProperty EventListener<TouchEvent> getOnwaiting();
  @JsProperty void setOnwaiting(EventListener<TouchEvent> onwaiting);

  @JsProperty EventListener<UIEvent> getOnresize();
  @JsProperty void setOnresize(EventListener<UIEvent> onresize);
}
