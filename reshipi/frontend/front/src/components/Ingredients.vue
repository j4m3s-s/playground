<template>
    <div v-if="ingredients">
        <ul v-for="(ingredient, index) in ingredients" :key="index">
            <li> {{ ingredient.name }} </li>
        </ul>
    </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'Ingredients',
  data () {
    return {
        ingredients: null,
    }
  },
  mounted: function() {
      axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/ingredients`)
        .then((resp) => {
            this.ingredients = resp.data
            console.log(this.ingredients)
        })
  }
})
</script>

<style>
</style>
