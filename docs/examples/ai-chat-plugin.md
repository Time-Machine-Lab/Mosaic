# 示例：AI 聊天插件

## 概述
- 类：AiChatCube（继承 MosaicCube）
- 能力：同步/异步 chat 接口，HTTP 调用外部 AI 服务
- 配置项（常见）：
  - model: String（默认 gpt-3.5-turbo）
  - maxTokens: Integer（默认 2048）
  - temperature: Double（默认 0.7）
  - timeout: Integer（默认 30）
  - retryCount: Integer（默认 3）
  - apiEndpoint: String（默认 https://api.openai.com/v1/chat/completions）
  - apiKey: String（可从环境变量 OPENAI_API_KEY 读取）

## 使用
```java
AiChatCube cube = new AiChatCube();
cube.init();
String answer = cube.chat("你好！");
cube.destroy();
```

## 说明
- 支持 chatAsync 方法；
- 包含简单重试与日志输出；
- 对敏感信息（apiKey）避免在对外接口中回显。