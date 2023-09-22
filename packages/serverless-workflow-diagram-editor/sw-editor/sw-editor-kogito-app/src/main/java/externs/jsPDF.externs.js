/**
 * @externs
 */

/**
 * @constructor
 * @param {Object} settings
 */
var jsPDF = function (settings) {};

/**
 * @param {string} imgData
 * @param {string} imgType
 * @param {number} x
 * @param {number} y
 * @param {number} width
 * @param {number} height
 */
jsPDF.prototype.addImage = function (imgData, imgType, x, y, width, height) {};

/**
 * @param {string} fileName
 */
jsPDF.prototype.save = function (fileName) {};

/**
 * @param {string} text
 * @param {number} x
 * @param {number} y
 */
jsPDF.prototype.text = function (text, x, y) {};
