import { immutableDeepMerge } from "../i18nProvider";
import { DeepOptional, TranslationBundle, TranslationBundleInterpolation } from "../types";

interface DummyBundle extends TranslationBundle<DummyBundle> {
  greeting: (name: string) => string;
  welcome: string;
  modal: {
    title: string;
  };
}
const interpolationFunction: TranslationBundleInterpolation = (name: string) => `Hi ${name}!`;

const dummyDefault: DummyBundle = {
  greeting: interpolationFunction,
  welcome: "Welcome",
  modal: {
    title: "My title"
  }
};

describe("I18nProvider::mergeSelectedDictionaryWithDefault", () => {
  it("should override the welcome property on dummyDefault and create a new object", () => {
    const dummyOptional: DeepOptional<DummyBundle> = {
      welcome: "Bienvenido"
    };

    const merged = immutableDeepMerge(dummyDefault, dummyOptional);

    expect(merged).toEqual({
      greeting: interpolationFunction,
      welcome: "Bienvenido",
      modal: {
        title: "My title"
      }
    });
    expect(dummyDefault).toEqual({
      greeting: interpolationFunction,
      welcome: "Welcome",
      modal: {
        title: "My title"
      }
    });
    expect(dummyOptional).toEqual({
      welcome: "Bienvenido"
    });
  });

  it("shouldn't override the welcome property on dummyDefault", () => {
    const dummyOptional: DeepOptional<DummyBundle> = {
      welcome: undefined
    };

    const merged = immutableDeepMerge(dummyDefault, dummyOptional);

    expect(merged).toEqual({
      greeting: interpolationFunction,
      welcome: "Welcome",
      modal: {
        title: "My title"
      }
    });
    expect(dummyDefault).toEqual({
      greeting: interpolationFunction,
      welcome: "Welcome",
      modal: {
        title: "My title"
      }
    });
    expect(dummyOptional).toEqual({
      welcome: undefined
    });
  });

  it("", () => {
    const dummyInterpolationFunction = (name: string, lastLogin: number) => `Hi ${name}. Last login: ${lastLogin}`
    const dummyOptional: DeepOptional<DummyBundle> = {
      greeting: dummyInterpolationFunction
    };

    const merged = immutableDeepMerge(dummyDefault, dummyOptional);

    expect(merged).toEqual({
      greeting: dummyInterpolationFunction,
      welcome: "Welcome",
      modal: {
        title: "My title"
      }
    });
    expect(dummyDefault).toEqual({
      greeting: interpolationFunction,
      welcome: "Welcome",
      modal: {
        title: "My title"
      }
    });
    expect(dummyOptional).toEqual({
      greeting: dummyInterpolationFunction
    });
  });
});
