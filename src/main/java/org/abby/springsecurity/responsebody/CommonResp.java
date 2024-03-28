package org.abby.springsecurity.responsebody;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResp<T> {
    /**
     * 回應時間
     */
    private long timestamp = System.currentTimeMillis();
    /**
     * 狀態碼
     */
    private int code;

    /**
     * 返回內容
     */
    private String message;

    /**
     * 返回資料
     */
    private T data;

    public static <T> CommonResp<T> resp(int code, String msg, T data) {
        return new CommonResp<>(System.currentTimeMillis(), code, msg, data);
    }
}
