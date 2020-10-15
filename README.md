# Hexagon Data Android SDK

This project contains Hexagon Match Platform Android SDK to be provided to clients to want implement our pixel into their Android mobile apps.


## Download the Hexagon Data SDK for Android

- Liga full

The current version of the SDK is 1.0.0.

## Updating the SDK

Update, unless otherwise indicated, can be done by just copying over
the previous version.

### Obtain Hexagon Data client ID

For any demo projects a client id of "1" can be used.

## Integrating the Hexagon Data SDK

Add the library as a dependency in gradle, it is in jcenter and maven central:

```
compile 'com.hexagondata:cc-hexagonmatch-sdk'
```

Alternatively, you can build the jar manually from the code with `./gradlew clean jarRelease`. The jar file
will be available in the build\libs directory. Then you can add that jar as a library to another project.

Copy the downloaded SDK (jar file) to your local folder. In your
project, create a folder named “libs” if it does not exist already.

--image

Copy hdsdkmatch.jar to the libs folder. Right click on your project and
select “Properties”.

--image

Select “Java Build Path” from the left side of the properties window
and select “Libraries” tab from the right side.

--image

Click “Add JARs” button. Select hdsdkmatch.jar” from project/libs/
folder and select “OK” button.
--image


Goto “Order and Export” tab on the properties window and check
hdsdkmatch.jar” and click “OK”.

--image

### Edit AndroidManifest.xml

Hexagon Data SDK needs the following permissions to work properly. Add
these permissions in `AndroidManifest.xml`.


```xml
<uses-permission android:name="android.permission.INTERNET"/>
```


### Obfuscating

If your project is using proguard to obfuscate, Hexagon Data SDK must be
excluded from getting obfuscated or shrinked. To do this, add the
following to your `proguard-project.txt` or proguard configuration
file.


```
-keep class com.hexagonmatch.** {*;}
```

### Import the SDK

In `MainActivity` add import statement to include the SDK

```java
import com.pixel.hexagonmatchsdk.HexagonMatch;
```

### Creating instance

Instance to Hexagon Data SDK can be created anywhere as required. Usually
it is created in `onCreate()` method of `MainActivity`


```java
pcHttps = new HexagonMatch(getApplicationContext(), hm_clientId, hm_keyValue, hm_keyType, hm_tag, hm_platform);
```

The `getInstance()` method accepts the following params:

* Activity activity
* Context appContext
* boolean devMode - Flag to enable devMode for debugging. If this is on, then actual calls are not made but the URLs are displayed in toast
* boolean httpsEnabled - Flag to determine if https or http would be used to connect
* String siteId - The site ID
* String version - This value can be “[app name]-[app version]”
* DataPostedListener listener - Listener callback implementation to be called when data is sent
* Handler handler - Handler object from UI Thread
* boolean useWebView - Flag determines if a web view would be used or direct calls would be made to tags server. It is recommended to set this value to true

**NOTE:** For all the calls, irrespective of whether using webview or making a direct call, the SDK will try to fetch the Advertising ID and send it as 'adid' query parameter. If user has limited ad tracking enabled from Google Settings or it is not supported by the device then the value will not be sent.

### Passing a value

```java
bk.put(key, value);
```
**NOTE:** For data to be sent, if using Hexagon Data's SettingsActivity to show the opt-in screen, user should have the 'Allow Hexagon Data to receive my data' setting checked. This setting can also be set/unset programmatically using the following method. Setting it to true allows collection and sending of data, whereas if it's false, there's no collection or sending of data.

```java
bk.setOptInPreference(true);
```

### Passing multiple values

Create a `Map<String, String>` and populate the map with key and
values. Pass the map to Hexagon Data using

```java
bk.putAll(map);
```

### Resuming data post

The `resume()` method should be invoked on the calling activity's
`onResume()` callback. This should be done in order to send out any
queued data, which could not be sent because the application was
closed or due to network issues. Override the `onResume()` method and
call the `resume()` method as follows

```java
@Override
protected void onResume() {
   super.onResume();
   bk.resume();
}
```

### Monitoring post status (optional)

To get notifications about the status of data posting, implement the
following callback method in `MainActivity`


```java
@Override
public void onDataPosted(boolean success, String message){
}
```


# Sample Application

A sample application is available in the main repository.  After
building you should see the following:




### Complete Example
```

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//Import our library
import com.pixel.hexagonmatchsdk.HexagonMatch;

public class SampleActivity extends Activity {
    private HexagonMatch pcHttps;
    private String hm_keyType = "email";
    private String hm_tag = "FRES45";
    private String hm_platform = "4";
    private Integer hm_clientId = 4000;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // You can call our Pixel in every part or function that you want/need
        // in this example we put the function into a click Button.

        Button boton = findViewById(R.id.enviarbtn);
        boton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               EditText valueTxt = findViewById(R.id.plain_text_input);
               String hm_keyValue = valueTxt.getText().toString();
               TextView txtView  = findViewById(R.id.textView1);
               valueTxt.setVisibility(View.INVISIBLE);

               // In this case we recollect the email and pass to Pixel.
              pcHttps = new HexagonMatch(getApplicationContext(), hm_clientId, hm_keyValue, hm_keyType, hm_tag, hm_platform);

               txtView.setVisibility(View.VISIBLE);

           }
       });
    }


}
```
Call the function in the place that you consider the correct.
