/*
 * Copyright (c) 2017. KMS Technology, Inc.
 */

package vn.kms.ngaythobet.web.util;

import lombok.Data;

@Data
public class DataResponse<T> {
    private final boolean success;
    private final T data;

    public DataResponse(boolean success) {
        this(success, null);
    }

    public DataResponse(T data) {
        this(data != null, data);
    }

    public DataResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }
}
