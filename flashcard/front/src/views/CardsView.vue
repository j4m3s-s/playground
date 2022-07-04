<template>
  <div>
    <h1>Cards' list</h1>
    <ul>
      <li v-for="item in items" :key="item.id">
        {{ item.front }}
        ----
        {{ item.back }}
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
      .get('http://localhost:8000/api/v1/cardtags/?tag1=tata')
      .then(response => (this.items = response.data))
  }
})
</script>
