package com.iotserv.flutter_easylink;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

//easylink
import io.fogcloud.sdk.easylink.api.EasylinkP2P;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;

/** FlutterEasylinkPlugin */
public class FlutterEasylinkPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private String ssid;
  private String bssid;
  private String password = null;
  private int timeout = 60;//miao
  private Activity activity;
  private MethodChannel channel;
  EasylinkP2P elp2p;

  public FlutterEasylinkPlugin(Activity activity,MethodChannel channel) {
    this.activity = activity;
    this.channel = channel;
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_easylink");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("start")){
      //参数获取
      ssid = call.argument("ssid");
      bssid = call.argument("bssid");
      password = call.argument("password");
      try {
        timeout = call.argument("timeout");
      }catch (Exception e){
        e.printStackTrace();
        timeout = 30;
      }
      elp2p = new EasylinkP2P(activity.getApplicationContext());

      EasyLinkParams elp = new EasyLinkParams();
      elp.ssid = ssid;
      elp.password = password;
      elp.sleeptime = 50;
      elp.runSecond = timeout;
      Log.d("====easylink===","start");
      try {
        elp2p.startEasyLink(elp, new EasyLinkCallBack() {
          @Override
          public void onSuccess(int code, String message) {
            Map<String, String> ret = new HashMap<String, String>();
            Log.d("easylink_success", "success");
            try {
              JSONObject jsonObject = new JSONObject(message);
              if (jsonObject.has("ip") && jsonObject.has("mac")) {
//              final String name = jsonObject.getString("name");
                final String mac = jsonObject.getString("mac");
//              final String type_name = jsonObject.getString("type_name");
                final String ip = jsonObject.getString("ip");
                ret.put("mac", mac);
                ret.put("ip", ip);
                Log.d("easylink_success", message);
                result.success(ret);
                return;
              }
            }catch (Exception e) {
              Log.d("easylink_exception", e.getMessage());
              e.printStackTrace();
              ret.put("mac", "");
              ret.put("ip", "");
              result.success(ret);
              return;
            }
            ret.put("result", "success");
            ret.put("code", String.valueOf(code));
            ret.put("message", message);
            result.success(ret);
          }

          @Override
          public void onFailure(int code, String message) {
            Map<String, String> ret = new HashMap<String, String>();
            Log.d("easylink_fail", message);
            ret.put("result", "fail");
            ret.put("code", String.valueOf(code));
            ret.put("message", message);
            result.success(ret);
          }
        });
      }catch (Exception e){
        e.printStackTrace();
        Log.d("====easylink===","exception");
      }
      Log.d("====easylink===","end");
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
