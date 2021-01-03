<template>
  <section class="chart">
    <header class="header">
      图文数据
    </header>
    <div ref="chart" :style="{height:height,width:width}"/>
    <!-- TODO: 抽出组件 -->
    <el-row type="flex" align="middle" class="legend">
      <el-col :span="8">
        <div><span class="num">15.6</span>%</div>
        <div><span class="circle circle-bg1"></span><span class="text">平均阅读进度</span></div>
      </el-col>
      <el-col :span="8">
        <div><span class="num">28</span>%</div>
        <div><span class="circle circle-bg2"></span><span class="text">跳出率</span></div>
      </el-col>
      <el-col :span="8">
        <div><span class="num">52</span>字/秒</div>
        <div><span class="circle circle-bg3"></span><span class="text">平均阅读速度</span></div>
      </el-col>
    </el-row>
  </section>

</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
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
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        series: [{
          name: '图文数据',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          label: {
            normal: {
              show: false,
              position: 'center'
            },
            emphasis: {
              show: false,
              textStyle: {
                fontSize: '30',
                fontWeight: 'bold'
              }
            }
          },
          labelLine: {
            normal: {
              show: false
            }
          },
          data: [
            { value: 335, name: '平均阅读进度' },
            { value: 310, name: '跳出率' },
            { value: 234, name: '平均阅读速度' }
          ]
        }],
        color: ['#2BB3FD', '#FF584F', '#48BF6D']
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
          background-color: #2BB3FD;
        }
        .circle-bg2 {
          background-color: #FF584F;
        }
        .circle-bg3 {
          background-color: #48BF6D;
        }
      }
      .el-col:last-child {
        border: none;
      }
    }
  }
</style>
