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
      axios.patch(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/tag/${this.$route.params.id}`, this.item)
    }
  },
  mounted () {
    axios
      .get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/tag/${this.$route.params.id}`)
      // FIXME: paging
      .then(response => (this.item = response.data))
  }
})
</script>
