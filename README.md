# Android MVC Framework
## How to use
1. Clone a copy of the repo

```bash
$ git clone git://Rmanaf/com.divankits.mvc.git
```

2. Add module in your project

3. Create a model and assign view by using "@View" annotation
```java
@View(R.layout.test_view)
public class TestModel extends Model {

    public CharSequence Title = "Test"
    
    public String Description;

    ...

}
```
4. Create layout for model
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" 
    android:layout_width="match_parent"
    android:layout_height="match_parent">


    <TextView
        android:id="@+id/title_txt"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

    ...
```
5. Bind view components to the model fields
```java
@View(R.layout.test_view)
public class TestModel extends Model {

    @Bind(value = R.id.title_txt , setter = "setText")
    public CharSequence Title;

    /* For custom components :
    
    @Bind(value = R.id.title_txt , setter = "name of setter method" , getter = "name of getter method")
    public CharSequence Title;

    */
...
```
6. Create controller

```java
public class TestController extends Controller {

    public TestController() {

        setModel(new TestModel())

    }

}
```
7. Add a FrameLayout in your activity layout
```xml
...

<FrameLayout
        android:id="@+id/placeholder"
        android:layout_width="match_parent"
        android:layout_height="match_parent"></FrameLayout>

...
``` 
8. Add controller instance to activity
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new TodoListController(this , R.id.placeholder);
    }

}
```
 For more information visit [Wiki](https://github.com/Rmanaf/com.divankits.mvc/wiki) page
# License
This project is licensed under the terms of the MIT license