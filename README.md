# react-native-spalla-player

Spalla SDK for RN

## Installation

```sh
npm install react-native-spalla-player
```

On Android, also add the THEOplayer repository to the list of repositories

```sh
maven { url 'https://maven.theoplayer.com/releases' } 
```

For example:
```sh
repositories {
  mavenCentral()
  google()
  maven { url 'https://maven.theoplayer.com/releases' } 
}
```


## Usage

```js
import SpallaPlayer, { initialize } from 'react-native-spalla-player';

// make sure to call initialize as soon as possible on your app. Can be on top of index.js or App.js
initialize(
  'your spalla token',
  null //application id for chromecast. 
);

// ...

const playerRef = React.useRef<SpallaPlayer | null>(null);

const [muted, setMuted] = React.useState(false);
const [playing, setPlaying] = React.useState(true);
const [subtitle, setSubtitle] = React.useState<String | null>('pt-br');

<SafeAreaView style={styles.container}>
  <SpallaPlayer
    ref={playerRef}
    style={styles.videoPlayer}
    contentId="Spalla contentId"
    muted={muted}
    hideUI={false}
    subtitle={subtitle}
    onPlayerEvent={({ nativeEvent }) => {
      switch (nativeEvent.event) {
        case 'timeUpdate':
          console.log('timeupdate', nativeEvent.time);
          break;
        case 'durationUpdate':
          console.log('durationUpdate', nativeEvent.duration);
          break;
        case 'play':
        case 'playing':
          setPlaying(true);
          break;
        case 'pause':
          setPlaying(false);
          break;
        case 'muted':
          setMuted(true);
          break;
        case 'unmuted':
          setMuted(false);
          break;
        case 'subtitleSelected':
          console.log('subtitleSelected', nativeEvent.subtitle);
          setSubtitle(nativeEvent.subtitle);
          break;
        case 'subtitlesAvailable':
          console.log('subtitlesAvailable', nativeEvent.subtitles);
          break;
        case 'playbackRateSelected':
          setPlaybackRate(nativeEvent.rate);
          break;
        case 'metadataLoaded':
          console.log(
            'metadataLoaded',
            nativeEvent.isLive,
            nativeEvent.duration
          );
          break;
        default:
          console.log('event', nativeEvent.event);
      }
    }}
  >
  <View style={styles.uicontainer}>{/*Place your custom UI here*/}</View>
  </SpallaPlayer> 
  <Button
    onPress={() => {
      if (playing) {
        playerRef.current?.pause();
      } else {
        playerRef.current?.play();
     }
    }}
    title={playing ? 'Pause' : 'Play'}
  />
  <Button
    onPress={() => setMuted(!muted)}
    title={muted ? 'Unmute' : 'Mute'}
  />
</SafeAreaView>
```

## Props

|    Property        | Type     | Description  |
| :----------------- | :------: | :----------- |
| **`contentId`**    | string   | Spalla contentId that will be played
| **`hideUI`**       | boolean  | hide or show the default UI (its a prop, but it can only be set once)
| **`muted`**        | boolean  | mute/unmute video
| **`startTime`**    | number  | time to start the video in seconds (defaults to 0 = start of the video)
| **`onPlayerEvent`**| callback | Function that will be called with player events
| **`subtitle`**     | string  | subtitle to enable. Null will hide subtitles
| **`playbackRate`**     | number  | Playback speed. Allowed values are 0.5, 1.0, 1.5 and 2.0

## Imperative Methods

You can control a player reference using 3 methods

|    Method          | Description  |
| :----------------- | :----------- |
| **`play()`**       | Resume playback
| **`pause()`**      | Pause playback
| **`seekTo(time)`** | Seek to a time (in seconds)

```js
playerRef.current?.play();
playerRef.current?.pause();
playerRef.current?.seekTo(12); //position in seconds, if higher than duration it will move to the end
```

## Chromecast

If you are using Chromecast, there are a few changes that needs to be done:

```js
// Add the application id for chromecast. In this example, A123456
initialize(
  'your spalla token',
  'A123456'
);
```

On iOS, open Xcode, open info.plist file as SourceCode, and copy this inside the main dict. Make sure to change A123456 with your App ID (keep the underscore at the start). More details on this [link](https://developers.google.com/cast/docs/ios_sender/permissions_and_discovery#updating_your_app_on_ios_14) if needed.
```
<key>NSBonjourServices</key>
<array>
  <string>_googlecast._tcp</string>
  <string>_A123456._googlecast._tcp</string>
</array>
<key>NSLocalNetworkUsageDescription</key>
<string>We need network access to search for Cast devices</string>
```

On Android, open Manifest.xml and add this meta data tag inside the <application> tag (same level as activities). As before, change A123456 with your App ID.

```
<meta-data
    android:name="com.spalla.sdk.CAST_ID"
    android:value="A123456"/>
```

Spalla provides a RN View that you can use to add the cast button to your interface. Check the example app if you need an example of usage

```js
import { SpallaCastButton } from 'react-native-spalla-player';

[...]

return <SpallaCastButton tintColor="white" />
```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
