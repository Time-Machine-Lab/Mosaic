package io.github.tml.plugin.aiChat.cube;

import io.github.tml.mosaic.cube.external.MCube;
import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.mosaic.cube.CubeConfig;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static io.github.tml.plugin.aiChat.config.Constant.PLUGIN_ID;
import static io.github.tml.plugin.aiChat.config.Constant.DEFAULT_SYSTEM_PROMPT;

@MCube(
        name = "AI聊天助手",
        description = "提供AI问答功能的插件，支持多种大语言模型调用",
        version = "1.0.0",
        model = "singleton",
        frontPath = "classPath:aiChat/ui/"
)
public class AiChatCube extends MosaicCube {

    private HttpClient httpClient;

    @Override
    public boolean init() {
        try {
            // 获取配置信息
            CubeConfig config = getCubeConfig();
            int timeout = config.getConfig("timeout", Integer.class, 30);

            // 初始化HTTP客户端
            this.httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(timeout))
                    .build();

            boolean enableLogging = config.getConfig("enableLogging", Boolean.class, true);
            String model = config.getConfig("model", String.class, "gpt-3.5-turbo");

            if (enableLogging) {
                System.out.println("[AI-CHAT] AI聊天插件初始化成功，模型: " + model);
                System.out.println("[AI-CHAT] 配置信息: " + config.getAllConfigs());
            }

            return true;
        } catch (Exception e) {
            System.err.println("[AI-CHAT] 初始化失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String cubeId() {
        return PLUGIN_ID;
    }

    /**
     * 同步AI问答方法
     */
    public String chat(String userMessage) {
        return chat(userMessage, DEFAULT_SYSTEM_PROMPT);
    }

    /**
     * 带系统提示的AI问答方法
     */
    public String chat(String userMessage, String systemPrompt) {
        CubeConfig config = getCubeConfig();

        try {
            JSONObject requestBody = buildRequestBody(userMessage, systemPrompt, config);

            int retryCount = config.getConfig("retryCount", Integer.class, 3);
            boolean enableLogging = config.getConfig("enableLogging", Boolean.class, true);
            int timeout = config.getConfig("timeout", Integer.class, 30);

            for (int attempt = 1; attempt <= retryCount; attempt++) {
                try {
                    HttpRequest request = buildHttpRequest(requestBody, config, timeout);

                    if (enableLogging) {
                        String truncatedMsg = userMessage.length() > 50 ?
                                userMessage.substring(0, 50) + "..." : userMessage;
                        System.out.println("[AI-CHAT] 发送请求 (尝试 " + attempt + "/" + retryCount + "): " + truncatedMsg);
                    }

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        String result = parseResponse(response.body());
                        if (enableLogging) {
                            System.out.println("[AI-CHAT] 请求成功，响应长度: " + result.length());
                        }
                        return result;
                    } else {
                        throw new RuntimeException("API请求失败，状态码: " + response.statusCode() +
                                ", 响应: " + response.body());
                    }

                } catch (Exception e) {
                    if (attempt == retryCount) {
                        throw e;
                    }
                    if (enableLogging) {
                        System.out.println("[AI-CHAT] 请求失败，准备重试 (" + attempt + "/" + retryCount + "): " + e.getMessage());
                    }
                    Thread.sleep(1000 * attempt); // 指数退避重试策略
                }
            }

            return "请求失败，已达到最大重试次数: " + retryCount;

        } catch (Exception e) {
            String errorMsg = "AI调用异常: " + e.getMessage();
            boolean enableLogging = config.getConfig("enableLogging", Boolean.class, true);
            if (enableLogging) {
                System.err.println("[AI-CHAT] " + errorMsg);
                e.printStackTrace();
            }
            return errorMsg;
        }
    }

    /**
     * 异步AI问答方法
     */
    public CompletableFuture<String> chatAsync(String userMessage) {
        return CompletableFuture.supplyAsync(() -> chat(userMessage));
    }

    /**
     * 异步AI问答方法（带系统提示）
     */
    public CompletableFuture<String> chatAsync(String userMessage, String systemPrompt) {
        return CompletableFuture.supplyAsync(() -> chat(userMessage, systemPrompt));
    }

    /**
     * 构建HTTP请求
     */
    private HttpRequest buildHttpRequest(JSONObject requestBody, CubeConfig config, int timeout) {
        String apiEndpoint = config.getConfig("apiEndpoint", String.class, "https://api.openai.com/v1/chat/completions");
        String apiKey = config.getConfig("apiKey", String.class);

        if (apiKey == null || apiKey.trim().isEmpty()) {
            // 如果配置中没有API Key，尝试从环境变量获取
            apiKey = System.getenv("OPENAI_API_KEY");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new RuntimeException("API Key未配置，请在配置文件中设置apiKey或设置环境变量OPENAI_API_KEY");
            }
        }

        return HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .header("User-Agent", "Mosaic-AI-Plugin/1.0.0")
                .timeout(Duration.ofSeconds(timeout))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSONString()))
                .build();
    }

    /**
     * 构建请求体
     */
    private JSONObject buildRequestBody(String userMessage, String systemPrompt, CubeConfig config) {
        JSONObject requestBody = new JSONObject();

        // 从配置获取参数
        String model = config.getConfig("model", String.class, "gpt-3.5-turbo");
        Integer maxTokens = config.getConfig("maxTokens", Integer.class, 2048);
        Double temperature = config.getConfig("temperature", Double.class, 0.7);

        requestBody.put("model", model);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", temperature);

        // 构建消息数组
        JSONArray messages = new JSONArray();

        // 添加系统提示（如果提供）
        if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);
        }

        // 添加用户消息
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        requestBody.put("messages", messages);

        return requestBody;
    }

    /**
     * 解析API响应
     */
    private String parseResponse(String responseBody) {
        try {
            JSONObject response = JSONObject.parseObject(responseBody);

            // 检查是否有错误
            if (response.containsKey("error")) {
                JSONObject error = response.getJSONObject("error");
                String errorMessage = error.getString("message");
                String errorType = error.getString("type");
                throw new RuntimeException("API错误 [" + errorType + "]: " + errorMessage);
            }

            JSONArray choices = response.getJSONArray("choices");

            if (choices != null && choices.size() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                String content = message.getString("content");

                if (content != null && !content.trim().isEmpty()) {
                    return content.trim();
                }
            }

            return "解析响应失败：未找到有效内容";

        } catch (Exception e) {
            return "解析响应异常: " + e.getMessage();
        }
    }

    /**
     * 获取当前配置信息
     */
    public JSONObject getCurrentConfig() {
        CubeConfig config = getCubeConfig();
        JSONObject result = new JSONObject();

        result.put("model", config.getConfig("model", String.class, "gpt-3.5-turbo"));
        result.put("maxTokens", config.getConfig("maxTokens", Integer.class, 2048));
        result.put("temperature", config.getConfig("temperature", Double.class, 0.7));
        result.put("timeout", config.getConfig("timeout", Integer.class, 30));
        result.put("enableLogging", config.getConfig("enableLogging", Boolean.class, true));
        result.put("retryCount", config.getConfig("retryCount", Integer.class, 3));
        result.put("apiEndpoint", config.getConfig("apiEndpoint", String.class, "https://api.openai.com/v1/chat/completions"));

        // 不返回敏感信息如API Key
        result.put("apiKeyConfigured", config.getConfig("apiKey", String.class) != null);

        return result;
    }

    /**
     * 验证配置有效性
     */
    public boolean validateConfig() {
        try {
            CubeConfig config = getCubeConfig();

            // 检查必需的配置项
            String apiKey = config.getConfig("apiKey", String.class);
            if (apiKey == null || apiKey.trim().isEmpty()) {
                apiKey = System.getenv("OPENAI_API_KEY");
                if (apiKey == null || apiKey.trim().isEmpty()) {
                    System.err.println("[AI-CHAT] 配置验证失败: API Key未配置");
                    return false;
                }
            }

            String apiEndpoint = config.getConfig("apiEndpoint", String.class);
            if (apiEndpoint == null || apiEndpoint.trim().isEmpty()) {
                System.err.println("[AI-CHAT] 配置验证失败: API端点未配置");
                return false;
            }

            // 验证数值范围
            Double temperature = config.getConfig("temperature", Double.class, 0.7);
            if (temperature < 0.0 || temperature > 2.0) {
                System.err.println("[AI-CHAT] 配置验证失败: temperature值应在0.0-2.0范围内");
                return false;
            }

            Integer maxTokens = config.getConfig("maxTokens", Integer.class, 2048);
            if (maxTokens <= 0 || maxTokens > 32000) {
                System.err.println("[AI-CHAT] 配置验证失败: maxTokens值应在1-32000范围内");
                return false;
            }

            return true;

        } catch (Exception e) {
            System.err.println("[AI-CHAT] 配置验证异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 健康检查方法
     */
    public boolean healthCheck() {
        try {
            if (!validateConfig()) {
                return false;
            }

            // 发送一个简单的测试请求
            String testResponse = chat("Hello", "You are a helpful assistant. Please respond with 'OK' only.");
            return testResponse != null && !testResponse.contains("异常") && !testResponse.contains("失败");

        } catch (Exception e) {
            System.err.println("[AI-CHAT] 健康检查失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean destroy() {
        try {
            if (httpClient != null) {
                // HttpClient没有显式的关闭方法，但我们可以清理引用
                httpClient = null;
            }

            CubeConfig config = getCubeConfig();
            boolean enableLogging = config.getConfig("enableLogging", Boolean.class, true);

            if (enableLogging) {
                System.out.println("[AI-CHAT] AI聊天插件已销毁");
            }

            return true;
        } catch (Exception e) {
            System.err.println("[AI-CHAT] 销毁插件时发生异常: " + e.getMessage());
            return false;
        }
    }
}