const {
  withAndroidManifest,
  withMainActivity,
} = require('@expo/config-plugins');

/**
 * Adds supportsPictureInPicture="true" to MainActivity
 */
function withPipManifest(config) {
  return withAndroidManifest(config, (config) => {
    const manifest = config.modResults.manifest;
    const app = manifest.application?.[0];

    if (!app?.activity) return config;

    const mainActivity = app.activity.find(
      (activity) => activity.$?.['android:name'] === '.MainActivity'
    );

    if (!mainActivity) return config;

    mainActivity.$['android:supportsPictureInPicture'] = 'true';
    return config;
  });
}

/**
 * Injects onUserLeaveHint() into MainActivity.kt
 */
function withPipMainActivity(config) {
  return withMainActivity(config, (config) => {
    let contents = config.modResults.contents;

    if (contents.includes('override fun onUserLeaveHint()')) {
      return config;
    }

    // Add necessary imports
    if (!contents.includes('import com.spallaplayer.SpallaPlayerPipModule')) {
      contents = contents.replace(
        /import\s+android\.os\.Bundle/,
        `import android.os.Bundle
import com.spallaplayer.SpallaPlayerPipModule`
      );
    }

    const method = `
  override fun onUserLeaveHint() {
    super.onUserLeaveHint()
    SpallaPlayerPipModule.triggerUserLeaveHint()
  }
`;

    contents = contents.replace(
      /class MainActivity[^{]*\{/,
      (match) => `${match}${method}`
    );

    config.modResults.contents = contents;
    return config;
  });
}

module.exports = function withPipAndroid(config) {
  config = withPipManifest(config);
  config = withPipMainActivity(config);
  return config;
};
