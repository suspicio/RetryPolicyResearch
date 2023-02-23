<template>
  <div class="wrapper">
    <div class="header">
      <h1>Retry Policy Testing Environment</h1>
    </div>
    <div class="wrapper-top-element">
      <div class="graph-wrapper">
        <Bar v-if="!showGraph" :data="dataBarChart" :options="options" :key="'bar-chart-' + key"/>
      </div>
      <div class="graph-wrapper">
        <ScatterGraphics />
      </div>
    </div>
    <div class="toolbar">
      <div class="toolbar-element">
        <NewConfiguration />
      </div>
      <div class="toolbar-element">
        <ConfigurationList />
      </div>
      <div class="toolbar-element">
        <TestingSystem />
      </div>
    </div>
  </div>
  <ProfileTesting />
</template>

<script>
import NewConfiguration from "@/components/shared/molecules/new-configuration";
import {mapActions, mapGetters} from "vuex";
import ConfigurationList from "@/components/shared/molecules/configurations";
import TestingSystem from "@/components/shared/molecules/testing-system";
import ProfileTesting from "@/components/shared/utils/profile-testing";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js'
import { Bar } from 'vue-chartjs'
import ScatterGraphics from "@/components/shared/molecules/graphics/ScatterGraphics";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

export default {
  name: "DashHero",
  components: {ScatterGraphics, ProfileTesting, TestingSystem, ConfigurationList, NewConfiguration, Bar},
  created() {
    this.getConfigs();
    this.getProfilesIds();
    this.debounce();
  },
  data() {
    return {
      options: {
        responsive: true,
        maintainAspectRatio: false
      },
      showGraph: false,
      key: 0,
      dataBarChart: {
        labels: [],
        datasets: [
          {
            label: 'Requests responses',
            backgroundColor: "#f87979",
            data: []
          }
        ]
      }
    }
  },
  watch: {
    dataBarChart: {
      handler() {
        this.key++
      },
      deep: true
    }
  },
  methods: {
    ...mapActions(['getConfigs', 'getProfilesIds', 'getRequestsTime', 'getRespEntry']),
    ...mapGetters(['getRequestCount']),

    debounce() {
      this.getRespEntry().then(
          resp => {
            const reqs = resp
            this.dataBarChart.labels = []
            this.dataBarChart.datasets[0].data = []
            Object.keys(reqs).forEach((key) => {
              this.dataBarChart.labels.push(key)
              this.dataBarChart.datasets[0].data.push(reqs[key])
            })
            setTimeout(() => {this.debounce()}, 5000);
          }
      )

    }
  }
}
</script>

<style lang="scss" src="./index.scss">

</style>