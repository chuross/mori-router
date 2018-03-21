# MoriRouter
Annotation based Android router library.

This library for single activity application.(multi fragments)

## Futures
- Auto generate routing codes
- DeepLink support
- Shared element support

## Goal(expected)
```
val router = MoriRouter(fragmentManager, R.id.container)

router.pop()

router.hoge().launch()
router.hoge(fuga).launch()
router.hoge(fuga).piyo(foo).launch()

// SharedElement support
router.hoge().shareFooImage(imageView).launch()

router.dispatch(Uri.parse("example://foo/1"))

-----

@RouterPath(
    name = "hoge",
    uri = "example://foo/{id}",
    transitionNames = *arrayOf("fooImage"),
    enterTransitionFactory = ExplodeSetFactory.class,
    exitTransitionFactory = ExplodeSetFactory.class
)
class HogeFragment

    @RouterParam
    lateinit var fuga: Fuga

    @RouterParam(name = "piyo", required = false)
    var piyo: String? = null

    @RouterPathParam(name = "id")
    var fooId: String? = null
```