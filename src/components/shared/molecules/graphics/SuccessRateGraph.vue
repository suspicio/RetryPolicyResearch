<template>
  <Scatter :data="datasets" :options="options" :key="'scatter-chart-' + key"/>
</template>

<script>
import {BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, PointElement, Title, Tooltip} from "chart.js";
import { Scatter } from 'vue-chartjs'
import {mapActions} from "vuex";

ChartJS.register(CategoryScale, LinearScale, PointElement, BarElement, Title, Tooltip, Legend)

export default {
  name: "SuccessRateGraph",
  components: { Scatter },
  data() {
    return {
      key: 1,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: {
            display: true,
            title: {
              display: true,
              text: 'Time from start in s',
              color: '#911',
              padding: {top: 20, left: 0, right: 0, bottom: 0}
            }
          },
          y: {
            display: true,
            title: {
              display: true,
              text: 'Percent of success',
              color: '#191',
              padding: {top: 30, left: 0, right: 0, bottom: 0}
            }
          }
        }
      },
      datasets: {
        datasets : [{
          label: 'Success rate',
          fill: false,
          borderColor: '#7acbf9',
          backgroundColor: '#7acbf9',
          data: []
        }]
      },
    }
  },
  created() {
    this.debounce();
  },
  watch: {
    datasets: {
      handler() {
        this.key++
      },
      deep: true
    }
  },
  methods: {
    ...mapActions(['getSuccessEntry']),
    debounce() {
      this.getSuccessEntry().then((resp) => {
        this.datasets.datasets[0].data = resp
        setTimeout(() => {this.debounce()}, 5000);
      })
    }
  }
}
</script>