import { useEffect } from 'react';
import { StyleSheet, Text, Button } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { initialize } from 'react-native-spalla-player';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import ContentView from './ContentView';

const Stack = createNativeStackNavigator();

export default function App() {
  useEffect(() => {
    initialize(
      'spalla api key', // Replace with your Spalla API key
      null // Optionally, provide an application id
    );
  }, []);

  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen
          name="Home"
          component={HomeScreen}
          options={{ title: 'Welcome' }}
        />
        <Stack.Screen name="ContentView" component={ContentView} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const HomeScreen = ({ navigation }: any) => {
  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.text}>Welcome to Spalla Player Example</Text>
      <Button
        title="Play"
        onPress={() => {
          navigation.navigate('ContentView');
        }}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontSize: 24,
    marginBottom: 20,
    color: 'red',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
