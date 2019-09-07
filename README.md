# springboot-blog

## 博客搭建

### 项目描述
与前端合作完成博客项目的搭建工作，具体分为认证模块和博客模块两个接口，登录模块包括用户注册、用户登录以及判断是否登录相关功能，博客模块包括获取博客列表、创建博客以及基本的博客CRUD功能。

### 使用技术
SpringBoot+Maven+IDEA+Docker+MyBatis+Jenkins

### 项目心得
该项目使用SpringBoot完成登录模块的构建并使用Cookie维持登录状态，使用Docker创建相关的MySQL数据库，并利用ORM连接，最后使用Flyway进行数据库的变更管理；对controller包以及service包进行了相应的单元测试；对项目的认证系统进行了集成测试；博客模块使用的是TDD方式开发，并配置和使用了MyBatis；最后使用了Docker与Jenkins，以实现自动化部署发布与回滚。
