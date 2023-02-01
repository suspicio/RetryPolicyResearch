<template>
  <div class="testing-buttons__wrapper">
    <v-btn
        v-if="!isRunning"
        @click="startTesting()"
        color="#1cb551"
        variant="tonal">Start Testing</v-btn>
    <div v-else class="testing-buttons__started">
      <v-btn v-if="!isPaused"
          @click="pauseTesting()"
          color="#1cc5c1"
          variant="tonal">Pause Testing</v-btn>
      <v-btn v-else
             @click="unpauseTesting()"
             color="#4cd5b1"
             variant="tonal">Unpause Testing</v-btn>
      <v-btn
          @click="stopTesting()"
          color="#ff4444"
          variant="tonal">Stop Testing</v-btn>
    </div>
  </div>
</template>

<script>
import {mapActions, mapGetters} from "vuex";
import {toast} from "vue3-toastify";

export default {
  name: "TestingSystem",
  data() {
    return {
      isRunning: false,
      isPaused: false
    }
  },
  methods: {
    ...mapActions(['setTestingState']),
    ...mapGetters(['getSelectedConfigs']),

    startTesting() {
      if (this.getSelectedConfigs() !== null) {
        this.isRunning = true;
        this.setTestingState("run")
      } else {
       toast.error("Testing Configuration not Selected")
      }
    },

    pauseTesting() {
      this.isPaused = true;
      this.setTestingState("pause")
    },

    unpauseTesting() {
      this.isPaused = false;
      this.setTestingState("run")
    },

    stopTesting() {
      this.isRunning = false;
      this.isPaused = false;
      this.setTestingState("stop")
    }
  }
}
</script>

<style lang="scss" src="./index.scss">

</style>