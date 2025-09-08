package com.example.userservice.userservice.client;

import com.example.userservice.userservice.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
@Slf4j
public class CourseClient {

    private final RestTemplate restTemplate;
    private final String courseServiceUrl;

    public CourseClient(RestTemplate restTemplate,
                        @Value("${course.service.url}") String courseServiceUrl) {
        this.restTemplate = restTemplate;
        this.courseServiceUrl = courseServiceUrl;
    }

    public boolean courseExists(Long courseId) {
        String url = courseServiceUrl + "/public/" + courseId + "/exists";
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return response.getBody() != null && response.getBody();
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Failed to call Course Service for courseId: " + courseId);
        }
    }
}
