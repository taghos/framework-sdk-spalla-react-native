import React from 'react';
import { Button, SafeAreaView, StyleSheet } from 'react-native';
import SpallaPlayer, { initialize } from 'react-native-spalla-player';

initialize('your spalla token', null);

export default function App() {
  const playerRef = React.useRef<SpallaPlayer | null>(null);

  const [muted, setMuted] = React.useState(false);
  const [playing, setPlaying] = React.useState(true);

  return (
    <SafeAreaView style={styles.container}>
      <SpallaPlayer
        ref={playerRef}
        style={styles.videoPlayer}
        contentId="0191e5c9-37fa-7332-949a-8afede0572f4"
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
          if (nativeEvent.event === 'timeUpdate') {
            console.log('timeupdate', nativeEvent.time);
          } else {
            console.log('event', nativeEvent.event);
          }
        }}
      />
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
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  videoPlayer: {
    flex: 1,
    backgroundColor: 'black',
    width: '100%',
  },
});
