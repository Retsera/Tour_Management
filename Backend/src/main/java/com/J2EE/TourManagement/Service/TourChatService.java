package com.J2EE.TourManagement.Service; // Tạo thư mục Service nếu chưa có

import com.J2EE.TourManagement.Model.Tour;
import com.J2EE.TourManagement.Repository.TourRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // QUAN TRỌNG

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourChatService {

    private final TourRepository tourRepository;
    private final VectorStore vectorStore;

    public TourChatService(TourRepository tourRepository, VectorStore vectorStore) {
        this.tourRepository = tourRepository;
        this.vectorStore = vectorStore;
    }

    // Đánh dấu @Transactional để giữ session mở
    @Transactional(readOnly = true)
    public List<Tour> getActiveToursForAI() {
        List<Tour> tours = tourRepository.findActiveToursWithDetails();

        if (!tours.isEmpty()) {
            tourRepository.fetchPricesForTourDetails(tours);
        }

        return tours;
    }

    public List<Document> searchToursForSuggestion(String query) {
        SearchRequest request = SearchRequest.builder().query(query)
                .topK(2)
                .similarityThreshold(0.5)
                .build();

        return vectorStore.similaritySearch(request);
    }

    public String formatDocumentsForPrompt(List<Document> documents) {
        if (documents.isEmpty()) {
            return "Không tìm thấy tour nào phù hợp trong hệ thống.";
        }

        return documents.stream()
                .map(doc -> {
                    String content = doc.getText();
                    String id = doc.getMetadata().getOrDefault("tour_id", "0").toString();

                    return content + " [ID_TOUR=" + id + "]";
                })
                .collect(Collectors.joining("\n---\n"));
    }
}