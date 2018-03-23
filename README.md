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

## Usage
### Basic

1. Add annotations in your screen fragment.

```kotlin
@RouterPath(name = "main")
class MainScreenFragment : Fragment() {

    @RouterParam
    lateinit var param1: String

    @RouterParam(name = "ieei")
    lateinit var param2: Int

    @RouterParam(required = false)
    var param3: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainScreenBinder.bind(this) // MainScreenBinder is auto generated class.
    }

    ....
}
```

2. Execute build command, Then `MoriRouter` class is auto generated.

```kotlin
val router = MoriRouter(supportFragmentManager, R.id.container) //This class is auto generated class.

router
    .main("required1", 1000) // main(String param1, Integer ieei)
    .param3(listOf("fuga")) // optional value
    .launch() // launch main screen
```

### DeepLink support
1. If use deepLink support, `uri` parameter add to `@RouterPath`, and add definition `@RouterUriParam` parameters in your screen fragment.

```kotlin
@RouterPath(name = "second", uri = "example://hoge/{hoge_id}/{fuga}")
class SecondScreenFragment : Fragment() {

    @RouterUriParam(name = "hoge_id")
    var hogeId: Int

    @RouterUriParam
    var fuga: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecondScreenBinder.bind(this) // SecondScreenBinder is auto generated class.
    }
}
```

2. `MoriRouter` has `dispatch` method. Then call dispatch with Uri.

```kotlin
router.dispatch(Uri.parse("example://hoge/123/test")) // launch SecondScreenFragment (hogeId = 123, fuga=test)
```
