public SVGShapeView ${name}(final boolean resizable) {
    return this.${name}(${width}d, ${height}d, resizable);
}

public SVGShapeView ${name}(final double width, final double height, final boolean resizable) {

    final SVGShapeViewImpl view = new SVGShapeViewImpl("${name}", ${main}, width, height, resizable);
    <#list scalableChildren as sc>
        view.addScalableChild(${sc});
    </#list>

    <#list children as c>
        view.addChild(${c});
    </#list>

    <#list rawChildren as child>
        ${child}
    </#list>

    return view;
}

private SVGBasicShapeView ${name}BasicView() {
    return this.${name}BasicView(${width}d, ${height}d);
}

private SVGBasicShapeView ${name}BasicView(final double width, final double height) {

    final SVGBasicShapeViewImpl view = new SVGBasicShapeViewImpl("${name}", ${main}, width, height);

    <#list children as c>
        view.addChild(${c});
    </#list>

    <#list rawChildren as child>
        ${child}
    </#list>

    return view;
}
