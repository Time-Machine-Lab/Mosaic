# 示例：系统日志插件（多例）

## 概述
- 类：PropertySystemLogCube（继承 MosaicCube）
- 能力：log()/error() 简单输出
- 配置项：
  - outputFormat: String
  - maxFileSize: Integer

## 说明
- init 时读取配置并打印，演示配置读取；
- 可搭配 scope=property 演示多实例隔离。