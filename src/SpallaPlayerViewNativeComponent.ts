import type { ViewProps, CodegenTypes, HostComponent } from 'react-native';

// NOTE: codegenNativeComponent MUST be imported from RN internals for Fabric.
// Do not change this import to `from 'react-native'`.
/* eslint-disable @react-native/no-deep-imports */
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

/* eslint-disable @react-native/no-deep-imports */
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';

// Define a single comprehensive event payload interface
interface PlayerEventPayload {
  event: string;
  time?: CodegenTypes.Double;
  duration?: CodegenTypes.Double;
  isLive?: boolean;
  subtitles?: string[];
  subtitle?: string;
  rate?: CodegenTypes.Float;
  message?: string;
  error?: string;
}

export interface NativeProps extends ViewProps {
  contentId?: string;
  muted?: boolean;
  startTime?: CodegenTypes.Double;
  subtitle?: string | null;
  playbackRate?: CodegenTypes.Float;
  hideUI?: boolean;
  onPlayerEvent?: CodegenTypes.BubblingEventHandler<PlayerEventPayload>;
}

export interface NativeCommands {
  play: (viewRef: React.ElementRef<HostComponent<NativeProps>>) => void;
  pause: (viewRef: React.ElementRef<HostComponent<NativeProps>>) => void;
  seekTo: (
    viewRef: React.ElementRef<HostComponent<NativeProps>>,
    time: CodegenTypes.Double
  ) => void;
  selectSubtitle: (
    viewRef: React.ElementRef<HostComponent<NativeProps>>,
    subtitle: string | null
  ) => void;
  selectPlaybackRate: (
    viewRef: React.ElementRef<HostComponent<NativeProps>>,
    rate: CodegenTypes.Float
  ) => void;
  unmount: (viewRef: React.ElementRef<HostComponent<NativeProps>>) => void;
}

export const Commands: NativeCommands = codegenNativeCommands<NativeCommands>({
  supportedCommands: [
    'play',
    'pause',
    'seekTo',
    'selectSubtitle',
    'selectPlaybackRate',
    'unmount',
  ],
});

export default codegenNativeComponent<NativeProps>('SpallaPlayerView');
