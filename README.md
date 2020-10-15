# Hexagon Data Android SDK

This project contains Hexagon Match Platform Android SDK to be provided to clients to want implement our pixel into their Android mobile apps.

## Integrating the Hexagon Data SDK

Add the library as a dependency in gradle, it is in jcenter and maven central:

```
compile 'com.hexagondata:cc-hexagonmatch-sdk'
```

Alternatively, you can build the jar manually from the code with `./gradlew clean jarRelease`. The jar file
will be available in the build\libs directory. Then you can add that jar as a library to another project.

Incorporate the following general code into your app to send the data to your hexagonmatch account, replacing hm_clientId with the id provided by Hexagon Data:
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


### Edit AndroidManifest.xml

Hexagon Data SDK needs the following permissions to work properly. Add
these permissions in `AndroidManifest.xml`.


```xml
<uses-permission android:name="android.permission.INTERNET"/>
```
