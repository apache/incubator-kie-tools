package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.Element;

@StubClass("elemental2.dom.HTMLElement")
public class HTMLElement extends elemental2.dom.Element {

    public static String[] observedAttributes;
    public String className;
    public String dir;
    public boolean draggable;
    public boolean hidden;
    public String id;
    public String lang;
    public int offsetHeight;
    public int offsetLeft;
    public Element offsetParent;
    public int offsetTop;
    public int offsetWidth;
    public boolean spellcheck;
    public CSSStyleDeclaration style = new CSSStyleDeclaration();
    public int tabIndex;
    public String title;
}