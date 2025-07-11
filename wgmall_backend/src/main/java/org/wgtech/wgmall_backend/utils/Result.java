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

    // 响应状态码
    private int code;

    // 提示信息
    private String message;

    // 返回的数据
    private T data;

    // 时间戳
    private long timestamp;

    // 分页信息 (可选, 如果有分页的需求)
    private Pagination pagination;

    // 错误信息的详细列表 (适用于复杂错误情况)
    private List<String> errors;

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null, System.currentTimeMillis(), null, null);
    }

    // 静态方法：成功返回，返回普通数据
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, System.currentTimeMillis(), null, null);
    }

    // 静态方法：成功返回，带分页数据
    public static <T> Result<T> success(T data, Pagination pagination) {
        return new Result<>(200, "操作成功", data, System.currentTimeMillis(), pagination, null);
    }

    // 静态方法：失败返回，返回错误消息
    public static <T> Result<T> failure(String message) {
        return new Result<>(500, message, null, System.currentTimeMillis(), null, null);
    }

    // 静态方法：失败返回，返回错误列表
    public static <T> Result<T> failure(List<String> errors) {
        return new Result<>(500, "操作失败", null, System.currentTimeMillis(), null, errors);
    }

    // 静态方法：自定义返回，传入状态码、消息、数据
    public static <T> Result<T> custom(int code, String message, T data) {
        return new Result<>(code, message, data, System.currentTimeMillis(), null, null);
    }

    // 静态方法：自定义返回，传入状态码、消息、数据、分页信息
    public static <T> Result<T> custom(int code, String message, T data, Pagination pagination) {
        return new Result<>(code, message, data, System.currentTimeMillis(), pagination, null);
    }

    // 内部分页类（可选）
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private long totalItems;
        private int pageSize;
    }
}
