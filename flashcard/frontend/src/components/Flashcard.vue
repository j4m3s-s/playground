<template>
  <div class="main-container">
    <div class="left">
      <b-jumbotron bg-variant="light" class="p-4 radius" v-on:click="show = !show">
        <p>
          {{ this.recto }}
        </p>
        <hr v-if="show" />
        <p v-if="show">
          {{ this.verso }}
        </p>
      </b-jumbotron>
    </div>
    <div class="right" />
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Flashcard',
  created: function () {
    axios.get(`http://localhost:8000/api/flashcard/${this.id}`)
      .then((response) => {
        this.recto = response.data.Recto
        this.verso = response.data.Verso
      })
  },
  data: function () {
    return {
      show: false,
      recto: null,
      verso: null
    }
  },
  props: {
    id: String
  }
}
</script>

<style scoped>
.main-container {
  display: flex;
  flex-direction: row;
}

.left{
  min-width:200px;
  width:800px;
  margin: 20px;
}

.radius {
  border-radius: 20px;
}
</style>
