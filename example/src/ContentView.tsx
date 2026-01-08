import React from 'react';
import { Button, StyleSheet, View, SafeAreaView } from 'react-native';
import SpallaPlayer, { SpallaCastButton } from 'react-native-spalla-player';

export default function ContentView() {
  const playerRef = React.useRef<SpallaPlayer | null>(null);

  const [muted, setMuted] = React.useState(false);
  const [playing, setPlaying] = React.useState(true);
  const [subtitle, setSubtitle] = React.useState<String | null>('pt-br');
  const [playbackRate, setPlaybackRate] = React.useState<
    0.25 | 0.5 | 1.0 | 1.25 | 1.5 | 2.0
  >(1.0);
  const [_, setTime] = React.useState(0);

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <SpallaCastButton tintColor="white" />
      </View>
      <View style={styles.videoPlayer}>
        <SpallaPlayer
          ref={playerRef}
          style={styles.videoPlayer}
          contentId={'spalla content id'}
          muted={muted}
          hideUI={false}
          startTime={100}
          subtitle={subtitle}
          playbackRate={playbackRate}
          onPlayerEvent={({ nativeEvent }) => {
            console.log('Native event received', nativeEvent.event);
            switch (nativeEvent.event) {
              case 'timeUpdate':
                console.log('timeupdate', nativeEvent.time);
                setTime(nativeEvent.time);
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
                console.log('playbackRateSelected', nativeEvent.rate);
                setPlaybackRate(nativeEvent.rate);
                break;
              case 'metadataLoaded':
                console.log(
                  'metadataLoaded',
                  nativeEvent.isLive,
                  nativeEvent.duration
                );
                break;
              case 'onEnterFullScreen':
                console.log('onEnterFullScreen');
                break;
              case 'onExitFullScreen':
                console.log('onExitFullScreen');
                break;

              default:
                console.log('event', nativeEvent.event);
            }
          }}
        />
        {/* <TestUI playing={playing} playerRef={playerRef} time={time} /> */}
      </View>
      <View style={styles.hstack}>
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
      </View>
      <View style={styles.hstack}>
        <Button
          onPress={() => {
            setSubtitle(subtitle ? null : 'pt-br');
          }}
          title={subtitle ? 'Disable subtitles' : 'Enable subtitles'}
        />
        <Button
          onPress={() => {
            setPlaybackRate(playbackRate === 1.0 ? 0.5 : 1.0);
          }}
          title={playbackRate.toString() + 'x'}
        />
      </View>
      <View style={styles.bottom} />
    </SafeAreaView>
  );
}

// type TestUIProps = {
//   playing: boolean;
//   time: number;
//   playerRef: React.RefObject<SpallaPlayer | null>;
// };

// const TestUI = ({ playing, time, playerRef }: TestUIProps) => {
//   return (
//     <View style={styles.uicontainer}>
//       <Button
//         onPress={() => {
//           if (playing) {
//             playerRef.current?.pause();
//           } else {
//             playerRef.current?.play();
//           }
//         }}
//         title={playing ? 'Pause' : 'Play'}
//       />
//       <Text
//         style={{
//           color: 'green',
//           backgroundColor: 'yellow',
//           width: 100,
//           height: 30,
//         }}
//       >
//         {time}
//       </Text>
//     </View>
//   );
// };

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
    position: 'absolute',
    width: '100%',
    height: '100%',
    alignItems: 'center',
    justifyContent: 'center',
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
  bottom: {
    height: 50,
  },
  hstack: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-around',
    width: '100%',
    marginBottom: 8,
  },
});
