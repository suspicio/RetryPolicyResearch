<template>
  <div class="wrapper">
    <div class="header">
      <h1>Retry Policy Testing Environment</h1>
    </div>
    <div class="right-toolbar">
      <NewConfiguration />
      <ConfigurationList />
      <TestingSystem />
    </div>
  </div>

  <ProfileTesting />
</template>

<script>
import NewConfiguration from "@/components/shared/molecules/new-configuration";
import {mapActions} from "vuex";
import ConfigurationList from "@/components/shared/molecules/configurations";
import TestingSystem from "@/components/shared/molecules/testing-system";
import ProfileTesting from "@/components/shared/utils/profile-testing";

export default {
  name: "DashHero",
  components: {ProfileTesting, TestingSystem, ConfigurationList, NewConfiguration},
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