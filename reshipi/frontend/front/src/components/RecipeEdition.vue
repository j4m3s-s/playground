<template>
    <div v-if="!posted">
        <!-- direct in item -->
        <input type="text" v-model="name" />

        <!-- Ustensils -->
        <h2> Ustensiles </h2>
        <h4> Sélectionnés </h4>

        <ul>
            <li v-for="(ustensil, i) in ustensils" v-bind:key="i" >
                {{ ustensil.name }} <button @click="removeFromUstensils(i)"> - </button>
            </li>
        </ul>

        <!-- TODO: optimize search -->
        <h4> Recherche </h4>
        <form @submit="ustensilSearch">
            <input type="text" v-model="ustensil_search_name" />
            <button> Chercher l'ustensil </button>
        </form>

        <h4> Résultats de la recherche </h4>
        <ul>
            <li v-for="(ustensil, i) in ustensils_searched" v-bind:key="i">
                {{ ustensil.name }} <button @click="addToUstensils(i)"> + </button>
            </li>
        </ul>

        <!-- Ingredients -->
        <!-- Steps -->
        <button v-on:click="createRecipe"> Créer la recette </button>
    </div>
    <div v-else>
      Successfuly posted
      <button v-on:click="reset"> Créer une nouvelle recette </button>
    </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'RecipeEdition',
  data () {
    return {
        name: "",
        cook_time: null,
        prep_time: null,
        ustensils: [],
        ustensils_searched: [],
        ustensil_search_name: "",
        posted: false
    }
  },
  methods: {
      removeFromUstensils (index) {
          console.log(index)
          console.log(this.ustensils)
          this.ustensils.splice(index, 1)
      },
      addToUstensils (index) {
          for (let i = 0; i < this.ustensils.length; i++) {
            if (this.ustensils[i].id === this.ustensils_searched[index].id)
                return
          }

          this.ustensils.push(this.ustensils_searched[index])
          this.ustensils.sort(function(a, b) { return a.id - b.id })
      },
      ustensilSearch(e) {
          e.preventDefault()

          axios.get("http://localhost:3000/ustensils")
          .then((response) => {
            console.log(response.data)

            let result_arr = []
            for (let i = 0; i < response.data.length; i++) {
                if (response.data[i].name.startsWith(this.ustensil_search_name)) {
                    result_arr.push(response.data[i])
                }
            }
            this.ustensils_searched = result_arr
          })
          .catch((error) => {
            console.log(error)
          })

      },
      createRecipe () {
          axios.post("http://localhost:3000/recipes", {
              name: this.name,
          }).then((response) => {
              console.log(response.data)

              this.ustensils.map((ustensil) => {
              axios.post("localhost:3000/ustensils_recipe", {
                  ustensil_id: ustensil.id,
                  recipe_id: response.data.id
              }).then((response) => {
                  console.log(response)
              }).catch((error) => {
                  console.log(error)
              })
          })

          }).catch((error) => {
              console.log(error)
              return
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
