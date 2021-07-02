## Setup example for a Typescript project

File structure:

```
i18n
├── index.ts
├── setup.ts
├── MyDictionary.ts
└── locales
    ├── en.ts
    ├── pt_BR.ts
    └── index.ts
```

- Create a dictionary type which extends the type `ReferenceDictionary<D>`:

```tsx
"./i18n/MyDictionary.ts";

interface MyDictionary extends ReferenceDictionary<MyDictionary> {
  myWord: string;
  myCurrentLocale: (locale: string) => string;
  myNestedObject: {
    myNestedWord: string;
  };
}
```

- Create a dictionary that use the `MyDictionary` type:

```tsx
"./i18n/locales/en.ts";

const en: MyDictionary = {
  myWord: "My word",
  myCurrentLocale: (locale: string) => `My current locale is: ${locale}`,
  myNestedObject: {
    myNestedWord: `My ${"Nested".bold()} word`,
  },
};
```

- Create a dictionary that use the `TranslatedDictionary<MyDictionary>` type.

_The `TranslatedDictionary<D>` has the same keys of the `MyDictionary`, but they're optionals.
The `TransletedDictionary` values override the default values on the `MyDictionary`, which prevents any missing translation._

```tsx
"./i18n/locales/pt_BR.ts";

const pt_BR: TranslatedDictionary<MyDictionary> = {
  myWord: "Minha palavra",
  myCurrentLocale: (locale: string) => `O meu local atual é: ${locale}`,
};
```

- Create the values that will be used by the I18nDictionariesProvider, and a custom hook that will be useful on nested custom components

```tsx
"./i18n/setup.ts";

export const myI18nDefaults: I18nDefaults<MyDictionary> = { locale: "en", dictionary: en };

// It's reccomended that the key follows the BCP-47 standard to be compatible with the browser locale
export const myI18nDictionaries: I18nDictionaries<MyDictionary> = new Map([
  ["en", en],
  ["pt-BR", pt_BR],
]);
```

- Use `I18n` on the top-level of your application and pass it down to your functions which uses i18n.

```ts
const my18n = new I18n(myI18nDefaults, myI18nDictionaries);

// Using the custom hook created on ./i18n/locales/index.ts
function myFunction(myI18n: I18n) {
  const i18n = myI18n.getCurrent();

  console.log(i18n.myWord);
}
```

_Remember: If you wish it's possible to use the Context directly with `MyAppI18nContext.Provider`!_
