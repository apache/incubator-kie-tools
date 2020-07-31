# Kogito Tooling i18n

This library provides a type-safe i18n implementation for a react project

## Install

Can be installed with `yarn` or `npm`:

- `yarn add @kogito-tooling/i18n`
- `npm install @kogito-tooling/i18n`

## Recommended setup

File structure:

```
i18n
├── index.ts
├── MyDictionary.ts
└── locales
    ├── en.ts
    └── index.ts
```

- Create a dictionary type which extends the type `ReferenceDictionary<D>`:

```tsx
"./i18n/MyDictionary.ts"

type MyDictionary extends ReferenceDictionary<MyDictionary> = {
    myWord: string;
    myFuncion: (user: string) => string;
    myNestedObject: {
        myNestedWord: string;
    }
}
```

- Create a dictionary that use the `MyDictionary` type:

```tsx
"./i18n/locales/en.ts"

import "MyDictionary" from "..";

const en: MyDictionary = {
    myWord: "My word";
    myFuncion: (user: string) => `Hi ${user}`;
    myNestedObject: {
        myNestedWord: `My ${"Nested".bold()} word`;
    }
}
```

- Create some auxiliaries constants, and a custom hook that will be useful on nested custom components

```tsx
"./i18n/locales/index.ts";

import * as React from "react";
import { useContext } from "react";
import { I18nContextType } from "@kogito-tooling/i18n";
import { en } from "./en";
import { MyDictionary } from "..";

export const myAppI18nDefaults = { locale: "en", dictionary: en };
export const myAppI18nDictionaries = new Map([["en", en]]);
export const MyAppI18nContext = React.createContext<I18nContextType<MyDictionary>>({} as any);

export function useMyAppI18n() {
  return useContext(MyAppI18nContext);
}
```

- Use `I18nDictionariesProvider` on the top-level of your App to have access to the `i18n` object using the custom hook or with the `Consumer` from the `Context`:

```tsx
import * as React from "react";
import { I18nDictionariesProvider } from "@kogito-tooling/i18n";
import { myAppI18nDefaults, myAppI18nDictionaries, MyAppI18nContext, useMyAppI18n } from "./i18n";

function App() {
  return (
    <I18nDictionariesProvider defaults={myAppI18nDefaults} dictionaries={myAppI18nDictionaries} ctx={MyAppI18nContext}>
      // Using the Consumer from Context
      <MyAppI18nContext.Consumer>
        {({ i18n }) => <p>{i18n.myFunction("John Doe")}</p>}
        <MyCustomComponent />
      </MyAppI18nContext.Consumer>
    </I18nDictionariesProvider>
  );
}

// Using the custom hook created on ./i18n/locales/index.ts
function MyCustomComponent() {
  const { i18n } = useMyAppI18n();
  return (
    <div>
      // Render a string with HTML tags
      <I18nHtml>{i18n.myNestedObject.myNestedWord} :)</I18nHtml>
      <p>{i18n.nestedObject}</p>
    </div>
  );
}
```

## Important Types
 - `ReferenceDictionary<D>`
 The type of the default dictionary
 
- `TranslatedDictionary<D>`
 The type of any other dictionary that isn't the default
 
- `I18nContextType<D>`
 The context type, provides an object with the following properties:
```
{
 locale: string // current locale
 setLocale: React.Dispatch<string> // a function to set the desired locale
 i18n: D // Dictionary
}
```

## Components
- `<I18nDictionariesProvider>`
Provides the `I18nContextType<D>`

- `<I18nHtml>` Render a string with HTML tags
