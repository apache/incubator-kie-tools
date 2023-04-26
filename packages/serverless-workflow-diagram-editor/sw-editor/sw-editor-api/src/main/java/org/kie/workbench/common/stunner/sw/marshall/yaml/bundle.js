(function () {
  /*

 Copyright The Closure Library Authors.
 SPDX-License-Identifier: Apache-2.0
*/
  function aa(a) {
    var b = 0;
    return function () {
      return b < a.length ? { done: !1, value: a[b++] } : { done: !0 };
    };
  }
  var ba =
    "function" == typeof Object.defineProperties
      ? Object.defineProperty
      : function (a, b, c) {
          if (a == Array.prototype || a == Object.prototype) return a;
          a[b] = c.value;
          return a;
        };
  function ca(a) {
    a = [
      "object" == typeof globalThis && globalThis,
      a,
      "object" == typeof window && window,
      "object" == typeof self && self,
      "object" == typeof global && global,
    ];
    for (var b = 0; b < a.length; ++b) {
      var c = a[b];
      if (c && c.Math == Math) return c;
    }
    throw Error("Cannot find global object");
  }
  var da = ca(this);
  function ea(a, b) {
    if (b)
      a: {
        var c = da;
        a = a.split(".");
        for (var d = 0; d < a.length - 1; d++) {
          var e = a[d];
          if (!(e in c)) break a;
          c = c[e];
        }
        a = a[a.length - 1];
        d = c[a];
        b = b(d);
        b != d && null != b && ba(c, a, { configurable: !0, writable: !0, value: b });
      }
  }
  ea("Symbol", function (a) {
    function b(f) {
      if (this instanceof b) throw new TypeError("Symbol is not a constructor");
      return new c(d + (f || "") + "_" + e++, f);
    }
    function c(f, g) {
      this.i = f;
      ba(this, "description", { configurable: !0, writable: !0, value: g });
    }
    if (a) return a;
    c.prototype.toString = function () {
      return this.i;
    };
    var d = "jscomp_symbol_" + ((1e9 * Math.random()) >>> 0) + "_",
      e = 0;
    return b;
  });
  ea("Symbol.iterator", function (a) {
    if (a) return a;
    a = Symbol("Symbol.iterator");
    for (
      var b =
          "Array Int8Array Uint8Array Uint8ClampedArray Int16Array Uint16Array Int32Array Uint32Array Float32Array Float64Array".split(
            " "
          ),
        c = 0;
      c < b.length;
      c++
    ) {
      var d = da[b[c]];
      "function" === typeof d &&
        "function" != typeof d.prototype[a] &&
        ba(d.prototype, a, {
          configurable: !0,
          writable: !0,
          value: function () {
            return fa(aa(this));
          },
        });
    }
    return a;
  });
  function fa(a) {
    a = { next: a };
    a[Symbol.iterator] = function () {
      return this;
    };
    return a;
  }
  var ha = this || self;
  function ia(a, b) {
    a = a.split(".");
    var c = ha;
    a[0] in c || "undefined" == typeof c.execScript || c.execScript("var " + a[0]);
    for (var d; a.length && (d = a.shift()); )
      a.length || void 0 === b ? (c[d] && c[d] !== Object.prototype[d] ? (c = c[d]) : (c = c[d] = {})) : (c[d] = b);
  }
  function ja(a, b) {
    return Object.prototype.hasOwnProperty.call(a, "$$class/0") ? a["$$class/0"] : (a["$$class/0"] = b("$$class/0"));
  }
  class ka {
    toString() {
      var a = y(la(ma(this.constructor))) + "@";
      var b = (
        (this.C || (Object.defineProperties(this, { C: { value: (na = (na + 1) | 0), enumerable: !1 } }), this.C)) >>> 0
      ).toString(16);
      return a + y(b);
    }
  }
  ka.prototype.S = ["java.lang.Object", 0];
  var na = 0;
  function la(a) {
    if (0 != a.l) {
      let b = 3 == a.i.prototype.S[1] ? a.i.prototype.S[2] : "L" + y(a.i.prototype.S[0]) + ";";
      a = a.l;
      let c = "";
      for (let d = 0; d < a; d = (d + 1) | 0) c = y(c) + "[";
      return y(c) + y(b);
    }
    return a.i.prototype.S[0];
  }
  class oa extends ka {
    constructor(a) {
      super();
      this.i = a;
      this.l = 0;
    }
    toString() {
      return (
        String(
          0 == this.l && 1 == this.i.prototype.S[1]
            ? "interface "
            : 0 == this.l && 3 == this.i.prototype.S[1]
            ? ""
            : "class "
        ) + y(la(this))
      );
    }
  }
  oa.prototype.S = ["java.lang.Class", 0];
  function ma(a) {
    return ja(a.prototype, function () {
      return new oa(a);
    });
  }
  function y(a) {
    return null == a ? "null" : a.toString();
  }
  const pa = Symbol.for("yaml.alias"),
    qa = Symbol.for("yaml.document"),
    A = Symbol.for("yaml.map"),
    ra = Symbol.for("yaml.pair"),
    B = Symbol.for("yaml.scalar"),
    sa = Symbol.for("yaml.seq"),
    D = Symbol.for("yaml.node.type"),
    ta = (a) => !!a && "object" === typeof a && a[D] === pa,
    ua = (a) => !!a && "object" === typeof a && a[D] === qa,
    va = (a) => !!a && "object" === typeof a && a[D] === A,
    F = (a) => !!a && "object" === typeof a && a[D] === ra,
    G = (a) => !!a && "object" === typeof a && a[D] === B,
    wa = (a) => !!a && "object" === typeof a && a[D] === sa;
  function H(a) {
    if (a && "object" === typeof a)
      switch (a[D]) {
        case A:
        case sa:
          return !0;
      }
    return !1;
  }
  function I(a) {
    if (a && "object" === typeof a)
      switch (a[D]) {
        case pa:
        case A:
        case B:
        case sa:
          return !0;
      }
    return !1;
  }
  class xa {
    constructor(a) {
      Object.defineProperty(this, D, { value: a });
    }
    clone() {
      const a = Object.create(Object.getPrototypeOf(this), Object.getOwnPropertyDescriptors(this));
      this.o && (a.o = this.o.slice());
      return a;
    }
  }
  const J = Symbol("break visit"),
    ya = Symbol("remove node");
  function za(a, b) {
    b =
      "object" === typeof b && (b.ya || b.Node || b.fa)
        ? Object.assign(
            { gb: b.Node, Map: b.Node, Oa: b.Node, za: b.Node },
            b.fa && { Map: b.fa, Oa: b.fa, za: b.fa },
            b.ya && { Map: b.ya, za: b.ya },
            b
          )
        : b;
    ua(a) ? Aa(null, a.v, b, Object.freeze([a])) === ya && (a.v = null) : Aa(null, a, b, Object.freeze([]));
  }
  function Aa(a, b, c, d) {
    const e = Ba(a, b, c, d);
    if (I(e) || F(e)) {
      b = d[d.length - 1];
      if (H(b)) b.items[a] = e;
      else if (F(b)) "key" === a ? (b.key = e) : (b.value = e);
      else if (ua(b)) b.v = e;
      else throw Error(`Cannot replace node with ${ta(b) ? "alias" : "scalar"} parent`);
      return Aa(a, e, c, d);
    }
    if ("symbol" !== typeof e)
      if (H(b))
        for (d = Object.freeze(d.concat(b)), a = 0; a < b.items.length; ++a) {
          const f = Aa(a, b.items[a], c, d);
          if ("number" === typeof f) a = f - 1;
          else {
            if (f === J) return J;
            f === ya && (b.items.splice(a, 1), --a);
          }
        }
      else if (F(b)) {
        d = Object.freeze(d.concat(b));
        a = Aa("key", b.key, c, d);
        if (a === J) return J;
        a === ya && (b.key = null);
        c = Aa("value", b.value, c, d);
        if (c === J) return J;
        c === ya && (b.value = null);
      }
    return e;
  }
  function Ba(a, b, c, d) {
    if ("function" === typeof c) return c(a, b, d);
    if (va(b)) {
      let e;
      return null == (e = c.Map) ? void 0 : e.call(c, a, b, d);
    }
    if (wa(b)) {
      let e;
      return null == (e = c.za) ? void 0 : e.call(c, a, b, d);
    }
    if (F(b)) {
      let e;
      return null == (e = c.Db) ? void 0 : e.call(c, a, b, d);
    }
    if (G(b)) {
      let e;
      return null == (e = c.Oa) ? void 0 : e.call(c, a, b, d);
    }
    if (ta(b)) {
      let e;
      return null == (e = c.gb) ? void 0 : e.call(c, a, b, d);
    }
  }
  const Ca = { "!": "%21", ",": "%2C", "[": "%5B", "]": "%5D", "{": "%7B", "}": "%7D" },
    Da = (a) => a.replace(/[!,[\]{}]/g, (b) => Ca[b]);
  function Ea(a, b) {
    for (const [c, d] of Object.entries(a.G)) if (b.startsWith(d)) return c + Da(b.substring(d.length));
    return "!" === b[0] ? b : `!<${b}>`;
  }
  class Fa {
    constructor(a, b) {
      this.aa = null;
      this.Ra = !1;
      this.J = Object.assign({}, Ga, a);
      this.G = Object.assign({}, Ha, b);
    }
    clone() {
      const a = new Fa(this.J, this.G);
      a.aa = this.aa;
      return a;
    }
    add(a, b) {
      this.i && ((this.J = { X: Ga.X, version: "1.1" }), (this.G = Object.assign({}, Ha)), (this.i = !1));
      a = a.trim().split(/[ \t]+/);
      const c = a.shift();
      switch (c) {
        case "%TAG":
          if (2 !== a.length && (b(0, "%TAG directive should contain exactly two parts"), 2 > a.length)) return !1;
          const [d, e] = a;
          this.G[d] = e;
          return !0;
        case "%YAML":
          this.J.X = !0;
          if (1 !== a.length) return b(0, "%YAML directive should contain exactly one part"), !1;
          [a] = a;
          if ("1.1" === a || "1.2" === a) return (this.J.version = a), !0;
          b(6, `Unsupported YAML version ${a}`, /^\d+\.\d+$/.test(a));
          return !1;
        default:
          return b(0, `Unknown directive ${c}`, !0), !1;
      }
    }
    tagName(a, b) {
      if ("!" === a) return "!";
      if ("!" !== a[0]) return b(`Not a valid tag: ${a}`), null;
      if ("<" === a[1]) {
        var c = a.slice(2, -1);
        if ("!" === c || "!!" === c) return b(`Verbatim tags aren't resolved, so ${a} is invalid.`), null;
        ">" !== a[a.length - 1] && b("Verbatim tags must end with a >");
        return c;
      }
      const [, d, e] = a.match(/^(.*!)([^!]*)$/);
      e || b(`The ${a} tag has no suffix`);
      if ((c = this.G[d])) return c + decodeURIComponent(e);
      if ("!" === d) return a;
      b(`Could not resolve tag: ${a}`);
      return null;
    }
    toString(a) {
      const b = this.J.X ? [`%YAML ${this.J.version || "1.2"}`] : [],
        c = Object.entries(this.G);
      let d;
      if (a && 0 < c.length && I(a.v)) {
        const e = {};
        za(a.v, (f, g) => {
          I(g) && g.tag && (e[g.tag] = !0);
        });
        d = Object.keys(e);
      } else d = [];
      for (const [e, f] of c)
        if ("!!" !== e || "tag:yaml.org,2002:" !== f)
          (a && !d.some((g) => g.startsWith(f))) || b.push(`%TAG ${e} ${f}`);
      return b.join("\n");
    }
  }
  var Ga = { X: !1, version: "1.2" },
    Ha = { "!!": "tag:yaml.org,2002:" };
  function Ia(a) {
    if (/[\x00-\x19\s,[\]{}]/.test(a))
      throw ((a = `Anchor must not contain whitespace or control characters: ${JSON.stringify(a)}`), Error(a));
    return !0;
  }
  function Ja(a) {
    const b = new Set();
    za(a, {
      fa(c, d) {
        d.anchor && b.add(d.anchor);
      },
    });
    return b;
  }
  function Ka(a, b) {
    const c = [],
      d = new Map();
    let e = null;
    return {
      da: (f) => {
        c.push(f);
        e || (e = Ja(a));
        a: {
          f = e;
          for (let g = 1; ; ++g) {
            const h = `${b}${g}`;
            if (!f.has(h)) {
              f = h;
              break a;
            }
          }
        }
        e.add(f);
        return f;
      },
      vb: () => {
        for (const g of c) {
          var f = d.get(g);
          if ("object" === typeof f && f.anchor && (G(f.node) || H(f.node))) f.node.anchor = f.anchor;
          else throw ((f = Error("Failed to resolve repeated object (this should not happen)")), (f.source = g), f);
        }
      },
      xa: d,
    };
  }
  class La extends xa {
    constructor(a) {
      super(pa);
      this.source = a;
      Object.defineProperty(this, "tag", {
        set() {
          throw Error("Alias nodes cannot have tags");
        },
      });
    }
    resolve(a) {
      let b = void 0;
      za(a, {
        Node: (c, d) => {
          if (d === this) return J;
          d.anchor === this.source && (b = d);
        },
      });
      return b;
    }
    toJSON(a, b) {
      if (!b) return { source: this.source };
      a = b.anchors;
      const c = b.A;
      b = b.$a;
      const d = this.resolve(c);
      if (!d) throw new ReferenceError(`Unresolved alias (the anchor must be set before the alias): ${this.source}`);
      const e = a.get(d);
      if (!e || void 0 === e.va) throw new ReferenceError("This should not happen: Alias anchor was not resolved?");
      if (0 <= b && ((e.count += 1), 0 === e.la && (e.la = Ma(c, d, a)), e.count * e.la > b))
        throw new ReferenceError("Excessive alias count indicates a resource exhaustion attack");
      return e.va;
    }
    toString(a) {
      const b = `*${this.source}`;
      if (a) {
        Ia(this.source);
        if (a.options.zb && !a.anchors.has(this.source))
          throw Error(`Unresolved alias (the anchor must be set before the alias): ${this.source}`);
        if (a.Y) return `${b} `;
      }
      return b;
    }
  }
  function Ma(a, b, c) {
    if (ta(b)) return (a = b.resolve(a)), (c = c && a && c.get(a)) ? c.count * c.la : 0;
    if (H(b)) {
      var d = 0;
      for (const e of b.items) (b = Ma(a, e, c)), b > d && (d = b);
      return d;
    }
    return F(b) ? ((d = Ma(a, b.key, c)), (c = Ma(a, b.value, c)), Math.max(d, c)) : 1;
  }
  function K(a, b, c) {
    if (Array.isArray(a)) return a.map((d, e) => K(d, String(e), c));
    if (a && "function" === typeof a.toJSON) {
      if (!c || (!G(a) && !H(a)) || !a.anchor) return a.toJSON(b, c);
      const d = { la: 0, count: 1, va: void 0 };
      c.anchors.set(a, d);
      c.R = (e) => {
        d.va = e;
        delete c.R;
      };
      a = a.toJSON(b, c);
      c.R && c.R(a);
      return a;
    }
    return "bigint" !== typeof a || (null != c && c.Va) ? a : Number(a);
  }
  const Na = (a) => !a || ("function" !== typeof a && "object" !== typeof a);
  class L extends xa {
    constructor(a) {
      super(B);
      this.value = a;
    }
    toJSON(a, b) {
      return (null == b ? 0 : b.Va) ? this.value : K(this.value, a, b);
    }
    toString() {
      return String(this.value);
    }
  }
  function Oa(a, b, c) {
    if (b) {
      c = c.filter((f) => f.tag === b);
      let d;
      const e = null != (d = c.find((f) => !f.format)) ? d : c[0];
      if (!e) throw Error(`Tag ${b} not found`);
      return e;
    }
    return c.find((d) => {
      let e;
      return (null == (e = d.B) ? void 0 : e.call(d, a)) && !d.format;
    });
  }
  function Pa(a, b, c) {
    ua(a) && (a = a.v);
    if (I(a)) return a;
    if (F(a)) {
      var d, e;
      b = null == (e = (d = c.m[A]).createNode) ? void 0 : e.call(d, c.m, null, c);
      b.items.push(a);
      return b;
    }
    if (
      a instanceof String ||
      a instanceof Number ||
      a instanceof Boolean ||
      ("undefined" !== typeof BigInt && a instanceof BigInt)
    )
      a = a.valueOf();
    const f = c.da;
    e = c.ta;
    const g = c.m,
      h = c.xa;
    d = void 0;
    if (c.Ba && a && "object" === typeof a) {
      if ((d = h.get(a))) return d.anchor || (d.anchor = f(a)), new La(d.anchor);
      d = { anchor: null, node: null };
      h.set(a, d);
    }
    var k;
    if (null == (k = b) ? 0 : k.startsWith("!!")) b = "tag:yaml.org,2002:" + b.slice(2);
    k = Oa(a, b, g.G);
    if (!k) {
      a && "function" === typeof a.toJSON && (a = a.toJSON());
      if (!a || "object" !== typeof a) return (a = new L(a)), d && (d.node = a), a;
      k = a instanceof Map ? g[A] : Symbol.iterator in Object(a) ? g[sa] : g[A];
    }
    e && (e(k), delete c.ta);
    let m;
    e = (null == (m = k) ? 0 : m.createNode) ? k.createNode(c.m, a, c) : new L(a);
    b && (e.tag = b);
    d && (d.node = e);
    return e;
  }
  function Qa(a, b, c) {
    for (let d = b.length - 1; 0 <= d; --d) {
      const e = b[d];
      if ("number" === typeof e && Number.isInteger(e) && 0 <= e) {
        const f = [];
        f[e] = c;
        c = f;
      } else c = new Map([[e, c]]);
    }
    return Pa(c, void 0, {
      Ba: !1,
      pa: !1,
      da: () => {
        throw Error("This should not happen, please report a bug.");
      },
      m: a,
      xa: new Map(),
    });
  }
  function Ra(a, b) {
    return a.items.every((c) => {
      if (!F(c)) return !1;
      c = c.value;
      return null == c || (b && G(c) && null == c.value && !c.D && !c.g && !c.tag);
    });
  }
  class Sa extends xa {
    constructor(a, b) {
      super(a);
      Object.defineProperty(this, "schema", { value: b, configurable: !0, enumerable: !1, writable: !0 });
    }
    clone(a) {
      const b = Object.create(Object.getPrototypeOf(this), Object.getOwnPropertyDescriptors(this));
      a && (b.m = a);
      b.items = b.items.map((c) => (I(c) || F(c) ? c.clone(a) : c));
      this.o && (b.o = this.o.slice());
      return b;
    }
  }
  const Ta = (a) => a.replace(/^(?!$)(?: $)?/gm, "#");
  function M(a, b) {
    return /^\n+$/.test(a) ? a.substring(1) : b ? a.replace(/^(?! *$)/gm, b) : a;
  }
  const N = (a, b, c) =>
    a.endsWith("\n") ? M(c, b) : c.includes("\n") ? "\n" + M(c, b) : (a.endsWith(" ") ? "" : " ") + c;
  function Ua(a, b, c = "flow", { ia: d, lineWidth: e = 80, sa: f = 20, Wb: g, Xb: h } = {}) {
    if (!e || 0 > e) return a;
    var k = Math.max(1 + f, 1 + e - b.length);
    if (a.length <= k) return a;
    const m = [],
      l = {};
    var n = e - b.length;
    "number" === typeof d && (d > e - Math.max(2, f) ? m.push(0) : (n = e - d));
    let p = void 0,
      q = void 0;
    d = !1;
    let r = (f = e = -1);
    "block" === c && ((e = Va(a, e)), -1 !== e && (n = e + k));
    for (let u; (u = a[(e += 1)]); ) {
      if ("quoted" === c && "\\" === u) {
        f = e;
        switch (a[e + 1]) {
          case "x":
            e += 3;
            break;
          case "u":
            e += 5;
            break;
          case "U":
            e += 9;
            break;
          default:
            e += 1;
        }
        r = e;
      }
      if ("\n" === u) "block" === c && (e = Va(a, e)), (n = e + k), (p = void 0);
      else {
        if (" " === u && q && " " !== q && "\n" !== q && "\t" !== q) {
          const v = a[e + 1];
          v && " " !== v && "\n" !== v && "\t" !== v && (p = e);
        }
        if (e >= n)
          if (p) m.push(p), (n = p + k), (p = void 0);
          else if ("quoted" === c) {
            for (; " " === q || "\t" === q; ) (q = u), (u = a[(e += 1)]), (d = !0);
            n = e > r + 1 ? e - 2 : f - 1;
            if (l[n]) return a;
            m.push(n);
            l[n] = !0;
            n += k;
            p = void 0;
          } else d = !0;
      }
      q = u;
    }
    d && h && h();
    if (0 === m.length) return a;
    g && g();
    g = a.slice(0, m[0]);
    for (h = 0; h < m.length; ++h)
      (k = m[h]),
        (n = m[h + 1] || a.length),
        0 === k
          ? (g = `\n${b}${a.slice(0, n)}`)
          : ("quoted" === c && l[k] && (g += `${a[k]}\\`), (g += `\n${b}${a.slice(k + 1, n)}`));
    return g;
  }
  function Va(a, b) {
    let c = a[b + 1];
    for (; " " === c || "\t" === c; ) {
      do c = a[(b += 1)];
      while (c && "\n" !== c);
      c = a[b + 1];
    }
    return b;
  }
  const Wa = (a) => ({ ia: a.ia, lineWidth: a.options.lineWidth, sa: a.options.sa });
  function Xa(a, b) {
    const c = JSON.stringify(a);
    if (b.options.nb) return c;
    const d = b.Y,
      e = b.options.ob;
    a = b.h || (/^(%|---|\.\.\.)/m.test(a) ? "  " : "");
    let f = "";
    var g = 0;
    for (let h = 0, k = c[h]; k; k = c[++h])
      if (
        (" " === k && "\\" === c[h + 1] && "n" === c[h + 2] && ((f += c.slice(g, h) + "\\ "), (g = h += 1), (k = "\\")),
        "\\" === k)
      )
        switch (c[h + 1]) {
          case "u":
            f += c.slice(g, h);
            g = c.substr(h + 2, 4);
            switch (g) {
              case "0000":
                f += "\\0";
                break;
              case "0007":
                f += "\\a";
                break;
              case "000b":
                f += "\\v";
                break;
              case "001b":
                f += "\\e";
                break;
              case "0085":
                f += "\\N";
                break;
              case "00a0":
                f += "\\_";
                break;
              case "2028":
                f += "\\L";
                break;
              case "2029":
                f += "\\P";
                break;
              default:
                f = "00" === g.substr(0, 2) ? f + ("\\x" + g.substr(2)) : f + c.substr(h, 6);
            }
            h += 5;
            g = h + 1;
            break;
          case "n":
            if (d || '"' === c[h + 2] || c.length < e) h += 1;
            else {
              for (f += c.slice(g, h) + "\n\n"; "\\" === c[h + 2] && "n" === c[h + 3] && '"' !== c[h + 4]; )
                (f += "\n"), (h += 2);
              f += a;
              " " === c[h + 2] && (f += "\\");
              h += 1;
              g = h + 1;
            }
            break;
          default:
            h += 1;
        }
    f = g ? f + c.slice(g) : c;
    return d ? f : Ua(f, a, "quoted", Wa(b));
  }
  function Ya(a, b) {
    if (!1 === b.options.eb || (b.Y && a.includes("\n")) || /[ \t]\n|\n[ \t]/.test(a)) return Xa(a, b);
    const c = b.h || (/^(%|---|\.\.\.)/m.test(a) ? "  " : "");
    a = "'" + a.replace(/'/g, "''").replace(/\n+/g, `$&\n${c}`) + "'";
    return b.Y ? a : Ua(a, c, "flow", Wa(b));
  }
  function Za(a, b) {
    const c = b.options.eb;
    let d;
    if (!1 === c) d = Xa;
    else {
      const e = a.includes('"'),
        f = a.includes("'");
      e && !f ? (d = Ya) : f && !e ? (d = Xa) : (d = c ? Ya : Xa);
    }
    return d(a, b);
  }
  function $a({ g: a, type: b, value: c }, d, e, f) {
    var g = d.options.kb;
    const h = d.options.$;
    var k = d.options.lineWidth;
    if (!g || /\n[\t ]+$/.test(c) || /^\s*$/.test(c)) return Za(c, d);
    const m = d.h || (d.Ta || /^(%|---|\.\.\.)/m.test(c) ? "  " : "");
    if ("literal" === g) b = !0;
    else if ("folded" === g || "BLOCK_FOLDED" === b) b = !1;
    else if ("BLOCK_LITERAL" === b) b = !0;
    else {
      a: if (!k || 0 > k) b = !1;
      else if (((b = k - m.length), (g = c.length), g <= b)) b = !1;
      else {
        for (let p = 0, q = 0; p < g; ++p)
          if ("\n" === c[p]) {
            if (p - q > b) break;
            q = p + 1;
            if (g - q <= b) {
              b = !1;
              break a;
            }
          }
        b = !0;
      }
      b = !b;
    }
    if (!c) return b ? "|\n" : ">\n";
    for (g = c.length; 0 < g && ((k = c[g - 1]), "\n" === k || "\t" === k || " " === k); --g);
    g = c.substring(g);
    k = g.indexOf("\n");
    -1 === k ? (k = "-") : c === g || k !== g.length - 1 ? ((k = "+"), f && f()) : (k = "");
    g &&
      ((c = c.slice(0, -g.length)),
      "\n" === g[g.length - 1] && (g = g.slice(0, -1)),
      (g = g.replace(/\n+(?!\n|$)/g, `$&${m}`)));
    f = !1;
    var l;
    let n = -1;
    for (l = 0; l < c.length; ++l) {
      const p = c[l];
      if (" " === p) f = !0;
      else if ("\n" === p) n = l;
      else break;
    }
    if ((l = c.substring(0, n < l ? n + 1 : l))) (c = c.substring(l.length)), (l = l.replace(/\n+/g, `$&${m}`));
    f = (b ? "|" : ">") + (f ? (m ? "2" : "1") : "") + k;
    a && ((f += " " + h(a.replace(/ ?[\r\n]+/g, " "))), e && e());
    if (b) return (c = c.replace(/\n+/g, `$&${m}`)), `${f}\n${m}${l}${c}${g}`;
    c = c
      .replace(/\n+/g, "\n$&")
      .replace(/(?:^|\n)([\t ].*)(?:([\n\t ]*)\n(?![\n\t ]))?/g, "$1$2")
      .replace(/\n+/g, `$&${m}`);
    a = Ua(`${l}${c}${g}`, m, "block", Wa(d));
    return `${f}\n${m}${a}`;
  }
  function ab(a, b, c, d) {
    const e = a.type,
      f = a.value,
      g = b.hb,
      h = b.Y,
      k = b.h,
      m = b.Ga,
      l = b.K;
    if ((h && /[\n[\]{},]/.test(f)) || (l && /[[\]{},]/.test(f))) return Za(f, b);
    if (!f || /^[\n\t ,[\]{}#&*!|>'"%@`]|^[?-]$|^[?-][ \t]|[\n:][ \t]|[ \t]\n|[\n\t ]#|[\n\t :]$/.test(f))
      return h || l || !f.includes("\n") ? Za(f, b) : $a(a, b, c, d);
    if (!h && !l && "PLAIN" !== e && f.includes("\n")) return $a(a, b, c, d);
    if (/^(%|---|\.\.\.)/m.test(f)) {
      if ("" === k) return (b.Ta = !0), $a(a, b, c, d);
      if (h && k === m) return Za(f, b);
    }
    const n = f.replace(/\n+/g, `$&\n${k}`);
    return g &&
      ((a = (p) => {
        let q;
        return p.default && "tag:yaml.org,2002:str" !== p.tag && (null == (q = p.test) ? void 0 : q.test(n));
      }),
      (c = b.A.m.W),
      b.A.m.G.some(a) || (null == c ? 0 : c.some(a)))
      ? Za(f, b)
      : h
      ? n
      : Ua(n, k, "flow", Wa(b));
  }
  function bb(a, b, c, d) {
    const e = b.Y,
      f = b.K,
      g = "string" === typeof a.value ? a : Object.assign({}, a, { value: String(a.value) });
    var { type: h } = a;
    "QUOTE_DOUBLE" !== h && /[\x00-\x08\x0b-\x1f\x7f-\x9f\u{D800}-\u{DFFF}]/u.test(g.value) && (h = "QUOTE_DOUBLE");
    a = (m) => {
      switch (m) {
        case "BLOCK_FOLDED":
        case "BLOCK_LITERAL":
          return e || f ? Za(g.value, b) : $a(g, b, c, d);
        case "QUOTE_DOUBLE":
          return Xa(g.value, b);
        case "QUOTE_SINGLE":
          return Ya(g.value, b);
        case "PLAIN":
          return ab(g, b, c, d);
        default:
          return null;
      }
    };
    h = a(h);
    if (null === h) {
      h = b.options.lb;
      var k = b.options.mb;
      k = (e && h) || k;
      h = a(k);
      if (null === h) throw Error(`Unsupported default string type ${k}`);
    }
    return h;
  }
  function cb(a, b) {
    b = Object.assign(
      {
        kb: !0,
        $: Ta,
        lb: null,
        mb: "PLAIN",
        s: null,
        nb: !1,
        ob: 40,
        Sa: "false",
        Fa: !0,
        sb: !0,
        lineWidth: 80,
        sa: 20,
        ub: "null",
        wb: !1,
        eb: null,
        fb: "true",
        zb: !0,
      },
      a.m.xb,
      b
    );
    let c;
    switch (b.Jb) {
      case "block":
        c = !1;
        break;
      case "flow":
        c = !0;
        break;
      default:
        c = null;
    }
    return {
      anchors: new Set(),
      A: a,
      Fa: b.Fa ? " " : "",
      h: "",
      Ga: "number" === typeof b.h ? " ".repeat(b.h) : "  ",
      K: c,
      options: b,
    };
  }
  function db(a, b) {
    if (b.tag) {
      var c = a.filter((f) => f.tag === b.tag);
      if (0 < c.length) {
        let f;
        return null != (f = c.find((g) => g.format === b.format)) ? f : c[0];
      }
    }
    c = void 0;
    let d;
    if (G(b)) {
      d = b.value;
      a = a.filter((f) => {
        let g;
        return null == (g = f.B) ? void 0 : g.call(f, d);
      });
      var e;
      c = null != (e = a.find((f) => f.format === b.format)) ? e : a.find((f) => !f.format);
    } else (d = b), (c = a.find((f) => f.ka && d instanceof f.ka));
    if (!c) {
      let f, g, h;
      e = null != (h = null == (f = d) ? void 0 : null == (g = f.constructor) ? void 0 : g.name) ? h : typeof d;
      throw Error(`Tag not resolved for ${e} value`);
    }
    return c;
  }
  function eb(a, b, { anchors: c, A: d }) {
    if (!d.s) return "";
    const e = [],
      f = (G(a) || H(a)) && a.anchor;
    f && Ia(f) && (c.add(f), e.push(`&${f}`));
    (a = a.tag ? a.tag : b.default ? null : b.tag) && e.push(Ea(d.s, a));
    return e.join(" ");
  }
  function O(a, b, c, d) {
    if (F(a)) return a.toString(b, c, d);
    if (ta(a)) {
      if (b.A.s) return a.toString(b);
      var e;
      if (null == (e = b.La) ? 0 : e.has(a))
        throw new TypeError("Cannot stringify circular structure without alias nodes");
      b.La ? b.La.add(a) : (b.La = new Set([a]));
      a = a.resolve(b.A);
    }
    let f = void 0;
    a = I(a) ? a : b.A.createNode(a, { ta: (g) => (f = g) });
    f || (f = db(b.A.m.G, a));
    e = eb(a, f, b);
    if (0 < e.length) {
      let g;
      b.ia = (null != (g = b.ia) ? g : 0) + e.length + 1;
    }
    c = "function" === typeof f.stringify ? f.stringify(a, b, c, d) : G(a) ? bb(a, b, c, d) : a.toString(b, c, d);
    return e ? (G(a) || "{" === c[0] || "[" === c[0] ? `${e} ${c}` : `${e}\n${b.h}${c}`) : c;
  }
  function fb({ key: a, value: b }, c, d, e) {
    const {
      ma: f,
      A: g,
      h,
      Ga: k,
      options: { $: m, sb: l, wb: n },
    } = c;
    var p = (I(a) && a.g) || null;
    if (n) {
      if (p) throw Error("With simple keys, key nodes cannot have comments");
      if (H(a)) throw Error("With simple keys, collection cannot be used as a key value");
    }
    var q =
      !n &&
      (!a ||
        (p && null == b && !c.K) ||
        H(a) ||
        (G(a) ? "BLOCK_FOLDED" === a.type || "BLOCK_LITERAL" === a.type : "object" === typeof a));
    c = Object.assign({}, c, { ma: !1, Y: !q && (n || !f), h: h + k });
    let r = !1,
      u = !1;
    a = O(
      a,
      c,
      () => (r = !0),
      () => (u = !0)
    );
    if (!q && !c.K && 1024 < a.length) {
      if (n) throw Error("With simple keys, single line scalar must not span more than 1024 characters");
      q = !0;
    }
    if (c.K) {
      if (f || null == b) return r && d && d(), "" === a ? "?" : q ? `? ${a}` : a;
    } else if ((f && !n) || (null == b && q))
      return (a = `? ${a}`), p && !r ? (a += N(a, c.h, m(p))) : u && e && e(), a;
    r && (p = null);
    q ? (p && (a += N(a, c.h, m(p))), (a = `? ${a}\n${h}:`)) : ((a = `${a}:`), p && (a += N(a, c.h, m(p))));
    let v;
    if (I(b)) {
      var w = !!b.N;
      var z = b.D;
      v = b.g;
    } else (w = !1), (v = z = null), b && "object" === typeof b && (b = g.createNode(b));
    c.Y = !1;
    q || p || !G(b) || (c.ia = a.length + 1);
    u = !1;
    l || !(2 <= k.length) || c.K || q || !wa(b) || b.M || b.tag || b.anchor || (c.h = c.h.substring(2));
    let C = !1;
    const t = O(
      b,
      c,
      () => (C = !0),
      () => (u = !0)
    );
    let x = " ";
    if (p || w || z)
      (x = w ? "\n" : ""),
        z && ((b = m(z)), (x += `\n${M(b, c.h)}`)),
        "" !== t || c.K ? (x += `\n${c.h}`) : "\n" === x && (x = "\n\n");
    else if (!q && H(b)) {
      p = t[0];
      q = t.indexOf("\n");
      w = -1 !== q;
      var E;
      let Qb;
      z = null != (Qb = null != (E = c.K) ? E : b.M) ? Qb : 0 === b.items.length;
      if (w || !z)
        (b = !1),
          w &&
            ("&" === p || "!" === p) &&
            ((E = t.indexOf(" ")),
            "&" === p && -1 !== E && E < q && "!" === t[E + 1] && (E = t.indexOf(" ", E + 1)),
            -1 === E || q < E) &&
            (b = !0),
          b || (x = `\n${c.h}`);
    } else if ("" === t || "\n" === t[0]) x = "";
    a += x + t;
    c.K ? C && d && d() : v && !C ? (a += N(a, c.h, m(v))) : u && e && e();
    return a;
  }
  function gb(a, b, { key: c, value: d }) {
    if ((null == a ? 0 : a.A.m.ja) && ("<<" === c || (G(c) && "<<" === c.value && (!c.type || "PLAIN" === c.type))))
      if (((d = ta(d) ? d.resolve(a.A) : d), wa(d))) for (var e of d.items) hb(a, b, e);
      else if (Array.isArray(d)) for (const g of d) hb(a, b, g);
      else hb(a, b, d);
    else if (((e = K(c, "", a)), b instanceof Map)) b.set(e, K(d, e, a));
    else if (b instanceof Set) b.add(e);
    else {
      if (null === e) c = "";
      else if ("object" !== typeof e) c = String(e);
      else if (I(c) && a && a.A) {
        e = cb(a.A, {});
        e.anchors = new Set();
        for (var f of a.anchors.keys()) e.anchors.add(f.anchor);
        e.K = !0;
        e.Pb = !0;
        f = c.toString(e);
        if (!a.Za) {
          e = JSON.stringify(f);
          40 < e.length && (e = e.substring(0, 36) + '..."');
          c = a.A.options.Ya;
          e = `Keys with collection values will be stringified due to JS Object restrictions: ${e}. Set mapAsMap: true to use object keys.`;
          if ("debug" === c || "warn" === c)
            "undefined" !== typeof process && process.pb ? process.pb(e) : console.warn(e);
          a.Za = !0;
        }
        c = f;
      } else c = JSON.stringify(e);
      a = K(d, c, a);
      c in b ? Object.defineProperty(b, c, { value: a, writable: !0, enumerable: !0, configurable: !0 }) : (b[c] = a);
    }
    return b;
  }
  function hb(a, b, c) {
    c = a && ta(c) ? c.resolve(a.A) : c;
    if (!va(c)) throw Error("Merge sources must be maps or map aliases");
    a = c.toJSON(null, a, Map);
    for (const [d, e] of a)
      b instanceof Map
        ? b.has(d) || b.set(d, e)
        : b instanceof Set
        ? b.add(d)
        : Object.prototype.hasOwnProperty.call(b, d) ||
          Object.defineProperty(b, d, { value: e, writable: !0, enumerable: !0, configurable: !0 });
  }
  function ib(a, b, c) {
    a = Pa(a, void 0, c);
    b = Pa(b, void 0, c);
    return new P(a, b);
  }
  class P {
    constructor(a, b = null) {
      Object.defineProperty(this, D, { value: ra });
      this.key = a;
      this.value = b;
    }
    clone(a) {
      let { key: b, value: c } = this;
      I(b) && (b = b.clone(a));
      I(c) && (c = c.clone(a));
      return new P(b, c);
    }
    toJSON(a, b) {
      return gb(b, (null == b ? 0 : b.ra) ? new Map() : {}, this);
    }
    toString(a, b, c) {
      return (null == a ? 0 : a.A) ? fb(this, a, b, c) : JSON.stringify(this);
    }
  }
  function jb(a, b, c) {
    let d;
    return ((null != (d = b.K) ? d : a.M) ? kb : lb)(a, b, c);
  }
  function lb({ g: a, items: b }, c, { Pa: d, Ea: e, Ha: f, bb: g, Ia: h }) {
    const {
        h: k,
        options: { $: m },
      } = c,
      l = Object.assign({}, c, { h: f, type: null });
    let n = !1;
    const p = [];
    for (let r = 0; r < b.length; ++r) {
      var q = b[r];
      let u = null;
      if (I(q)) !n && q.N && p.push(""), mb(c, p, q.D, n), q.g && (u = q.g);
      else if (F(q)) {
        const v = I(q.key) ? q.key : null;
        v && (!n && v.N && p.push(""), mb(c, p, v.D, n));
      }
      n = !1;
      q = O(
        q,
        l,
        () => (u = null),
        () => (n = !0)
      );
      u && (q += N(q, f, m(u)));
      n && u && (n = !1);
      p.push(d + q);
    }
    if (0 === p.length) b = e.start + e.end;
    else for (b = p[0], c = 1; c < p.length; ++c) (d = p[c]), (b += d ? `\n${k}${d}` : "\n");
    a ? ((b += "\n" + M(m(a), k)), h && h()) : n && g && g();
    return b;
  }
  function kb({ g: a, items: b }, c, { Ea: d, Ha: e, Ia: f }) {
    const {
      h: g,
      Ga: h,
      Fa: k,
      options: { $: m },
    } = c;
    e += h;
    const l = Object.assign({}, c, { h: e, K: !0, type: null });
    var n = !1;
    let p = 0;
    const q = [];
    for (let u = 0; u < b.length; ++u) {
      var r = b[u];
      let v = null;
      if (I(r)) r.N && q.push(""), mb(c, q, r.D, !1), r.g && (v = r.g);
      else if (F(r)) {
        const w = I(r.key) ? r.key : null;
        w && (w.N && q.push(""), mb(c, q, w.D, !1), w.g && (n = !0));
        const z = I(r.value) ? r.value : null;
        z ? (z.g && (v = z.g), z.D && (n = !0)) : null == r.value && w && w.g && (v = w.g);
      }
      v && (n = !0);
      r = O(r, l, () => (v = null));
      u < b.length - 1 && (r += ",");
      v && (r += N(r, e, m(v)));
      !n && (q.length > p || r.includes("\n")) && (n = !0);
      q.push(r);
      p = q.length;
    }
    b = d.start;
    d = d.end;
    if (0 === q.length) n = b + d;
    else if ((n || (n = 600 < q.reduce((u, v) => u + v.length + 2, 2)), n)) {
      n = b;
      for (const u of q) n += u ? `\n${h}${g}${u}` : "\n";
      n += `\n${g}${d}`;
    } else n = `${b}${k}${q.join(" ")}${k}${d}`;
    a && ((n += N(n, m(a), g)), f && f());
    return n;
  }
  function mb({ h: a, options: { $: b } }, c, d, e) {
    d && e && (d = d.replace(/^\n+/, ""));
    d && ((a = M(b(d), a)), c.push(a.trimStart()));
  }
  function Q(a, b) {
    const c = G(b) ? b.value : b;
    for (const d of a) if (F(d) && (d.key === b || d.key === c || (G(d.key) && d.key.value === c))) return d;
  }
  class R extends Sa {
    static get tagName() {
      return "tag:yaml.org,2002:map";
    }
    constructor(a) {
      super(A, a);
      this.items = [];
    }
    add(a, b) {
      let c;
      F(a)
        ? (c = a)
        : (c =
            a && "object" === typeof a && "key" in a ? new P(a.key, a.value) : new P(a, null == a ? void 0 : a.value));
      a = Q(this.items, c.key);
      let d;
      const e = null == (d = this.m) ? void 0 : d.wa;
      if (a) {
        if (!b) throw Error(`Key ${c.key} already set`);
        G(a.value) && Na(c.value) ? (a.value.value = c.value) : (a.value = c.value);
      } else
        e
          ? ((b = this.items.findIndex((f) => 0 > e(c, f))), -1 === b ? this.items.push(c) : this.items.splice(b, 0, c))
          : this.items.push(c);
    }
    delete(a) {
      return (a = Q(this.items, a)) ? 0 < this.items.splice(this.items.indexOf(a), 1).length : !1;
    }
    get(a, b) {
      let c;
      a = null == (c = Q(this.items, a)) ? void 0 : c.value;
      let d;
      return null != (d = !b && G(a) ? a.value : a) ? d : void 0;
    }
    has(a) {
      return !!Q(this.items, a);
    }
    set(a, b) {
      this.add(new P(a, b), !0);
    }
    toJSON(a, b, c) {
      a = c ? new c() : (null == b ? 0 : b.ra) ? new Map() : {};
      (null == b ? 0 : b.R) && b.R(a);
      for (const d of this.items) gb(b, a, d);
      return a;
    }
    toString(a, b, c) {
      if (!a) return JSON.stringify(this);
      for (const d of this.items)
        if (!F(d)) throw Error(`Map items must all be pairs; found ${JSON.stringify(d)} instead`);
      !a.ma && Ra(this, !1) && (a = Object.assign({}, a, { ma: !0 }));
      return jb(this, a, { Pa: "", Ea: { start: "{", end: "}" }, Ha: a.h || "", bb: c, Ia: b });
    }
  }
  const nb = {
    T: "map",
    createNode: function (a, b, c) {
      const d = c.pa,
        e = c.ua,
        f = new R(a),
        g = (h, k) => {
          if ("function" === typeof e) k = e.call(b, h, k);
          else if (Array.isArray(e) && !e.includes(h)) return;
          (void 0 !== k || d) && f.items.push(ib(h, k, c));
        };
      if (b instanceof Map) for (const [h, k] of b) g(h, k);
      else if (b && "object" === typeof b) for (const h of Object.keys(b)) g(h, b[h]);
      "function" === typeof a.wa && f.items.sort(a.wa);
      return f;
    },
    default: !0,
    ka: R,
    tag: "tag:yaml.org,2002:map",
    resolve(a, b) {
      va(a) || b("Expected a mapping for this tag");
      return a;
    },
  };
  class ob extends Sa {
    static get tagName() {
      return "tag:yaml.org,2002:seq";
    }
    constructor(a) {
      super(sa, a);
      this.items = [];
    }
    add(a) {
      this.items.push(a);
    }
    delete(a) {
      a = pb(a);
      return "number" !== typeof a ? !1 : 0 < this.items.splice(a, 1).length;
    }
    get(a, b) {
      a = pb(a);
      if ("number" === typeof a) return (a = this.items[a]), !b && G(a) ? a.value : a;
    }
    has(a) {
      a = pb(a);
      return "number" === typeof a && a < this.items.length;
    }
    set(a, b) {
      const c = pb(a);
      if ("number" !== typeof c) throw Error(`Expected a valid index, not ${a}.`);
      a = this.items[c];
      G(a) && Na(b) ? (a.value = b) : (this.items[c] = b);
    }
    toJSON(a, b) {
      a = [];
      (null == b ? 0 : b.R) && b.R(a);
      let c = 0;
      for (const d of this.items) a.push(K(d, String(c++), b));
      return a;
    }
    toString(a, b, c) {
      return a
        ? jb(this, a, { Pa: "- ", Ea: { start: "[", end: "]" }, Ha: (a.h || "") + "  ", bb: c, Ia: b })
        : JSON.stringify(this);
    }
  }
  function pb(a) {
    (a = G(a) ? a.value : a) && "string" === typeof a && (a = Number(a));
    return "number" === typeof a && Number.isInteger(a) && 0 <= a ? a : null;
  }
  const qb = {
    T: "seq",
    createNode: function (a, b, c) {
      const d = c.ua;
      a = new ob(a);
      if (b && Symbol.iterator in Object(b)) {
        let e = 0;
        for (let f of b) {
          if ("function" === typeof d) {
            const g = b instanceof Set ? f : String(e++);
            f = d.call(b, g, f);
          }
          a.items.push(Pa(f, void 0, c));
        }
      }
      return a;
    },
    default: !0,
    ka: ob,
    tag: "tag:yaml.org,2002:seq",
    resolve(a, b) {
      wa(a) || b("Expected a sequence for this tag");
      return a;
    },
  };
  const rb = {
    B: (a) => "string" === typeof a,
    default: !0,
    tag: "tag:yaml.org,2002:str",
    resolve: (a) => a,
    stringify(a, b, c, d) {
      b = Object.assign({ hb: !0 }, b);
      return bb(a, b, c, d);
    },
  };
  const sb = {
    B: (a) => null == a,
    createNode: () => new L(null),
    default: !0,
    tag: "tag:yaml.org,2002:null",
    test: /^(?:~|[Nn]ull|NULL)?$/,
    resolve: () => new L(null),
    stringify: ({ source: a }, b) => ("string" === typeof a && sb.test.test(a) ? a : b.options.ub),
  };
  const tb = {
    B: (a) => "boolean" === typeof a,
    default: !0,
    tag: "tag:yaml.org,2002:bool",
    test: /^(?:[Tt]rue|TRUE|[Ff]alse|FALSE)$/,
    resolve: (a) => new L("t" === a[0] || "T" === a[0]),
    stringify({ source: a, value: b }, c) {
      return a && tb.test.test(a) && b === ("t" === a[0] || "T" === a[0]) ? a : b ? c.options.fb : c.options.Sa;
    },
  };
  function S({ format: a, ab: b, tag: c, value: d }) {
    if ("bigint" === typeof d) return String(d);
    const e = "number" === typeof d ? d : Number(d);
    if (!isFinite(e)) return isNaN(e) ? ".nan" : 0 > e ? "-.inf" : ".inf";
    d = JSON.stringify(d);
    if (!a && b && (!c || "tag:yaml.org,2002:float" === c) && /^\d/.test(d))
      for (a = d.indexOf("."), 0 > a && ((a = d.length), (d += ".")), b -= d.length - a - 1; 0 < b--; ) d += "0";
    return d;
  }
  const ub = {
      B: (a) => "number" === typeof a,
      default: !0,
      tag: "tag:yaml.org,2002:float",
      test: /^(?:[-+]?\.(?:inf|Inf|INF|nan|NaN|NAN))$/,
      resolve: (a) =>
        "nan" === a.slice(-3).toLowerCase() ? NaN : "-" === a[0] ? Number.NEGATIVE_INFINITY : Number.POSITIVE_INFINITY,
      stringify: S,
    },
    vb = {
      B: (a) => "number" === typeof a,
      default: !0,
      tag: "tag:yaml.org,2002:float",
      format: "EXP",
      test: /^[-+]?(?:\.[0-9]+|[0-9]+(?:\.[0-9]*)?)[eE][-+]?[0-9]+$/,
      resolve: (a) => parseFloat(a),
      stringify(a) {
        const b = Number(a.value);
        return isFinite(b) ? b.toExponential() : S(a);
      },
    },
    wb = {
      B: (a) => "number" === typeof a,
      default: !0,
      tag: "tag:yaml.org,2002:float",
      test: /^[-+]?(?:\.[0-9]+|[0-9]+\.[0-9]*)$/,
      resolve(a) {
        const b = new L(parseFloat(a)),
          c = a.indexOf(".");
        -1 !== c && "0" === a[a.length - 1] && (b.ab = a.length - c - 1);
        return b;
      },
      stringify: S,
    };
  const xb = (a) => "bigint" === typeof a || Number.isInteger(a),
    yb = (a, b, c, { na: d }) => (d ? BigInt(a) : parseInt(a.substring(b), c));
  function zb(a, b, c) {
    const d = a.value;
    return xb(d) && 0 <= d ? c + d.toString(b) : S(a);
  }
  const Ab = {
      B: (a) => xb(a) && 0 <= a,
      default: !0,
      tag: "tag:yaml.org,2002:int",
      format: "OCT",
      test: /^0o[0-7]+$/,
      resolve: (a, b, c) => yb(a, 2, 8, c),
      stringify: (a) => zb(a, 8, "0o"),
    },
    Bb = {
      B: xb,
      default: !0,
      tag: "tag:yaml.org,2002:int",
      test: /^[-+]?[0-9]+$/,
      resolve: (a, b, c) => yb(a, 0, 10, c),
      stringify: S,
    },
    Cb = {
      B: (a) => xb(a) && 0 <= a,
      default: !0,
      tag: "tag:yaml.org,2002:int",
      format: "HEX",
      test: /^0x[0-9a-fA-F]+$/,
      resolve: (a, b, c) => yb(a, 2, 16, c),
      stringify: (a) => zb(a, 16, "0x"),
    };
  function Db(a) {
    return "bigint" === typeof a || Number.isInteger(a);
  }
  const Eb = ({ value: a }) => JSON.stringify(a);
  const Fb = {
    B: (a) => a instanceof Uint8Array,
    default: !1,
    tag: "tag:yaml.org,2002:binary",
    resolve(a, b) {
      if ("function" === typeof Buffer) return Buffer.from(a, "base64");
      if ("function" === typeof atob) {
        a = atob(a.replace(/[\n\r]/g, ""));
        b = new Uint8Array(a.length);
        for (let c = 0; c < a.length; ++c) b[c] = a.charCodeAt(c);
        return b;
      }
      b("This environment does not support reading binary tags; either Buffer or atob is required");
      return a;
    },
    stringify({ g: a, type: b, value: c }, d, e, f) {
      if ("function" === typeof Buffer)
        c = c instanceof Buffer ? c.toString("base64") : Buffer.from(c.buffer).toString("base64");
      else if ("function" === typeof btoa) {
        var g = "";
        for (var h = 0; h < c.length; ++h) g += String.fromCharCode(c[h]);
        c = btoa(g);
      } else throw Error("This environment does not support writing binary tags; either Buffer or btoa is required");
      b || (b = "BLOCK_LITERAL");
      if ("QUOTE_DOUBLE" !== b) {
        g = Math.max(d.options.lineWidth - d.h.length, d.options.sa);
        h = Math.ceil(c.length / g);
        const k = Array(h);
        for (let m = 0, l = 0; m < h; ++m, l += g) k[m] = c.substr(l, g);
        c = k.join("BLOCK_LITERAL" === b ? "\n" : " ");
      }
      return bb({ g: a, type: b, value: c }, d, e, f);
    },
  };
  function Gb(a, b) {
    if (wa(a))
      for (let c = 0; c < a.items.length; ++c) {
        let d = a.items[c];
        if (!F(d)) {
          if (va(d)) {
            1 < d.items.length && b("Each pair must have its own sequence indicator");
            const e = d.items[0] || new P(new L(null));
            d.D && (e.key.D = e.key.D ? `${d.D}\n${e.key.D}` : d.D);
            if (d.g) {
              let f;
              const g = null != (f = e.value) ? f : e.key;
              g.g = g.g ? `${d.g}\n${g.g}` : d.g;
            }
            d = e;
          }
          a.items[c] = F(d) ? d : new P(d);
        }
      }
    else b("Expected a sequence for this tag");
    return a;
  }
  function Hb(a, b, c) {
    const d = c.ua;
    a = new ob(a);
    a.tag = "tag:yaml.org,2002:pairs";
    let e = 0;
    if (b && Symbol.iterator in Object(b))
      for (let g of b) {
        "function" === typeof d && (g = d.call(b, String(e++), g));
        let h;
        var f = void 0;
        if (Array.isArray(g))
          if (2 === g.length) (h = g[0]), (f = g[1]);
          else throw new TypeError(`Expected [key, value] tuple: ${g}`);
        else if (g && g instanceof Object)
          if (((f = Object.keys(g)), 1 === f.length)) (h = f[0]), (f = g[h]);
          else throw new TypeError(`Expected { key: value } tuple: ${g}`);
        else h = g;
        a.items.push(ib(h, f, c));
      }
    return a;
  }
  const Ib = { T: "seq", default: !1, tag: "tag:yaml.org,2002:pairs", resolve: Gb, createNode: Hb };
  class Jb extends ob {
    constructor() {
      super();
      this.add = R.prototype.add.bind(this);
      this.delete = R.prototype.delete.bind(this);
      this.get = R.prototype.get.bind(this);
      this.has = R.prototype.has.bind(this);
      this.set = R.prototype.set.bind(this);
      this.tag = Kb;
    }
    toJSON(a, b) {
      if (!b) return super.toJSON(a);
      a = new Map();
      (null == b ? 0 : b.R) && b.R(a);
      for (const c of this.items) {
        let d, e;
        F(c) ? ((d = K(c.key, "", b)), (e = K(c.value, d, b))) : (d = K(c, "", b));
        if (a.has(d)) throw Error("Ordered maps must not include duplicate keys");
        a.set(d, e);
      }
      return a;
    }
  }
  var Kb = "tag:yaml.org,2002:omap";
  const Lb = {
    T: "seq",
    B: (a) => a instanceof Map,
    ka: Jb,
    default: !1,
    tag: "tag:yaml.org,2002:omap",
    resolve(a, b) {
      a = Gb(a, b);
      const c = [];
      for (const { key: d } of a.items)
        G(d) && (c.includes(d.value) ? b(`Ordered maps must not include duplicate keys: ${d.value}`) : c.push(d.value));
      return Object.assign(new Jb(), a);
    },
    createNode(a, b, c) {
      a = Hb(a, b, c);
      b = new Jb();
      b.items = a.items;
      return b;
    },
  };
  function Mb({ value: a, source: b }, c) {
    const d = a ? Nb : Ob;
    return b && d.test.test(b) ? b : a ? c.options.fb : c.options.Sa;
  }
  const Nb = {
      B: (a) => !0 === a,
      default: !0,
      tag: "tag:yaml.org,2002:bool",
      test: /^(?:Y|y|[Yy]es|YES|[Tt]rue|TRUE|[Oo]n|ON)$/,
      resolve: () => new L(!0),
      stringify: Mb,
    },
    Ob = {
      B: (a) => !1 === a,
      default: !0,
      tag: "tag:yaml.org,2002:bool",
      test: /^(?:N|n|[Nn]o|NO|[Ff]alse|FALSE|[Oo]ff|OFF)$/i,
      resolve: () => new L(!1),
      stringify: Mb,
    };
  const Pb = (a) => "bigint" === typeof a || Number.isInteger(a);
  function Rb(a, b, c, { na: d }) {
    const e = a[0];
    if ("-" === e || "+" === e) b += 1;
    a = a.substring(b).replace(/_/g, "");
    if (d) {
      switch (c) {
        case 2:
          a = `0b${a}`;
          break;
        case 8:
          a = `0o${a}`;
          break;
        case 16:
          a = `0x${a}`;
      }
      a = BigInt(a);
      return "-" === e ? BigInt(-1) * a : a;
    }
    a = parseInt(a, c);
    return "-" === e ? -1 * a : a;
  }
  function Sb(a, b, c) {
    const d = a.value;
    return Pb(d) ? ((a = d.toString(b)), 0 > d ? "-" + c + a.substr(1) : c + a) : S(a);
  }
  class Tb extends R {
    constructor(a) {
      super(a);
      this.tag = Ub;
    }
    add(a) {
      let b;
      F(a)
        ? (b = a)
        : (b =
            a && "object" === typeof a && "key" in a && "value" in a && null === a.value
              ? new P(a.key, null)
              : new P(a, null));
      Q(this.items, b.key) || this.items.push(b);
    }
    get(a, b) {
      a = Q(this.items, a);
      return !b && F(a) ? (G(a.key) ? a.key.value : a.key) : a;
    }
    set(a, b) {
      if ("boolean" !== typeof b)
        throw Error(`Expected boolean value for set(key, value) in a YAML set, not ${typeof b}`);
      const c = Q(this.items, a);
      c && !b ? this.items.splice(this.items.indexOf(c), 1) : !c && b && this.items.push(new P(a));
    }
    toJSON(a, b) {
      return super.toJSON(a, b, Set);
    }
    toString(a, b, c) {
      if (!a) return JSON.stringify(this);
      if (Ra(this, !0)) return super.toString(Object.assign({}, a, { ma: !0 }), b, c);
      throw Error("Set items must all have null values");
    }
  }
  var Ub = "tag:yaml.org,2002:set";
  const Vb = {
    T: "map",
    B: (a) => a instanceof Set,
    ka: Tb,
    default: !1,
    tag: "tag:yaml.org,2002:set",
    resolve(a, b) {
      if (va(a)) {
        if (Ra(a, !0)) return Object.assign(new Tb(), a);
        b("Set items must all have null values");
      } else b("Expected a mapping for this tag");
      return a;
    },
    createNode(a, b, c) {
      const d = c.ua;
      a = new Tb(a);
      if (b && Symbol.iterator in Object(b))
        for (let e of b) "function" === typeof d && (e = d.call(b, e, e)), a.items.push(ib(e, null, c));
      return a;
    },
  };
  function Wb(a, b) {
    const c = a[0];
    a = ("-" === c || "+" === c ? a.substring(1) : a)
      .replace(/_/g, "")
      .split(":")
      .reduce((d, e) => d * (b ? BigInt(60) : Number(60)) + (b ? BigInt(e) : Number(e)), b ? BigInt(0) : Number(0));
    return "-" === c ? (b ? BigInt(-1) : Number(-1)) * a : a;
  }
  function Xb(a) {
    let { value: b } = a;
    var c = (e) => e;
    if ("bigint" === typeof b) c = (e) => BigInt(e);
    else if (isNaN(b) || !isFinite(b)) return S(a);
    a = "";
    0 > b && ((a = "-"), (b *= c(-1)));
    c = c(60);
    const d = [b % c];
    60 > b ? d.unshift(0) : ((b = (b - d[0]) / c), d.unshift(b % c), 60 <= b && ((b = (b - d[0]) / c), d.unshift(b)));
    return (
      a +
      d
        .map((e) => (10 > e ? "0" + String(e) : String(e)))
        .join(":")
        .replace(/000000\d*$/, "")
    );
  }
  const Yb = {
      B: (a) => "bigint" === typeof a || Number.isInteger(a),
      default: !0,
      tag: "tag:yaml.org,2002:int",
      format: "TIME",
      test: /^[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+$/,
      resolve: (a, b, { na: c }) => Wb(a, c),
      stringify: Xb,
    },
    Zb = {
      B: (a) => "number" === typeof a,
      default: !0,
      tag: "tag:yaml.org,2002:float",
      format: "TIME",
      test: /^[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\.[0-9_]*$/,
      resolve: (a) => Wb(a, !1),
      stringify: Xb,
    },
    $b = {
      B: (a) => a instanceof Date,
      default: !0,
      tag: "tag:yaml.org,2002:timestamp",
      test: RegExp(
        "^([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})(?:(?:t|T|[ \\t]+)([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2}(\\.[0-9]+)?)(?:[ \\t]*(Z|[-+][012]?[0-9](?::[0-9]{2})?))?)?$"
      ),
      resolve(a) {
        var b = a.match($b.test);
        if (!b) throw Error("!!timestamp expects a date, starting with yyyy-mm-dd");
        const [, c, d, e, f, g, h] = b.map(Number);
        a = Date.UTC(c, d - 1, e, f || 0, g || 0, h || 0, b[7] ? Number((b[7] + "00").substr(1, 3)) : 0);
        (b = b[8]) && "Z" !== b && ((b = Wb(b, !1)), 30 > Math.abs(b) && (b *= 60), (a -= 6e4 * b));
        return new Date(a);
      },
      stringify: ({ value: a }) => a.toISOString().replace(/((T00:00)?:00)?\.000Z$/, ""),
    };
  const ac = [
    nb,
    qb,
    rb,
    sb,
    Nb,
    Ob,
    {
      B: Pb,
      default: !0,
      tag: "tag:yaml.org,2002:int",
      format: "BIN",
      test: /^[-+]?0b[0-1_]+$/,
      resolve: (a, b, c) => Rb(a, 2, 2, c),
      stringify: (a) => Sb(a, 2, "0b"),
    },
    {
      B: Pb,
      default: !0,
      tag: "tag:yaml.org,2002:int",
      format: "OCT",
      test: /^[-+]?0[0-7_]+$/,
      resolve: (a, b, c) => Rb(a, 1, 8, c),
      stringify: (a) => Sb(a, 8, "0"),
    },
    {
      B: Pb,
      default: !0,
      tag: "tag:yaml.org,2002:int",
      test: /^[-+]?[0-9][0-9_]*$/,
      resolve: (a, b, c) => Rb(a, 0, 10, c),
      stringify: S,
    },
    {
      B: Pb,
      default: !0,
      tag: "tag:yaml.org,2002:int",
      format: "HEX",
      test: /^[-+]?0x[0-9a-fA-F_]+$/,
      resolve: (a, b, c) => Rb(a, 2, 16, c),
      stringify: (a) => Sb(a, 16, "0x"),
    },
    {
      B: (a) => "number" === typeof a,
      default: !0,
      tag: "tag:yaml.org,2002:float",
      test: /^[-+]?\.(?:inf|Inf|INF|nan|NaN|NAN)$/,
      resolve: (a) =>
        "nan" === a.slice(-3).toLowerCase() ? NaN : "-" === a[0] ? Number.NEGATIVE_INFINITY : Number.POSITIVE_INFINITY,
      stringify: S,
    },
    {
      B: (a) => "number" === typeof a,
      default: !0,
      tag: "tag:yaml.org,2002:float",
      format: "EXP",
      test: /^[-+]?(?:[0-9][0-9_]*)?(?:\.[0-9_]*)?[eE][-+]?[0-9]+$/,
      resolve: (a) => parseFloat(a.replace(/_/g, "")),
      stringify(a) {
        const b = Number(a.value);
        return isFinite(b) ? b.toExponential() : S(a);
      },
    },
    {
      B: (a) => "number" === typeof a,
      default: !0,
      tag: "tag:yaml.org,2002:float",
      test: /^[-+]?(?:[0-9][0-9_]*)?\.[0-9_]*$/,
      resolve(a) {
        const b = new L(parseFloat(a.replace(/_/g, ""))),
          c = a.indexOf(".");
        -1 !== c && ((a = a.substring(c + 1).replace(/_/g, "")), "0" === a[a.length - 1] && (b.ab = a.length));
        return b;
      },
      stringify: S,
    },
    Fb,
    Lb,
    Ib,
    Vb,
    Yb,
    Zb,
    $b,
  ];
  const bc = new Map([
      ["core", [nb, qb, rb, sb, tb, Ab, Bb, Cb, ub, vb, wb]],
      ["failsafe", [nb, qb, rb]],
      [
        "json",
        [nb, qb].concat(
          [
            {
              B: (a) => "string" === typeof a,
              default: !0,
              tag: "tag:yaml.org,2002:str",
              resolve: (a) => a,
              stringify: Eb,
            },
            {
              B: (a) => null == a,
              createNode: () => new L(null),
              default: !0,
              tag: "tag:yaml.org,2002:null",
              test: /^null$/,
              resolve: () => null,
              stringify: Eb,
            },
            {
              B: (a) => "boolean" === typeof a,
              default: !0,
              tag: "tag:yaml.org,2002:bool",
              test: /^true|false$/,
              resolve: (a) => "true" === a,
              stringify: Eb,
            },
            {
              B: Db,
              default: !0,
              tag: "tag:yaml.org,2002:int",
              test: /^-?(?:0|[1-9][0-9]*)$/,
              resolve: (a, b, { na: c }) => (c ? BigInt(a) : parseInt(a, 10)),
              stringify: ({ value: a }) => (Db(a) ? a.toString() : JSON.stringify(a)),
            },
            {
              B: (a) => "number" === typeof a,
              default: !0,
              tag: "tag:yaml.org,2002:float",
              test: /^-?(?:0|[1-9][0-9]*)(?:\.[0-9]*)?(?:[eE][-+]?[0-9]+)?$/,
              resolve: (a) => parseFloat(a),
              stringify: Eb,
            },
          ],
          {
            default: !0,
            tag: "",
            test: /^/,
            resolve(a, b) {
              b(`Unresolved plain scalar ${JSON.stringify(a)}`);
              return a;
            },
          }
        ),
      ],
      ["yaml11", ac],
      ["yaml-1.1", ac],
    ]),
    cc = {
      Hb: Fb,
      Ib: tb,
      Lb: wb,
      Mb: vb,
      Nb: ub,
      Ob: Zb,
      Qb: Bb,
      Rb: Cb,
      Sb: Ab,
      Tb: Yb,
      map: nb,
      Ub: sb,
      Vb: Lb,
      Yb: Ib,
      $b: qb,
      set: Vb,
      timestamp: $b,
    },
    dc = {
      "tag:yaml.org,2002:binary": Fb,
      "tag:yaml.org,2002:omap": Lb,
      "tag:yaml.org,2002:pairs": Ib,
      "tag:yaml.org,2002:set": Vb,
      "tag:yaml.org,2002:timestamp": $b,
    };
  function ec(a, b) {
    let c = bc.get(b);
    if (!c)
      if (Array.isArray(a)) c = [];
      else
        throw (
          ((a = Array.from(bc.keys())
            .filter((d) => "yaml11" !== d)
            .map((d) => JSON.stringify(d))
            .join(", ")),
          Error(`Unknown schema "${b}"; use one of ${a} or define customTags array`))
        );
    if (Array.isArray(a)) for (const d of a) c = c.concat(d);
    else "function" === typeof a && (c = a(c.slice()));
    return c.map((d) => {
      if ("string" !== typeof d) return d;
      var e = cc[d];
      if (e) return e;
      e = Object.keys(cc)
        .map((f) => JSON.stringify(f))
        .join(", ");
      throw Error(`Unknown custom tag "${d}"; use one of ${e}`);
    });
  }
  const fc = (a, b) => (a.key < b.key ? -1 : a.key > b.key ? 1 : 0);
  class gc {
    constructor({ W: a, Kb: b, ja: c, cb: d, m: e, wa: f, ac: g }) {
      this.W = Array.isArray(a) ? ec(a, "compat") : a ? ec(null, a) : null;
      this.ja = !!c;
      this.name = ("string" === typeof e && e) || "core";
      this.Wa = d ? dc : {};
      this.G = ec(b, this.name);
      this.xb = null != g ? g : null;
      Object.defineProperty(this, A, { value: nb });
      Object.defineProperty(this, B, { value: rb });
      Object.defineProperty(this, sa, { value: qb });
      this.wa = "function" === typeof f ? f : !0 === f ? fc : null;
    }
    clone() {
      const a = Object.create(gc.prototype, Object.getOwnPropertyDescriptors(this));
      a.G = this.G.slice();
      return a;
    }
  }
  function hc(a, b) {
    const c = [];
    var d = !0 === b.s;
    if (!1 !== b.s && a.s) {
      var e = a.s.toString(a);
      e ? (c.push(e), (d = !0)) : a.s.aa && (d = !0);
    }
    d && c.push("---");
    e = cb(a, b);
    b = e.options.$;
    if (a.D) {
      1 !== c.length && c.unshift("");
      const k = b(a.D);
      c.unshift(M(k, ""));
    }
    let f = !1,
      g = null;
    a.v
      ? (I(a.v) && (a.v.N && d && c.push(""), a.v.D && ((d = b(a.v.D)), c.push(M(d, ""))), (e.Ta = !!a.g), (g = a.v.g)),
        (d = O(a.v, e, () => (g = null), g ? void 0 : () => (f = !0))),
        g && (d += N(d, "", b(g))),
        ("|" !== d[0] && ">" !== d[0]) || "---" !== c[c.length - 1] ? c.push(d) : (c[c.length - 1] = `--- ${d}`))
      : c.push(O(a.v, e));
    let h;
    (null == (h = a.s) ? 0 : h.Ra)
      ? a.g
        ? ((a = b(a.g)), a.includes("\n") ? (c.push("..."), c.push(M(a, ""))) : c.push(`... ${a}`))
        : c.push("...")
      : ((a = a.g) && f && (a = a.replace(/^\n+/, "")),
        a && ((f && !g) || "" === c[c.length - 1] || c.push(""), c.push(M(b(a), ""))));
    return c.join("\n") + "\n";
  }
  function ic(a, b, c, d) {
    if (d && "object" === typeof d)
      if (Array.isArray(d))
        for (let g = 0, h = d.length; g < h; ++g) {
          var e = d[g],
            f = ic(a, d, String(g), e);
          void 0 === f ? delete d[g] : f !== e && (d[g] = f);
        }
      else if (d instanceof Map)
        for (e of Array.from(d.keys())) {
          f = d.get(e);
          const g = ic(a, d, e, f);
          void 0 === g ? d.delete(e) : g !== f && d.set(e, g);
        }
      else if (d instanceof Set)
        for (f of Array.from(d)) (e = ic(a, d, f, f)), void 0 === e ? d.delete(f) : e !== f && (d.delete(f), d.add(e));
      else
        for (const [g, h] of Object.entries(d))
          (e = ic(a, d, g, h)), void 0 === e ? delete d[g] : e !== h && (d[g] = e);
    return a.call(b, c, d);
  }
  function jc(a, b, c = {}) {
    "number" === typeof b && (b = String(b));
    switch (b) {
      case "1.1":
        a.s ? (a.s.J.version = "1.1") : (a.s = new Fa({ version: "1.1" }));
        b = { ja: !0, cb: !1, m: "yaml-1.1" };
        break;
      case "1.2":
      case "next":
        a.s ? (a.s.J.version = b) : (a.s = new Fa({ version: b }));
        b = { ja: !1, cb: !0, m: "core" };
        break;
      case null:
        a.s && delete a.s;
        b = null;
        break;
      default:
        throw ((a = JSON.stringify(b)), Error(`Expected '1.1', '1.2' or null as first argument, but found: ${a}`));
    }
    if (c.m instanceof Object) a.m = c.m;
    else if (b) a.m = new gc(Object.assign(b, c));
    else throw Error("With a null YAML version, the { schema: Schema } option is required");
  }
  function kc(a, { json: b, tb: c, ra: d, $a: e, da: f, Zb: g } = {}) {
    b = { anchors: new Map(), A: a, Va: !b, ra: !0 === d, Za: !1, $a: "number" === typeof e ? e : 100, stringify: O };
    a = K(a.v, null != c ? c : "", b);
    if ("function" === typeof f) for (const { count: h, va: k } of b.anchors.values()) f(k, h);
    return "function" === typeof g ? ic(g, { "": a }, "", a) : a;
  }
  class lc {
    constructor(a, b, c) {
      this.g = this.D = null;
      this.errors = [];
      this.O = [];
      Object.defineProperty(this, D, { value: qa });
      let d = null;
      "function" === typeof b || Array.isArray(b) ? (d = b) : void 0 === c && b && (c = b);
      this.options = b = Object.assign({ na: !1, oa: !1, Ya: "warn", Ja: !0, V: !0, yb: !0, version: "1.2" }, c);
      ({ version: b } = b);
      var e;
      if (null == (e = c) ? 0 : e.Aa) {
        e = c.Aa;
        const f = new Fa(e.J, e.G);
        switch (e.J.version) {
          case "1.1":
            e.i = !0;
            break;
          case "1.2":
            (e.i = !1), (e.J = { X: Ga.X, version: "1.2" }), (e.G = Object.assign({}, Ha));
        }
        this.s = f;
        this.s.J.X && (b = this.s.J.version);
      } else this.s = new Fa({ version: b });
      jc(this, b, c);
      void 0 === a ? (this.v = null) : (this.v = this.createNode(a, d, c));
    }
    clone() {
      const a = Object.create(lc.prototype, { [D]: { value: qa } });
      a.D = this.D;
      a.g = this.g;
      a.errors = this.errors.slice();
      a.O = this.O.slice();
      a.options = Object.assign({}, this.options);
      this.s && (a.s = this.s.clone());
      a.m = this.m.clone();
      a.v = I(this.v) ? this.v.clone(a.m) : this.v;
      this.o && (a.o = this.o.slice());
      return a;
    }
    add(a) {
      mc(this.v) && this.v.add(a);
    }
    createNode(a, b, c) {
      var d = void 0;
      "function" === typeof b
        ? ((a = b.call({ "": a }, "", a)), (d = b))
        : Array.isArray(b)
        ? ((d = b.filter((r) => "number" === typeof r || r instanceof String || r instanceof Number).map(String)),
          0 < d.length && (b = b.concat(d)),
          (d = b))
        : void 0 === c && b && ((c = b), (b = void 0));
      let e;
      const { Ba: f, Gb: g, M: h, pa: k, ta: m, tag: l } = null != (e = c) ? e : {},
        { da: n, vb: p, xa: q } = Ka(this, g || "a");
      a = Pa(a, l, { Ba: null != f ? f : !0, pa: null != k ? k : !1, da: n, ta: m, ua: d, m: this.m, xa: q });
      h && H(a) && (a.M = !0);
      p();
      return a;
    }
    delete(a) {
      return mc(this.v) ? this.v.delete(a) : !1;
    }
    get(a, b) {
      return H(this.v) ? this.v.get(a, b) : void 0;
    }
    has(a) {
      return H(this.v) ? this.v.has(a) : !1;
    }
    set(a, b) {
      null == this.v ? (this.v = Qa(this.m, [a], b)) : mc(this.v) && this.v.set(a, b);
    }
    toJSON(a, b) {
      return kc(this, { json: !0, tb: a, ra: !1, da: b });
    }
    toString(a = {}) {
      if (0 < this.errors.length) throw Error("Document with errors cannot be stringified");
      if ("indent" in a && (!Number.isInteger(a.h) || 0 >= Number(a.h)))
        throw ((a = JSON.stringify(a.h)), Error(`"indent" option must be a positive integer, not ${a}`));
      return hc(this, a);
    }
  }
  function mc(a) {
    if (H(a)) return !0;
    throw Error("Expected a YAML collection as document contents");
  }
  class nc extends Error {
    constructor(a, b, c, d) {
      super();
      this.name = a;
      this.code = c;
      this.message = d;
      this.u = b;
    }
  }
  class oc extends nc {
    constructor(a, b, c) {
      super("YAMLParseError", a, b, c);
    }
  }
  class pc extends nc {
    constructor(a, b, c) {
      super("YAMLWarning", a, b, c);
    }
  }
  const qc = (a, b) => (c) => {
    if (-1 !== c.u[0]) {
      c.qa = c.u.map((k) => b.qa(k));
      var { line: d, ga: e } = c.qa[0];
      c.message += ` at line ${d}, column ${e}`;
      var f = e - 1,
        g = a.substring(b.P[d - 1], b.P[d]).replace(/[\n\r]+$/, "");
      if (60 <= f && 80 < g.length) {
        var h = Math.min(f - 39, g.length - 79);
        g = "\u2026" + g.substring(h);
        f -= h - 1;
      }
      80 < g.length && (g = g.substring(0, 79) + "\u2026");
      1 < d &&
        /^ *$/.test(g.substring(0, f)) &&
        ((h = a.substring(b.P[d - 2], b.P[d - 1])),
        80 < h.length && (h = h.substring(0, 79) + "\u2026\n"),
        (g = h + g));
      if (/[^ ]/.test(g)) {
        h = 1;
        const k = c.qa[1];
        k && k.line === d && k.ga > e && (h = Math.min(k.ga - e, 80 - f));
        f = " ".repeat(f) + "^".repeat(h);
        c.message += `:\n\n${g}\n${f}\n`;
      }
    }
  };
  function rc(a, { M: b, ba: c, next: d, offset: e, L: f, ea: g }) {
    let h = !1;
    var k = g;
    let m = g,
      l = (g = ""),
      n = !1,
      p = !1,
      q = !1,
      r = null,
      u = null,
      v = null,
      w = null,
      z = null;
    for (const t of a)
      switch (
        (q &&
          ("space" !== t.type &&
            "newline" !== t.type &&
            "comma" !== t.type &&
            f(t.offset, "MISSING_CHAR", "Tags and anchors must be separated from the next token by white space"),
          (q = !1)),
        t.type)
      ) {
        case "space":
          !b &&
            k &&
            "doc-start" !== c &&
            "\t" === t.source[0] &&
            f(t, "TAB_AS_INDENT", "Tabs are not allowed as indentation");
          m = !0;
          break;
        case "comment":
          m || f(t, "MISSING_CHAR", "Comments must be separated from other tokens by white space characters");
          k = t.source.substring(1) || " ";
          g ? (g += l + k) : (g = k);
          l = "";
          k = !1;
          break;
        case "newline":
          k ? (g ? (g += t.source) : (h = !0)) : (l += t.source);
          n = k = !0;
          if (r || u) p = !0;
          m = !0;
          break;
        case "anchor":
          r && f(t, "MULTIPLE_ANCHORS", "A node can have at most one anchor");
          t.source.endsWith(":") &&
            f(t.offset + t.source.length - 1, "BAD_ALIAS", "Anchor ending in : is ambiguous", !0);
          r = t;
          null === z && (z = t.offset);
          m = k = !1;
          q = !0;
          break;
        case "tag":
          u && f(t, "MULTIPLE_TAGS", "A node can have at most one tag");
          u = t;
          null === z && (z = t.offset);
          m = k = !1;
          q = !0;
          break;
        case c:
          (r || u) && f(t, "BAD_PROP_ORDER", `Anchors and tags must be after the ${t.source} indicator`);
          if (w) {
            let x;
            f(t, "UNEXPECTED_TOKEN", `Unexpected ${t.source} in ${null != (x = b) ? x : "collection"}`);
          }
          w = t;
          m = k = !1;
          break;
        case "comma":
          if (b) {
            v && f(t, "UNEXPECTED_TOKEN", `Unexpected , in ${b}`);
            v = t;
            m = k = !1;
            break;
          }
        default:
          f(t, "UNEXPECTED_TOKEN", `Unexpected ${t.type} token`), (m = k = !1);
      }
    e = (a = a[a.length - 1]) ? a.offset + a.source.length : e;
    q &&
      d &&
      "space" !== d.type &&
      "newline" !== d.type &&
      "comma" !== d.type &&
      ("scalar" !== d.type || "" !== d.source) &&
      f(d.offset, "MISSING_CHAR", "Tags and anchors must be separated from the next token by white space");
    let C;
    return { ha: v, I: w, N: h, g, Ua: n, rb: p, anchor: r, tag: u, end: e, start: null != (C = z) ? C : e };
  }
  function sc(a) {
    if (!a) return null;
    switch (a.type) {
      case "alias":
      case "scalar":
      case "double-quoted-scalar":
      case "single-quoted-scalar":
        if (a.source.includes("\n")) return !0;
        if (a.end) for (const b of a.end) if ("newline" === b.type) return !0;
        return !1;
      case "flow-collection":
        for (const b of a.items) {
          for (const c of b.start) if ("newline" === c.type) return !0;
          if (b.j) for (const c of b.j) if ("newline" === c.type) return !0;
          if (sc(b.key) || sc(b.value)) return !0;
        }
        return !1;
      default:
        return !0;
    }
  }
  function tc(a, b, c) {
    if ("flow-collection" === (null == b ? void 0 : b.type)) {
      const d = b.end[0];
      d.h !== a ||
        ("]" !== d.source && "}" !== d.source) ||
        !sc(b) ||
        c(d, "BAD_INDENT", "Flow end indicator should be more indented than parent", !0);
    }
  }
  function uc(a, b, c) {
    const d = a.options.yb;
    if (!1 === d) return !1;
    const e =
      "function" === typeof d
        ? d
        : (f, g) => f === g || (G(f) && G(g) && f.value === g.value && !("<<" === f.value && a.m.ja));
    return b.some((f) => e(f.key, c));
  }
  function vc({ Da: a, Ca: b }, c, d, e) {
    const f = new R(c.m);
    c.Z && (c.Z = !1);
    var g = d.offset;
    let h = null;
    for (const p of d.items) {
      var k = p.start,
        m = p.key;
      const q = p.j;
      var l = p.value;
      let r, u;
      const v = rc(k, {
          ba: "explicit-key-ind",
          next: null != (u = m) ? u : null == (r = q) ? void 0 : r[0],
          offset: g,
          L: e,
          ea: !0,
        }),
        w = !v.I;
      if (w) {
        m &&
          ("block-seq" === m.type
            ? e(g, "BLOCK_AS_IMPLICIT_KEY", "A block sequence may not be used as an implicit map key")
            : "indent" in m && m.h !== d.h && e(g, "BAD_INDENT", "All mapping items must start at the same column"));
        if (!v.anchor && !v.tag && !q) {
          h = v.end;
          v.g && (f.g ? (f.g += "\n" + v.g) : (f.g = v.g));
          continue;
        }
        if (v.rb || sc(m)) {
          let t;
          e(
            null != (t = m) ? t : k[k.length - 1],
            "MULTILINE_IMPLICIT_KEY",
            "Implicit keys need to be on a single line"
          );
        }
      } else {
        let t;
        (null == (t = v.I) ? void 0 : t.h) !== d.h &&
          e(g, "BAD_INDENT", "All mapping items must start at the same column");
      }
      g = v.end;
      k = m ? a(c, m, v, e) : b(c, g, k, null, v, e);
      c.m.W && tc(d.h, m, e);
      uc(c, f.items, k) && e(g, "DUPLICATE_KEY", "Map keys must be unique");
      let z;
      const C = rc(null != (z = q) ? z : [], {
        ba: "map-value-ind",
        next: l,
        offset: k.o[2],
        L: e,
        ea: !m || "block-scalar" === m.type,
      });
      g = C.end;
      if (C.I) {
        if (w) {
          let t;
          "block-map" !== (null == (t = l) ? void 0 : t.type) ||
            C.Ua ||
            e(g, "BLOCK_AS_IMPLICIT_KEY", "Nested mappings are not allowed in compact mappings");
          c.options.V &&
            v.start < C.I.offset - 1024 &&
            e(
              k.o,
              "KEY_OVER_1024_CHARS",
              "The : indicator must be at most 1024 chars after the start of an implicit block mapping key"
            );
        }
        m = l ? a(c, l, C, e) : b(c, g, q, null, C, e);
        c.m.W && tc(d.h, l, e);
        g = m.o[2];
        l = new P(k, m);
        c.options.oa && (l.Na = p);
        f.items.push(l);
      } else
        w && e(k.o, "MISSING_CHAR", "Implicit map keys need to be followed by map values"),
          C.g && (k.g ? (k.g += "\n" + C.g) : (k.g = C.g)),
          (l = new P(k)),
          c.options.oa && (l.Na = p),
          f.items.push(l);
    }
    h && h < g && e(h, "IMPOSSIBLE", "Map comment with trailing content");
    let n;
    f.o = [d.offset, g, null != (n = h) ? n : g];
    return f;
  }
  function wc({ Da: a, Ca: b }, c, d, e) {
    const f = new ob(c.m);
    c.Z && (c.Z = !1);
    let g = d.offset,
      h = null;
    for (const { start: l, value: n } of d.items) {
      var k = rc(l, { ba: "seq-item-ind", next: n, offset: g, L: e, ea: !0 });
      if (!k.I)
        if (k.anchor || k.tag || n)
          n && "block-seq" === n.type
            ? e(k.end, "BAD_INDENT", "All sequence items must start at the same column")
            : e(g, "MISSING_CHAR", "Sequence item without - indicator");
        else {
          h = k.end;
          k.g && (f.g = k.g);
          continue;
        }
      k = n ? a(c, n, k, e) : b(c, k.end, l, null, k, e);
      c.m.W && tc(d.h, n, e);
      g = k.o[2];
      f.items.push(k);
    }
    let m;
    f.o = [d.offset, g, null != (m = h) ? m : g];
    return f;
  }
  function xc(a, b, c, d) {
    let e = "";
    if (a) {
      let g = !1,
        h = "";
      for (const k of a) {
        a = k.source;
        var f = k.type;
        switch (f) {
          case "space":
            g = !0;
            break;
          case "comment":
            c && !g && d(k, "MISSING_CHAR", "Comments must be separated from other tokens by white space characters");
            f = a.substring(1) || " ";
            e ? (e += h + f) : (e = f);
            h = "";
            break;
          case "newline":
            e && (h += a);
            g = !0;
            break;
          default:
            d(k, "UNEXPECTED_TOKEN", `Unexpected ${f} at node end`);
        }
        b += a.length;
      }
    }
    return { g: e, offset: b };
  }
  const yc = (a) => a && ("block-map" === a.type || "block-seq" === a.type);
  function zc({ Da: a, Ca: b }, c, d, e) {
    const f = "{" === d.start.source;
    var g = f ? "flow map" : "flow sequence";
    const h = f ? new R(c.m) : new ob(c.m);
    h.M = !0;
    const k = c.Z;
    k && (c.Z = !1);
    var m = d.offset + d.start.source.length;
    for (let w = 0; w < d.items.length; ++w) {
      var l = d.items[w],
        n = l.start,
        p = l.key,
        q = l.j,
        r = l.value;
      let z, C;
      const t = rc(n, {
        M: g,
        ba: "explicit-key-ind",
        next: null != (C = p) ? C : null == (z = q) ? void 0 : z[0],
        offset: m,
        L: e,
        ea: !1,
      });
      if (!t.I) {
        if (!(t.anchor || t.tag || q || r)) {
          0 === w && t.ha
            ? e(t.ha, "UNEXPECTED_TOKEN", `Unexpected , in ${g}`)
            : w < d.items.length - 1 && e(t.start, "UNEXPECTED_TOKEN", `Unexpected empty item in ${g}`);
          t.g && (h.g ? (h.g += "\n" + t.g) : (h.g = t.g));
          m = t.end;
          continue;
        }
        !f &&
          c.options.V &&
          sc(p) &&
          e(p, "MULTILINE_IMPLICIT_KEY", "Implicit keys of flow sequence pairs need to be on a single line");
      }
      if (0 === w) t.ha && e(t.ha, "UNEXPECTED_TOKEN", `Unexpected , in ${g}`);
      else if ((t.ha || e(t.start, "MISSING_CHAR", `Missing , between ${g} items`), t.g)) {
        m = "";
        a: for (const x of n)
          switch (x.type) {
            case "comma":
            case "space":
              break;
            case "comment":
              m = x.source.substring(1);
              break a;
            default:
              break a;
          }
        if (m) {
          let x = h.items[h.items.length - 1];
          if (F(x)) {
            let E;
            x = null != (E = x.value) ? E : x.key;
          }
          x.g ? (x.g += "\n" + m) : (x.g = m);
          t.g = t.g.substring(m.length + 1);
        }
      }
      if (f || q || t.I) {
        m = t.end;
        n = p ? a(c, p, t, e) : b(c, m, n, null, t, e);
        yc(p) && e(n.o, "BLOCK_IN_FLOW", "Block collections are not allowed within flow collections");
        let x;
        p = rc(null != (x = q) ? x : [], { M: g, ba: "map-value-ind", next: r, offset: n.o[2], L: e, ea: !1 });
        if (p.I) {
          if (!f && !t.I && c.options.V) {
            if (q)
              for (const E of q) {
                if (E === p.I) break;
                if ("newline" === E.type) {
                  e(E, "MULTILINE_IMPLICIT_KEY", "Implicit keys of flow sequence pairs need to be on a single line");
                  break;
                }
              }
            t.start < p.I.offset - 1024 &&
              e(
                p.I,
                "KEY_OVER_1024_CHARS",
                "The : indicator must be at most 1024 chars after the start of an implicit flow sequence key"
              );
          }
        } else
          r &&
            ("source" in r && r.source && ":" === r.source[0]
              ? e(r, "MISSING_CHAR", `Missing space after : in ${g}`)
              : e(p.start, "MISSING_CHAR", `Missing , or : between ${g} items`));
        (q = r ? a(c, r, p, e) : p.I ? b(c, p.end, q, null, p, e) : null)
          ? yc(r) && e(q.o, "BLOCK_IN_FLOW", "Block collections are not allowed within flow collections")
          : p.g && (n.g ? (n.g += "\n" + p.g) : (n.g = p.g));
        r = new P(n, q);
        c.options.oa && (r.Na = l);
        f
          ? ((l = h), uc(c, l.items, n) && e(m, "DUPLICATE_KEY", "Map keys must be unique"), l.items.push(r))
          : ((l = new R(c.m)), (l.M = !0), l.items.push(r), h.items.push(l));
        m = q ? q.o[2] : p.end;
      } else
        (l = r ? a(c, r, t, e) : b(c, t.end, q, null, t, e)),
          h.items.push(l),
          (m = l.o[2]),
          yc(r) && e(l.o, "BLOCK_IN_FLOW", "Block collections are not allowed within flow collections");
    }
    b = f ? "}" : "]";
    const [u, ...v] = d.end;
    a = m;
    u && u.source === b
      ? (a = u.offset + u.source.length)
      : ((g = g[0].toUpperCase() + g.substring(1)),
        e(
          m,
          k ? "MISSING_CHAR" : "BAD_INDENT",
          k ? `${g} must end with a ${b}` : `${g} in block collection must be sufficiently indented and end with a ${b}`
        ),
        u && 1 !== u.source.length && v.unshift(u));
    0 < v.length
      ? ((c = xc(v, a, c.options.V, e)),
        c.g && (h.g ? (h.g += "\n" + c.g) : (h.g = c.g)),
        (h.o = [d.offset, a, c.offset]))
      : (h.o = [d.offset, a, a]);
    return h;
  }
  function Ac(a, b, c, d) {
    var e = Bc;
    let f;
    switch (b.type) {
      case "block-map":
        f = vc(e, a, b, d);
        break;
      case "block-seq":
        f = wc(e, a, b, d);
        break;
      case "flow-collection":
        f = zc(e, a, b, d);
    }
    if (!c) return f;
    const g = a.s.tagName(c.source, (m) => d(c, "TAG_RESOLVE_FAILED", m));
    if (!g) return f;
    b = f.constructor;
    if ("!" === g || g === b.tagName) return (f.tag = b.tagName), f;
    const h = va(f) ? "map" : "seq";
    b = a.m.G.find((m) => m.T === h && m.tag === g);
    if (!b)
      if ((b = a.m.Wa[g]) && b.T === h) a.m.G.push(Object.assign({}, b, { default: !1 }));
      else return d(c, "TAG_RESOLVE_FAILED", `Unresolved tag: ${g}`, !0), (f.tag = g), f;
    a = b.resolve(f, (m) => d(c, "TAG_RESOLVE_FAILED", m), a.options);
    a = I(a) ? a : new L(a);
    a.o = f.o;
    a.tag = g;
    let k;
    if (null == (k = b) ? 0 : k.format) a.format = b.format;
    return a;
  }
  function Cc(a, b, c) {
    const d = a.offset;
    b = Dc(a, b, c);
    if (!b) return { value: "", type: null, g: "", o: [d, d, d] };
    const e = ">" === b.mode ? "BLOCK_FOLDED" : "BLOCK_LITERAL";
    if (a.source) {
      var f = a.source.split(/\n( *)/);
      var g = f[0],
        h = g.match(/^( *)/);
      g = [(null == h ? 0 : h[1]) ? [h[1], g.slice(h[1].length)] : ["", g]];
      for (h = 1; h < f.length; h += 2) g.push([f[h], f[h + 1]]);
      f = g;
    } else f = [];
    g = f.length;
    for (h = f.length - 1; 0 <= h; --h) {
      var k = f[h][1];
      if ("" === k || "\r" === k) g = h;
      else break;
    }
    if (0 === g)
      return (
        (c = "+" === b.Qa && 0 < f.length ? "\n".repeat(Math.max(1, f.length - 1)) : ""),
        (f = d + b.length),
        a.source && (f += a.source.length),
        { value: c, type: e, g: b.g, o: [d, f, f] }
      );
    h = a.h + b.h;
    k = a.offset + b.length;
    var m = 0;
    for (var l = 0; l < g; ++l) {
      const [r, u] = f[l];
      if ("" === u || "\r" === u) 0 === b.h && r.length > h && (h = r.length);
      else {
        r.length < h &&
          c(
            k + r.length,
            "MISSING_CHAR",
            "Block scalars with more-indented leading empty lines must use an explicit indentation indicator"
          );
        0 === b.h && (h = r.length);
        m = l;
        break;
      }
      k += r.length + u.length + 1;
    }
    for (l = f.length - 1; l >= g; --l) f[l][0].length > h && (g = l + 1);
    let n = (l = ""),
      p = !1;
    for (var q = 0; q < m; ++q) l += f[q][0].slice(h) + "\n";
    for (; m < g; ++m) {
      let [r, u] = f[m];
      k += r.length + u.length + 1;
      (q = "\r" === u[u.length - 1]) && (u = u.slice(0, -1));
      u &&
        r.length < h &&
        (c(
          k - u.length - (q ? 2 : 1),
          "BAD_INDENT",
          `Block scalar lines must not be less indented than their ${
            b.h ? "explicit indentation indicator" : "first line"
          }`
        ),
        (r = ""));
      "BLOCK_LITERAL" === e
        ? ((l += n + r.slice(h) + u), (n = "\n"))
        : r.length > h || "\t" === u[0]
        ? (" " === n ? (n = "\n") : p || "\n" !== n || (n = "\n\n"), (l += n + r.slice(h) + u), (n = "\n"), (p = !0))
        : "" === u
        ? "\n" === n
          ? (l += "\n")
          : (n = "\n")
        : ((l += n + u), (n = " "), (p = !1));
    }
    switch (b.Qa) {
      case "-":
        break;
      case "+":
        for (c = g; c < f.length; ++c) l += "\n" + f[c][0].slice(h);
        "\n" !== l[l.length - 1] && (l += "\n");
        break;
      default:
        l += "\n";
    }
    a = d + b.length + a.source.length;
    return { value: l, type: e, g: b.g, o: [d, a, a] };
  }
  function Dc({ offset: a, Ka: b }, c, d) {
    if ("block-scalar-header" !== b[0].type) return d(b[0], "IMPOSSIBLE", "Block scalar header not found"), null;
    var { source: e } = b[0];
    const f = e[0];
    let g = 0,
      h = "";
    var k = -1;
    for (var m = 1; m < e.length; ++m) {
      var l = e[m];
      h || ("-" !== l && "+" !== l) ? ((l = Number(l)), !g && l ? (g = l) : -1 === k && (k = a + m)) : (h = l);
    }
    -1 !== k && d(k, "UNEXPECTED_TOKEN", `Block scalar header includes extra characters: ${e}`);
    a = !1;
    k = "";
    e = e.length;
    for (m = 1; m < b.length; ++m)
      switch (((l = b[m]), l.type)) {
        case "space":
          a = !0;
        case "newline":
          e += l.source.length;
          break;
        case "comment":
          c && !a && d(l, "MISSING_CHAR", "Comments must be separated from other tokens by white space characters");
          e += l.source.length;
          k = l.source.substring(1);
          break;
        case "error":
          d(l, "UNEXPECTED_TOKEN", l.message);
          e += l.source.length;
          break;
        default:
          d(l, "UNEXPECTED_TOKEN", `Unexpected token in block scalar header: ${l.type}`),
            (l = l.source) && "string" === typeof l && (e += l.length);
      }
    return { mode: f, h: g, Qa: h, g: k, length: e };
  }
  function Ec(a, b, c) {
    const d = a.offset,
      e = a.type;
    var f = a.source;
    const g = a.end;
    var h = (k, m, l) => c(d + k, m, l);
    switch (e) {
      case "scalar":
        a = "PLAIN";
        h = Fc(f, h);
        break;
      case "single-quoted-scalar":
        a = "QUOTE_SINGLE";
        h = Gc(f, h);
        break;
      case "double-quoted-scalar":
        a = "QUOTE_DOUBLE";
        h = Hc(f, h);
        break;
      default:
        return (
          c(a, "UNEXPECTED_TOKEN", `Expected a flow scalar value, but found: ${e}`),
          { value: "", type: null, g: "", o: [d, d + f.length, d + f.length] }
        );
    }
    f = d + f.length;
    b = xc(g, f, b, c);
    return { value: h, type: a, g: b.g, o: [d, f, b.offset] };
  }
  function Fc(a, b) {
    let c = "";
    switch (a[0]) {
      case "\t":
        c = "a tab character";
        break;
      case ",":
        c = "flow indicator character ,";
        break;
      case "%":
        c = "directive indicator character %";
        break;
      case "|":
      case ">":
        c = `block scalar indicator ${a[0]}`;
        break;
      case "@":
      case "`":
        c = `reserved character ${a[0]}`;
    }
    c && b(0, "BAD_SCALAR_START", `Plain value cannot start with ${c}`);
    return Ic(a);
  }
  function Gc(a, b) {
    ("'" === a[a.length - 1] && 1 !== a.length) || b(a.length, "MISSING_CHAR", "Missing closing 'quote");
    return Ic(a.slice(1, -1)).replace(/''/g, "'");
  }
  function Ic(a) {
    try {
      var b = RegExp("(.*?)(?<![ \t])[ \t]*\r?\n", "sy");
      var c = RegExp("[ \t]*(.*?)(?:(?<![ \t])[ \t]*)?\r?\n", "sy");
    } catch (k) {
      (b = /(.*?)[ \t]*\r?\n/sy), (c = /[ \t]*(.*?)[ \t]*\r?\n/sy);
    }
    let d = b.exec(a);
    if (!d) return a;
    let e = d[1],
      f = " ";
    b = b.lastIndex;
    for (c.lastIndex = b; (d = c.exec(a)); )
      "" === d[1] ? ("\n" === f ? (e += f) : (f = "\n")) : ((e += f + d[1]), (f = " ")), (b = c.lastIndex);
    c = /[ \t]*(.*)/sy;
    c.lastIndex = b;
    d = c.exec(a);
    let g, h;
    return e + f + (null != (h = null == (g = d) ? void 0 : g[1]) ? h : "");
  }
  function Hc(a, b) {
    var c = "";
    for (var d = 1; d < a.length - 1; ++d) {
      var e = a[d];
      if ("\r" !== e || "\n" !== a[d + 1])
        if ("\n" === e) {
          e = "";
          for (
            var f = a[d + 1];
            !((" " !== f && "\t" !== f && "\n" !== f && "\r" !== f) || ("\r" === f && "\n" !== a[d + 2]));

          )
            "\n" === f && (e += "\n"), (d += 1), (f = a[d + 1]);
          e || (e = " ");
          const { qb: h, offset: k } = { qb: e, offset: d };
          c += h;
          d = k;
        } else if ("\\" === e)
          if (((e = a[++d]), (f = Jc[e]))) c += f;
          else if ("\n" === e) for (e = a[d + 1]; " " === e || "\t" === e; ) e = a[++d + 1];
          else if ("\r" === e && "\n" === a[d + 1]) for (e = a[++d + 1]; " " === e || "\t" === e; ) e = a[++d + 1];
          else if ("x" === e || "u" === e || "U" === e) {
            e = { x: 2, bc: 4, Eb: 8 }[e];
            f = d + 1;
            var g = a.substr(f, e);
            g = g.length === e && /^[0-9a-fA-F]+$/.test(g) ? parseInt(g, 16) : NaN;
            isNaN(g)
              ? ((g = a.substr(f - 2, e + 2)), b(f - 2, "BAD_DQ_ESCAPE", `Invalid escape sequence ${g}`), (f = g))
              : (f = String.fromCodePoint(g));
            c += f;
            d += e;
          } else (e = a.substr(d - 1, 2)), b(d - 1, "BAD_DQ_ESCAPE", `Invalid escape sequence ${e}`), (c += e);
        else if (" " === e || "\t" === e) {
          f = d;
          for (g = a[d + 1]; " " === g || "\t" === g; ) g = a[++d + 1];
          "\n" === g || ("\r" === g && "\n" === a[d + 2]) || (c += d > f ? a.slice(f, d + 1) : e);
        } else c += e;
    }
    ('"' === a[a.length - 1] && 1 !== a.length) || b(a.length, "MISSING_CHAR", 'Missing closing "quote');
    return c;
  }
  const Jc = {
    0: "\x00",
    a: "\u0007",
    b: "\b",
    e: "\u001b",
    f: "\f",
    n: "\n",
    r: "\r",
    t: "\t",
    cc: "\v",
    Bb: "\u0085",
    Fb: "\u00a0",
    Ab: "\u2028",
    Cb: "\u2029",
    " ": " ",
    '"': '"',
    "/": "/",
    "\\": "\\",
    "\t": "\t",
  };
  function Kc(a, b, c, d) {
    const { value: e, type: f, g, o: h } = "block-scalar" === b.type ? Cc(b, a.options.V, d) : Ec(b, a.options.V, d),
      k = c ? a.s.tagName(c.source, (n) => d(c, "TAG_RESOLVE_FAILED", n)) : null,
      m = c && k ? Lc(a.m, e, k, c, d) : "scalar" === b.type ? Mc(a, e, b, d) : a.m[B];
    let l;
    try {
      const n = m.resolve(e, (p) => d(null != c ? c : b, "TAG_RESOLVE_FAILED", p), a.options);
      l = G(n) ? n : new L(n);
    } catch (n) {
      d(null != c ? c : b, "TAG_RESOLVE_FAILED", n instanceof Error ? n.message : String(n)), (l = new L(e));
    }
    l.o = h;
    l.source = e;
    f && (l.type = f);
    k && (l.tag = k);
    m.format && (l.format = m.format);
    g && (l.g = g);
    return l;
  }
  function Lc(a, b, c, d, e) {
    if ("!" === c) return a[B];
    const f = [];
    for (const g of a.G)
      if (!g.T && g.tag === c)
        if (g.default && g.test) f.push(g);
        else return g;
    for (const g of f) {
      let h;
      if (null == (h = g.test) ? 0 : h.test(b)) return g;
    }
    if ((b = a.Wa[c]) && !b.T) return a.G.push(Object.assign({}, b, { default: !1, test: void 0 })), b;
    e(d, "TAG_RESOLVE_FAILED", `Unresolved tag: ${c}`, "tag:yaml.org,2002:str" !== c);
    return a[B];
  }
  function Mc({ s: a, m: b }, c, d, e) {
    const f =
      b.G.find((g) => {
        let h;
        return g.default && (null == (h = g.test) ? void 0 : h.test(c));
      }) || b[B];
    if (b.W) {
      let g;
      const h =
        null !=
        (g = b.W.find((k) => {
          let m;
          return k.default && (null == (m = k.test) ? void 0 : m.test(c));
        }))
          ? g
          : b[B];
      f.tag !== h.tag &&
        ((b = Ea(a, f.tag)),
        (a = Ea(a, h.tag)),
        e(d, "TAG_RESOLVE_FAILED", `Value may be parsed as either ${b} or ${a}`, !0));
    }
    return f;
  }
  const Bc = { Da: Nc, Ca: Oc };
  function Nc(a, b, c, d) {
    const e = c.N,
      f = c.g,
      g = c.anchor,
      h = c.tag;
    let k = !0;
    switch (b.type) {
      case "alias":
        c = Pc(a, b, d);
        (g || h) && d(b, "ALIAS_PROPS", "An alias node must not specify any properties");
        break;
      case "scalar":
      case "single-quoted-scalar":
      case "double-quoted-scalar":
      case "block-scalar":
        c = Kc(a, b, h, d);
        g && (c.anchor = g.source.substring(1));
        break;
      case "block-map":
      case "block-seq":
      case "flow-collection":
        c = Ac(a, b, h, d);
        g && (c.anchor = g.source.substring(1));
        break;
      default:
        d(b, "UNEXPECTED_TOKEN", "error" === b.type ? b.message : `Unsupported token (type: ${b.type})`),
          (c = Oc(a, b.offset, void 0, null, c, d)),
          (k = !1);
    }
    g && "" === c.anchor && d(g, "BAD_ALIAS", "Anchor cannot be an empty string");
    e && (c.N = !0);
    f && ("scalar" === b.type && "" === b.source ? (c.g = f) : (c.D = f));
    a.options.oa && k && (c.Na = b);
    return c;
  }
  function Oc(a, b, c, d, { N: e, g: f, anchor: g, tag: h, end: k }, m) {
    if (c)
      for (null === d && (d = c.length), --d; 0 <= d; --d) {
        let l = c[d];
        switch (l.type) {
          case "space":
          case "comment":
          case "newline":
            b -= l.source.length;
            continue;
        }
        l = c[++d];
        let n;
        for (; "space" === (null == (n = l) ? void 0 : n.type); ) (b += l.source.length), (l = c[++d]);
        break;
      }
    a = Kc(a, { type: "scalar", offset: b, h: -1, source: "" }, h, m);
    g && ((a.anchor = g.source.substring(1)), "" === a.anchor && m(g, "BAD_ALIAS", "Anchor cannot be an empty string"));
    e && (a.N = !0);
    f && ((a.g = f), (a.o[2] = k));
    return a;
  }
  function Pc({ options: a }, { offset: b, source: c, end: d }, e) {
    const f = new La(c.substring(1));
    "" === f.source && e(b, "BAD_ALIAS", "Alias cannot be an empty string");
    f.source.endsWith(":") && e(b + c.length - 1, "BAD_ALIAS", "Alias ending in : is ambiguous", !0);
    c = b + c.length;
    a = xc(d, c, a.V, e);
    f.o = [b, c, a.offset];
    a.g && (f.g = a.g);
    return f;
  }
  function Qc(a, b, { offset: c, start: d, value: e, end: f }, g) {
    a = Object.assign({ Aa: b }, a);
    a = new lc(void 0, a);
    b = { Z: !0, s: a.s, options: a.options, m: a.m };
    const h = rc(d, { ba: "doc-start", next: null != e ? e : null == f ? void 0 : f[0], offset: c, L: g, ea: !0 });
    h.I &&
      ((a.s.aa = !0),
      !e ||
        ("block-map" !== e.type && "block-seq" !== e.type) ||
        h.Ua ||
        g(h.end, "MISSING_CHAR", "Block collection cannot start on same line with directives-end marker"));
    a.v = e ? Nc(b, e, h, g) : Oc(b, h.end, d, null, h, g);
    d = a.v.o[2];
    f = xc(f, d, !1, g);
    f.g && (a.g = f.g);
    a.o = [c, d, f.offset];
    return a;
  }
  function Rc(a) {
    if ("number" === typeof a) return [a, a + 1];
    if (Array.isArray(a)) return 2 === a.length ? a : [a[0], a[1]];
    const b = a.offset;
    a = a.source;
    return [b, b + ("string" === typeof a ? a.length : 1)];
  }
  function Sc(a, b, c) {
    var d = a.l,
      e = "";
    let f = !1,
      g = !1;
    for (let m = 0; m < d.length; ++m) {
      const l = d[m];
      switch (l[0]) {
        case "#":
          e += ("" === e ? "" : g ? "\n\n" : "\n") + (l.substring(1) || " ");
          f = !0;
          g = !1;
          break;
        case "%":
          let n;
          "#" !== (null == (n = d[m + 1]) ? void 0 : n[0]) && (m += 1);
          f = !1;
          break;
        default:
          f || (g = !0), (f = !1);
      }
    }
    const { g: h, jb: k } = { g: e, jb: g };
    h &&
      ((d = b.v),
      c
        ? (b.g = b.g ? `${b.g}\n${h}` : h)
        : k || b.s.aa || !d
        ? (b.D = h)
        : H(d) && !d.M && 0 < d.items.length
        ? ((d = d.items[0]), F(d) && (d = d.key), (e = d.D), (d.D = e ? `${h}\n${e}` : h))
        : ((e = d.D), (d.D = e ? `${h}\n${e}` : h)));
    c
      ? (Array.prototype.push.apply(b.errors, a.errors), Array.prototype.push.apply(b.O, a.O))
      : ((b.errors = a.errors), (b.O = a.O));
    a.l = [];
    a.errors = [];
    a.O = [];
  }
  function* Tc(a, b, c = -1) {
    for (const d of b) yield* a.next(d);
    yield* a.end(!0, c);
  }
  class Uc {
    constructor(a = {}) {
      this.A = null;
      this.i = !1;
      this.l = [];
      this.errors = [];
      this.O = [];
      this.L = (b, c, d, e) => {
        b = Rc(b);
        e ? this.O.push(new pc(b, c, d)) : this.errors.push(new oc(b, c, d));
      };
      this.s = new Fa({ version: a.version || "1.2" });
      this.options = a;
    }
    *next(a) {
      switch (a.type) {
        case "directive":
          this.s.add(a.source, (c, d, e) => {
            const f = Rc(a);
            f[0] += c;
            this.L(f, "BAD_DIRECTIVE", d, e);
          });
          this.l.push(a.source);
          this.i = !0;
          break;
        case "document":
          var b = Qc(this.options, this.s, a, this.L);
          this.i && !b.s.aa && this.L(a, "MISSING_CHAR", "Missing directives-end/doc-start indicator line");
          Sc(this, b, !1);
          this.A && (yield this.A);
          this.A = b;
          this.i = !1;
          break;
        case "byte-order-mark":
        case "space":
          break;
        case "comment":
        case "newline":
          this.l.push(a.source);
          break;
        case "error":
          b = a.source ? `${a.message}: ${JSON.stringify(a.source)}` : a.message;
          b = new oc(Rc(a), "UNEXPECTED_TOKEN", b);
          this.i || !this.A ? this.errors.push(b) : this.A.errors.push(b);
          break;
        case "doc-end":
          if (!this.A) {
            this.errors.push(new oc(Rc(a), "UNEXPECTED_TOKEN", "Unexpected doc-end without preceding document"));
            break;
          }
          this.A.s.Ra = !0;
          b = xc(a.end, a.offset + a.source.length, this.A.options.V, this.L);
          Sc(this, this.A, !0);
          if (b.g) {
            const c = this.A.g;
            this.A.g = c ? `${c}\n${b.g}` : b.g;
          }
          this.A.o[2] = b.offset;
          break;
        default:
          this.errors.push(new oc(Rc(a), "UNEXPECTED_TOKEN", `Unsupported token ${a.type}`));
      }
    }
    *end(a = !1, b = -1) {
      this.A
        ? (Sc(this, this.A, !0), yield this.A, (this.A = null))
        : a &&
          ((a = Object.assign({ Aa: this.s }, this.options)),
          (a = new lc(void 0, a)),
          this.i && this.L(b, "MISSING_CHAR", "Missing directives-end indicator line"),
          (a.o = [0, b, b]),
          Sc(this, a, !1),
          yield a);
    }
  }
  class Vc {
    constructor() {
      this.P = [];
      this.ib = (a) => this.P.push(a);
      this.qa = (a) => {
        let b = 0,
          c = this.P.length;
        for (; b < c; ) {
          const d = (b + c) >> 1;
          this.P[d] < a ? (b = d + 1) : (c = d);
        }
        return this.P[b] === a
          ? { line: b + 1, ga: 1 }
          : 0 === b
          ? { line: 0, ga: a }
          : { line: b, ga: a - this.P[b - 1] + 1 };
      };
    }
  }
  function Wc(a) {
    switch (a) {
      case "\ufeff":
        return "byte-order-mark";
      case "\u0002":
        return "doc-mode";
      case "\u0018":
        return "flow-error-end";
      case "\u001f":
        return "scalar";
      case "---":
        return "doc-start";
      case "...":
        return "doc-end";
      case "":
      case "\n":
      case "\r\n":
        return "newline";
      case "-":
        return "seq-item-ind";
      case "?":
        return "explicit-key-ind";
      case ":":
        return "map-value-ind";
      case "{":
        return "flow-map-start";
      case "}":
        return "flow-map-end";
      case "[":
        return "flow-seq-start";
      case "]":
        return "flow-seq-end";
      case ",":
        return "comma";
    }
    switch (a[0]) {
      case " ":
      case "\t":
        return "space";
      case "#":
        return "comment";
      case "%":
        return "directive-line";
      case "*":
        return "alias";
      case "&":
        return "anchor";
      case "!":
        return "tag";
      case "'":
        return "single-quoted-scalar";
      case '"':
        return "double-quoted-scalar";
      case "|":
      case ">":
        return "block-scalar-header";
    }
    return null;
  }
  function T(a) {
    switch (a) {
      case void 0:
      case " ":
      case "\n":
      case "\r":
      case "\t":
        return !0;
      default:
        return !1;
    }
  }
  const Xc = "0123456789ABCDEFabcdef".split(""),
    Yc = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-#;/?:@&=+$_.!~*'()".split(""),
    Zc = [",", "[", "]", "{", "}"],
    $c = " ,[]{}\n\r\t".split(""),
    ad = (a) => !a || $c.includes(a);
  function* bd(a, b) {
    switch (b) {
      case "stream":
        return yield* cd(a);
      case "line-start":
        return yield* dd(a);
      case "block-start":
        return yield* ed(a);
      case "doc":
        return yield* fd(a);
      case "flow":
        return yield* gd(a);
      case "quoted-scalar":
        return yield* hd(a);
      case "block-scalar":
        return yield* id(a);
      case "plain-scalar":
        return yield* jd(a);
    }
  }
  function* kd(a, b) {
    b && ((a.buffer = a.buffer ? a.buffer + b : b), (a.ca = null));
    a.l = !0;
    let c;
    for (b = null != (c = a.next) ? c : "stream"; b && a.u + 1 <= a.buffer.length; ) b = yield* bd(a, b);
  }
  function U(a, b) {
    return a.buffer[a.u + b];
  }
  function ld(a, b) {
    var c = a.buffer[b];
    if (0 < a.i) {
      let d = 0;
      for (; " " === c; ) c = a.buffer[++d + b];
      if ("\r" === c) {
        const e = a.buffer[d + b + 1];
        if ("\n" === e || (!e && !a.l)) return b + d + 1;
      }
      return "\n" === c || d >= a.i || (!c && !a.l) ? b + d : -1;
    }
    if ("-" === c || "." === c)
      if (((c = a.buffer.substr(b, 3)), ("---" === c || "..." === c) && T(a.buffer[b + 3]))) return -1;
    return b;
  }
  function md(a) {
    let b = a.ca;
    if ("number" !== typeof b || (-1 !== b && b < a.u)) (b = a.buffer.indexOf("\n", a.u)), (a.ca = b);
    if (-1 === b) return a.l ? a.buffer.substring(a.u) : null;
    "\r" === a.buffer[b - 1] && --b;
    return a.buffer.substring(a.u, b);
  }
  function V(a, b) {
    a.buffer = a.buffer.substring(a.u);
    a.u = 0;
    a.ca = null;
    a.next = b;
    return null;
  }
  function* cd(a) {
    let b = md(a);
    if (null === b) return V(a, "stream");
    "\ufeff" === b[0] && (yield* W(a, 1), (b = b.substring(1)));
    if ("%" === b[0]) {
      var c = b.length,
        d = b.indexOf("#");
      if (-1 !== d) {
        const e = b[d - 1];
        if (" " === e || "\t" === e) c = d - 1;
      }
      for (;;)
        if (((d = b[c - 1]), " " === d || "\t" === d)) --c;
        else break;
      c = (yield* W(a, c)) + (yield* X(a, !0));
      yield* W(a, b.length - c);
      nd(a);
      return "stream";
    }
    c = a.u;
    for (d = a.buffer[c]; " " === d || "\t" === d; ) d = a.buffer[++c];
    if (d && "#" !== d && "\n" !== d ? "\r" === d && "\n" === a.buffer[c + 1] : 1)
      return (c = yield* X(a, !0)), yield* W(a, b.length - c), yield* nd(a), "stream";
    yield "\u0002";
    return yield* dd(a);
  }
  function* dd(a) {
    var b = U(a, 0);
    if (!b && !a.l) return V(a, "line-start");
    if ("-" === b || "." === b) {
      if (!(a.l || a.u + 4 <= a.buffer.length)) return V(a, "line-start");
      b = a.buffer.substr(a.u, 3);
      if ("---" === b && T(U(a, 3))) return yield* W(a, 3), (a.H = 0), (a.i = 0), "doc";
      if ("..." === b && T(U(a, 3))) return yield* W(a, 3), "stream";
    }
    a.H = yield* X(a, !1);
    a.i > a.H && !T(U(a, 1)) && (a.i = a.H);
    return yield* ed(a);
  }
  function* ed(a) {
    const [b, c] = a.buffer.substr(a.u, 2);
    if (!c && !a.l) return V(a, "block-start");
    if (("-" === b || "?" === b || ":" === b) && T(c)) {
      const d = (yield* W(a, 1)) + (yield* X(a, !0));
      a.i = a.H + 1;
      a.H += d;
      return yield* ed(a);
    }
    return "doc";
  }
  function* fd(a) {
    yield* X(a, !0);
    const b = md(a);
    if (null === b) return V(a, "doc");
    let c = yield* od(a);
    switch (b[c]) {
      case "#":
        yield* W(a, b.length - c);
      case void 0:
        return yield* nd(a), yield* dd(a);
      case "{":
      case "[":
        return yield* W(a, 1), (a.C = !1), (a.F = 1), "flow";
      case "}":
      case "]":
        return yield* W(a, 1), "doc";
      case "*":
        return yield* pd(a, ad), "doc";
      case '"':
      case "'":
        return yield* hd(a);
      case "|":
      case ">":
        return (c += yield* qd(a)), (c += yield* X(a, !0)), yield* W(a, b.length - c), yield* nd(a), yield* id(a);
      default:
        return yield* jd(a);
    }
  }
  function* gd(a) {
    let b;
    var c = -1;
    do {
      var d = yield* nd(a);
      0 < d ? ((b = yield* X(a, !1)), (a.H = c = b)) : (b = 0);
      b += yield* X(a, !0);
    } while (0 < d + b);
    d = md(a);
    if (null === d) return V(a, "flow");
    if ((-1 !== c && c < a.i && "#" !== d[0]) || (0 === c && (d.startsWith("---") || d.startsWith("...")) && T(d[3])))
      if (c !== a.i - 1 || 1 !== a.F || ("]" !== d[0] && "}" !== d[0])) return (a.F = 0), yield "\u0018", yield* dd(a);
    for (c = 0; "," === d[c]; ) (c += yield* W(a, 1)), (c += yield* X(a, !0)), (a.C = !1);
    c += yield* od(a);
    switch (d[c]) {
      case void 0:
        return "flow";
      case "#":
        return yield* W(a, d.length - c), "flow";
      case "{":
      case "[":
        return yield* W(a, 1), (a.C = !1), (a.F += 1), "flow";
      case "}":
      case "]":
        return yield* W(a, 1), (a.C = !0), --a.F, a.F ? "flow" : "doc";
      case "*":
        return yield* pd(a, ad), "flow";
      case '"':
      case "'":
        return (a.C = !0), yield* hd(a);
      case ":":
        if (((c = U(a, 1)), a.C || T(c) || "," === c)) return (a.C = !1), yield* W(a, 1), yield* X(a, !0), "flow";
      default:
        return (a.C = !1), yield* jd(a);
    }
  }
  function* hd(a) {
    var b = U(a, 0);
    let c = a.buffer.indexOf(b, a.u + 1);
    if ("'" === b) for (; -1 !== c && "'" === a.buffer[c + 1]; ) c = a.buffer.indexOf("'", c + 2);
    else
      for (; -1 !== c; ) {
        for (b = 0; "\\" === a.buffer[c - 1 - b]; ) b += 1;
        if (0 === b % 2) break;
        c = a.buffer.indexOf('"', c + 1);
      }
    b = a.buffer.substring(0, c);
    let d = b.indexOf("\n", a.u);
    if (-1 !== d) {
      for (; -1 !== d; ) {
        const e = ld(a, d + 1);
        if (-1 === e) break;
        d = b.indexOf("\n", e);
      }
      -1 !== d && (c = d - ("\r" === b[d - 1] ? 2 : 1));
    }
    if (-1 === c) {
      if (!a.l) return V(a, "quoted-scalar");
      c = a.buffer.length;
    }
    yield* rd(a, c + 1, !1);
    return a.F ? "flow" : "doc";
  }
  function* id(a) {
    let b = a.u - 1,
      c = 0;
    var d;
    a: for (var e = a.u; (d = a.buffer[e]); ++e)
      switch (d) {
        case " ":
          c += 1;
          break;
        case "\n":
          b = e;
          c = 0;
          break;
        case "\r":
          var f = a.buffer[e + 1];
          if (!f && !a.l) return V(a, "block-scalar");
          if ("\n" === f) break;
        default:
          break a;
      }
    if (!d && !a.l) return V(a, "block-scalar");
    if (c >= a.i) {
      -1 === a.U ? (a.i = c) : (a.i += a.U);
      do {
        d = ld(a, b + 1);
        if (-1 === d) break;
        b = a.buffer.indexOf("\n", d);
      } while (-1 !== b);
      if (-1 === b) {
        if (!a.l) return V(a, "block-scalar");
        b = a.buffer.length;
      }
    }
    if (!a.Ma) {
      do {
        d = b - 1;
        e = a.buffer[d];
        "\r" === e && (e = a.buffer[--d]);
        for (f = d; " " === e || "\t" === e; ) e = a.buffer[--d];
        if ("\n" === e && d >= a.u && d + 1 + c > f) b = d;
        else break;
      } while (1);
    }
    yield "\u001f";
    yield* rd(a, b + 1, !0);
    return yield* dd(a);
  }
  function* jd(a) {
    const b = 0 < a.F;
    let c = a.u - 1,
      d = a.u - 1,
      e;
    for (; (e = a.buffer[++d]); )
      if (":" === e) {
        var f = a.buffer[d + 1];
        if (T(f) || (b && "," === f)) break;
        c = d;
      } else if (T(e)) {
        f = a.buffer[d + 1];
        "\r" === e && ("\n" === f ? ((d += 1), (e = "\n"), (f = a.buffer[d + 1])) : (c = d));
        if ("#" === f || (b && Zc.includes(f))) break;
        if ("\n" === e) {
          f = ld(a, d + 1);
          if (-1 === f) break;
          d = Math.max(d, f - 2);
        }
      } else {
        if (b && Zc.includes(e)) break;
        c = d;
      }
    if (!e && !a.l) return V(a, "plain-scalar");
    yield "\u001f";
    yield* rd(a, c + 1, !0);
    return b ? "flow" : "doc";
  }
  function* W(a, b) {
    return 0 < b ? (yield a.buffer.substr(a.u, b), (a.u += b), b) : 0;
  }
  function* X(a, b) {
    let c = a.u - 1,
      d;
    do d = a.buffer[++c];
    while (" " === d || (b && "\t" === d));
    b = c - a.u;
    0 < b && (yield a.buffer.substr(a.u, b), (a.u = c));
    return b;
  }
  function* nd(a) {
    const b = a.buffer[a.u];
    return "\n" === b ? yield* W(a, 1) : "\r" === b && "\n" === U(a, 1) ? yield* W(a, 2) : 0;
  }
  function* od(a) {
    switch (U(a, 0)) {
      case "!":
        return (yield* sd(a)) + (yield* X(a, !0)) + (yield* od(a));
      case "&":
        return (yield* pd(a, ad)) + (yield* X(a, !0)) + (yield* od(a));
      case "-":
      case "?":
      case ":":
        const b = 0 < a.F,
          c = U(a, 1);
        if (T(c) || (b && Zc.includes(c)))
          return b ? a.C && (a.C = !1) : (a.i = a.H + 1), (yield* W(a, 1)) + (yield* X(a, !0)) + (yield* od(a));
    }
    return 0;
  }
  function* pd(a, b) {
    let c = a.u,
      d = a.buffer[c];
    for (; !b(d); ) d = a.buffer[++c];
    return yield* rd(a, c, !1);
  }
  function* qd(a) {
    a.U = -1;
    a.Ma = !1;
    let b = a.u;
    for (;;) {
      const c = a.buffer[++b];
      if ("+" === c) a.Ma = !0;
      else if ("0" < c && "9" >= c) a.U = Number(c) - 1;
      else if ("-" !== c) break;
    }
    return yield* pd(a, (c) => T(c) || "#" === c);
  }
  function* rd(a, b, c) {
    if ((b = a.buffer.slice(a.u, b))) return yield b, (a.u += b.length), b.length;
    c && (yield "");
    return 0;
  }
  function* sd(a) {
    if ("<" === U(a, 1)) {
      for (var b = a.u + 2, c = a.buffer[b]; !T(c) && ">" !== c; ) c = a.buffer[++b];
      return yield* rd(a, ">" === c ? b + 1 : b, !1);
    }
    b = a.u + 1;
    for (c = a.buffer[b]; c; )
      if (Yc.includes(c)) c = a.buffer[++b];
      else if ("%" === c && Xc.includes(a.buffer[b + 1]) && Xc.includes(a.buffer[b + 2])) c = a.buffer[(b += 3)];
      else break;
    return yield* rd(a, b, !1);
  }
  class td {
    constructor() {
      this.l = !1;
      this.U = -1;
      this.Ma = !1;
      this.buffer = "";
      this.C = !1;
      this.H = this.i = this.F = 0;
      this.next = this.ca = null;
      this.u = 0;
    }
  }
  function Y(a, b) {
    for (let c = 0; c < a.length; ++c) if (a[c].type === b) return !0;
    return !1;
  }
  function ud(a) {
    for (let b = 0; b < a.length; ++b)
      switch (a[b].type) {
        case "space":
        case "comment":
        case "newline":
          break;
        default:
          return b;
      }
    return -1;
  }
  function vd(a) {
    switch (null == a ? void 0 : a.type) {
      case "alias":
      case "scalar":
      case "single-quoted-scalar":
      case "double-quoted-scalar":
      case "flow-collection":
        return !0;
      default:
        return !1;
    }
  }
  function wd(a) {
    switch (a.type) {
      case "document":
        return a.start;
      case "block-map":
        a = a.items[a.items.length - 1];
        let b;
        return null != (b = a.j) ? b : a.start;
      case "block-seq":
        return a.items[a.items.length - 1].start;
      default:
        return [];
    }
  }
  function xd(a) {
    if (0 === a.length) return [];
    let b = a.length;
    a: for (; 0 <= --b; )
      switch (a[b].type) {
        case "doc-start":
        case "explicit-key-ind":
        case "map-value-ind":
        case "seq-item-ind":
        case "newline":
          break a;
      }
    let c;
    for (; "space" === (null == (c = a[++b]) ? void 0 : c.type); );
    return a.splice(b, a.length);
  }
  function yd(a) {
    if ("flow-seq-start" === a.start.type)
      for (const b of a.items)
        !b.j ||
          b.value ||
          Y(b.start, "explicit-key-ind") ||
          Y(b.j, "map-value-ind") ||
          (b.key && (b.value = b.key),
          delete b.key,
          vd(b.value)
            ? b.value.end
              ? Array.prototype.push.apply(b.value.end, b.j)
              : (b.value.end = b.j)
            : Array.prototype.push.apply(b.start, b.j),
          delete b.j);
  }
  function* zd(a, b) {
    a.F && 0 === a.offset && a.F(0);
    for (const c of kd(a.ca, b)) yield* a.next(c);
    yield* a.end();
  }
  function* Z(a) {
    const b = a.l[a.l.length - 1];
    if ("doc-end" !== a.type || (b && "doc-end" === b.type)) {
      if (!b) return yield* Ad(a);
      switch (b.type) {
        case "document":
          return yield* Bd(a, b);
        case "alias":
        case "scalar":
        case "single-quoted-scalar":
        case "double-quoted-scalar":
          return yield* Cd(a, b);
        case "block-scalar":
          return yield* Dd(a, b);
        case "block-map":
          return yield* Ed(a, b);
        case "block-seq":
          return yield* Fd(a, b);
        case "flow-collection":
          return yield* Gd(a, b);
        case "doc-end":
          return yield* Hd(a, b);
      }
      yield* a.pop();
    } else {
      for (; 0 < a.l.length; ) yield* a.pop();
      a.l.push({ type: "doc-end", offset: a.offset, source: a.source });
    }
  }
  function* Ad(a) {
    switch (a.type) {
      case "directive-line":
        yield { type: "directive", offset: a.offset, source: a.source };
        return;
      case "byte-order-mark":
      case "space":
      case "comment":
      case "newline":
        yield a.i;
        return;
      case "doc-mode":
      case "doc-start":
        const b = { type: "document", offset: a.offset, start: [] };
        "doc-start" === a.type && b.start.push(a.i);
        a.l.push(b);
        return;
    }
    yield { type: "error", offset: a.offset, message: `Unexpected ${a.type} token in YAML stream`, source: a.source };
  }
  function* Bd(a, b) {
    if (b.value) return yield* Id(a, b);
    switch (a.type) {
      case "doc-start":
        -1 !== ud(b.start) ? (yield* a.pop(), yield* Z(a)) : b.start.push(a.i);
        return;
      case "anchor":
      case "tag":
      case "space":
      case "comment":
      case "newline":
        b.start.push(a.i);
        return;
    }
    (b = Jd(a, b))
      ? a.l.push(b)
      : yield {
          type: "error",
          offset: a.offset,
          message: `Unexpected ${a.type} token in YAML document`,
          source: a.source,
        };
  }
  function* Cd(a, b) {
    if ("map-value-ind" === a.type) {
      const c = xd(wd(a.l[a.l.length - 2]));
      let d;
      b.end ? ((d = b.end), d.push(a.i), delete b.end) : (d = [a.i]);
      b = { type: "block-map", offset: b.offset, h: b.h, items: [{ start: c, key: b, j: d }] };
      a.C = !0;
      a.l[a.l.length - 1] = b;
    } else yield* Id(a, b);
  }
  function* Dd(a, b) {
    switch (a.type) {
      case "space":
      case "comment":
      case "newline":
        b.Ka.push(a.i);
        break;
      case "scalar":
        b.source = a.source;
        a.H = !0;
        a.h = 0;
        if (a.F) for (b = a.source.indexOf("\n") + 1; 0 !== b; ) a.F(a.offset + b), (b = a.source.indexOf("\n", b) + 1);
        yield* a.pop();
        break;
      default:
        yield* a.pop(), yield* Z(a);
    }
  }
  function* Ed(a, b) {
    var c = b.items[b.items.length - 1];
    switch (a.type) {
      case "newline":
        a.C = !1;
        if (c.value) {
          c = "end" in c.value ? c.value.end : void 0;
          var d;
          "comment" === (null == (d = Array.isArray(c) ? c[c.length - 1] : void 0) ? void 0 : d.type)
            ? null == c || c.push(a.i)
            : b.items.push({ start: [a.i] });
        } else c.j ? c.j.push(a.i) : c.start.push(a.i);
        return;
      case "space":
      case "comment":
        if (c.value) b.items.push({ start: [a.i] });
        else if (c.j) c.j.push(a.i);
        else {
          if (Kd(a, c.start, b.h)) {
            var e, f;
            d = null == (e = b.items[b.items.length - 2]) ? void 0 : null == (f = e.value) ? void 0 : f.end;
            if (Array.isArray(d)) {
              Array.prototype.push.apply(d, c.start);
              d.push(a.i);
              b.items.pop();
              return;
            }
          }
          c.start.push(a.i);
        }
        return;
    }
    if (a.h >= b.h) {
      d = !a.C && a.h === b.h && c.j;
      e = [];
      if (d && c.j && !c.value) {
        f = [];
        for (let g = 0; g < c.j.length; ++g) {
          const h = c.j[g];
          switch (h.type) {
            case "newline":
              f.push(g);
              break;
            case "space":
              break;
            case "comment":
              h.h > b.h && (f.length = 0);
              break;
            default:
              f.length = 0;
          }
        }
        2 <= f.length && (e = c.j.splice(f[1]));
      }
      switch (a.type) {
        case "anchor":
        case "tag":
          d || c.value
            ? (e.push(a.i), b.items.push({ start: e }), (a.C = !0))
            : c.j
            ? c.j.push(a.i)
            : c.start.push(a.i);
          return;
        case "explicit-key-ind":
          c.j || Y(c.start, "explicit-key-ind")
            ? d || c.value
              ? (e.push(a.i), b.items.push({ start: e }))
              : a.l.push({ type: "block-map", offset: a.offset, h: a.h, items: [{ start: [a.i] }] })
            : c.start.push(a.i);
          a.C = !0;
          return;
        case "map-value-ind":
          Y(c.start, "explicit-key-ind")
            ? c.j
              ? c.value
                ? b.items.push({ start: [], key: null, j: [a.i] })
                : Y(c.j, "map-value-ind")
                ? a.l.push({ type: "block-map", offset: a.offset, h: a.h, items: [{ start: e, key: null, j: [a.i] }] })
                : vd(c.key) && !Y(c.j, "newline")
                ? ((b = xd(c.start)),
                  (d = c.key),
                  (e = c.j),
                  e.push(a.i),
                  delete c.key,
                  delete c.j,
                  a.l.push({ type: "block-map", offset: a.offset, h: a.h, items: [{ start: b, key: d, j: e }] }))
                : 0 < e.length
                ? (c.j = c.j.concat(e, a.i))
                : c.j.push(a.i)
              : Y(c.start, "newline")
              ? Object.assign(c, { key: null, j: [a.i] })
              : ((c = xd(c.start)),
                a.l.push({ type: "block-map", offset: a.offset, h: a.h, items: [{ start: c, key: null, j: [a.i] }] }))
            : c.j
            ? c.value || d
              ? b.items.push({ start: e, key: null, j: [a.i] })
              : Y(c.j, "map-value-ind")
              ? a.l.push({ type: "block-map", offset: a.offset, h: a.h, items: [{ start: [], key: null, j: [a.i] }] })
              : c.j.push(a.i)
            : Object.assign(c, { key: null, j: [a.i] });
          a.C = !0;
          return;
        case "alias":
        case "scalar":
        case "single-quoted-scalar":
        case "double-quoted-scalar":
          f = Ld(a, a.type);
          d || c.value
            ? (b.items.push({ start: e, key: f, j: [] }), (a.C = !0))
            : c.j
            ? a.l.push(f)
            : (Object.assign(c, { key: f, j: [] }), (a.C = !0));
          return;
        default:
          if ((f = Jd(a, b))) {
            d && "block-seq" !== f.type && Y(c.start, "explicit-key-ind") && b.items.push({ start: e });
            a.l.push(f);
            return;
          }
      }
    }
    yield* a.pop();
    yield* Z(a);
  }
  function* Fd(a, b) {
    var c = b.items[b.items.length - 1];
    switch (a.type) {
      case "newline":
        if (c.value) {
          c = "end" in c.value ? c.value.end : void 0;
          var d;
          "comment" === (null == (d = Array.isArray(c) ? c[c.length - 1] : void 0) ? void 0 : d.type)
            ? null == c || c.push(a.i)
            : b.items.push({ start: [a.i] });
        } else c.start.push(a.i);
        return;
      case "space":
      case "comment":
        if (c.value) b.items.push({ start: [a.i] });
        else {
          if (Kd(a, c.start, b.h)) {
            let e, f;
            d = null == (e = b.items[b.items.length - 2]) ? void 0 : null == (f = e.value) ? void 0 : f.end;
            if (Array.isArray(d)) {
              Array.prototype.push.apply(d, c.start);
              d.push(a.i);
              b.items.pop();
              return;
            }
          }
          c.start.push(a.i);
        }
        return;
      case "anchor":
      case "tag":
        if (c.value || a.h <= b.h) break;
        c.start.push(a.i);
        return;
      case "seq-item-ind":
        if (a.h === b.h) {
          c.value || Y(c.start, "seq-item-ind") ? b.items.push({ start: [a.i] }) : c.start.push(a.i);
          return;
        }
    }
    if (a.h > b.h && (b = Jd(a, b))) {
      a.l.push(b);
      return;
    }
    yield* a.pop();
    yield* Z(a);
  }
  function* Gd(a, b) {
    var c = b.items[b.items.length - 1];
    if ("flow-error-end" === a.type) {
      do yield* a.pop(), (b = a.l[a.l.length - 1]);
      while (b && "flow-collection" === b.type);
    } else if (0 === b.end.length) {
      switch (a.type) {
        case "comma":
        case "explicit-key-ind":
          !c || c.j ? b.items.push({ start: [a.i] }) : c.start.push(a.i);
          return;
        case "map-value-ind":
          !c || c.value
            ? b.items.push({ start: [], key: null, j: [a.i] })
            : c.j
            ? c.j.push(a.i)
            : Object.assign(c, { key: null, j: [a.i] });
          return;
        case "space":
        case "comment":
        case "newline":
        case "anchor":
        case "tag":
          !c || c.value ? b.items.push({ start: [a.i] }) : c.j ? c.j.push(a.i) : c.start.push(a.i);
          return;
        case "alias":
        case "scalar":
        case "single-quoted-scalar":
        case "double-quoted-scalar":
          var d = Ld(a, a.type);
          !c || c.value
            ? b.items.push({ start: [], key: d, j: [] })
            : c.j
            ? a.l.push(d)
            : Object.assign(c, { key: d, j: [] });
          return;
        case "flow-map-end":
        case "flow-seq-end":
          b.end.push(a.i);
          return;
      }
      (b = Jd(a, b)) ? a.l.push(b) : (yield* a.pop(), yield* Z(a));
    } else
      (c = a.l[a.l.length - 2]),
        "block-map" === c.type &&
        (("map-value-ind" === a.type && c.h === b.h) || ("newline" === a.type && !c.items[c.items.length - 1].j))
          ? (yield* a.pop(), yield* Z(a))
          : "map-value-ind" === a.type && "flow-collection" !== c.type
          ? ((c = xd(wd(c))),
            yd(b),
            (d = b.end.splice(1, b.end.length)),
            d.push(a.i),
            (b = { type: "block-map", offset: b.offset, h: b.h, items: [{ start: c, key: b, j: d }] }),
            (a.C = !0),
            (a.l[a.l.length - 1] = b))
          : yield* Id(a, b);
  }
  function* Hd(a, b) {
    "doc-mode" !== a.type && (b.end ? b.end.push(a.i) : (b.end = [a.i]), "newline" === a.type && (yield* a.pop()));
  }
  function* Id(a, b) {
    switch (a.type) {
      case "comma":
      case "doc-start":
      case "doc-end":
      case "flow-seq-end":
      case "flow-map-end":
      case "map-value-ind":
        yield* a.pop();
        yield* Z(a);
        break;
      case "newline":
        a.C = !1;
      default:
        b.end ? b.end.push(a.i) : (b.end = [a.i]), "newline" === a.type && (yield* a.pop());
    }
  }
  function Jd(a, b) {
    switch (a.type) {
      case "alias":
      case "scalar":
      case "single-quoted-scalar":
      case "double-quoted-scalar":
        return Ld(a, a.type);
      case "block-scalar-header":
        return { type: "block-scalar", offset: a.offset, h: a.h, Ka: [a.i], source: "" };
      case "flow-map-start":
      case "flow-seq-start":
        return { type: "flow-collection", offset: a.offset, h: a.h, start: a.i, items: [], end: [] };
      case "seq-item-ind":
        return { type: "block-seq", offset: a.offset, h: a.h, items: [{ start: [a.i] }] };
      case "explicit-key-ind":
        return (
          (a.C = !0),
          (b = xd(wd(b))),
          b.push(a.i),
          { type: "block-map", offset: a.offset, h: a.h, items: [{ start: b }] }
        );
      case "map-value-ind":
        return (
          (a.C = !0),
          (b = xd(wd(b))),
          { type: "block-map", offset: a.offset, h: a.h, items: [{ start: b, key: null, j: [a.i] }] }
        );
    }
    return null;
  }
  function Kd(a, b, c) {
    return "comment" !== a.type || a.h <= c ? !1 : b.every((d) => "newline" === d.type || "space" === d.type);
  }
  function Ld(a, b) {
    if (a.F) {
      let c = a.source.indexOf("\n") + 1;
      for (; 0 !== c; ) a.F(a.offset + c), (c = a.source.indexOf("\n", c) + 1);
    }
    return { type: b, offset: a.offset, h: a.h, source: a.source };
  }
  class Md {
    constructor(a) {
      this.H = !0;
      this.U = !1;
      this.offset = this.h = 0;
      this.C = !1;
      this.l = [];
      this.type = this.source = "";
      this.ca = new td();
      this.F = a;
    }
    *next(a) {
      this.source = a;
      if (this.U) (this.U = !1), yield* Z(this), (this.offset += a.length);
      else {
        var b = Wc(a);
        if (b)
          if ("scalar" === b) (this.H = !1), (this.U = !0), (this.type = "scalar");
          else {
            this.type = b;
            yield* Z(this);
            switch (b) {
              case "newline":
                this.H = !0;
                this.h = 0;
                this.F && this.F(this.offset + a.length);
                break;
              case "space":
                this.H && " " === a[0] && (this.h += a.length);
                break;
              case "explicit-key-ind":
              case "map-value-ind":
              case "seq-item-ind":
                this.H && (this.h += a.length);
                break;
              case "doc-mode":
              case "flow-error-end":
                return;
              default:
                this.H = !1;
            }
            this.offset += a.length;
          }
        else
          yield* this.pop({ type: "error", offset: this.offset, message: `Not a YAML token: ${a}`, source: a }),
            (this.offset += a.length);
      }
    }
    *end() {
      for (; 0 < this.l.length; ) yield* this.pop();
    }
    get i() {
      return { type: this.type, offset: this.offset, h: this.h, source: this.source };
    }
    *pop(a) {
      const b = null != a ? a : this.l.pop();
      if (b)
        if (0 === this.l.length) yield b;
        else {
          a = this.l[this.l.length - 1];
          "block-scalar" === b.type
            ? (b.h = "indent" in a ? a.h : 0)
            : "flow-collection" === b.type && "document" === a.type && (b.h = 0);
          "flow-collection" === b.type && yd(b);
          switch (a.type) {
            case "document":
              a.value = b;
              break;
            case "block-scalar":
              a.Ka.push(b);
              break;
            case "block-map":
              var c = a.items[a.items.length - 1];
              if (c.value) {
                a.items.push({ start: [], key: b, j: [] });
                this.C = !0;
                return;
              }
              if (c.j) c.value = b;
              else {
                Object.assign(c, { key: b, j: [] });
                this.C = !Y(c.start, "explicit-key-ind");
                return;
              }
              break;
            case "block-seq":
              c = a.items[a.items.length - 1];
              c.value ? a.items.push({ start: [], value: b }) : (c.value = b);
              break;
            case "flow-collection":
              c = a.items[a.items.length - 1];
              !c || c.value
                ? a.items.push({ start: [], key: b, j: [] })
                : c.j
                ? (c.value = b)
                : Object.assign(c, { key: b, j: [] });
              return;
            default:
              yield* this.pop(), yield* this.pop(b);
          }
          ("document" !== a.type && "block-map" !== a.type && "block-seq" !== a.type) ||
            ("block-map" !== b.type && "block-seq" !== b.type) ||
            ((c = b.items[b.items.length - 1]) &&
              !c.j &&
              !c.value &&
              0 < c.start.length &&
              -1 === ud(c.start) &&
              (0 === b.h || c.start.every((d) => "comment" !== d.type || d.h < b.h)) &&
              ("document" === a.type ? (a.end = c.start) : a.items.push({ start: c.start }), b.items.splice(-1, 1)));
        }
      else yield { type: "error", offset: this.offset, source: "", message: "Tried to pop an empty stack" };
    }
  }
  function Nd(a, b = {}) {
    var c = !1 !== b.Ja;
    const { Xa: d, Ja: e } = { Xa: b.Xa || (c && new Vc()) || null, Ja: c };
    c = new Md(null == d ? void 0 : d.ib);
    b = new Uc(b);
    let f = null;
    for (const g of Tc(b, zd(c, a), a.length))
      if (!f) f = g;
      else if ("silent" !== f.options.Ya) {
        f.errors.push(
          new oc(
            g.o.slice(0, 2),
            "MULTIPLE_DOCS",
            "Source contains multiple documents; please use YAML.parseAllDocuments()"
          )
        );
        break;
      }
    e && d && (f.errors.forEach(qc(a, d)), f.O.forEach(qc(a, d)));
    return f;
  }
  class Od extends ka {}
  Od.prototype.S = ["org.kie.workbench.common.stunner.sw.marshall.Yaml", 0];
  class Pd extends Od {}
  ia("org.kie.workbench.common.stunner.sw.marshall.Yaml", Pd);
  ia("org.kie.workbench.common.stunner.sw.marshall.Yaml.beautify", function (a, b) {
    b.lineWidth = 600;
    a = Nd(a, b);
    a: {
      var c;
      let d = null;
      "function" === typeof b || Array.isArray(b) ? (d = b) : void 0 === c && b && (c = b);
      "string" === typeof c && (c = c.length);
      "number" === typeof c && ((c = Math.round(c)), (c = 1 > c ? void 0 : 8 < c ? { h: 8 } : { h: c }));
      if (void 0 === a) {
        let e, f;
        const { pa: g } = null != (f = null != (e = c) ? e : b) ? f : {};
        if (!g) {
          b = void 0;
          break a;
        }
      }
      b = new lc(a, d, c).toString(c);
    }
    return b;
  });
}.call(this));
