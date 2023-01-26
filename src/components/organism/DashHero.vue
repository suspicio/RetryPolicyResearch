<template>
  <div>Hero Component</div>
</template>

<script>
import axios from "axios"

export default {
  name: "DashHero",
  created() {
    axios.get("http://127.0.0.1:8080/profile/count").then((resp) => {
      console.log(resp)
    })
  }
}
</script>

<style scoped>

</style>