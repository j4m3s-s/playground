<template>
  <div id="app">
    <div id="nav">
      <router-link to="/"> Liste de recettes </router-link> &nbsp;
      <router-link to="/ustensils"> Liste d'ustensiles </router-link> &nbsp;
      <router-link to="/ingredients"> Liste d'ingrédients </router-link> &nbsp;
      <router-link to="/ingredient-edit"> Rajouter un ingrédient </router-link> &nbsp;
      <router-link to="/ustensil-edit"> Rajouter un ustensil </router-link> &nbsp;
      <router-link to="/recipe-edit"> Rajouter un recette </router-link> &nbsp;
    </div>
    <router-view/>
  </div>
</template>

<style>
</style>
