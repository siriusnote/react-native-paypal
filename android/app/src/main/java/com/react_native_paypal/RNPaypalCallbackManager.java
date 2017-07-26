package com.react_native_paypal;

import android.content.Intent;

/**
 * Created by siriusnote on 25/7/2017.
 */

public class RNPaypalCallbackManager {

    RNPaypal paypal;

    public void setHandler(RNPaypal paypal){
        this.paypal = paypal;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data){
        paypal.handleActivityResult(requestCode, resultCode, data);
        return true;
    }
}
