<template>
  <div>
    <h1>Card Edit</h1>
    <input v-model="item.front" />
    <input v-model="item.back" />
    <button @click="submit"> submit </button>
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'CardEdit',
  data () {
    return {
      item: null
    }
  },
  methods: {
    submit: function () {
      axios.patch(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/card/${this.$route.params.id}`, this.item)
    }
  },
  mounted () {
    axios
      .get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/card/${this.$route.params.id}`)
      .then(response => (this.item = response.data))
  }
})
</script>
