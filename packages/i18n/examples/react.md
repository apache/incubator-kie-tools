## Setup example for a React project

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

export const myAppI18nDefaults: I18nDefaults<MyDictionary> = { locale: "en", dictionary: en };

// It's reccomended that the key follows the BCP-47 standard to be compatible with the browser locale
export const myAppI18nDictionaries: I18nDictionaries<MyDictionary> = new Map([
  ["en", en],
  ["pt-BR", pt_BR],
]);

export const MyAppI18nContext = React.createContext<I18nContextType<MyDictionary>>({} as any);

export function useMyAppI18n() {
  return useContext(MyAppI18nContext);
}
```

- Use `I18nDictionariesProvider` on the top-level of your App to have access to the `i18n` object using the custom hook.

```tsx
function App() {
  return (
    <I18nDictionariesProvider defaults={myAppI18nDefaults} dictionaries={myAppI18nDictionaries} ctx={MyAppI18nContext}>
      <MyCustomComponent />
    </I18nDictionariesProvider>
  );
}

// Using the custom hook created on ./i18n/locales/index.ts
function MyCustomComponent() {
  const { locale, setLocale, i18n } = useMyAppI18n();

  return (
    <div>
      {/* `myNestedWord` will always fallback on the 'en' dictionary because the pt-BR doesn't provide it. */}
      <I18nHtml>{i18n.myNestedObject.myNestedWord} :)</I18nHtml>

      {/* `myWord` will change accordling to the selected locale*/}
      <p>{i18n.myWord}</p>
      <a onClick={() => setLocale("pt-BR")}>pt-BR</a>
      <a onClick={() => setLocale("en")}> en</a>

      <p>{i18n.myCurrentLocale(locale)}</p>
    </div>
  );
}
```

_Remember: If you wish it's possible to use the Context directly with `MyAppI18nContext.Provider`!_
