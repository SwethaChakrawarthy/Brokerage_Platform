package portfolio_service.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import portfolio_service.model.Portfolio;
import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class AiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max-tokens}")
    private int maxTokens;

    // Generate AI portfolio summary
    public String generatePortfolioSummary(Portfolio portfolio) {
        log.info("Generating AI summary for portfolio: {}", portfolio.getId());

        try {
            // Build the prompt with portfolio data
            String prompt = buildPortfolioPrompt(portfolio);

            // Create OpenAI service
            OpenAiService openAiService = new OpenAiService(
                    apiKey, Duration.ofSeconds(30));

            // Create chat request
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(List.of(
                            new ChatMessage("system",
                                    "You are a professional financial advisor assistant. "
                                            + "Generate concise, professional portfolio summaries "
                                            + "for investment advisors. Keep summaries under 150 words."),
                            new ChatMessage("user", prompt)
                    ))
                    .maxTokens(maxTokens)
                    .temperature(0.7)
                    .build();

            // Call OpenAI API
            String summary = openAiService
                    .createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            log.info("AI summary generated successfully for portfolio: {}",
                    portfolio.getId());
            return summary;

        } catch (Exception e) {
            log.error("Failed to generate AI summary: {}", e.getMessage());
            // Return a default summary if AI fails
            return generateFallbackSummary(portfolio);
        }
    }

    private String buildPortfolioPrompt(Portfolio portfolio) {
        return String.format("""
            Generate a professional portfolio summary for:
            Portfolio ID: %d
            Total Value: $%s
            Cash Balance: $%s
            Number of Holdings: %d
            Last Rebalanced: %s

            Include: overall assessment, cash allocation analysis,
            and a brief recommendation.
            """,
                portfolio.getId(),
                portfolio.getTotalValue(),
                portfolio.getCashBalance(),
                portfolio.getHoldings() != null ? portfolio.getHoldings().size() : 0,
                portfolio.getLastRebalanced() != null
                        ? portfolio.getLastRebalanced().toString() : "Never"
        );
    }

    // Fallback if OpenAI API is unavailable
    private String generateFallbackSummary(Portfolio portfolio) {
        return String.format(
                "Portfolio #%d Summary: Total value of $%s with cash balance of $%s. "
                        + "Portfolio contains %d holdings. "
                        + "Last rebalanced: %s. "
                        + "Recommend reviewing asset allocation for optimization.",
                portfolio.getId(),
                portfolio.getTotalValue(),
                portfolio.getCashBalance(),
                portfolio.getHoldings() != null ? portfolio.getHoldings().size() : 0,
                portfolio.getLastRebalanced() != null
                        ? portfolio.getLastRebalanced().toString() : "Never"
        );
    }
}