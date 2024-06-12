<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

    // Webapp URLs
    file:////home/roger/development/romartin/lienzo-webapp/target/lienzo-webapp-7.48.0-SNAPSHOT/LienzoShowcase.html
    http://localhost/lienzo-webapp-7.48.0-SNAPSHOT/LienzoShowcase.html

    // Examples webapp usage
    window.jsCanvasExamples.goToExample(0);

    // JsCanvas - Move shape
    window.jsCanvas.move(window.jsCanvas.getShape('rectangle'), 300, 300);

    // JsCanvas - Click & Move shape
    var r = window.jsCanvas.getShape('r');
    window.jsCanvas.click(r);
    window.jsCanvas.move(r, 100, 100);

    // Create badge
    var jsl = window.jsCanvas;
    var badge = new com.ait.lienzo.client.core.shape.Group();
    badge.listening = false;
    badge.alpha = 0;
    var text = new com.ait.lienzo.client.core.shape.Text("100", "arial", "italic", 12);
    badge.add(text);
    var bb = text.getBoundingBox();
    var decorator = new com.ait.lienzo.client.core.shape.Rectangle(bb.getWidth() + 10, bb.getHeight() + 10);
    decorator.x = bb.getX() - 5;
    decorator.y = bb.getY() - 5;
    decorator.fillAlpha = 0;
    decorator.strokeAlpha = 1;
    decorator.strokeColor = 'red';
    decorator.strokeWitrh = 2;
    decorator.cornerRadius = 5;
    badge.add(decorator);
    badge.x = 100;
    badge.y = 100;
    jsl.add(badge);
    jsl.animations().alpha(badge, 1, 1500);
