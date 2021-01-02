<template>
  <section class="filter">
    <div class="filter-container">
      <el-form ref="form" :inline="true">
        <el-form-item label="频道名称：">
          <el-input
            v-model="name"
            placeholder="请输入频道名称"
            class="filter-item"
            clearable
            @change="queryData"
          />
        </el-form-item>
        <el-form-item label="账号状态：">
          <!-- TODO: clearable按钮还原，下拉列表宽度，输入框宽度 -->
          <el-select v-model="selectState" placeholder="请选择状态" clearable @change="changeState">
            <el-option
              v-for="item in stateList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <el-button type="success" icon="el-icon-circle-plus-outline" @click="addData">新建</el-button>
  </section>
</template>

<script>
export default {
  props: ['changeParam', 'addData'],
  data () {
    return {
      stateList: [
        { label: '全部', value: '' },
        { label: '启动', value: 1 },
        { label: '禁用', value: 0 }
      ],
      name: '',
      selectState: ''
    }
  },
  methods: {
    queryData () {
      const params = {
        name: ''
      }

      if (this.name) {
        params.name = this.name
      }
      // TODO: 新接口的字段需要问后端老师
      // if (this.selectState !== '') {
      //   params.push({
      //     filed: 'status',
      //     type: 'eq',
      //     value: this.selectState
      //   })
      // }
      this.changeParam(params)
    },
    // 切换状态
    changeState () {
      this.queryData() // 查询数据
    }
  }
}
</script>
