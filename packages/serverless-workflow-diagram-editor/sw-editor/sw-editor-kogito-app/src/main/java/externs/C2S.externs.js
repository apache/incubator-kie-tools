/**
 * @externs
 */

/**
 * @typedef {{width:(number|undefined),height:(number|undefined), ctx:(*), enableMirroring: (boolean|undefined)}}
 */
var C2SSettings;

/**
 *
 * @param {C2SSettings=} options
 * @constructor
 */
var C2S = function (options) {};

/**
 * @type {C2SSettings}
 */
C2S.prototype.settings;

/**
 * @return {string}
 */
C2S.prototype.getSerializedSvg = function () {};

/**
 * @return {Element}
 */
C2S.prototype.getSvg = function () {};

/**
 * @type {string}
 */
C2S.prototype.fillStyleColor;

/**
 * @type {string}
 */
C2S.prototype.strokeStyle;

/**
 * @type {number}
 */
C2S.prototype.lineWidth;

/**
 * @type {string}
 */
C2S.prototype.lineCap;

/**
 * @type {string}
 */
C2S.prototype.lineJoin;

/**
 * @type {boolean}
 */
C2S.prototype.ImageSmoothingEnabled;

/**
 * @type {string}
 */
C2S.prototype.font;

/**
 * @type {string}
 */
C2S.prototype.textBaseline;

/**
 * @type {string}
 */
C2S.prototype.textAlign;

/**
 * @type {number}
 */
C2S.prototype.globalAlpha;

/**
 * @type {string}
 */
C2S.prototype.shadowColor;

/**
 * @type {number}
 */
C2S.prototype.shadowOffsetX;

/**
 * @type {number}
 */
C2S.prototype.shadowOffsetY;

/**
 * @type {number}
 */
C2S.prototype.shadowBlur;

/**
 * @type {number}
 */
C2S.prototype.miterLimit;

/**
 * @type {number}
 */
C2S.prototype.lineDashOffset;

C2S.prototype.save = function () {};

C2S.prototype.restore = function () {};

C2S.prototype.beginPath = function () {};

C2S.prototype.closePath = function () {};

/**
 * @param {number} x
 * @param {number} y
 */
C2S.prototype.moveTo = function (x, y) {};

/**
 * @param {number} x
 * @param {number} y
 */
C2S.prototype.lineTo = function (x, y) {};

/**
 * @param {string} operation
 */
C2S.prototype.setGlobalCompositeOperation = function (operation) {};

/**
 * @param {number} cpx
 * @param {number} cpy
 * @param {number} x
 * @param {number} y
 */
C2S.prototype.quadraticCurveTo = function (cpx, cpy, x, y) {};

/**
 * @param {number} x
 * @param {number} y
 * @param {number} radius
 * @param {number} startAngle
 * @param {number} endAngle
 * @param {boolean=} anticlockwise
 */
C2S.prototype.arc = function (x, y, radius, startAngle, endAngle, anticlockwise) {};

/**
 * @param {number} x
 * @param {number} y
 * @param {number} rx
 * @param {number} ry
 * @param {number} ro
 * @param {number} sa
 * @param {number} ea
 * @param {boolean=} ac
 */
C2S.prototype.ellipse = function (x, y, rx, ry, ro, sa, ea, ac) {};

/**
 * @param {number} x1
 * @param {number} y1
 * @param {number} x2
 * @param {number} y2
 * @param {number} radius
 */
C2S.prototype.arcTo = function (x1, y1, x2, y2, radius) {};

/**
 * @param {number} cp1x
 * @param {number} cp1y
 * @param {number} cp2x
 * @param {number} cp2y
 * @param {number} x
 * @param {number} y
 */
C2S.prototype.bezierCurveTo = function (cp1x, cp1y, cp2x, cp2y, x, y) {};

/**
 * @param {number} x
 * @param {number} y
 * @param {number} w
 * @param {number} h
 */
C2S.prototype.clearRect = function (x, y, w, h) {};

C2S.prototype.clip = function () {};

C2S.prototype.fill = function () {};

C2S.prototype.stroke = function () {};

/**
 * @param {number} x
 * @param {number} y
 * @param {number} w
 * @param {number} h
 */
C2S.prototype.fillRect = function (x, y, w, h) {};

/**
 * @param {string} text
 * @param {number} x
 * @param {number} y
 */
C2S.prototype.fillText = function (text, x, y) {};

/**
 * @param {number} x0
 * @param {number} y0
 * @param {number} x1
 * @param {number} y1
 * @return {CanvasGradient}
 */
C2S.prototype.createLinearGradient = function (x0, y0, x1, y1) {};

/**
 * @param {number} x0
 * @param {number} y0
 * @param {number} r0
 * @param {number} x1
 * @param {number} y1
 * @param {number} r1
 * @return {CanvasGradient}
 */
C2S.prototype.createRadialGradient = function (x0, y0, r0, x1, y1, r1) {};

/**
 * @param {number} x
 * @param {number} y
 * @param {number} w
 * @param {number} h
 */
C2S.prototype.rect = function (x, y, w, h) {};

/**
 * @param {number} angle
 */
C2S.prototype.rotate = function (angle) {};

/**
 * @param {number} sx
 * @param {number} sy
 */
C2S.prototype.scale = function (sx, sy) {};

/**
 * @param {number} d0
 * @param {number} d1
 * @param {number} d2
 * @param {number} d3
 * @param {number} d4
 * @param {number} d5
 */
C2S.prototype.transform = function (d0, d1, d2, d3, d4, d5) {};

/**
 * @param {number} d0
 * @param {number} d1
 * @param {number} d2
 * @param {number} d3
 * @param {number} d4
 * @param {number} d5
 */
C2S.prototype.setTransform = function (d0, d1, d2, d3, d4, d5) {};

/**
 * @param {string} text
 * @param {number} x
 * @param {number} y
 */
C2S.prototype.strokeText = function (text, x, y) {};

/**
 * @param {number} x
 * @param {number} y
 */
C2S.prototype.translate = function (x, y) {};

/**
 * @param {number} x
 * @param {number} y
 * @return {boolean}
 */
C2S.prototype.isPointInPath = function (x, y) {};

/**
 *
 * @param {ImageData} imageData
 * @param {number} x
 *  @param {number} y
 *  @param {number=} dx
 *  @param {number=} dy
 *  @param {number=} dw
 *  @param {number=} dh
 */
C2S.prototype.putImageData = function (imageData, x, y, dx, dy, dw, dh) {};

C2S.prototype.resetClip = function () {};

/**
 * @param {Array<number>} dashes
 */
C2S.prototype.setLineDash = function (dashes) {};

/**
 * @param {string} text
 * @return {TextMetrics}
 */
C2S.prototype.measureText = function (text) {};

/**
 * @param {number} x
 * @param {number} y
 * @param {number=} width
 * @param {number=} height
 * @return {ImageData}
 */
C2S.prototype.getImageData = function (x, y, width, height) {};

/**
 * @param {Element} image
 * @param {number} sx
 * @param {number} sy
 * @param {number=} sw
 * @param {number=} sh
 * @param {number=} x
 * @param {number=} y
 * @param {number=} w
 * @param {number=} h
 */
C2S.prototype.drawImage = function (image, sx, sy, sw, sh, x, y, w, h) {};

/**
 * @param {string} elementName
 * @return {Element}
 */
C2S.prototype.__createElement = function (elementName) {};

/**
 * @param {Object} node
 * @return {Element}
 */
C2S.prototype.__closestGroupOrSvg = function (node) {};

/**
 * @return {Object}
 */
C2S.prototype.__getStyleState = function () {};

/**
 * @param {Object} styleState
 */
C2S.prototype.__applyStyleState = function (styleState) {};

/** @type {Array} */
C2S.prototype.__groupStack;

/** @type {Array} */
C2S.prototype.__stack;

/** @type {Element} */
C2S.prototype.__currentElement;

/** @type {Element} */
C2S.prototype.__root;

/** @type {Object} */
C2S.prototype.__currentElementsToStyle;

/**
 * @param {ImageData|number} width
 * @param {number=} height
 * @return {HTMLCanvasElement|ImageData}
 */
C2S.prototype.createImageData = function (width, height) {};
