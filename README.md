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

<SafeAreaView style={styles.container}>
  <SpallaPlayer
    ref={playerRef}
    style={styles.videoPlayer}
    contentId="Spalla contentId"
    muted={muted}
    hideUI={false}
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
| **`onPlayerEvent`**| callback | Function that will be called with player events

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

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
