<template>
  <Scatter :data="datasets" :options="options" :key="'scatter-chart-' + key"/>
</template>

<script>
import {BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, PointElement, Title, Tooltip} from "chart.js";
import { Scatter } from 'vue-chartjs'
import {mapActions} from "vuex";

ChartJS.register(CategoryScale, LinearScale, PointElement, BarElement, Title, Tooltip, Legend)

export default {
  name: "ScatterGraphics",
  components: { Scatter },
  data() {
    return {
      key: 1,
      options: {
        responsive: true,
        maintainAspectRatio: false
      },
      datasets: {
        datasets : [{
          label: 'Time spent',
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
    ...mapActions(['getTimeEntry']),
    debounce() {
      this.getTimeEntry().then(
          resp => {
            this.datasets.datasets[0].data = resp
            setTimeout(() => {this.debounce()}, 5000);
          }
      )

    }
  }
}
</script>