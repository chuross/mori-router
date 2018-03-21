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

## Current
### definition
```kotlin
@RouterPath(
        name = "main",
        uri = "morirouter://main"
)
class MainScreenFragment : Fragment() {

    @RouterParam
    lateinit var param1: String

    @RouterParam(name = "ieei")
    lateinit var param2: String

    @RouterParam(required = false)
    var param3: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainScreenBinder.bind(this)
    }
}
```

## Generated code
```kotlin
public final class MoriRouter {
  private FragmentManager fm;

  private int containerId;

  public MoriRouter(FragmentManager fm, int containerId) {
    this.fm = fm;
    this.containerId = containerId;
  }

  public MainScreenLauncher main(String param1, String ieei) {
    return new MainScreenLauncher(fm, containerId, param1, ieei);
  }

  public void pop() {
    fm.popBackStackImmediate();
  }
}
```