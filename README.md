[![](https://jitpack.io/v/chuross/mori-router.svg)](https://jitpack.io/#chuross/mori-router)

# MoriRouter
Annotation based Android router library.

This library for single activity application.(multi fragments)

## Futures
- Auto generate routing codes
- Auto generate Fragment builder codes
- DeepLink support
- Shared element support

## Download
### Gradle
1. add JitPack repository to your project root `build.gradle`.
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```

2. add the dependency
[![](https://jitpack.io/v/chuross/mori-router.svg)](https://jitpack.io/#chuross/mori-router)

```groovy
dependencies {
    compile 'com.github.chuross.mori-router:annotation:x.x.x'
    annotationProcessor 'com.github.chuross.mori-router:compiler:x.x.x' // or kpt
}
```

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
                .build()

val router = MoriRouter(supportFragmentManager, options) // MoriRouter is auto generated class.

router
    .main("required1", 1000) // main(String param1, Integer ieei)
    .param3(listOf("fuga")) // optional value
    .launch() // launch main screen


// pop screen
router.pop()
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

### override enter / exit transition
```
@RouterPath(
    name = "main",
    overrideEnterTransitionFactory = MainScreenTransitionFactory::class,
    overrideExitTransitionFactory = MainScreenTransitionFactory::class
)
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
    android:id="@+id/your_id" <!-- must have view id -->
    android:transitionName="your_transition_name" />
```

Code

```java
// yourView must has view id
// ex) yourView.setId(R.id.your_id)
ViewCompat.setTransitionName(yourView, "your_transition_name");
```

2. add sharedEnterTransitionFactory and sharedExitTransitionFactory to `@RouterPath`.

```kotlin
@RouterPath(
    name = "third",
    sharedEnterTransitionFactory = ThirdScreenSharedTransitionFactory::class,
    sharedExitTransitionFactory = ThirdScreenSharedTransitionFactory::class
)
class ThirdScreenFragment : Fragment() {
   ....

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ThirdScreenFragment must has `R.id.your_id` view
        // this id is same before screen's shared element id
        ThirdScreenFragmentBinder.bindElement(this, R.id.your_id) // This class and method are auto generated.
   }
}
```

3. add SharedElements when before transition `third` screen.

```kotlin
router.third().addSharedElement(yourView).launch()
```

## License
```
Copyright 2018 chuross

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```