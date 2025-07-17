package org.wgtech.wgmall_backend.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;
    private Pagination pagination;
    private List<String> errors;

    // 200 成功
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null, System.currentTimeMillis(), null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, System.currentTimeMillis(), null, null);
    }

    public static <T> Result<T> success(T data, Pagination pagination) {
        return new Result<>(200, "操作成功", data, System.currentTimeMillis(), pagination, null);
    }

    // 400 参数错误、非法请求
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null, System.currentTimeMillis(), null, null);
    }

    // 401 权限不足
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null, System.currentTimeMillis(), null, null);
    }

    // 403 Token 无效或失效
    public static <T> Result<T> tokenInvalid(String message) {
        return new Result<>(403, message, null, System.currentTimeMillis(), null, null);
    }

    // 404 资源不存在
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null, System.currentTimeMillis(), null, null);
    }

    // 500 系统内部错误
    public static <T> Result<T> failure(String message) {
        return new Result<>(500, message, null, System.currentTimeMillis(), null, null);
    }

    public static <T> Result<T> failure(List<String> errors) {
        return new Result<>(500, "操作失败", null, System.currentTimeMillis(), null, errors);
    }

    // 自定义返回
    public static <T> Result<T> custom(int code, String message, T data) {
        return new Result<>(code, message, data, System.currentTimeMillis(), null, null);
    }

    public static <T> Result<T> custom(int code, String message, T data, Pagination pagination) {
        return new Result<>(code, message, data, System.currentTimeMillis(), pagination, null);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private long totalItems;
        private int pageSize;
    }

    public boolean isSuccess() {
        return this.code == 200;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

}
