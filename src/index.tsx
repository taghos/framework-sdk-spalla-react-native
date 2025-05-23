import React from 'react';
import {
  requireNativeComponent,
  type ViewStyle,
  NativeModules,
  findNodeHandle,
} from 'react-native';

interface RNSpallaPlayerProps {
  children?: React.ReactNode;
  style?: ViewStyle;
  startTime: number;
  subtitle?: String | null;
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
interface PlayerEvent {
  event:
    | 'play'
    | 'pause'
    | 'ended'
    | 'muted'
    | 'unmuted'
    | 'buffering'
    | 'playing';
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
  onPlayerEvent?: (event: {
    nativeEvent:
      | PlayerEventTimeUpdate
      | PlayerEvent
      | PlayerEventDurationUpdate
      | PlayerEventSubtitlesAvailable
      | PlayerEventSubtitleSelected;
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
    const { style, startTime } = this.props;

    return (
      <RNSpallaPlayer
        {...this.props}
        ref={this._setRef}
        style={style}
        startTime={startTime ?? 0}
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
}

export const initialize = (token: String, applicationId: String | null) => {
  RNSpallaPlayerModule.initialize(token, applicationId);
};

export default SpallaPlayer;
export { default as SpallaCastButton } from './components/CastButton';
