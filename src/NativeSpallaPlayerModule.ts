import type { TurboModule, CodegenTypes } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  play(tag: CodegenTypes.Int32): void;
  pause(tag: CodegenTypes.Int32): void;
  seekTo(tag: CodegenTypes.Int32, time: number): void;
  selectSubtitle(tag: CodegenTypes.Int32, subtitle: string | null): void;
  selectPlaybackRate(tag: CodegenTypes.Int32, rate: number): void;
  unmount(tag: CodegenTypes.Int32): void;
  initialize(token: string, applicationId: string): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('SpallaPlayerModule');
