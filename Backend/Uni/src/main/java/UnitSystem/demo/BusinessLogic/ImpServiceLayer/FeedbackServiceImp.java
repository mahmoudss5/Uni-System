package UnitSystem.demo.BusinessLogic.ImpServiceLayer;

import UnitSystem.demo.BusinessLogic.InterfaceServiceLayer.FeedbackService;
import UnitSystem.demo.DataAccessLayer.Dto.Feedback.FeedbackRequest;
import UnitSystem.demo.DataAccessLayer.Dto.Feedback.FeedbackResponse;
import UnitSystem.demo.DataAccessLayer.Entities.Feedback;
import UnitSystem.demo.DataAccessLayer.Entities.User;
import UnitSystem.demo.DataAccessLayer.Repositories.FeedbackRepository;
import UnitSystem.demo.DataAccessLayer.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImp implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    private FeedbackResponse mapToFeedbackResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .userId(feedback.getUser().getId())
                .userName(feedback.getUser().getUserName())
                .role(feedback.getRole())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }

    private Feedback mapToFeedback(FeedbackRequest feedbackRequest) {
        User user = userRepository.findById(feedbackRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Feedback.builder()
                .user(user)
                .role(feedbackRequest.getRole())
                .comment(feedbackRequest.getComment())
                .build();
    }

    @Override
    @Cacheable(value = "feedbacksCache", key = "'allFeedbacks'")
    public List<FeedbackResponse> getAllFeedbacks() {
        return feedbackRepository.findAllOrderByCreatedAtDesc().stream()
                .map(this::mapToFeedbackResponse)
                .collect(Collectors.toList());
    }



    @Override
    @Cacheable(value = "feedbacksCache", key = "'feedbacksByRole:' + #role")
    public List<FeedbackResponse> getFeedbacksByRole(String role) {
        return feedbackRepository.findByRole(role).stream()
                .map(this::mapToFeedbackResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "feedbacksCache", key = "'feedbacksByUser:' + #userId")
    public List<FeedbackResponse> getFeedbacksByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return feedbackRepository.findByUser(user).stream()
                .limit(4) // Limit to the most recent 4 feedbacks
                .map(this::mapToFeedbackResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "feedbacksCache", key = "'feedbackById:' + #feedbackId")
    public FeedbackResponse getFeedbackById(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .map(this::mapToFeedbackResponse)
                .orElse(null);
    }

    @Override
    @CacheEvict(value = "feedbacksCache", allEntries = true)
    public FeedbackResponse createFeedback(FeedbackRequest feedbackRequest) {
        Feedback feedback = mapToFeedback(feedbackRequest);
        feedbackRepository.save(feedback);
        return mapToFeedbackResponse(feedback);
    }

    @Override
    @CacheEvict(value = "feedbacksCache", allEntries = true)
    public FeedbackResponse updateFeedback(Long feedbackId, FeedbackRequest feedbackRequest) {
        Feedback existingFeedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        existingFeedback.setRole(feedbackRequest.getRole());
        existingFeedback.setComment(feedbackRequest.getComment());

        feedbackRepository.save(existingFeedback);
        return mapToFeedbackResponse(existingFeedback);
    }

    @Override
    @Cacheable(value = "feedbacksCache", key = "'recentFeedbacks'")
    public List<FeedbackResponse> getRecentFeedbacks() {
        return feedbackRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .limit(4)
                .map(this::mapToFeedbackResponse)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "feedbacksCache", allEntries = true)
    public void deleteFeedback(Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }
}
