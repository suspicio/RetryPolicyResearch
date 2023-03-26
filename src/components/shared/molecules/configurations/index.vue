<template>
  <v-expansion-panels>
    <v-expansion-panel title="Configuration List">
      <v-expansion-panel-text eager>
        <div class="list" v-for="configs in configsList" :key="configs.id">
        <div class="list-wrapper" :class="{'list-selected': selectedID === configs.id}">
          <div class="list-element">
            Requests per second: <span>{{configs["request-per-second"]}}</span>
          </div>
          <div class="list-element">
            Retry Policy: <span>{{configs["retry-policy-type"]}}</span>
          </div>
          <div class="list-element">
            Count Limit: <span>{{configs["count-limit"]}}</span>
          </div>
          <div class="list-element">
            Base timeout: <span>{{configs["base-timeout"]}}</span>
          </div>
          <div class="list-element">
            <v-btn
                v-if="selectedID !== configs.id"
                   @click="selectConfig(configs.id)"
                   color="#1cb551"
                   variant="tonal">
              Select
            </v-btn>
            <v-btn
                v-else
                @click="unselectConfig()"
                color="#1cb551"
                variant="tonal">
              Unselect
            </v-btn>
            <v-btn
                   @click="deleteConf(configs.id)"
                   color="#ff4444"
                   variant="tonal">
              Delete
            </v-btn>
          </div>

        </div>
        </div>
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<script>
import 'vue3-toastify/dist/index.css';
import {mapActions, mapGetters} from "vuex";
import {toast} from "vue3-toastify";

export default {
  name: "ConfigurationList",
  data() {
    return {
      selectedID: ''
    };
  },
  computed: {
    configsList() {
      return this.getConfigs()
    }
  },
  methods: {
    ...mapActions(['deleteConfig', 'setSelectedConfig']),
    ...mapGetters(['getConfigs', 'getTestingState']),

    checkForState() {
      return this.getTestingState() !== "stop";
    },

    deleteConf(id) {
      if (this.checkForState()) {
        toast.error('Configs changes forbidden during run, please stop testing')
        return
      }
      if (id === this.selectedID) {
        this.unselectConfig();
      }
      this.deleteConfig(id)
    },

    selectConfig(id) {
      if (this.checkForState()) {
        toast.error('Configs changes forbidden during run, please stop testing')
        return
      }
      this.selectedID = id
      this.setSelectedConfig(this.configsList.filter((config) => config.id === id)[0])
    },

    unselectConfig() {
      if (this.checkForState()) {
        toast.error('Configs changes forbidden during run, please stop testing')
        return
      }
      this.selectedID = ''
      this.setSelectedConfig(null)
    }
  }
}
</script>

<style lang="scss" src="./index.scss"/>