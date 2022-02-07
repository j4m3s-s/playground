<template>
    <div v-if="!posted">
        <input type="text" v-model="name" />
        <button v-on:click="createIngredient"> Créer </button>
    </div>
    <div v-else>
      Successfuly posted
      <button v-on:click="reset"> Créer un nouvel ingrédient </button>
    </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'IngredientEdition',
  data () {
    return {
        name: "",
        posted: false
    }
  },
  methods: {
      createIngredient () {
          axios.post(`${process.env.VUE_APP_API_ENDPOINT_URL}/ingredients`, {
              name: this.name
          })
          .then((response) => {
            console.log(response)
            this.posted = true
          })
          .catch((error) => {
            console.log(error)
          })
      },
      reset () {
          this.name = ""
          this.posted = false
      }
  }
})
</script>

<style>
</style>
