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
        <el-form-item prop="signAgreement" class="signagreement-input-box">
          <el-checkbox v-model="ruleForm.signAgreement">我已阅读并同意&nbsp;用户协议&nbsp;和&nbsp;隐私条款</el-checkbox>
        </el-form-item>
        <el-form-item class="loginBtn">
          <el-button type="primary" @click="submitForm('ruleForm')">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { loginByUsername } from '@/api/login'
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
    var validateSignAgreement = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请阅读并同意协议'))
      } else {
        callback()
      }
    }
    return {
      ruleForm: {
        name: 'guest',
        password: 'guest',
        signAgreement: true
      },
      rules: {
        name: [
          { validator: validateName, trigger: 'blur' }
        ],
        password: [
          { validator: validatePass, trigger: 'blur' }
        ],
        signAgreement: [
          { validator: validateSignAgreement, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async submitForm () {
      // this.$router.replace({ path: '/auth/index' })
      this.$refs.ruleForm.validate(async (valid) => {
        if (valid) {
          const { password, name } = this.ruleForm

          const result = await loginByUsername(name, password)
          if (result.code === 200) {
            // this.$router.replace({ path: '/users/index' })
            this.$router.replace({ path: '/auth/index' })
          } else {
            this.$message({
              message: result.error_message,
              type: 'error'
            })
          }
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
$input-height: 60px;

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

    .name-input-box,
    .signagreement-input-box {
      margin-bottom: 35px;
    }

    .password-input-box {
      margin-bottom: 50px;
    }

    /deep/ .el-input__inner {
      height: $input-height;
      padding-left: 70px;
      line-height: $input-height;
    }

    /deep/ .el-input__icon {
      width: 66px;
      height: $input-height;
      font-size: 28px;
      line-height: $input-height;
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
