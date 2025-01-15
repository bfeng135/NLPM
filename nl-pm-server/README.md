# 北光项目管理系统
## nl-pm-server
# 
#### 介绍
本框架基于spring-boot+spring-security。
本框架基于区域、用户的模式。

#### 使用说明

1. 放行登录url：localhost:9521/nl-pm-server/login  ，返回用户相关信息及token
2. 其余访问请求需携带Bearer token.
3. 权限相关内容查看security文件夹
4. 异常处理相关内容查看exception文件夹
5. swagger http://127.0.0.1:9521/nl-pm-server/swagger-ui.html

#### 登录与权限相关
初始化超级管理员

区域id：1

用户名：superadmin

密码：123456



1. **登录url**
   localhost:9521/nl-pm-server/login
2. **登录验证**
   参考security包，本架构基于filter和provider，通过WebSecurityConfig配置校验项。
   密码采用md5加密，共用方法TokenTools.md5Security(String str)
   
3. **token验证**
   参考security.filter包下JwtTokenFilter。
   登录生成token用于其他url的验证

#### 日志相关
4. **操作日志**
    参考operationLog包下的内容，对需要记录操作日志的controller方法，在方法上加@OperationLog注解即可。
   如要对入参进行定制，可在controller下面的formatter包中编写自己的formatter类，实现ILogFormatter接口

#### 异常相关
5. **异常**
    本框架区分权限部分的异常和业务部分的异常，即401和500分别写在不同的异常处理类中，参考代码在exception包下，
   异常枚举分为AuthErrorCodeEnum记录401的对应BaseAuthException，ServiceErrorCodeEnum记录500的对应
   BaseServiceException

#### 共用工具类
6. **工具类**
    如要获取全局区域变量，请调用security.tools包下的TokenTools.getCurrentAreaId()。
   其余工具类请参考common包中的内容。获取当前用户名清调用SecurityContextUtils.getCurrentUsername()

#### 数据库
7. 本框架采用mysql数据库+mybatis-plus
   
8. **mysql配置与初始化**
   配置文件请参考yml文件和config包下的内容。sql包下frame-server.sql文件提供了初始化数据库的脚本
   
#### 业务代码开发模式
9. **业务代码开发模式**
    本框架采用分层mvc模式，由controller，service，repository，dao这几层构成，
    其中
    **entity**为映射表的类在repo层
    **model**为转换模型在service层
    **vo**为与前端交互模型在controller层
    **_各层之间不可交叉引用_**

#### 多环境配置
10. 多环境配置如果出现@无法识别

    found character ‘@’ that cannot start any token. (Do not use @ for indentation)

    **请先运行脚本**  
    **mvn spring-boot:run**