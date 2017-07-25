//
//  RNPaypal.m
//  react_native_paypal
//
//  Created by Samual Tam on 24/7/2017.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "RNPaypal.h"
#import <React/RCTLog.h>


@interface RNPaypal ()

@property (nonatomic, strong, readwrite) PayPalConfiguration *configuration;

@property (copy) RCTPromiseResolveBlock resolve;
@property (copy) RCTPromiseRejectBlock reject;
@property (nonatomic) NSMutableArray *items;

@end

@implementation RNPaypal

NSString * const USER_CANCELLED = @"USER_CANCELLED";

- (NSDictionary *)constantsToExport
{
  return @{
           @"PRODUCTION": PayPalEnvironmentProduction,
           @"SANDBOX": PayPalEnvironmentSandbox,
           @"NO_NETWORK": PayPalEnvironmentNoNetwork,
           USER_CANCELLED: USER_CANCELLED
           };
}

// The React Native bridge needs to know our module
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(config:(NSDictionary *)options){
  
  NSString *environment = [RCTConvert NSString:options[@"env"]];
  NSString *clientId = [RCTConvert NSString:options[@"clientId"]];
  BOOL acceptCreditCards = [RCTConvert BOOL:options[@"acceptCreditCard"]];
  NSString *langCode = [RCTConvert NSString:options[@"languageOrLocale"]];
  
  dispatch_async(dispatch_get_main_queue(), ^{
    [PayPalMobile initializeWithClientIdsForEnvironments:@{environment : clientId}];
    [PayPalMobile preconnectWithEnvironment:environment];
  });
  
  self.configuration = [[PayPalConfiguration alloc] init];
  self.configuration.acceptCreditCards = acceptCreditCards;
  self.configuration.languageOrLocale = langCode;
  [self.configuration setPayPalShippingAddressOption: PayPalShippingAddressOptionNone];
  
}

RCT_EXPORT_METHOD(addItems:(NSArray *) itemsMeta){
  //RCTLogInfo(@"create item %@", items);
  self.items = [[NSMutableArray alloc] init];
  for (id item in itemsMeta) {
    NSString *name = [RCTConvert NSString:item[@"name"]];
    NSInteger quantity = [RCTConvert NSInteger:item[@"quantity"]];
    NSDecimalNumber *price = [NSDecimalNumber decimalNumberWithString:[RCTConvert NSString:item[@"price"]]];
    NSString *currency = [RCTConvert NSString:item[@"currency"]];
    NSString *sku = [RCTConvert NSString:item[@"sku"]];
    if(sku == NULL){
      sku = nil;
    }
    
    PayPalItem *tmpItem = [PayPalItem itemWithName:name withQuantity:quantity withPrice:price withCurrency:currency withSku:sku];
    [self.items addObject:tmpItem];
    
  }
}

RCT_EXPORT_METHOD(pay:(NSDictionary *)options resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  self.resolve = resolve;
  self.reject = reject;

  NSString *currency = [RCTConvert NSString:options[@"currency"]];
  NSString *description = [RCTConvert NSString:options[@"description"]];
    //NSString *address = [RCTConvert NSString:options[@"address"]];
  
  
  PayPalPayment *payment = [[PayPalPayment alloc] init];
  payment.items = self.items;
  payment.amount =  [PayPalItem totalPriceForItems:self.items];
  payment.currencyCode = currency;
  payment.shortDescription = description;
  
  //payment.shippingAddress = address;
  
  payment.intent = PayPalPaymentIntentSale;
  
  
  PayPalPaymentViewController *paymentViewController;
  paymentViewController = [[PayPalPaymentViewController alloc]
                           initWithPayment:payment
                           configuration:self.configuration
                           delegate:self];

  UIViewController *visibleVC = [[[UIApplication sharedApplication] keyWindow] rootViewController];
  do {
    if ([visibleVC isKindOfClass:[UINavigationController class]]) {
      visibleVC = [(UINavigationController *)visibleVC visibleViewController];
    } else if (visibleVC.presentedViewController) {
      visibleVC = visibleVC.presentedViewController;
    }
  } while (visibleVC.presentedViewController);
  
  dispatch_async(dispatch_get_main_queue(), ^{
    [visibleVC presentViewController:paymentViewController animated:YES completion:nil];
  });
  
}

#pragma mark Paypal Delegate

- (void)payPalPaymentDidCancel:(PayPalPaymentViewController *)paymentViewController
{
  [paymentViewController.presentingViewController dismissViewControllerAnimated:YES completion:^{
    if (self.reject) {
      NSError *error = [NSError errorWithDomain:RCTErrorDomain code:1 userInfo:NULL];
      self.reject(USER_CANCELLED, USER_CANCELLED, error);
    }
  }];
}

- (void)payPalPaymentViewController:(PayPalPaymentViewController *)paymentViewController
                 didCompletePayment:(PayPalPayment *)completedPayment
{
  [paymentViewController.presentingViewController dismissViewControllerAnimated:YES completion:^{
    if (self.resolve) {
      self.resolve(completedPayment.confirmation);
    }
  }];
}

@end

