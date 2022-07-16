<template>
  <div>
    <h1>Add Card</h1>
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
      item: {
        front: '',
        back: ''
      }
    }
  },
  methods: {
    submit: function () {
      // FIXME: url
      axios.post(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/card/`, this.item)
      this.$router.push({ name: 'Cards' })
    }
  }
})
</script>
