jpackage --name Heimdall \
  --input target \
  --main-jar core-1.0-SNAPSHOT.jar \
  --main-class heimdall.App \
  --mac-entitlements ./src/main/resources/entitlements.plist \
  --type dmg
