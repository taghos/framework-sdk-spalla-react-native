import React from 'react';
import { Button, SafeAreaView, StyleSheet, View } from 'react-native';
import SpallaPlayer, {
  initialize,
  SpallaCastButton,
} from 'react-native-spalla-player';

initialize('your Spalla token', 'Chromecast app id or null');

export default function App() {
  const playerRef = React.useRef<SpallaPlayer | null>(null);

  const [muted, setMuted] = React.useState(false);
  const [playing, setPlaying] = React.useState(true);

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <SpallaCastButton tintColor="white" />
      </View>
      <SpallaPlayer
        ref={playerRef}
        style={styles.videoPlayer}
        contentId="your spalla content id"
        muted={muted}
        hideUI={false}
        startTime={50}
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
          if (nativeEvent.event === 'timeUpdate') {
            console.log('timeupdate', nativeEvent.time);
          } else {
            console.log('event', nativeEvent.event);
          }
        }}
      >
        <View style={styles.uicontainer}>{/* Custom UI */}</View>
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
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  spacer: {
    flex: 1,
    height: 100,
    backgroundColor: 'green',
  },
  uicontainer: {
    flexDirection: 'column',
    flex: 1,
  },
  videoPlayer: {
    flex: 1,
    backgroundColor: 'black',
    width: '100%',
  },
  header: {
    flexDirection: 'row',
    width: '100%',
    backgroundColor: 'green',
    height: 50,
    alignItems: 'center',
    justifyContent: 'flex-end',
    paddingHorizontal: 10,
  },
});
