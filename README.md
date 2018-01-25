# Reagent-dev-tools

[![Clojars Project](http://clojars.org/metosin/reagent-dev-tools/latest-version.svg)](http://clojars.org/metosin/reagent-dev-tools)

## Features

- Display state tree from Reagent atoms
  - Supports multiple atoms
- Styles embedded for easy use
- Simple toggleable development tool panel which can be extended with own tabs

## Configuration

To enable, add `reagent-dev-tools.preload` to your `:compiler-options` `:preloads`.
This will ensure that Dev tool is only included in the output JS for
development builds.

To configure dev tool in this setup, one can use `:external-config :reagent-dev-tools` option:

```edn
:external-config {:reagent-dev-tools {:state-atom example.main/state
                                      :panels-fn example.main/dev-panels}}}}
```

- `:state-atom` should refer to the var which is the Reagent state atom
- `:panels-fn` should refer to function which will return map of additional panels

To make your own application to make room for the dev-tool, you can for
example add some padding to your app when the dev-tool is open:

```cljs
(defn main-view []
  [:div.main-view
   (if (:open? @dev-tools/dev-state)
     {:style {:padding-bottom (str (:height @dev-tools/dev-state) "px")}})
   ...])
```

### Manual use

If one wants to include Dev tool in production builds, `reagent-dev-tools.core/start!`
can be called with options map.

Reagent component `reagent-dev-tools.core/dev-tool` can also be used directly
as part of Reagent applications.

## License

Copyright © 2015-2018 [Metosin Oy](http://www.metosin.fi)

Distributed under the Eclipse Public License, the same as Clojure.
