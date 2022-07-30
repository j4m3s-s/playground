<template>
  <div>
    <h1>Login</h1>
    <input ref="login_input" v-model="form.username">
    <input type="password" v-model="form.password" @keyup.enter="submit">
    <button @click="submit"> submit </button>
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'LoginView',
  mounted () {
    // Autofocus front input on component load
    this.$refs.front_input.focus()
  },
  data () {
    return {
      form: {
        username: '',
        password: ''
      }
    }
  },
  methods: {
    submit: function () {
      axios.post(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/users/auth/`, this.form)
      this.$router.push({ name: 'Cards' })
    }
  }
})
</script>
