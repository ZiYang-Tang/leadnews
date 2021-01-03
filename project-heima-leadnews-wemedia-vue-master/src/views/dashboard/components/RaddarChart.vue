<template>
  <section class="chart">
    <header class="header">
      粉丝数据
    </header>
    <div ref="chart" :style="{height:height,width:width}"/>
    <!-- TODO: 抽出组件 -->
    <el-row type="flex" align="middle" class="legend">
      <el-col :span="8">
        <div><span class="num">56</span></div>
        <div><span class="circle circle-bg1"></span><span class="text">新增粉丝</span></div>
      </el-col>
      <el-col :span="8">
        <div><span class="num">1352</span></div>
        <div><span class="circle circle-bg2"></span><span class="text">活跃粉丝</span></div>
      </el-col>
      <el-col :span="8">
        <div><span class="num">33</span></div>
        <div><span class="circle circle-bg3"></span><span class="text">总粉丝数</span></div>
      </el-col>
    </el-row>
  </section>

</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
// import { debounce } from '@/utils'

const animationDuration = 3000

export default {
  props: {
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '253px'
    }
  },
  data () {
    return {
      chart: null
    }
  },
  mounted () {
    this.initChart()
  },
  beforeDestroy () {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart () {
      this.chart = echarts.init(this.$refs.chart, 'macarons')

      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        radar: {
          radius: '66%',
          indicator: [
            { name: '引用', max: 10000 },
            { name: '产量', max: 20000 },
            { name: '贡献', max: 20000 },
            { name: '热度', max: 20000 },
            { name: '口碑', max: 20000 }
          ],
          axisLine: {
            lineStyle: {
              color: '#ccc',
              opacity: 0.2
            }
          },
          splitLine: {
            lineStyle: {
              opacity: 0.2
            }
          },
          splitArea: {
            show: false
          }
        },
        series: [{
          type: 'radar',
          symbolSize: 0,
          data: [
            {
              value: [5000, 7000, 12000, 11000, 15000, 14000],
              name: 'Allocated Budget'
            },
            {
              value: [4000, 9000, 15000, 15000, 13000, 11000],
              name: 'Expected Spending'
            },
            {
              value: [5500, 11000, 12000, 15000, 12000, 12000],
              name: 'Actual Spending'
            }
          ],
          animationDuration: animationDuration
        }],
        color: ['#178FFF', '#FACC14', '#2FC25B']
      })
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
  .chart {
    background-color: #ffffff;
    border: 1px solid #e7e7e9;
    margin-top: 19px;
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      color: #323745;
      font-size: 20px;
      height: 63px;
      padding: 0 15px;
      background-color: #fbfbfb;
      border-bottom: 1px solid #e8e8e8;
    }
    .legend {
      height: 133px;
      border: 1px solid #e9e9e9;
      margin: 0 10px 10px 10px;
      .el-col {
        border-right: 1px solid #e9e9e9;
        .num {
          display: inline-block;
          margin-right: 3px;
          font-size: 35px;
          color: #444444;
        }
        .text {
          font-size: 18px;
          color: #AFAFAF;
        }
        .circle {
          display: inline-block;
          width: 11px;
          height: 11px;
          margin-right: 5px;
          border-radius: 50%;
        }
        .circle-bg1 {
          background-color: #178FFF;
        }
        .circle-bg2 {
          background-color: #FACC14;
        }
        .circle-bg3 {
          background-color: #2FC25B;
        }
      }
      .el-col:last-child {
        border: none;
      }
    }
  }
</style>
