package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Service.TourAIService;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ai/tour")
public class TourAIController {
    private final TourAIService tourAIService;
    public TourAIController(TourAIService tourAIService) {
        this.tourAIService = tourAIService;
    }

    @PostMapping("/sync-database")
    public ResponseEntity<String> syncData() {
        String result = tourAIService.syncDatabaseToVectorStore();
        return ResponseEntity.ok(result);
    }


    @GetMapping("/search")
    public List<String> search(@RequestParam("query") String query) {
        List<Document> results = tourAIService.searchTours(query);
        return results.stream()
                .map(doc -> "Tìm thấy Tour ID: " + doc.getMetadata().get("tour_id") +
                        " - Nội dung khớp: " + doc.getText())

                .collect(Collectors.toList());
    }
}
