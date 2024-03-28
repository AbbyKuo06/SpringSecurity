package org.abby.springsecurity.responsebody;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonNoDataResp {
    /**
     * 回應時間
     */
    private long timestamp;
    /**
     * 狀態碼
     */
    private int code;
    /**
     * 返回內容
     */
    private String message;

    public static CommonNoDataResp resp(int code, String msg) {
        return new CommonNoDataResp(System.currentTimeMillis(), code, msg);
    }

}
