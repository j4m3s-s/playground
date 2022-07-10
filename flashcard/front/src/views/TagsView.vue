<template>
  <div>
    <h1>Tags' list</h1>
    <ul>
      <li v-for="item in items" :key="item.id">
        {{ item.name }}
      </li>
    </ul>
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'TagList',
  data () {
    return {
      items: null
    }
  },
  mounted () {
    axios
      .get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/tags`)
      // FIXME: paging
      .then(response => (this.items = response.data.results))
  }
})
</script>
