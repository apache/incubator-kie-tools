NativeConcreteJsType = function () {
  this.message = function () {
    return "I am a native type!";
  };
};

NativeConcreteJsTypeWithConstructorDependency = function (nativeConcreteJsType) {
  this.get = function () {
    return nativeConcreteJsType;
  };
};

NativeConcreteJsTypeWithFieldDependency = function () {
  var ref = this;
  this.get = function () {
    return ref.nativeConcreteJsType;
  };
};

ProducedNativeIface = function () {
  this.getMagicWord = function () {
    return "please";
  };
};

NativeFactory = function () {};

NativeFactory.create = function () {
  return new ProducedNativeIface();
};
