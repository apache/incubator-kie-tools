// GitHub is a SPA and update its URL without refreshing the page.
// This script ensures the extension is not being loaded more than once.

var _wr = function (type) {
  var orig = history[type];
  return function () {
    var rv = orig.apply(this, arguments);
    var e = new Event(type);
    e.arguments = arguments;
    window.dispatchEvent(e);
    return rv;
  };
};
history.replaceState = _wr("replaceState");
