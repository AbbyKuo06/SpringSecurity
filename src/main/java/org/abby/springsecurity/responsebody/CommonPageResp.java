package org.abby.springsecurity.responsebody;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonPageResp<T> {
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
    private List<T> data;

    private Integer size;

    private int page;

    private int totalPages;

    private long total;

    public static <T> CommonPageResp<T> resp(List<T> listData) {
        return resp(listData, 0, null);
    }

    public static <T> CommonPageResp<T> resp(List<T> listData, Integer page, Integer size) {
        CommonPageResp<T> resp = new CommonPageResp<>();
        if (listData != null) {
            resp.setTotalPages(1);
            resp.setPage(page);
            resp.setSize(size);
            resp.setTotal(listData.size());
            resp.setData(listData);
        }
        return resp;
    }

    public static <T> CommonPageResp<T> resp(int code,String message,List<T> listData) {
        CommonPageResp<T> resp = new CommonPageResp<>();
        resp.setCode(code);
        resp.setMessage(message);
        if (listData != null) {
            resp.setTotal(listData.size());
            resp.setData(listData);
            resp.setSize(listData.size());
        }
        return resp;
    }

}
