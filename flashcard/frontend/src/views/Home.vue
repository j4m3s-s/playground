<template>
  <div class="home">
    <Flashcard id=1 />
  </div>
</template>

<script>
// @ is an alias to /src
import Flashcard from '@/components/Flashcard.vue'

export default {
  name: 'Home',
  components: {
    Flashcard
  }
}
</script>
