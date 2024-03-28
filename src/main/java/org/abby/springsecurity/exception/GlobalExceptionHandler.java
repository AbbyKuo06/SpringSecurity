package org.abby.springsecurity.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abby.springsecurity.responsebody.CommonNoDataResp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.rmi.ServerException;


/**
 * 全域性異常處理器<p>
 * 該註解定義全域性異常處理類,只能加在class<p>
 */
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<CommonNoDataResp> handleNullPointerException(ServerException e) {
        log.error("ServerException : ", e);
        String msg = " ServerException : " + e;
        return ResponseEntity.badRequest()
                .body(CommonNoDataResp.resp(HttpStatus.BAD_REQUEST.value(), msg));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CommonNoDataResp> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException : ", e);
        var msg = " NullPointerException : " + e;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonNoDataResp.resp(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonNoDataResp> handleException(Exception e) {
        log.error("Exception : ", e);
        var msg = " Exception : " + e;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonNoDataResp.resp(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonNoDataResp> handleException(RuntimeException e) {
        log.error("RuntimeException : ", e);
        var msg = " RuntimeException : " + e;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonNoDataResp.resp(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg));
    }

}

