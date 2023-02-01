<template>
  <div></div>
</template>

<script>
import {mapActions, mapGetters} from "vuex";

export default {
  name: "ProfileTesting",
  data() {
    return {
      currentSentRequests: 0,
      id: 0,
      successRate: 0,
      requestsInLastSecond: 0
    }
  },
  watch: {
    testingState(newState, oldState) {
      if (newState === 'run' && oldState === 'stop') {
        this.test();
      }
    }
  },
  computed: {
    testingState() {
      return this.getTestingState();
    },

    selectedConfigs() {
      return this.getSelectedConfigs();
    },

    profilesCount() {
      return this.getCountOfProfiles();
    }
  },
  methods: {
    ...mapGetters(['getTestingState', 'getSelectedConfigs', 'getCountOfProfiles']),
    ...mapActions(['createRandomProfile', 'updateRandomProfile', 'deleteRandomProfile', 'getRandomProfile', 'getProfilesCount']),

    randomIntFromInterval(min, max) { // min and max included
      return Math.floor(Math.random() * (max - min + 1) + min)
    },

    test() {
      while (this.testingState !== 'stop') {
        if (this.testingState === 'pause') {
          setTimeout(this.test, 100);
          break;
        }

        if (this.requestsInLastSecond < this.selectedConfigs['request-per-second']) {
          this.requestsInLastSecond++;
          setTimeout(() => {this.requestsInLastSecond--}, 1000);
          this.request(this.typeConvert(this.randomIntFromInterval(1, 100)));
        } else {
          setTimeout(this.test, 100);
          break;
        }
      }
    },

    typeConvert(number) {
      if (this.profilesCount < this.selectedConfigs['count-limit']) {
        if (number < 24) {
          return 'create'
        } else if (number < 48) {
          return 'update'
        } else if (number < 50) {
          return 'delete'
        } else if (number < 96) {
          return 'get'
        } else {
          return 'count'
        }
      } else {
        if (number < 32) {
          return 'update'
        } else if (number < 64) {
          return 'delete'
        } else if (number < 96) {
          return 'get'
        } else {
          return 'count'
        }
      }
    },

    request(type) {
      switch (type) {
        case 'create':  this.createRandomProfile(); break;
        case 'update':  this.updateRandomProfile(); break;
        case 'delete':  this.deleteRandomProfile(); break;
        case 'get':     this.getRandomProfile();    break;
        case 'count':   this.getProfilesCount();    break;
      }
    }
  }
}
</script>