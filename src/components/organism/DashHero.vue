<template>
  <div class="wrapper">
    <div class="header">
      <h1>Retry Policy Testing Environment</h1>
    </div>
    <div class="right-toolbar">
      <NewConfiguration />
      <ConfigurationList />
    </div>
  </div>
</template>

<script>
import NewConfiguration from "@/components/shared/molecules/new-configuration";
import {mapActions} from "vuex";
import ConfigurationList from "@/components/shared/molecules/configurations";

export default {
  name: "DashHero",
  components: {ConfigurationList, NewConfiguration},
  created() {
    this.getConfigs();
    this.getProfilesIds();
  },
  methods: {
    ...mapActions(['getConfigs', 'getProfilesIds'])
  }
}
</script>

<style lang="scss" src="./index.scss">

</style>