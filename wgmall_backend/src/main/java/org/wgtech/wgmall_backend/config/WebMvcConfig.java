package org.wgtech.wgmall_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 声明这是一个 Spring 配置类，自动装配
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源映射
     *
     * 将本地文件系统中的 /uploads 目录映射为 HTTP 路径 /uploads/**，
     * 使得浏览器可以通过 URL 访问服务器上的本地文件，例如图片、视频等。
     *
     * 示例：
     * 假设上传了一张图片，存储路径为：
     *    /your-project-root/uploads/example.jpg
     * 那么前端访问地址为：
     *    http://localhost:8080/uploads/example.jpg
     *
     * System.getProperty("user.dir") 表示当前项目的根目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // 访问 URL 中以 /uploads/ 开头的请求
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/"); // 映射到本地的 uploads 文件夹
    }
}
