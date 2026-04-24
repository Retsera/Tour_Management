package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Model.Tour;
import com.J2EE.TourManagement.Repository.TourRepository;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class TourAIService {
    private final VectorStore vectorStore;
    private final TourRepository tourRepository;

    public TourAIService(VectorStore vectorStore, TourRepository tourRepository) {
        this.vectorStore = vectorStore;
        this.tourRepository = tourRepository;
    }

    public void addTourToVectorStore(Long tourId, String tourName, String description, BigDecimal price, String location, String duration) {
        String content = "Tên tour: " + tourName + ". Mô tả: " + description + ". Giá: " + price + " VND. Địa điểm: " + location + ". Thời lượng: " + duration + ". Link chi tiết: /tour/details/" + tourId;

        Map<String, Object> metadata = Map.of(
                "tour_id", tourId,
                "price", price
        );

        Document document = new Document(content, metadata);
        vectorStore.add(List.of(document));

        System.out.println("Đã thêm tour '" + tourName + "' vào Vector Store.");
    }

    @Transactional(readOnly = true)
    public String syncDatabaseToVectorStore() {
        List<Tour> tours = tourRepository.findAll();

        if (tours.isEmpty()) {
            return "Database trống, không có gì để nạp!";
        }
        int count = 0;
        for (Tour tour : tours) {
            String description = tour.getLongDesc();
            if (description == null || description.isEmpty()) {
                description = tour.getShortDesc();
            }
            BigDecimal representativePrice = BigDecimal.ZERO;
            if (tour.getTourDetails() != null && !tour.getTourDetails().isEmpty()) {
                var firstDetail = tour.getTourDetails().get(0);
                if (firstDetail.getTourPrices() != null && !firstDetail.getTourPrices().isEmpty()) {
                    representativePrice = firstDetail.getTourPrices().get(0).getPrice();
                }
            }

            addTourToVectorStore(
                    tour.getId(),
                    tour.getTitle(),
                    description,
                    representativePrice,
                    tour.getLocation(),
                    tour.getDuration()
            );
            count++;
        }

        return "Đã đồng bộ thành công " + count + " tour vào bộ nhớ AI.";
    }

    public List<Document> searchTours(String query) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(3)
                .similarityThreshold(0.7)
                .build();
        return vectorStore.similaritySearch(searchRequest);
    }
}
