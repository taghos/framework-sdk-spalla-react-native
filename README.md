# react-native-spalla-player

Spalla SDK for RN

## Installation

```sh
npm install react-native-spalla-player
```

## Usage


```js
import SpallaPlayer, { initialize } from 'react-native-spalla-player';

// make sure to call initialize as soon as possible on your app. Can be on top of index.js or App.js
initialize(
  'your spalla token',
  null //application id for chromecast. 
);

// ...

<SpallaPlayer
        ref={playerRef}
        contentId="Spalla contentId"
        hideUI={false}
      />
```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
