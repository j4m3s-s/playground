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

async function fetchItems () {
  let next = true
  let count = 1
  let results = []
  while (next != null) {
    const res = await axios
      .get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/cards?page=${count}`)
    results = results.concat(...res.data.results)

    next = res.data.next
    count++
  }
  return results
}

export default Vue.extend({
  name: 'CardList',
  data () {
    return {
      items: null
    }
  },
  // HACK: Called everytime this component is accessed
  async setup () {
    if (this != null) {
      await this.fetchItems()
    }
  },
  methods: {
    async fetchItems () {
      this.items = await fetchItems()
    },
    async deleteCard (id) {
      await axios
        .delete(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/card/${id}`)
      await this.fetchItems()
    }
  },
  async mounted () {
    await this.fetchItems()
  }
})
</script>
