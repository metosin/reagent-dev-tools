# 1.0.0 (Unreleased)

[compare](https://github.com/metosin/reagent-dev-tools/compare/0.4.2...1.0.0)

- **New**:
    - If `:state-atom` value isn't given, no default panel is added if `:panels`
    is defined. If neither is provided, help text is displayed.
    - New `:panels` option
        - Panels are now defined as a vector, so they keep their order.
        - Panels are appended to default panels.
    - State-tree component and functions is now accessible through the core namespace:
        - `reagent-dev-tools.core/state-tree`
        - `reagent-dev-tools.core/register-collection-info-handler!`
        - `reagent-dev-tools.core/collection-info-handler`
- **Breaking**:
    - Removed `register-state-atom`
        - Use `:panels` with additional `state-tree` components instead.
    - Removed `:panels-fn` option
        - Use new `:panels` list, which is just a vector instead of map returning map.
    - Requires Reagent 1.0.0+
- Add `max-width: 100vw` to prevent vertical panel being wider than screen width
- Navigation bar panel list now wraps to multiple lines if it doesn't fit on one line

# 0.4.2 (2022-03-18)

[compare](https://github.com/metosin/reagent-dev-tools/compare/0.4.1...0.4.2)

- Reset `font: inherit` rule from tailwind
- Reset `line-height`

# 0.4.1 (2022-03-18)

[compare](https://github.com/metosin/reagent-dev-tools/compare/0.4.0...0.4.1)

- Ensure text `color`, `font-style` and `font-weight` are reset inside the panel

# 0.4.0 (2021-01-12)

[compare](https://github.com/metosin/reagent-dev-tools/compare/0.3.1...0.4.0)

- Add way to control collection description text for custom types,
like Linked:

```
(state-tree/register-collection-info-handler
  lm/LinkedMap
  #(state-tree/collection-info-handler "LinkedMap" "{LinkedMap, " (count %) " keys}"))
```

# 0.3.1 (2020-12-30)

[compare](https://github.com/metosin/reagent-dev-tools/compare/0.3.0...0.3.1)

- Added toggle collection to state atoms
- Add `:state-atom-name` option to customize name for state atom added using `start!`
- Ensure `nil`, vectors and other things as map keys in the state tree
  are rendered.

# 0.3.0 (2020-12-07)

[compare](https://github.com/metosin/reagent-dev-tools/compare/0.2.1...0.3.0)

- Add option to toggle panel placement between bottom and right
- Add `:margin-element` option to automatically set margin-bottom/right on some
element, so that panel doesn't go over the application content
- Store open paths on state tree to local storage

# 0.2.1 (2020-03-06)

- Use `reagent.dom/render` instead of `reagent.core/render` to prepare for
next Reagent releases

# 0.2.0 (2018-01-25)

- Made the panel resizeable
- Save the state (open, height, active panel) on `localStorage`
- Added `start!` function for easier configuration
- Added some colors to the state tree
- Add collection type name to state tree
    - Type can be clicked to open/close collection items
- Fixes

# 0.1.0 (2015-11-29)

- Initial release
