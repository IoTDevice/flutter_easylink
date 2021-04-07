#import "FlutterEasylinkPlugin.h"
#if __has_include(<flutter_easylink/flutter_easylink-Swift.h>)
#import <flutter_easylink/flutter_easylink-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_easylink-Swift.h"
#endif

@implementation FlutterEasylinkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterEasylinkPlugin registerWithRegistrar:registrar];
}
@end
