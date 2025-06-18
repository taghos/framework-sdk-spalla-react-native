import React from 'react';
import { initialize } from 'react-native-spalla-player';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Button, SafeAreaView, Text } from 'react-native';
import ContentView from './ContentView';

const Stack = createNativeStackNavigator();

initialize('your spalla token here', 'Chromecast app id or null');

export default function App() {
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
    <SafeAreaView
      style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}
    >
      <Text style={{ fontSize: 24, marginBottom: 20, color: 'red' }}>
        Welcome to Spalla Player Example
      </Text>
      <Button
        title="Play"
        onPress={() => {
          navigation.navigate('ContentView');
        }}
      />
    </SafeAreaView>
  );
};
