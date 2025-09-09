import React from 'react';
import {
  requireNativeComponent,
  type ViewStyle,
  NativeModules,
  findNodeHandle,
} from 'react-native';

type allowedPlaybackRates = 0.25 | 0.5 | 1.0 | 1.25 | 1.5 | 2.0;

interface RNSpallaPlayerProps {
  children?: React.ReactNode;
  style?: ViewStyle;
  startTime: number;
  subtitle?: String | null;
  playbackRate?: allowedPlaybackRates;
  hideUI?: boolean;
  ref?: (ref: any) => void;
}

const RNSpallaPlayer =
  requireNativeComponent<RNSpallaPlayerProps>('RNSpallaPlayer');

const RNSpallaPlayerModule = NativeModules.RNSpallaPlayer;

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
  playbackRate?: 0.25 | 0.5 | 1.0 | 1.25 | 1.5 | 2.0;
  onPlayerEvent?: (event: {
    nativeEvent:
      | PlayerEventTimeUpdate
      | PlayerEvent
      | PlayerEventDurationUpdate
      | PlayerEventSubtitlesAvailable
      | PlayerEventSubtitleSelected
      | PlayerEventPlaybackRateSelected
      | PlayerEventMedataLoaded;
  }) => void;
}

export const play = (ref: any) => {
  const handle = findNodeHandle(ref);
  RNSpallaPlayerModule.play(handle);
};

export const pause = (ref: any) => {
  const handle = findNodeHandle(ref);
  RNSpallaPlayerModule.pause(handle);
};

export const seekTo = (ref: any, time: number) => {
  const handle = findNodeHandle(ref);
  RNSpallaPlayerModule.seekTo(handle, time);
};

//export default SpallaPlayer;

class SpallaPlayer extends React.Component<Props> {
  _player = null;

  _setRef = (ref: any) => {
    this._player = ref;
  };

  render() {
    const { style, startTime, playbackRate, hideUI } = this.props;

    return (
      <RNSpallaPlayer
        {...this.props}
        ref={this._setRef}
        style={style}
        startTime={startTime ?? 0}
        playbackRate={playbackRate ?? 1.0}
        hideUI={hideUI ?? false}
      >
        {this.props.children}
      </RNSpallaPlayer>
    );
  }

  play = () => {
    const handle = findNodeHandle(this._player);
    RNSpallaPlayerModule.play(handle);
  };

  pause = () => {
    const handle = findNodeHandle(this._player);
    RNSpallaPlayerModule.pause(handle);
  };

  componentWillUnmount() {
    const handle = findNodeHandle(this._player);
    RNSpallaPlayerModule.unmount(handle);
  }
}

export const initialize = (token: String, applicationId: String | null) => {
  RNSpallaPlayerModule.initialize(token, applicationId);
};

export default SpallaPlayer;
export { default as SpallaCastButton } from './components/CastButton';
