public SVGShapeViewResource ${name}() {
    return new SVGShapeViewResource(args -> {
                                        if (null != args.width && null != args.heigth) {
                                            return this.${name}View(args.width, args.heigth, args.resizable);
                                        } else {
                                            return this.${name}View(args.resizable);
                                        }});
}

private SVGShapeView ${name}View(final boolean resizable) {
    return this.${name}View(${width}d, ${height}d, resizable);
}

private SVGShapeView ${name}View(final double width, final double height, final boolean resizable) {

    SVGPrimitiveShape mainShape = SVGPrimitiveFactory.newSVGPrimitiveShape(${mainShape}, true, ${layout}, SVGPrimitivePolicies.Builder.build(${policy}));

    final SVGShapeViewImpl view = new SVGShapeViewImpl("${viewId}", mainShape, width, height, resizable);

    <#list children as c>
        ${c}
    </#list>

    <#list svgChildren as child>
        ${child}
    </#list>

    ${text}

    ${stateHolders}

    view.refresh();

    return view;
}

private SVGBasicShapeView ${name}BasicView() {
    return this.${name}BasicView(${width}d, ${height}d);
}

private SVGBasicShapeView ${name}BasicView(final double width, final double height) {

    SVGPrimitiveShape mainShape = SVGPrimitiveFactory.newSVGPrimitiveShape(${mainShape}, false, ${layout}, SVGPrimitivePolicies.Builder.build(${policy}));

    final SVGBasicShapeViewImpl view = new SVGBasicShapeViewImpl("${viewId}", mainShape, width, height);

    <#list children as c>
        ${c}
    </#list>

    <#list svgChildren as child>
        ${child}
    </#list>

    return view;
}
