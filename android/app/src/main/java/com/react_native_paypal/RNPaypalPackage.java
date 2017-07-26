package com.react_native_paypal;

import android.content.Context;
import android.content.Intent;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by siriusnote on 25/7/2017.
 */

public class RNPaypalPackage implements ReactPackage {

    private RNPaypal paypalModule;
    private RNPaypalCallbackManager callback;

    public RNPaypalPackage(RNPaypalCallbackManager callback) {
        this.callback = callback;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        paypalModule = new RNPaypal(reactContext, callback);

        modules.add(paypalModule);
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

}
