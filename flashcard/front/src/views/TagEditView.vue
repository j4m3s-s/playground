<template>
  <div>
    <h1>Tag Edit</h1>
    <input ref="item" v-model="item.name" @keyup.enter="submit">
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
    submit: async function () {
      await axios.patch(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/tag/${this.$route.params.id}`, this.item, { withCredentials: true })
      this.$router.push({ name: 'Cards' })
    }
  },
  async mounted () {
    await axios
      .get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/tag/${this.$route.params.id}`, { withCredentials: true })
      // FIXME: paging
      .then(response => (this.item = response.data))
    this.$refs.item.focus()
  }
})
</script>
