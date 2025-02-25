import {
  requireNativeComponent,
  type ViewProps,
  StyleSheet,
} from 'react-native';

export interface Props extends ViewProps {
  style?: ViewProps['style'] & { tintColor?: string };
  tintColor?: string;
}

export default function CastButton({ style, ...rest }: Props) {
  // @ts-ignore FIXME
  return <GoogleCastButton style={[styles.default, style]} {...rest} />;
}

CastButton.propTypes = {
  /**
   * A flag that indicates whether a touch event on this button will trigger the display of the Cast dialog that is provided by the framework.
   *
   * By default this property is set to YES. If an application wishes to handle touch events itself, it should set the property to NO and register an appropriate target and action for the touch event.
   */
  // triggersDefaultCastDialog: PropTypes.bool
  // accessibilityLabel: PropTypes.string
};

const GoogleCastButton = requireNativeComponent(
  'RNGoogleCastButton'
  // CastButton
  // {
  //   nativeOnly: {
  //     accessibilityLabel: true,
  //     accessibilityLiveRegion: true,
  //     accessibilityComponentType: true,
  //     testID: true,
  //     nativeID: true,
  //     importantForAccessibility: true,
  //     renderToHardwareTextureAndroid: true,
  //     onLayout: true,
  //   },
  // }
);

const styles = StyleSheet.create({
  default: {
    width: 40,
    height: 40,
  },
});
