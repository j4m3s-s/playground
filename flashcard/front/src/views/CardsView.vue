<template>
  <div>
    <h1>Cards' list</h1>
    <ul>
      <li v-for="item in items" :key="item.id">
        {{ item.front }}
        ----
        {{ item.back }}
        <router-link :to="{ name: 'CardEdit', params: { id: item.id } }"> Edit </router-link>
        <div v-for="tag in item.tags" :key="tag.id"> #{{ tag }} </div>
      </li>
    </ul>
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'CardList',
  data () {
    return {
      items: null
    }
  },
  mounted () {
    axios
      .get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/cards`)
      .then(response => (this.items = response.data.results))
  }
})
</script>
