<template>
  <div id="app">
    <nav>
      <router-link :to="{ name: 'Cards' }">Cards</router-link> |
      <router-link :to="{ name: 'Tags' }">Tags</router-link> |
      <router-link :to="{ name: 'CardAdd' }">Add Card</router-link> |
      <router-link :to="{ name: 'TestWorkflow' }">TestWorkflow</router-link> |
      <!-- FIXME: show Only if not connected, store (?) -->
      <router-link :to="{ name: 'Login' }">Login</router-link>
      <!-- FIXME: Show "login" is connected -->
    </nav>
    <router-view/>
  </div>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>
