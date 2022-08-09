<template>
  <div>
    <h1>Add Card</h1>
    <textarea ref="front_input" v-model="item.front"></textarea>
    <textarea v-model="item.back" @keyup.enter="submit"></textarea>
    <button @click="submit"> submit </button>
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'CardEdit',
  mounted () {
    // Autofocus front input on component load
    this.$refs.front_input.focus()
  },
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
      axios.post(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/card/`, this.item, { withCredentials: true })
      this.$router.push({ name: 'Cards' })
    }
  }
})
</script>
