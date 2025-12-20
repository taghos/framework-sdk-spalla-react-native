import {
  codegenNativeComponent,
  type ViewProps,
  type CodegenTypes,
  type HostComponent,
  codegenNativeCommands,
} from 'react-native';

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
  play: (viewRef: React.ElementRef<HostComponent<any>>) => void;
  pause: (viewRef: React.ElementRef<HostComponent<any>>) => void;
  seekTo: (
    viewRef: React.ElementRef<HostComponent<any>>,
    time: CodegenTypes.Double
  ) => void;
  selectSubtitle: (
    viewRef: React.ElementRef<HostComponent<any>>,
    subtitle: string | null
  ) => void;
  selectPlaybackRate: (
    viewRef: React.ElementRef<HostComponent<any>>,
    rate: CodegenTypes.Float
  ) => void;
  unmount: (viewRef: React.ElementRef<HostComponent<any>>) => void;
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
