# MoriRouter
Annotation based Android router library.

This library for single activity application.(multi fragments)

## Futures
- Auto generate routing codes
- DeepLink support
- Shared element support

## Usage
### Basic

1. Add annotations in your screen fragment.

```kotlin
@RouterPath(name = "main")
class MainScreenFragment : Fragment() {

    @Argument
    lateinit var param1: String

    @Argument(name = "ieei")
    lateinit var param2: Int

    @Argument(required = false)
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
val options = MoriRouterOptions.Builder(R.id.container)
                .setEnterTransition(transition) // `android.support.transition` or `android.transition`
                .setExitTransition(transition)

val router = MoriRouter(supportFragmentManager, options) // MoriRouter is auto generated class.

router
    .main("required1", 1000) // main(String param1, Integer ieei)
    .param3(listOf("fuga")) // optional value
    .launch() // launch main screen
```

### Fragment builder support
Also can use `@WithArguments` annotation.
This library generate {class_name}Builder code.

```kotlin
@WithArguments
class HogeScreenFragment : Fragment() {

    @Argument
    lateinit var hogeName: String

    ....
}
```

```kotlin
val fragment: Fragment = HogeScreenFragmentBuilder(hogeName).build() // HogeScreenFragmentBuilder is auto generated class
```

### DeepLink support
1. If use deepLink support, `uri` parameter add to `@RouterPath`, and add definition `@RouterUriParam` parameters in your screen fragment.

```kotlin
@RouterPath(
  name = "second",
  uris = [
    "example://hoge/{hoge_id}/{fuga}",
    "https://example.com/hoge/{hoge_id}/{fuga}" //also can use multiple uri
  ]
)
class SecondScreenFragment : Fragment() {

    @UriArgument(name = "hoge_id")
    var hogeId: Int

    @UriArgument
    var fuga: String

    // If use `@RouterUriParam`, Don't use `required = true`.
    @Argument(required = false)
    var piyo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecondScreenBinder.bind(this) // SecondScreenBinder is auto generated class.
    }
}
```

2. `MoriRouter` has `dispatch` method. Then call dispatch with Uri.

```kotlin
router.dispatch(Uri.parse("example://hoge/123/test")) // launch SecondScreenFragment (hogeId = 123, fuga=test)
router.dispatch(Uri.parse("https://example.com/hoge/123/test")) // launch SecondScreenFragment (hogeId = 123, fuga=test)
```

### SharedElement support
1. set transition name in your XML layout or in your code.

XML

```xml
<YourLayout
    ....
    android:transitionName="your_transition_name" />
```

Code

```java
ViewCompat.setTransitionName(yourView, "your_transition_name");
```

2. add enterTransitionFactory and exitTransitionFactory to `@RouterPath`.

```kotlin
@RouterPath(
        name = "third",
        enterTransitionFactory = ThirdScreenTransitionFactory::class,
        exitTransitionFactory = ThirdScreenTransitionFactory::class
)
class ThirdScreenFragment : Fragment() {
   ....
}
```

3. add SharedElements when before transition `third` screen.

```kotlin
router.third().addSharedElement(yourView).launch()
```