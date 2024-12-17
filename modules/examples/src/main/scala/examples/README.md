## Examples

Runnable with `scala-cli`.

Source file | Description
:---|:---
[ex00_constantResponse](ex00_constantResponse.scala) | The most basic: serves constant response.
[ex01_basicRouter](ex01_basicRouter.scala) | Uses `Router` effect and its partial-function DSL.
[ex02_composingRoutes](ex02_composingRoutes.scala) | Combines routes as with `Alternative` functor.
[ex03_accessingRequest](ex03_accessingRequest.scala) | Uses reader effect to access `Request`.
[ex04_localEffects](ex04_localEffects.scala) | Uses effects handled at request scope.
[ex05_jsonBody](ex05_jsonBody.scala) | JSON body encoding & decoding.
