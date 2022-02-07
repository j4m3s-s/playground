<template>
    <div v-if="recipes">
        <ul>
            <li v-for="(recipe, index) in this.recipes" v-bind:key="index">
                <a :href="'recipe/' + recipe.id"> {{ recipe.name }} </a>
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
        recipes: null
    }
  },
  mounted: function() {
      axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/recipes?select=id,name`)
        .then((resp) => {
            this.recipes = resp.data
            console.log(this.recipes)
        })
  }
})
</script>

<style>
</style>
