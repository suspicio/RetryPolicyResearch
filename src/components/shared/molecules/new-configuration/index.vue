<template>
  <v-expansion-panels>
    <v-expansion-panel title="Add configuration">
      <v-expansion-panel-text eager>
        <v-text-field label="Request Per Second"
                      v-model="requestPerSecond"
                      validate-on="input"
                      type="input"
                      hint="Integer numbers in range [1:1000]"
                      :rules="requestPerSecondRules"/>
        <v-text-field label="Database Limit"
                      v-model="countLimit"
                      validate-on="input"
                      type="input"
                      hint="Integer numbers in range [1:100.000]"
                      :rules="countLimitRules"/>
        <v-text-field label="Base timeout"
                      v-model="baseTimeout"
                      validate-on="input"
                      type="input"
                      hint="Integer numbers in range [1:5]"
                      :rules="baseTimeoutRules"/>
        <v-select v-model="retryPolicy" label="Retry Policy" :rules="retryPolicyRules" :items="retryPolicyList" />
        <v-btn :loading="loadingSaveBtn"
               :disabled="loadingSaveBtn"
               @click="loadConfigs"
               color="#1cb551"
               variant="tonal">
          Save
        </v-btn>
        <v-btn :loading="loadingResetBtn"
               :disabled="loadingResetBtn"
               @click="resetLoad"
               color="#ff4444"
               variant="tonal">
          Reset
        </v-btn>
      </v-expansion-panel-text>
    </v-expansion-panel>
  </v-expansion-panels>
</template>

<script>
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';
import {mapActions} from "vuex";

export default {
  name: "NewConfiguration",
  data() {
    return {
      requestPerSecond: 1,
      requestPerSecondRules: [
        value => !!value || 'Required.',
        value => (value && value > 0 && value <= 1000) || 'Please enter only integer in [1-1000] range!'
      ],
      countLimit: 1,
      baseTimeout: 1,
      countLimitRules: [
        value => !!value || 'Required.',
        value => (value && value > 0 && value <= 100000) || 'Please enter only integer in [1-100000] range!'
      ],
      baseTimeoutRules: [
        value => !!value || 'Required.',
        value => (value && value > 0 && value <= 5) || 'Please enter only integer in [1-5] range!'
      ],
      retryPolicyList: ["simple", "simple delay", "cancel", "incremental delay", "exponential backoff", "fibonacci backoff", "LILD", "LIMD", "MILD", "MIMD"],
      retryPolicyRules: [
          value => !!value || 'Required.'
      ],
      retryPolicy: "simple",
      loadingSaveBtn: false,
      loadingResetBtn: false
    };
  },
  methods: {
    ...mapActions(['createConfig']),

    loadConfigs() {
      this.loadingSaveBtn = true
      let isValidated = this.validator(this.requestPerSecond, this.requestPerSecondRules)
          && this.validator(this.retryPolicy, this.retryPolicyRules)
          && this.validator(this.countLimit, this.countLimitRules);
      if (isValidated) {
        this.createConfig({
          "id": "",
          "request-per-second": this.requestPerSecond,
          "retry-policy-type": this.retryPolicy,
          "count-limit": this.countLimit,
          "base-timeout": this.baseTimeout
        }).then(resp => {
          if (resp) {
            toast.success("Config saved successfully")
          } else {
            toast.error("Something went wrong")
          }
        })
      } else {
        toast.error("Some of the fields filled wrongly");
      }
      setTimeout(() => this.loadingSaveBtn = false, 300);
    },

    resetLoad() {
      this.loadingResetBtn = true;
      this.requestPerSecond = 1;
      this.retryPolicy = "default";
      this.countLimit = 1;
      setTimeout(() => this.loadingResetBtn = false, 300);
    },

    validator(value, rules) {
      let checked = true;
      rules.forEach((rule) => {
        checked = checked && rule(value)
      })
      return typeof checked !== 'string'
    }
  }
}
</script>

<style lang="scss" src="./index.scss"/>