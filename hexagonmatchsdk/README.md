[ ![Download](https://api.bintray.com/packages/hexagonmatch/Android-SDK/hd-android-sdk/images/download.svg) ](https://bintray.com/hexagonmatch/Android-SDK/hd-android-sdk/_latestVersion)
# Hexagon Data Android SDK

This project contains Hexagon Match Platform Android SDK to be provided to clients to want implement our pixel into their Android mobile apps.

## Integrating the Hexagon Data SDK

Add the library as a dependency in gradle, it is in jcenter and maven central:

```
implementation 'com.pixel.hexagonmatchsdk.HexagonMatch:hexagonmatchsdk:1.0.0'
```

Alternatively...

### Edit AndroidManifest.xml

Hexagon Data SDK needs the following permissions to work properly. Add
these permissions in `AndroidManifest.xml`.


```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

The library need to put your App ID inside the AndroidManifest.
For more information please see https://googlemobileadssdk.page.link/admob-android-update-manifest

```xml
<manifest>
    <application>
        <!-- Sample AdMob App ID: ca-app-pub-3940256099942544~3347511713 -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
    </application>
</manifest>
```


### Obfuscating

If your project is using proguard to obfuscate, Hexagon Data SDK must be
excluded from getting obfuscated or shrinked. To do this, add the
following to your `proguard-project.txt` or proguard configuration
file.


```
-keep class com.pixel.hexagonmatchsdk.** {*;}
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
    private HexagonMatch pcHttps;
    private String hm_tag = "5003";
    private String hm_platform = "4";
    private Integer hm_clientId = 1;

    pcHttps = new HexagonMatch(getApplicationContext(), hm_clientId, hm_tag, hm_platform);
```

The `HexagonMatch` object accepts the following params:

* Context appContext
* clientId of  Hexagon Match Platform provided by Hexagon Data
* String tagId - provided by Hexagon Data.
* String Platform - provided by Hexagon Data

**NOTE:** For all the calls, irrespective of whether using webview or making a direct call, the SDK will try to fetch the Advertising ID and send it as query parameter. If user has limited ad tracking enabled from Google Settings or it is not supported by the device then the value will not be sent.

### Passing a value

```java
pcHttps.sendData(hm_keyValue, hm_keyType);
```

### Complete Example
```java
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
    private String hm_tag = "FRES45";
    private String hm_platform = "4";
    private Integer hm_clientId = 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // You can call our Pixel in every part or function that you want/need
        // in this example we put the function into a click Button.

        //Firstable we created the object before send the data.
        pcHttps = new HexagonMatch(getApplicationContext(),hm_clientId, hm_tag, hm_platform);

        Button boton = findViewById(R.id.enviarbtn);
        boton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String hm_keyType = "email";
               EditText valueTxt = findViewById(R.id.plain_text_input);
               String hm_keyValue = valueTxt.getText().toString();
               TextView txtView  = findViewById(R.id.textView1);
               valueTxt.setVisibility(View.INVISIBLE);

               // In this case we recollect the email and pass to Pixel.
              pcHttps.sendData(hm_keyValue, hm_keyType);

               txtView.setVisibility(View.VISIBLE);

           }
       });
    }


}
```
Call the function in the place that you consider the correct.
