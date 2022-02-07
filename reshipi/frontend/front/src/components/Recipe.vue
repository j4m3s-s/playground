<template>
    <div v-if="recipe">
        <h1> {{ recipe.name }} </h1>
        <h2> Ingrédients </h2>
        <ul>
            <li v-for="(ingredient, index) in this.recipe.ingredients" v-bind:key="index">
                {{ ingredient.name }}
            </li>
        </ul>
        <h2> Ustensiles </h2>
        <ul>
            <li v-for="(ustensil, index) in this.recipe.ustensils" v-bind:key="index">
                {{ ustensil.name }}
            </li>
        </ul>
        <h2> Préparation </h2>
        <ul>
            <li v-for="(step, index) in this.recipe.steps" v-bind:key="index">
                Étape {{ index }} : {{ step.text }}
            </li>
        </ul>
    </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'Recipe',
  props: ['id'],
  data () {
    return {
        recipe: null,
        recipe_id: this.id
    }
  },
  mounted: function() {
      axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/recipes?id=eq.${this.recipe_id}&select=*,ingredients(*),ustensils(*),steps(*)`)
        .then((resp) => {
            this.recipe = resp.data[0]
            console.log(this.recipe)
        })
  }
})
</script>

<style>
</style>
