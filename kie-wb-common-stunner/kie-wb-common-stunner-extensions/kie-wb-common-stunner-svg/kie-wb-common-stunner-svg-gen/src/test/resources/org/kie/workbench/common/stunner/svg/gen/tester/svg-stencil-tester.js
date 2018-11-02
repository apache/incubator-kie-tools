/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
    The tester HTML template URI.
 */
//const testerHtmlTemplatePath = "https://rawgit.com/kiegroup/kie-wb-common/master/kie-wb-common-stunner/kie-wb-common-stunner-extensions/kie-wb-common-stunner-svg/kie-wb-common-stunner-svg-gen/src/test/resources/org/kie/workbench/common/stunner/svg/gen/tester/svg-shape-tester.html";
const testerHtmlTemplatePath = "/kie-wb-common-stunner/kie-wb-common-stunner-svg-gen/org/kie/workbench/common/stunner/svg/gen/tester/svg-shape-tester.html";
const ICON_SIZE = 32;
var currentTester = undefined;

// Global function for HTML templates.
function updateContent() {
    if (!currentTester) {
        alert("No current SVG Stencil Tester has been found for displaying.")
    }
    currentTester.updateContent();
}

// Some global functions.
function navigateTo(target) {
    currentTester.navigateTo(target);
}

function strReplaceAll(string, str_find, str_replace){
    return strReplace(string, str_find, str_replace, "g");
}

function strReplace(string, str_find, str_replace, flags){
    try{
        return string.replace( new RegExp(str_find, flags), str_replace ) ;
    } catch(ex){return string;}
}

// Constructor functions for the model objects.
var SVGDiagram = function (id, name, width, height) {
    this.id = id;
    this.name = name;
    this.width = width;
    this.height = height;
    this.nodes = [];
    this.edges = [];
};

var SVGNode = function (id, stencilId, x, y) {
    this.id = id;
    this.stencilId = stencilId;
    this.x = x;
    this.y = y;
    this.width = undefined;
    this.height = undefined;
};

var SVGEdge = function (from, to) {
    this.from = from;
    this.to = to;
};

var SVGStencilShape = function (path, file) {
    this.path = path;
    this.file = file;
};

var SVGStencilIcon = function (path, file, refs) {
    this.path = path;
    this.file = file;
    this.refs = refs;
};

var SVGStencil = function (id, name) {
    this.id = id;
    this.name = name;
    this.shape = undefined;
    this.icon = undefined;
};

var SVGStencilSet = function (name) {
    this.name = name;
    this.path = "";
    this.cssPath = "";
    this.stencils = [];
};

// The SVG Tester object's constructor function.
var SVGStencilTester = function(inputPath) {
    this.stencilSet = undefined;
    this.diagramTesters = [];
    this.stencilIndex = 0;
    this.svgOriginalId = undefined;
    this.svgTesterContainerId = undefined;
    this.parseStencilSetInput(inputPath);
};

SVGStencilTester.prototype.parseStencilSetInput = function(inputPath) {
    const self = this;
    console.log("Parsing input stencil set file at [" + inputPath + "]");
    $.get(inputPath, function(inputRaw){
        // Parse the JSON input.
        const input = JSON.parse(inputRaw);
        console.log("Loading Stencil Set [" + input.name + "]");
        self.stencilSet = new SVGStencilSet(input.name);
        self.stencilSet.path = input.path;
        self.stencilSet.cssPath = input.css;
        for (var i = 0; i < input.stencils.length; i ++) {
            const inputStencil = input.stencils[i];
            console.log("Loading Stencil [" + inputStencil.id + "]");
            const stencil = new SVGStencil(inputStencil.id, inputStencil.name);
            stencil.shape = new SVGStencilShape(inputStencil.shape.path, inputStencil.shape.file);
            if (inputStencil.icon) {
                var iconRefs = inputStencil.icon.refs ? inputStencil.icon.refs : undefined;
                stencil.icon = new SVGStencilIcon(inputStencil.icon.path, inputStencil.icon.file, iconRefs);
            }
            self.stencilSet.stencils.push(stencil);
        }

    }, 'text');

};

SVGStencilTester.prototype.populateDiagramButtons = function() {
    const diagramButtonsPanel = document.getElementById('diagramButtonsPanel');
    for (var i = 0; i < this.diagramTesters.length; i++) {
        const diagramTester = this.diagramTesters[i];
        const button = document.createElement('input');
        button.type = "button";
        button.title = diagramTester.svgDiagram.name;
        button.value = diagramTester.svgDiagram.name;
        button.style.float = "left";
        const self = this;
        button.onclick = function (e) {
            self.navigateTo(diagramTester.svgDiagram.id);
        };
        diagramButtonsPanel.appendChild(button);
    }

};

SVGStencilTester.prototype.populateStencils = function() {
    const domShapeSelector = document.getElementById('shapeSelector');
    for (var i = 0; i < this.stencilSet.stencils.length; i++) {
        const stencil = this.stencilSet.stencils[i];
        const shapeItem = document.createElement('option');
        shapeItem.value = i;
        if (i === 0) {
            shapeItem.selected = true;
        }
        shapeItem.innerHTML = stencil.name;
        domShapeSelector.appendChild(shapeItem);
    }
    stencilIndex = 0;
};

SVGStencilTester.prototype.processSvgContent = function(svgContent, relativePath) {
    // Remove the declaration tags.
    // TODO: regexp.
    var svgProcessed = svgContent.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
    // TODO: Remove comments
    // Use the right uri paths for each href.
    // TODO: regexp.
    svgProcessed = strReplaceAll(svgProcessed, "xlink:href=\"", "xlink:href=\"" + relativePath)
    // console.log("************ PROCESSED SVG CONTENT ***************************************");
    // console.log( svgProcessed );
    // console.log("**************************************************************************");
    return svgProcessed;
};

SVGStencilTester.prototype.clearContent = function () {
    const shapeContentDiv = document.getElementById('shapePanel');
    while (shapeContentDiv.firstChild) {
        shapeContentDiv.removeChild(shapeContentDiv.firstChild);
    }
    const iconContentDiv = document.getElementById('iconPanel');
    while (iconContentDiv.firstChild) {
        iconContentDiv.removeChild(iconContentDiv.firstChild);
    }
};

SVGStencilTester.prototype.updateContent = function () {
    // Clear current content, if any.
    this.clearContent();
    // Obtain selected values.
    const domShapeSelector = document.getElementById('shapeSelector');
    const domAppendTextInput = document.getElementById('appendTextInput');
    stencilIndex = domShapeSelector.options[domShapeSelector.selectedIndex].value;
    const appendTextValue = domAppendTextInput.checked;
    // Load and process the svg content.
    const self = this;
    const shapeLoadCallback = function (svgProcessed) {
        const contentDiv = document.getElementById('shapePanel');
        // Update the content div.
        contentDiv.innerHTML = svgProcessed;
        svgOriginalId = $("#shapePanel > svg").first().attr("id");
        console.log("Original SVG Element ID = " + svgOriginalId);
        // Set the stencil id attribute for making CSS styles to work.
        const currentShapeId = self.stencilSet.stencils[stencilIndex].id;
        if (svgOriginalId !== currentShapeId) {
            $("#shapePanel > svg").first().attr("id", currentShapeId);
        }

        // Apply syles for the concrete stencil selected.
        self.applySelectedShapeState();

        if (appendTextValue) {
            self.appendSvgText();
        }
    };
    const iconLoadCallback = function (svgProcessed) {
        const contentDiv = document.getElementById('iconPanel');
        contentDiv.style.display = "none";
        contentDiv.innerHTML = svgProcessed;
        setTimeout(function() {
            const iconStencil = self.stencilSet.stencils[stencilIndex];
            const contentDiv = document.getElementById('iconPanel');
            const svgElement = $("#iconPanel > svg").first()[0];
            var iconRefs = [ "" ];
            if (iconStencil.icon.refs && iconStencil.icon.refs.length > 0) {
                iconRefs = iconStencil.icon.refs.split(',');
            }
            const useElements = $("#" + svgElement.id + " use");
            for (var i = 0; i < useElements.length; i++) {
                const useElement = useElements[i];
                // TODO: Handle namespace attribute name.
                const useHref = $(useElement).attr("xlink:href");
                var enabled = false;
                for (var j = 0; j < iconRefs.length; j++) {
                    const iconRef = iconRefs[j];
                    if (useHref.match("#" + iconRef + "$")) {
                        enabled = true;
                    }
                }
                if (!enabled) {
                    useElement.style.display = "none";
                }
            }
            // Resize the icon to the given values.
            const sizeRaw = ICON_SIZE + "px";
            contentDiv.style.width = sizeRaw;
            contentDiv.style.height = sizeRaw;
            svgElement.style.width = sizeRaw;
            svgElement.style.height = sizeRaw;
            contentDiv.style.display = "block";
        }, 200);
    };
    const s = this.stencilSet.stencils[stencilIndex];
    this.loadSvgShape(s, shapeLoadCallback);
    if (s.icon) {
        this.loadSvgIcon(s, iconLoadCallback);
    } else {
        document.getElementById('iconPanel').innerHTML = "<h4>No Icon</h4>"
    }
};

SVGStencilTester.prototype.parseSvgElementAttributes = function (svgElement) {
    const width = parseFloat($(svgElement).first().attr("width"));
    const height = parseFloat($(svgElement).first().attr("height"));
    return [ width, height];
};

SVGStencilTester.prototype.loadSvgShape= function (stencil, callback) {
    const relativePath = stencil.shape.path + "/";
    const shapePath = this.stencilSet.path + "/" + relativePath + stencil.shape.file;
    this.loadSvgContent(shapePath, relativePath, callback);
};

SVGStencilTester.prototype.loadSvgIcon = function (stencil, callback) {
    const relativePath = stencil.icon.path + "/";
    const iconPath = this.stencilSet.path + "/" + relativePath + stencil.icon.file;
    this.loadSvgContent(iconPath, relativePath, callback);
};

SVGStencilTester.prototype.loadSvgContent= function (path, relativePath, callback) {
    const self = this;
    $.get(path, function(theSvgContent){
        var svgProcessed = self.processSvgContent(theSvgContent, relativePath);
        callback.call(self, svgProcessed);
    }, 'text');
};

SVGStencilTester.prototype.appendTesterHtmlContent = function (testerContainerId) {
    const self = this;
    // currentTester = this;
    $.get(testerHtmlTemplatePath, function(data){
        // Append the content into the DOM.
        self.svgTesterContainerId = testerContainerId;
        $("#" + testerContainerId).html(data);
        self.populateDiagramButtons(self.diagramTesters);
        self.injectStencilSetStyleDeclarations();
        // Update the icon header's text in order to display the icon size.
        const sizeRaw = ICON_SIZE + "px";
        document.getElementById('iconHeaderSize').innerHTML = "(" + sizeRaw + ", " + sizeRaw + ")";
    });
};

SVGStencilTester.prototype.injectStencilSetStyleDeclarations = function () {
    const self = this;
    const cssPath = self.stencilSet.path + "/" + self.stencilSet.cssPath;
    $.get(cssPath, function(theCssContent){
        // Append style declarations into the DOM.
        var node = document.createElement('style');
        node.innerHTML = theCssContent;
        document.body.appendChild(node);

        // Populate the stencils dropdown.
        self.populateStencils();

        // Update the content.
        self.updateContent();

    }, 'text');
};

SVGStencilTester.prototype.applySelectedShapeState = function() {
    const domShapeStateSelector = document.getElementById('shapeStateSelector');
    const shapeStateValue = domShapeStateSelector.options[domShapeStateSelector.selectedIndex].value;
    if (shapeStateValue !== "shape-state-none") {
        this.applyShapeState(shapeStateValue);
    }
};

SVGStencilTester.prototype.applyShapeState = function(shapeStateValue) {
    if (shapeStateValue === "shape-state-selected") {
        $("#shapePanel > svg").first().attr("filter","url(#selectShadow)");
    } else if (shapeStateValue === "shape-state-highlight") {
        // TODO
        $("#shapePanel > svg").first().attr("filter","");
    } else if (shapeStateValue === "shape-state-invalid") {
        // TODO
        $("#shapePanel > svg").first().attr("filter","");
    }
};

SVGStencilTester.prototype.appendSvgText = function() {
    const currentShapeId = this.stencilSet.stencils[stencilIndex].id;
    const svgElement = document.getElementById(currentShapeId);
    const attrs = this.parseSvgElementAttributes(svgElement);
    const width = attrs[0];
    const height = attrs[1];
    const newText = document.createElementNS("http://www.w3.org/2000/svg","text");
    newText.setAttributeNS(null,"x", (width / 2) - 20 + "px");
    newText.setAttributeNS(null,"y", (height / 2) + "px");
    newText.setAttributeNS(null,"id","text");
    const textNode = document.createTextNode("Some text");
    newText.appendChild(textNode);
    svgElement.appendChild(newText);
};

SVGStencilTester.prototype.show = function (testerContainerId) {
    currentTester = this;
    this.appendTesterHtmlContent(testerContainerId);
};

SVGStencilTester.prototype.clear = function () {
    this.clearContent();
};

SVGStencilTester.prototype.addDiagram = function (inputDiagramPath) {
    const t = new SVGDiagramTester(inputDiagramPath);
    this.diagramTesters.push(t);
};

SVGStencilTester.prototype.navigateTo = function (target) {
    const self = this;
    if ('shape-tester' === target) {
        $("#diagramTesterPanel").attr("style", "display: none;");
        $("#shapeTesterPanel").attr("style", "display: inline;");
        document.getElementById("diagramTesterPanel").innerHTML = "";
    } else {
        $("#shapeTesterPanel").attr("style", "display: none;");
        $("#diagramTesterPanel").attr("style", "display: inline;");
        const tester = self.diagramTesters
                .filter(function (e) {
                    return (e.svgDiagram.id === target);
                });
        tester[0].draw(self);
    }
};

// The SVG Diagram Tester object factory method.
var SVGDiagramTester = function(inputDiagramPath) {
    this.svgDiagram = undefined;
    this.parseDiagramInput(inputDiagramPath);
};

SVGDiagramTester.prototype.draw = function(tester) {
    const diagramTesterPanel = document.getElementById("diagramTesterPanel");
    const compositeRaw = this.generateCompositeSvg();
    diagramTesterPanel.innerHTML = compositeRaw;
    const after = function () {
        this.populateEdges(tester, diagramTesterPanel, 0);
    };
    this.populateNodes(tester, diagramTesterPanel, 0, after);
};

SVGDiagramTester.prototype.generateCompositeSvg = function() {
    var content = "<svg id=\"" + this.svgDiagram.id + "\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" x=\"0\" y=\"0\" width=\"" + this.svgDiagram.width + "px\" height=\"" + this.svgDiagram.height + "px\">";
    for (var i = 0; i < this.svgDiagram.nodes.length; i++) {
        var node = this.svgDiagram.nodes[i];
        content += "<g id=\"" + node.id + "_group\" transform=\"translate(" + node.x + "," + node.y + ")\"></g>";
    }
    for (var i = 0; i < this.svgDiagram.edges.length; i++) {
        var edge = this.svgDiagram.edges[i];
        content += "<g id=\"edge" + i + "\"></g>";
    }
    content += "</svg>";
    return content;
};

SVGDiagramTester.prototype.getNode = function(id) {
    return this.svgDiagram.nodes.filter(function (e) {
        return (e.id === id);
    })[0];
};

SVGDiagramTester.prototype.populateEdges = function(tester, diagramTesterPanel, index) {
    var edges = this.svgDiagram.edges[index];
    var edgeRaw = this.generateEdge(edges);
    const groupElement = document.getElementById("edge" + index);
    groupElement.innerHTML = edgeRaw;
    if (index + 1 < this.svgDiagram.edges.length) {
        this.populateEdges(tester, diagramTesterPanel, index + 1);
    }
};

SVGDiagramTester.prototype.generateEdge = function(edge) {
    const from = edge.from;
    const to = edge.to;
    const source = this.getNode(from);
    const target = this.getNode(to);
    const x1 = source.x + source.width;
    const y1 = source.y + (source.height / 2);
    const x2 = target.x;
    const y2 = target.y + (target.height / 2);
    var content = "<polyline fill=\"none\" stroke=\"black\" points=\""+ x1 + "," + y1 + " " + x2 + "," + y2 + "\"></polyline>";
    return content;
};

SVGDiagramTester.prototype.populateNodes = function(tester, diagramTesterPanel, index, onComplete) {
    const self = this;
    var node = this.svgDiagram.nodes[index];
    const after = function (svgProcessed) {
        console.log("Loading SVG #" + index);
        const panelId = node.id + "_group";
        const groupPanel = document.getElementById(panelId);
        groupPanel.innerHTML = svgProcessed;
        $("#" + panelId + " > svg").first().attr("id", node.stencilId);
        const size = tester.parseSvgElementAttributes(document.getElementById(node.stencilId));
        node.width = size[0];
        node.height = size[1];
        if (index + 1 < self.svgDiagram.nodes.length) {
            self.populateNodes(tester, diagramTesterPanel, index + 1, onComplete);
        } else {
            onComplete.call(self);
        }
    };
    const stencil = tester.stencilSet.stencils
            .filter(function (e) {
                return (e.id === node.stencilId);
            });
    tester.loadSvgShape(stencil[0], after);
};

SVGDiagramTester.prototype.parseDiagramInput = function(inputDiagramPath) {
    console.log("Parsing input diagram file at [" + inputDiagramPath + "]");
    const self = this;
    $.get(inputDiagramPath, function(inputRaw){
        const input = JSON.parse(inputRaw);
        console.log("Loading Diagram [" + input.id + "]");
        self.svgDiagram = new SVGDiagram(input.id, input.name, input.width, input.height);
        for (var i = 0; i < input.nodes.length; i ++) {
            const inputNode  = input.nodes[i];
            console.log("Loading Node [" + inputNode.id + "]");
            const node = new SVGNode(inputNode.id, inputNode.stencilId, parseInt(inputNode.x), parseInt(inputNode.y));
            self.svgDiagram.nodes.push(node);
        }
        for (var i = 0; i < input.edges.length; i ++) {
            const inputEdge = input.edges[i];
            console.log("Loading Edge [from=" + inputEdge.from + ", to=" + inputEdge.to + "]");
            const edge = new SVGEdge(inputEdge.from, inputEdge.to);
            self.svgDiagram.edges.push(edge);
        }
    }, 'text');

};