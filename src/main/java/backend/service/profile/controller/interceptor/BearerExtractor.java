package backend.service.profile.controller.interceptor;

public interface BearerExtractor {
    String extract(String header);
}
