//
//  RNPaypal.h
//  react_native_paypal
//
//  Created by Samual Tam on 24/7/2017.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "React/RCTBridgeModule.h"
#import "React/RCTConvert.h"
#import "PayPalMobile.h"
#import <Foundation/Foundation.h>


@interface RNPaypal : NSObject <RCTBridgeModule,PayPalPaymentDelegate>

@end
