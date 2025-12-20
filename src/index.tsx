import React from 'react';
import { type ViewStyle } from 'react-native';
import { Commands } from './SpallaPlayerViewNativeComponent';

// Import the appropriate component based on architecture
const isFabricEnabled = (global as any)?.nativeFabricUIManager != null;

const RNSpallaPlayer = isFabricEnabled
  ? require('./SpallaPlayerViewNativeComponent').default
  : require('react-native').requireNativeComponent('RNSpallaPlayer');

// Import the appropriate module
const RNSpallaPlayerModule = isFabricEnabled
  ? require('./NativeSpallaPlayerModule').default
  : require('react-native').NativeModules.RNSpallaPlayer;

type allowedPlaybackRates = 0.25 | 0.5 | 1.0 | 1.25 | 1.5 | 2.0;

// Event interfaces (keep these as they are)
interface PlayerEventTimeUpdate {
  event: 'timeUpdate';
  time: number;
}

interface PlayerEventDurationUpdate {
  event: 'durationUpdate';
  duration: number;
}

interface PlayerEventSubtitlesAvailable {
  event: 'subtitlesAvailable';
  subtitles: Array<String>;
}

interface PlayerEventSubtitleSelected {
  event: 'subtitleSelected';
  subtitle: String;
}

interface PlayerEventPlaybackRateSelected {
  event: 'playbackRateSelected';
  rate: allowedPlaybackRates;
}

interface PlayerEventMedataLoaded {
  event: 'metadataLoaded';
  isLive: boolean;
  duration: number;
}

interface PlayerEvent {
  event:
    | 'play'
    | 'pause'
    | 'ended'
    | 'muted'
    | 'unmuted'
    | 'buffering'
    | 'playing'
    | 'onEnterFullScreen'
    | 'onExitFullScreen';
}

interface Props {
  style?: ViewStyle;
  children?: React.ReactNode;
  hideUI?: boolean;
  contentId: string;
  muted?: boolean;
  autoplay?: boolean;
  startTime?: number;
  subtitle?: String | null;
  playbackRate?: allowedPlaybackRates;
  onPlayerEvent?: (event: {
    nativeEvent:
      | PlayerEvent
      | PlayerEventTimeUpdate
      | PlayerEventDurationUpdate
      | PlayerEventSubtitlesAvailable
      | PlayerEventSubtitleSelected
      | PlayerEventPlaybackRateSelected
      | PlayerEventMedataLoaded;
  }) => void;
}

export const initialize = (token: string, applicationId: string | null) => {
  RNSpallaPlayerModule.initialize(token, applicationId);
};

export interface SpallaPlayerRef {
  play(): void;
  pause(): void;
  seekTo(time: number): void;
}

const SpallaPlayer = React.forwardRef<SpallaPlayerRef, Props>((props, ref) => {
  const playerRef = React.useRef(null);

  React.useImperativeHandle(
    ref,
    () => ({
      play: () => {
        if (playerRef.current) {
          Commands.play(playerRef.current);
        }
      },
      pause: () => {
        console.log('Calling pause command');
        if (playerRef.current) {
          Commands.pause(playerRef.current);
        }
      },
      seekTo: (time: number) => {
        if (playerRef.current) {
          Commands.seekTo(playerRef.current, time);
        }
      },
    }),
    []
  );

  React.useEffect(() => {
    const currentRef = playerRef.current;
    return () => {
      if (currentRef) {
        Commands.unmount(currentRef);
      }
    };
  }, []);

  return (
    <RNSpallaPlayer
      {...props}
      ref={playerRef}
      style={props.style}
      startTime={props.startTime ?? 0}
      playbackRate={props.playbackRate ?? 1.0}
      hideUI={props.hideUI ?? false}
    >
      {props.children}
    </RNSpallaPlayer>
  );
});

SpallaPlayer.displayName = 'SpallaPlayer';

export default SpallaPlayer;
export { default as SpallaCastButton } from './components/CastButton';
