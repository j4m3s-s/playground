<template>
  <div>
    {{ card.front }}
    <button @click="showCard">Show </button>
    <div v-if="show_card">
      {{ card.back }}
    </div>
    <button @click="submitGrade(0)"> Total Blackout </button>
    <button @click="submitGrade(1)"> Incorrect but familiar </button>
    <button @click="submitGrade(2)"> Incorrect but easy to remember </button>
    <button @click="submitGrade(3)"> Correct with significant remembering
    effort </button>
    <button @click="submitGrade(4)"> Correct but hesitant </button>
    <button @click="submitGrade(5)"> Perfect </button>
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'

export default Vue.extend({
  name: 'CardList',
  data () {
    return {
      testing_workflow_id: null,
      show_card: false,
      card: {
        id: null,
        front: '',
        back: ''
      }
    }
  },
  methods: {
    fetchNextCard: function () {
    },
    showCard: function () {
      this.show_card = true
    },
    hideCard: function () {
      this.show_card = false
    },
    submitGrade: function (userGrade) {
      // FIXME: callback hell
      axios
        .post(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/testingworkflowquestions/sm2/${this.testing_workflow_id}`,
          {
            card_id: this.card.id,
            user_grade: userGrade,
            test_workflow_question_id: this.testing_workflow_id
          },
          { withCredentials: true }
        )
        .then(response => {
          axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/testingworkflowquestions/sm2/${this.testing_workflow_id}`, { withCredentials: true })
            .then(response => {
              if (response.data.id === undefined) {
                this.$router.push({
                  name: 'Cards'
                })
              }
              axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/card/${response.data.id}`, { withCredentials: true })
                .then(response => {
                  this.show_card = false
                  this.card.id = response.data.id
                  this.card.front = response.data.front
                  this.card.back = response.data.back
                })
            })
        })
    }
  },
  mounted () {
    // POST call to create testworkflow  // FIXME: fix callback hell -> async/await
    axios
      .post(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/testingworkflow/sm2/`)
      .then(response => {
        this.testing_workflow_id = response.data.id
        axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/testingworkflowquestions/sm2/${this.testing_workflow_id}`, { withCredentials: true })
          .then(response => {
            axios.get(`${process.env.VUE_APP_API_ENDPOINT_URL}/api/v1/card/${response.data.id}`, { withCredentials: true })
              .then(response => {
                this.card.id = response.data.id
                this.card.front = response.data.front
                this.card.back = response.data.back
              })
          })
      }).catch((_) => {
        // FIXME: add
        this.$router.push({ name: 'Cards' })
      })
  }
})
</script>
