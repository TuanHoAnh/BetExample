package vn.kms.ngaythobet.web.util;

import lombok.Data;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Data
public class JsonResponse<T> {
    private Class<T> type;

    @SuppressWarnings("unchecked")
    public JsonResponse(Class<T> type) {
        this.type = type;
    }

    public T request(String url, HttpHeaders headers) {
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new RestTemplate()
                .exchange(url, HttpMethod.GET, new HttpEntity<>("--header", headers), type)
                .getBody();

    }
}
