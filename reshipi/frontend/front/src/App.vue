<template>
  <div id="app">
    <div id="nav">
      <router-link to="/"> Liste de recettes </router-link>
      <router-link to="/ustensils"> Liste d'ustensiles </router-link>
      <router-link to="/ingredients"> Liste d'ingrédients </router-link>
    </div>
    <router-view/>
  </div>
</template>

<style>
</style>
