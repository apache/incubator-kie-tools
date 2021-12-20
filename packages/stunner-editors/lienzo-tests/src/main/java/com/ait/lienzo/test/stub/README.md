# Out of the box Lienzo's testing doubles

This package contain some testing doubles for overlay types, wrappers and other classes that are using native interfaces.

- Here are some provided stub classes that are used out-of-the box by `com.ait.lienzo.test.translator.LienzoStubTranslatorInterceptor`.

- If you need to stub the methods for a given JSO class, please ensure no native interfaces are present in your stub.
  You have to provide an stub class with the same methods declared on the original one but removing at least,
  the `native `keywords from methods and procing an implementation for each one.

- The stubs present in this package have all `final` methods modifiers stripped as well, so you can use regular mockito API for mocking those methods
  if you expect custom behaviors.

- If the stub you need is not here, you can provide a no-op methods stub for a given class ( that causes linkage issues with the native interface on the testing scope )
  by using the `com.ait.lienzo.test.annotation.Settings` annotation.
