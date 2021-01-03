<template>
  <div class="login">
    <div class="container">
      <img src="@/assets/logo_index.png" class="user-avatar" />
      <el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm" class="login-ruleForm">
        <el-form-item prop="name" class="name-input-box">
          <el-input
            type="text"
            v-model="ruleForm.name"
            autocomplete="off"
            placeholder="请输入账户名"
            prefix-icon="el-icon-user"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password" class="password-input-box">
          <el-input
            type="password"
            v-model="ruleForm.password"
            autocomplete="off"
            placeholder="请输入密码"
            prefix-icon="el-icon-key"
          ></el-input>
        </el-form-item>
        <div class="allow">
          <div id="myCode"></div>
          <el-checkbox v-model="checked"></el-checkbox>我已阅读并同意
          <a href="javascript:;">用户协议</a>和
          <a href="javascript:;">隐私条款</a>
        </div>
        <el-form-item class="loginBtn">
          <el-button type="primary" @click="submitForm('ruleForm')">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import gt from '@/components/gt' // 人机交互验证码
import { loginByUsername, getMobileCode, getCaptchas } from '@/api/login'
export default {
  data () {
    var validateName = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入登录用户名'))
      } else {
        callback()
      }
    }
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        callback()
      }
    }
    return {
      checked: true,
      ruleForm: {
        name: 'admin',
        password: 'admin'
      },
      rules: {
        name: [
          { validator: validateName, trigger: 'blur' }
        ],
        password: [
          { validator: validatePass, trigger: 'blur' }
        ]
      }
    }
  },
  components: {
  },
  computed: {
  },
  methods: {
    async submitForm () {
      const { password, name } = this.ruleForm
      if (!name || !password) {
        this.$message({
          message: '用户名和密码不能为空',
          type: 'warning'
        })
        return
      }
      // 登录
      const result = await loginByUsername(name, password) // 登录
      if (result.code === 200) {
        // this.$router.replace({ path: '/index' }) // 跳转
        this.$router.replace({ path: '/article/publish' }) // 跳转
      } else {
        this.$message({
          message: result.error_message,
          type: 'error'
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
$input-height: 61px;

.login {
  background-image: url('../../assets/login_bg.jpg');
  background-size: cover;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  .container {
    background-color: #ffffff;
    width: 650px;
    height: 543px;
    padding-top: 40px;
    img {
      display: block;
      margin: 0 auto;
    }
  }
  .login-ruleForm {
    padding: 45px 52px 0 53px;
    .allow {
      text-align: left;
      font-size: 14px;
      margin-bottom: 36px;
      color: #999999;
      a {
        color: #3296fa;
      }
      .el-checkbox {
        margin-right: 10px;
      }
    }
    .name-input-box {
      margin-bottom: 35px;
    }
    .password-input-box {
      margin-bottom: 50px;
    }
    /deep/ .el-input__inner {
      height: $input-height;
      padding-left: 71px;
      line-height: $input-height;
    }
    /deep/ .el-input__icon {
      width: 66px;
      height: $input-height;
      font-size: 28px;
      line-height: $input-height;
    }
    .checkCode {
      .el-input {
        width: 60%;
        float: left;
      }
      .el-button {
        width: 35%;
        float: right;
        span {
          width: 100%;
          display: inline-block;
        }
      }
    }
    .loginBtn {
      /deep/ .el-button {
        width: 100%;
        height: auto;
        padding: 20px 20px;
        font-size: 28px;
        border: none;
      }
    }
  }
}
</style>
