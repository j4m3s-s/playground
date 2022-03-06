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
        <h2> Ingredients </h2>
        <h4> Sélectionnés </h4>

        <ul>
            <li v-for="(ingredient, i) in ingredients" v-bind:key="i" >
                {{ ingredient.name }} <button @click="removeFromIngredients(i)"> - </button>
            </li>
        </ul>

        <h4> Recherche </h4>
        <form @submit="ingredientSearch">
            <input type="text" v-model="ingredient_search_name" />
            <button> Chercher l'ingrédient </button>
        </form>

        <h4> Résultats de la recherche </h4>
        <ul>
            <li v-for="(ingredient, i) in ingredients_searched" v-bind:key="i">
                {{ ingredient.name }} <button @click="addToIngredient(i)"> + </button>
            </li>
        </ul>
        <!-- Steps -->
        <h2> Étapes </h2>
        <ul>
            <li v-for="(step, i) in steps" v-bind:key="i">
                <input type="text" v-model="steps[i]" />
                <button @click="removeStep(i)" > - </button>
            </li>
            <button @click="addStep" > Ajout d'une nouvelle étape </button>
        </ul>
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
        ingredients: [],
        ingredients_searched: [],
        ingredient_search_name: "",
        steps: [],
        posted: false
    }
  },
  methods: {
      addStep () {
          this.steps.push("")
      },
      removeStep (index) {
          this.steps.splice(index, 1)
      },
      removeFromUstensils (index) {
          console.log(index)
          console.log(this.ustensils)
          this.ustensils.splice(index, 1)
      },
      removeFromIngredients (index) {
          this.ingredients.splice(index, 1)
      },
      addToUstensils (index) {
          for (let i = 0; i < this.ustensils.length; i++) {
            if (this.ustensils[i].id === this.ustensils_searched[index].id)
                return
          }

          this.ustensils.push(this.ustensils_searched[index])
          this.ustensils.sort(function(a, b) { return a.id - b.id })
      },
      addToIngredient (index) {
          for (let i = 0; i < this.ingredients.length; i++) {
            if (this.ingredients[i].id === this.ingredients_searched[index].id)
                return
          }

          this.ingredients.push(this.ingredients_searched[index])
          this.ingredients.sort(function(a, b) { return a.id - b.id })

      },
      ustensilSearch(e) {
          e.preventDefault()

          axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/ustensils`)
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
      ingredientSearch(e) {
          e.preventDefault()

          axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/ingredients`)
          .then((response) => {
            console.log(response.data)

            let result_arr = []
            for (let i = 0; i < response.data.length; i++) {
                if (response.data[i].name.startsWith(this.ingredient_search_name)) {
                    result_arr.push(response.data[i])
                }
            }
            this.ingredients_searched = result_arr
          })
          .catch((error) => {
            console.log(error)
          })
      },
      async createRecipe () {
          await axios.post(`${process.env.VUE_APP_API_ENDPOINT_URL}/recipes`, {
              name: this.name,
          })

          let recipe_create = axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/recipes?name=eq.${this.name}`, {
              name: this.name,
          })

          let recipe_create_response = await recipe_create
          console.log(recipe_create_response)
          // TODO: don't ignore multiple answers to call
          let recipe_id = recipe_create_response.data[0].id

          this.ustensils.map((ustensil) => {
              axios.post(`${process.env.VUE_APP_API_ENDPOINT_URL}/ustensils_recipe`, {
                  ustensil_id: ustensil.id,
                  recipe_id: recipe_id
              }).then((response) => {
                  console.log(response)
              }).catch((error) => {
                  console.log(error)
              })
          })


          this.ingredients.map((ingredient) => {
              axios.post(`${process.env.VUE_APP_API_ENDPOINT_URL}/ingredients_recipe`, {
                  ingredient_id: ingredient.id,
                  recipe_id: recipe_id
              }).then((response) => {
                  console.log(response)
              }).catch((error) => {
                  console.log(error)
              })
          })

          let position = 0
          this.steps.map((step) => {
              console.log(`Step : ${position} : ${step}`)
              axios.post(`${process.env.VUE_APP_API_ENDPOINT_URL}/steps`, {
                  text: step,
                  recipe_id: recipe_id,
                  position: position
              }).catch((error) => {
                  console.log(error)
              })
              position++
          })
          this.posted = true
      },
      reset () {
          this.name = ""
          this.posted = false
          this.ustensils = []
          this.ustensils_searched = []
          this.ustensil_search_name = ""
          this.ingredients = []
          this.ingredients_searched = []
          this.ingredient_search_name = ""
          this.steps = []
          this.steps_id = []
          this.posted = false
      }
  }
})
</script>

<style>
</style>
