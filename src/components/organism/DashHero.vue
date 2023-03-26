<template>
  <div class="wrapper">
    <div class="header">
      <h1>Retry Policy Testing Environment</h1>
      <div>Request: {{requests}}</div>
      <div>Retry request: {{retryRequests}}</div>
    </div>
    <div class="wrapper-top-element">
      <div class="graph-wrapper">
        <Bar v-if="!showGraph" :data="dataBarChart" :options="options" :key="'bar-chart-' + key"/>
      </div>
      <div class="graph-wrapper">
        <ScatterGraphics />
      </div>
    </div>
    <div class="wrapper-top-element">
      <div class="graph-wrapper">
        <SuccessRateGraph />
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
</template>

<script>
import NewConfiguration from "@/components/shared/molecules/new-configuration";
import {mapActions} from "vuex";
import ConfigurationList from "@/components/shared/molecules/configurations";
import TestingSystem from "@/components/shared/molecules/testing-system";
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
import SuccessRateGraph from "@/components/shared/molecules/graphics/SuccessRateGraph";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

export default {
  name: "DashHero",
  components: {
    SuccessRateGraph,
    ScatterGraphics, TestingSystem, ConfigurationList, NewConfiguration, Bar},
  created() {
    this.getConfigs();
    this.debounce();
  },
  data() {
    return {
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: {
            display: true,
            title: {
              display: true,
              text: 'Response codes',
              color: '#911',
              padding: {top: 20, left: 0, right: 0, bottom: 0}
            }
          },
          y: {
            display: true,
            title: {
              display: true,
              text: 'Count',
              color: '#191',
              padding: {top: 30, left: 0, right: 0, bottom: 0}
            }
          }
        }
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
      },
      requests: 0,
      retryRequests: 0
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
    ...mapActions(['getConfigs', 'getRespEntry', 'gatherStats', 'getRequests']),

    debounce() {
      this.gatherStats();

      this.getRequests().then((resp) => {
        console.log(resp)
        this.requests = resp.requests;
        this.retryRequests = resp.retryRequests;
      });

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