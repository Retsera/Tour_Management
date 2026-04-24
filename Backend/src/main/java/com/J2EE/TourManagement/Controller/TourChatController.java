package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Model.Tour;
import com.J2EE.TourManagement.Model.TourDetail;
import com.J2EE.TourManagement.Model.TourPrice;
import com.J2EE.TourManagement.Service.TourAIService;
import com.J2EE.TourManagement.Service.TourChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class TourChatController {

    private final TourChatService tourChatService;
    private final ChatClient.Builder chatClientBuilder;
    private final TourAIService tourAIService;

    public TourChatController(TourChatService tourChatService, ChatClient.Builder chatClientBuilder, TourAIService tourAIService) {
        this.tourChatService = tourChatService;
        this.chatClientBuilder = chatClientBuilder;
        this.tourAIService = tourAIService;
    }

    @PostMapping("/ai/sync-data")
    public String syncData() {
        return tourAIService.syncDatabaseToVectorStore();
    }

    @GetMapping("/ai/suggest-tour")
    public String suggestTour(@RequestParam String question) {

        List<Document> similarDocs = tourChatService.searchToursForSuggestion(question);

        String contextInformationTour = tourChatService.formatDocumentsForPrompt(similarDocs);

        String systemMessage = """
            Bạn là một trợ lý tìm kiếm tour du lịch.
            Nhiệm vụ của bạn là trả lời câu hỏi dựa trên "DANH SÁCH TOUR" được cung cấp.

            QUY TẮC TRẢ LỜI:
            1. Tìm các tour phù hợp trong danh sách.
            2. Nếu tìm thấy, hãy mô tả hấp dẫn về tour đó (Tên, giá, điểm nổi bật).
            
            3. QUAN TRỌNG NHẤT - TẠO LINK:
               Khi bạn nhắc đến một tour cụ thể, bạn BẮT BUỘC phải chèn Link HTML theo mẫu sau:
                <br/>
                <a href="/tour/details/{ID_TOUR_LẤY_TỪ_CONTEXT}">
                    👉 Xem chi tiết và đặt tour này ngay
                </a>
                <br/>
            
            4. Nếu không tìm thấy tour nào, chỉ trả lời: "Xin lỗi, tôi không tìm thấy tour nào phù hợp."
            
            5. Phải trả lời bằng tiếng Việt.
                
                (Lấy ID từ cụm [ID_TOUR=...] trong thông tin tìm được).
            Lưu ý: Chỉ sử dụng ID và thông tin có trong "DANH SÁCH TOUR".
        """;

        String userPromptTemplate = """
            Câu hỏi người dùng: {question}
            
            --- THÔNG TIN TÌM ĐƯỢC TỪ HỆ THỐNG ---
            {context}
            --- HẾT ---
        """;

        PromptTemplate template = new PromptTemplate(userPromptTemplate);
        String userMessageText = template.render(Map.of(
                "question", question,
                "context", contextInformationTour
        ));

        ChatClient chatClient = chatClientBuilder.build();

        return chatClient.prompt()
                .system(systemMessage)
                .user(userMessageText)
                .call()
                .content();
    }
}
