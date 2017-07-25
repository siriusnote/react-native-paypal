/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableOpacity
} from 'react-native';
import { NativeModules } from 'react-native';
var Paypal = NativeModules.RNPaypal;

export default class react_native_paypal extends Component {
  
  onPress(){

    Paypal.config({
      env: Paypal.SANDBOX,
      clientId: 'AYlKn_gTrBA-oxVktxDtyjEAxa6nIquHNofSHbBqx0B-5PNRL4LY_rFGYAKv5qbTDw_ejCgjddq_rXBv',
      acceptCreditCard:"YES",
      languageOrLocale:"en_HK"
    });
    Paypal.addItems([
        {
        name: "Test item1",
        quantity: 2,
        price: '1.00',
        currency: 'HKD',
        sku: 'test_item_1'
      }
      ,{
        name: "Test item2",
        quantity: 2,
        price: '1.00',
        currency: 'HKD',
        sku: 'test_item_2'
      },{
        name: "Surcharge",
        quantity: 1,
        price: '30.00',
        currency: 'HKD',
      }
      ]);

    Paypal.pay({
      currency: 'HKD',
      description: 'Payment',
      })
    .then(confirm => console.log(confirm))
    .catch(error => console.log(error));
  }

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={this.onPress}>
          <View style={styles.paypalButton}>
            <Text style={styles.paypalButtonText}>{"Pay with Paypal"}</Text>
          </View>
        </TouchableOpacity>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  paypalButton:{
    backgroundColor: '#0000EE',
    borderRadius:5,
    height:20,
    alignItems:'center',
    
  },
  paypalButtonText:{
    color:'#FFF',
    padding:10,
  }
});

AppRegistry.registerComponent('react_native_paypal', () => react_native_paypal);
