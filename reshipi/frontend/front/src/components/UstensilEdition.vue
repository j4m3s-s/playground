<template>
    <div v-if="!posted">
        <input type="text" v-model="name" />
        <button v-on:click="createUstensil"> Créer </button>
    </div>
    <div v-else>
      Successfuly posted
      <button v-on:click="reset"> Créer un nouvel ustensil </button>
    </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'UstensilEdition',
  data () {
    return {
        name: "",
        posted: false
    }
  },
  methods: {
      createUstensil () {
          axios.post(`${process.env.VUE_APP_API_ENDPOINT_URL}/ustensils`, {
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
