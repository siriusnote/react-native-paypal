package com.react_native_paypal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by siriusnote on 25/7/2017.
 */

public class RNPaypal extends ReactContextBaseJavaModule {

    private static final String USER_CANCELLED = "USER_CANCELLED";
    private static final String INVALID_CONFIG = "INVALID_CONFIG";
    private static final int paymentIntentRequestCode = 9;

    private PayPalConfiguration config;
    private RNPaypalCallbackManager callback;
    private Promise promise;
    /*private Callback successCallback;
    private Callback errorCallback;*/

    private ReactApplicationContext reactContext;


    public RNPaypal(ReactApplicationContext reactContext, RNPaypalCallbackManager callback) {
        super(reactContext);
        this.reactContext = reactContext;
        callback.setHandler(this);
    }

    @Override
    public String getName() {
        return "RNPaypal";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("PRODUCTION", PayPalConfiguration.ENVIRONMENT_PRODUCTION);
        constants.put("SANDBOX", PayPalConfiguration.ENVIRONMENT_SANDBOX);
        constants.put("NO_NETWORK", PayPalConfiguration.ENVIRONMENT_NO_NETWORK);
        constants.put(USER_CANCELLED, USER_CANCELLED);
        constants.put(INVALID_CONFIG, INVALID_CONFIG);
        return constants;
    }

    public Boolean getBoolean(String word){
        switch(word) {
            case "YES":
                return true;
            case "NO":
            default:
                return false;
        }
    }

    @ReactMethod
    public void config(ReadableMap options) {
        Log.d("PAYPAL", options.toString());

        this.config = new PayPalConfiguration()
                .environment(options.getString("env"))
                .clientId(options.getString("clientId"))
                .acceptCreditCards(getBoolean(options.getString("acceptCreditCard")))
                .languageOrLocale(options.getString("languageOrLocale"));

        Intent intent = new Intent(reactContext, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, this.config);
        reactContext.startService(intent);
    }

    @ReactMethod
    public void pay(ReadableMap options, Promise promise){
        this.promise = promise;

        PayPalPayment payment = new PayPalPayment(new BigDecimal("10.00"), "HKD", "Simple item", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this.getReactApplicationContext(), PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, this.config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        reactContext.startActivityForResult(intent,paymentIntentRequestCode,null);
    }

    public void handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode != paymentIntentRequestCode) { return; }

        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm =
                    data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                promise.resolve(confirm.toJSONObject().toString());
                /*successCallback.invoke(
                        confirm.toJSONObject().toString(),
                        confirm.getPayment().toJSONObject().toString()
                );*/
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            promise.reject(USER_CANCELLED);
            //errorCallback.invoke(USER_CANCELLED);
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            promise.reject(INVALID_CONFIG);
            //errorCallback.invoke(INVALID_CONFIG);
        }

        reactContext.stopService(new Intent(reactContext, PayPalService.class));
    }


}
