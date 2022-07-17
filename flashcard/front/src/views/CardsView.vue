<template>
  <div>
    <h1>Cards' list</h1>
    <ul>
      <li v-for="item in items" :key="item.id">
        {{ item.front }}
        ----
        {{ item.back }}
        <router-link :to="{ name: 'CardEdit', params: { id: item.id } }"> Edit </router-link>
        <button @click="deleteCard(item.id)"> Delete </button>
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
  methods: {
    deleteCard (id) {
      axios
        .delete(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/card/${id}`).then((_) => {
          // Refresh to re-retrieve items (and thus remove deleted one)
          axios
            .get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/cards`)
            // FIXME: paging
            .then(response => (this.items = response.data.results))
        })
    }
  },
  mounted () {
    axios
      .get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/cards`)
      // FIXME: paging
      .then(response => (this.items = response.data.results))
  }
})
</script>
