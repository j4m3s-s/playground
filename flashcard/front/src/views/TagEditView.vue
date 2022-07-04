<template>
  <div>
    <h1>Tag Edit</h1>
    <input v-model="item.name" />
    <button @click="submit"> submit </button>
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'TagEdit',
  data () {
    return {
      item: null
    }
  },
  methods: {
    submit: function () {
      // FIXME: url
      axios.patch(`http://localhost:8000/api/v1/tag/${this.$route.params.id}`, this.item)
    }
  },
  mounted () {
    axios
      // FIXME: url
      .get(`http://localhost:8000/api/v1/tag/${this.$route.params.id}`)
      // FIXME: paging
      .then(response => (this.item = response.data))
  }
})
</script>
