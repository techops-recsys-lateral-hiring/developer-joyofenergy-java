package uk.tw.energy.domain;

import java.io.Serializable;
import org.springframework.http.HttpStatus;

public class CustomResponse<T> implements Serializable {
    private String error;
    private HttpStatus httpStatus;
    private T payload;

    private CustomResponse(Builder<T> builder) {
        this.error = builder.error;
        this.httpStatus = builder.httpStatus;
        this.payload = builder.payload;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private String error;
        private HttpStatus httpStatus;
        private T payload;

        public Builder<T> error(String error) {
            this.error = error;
            return this;
        }

        public Builder<T> httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder<T> payload(T payload) {
            this.payload = payload;
            return this;
        }

        public CustomResponse<T> build() {
            return new CustomResponse<>(this);
        }
    }

    public String getError() {
        return error;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public T getPayload() {
        return payload;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public CustomResponse() {}
}
