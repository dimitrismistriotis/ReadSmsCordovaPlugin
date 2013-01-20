ReadSmsCordovaPlugin
====================

A plugin to send SMSes using apache Cordova, developed against version 2.3.0

Setup
-----

1. Copy Java file from src folder to the src folder or your project.
2. In config.xml (under "res/xml"), register the plugin (see also "res/xml/config.additional.xml"):
```xml
    <plugin name="ReadSms" value="net.webootu.cordova.plugin.ReadSms" />
```
3. Add the following lines in your AndroidManifest.additional.xml (see also AndroidManifest.additional.xml):
```xml
<!-- Additional permission for ReadSms plugin -->
<uses-permission android:name="android.permission.READ_SMS" />
```
4. Call can be made using the [Cordova plugin invocation/interface](http://docs.phonegap.com/en/2.3.0/guide_plugin-development_index.md.html#Plugin%20Development%20Guide):
```javascript
// GetTexts action:
cordova.exec(function(winParam) {}, function(error) {}, "ReadSms", "GetTexts", [phoneNumber, numberOfTextsToRead]);
```

Read Actions section for input parameters.

Actions
-------
Currently only one action is available, "GetTexts", there is the intention to add more in the future.

"GetTexts" is being called with two parameters: mandatory phoneNumber and optional numberOfTextsToRead.
* First parameter "phoneNumber" is the number of the phone from which we want to read texts messages from. This means messages stored in the inbox of the user's phone.  Please also note that Android distinguishes between numbers containing the international "+" sign, so "+9999999999" is different from "09999999999" (leading zero).
* Second parameter is "numberOfTextsToRead". A positive integer is used for maximum results returned, while a non-positive one is used to flag that all stored messages will be retrieved. Prefer to use "-1" instead of a random negative number of zero, in order to make your code future proof from possible extensions.

Results are being retrieved in descending order on time received, so last one will be the first one to be retrieved, etc.

Results
-------

Successful results contain a JSON object with the following properties in case of success:
phone/_number: the number provided (echoed).
texts: Retrieved texts. Each one of these has two properties, time stamp in "time_received" and SMS/text's payload in "message".

Examples
--------

There is a sample execution in "assets/www/index.sample.html".

```javascript
cordova.exec(function(winParam) {}, function(error) {}, "ReadSms", "GetTexts", ["999999999999", -1]);
```

Will try to read all texts from number "999999999999".

Sample output:

```javascript
{
  "texts":[
    {
      "message":"ETA 20.02 ",
      "time_received":"1358538770262"
    },
    {
      "message":"Ok, I'm on my way.",
      "time_received":"1358536046947"
    }
  ],
  "phone_number":"999999999999"
}
```

```javascript
cordova.exec(function(winParam) {}, function(error) {}, "ReadSms", "GetTexts", ["999999999999", 1]);
```

Will try to read all the last (since "numberOfTextsToRead" is 1) text from number "999999999999".

Sample output:

```javascript
{
  "texts":[
    {
      "message":"Ok, I'm on my way.",
      "time_received":"1358536046947"
    }
  ],
  "phone_number":"999999999999"
}
```

Notes
-----

In case you are interested in dispatching texts/Sms messages, refer to this plugin's sibling: [SendSmsPlugin](https://github.com/dimitrismistriotis/SendSmsCordovaPlugin), using the same semantics, under the same license.

License
-------
Please refer to LICENSE file in this repository.

Note
----
Initially by Dimitrios of [WeBootU](http://www.webootu.com) please consider us for your Cordova related projects.

