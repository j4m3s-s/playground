<template>
    <div v-if="ustensils">
        <ul v-for="(ustensil, index) in ustensils" v-bind:key="index">
            <li> {{ ustensil.name }} </li>
        </ul>
    </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'Ustensil',
  data () {
    return {
        ustensils: null,
    }
  },
  mounted: function() {
      axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/ustensils`)
        .then((resp) => {
            this.ustensils = resp.data
            console.log(this.ustensils)
        })
  }
})
</script>

<style>
</style>
