package com.pixel.hexagonmatchsdk;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;

import java.util.HashMap;
import java.util.Map;
import android.os.Build;

/**
 *
 * The MIT License (MIT)
 *
 *  Copyright (c) 2020 Hexagon Data
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * *******************************************************************************
 *
 * Hexagon Data Platform data collection for Android. This
 * library provides methods to collect data managed by the Hexagon Match Platform.
 *
 * The SDK will attempt to get the Google Advertising ID and If the SDK determines that the Limit Ad Tracking
 * preference is set to true, the SDK will NOT collect, send, or extract the Google Advertising ID.
 *
 *
 * The general pattern of use is:
 *
 * <pre>
 * {#code
 * // Instantiate a cc instance configured for https calls.
 * HexagonMatch pixel = new HexagonMatch(getApplicationContext(), CLIENT_ID, keyValue, keytype,tag,platform);
 *
 * // Determine if the user has opted out of ad collection
 * boolean optedOut = pixel.isLimitedAdTrackingEnabled();
 *
 */
public class HexagonMatch {
    public static final String LOG_TAG = "Hexagon Match Pixel";
    private static final String HL_SUBDOMAIN = "hexagondata.com";
    private static final String DEFAULT_DOMAIN = "locate.";
    private static final String DEFAULT_PIXEL = "/pixel.png";
    private static int CONNECTION_TIMEOUT = 5 * 1000;//5 seconds
    private static String PROTOCOL_SSL = "https://";
    private Context context;
    private int clientId = 1;
    private String PlatformId;
    private String TagId;
    private String ModelDevice;
    private String ManufacturerDevice;
    private String BrandDevice;
    private String BoardDevice;
    private String Device;
    private CharSequence NameApp;
    protected static boolean debug = false;


    public enum IdType {
        SHA1,GAID;
    }


    public class Id {
        String mid;
        IdType idType;
    }

    private Id id;

    /**
     * Indicates whether the Google Play limited ad tracking is enabled.  If
     * we do not have access to the Google Play service then it will be
     * false by default.
     */
    private boolean limitedAdTrackingEnabled;

    private boolean googleAdvertiserIdAvailable;


    Thread setupThread;

    /**
     * Indicates whether or not the instance has been initialized.
     */
    private boolean initialized;


    /**
     * Constructs a HexagonMatch instance.
     *
     * @param ctx
     * @param clientId
     * @param tagId
     * @param platformId
     */
    public HexagonMatch(Context ctx, int clientId, String tagId, String platformId) {
        init(ctx, clientId, tagId, platformId);
    }


    private void init(Context ctx, int clientId, String tagId, String platformId) {
        setInitialized(false);
        enableDebug(false);
        this.setContext(ctx);
        this.clientId = clientId;


        final Resources appR = ctx.getResources();
        this.NameApp = "&app=" + appR.getText(appR.getIdentifier("app_name",
                "string", ctx.getPackageName()));
        this.PlatformId  = "&platform_id=" + platformId;
        this.TagId  = "&tag_id=" + tagId;
        this.ModelDevice = "&ml=" + Build.MODEL;
        this.ManufacturerDevice = "&mf=" + Build.MANUFACTURER;
        this.BrandDevice = "&bra=" + Build.BRAND;
        this.BoardDevice = "&br=" + Build.BOARD;
        this.Device = "&d=" + Build.DEVICE;

        if (HexagonMatch.debug) Log.d(HexagonMatch.LOG_TAG, "Setting up");

        final Context contextFinal = ctx;
        Runnable runnable = new Runnable() {

            public void run() {
                setGoogleAdvertiserIdAvailable(false);
                setLimitedAdTrackingEnabled(false);
                String id = Utils.getUuid(contextFinal);
                IdType idType = IdType.SHA1;
                try {
                    Info adInfo = null;
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(contextFinal);
                    if (adInfo != null) {
                        if (HexagonMatch.debug)
                            Log.d(HexagonMatch.LOG_TAG, "access to the Google Play");
                        setGoogleAdvertiserIdAvailable(true);
                        setLimitedAdTrackingEnabled(adInfo.isLimitAdTrackingEnabled());
                        id = adInfo.getId();
                        idType = IdType.GAID;
                        if (HexagonMatch.debug)
                            Log.d(HexagonMatch.LOG_TAG, "AdvertiserId  = " + id);
                        if (HexagonMatch.debug)
                            Log.d(HexagonMatch.LOG_TAG, "isLimitedAdTrackingEnabled = " + isLimitedAdTrackingEnabled());
                    } else {
                        if (HexagonMatch.debug)
                            Log.d(HexagonMatch.LOG_TAG, "adInfo is null, unable to access the Google Play AdvertiserId data.  Using the hashed android id and unable to check the ad tracking preferences");
                    }

                } catch (Exception e) {
                    if (HexagonMatch.debug)
                        Log.d(HexagonMatch.LOG_TAG, "Exception thrown attempting to access Google Play Service to retrieve AdvertiserId data; e = " + e.toString());
                } finally {

                    setIdAndType(id, idType);
                    startSession();
                    setInitialized(true);
                }
            }
        };

        try {
            setupThread = new Thread(runnable);
            if (HexagonMatch.debug)
                Log.d(HexagonMatch.LOG_TAG, "Starting Thread which will gather id and ad tracking preferences");
            setupThread.start();
        } catch (Exception e) {
            if (HexagonMatch.debug)
                Log.e(HexagonMatch.LOG_TAG, "Unable to run the thread which determines the id and ad tracking preferences");
            if (HexagonMatch.debug) Log.e(HexagonMatch.LOG_TAG, "Exception e = " + e.toString());
        }
    }

    /**
     * Set to true to show log messages, or false to hide log messages
     *
     * @param debug defaults to false
     */
    public static void enableDebug(boolean debug) {
        HexagonMatch.debug = debug;
    }



    /**
     * Will return either the Advertiser ID or the SHA-1 hash of the value
     * returned by the Secure.ANDROID_ID android field.
     *
     * This method always returns immediately, whether or not the id field
     * has yet been populated by the completion of the construction of the
     * HexagonMatch instance.
     *
     * @return the hashed android id string, the Advertiser ID (if
     * available), or null if an id has not yet been generated.
     * (the id is generated asynchronously upon instantiation of the
     * HexagonMatch instance).
     */
    public String getId() {
        if (id == null) {
            return null;
        }
        return id.mid;
    }

    private void setIdAndType(String mid, IdType idType) {
        if (id == null) {
            id = new Id();
        }
        this.id.mid = mid;
        this.id.idType = idType;
    }

    public IdType getIdType() {
        if (id == null) {
            return null;
        }
        return id.idType;
    }




    /**
     * Send the value of key and type by https.
     */
    public void sendData(String keyString, String keyType) throws Exception {

        if (isLimitedAdTrackingEnabled() || !isInitialized()) {
            return;
        }

        if(!getId().isEmpty() && !getIdType().toString().isEmpty()) {

            final String key256 = String.format("%s%s", Utils.getKeyType(keyType), Utils.sha256(keyString));
            String urlMatch = PROTOCOL_SSL + DEFAULT_DOMAIN + HL_SUBDOMAIN +DEFAULT_PIXEL + "?mid=" + getId() + "&type=" + getIdType()  + getPlatformId() + getTagId() + key256 + getNameApp() + getDevice() + getBoardDevice() + getBrandDevice()  + getModelDevice() + getManufacturerDevice();
            Log.i(HexagonMatch.LOG_TAG, urlMatch);

            try {

                final Map<String, String> newUrlPatternParameters = new HashMap<>();

                SendOverHTTPS sender = new SendOverHTTPS(newUrlPatternParameters, CONNECTION_TIMEOUT);
                sender.execute(urlMatch);

            } catch (Exception e) {
                if (HexagonMatch.debug)
                    Log.e(HexagonMatch.LOG_TAG, "Error Sending sendRequest", e);
            }
        }
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getClientId() {
        return clientId;
    }

    public String getTagId() {
        return TagId;
    }

    public String getPlatformId() {
        return PlatformId;
    }

    public String getDevice() {
        return Device;
    }

    public String getBoardDevice() {
        return BoardDevice;
    }

    public String getBrandDevice() {
        return BrandDevice;
    }

    public String getManufacturerDevice() {
        return ManufacturerDevice;
    }

    public String getModelDevice() {
        return ModelDevice;
    }

    public CharSequence getNameApp() {
        return  NameApp;
    }

    /**
     * Indicates whether the HexagonMatch instance has completed it's
     * initialization and whether it is ready to send and extract data.
     *
     * Until isInitialized() returns true, no data can't send.
     *
     * @return - true indicates the instance is completely initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    private void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Indicates whether the Google Play Advertising preferences have been set
     * to limit advertising tracking.  If the device is not running Google Play
     * Services, or the version is not recent enough for this feature the value
     * will default to false.
     *
     * @return - If discoverable, the users limited ad tracking preferences.  true
     * indicates that the user requested LIMITED ad tracking, false
     * indicates that the user either has not opted-out, or that we
     * were unable to determine whether a preference was set.
     */
    public boolean isLimitedAdTrackingEnabled() {
        return limitedAdTrackingEnabled;
    }

    private void setLimitedAdTrackingEnabled(boolean limitedAdTrackingEnabled) {
        this.limitedAdTrackingEnabled = limitedAdTrackingEnabled;
    }

    /**
     * Indicates whether the SDK was successful in connecting to the Google
     * Play Service and was able to determine the user's Advertising ID and
     * their Limited Ad Tracking preferences.
     *
     * @return - true indicates that the SDK was able to connect to the Google
     * Play Service to extract the Advertiser ID and Limited Ad
     * Tracking preferences.
     */
    public boolean isGoogleAdvertiserIdAvailable() {
        return googleAdvertiserIdAvailable;
    }

    private void setGoogleAdvertiserIdAvailable(boolean googleAdvertiserIdAvailable) {
        this.googleAdvertiserIdAvailable = googleAdvertiserIdAvailable;
    }

    /**
     * Starts new session.
     */
    public void startSession() {
        if (HexagonMatch.debug) Log.d(HexagonMatch.LOG_TAG, "Starting new HexagonMatch session");
    }

}