package chocolate.gametask.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {
    private Boolean success;
    private String message;
    private T data;

    public static <T> ApiResponseDTO<T> ok(T data) {
        return ApiResponseDTO.<T>builder().success(true).data(data).build();
    }

    public static <T> ApiResponseDTO<T> ok(T data, String message) {
        return ApiResponseDTO.<T>builder().success(true).data(data).message(message).build();
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder().success(false).message(message).build();
    }
}